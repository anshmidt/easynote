package com.anshmidt.easynote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigurd Sigurdsson on 09.09.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notesManager";
    private static final String TABLE_NOTES_NAME = "notes";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MOD_TIME = "modificationTime";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_TEXT = "text";
    private Context context;

    private final String LOG_TAG = "DATABASE";


    private static DatabaseHelper databaseHelperInstance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DatabaseHelper getInstance(Context context){
        if (databaseHelperInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (databaseHelperInstance == null) {
                    databaseHelperInstance = new DatabaseHelper(context);
                }
            }
        }
        return databaseHelperInstance;
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO: KEYMODETIME - TIMESTAMP ?
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TEXT + " TEXT," + KEY_MOD_TIME + " INTEGER,"
                + KEY_PRIORITY + " INTEGER" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES_NAME);

        // Create table again
        onCreate(db);
    }


    public void addNote(Note note) {
//        //temp
//        try {
//            Thread.sleep(10000);
//        } catch (Exception e) {
//        }
//        //end of temp

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MOD_TIME, note.getModificationTime());
        values.put(KEY_PRIORITY, note.getPriority());
        values.put(KEY_TEXT, note.getText());

        // Inserting Row
        db.insert(TABLE_NOTES_NAME, null, values);
        db.close();

    }

    public List<Note> getAllNotes() {
        List<Note> notesList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NOTES_NAME
                + " ORDER BY "
                + KEY_PRIORITY + " ASC, "
                + KEY_MOD_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setText(cursor.getString(1));
                note.setModificationTime(Long.parseLong(cursor.getString(2)));
                note.setPriority(Integer.parseInt(cursor.getString(3)));
                //note.writeContentToLog();

                notesList.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notesList;
    }

    public int updateNote(Note note) {
        Log.d("TAG", "Updating note: id = "+ note.getId() + ", text = " + note.getText());

//        //temp
//        try {
//            Thread.sleep(10000);
//        } catch (Exception e) {
//        }
//        //end of temp

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MOD_TIME, note.getModificationTime());
        values.put(KEY_PRIORITY, note.getPriority());
        values.put(KEY_TEXT, note.getText());

        // updating row
        int result = db.update(TABLE_NOTES_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        db.close();
        return result;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NOTES_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        Log.d("DATABASE", "getNotesCount: "+count);
        // return count
        return count;
    }

    public void deleteNote(Note note) {
//        //temp
//        try {
//            Thread.sleep(10000);
//        } catch (Exception e) {
//        }
//        //end of temp
        int noteId = note.getId();
        Log.d(LOG_TAG,"Start deleting note from database: id: "+noteId+" ");
        note.printContent();

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(noteId) });
        Log.d(LOG_TAG,"note was deleted from database: id: "+noteId+" ");

        db.close();

    }

    public void fillDatabaseWithTestData() {
        if (getNotesCount() != 0) {
            clearAllNotes();
        }

        List<Note> tempNoteData = new ArrayList<>();
//        tempNoteData.add(new Note(1,1310000000000L,"Day1"));
//        tempNoteData.add(new Note(1,1320000000000L,"Day2"));
//        tempNoteData.add(new Note(1,1330000000000L,"Day3"));
        tempNoteData.add(new Note(1,1500000000000L,"Zero0"));
        tempNoteData.add(new Note(1,1500000001000L,"If1 a dog chews shoes, whose shoes does he choose?"));
        tempNoteData.add(new Note(1,1500000002000L,"She2 sells seashells by the seashore. \nThe shells she sells are surely seashells. \nSo if she sells shells on the seashore, I'm sure she sells seashore shells."));
        tempNoteData.add(new Note(1,1500000003000L,"Lillie3 Watts"));
        tempNoteData.add(new Note(1,1500000004000L,"Millie4 Watts"));
        tempNoteData.add(new Note(1,1500000005000L,"Fuzzy5 Wuzzy was a bear. Fuzzy Wuzzy had no hair. Fuzzy Wuzzy wasn’t fuzzy, was he?"));
        tempNoteData.add(new Note(1,1500000006000L,"Aillie6 Watts\nWent to a shop.\nWent to a shop.\nWent to a shop."));
        tempNoteData.add(new Note(1,1500000007000L,"Billie7 Watts"));
        tempNoteData.add(new Note(1,1500000008000L,"She8 sells seashells by the seashore. \nThe shells she sells are surely seashells. \nSo if she sells shells on the seashore, I'm sure she sells seashore shells."));
        tempNoteData.add(new Note(1,1500000009000L,"Dillie9 Watts"));
        tempNoteData.add(new Note(1,1500000010000L,"Eillie10 Watts"));
        tempNoteData.add(new Note(1,1500000011000L,"Nillie11 Watts"));
        tempNoteData.add(new Note(1,1500000012000L,"Yillie12 Watts"));
        tempNoteData.add(new Note(1,1500000013000L,"Zillie13 Watts"));
        tempNoteData.add(new Note(1,1500000014000L,"Fuzzy14 Wuzzy was a bear. Fuzzy Wuzzy had no hair. Fuzzy Wuzzy wasn’t fuzzy, was he?"));
        tempNoteData.add(new Note(1,1500000015000L,"Emma Maiss15 \nWent to a shop"));
        tempNoteData.add(new Note(1,1500000016000L,"Peter16 Piper picked a peck of pickled peppers. \nA peck of pickled peppers Peter Piper picked"));
        tempNoteData.add(new Note(1,1500000017000L,"She17 sells seashells by the seashore. \nThe shells she sells are surely seashells. \nSo if she sells shells on the seashore, I'm sure she sells seashore shells."));
        tempNoteData.add(new Note(1,1500000018000L,"Lemma Maiss18"));

        //writing to database
        for (int i=0; i<tempNoteData.size(); i++) {
            addNote(tempNoteData.get(i));
        }

    }

    public void clearAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES_NAME, null, null);
        db.close();
    }

    public void printAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = null;
        Log.d(LOG_TAG, "List of all notes in database");
        c = db.query(TABLE_NOTES_NAME, null, null, null, null, null, null);

        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = "
                                + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);

                } while (c.moveToNext());
            }
            c.close();
        } else {
            Log.d(LOG_TAG, "Cursor is null");
        }
        db.close();
    }

}