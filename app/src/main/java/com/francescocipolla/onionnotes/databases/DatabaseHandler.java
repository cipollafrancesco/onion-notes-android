package com.francescocipolla.onionnotes.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.francescocipolla.onionnotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ciccio on 27/02/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

    //  Table columns
    private static final String PRIMARY_KEY = "id";
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String CREATION_DATE = "creation_date";
    private static final String LAST_UPDATE = "last_update";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "notes";

    // Contacts table name
    private static final String TABLE_NOTES = "my_notes";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + PRIMARY_KEY + " INTEGER PRIMARY KEY," + TITLE + " TEXT,"
                + BODY + " TEXT," + CREATION_DATE + " TEXT, " + LAST_UPDATE + " TEXT " + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES); // Drop the old table
        onCreate(db); // Create a new version of the DB
    }

    /**
     * CRUD operations
     */

    public int addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues myContainer = new ContentValues(); // Container for Values to Query
        myContainer.put(TITLE, note.getTitle());
        myContainer.put(BODY, note.getBody());
        myContainer.put(CREATION_DATE, note.getCreationDate());
        myContainer.put(LAST_UPDATE, note.getCreationDate());
        int noteId = (int) db.insert(TABLE_NOTES, null, myContainer); // Query to insert data
        db.close(); // Close connection
        return noteId;
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> myNotes = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(); // getting the DB in write mode

        String selectAllQuery = "SELECT * FROM " + TABLE_NOTES;

        Cursor cursor = db.rawQuery(selectAllQuery, null);  // provides random read-write access to the result set returned by a database query.

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setBody(cursor.getString(2));
                note.setCreationDate(cursor.getString(3));
                note.setLastUpdateDate(cursor.getString(4));
                myNotes.add(note);
            } while (cursor.moveToNext());
        }
        return myNotes;
    }

    public void removeNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = PRIMARY_KEY + " = '" + note.getId() + "'";
        db.delete(TABLE_NOTES, whereClause, null); //Query
        db.close();
    }

    public void editNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NOTES +
                " SET " + TITLE + " = '" + note.getTitle() + "' , " +
                BODY + " = '" + note.getBody() + "' , " + LAST_UPDATE + " = '" + dateFormat.format(new Date()) + "' ";
        String whereClause = "WHERE " + PRIMARY_KEY + " = '" + note.getId() + "' ;";
        // Log.d("UPDATE QUERY: ", updateQuery + whereClause);
        db.execSQL(updateQuery + whereClause);
    }

    public Note getNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NOTES + " ";
        String whereClause = "WHERE " + PRIMARY_KEY + " = '" + id + "' ;";
        Cursor cursor = db.rawQuery(query + whereClause, null);  // provides random read-write access to the result set returned by a database query.

        Note note = new Note();
        if (cursor.moveToFirst()) {
            note.setId(Integer.parseInt(cursor.getString(0)));
            note.setTitle(cursor.getString(1));
            note.setBody(cursor.getString(2));
            note.setCreationDate(cursor.getString(3));
            note.setLastUpdateDate(cursor.getString(4));
        }
        return note;
    }

    public void deleteAllNotes() {
        getWritableDatabase().delete(TABLE_NOTES, " 1 = 1", null);
    }

    public int getNotesNumber() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NOTES + " ";
        Cursor cursor = db.rawQuery(query, null);
        return cursor.getInt(0);
    }
}
