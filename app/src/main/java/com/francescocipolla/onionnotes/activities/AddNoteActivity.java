package com.francescocipolla.onionnotes.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.francescocipolla.onionnotes.R;
import com.francescocipolla.onionnotes.databases.DatabaseHandler;
import com.francescocipolla.onionnotes.models.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by Ciccio on 20/02/2017.
 */

public class AddNoteActivity extends AppCompatActivity {
    private EditText titleEt, bodyEt, dateDP;
    private String sharedTextAction, sharedTextType;
    private DatabaseHandler dbHandler;
    private Context context;
    final Calendar myCalendar = Calendar.getInstance();
    private ColorPickerDialog colorPickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);

        // Connect the XML to the Activity
        titleEt = (EditText) findViewById(R.id.activity_add_title_et);
        bodyEt = (EditText) findViewById(R.id.activity_add_body_et);
        dateDP = (EditText) findViewById(R.id.activity_add_date_et);
        context = this;

        // Text from external intent
        Intent sharedTextIntent = getIntent();
        if (sharedTextIntent != null) {
            sharedTextAction = sharedTextIntent.getAction();
            sharedTextType = sharedTextIntent.getType();
            String body = sharedTextIntent.getStringExtra(Intent.EXTRA_TEXT);
            if (body != null) {
                bodyEt.setText(body);
            }
            dbHandler = new DatabaseHandler(this);
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dateDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // ColorPicker Objects
        int[] colors = getResources().getIntArray(R.array.colors_array);
        colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker, colors, colors[0], 4, colors.length);

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateDP.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title, body;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_add_confirm_button:
                title = titleEt.getText().toString();
                body = titleEt.getText().toString();
                if (titleEt.getText() != null && bodyEt.getText() != null) {
                    //Create note with Shared Text
                    if (Intent.ACTION_SEND.equals(sharedTextAction) && sharedTextType != null && "text/plain".equals(sharedTextType)) {
                        dbHandler.addNote(new Note(title, body));
                        finish();
                    }
                    // Create note from the MainActivity contents
                    Intent intent = getIntent();
                    intent.putExtra("NOTE_TITLE", title);
                    intent.putExtra("NOTE_BODY", body);
                    intent.putExtra("NOTE_EXPIRATION", dateDP.getText().toString());
                    intent.putExtra("NOTE_COLOR", colorPickerDialog.getSelectedColor());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.menu_add_change_color_button:
                showColorPicker();
                break;
            default:
                finish();
        }
        return true;
    }

    private void showColorPicker() {
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                findViewById(R.id.activity_add_note).setBackgroundColor(color);
                colorPickerDialog.setSelectedColor(color);
            }
        });
        colorPickerDialog.show(getFragmentManager(), "Color Picker Dialog");
    }
}
