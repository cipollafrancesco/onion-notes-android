package com.francescocipolla.onionnotes.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.francescocipolla.onionnotes.R;

/**
 * Created by Ciccio on 20/02/2017.
 */

public class AddNoteActivity extends Activity {
    EditText titleEt, bodyEt;
    TextView creationDateTv;
    Button doneBtn;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setTitle("My Notes");

        titleEt = (EditText) findViewById(R.id.activity_add_title_et);
        bodyEt = (EditText) findViewById(R.id.activity_edit_body_et);
        creationDateTv = (TextView) findViewById(R.id.activity_add_creation_date_tv);
        doneBtn = (Button) findViewById(R.id.activity_add_done_btn);

        creationDateTv.setText("2017-02-20");
        //Log.d("Data: ", current);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleEt != null && bodyEt != null) {
                    Intent intent = new Intent();
                    intent.putExtra("NOTE_TITLE", titleEt.getText().toString());
                    intent.putExtra("NOTE_BODY", bodyEt.getText().toString());
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
}
