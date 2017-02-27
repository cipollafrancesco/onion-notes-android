package com.francescocipolla.onionnotes.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.francescocipolla.onionnotes.R;
import com.francescocipolla.onionnotes.adapters.NoteAdapter;
import com.francescocipolla.onionnotes.databases.DatabaseHandler;
import com.francescocipolla.onionnotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

    private static final int REQUEST_ADD = 1001;
    private static final int REQUEST_EDIT = 1002;

    LinearLayoutManager layoutManager;
    NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    Intent intent;

    DatabaseHandler dbHandler;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating the OBJs
        recyclerView = (RecyclerView) findViewById(R.id.notes_rv); // get the recycleView from the XML
        layoutManager = new LinearLayoutManager(this); // needs a context -> this
        noteAdapter = new NoteAdapter();

        dbHandler = new DatabaseHandler(this);


        // Setting the RV fundamentals
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteAdapter);
        intent = getIntent();

        context = this; // save this context for inner classes
        // setting action to the + button

        findViewById(R.id.add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNoteActivity.class);
                ((Activity) context).startActivityForResult(intent, REQUEST_ADD);
            }
        });

        // Set the dataSet with the DB elements
        noteAdapter.setDataSet(dbHandler.getAllNotes());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String noteTitle = data.getStringExtra("NOTE_TITLE");
            String noteBody = data.getStringExtra("NOTE_BODY");
            Note newNote = new Note(noteTitle, noteBody);
            int noteId = dbHandler.addNote(newNote); // Add to DB
            newNote.setId(noteId);
            // Log.d("NOTE_ID: ",String.valueOf(noteId));
            noteAdapter.addNote(newNote);   // add to the list
            recyclerView.scrollToPosition(0);
        }
    }

    public ActionMode actionMode;
    public ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_options, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete_button:
                    int deadPosition = noteAdapter.getPosition();
                    Note deadNote = noteAdapter.getDataSet().get(deadPosition);
                    noteAdapter.deleteNote(deadPosition);
                    dbHandler.removeNote(deadNote);
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT);
                    break;

                case R.id.menu_edit_button:
                    int editNotePosition = noteAdapter.getPosition();
                    showEditDialog(editNotePosition);
                    Toast.makeText(context, "Edited", Toast.LENGTH_SHORT);
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    private void showEditDialog(final int adapterPosition) {

        Log.d("MainActivity: ", "showEditDialog ENTERED");
        Note nota = noteAdapter.getDataSet().get(adapterPosition);

        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_note, null);
        final EditText titleEt = (EditText) dialogView.findViewById(R.id.activity_edit_title_et);
        final EditText bodyEt = (EditText) dialogView.findViewById(R.id.activity_edit_body_et);

        titleEt.setText(nota.getTitle());
        bodyEt.setText(nota.getBody());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context); // wants the context
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Edit");

        dialogBuilder.setPositiveButton(R.string.button_done_label, new DialogInterface.OnClickListener() { // done button
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = titleEt.getText().toString();
                String newBody = bodyEt.getText().toString();
                Note myChoose = noteAdapter.getDataSet().get(adapterPosition);
                // I have to get the adapter position and alter the fields

                myChoose.setTitle(newTitle);
                myChoose.setBody(newBody);

                myChoose.setLastUpdateDate(dateFormat.format(new Date()));
                dbHandler.editNote(myChoose);
                noteAdapter.editNote(myChoose,adapterPosition);
                Toast.makeText(context,"Edited",Toast.LENGTH_SHORT);
            }
        });

        dialogBuilder.setNegativeButton(R.string.button_cancel_label, new DialogInterface.OnClickListener() { // cancel button
            public void onClick(DialogInterface dialog, int whichButton) {
                // stop creation
            }
        });

        AlertDialog addNoteDialog = dialogBuilder.create(); // creating the dialog
        addNoteDialog.show();   // showing it
    }
}
