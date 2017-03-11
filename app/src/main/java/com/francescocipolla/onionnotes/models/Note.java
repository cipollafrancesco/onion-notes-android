package com.francescocipolla.onionnotes.models;

import android.graphics.Color;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ciccio on 20/02/2017.
 */

public class Note {

    private DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
    private int id, noteColor;
    private String title, body, lastUpdateDate, expireDate;
    private States status;
    private boolean bookmarked;

    public Note() {
    }

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
        this.lastUpdateDate = dateFormat.format(new Date());
        this.status = States.RUNNING;
        this.noteColor = Color.WHITE;
        this.bookmarked = false;
    }

    public Note(String title, String body, String expireDate, int noteColor) {
        this(title, body);
        this.expireDate = expireDate;
        this.noteColor = noteColor;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String toString() {
        return "Title: " + this.title + "\n Body: "
                + this.body + "\n Last Update: " + this.lastUpdateDate +
                "\n Bookmark: " + bookmarked + "\n Expire Date: " + expireDate +
                "\n Status: " + status;
    }

    public int getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(int noteColor) {
        this.noteColor = noteColor;
    }

    public States getStatus() {
        return status;
    }

    public void setStatus(States status) {
        this.status = status;
    }
}
