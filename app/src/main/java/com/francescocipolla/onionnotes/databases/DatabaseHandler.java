package com.francescocipolla.onionnotes.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.francescocipolla.onionnotes.models.Note;
import com.francescocipolla.onionnotes.models.States;

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
    private static final String LAST_UPDATE = "last_update";
    private static final String EXPIRE_DATE = "expire_date";
    private static final String BOOKMARKED = "bookmarked";
    private static final String NOTE_COLOR = "note_color";
    private static final String NOTE_STATUS = "note_status";


    // Database Version
    private static final int DATABASE_VERSION = 4;

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
                + BODY + " TEXT," + LAST_UPDATE + " TEXT, " + EXPIRE_DATE + " TEXT, "
                + BOOKMARKED + " INTEGER, " + NOTE_COLOR + " INTEGER, "
                + NOTE_STATUS + " TEXT" + ")";
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
        myContainer.put(LAST_UPDATE, note.getLastUpdateDate());
        myContainer.put(EXPIRE_DATE, note.getExpireDate());
        myContainer.put(BOOKMARKED, note.isBookmarked());
        myContainer.put(NOTE_COLOR, note.getNoteColor());
        myContainer.put(NOTE_STATUS, note.getStatus().name());
        // Insert the status
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
            String status = null;
            States state = null;
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setBody(cursor.getString(2));
                note.setLastUpdateDate(cursor.getString(3));
                note.setExpireDate(cursor.getString(4));
                note.setBookmarked(cursor.getInt(5) > 0);
                note.setNoteColor(cursor.getInt(6));
                status = cursor.getString(7);
                state = (status.equalsIgnoreCase(States.RUNNING.name())) ? States.RUNNING : States.EXPIRED;
                note.setStatus(state);
//                System.out.println("Note: " + note.getTitle() + " " + "Status: " + note.getStatus());
//                if (state.equals(States.RUNNING))
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
            note.setLastUpdateDate(cursor.getString(3));
            note.setExpireDate(cursor.getString(4));
            note.setBookmarked(cursor.getInt(5) > 0);
            note.setNoteColor(cursor.getInt(6));
            String status = cursor.getString(7);
            States state = (status.equalsIgnoreCase(States.RUNNING.name())) ? States.RUNNING : States.EXPIRED;
            note.setStatus(state);
            System.out.println("Status: " + state);
        }
        return note;
    }

    public void changeStatus(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        String changeStatusQuery = "UPDATE " + TABLE_NOTES +
                " SET " + NOTE_STATUS + " = '" + note.getStatus().name() + " '";
        String whereClause = "WHERE " + PRIMARY_KEY + " = '" + note.getId() + "' ;";
        Log.d("UPDATE QUERY: ", changeStatusQuery + whereClause);
        db.execSQL(changeStatusQuery + whereClause);
    }

//    public void deleteAllNotes() {
//        getWritableDatabase().delete(TABLE_NOTES, " 1 = 1", null);
//    }

//    public int getNotesNumber() {
//        SQLiteDatabase db = getWritableDatabase();
//        String query = "SELECT COUNT(*) FROM " + TABLE_NOTES + " ";
//        Cursor cursor = db.rawQuery(query, null);
//        return cursor.getInt(0);
//    }

    public void bookmark(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String bookmarkQuery = "UPDATE " + TABLE_NOTES +
                " SET " + BOOKMARKED + " = 1 ";
        String whereClause = "WHERE " + PRIMARY_KEY + " = '" + id + "' ";
        // Log.d("UPDATE QUERY: ", updateQuery + whereClause);
        db.execSQL(bookmarkQuery + whereClause);
    }

    public void removeBookmark(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String removeBookmarkQuery = "UPDATE " + TABLE_NOTES +
                " SET " + BOOKMARKED + " = 0 ";
        String whereClause = "WHERE " + PRIMARY_KEY + " = '" + id + "' ";
        // Log.d("UPDATE QUERY: ", updateQuery + whereClause);
        db.execSQL(removeBookmarkQuery + whereClause);
    }

    public void changeNoteColor(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        String changeColorQuery = "UPDATE " + TABLE_NOTES +
                " SET " + NOTE_COLOR + " = " + note.getNoteColor();
        String whereClause = " WHERE " + PRIMARY_KEY + " = '" + note.getId()+ "' ";
        db.execSQL(changeColorQuery + whereClause);
    }
}
