package com.francescocipolla.onionnotes.models;

import android.text.Editable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ciccio on 20/02/2017.
 */

public class Note {

    private DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
    private int id;
    private String title;
    private String body;
    private String creationDate, lastUpdateDate;
    private States status;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
        this.creationDate = dateFormat.format(new Date());
        this.lastUpdateDate = dateFormat.format(new Date());
        this.status = States.RUNNING;
    }

    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public States getStatus() {
        return status;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
