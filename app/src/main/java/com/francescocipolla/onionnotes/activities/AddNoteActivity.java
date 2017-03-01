package com.francescocipolla.onionnotes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.francescocipolla.onionnotes.R;
import com.francescocipolla.onionnotes.databases.DatabaseHandler;
import com.francescocipolla.onionnotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ciccio on 20/02/2017.
 */

public class AddNoteActivity extends AppCompatActivity {
    EditText titleEt, bodyEt;
    TextView creationDateTv;
    private Intent sharedTextIntent;
    private String sharedTextAction;
    private String sharedTextType;
    private DatabaseHandler dbHandler;

    DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //Setto la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);

        titleEt = (EditText) findViewById(R.id.activity_add_title_et);
        bodyEt = (EditText) findViewById(R.id.activity_add_body_et);
        creationDateTv = (TextView) findViewById(R.id.activity_add_creation_date_tv);


        creationDateTv.setText(dateFormat.format(new Date()));
        //Log.d("Data: ", current);

        sharedTextIntent = getIntent();
        if (sharedTextIntent != null) {
            sharedTextAction = sharedTextIntent.getAction();
            sharedTextType = sharedTextIntent.getType();
            String body = sharedTextIntent.getStringExtra(Intent.EXTRA_TEXT);
            if (body != null) {
                bodyEt.setText(body);
            }
            dbHandler = new DatabaseHandler(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_add_confirm_button:
                if (titleEt.getText() != null && bodyEt.getText() != null) {
                    //Create note with shared Text
                    if (Intent.ACTION_SEND.equals(sharedTextAction) && sharedTextType != null && "text/plain".equals(sharedTextType)) {
                        dbHandler.addNote(new Note(titleEt.getText().toString(), bodyEt.getText().toString()));
                        //Log.d("Data: ", "INSERTING ");
                        finish();
                    }
                    // Create note from the MainActivity contents
                    Intent intent = getIntent();
                    intent.putExtra("NOTE_TITLE", titleEt.getText().toString());
                    intent.putExtra("NOTE_BODY", bodyEt.getText().toString());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                finish();
        }
        return true;
    }
}
