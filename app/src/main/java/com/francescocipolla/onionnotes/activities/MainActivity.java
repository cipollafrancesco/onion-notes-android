package com.francescocipolla.onionnotes.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.francescocipolla.onionnotes.R;
import com.francescocipolla.onionnotes.adapters.NoteAdapter;
import com.francescocipolla.onionnotes.models.Note;


public class MainActivity extends AppCompatActivity {

    LinearLayoutManager layoutManager;
    NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    Intent intent;
    final int PICK_NOTE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating the OBJs
        recyclerView = (RecyclerView) findViewById(R.id.notes_rv); // get the recycleView from the XML
        layoutManager = new LinearLayoutManager(this); // needs a context -> this
        noteAdapter = new NoteAdapter();

        // Setting the RV fundamentals
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteAdapter);
        intent = getIntent();

        final Context context = this; // save this context for inner classes
        // setting action to the + button

        findViewById(R.id.add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNoteActivity.class);
                ((Activity) context).startActivityForResult(intent, PICK_NOTE_REQUEST);
            }
        });

        //Examples of note
        Note newNote = new Note("RecyclerView","The RecyclerView widget is a more advanced and flexible version of ListView. This widget is a container for displaying large data sets that can be scrolled very efficiently by maintaining a limited number of views. Use the RecyclerView widget when you have data collections whose elements change at runtime based on user action or network events.");
        Note newNote2 = new Note("CardView","CardView extends the FrameLayout class and lets you show information inside cards that have a consistent look across the platform. CardView widgets can have shadows and rounded corners.To create a card with a shadow, use the card_view:cardElevation attribute. CardView uses real elevation and dynamic shadows on Android 5.0.");
        noteAdapter.addNote(newNote);
        noteAdapter.addNote(newNote2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            String noteTitle = data.getStringExtra("NOTE_TITLE");
            String noteBody = data.getStringExtra("NOTE_BODY");
            Log.d("RESULT: ",noteTitle);
            Log.d("RESULT: ",noteTitle);
            Note newNote = new Note(noteTitle,noteBody);
            noteAdapter.addNote(newNote);
    }
}
