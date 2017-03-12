package com.francescocipolla.onionnotes.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.francescocipolla.onionnotes.R;
import com.francescocipolla.onionnotes.adapters.NoteAdapter;
import com.francescocipolla.onionnotes.databases.DatabaseHandler;
import com.francescocipolla.onionnotes.models.Note;
import com.francescocipolla.onionnotes.models.States;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    //    Date Formatter
    private static DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
    private static DateFormat otherDateFormat = new SimpleDateFormat("MM/dd/yy");

    //    Constants
    private static final int REQUEST_ADD = 1001;
    private static final String SHARED_PREFERENCES_KEY = "LAYOUT_TYPE";

    //    RecyclerView Objects
    private RecyclerView.LayoutManager layoutManager;
    private NoteAdapter noteAdapter;
    private RecyclerView recyclerView;
    //  Shared Preferences
    SharedPreferences sharedPreferences;

    //    Helper variables
    private static boolean checkExpiration = true;
    private boolean layoutType;

    //    Utilities
    private DatabaseHandler dbHandler;
    public Context context;
    private ColorPickerDialog colorPickerDialog;

    public ActionMode actionMode;
    public ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Setting the other Menu Bar
            getMenuInflater().inflate(R.menu.menu_options, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            int adapterPosition = noteAdapter.getPosition();
            if (noteAdapter.getDataSet().get(adapterPosition).isBookmarked()) {
                menu.findItem(R.id.menu_mark_button).setIcon(R.drawable.ic_bookmark_white_24dp);
                return true;
            } else {
                menu.findItem(R.id.menu_mark_button).setIcon(R.drawable.ic_bookmark_border_white_24dp);
                return true;
            }
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int notePosition = 0;
            Note temp = null;
            String toastString = null;

            switch (item.getItemId()) {
                case R.id.menu_delete_button:
                    notePosition = noteAdapter.getPosition();
                    temp = noteAdapter.getDataSet().get(notePosition);
                    noteAdapter.deleteNote(notePosition);
                    dbHandler.removeNote(temp);
                    toastString = "Deleted";
                    break;

                case R.id.menu_edit_button:
                    notePosition = noteAdapter.getPosition();
                    showEditDialog(notePosition);
                    mode.finish(); //Closes the bar
                    return true; // Doesn't make the toast

                case R.id.menu_mark_button:
                    notePosition = noteAdapter.getPosition();
                    temp = noteAdapter.getDataSet().get(notePosition);
                    int id = noteAdapter.getDataSet().get(notePosition).getId();
                    boolean bookmark = false;
                    if (!temp.isBookmarked()) {
                        bookmark = true;
                        dbHandler.bookmark(id);
                        toastString = "Bookmarked";
                    } else {
                        dbHandler.removeBookmark(id);
                        toastString = "Bookmark removed";
                    }
                    temp.setBookmarked(bookmark);
                    //Log.d("is it Bookmarked? ", dbHandler.getNote(id).toString());
                    // Log.d("Result: ",temp.toString());
                    noteAdapter.notifyItemChanged(notePosition);
                    break;
                case R.id.menu_color_button:
                    // Show palette and change color
                    notePosition = noteAdapter.getPosition();
                    temp = noteAdapter.getDataSet().get(notePosition);
                    setNoteColor(temp,notePosition);
                    toastString = "Color Changed";
                    break;
            }
            Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
            mode.finish(); //Closes the bar
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();
        context = this; // save this context for inner classes

//         ColorPicker Objects
        int[] colors = getResources().getIntArray(R.array.colors_array);
        colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker, colors, colors[0], 4, colors.length);

//        Get the shared preferences from the last time
        sharedPreferences = getPreferences(MODE_PRIVATE);
        layoutType = sharedPreferences.getBoolean(SHARED_PREFERENCES_KEY, true);

//         Creating the OBJs
        recyclerView = (RecyclerView) findViewById(R.id.notes_rv); // get the recycleView from the XML

        noteAdapter = new NoteAdapter();
        dbHandler = new DatabaseHandler(this);

//         Setting the right Layout  basing on the SharedPreferences
        RecyclerView.LayoutManager layoutManager = (layoutType) ? new LinearLayoutManager(this)
                : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

//        Setting the RV fundamentals
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteAdapter);

//         Setting action to the FloatingActionButton
        findViewById(R.id.add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNoteActivity.class);
                ((Activity) context).startActivityForResult(intent, REQUEST_ADD);
            }
        });

//       Updating the Expired Notes's STATUS
        if (checkExpiration) {
            setExpired(noteAdapter.getDataSet());
            checkExpiration = false;
        }
//         Add the Database Elements to the Data Set
        noteAdapter.setDataSet(dbHandler.getAllNotes());
    }

    private void setExpired(ArrayList<Note> dataSet) {
        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
        long expireTimeMillis;
        Date d = null;
        for (Note note : dataSet) {
            try {
                d = otherDateFormat.parse(note.getExpireDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            expireTimeMillis = d.getTime();
            if (currentTimeMillis > expireTimeMillis) {
                note.setStatus(States.EXPIRED);
                dbHandler.changeStatus(note);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        noteAdapter.setDataSet(dbHandler.getAllNotes()); // Sends to much requests
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Set the new SharedPreferences when closing the App
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_PREFERENCES_KEY, layoutType);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Setting the menu (It is called when MainActivity Starts)
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if (layoutType) {
            menu.getItem(0).setIcon(R.drawable.ic_view_quilt_white_24dp);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_view_stream_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Switch LinearLayout || StaggeredLayout
        switch (item.getItemId()) {
            case R.id.menu_main_change_layout:
                // Switch the Layouts
                if (!layoutType) {
                    item.setIcon(R.drawable.ic_view_quilt_white_24dp);
                    layoutManager = new LinearLayoutManager(context);
                    layoutType = true;
                } else {
                    item.setIcon(R.drawable.ic_view_stream_white_24dp);
                    layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    layoutType = false;
                }
                // Set the LayoutManager
                recyclerView.setLayoutManager(layoutManager);
                break;
        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Note newNote = new Note(
                    data.getStringExtra("NOTE_TITLE"),
                    data.getStringExtra("NOTE_BODY"),
                    data.getStringExtra("NOTE_EXPIRATION"),
                    data.getIntExtra("NOTE_COLOR", Color.WHITE)
            );
            int noteId = dbHandler.addNote(newNote); // Add to Database getting the ID
            newNote.setId(noteId);      // Create the note
            noteAdapter.addNote(newNote);   // Add to the Data Set
            recyclerView.scrollToPosition(0);
        }
    }

    private void showEditDialog(final int adapterPosition) {

//        Log.d("MainActivity: ", "showEditDialog ENTERED");
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
                noteAdapter.editNote(myChoose, adapterPosition);
                Toast.makeText(context, "Edited", Toast.LENGTH_SHORT);
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

    private void setNoteColor(final Note note, final int position) {
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                note.setNoteColor(color);
                dbHandler.changeNoteColor(note);
                colorPickerDialog.setSelectedColor(color);
                noteAdapter.notifyItemChanged(position);
            }
        });
        colorPickerDialog.show(getFragmentManager(), "Color Picker Dialog");
    }

}
