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
 * Created by Ilya Anshmidt on 09.09.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notes";

    // Table names
    private final String NOTES_TABLE_NAME = "notes";
    private final String LISTS_TABLE_NAME = "lists";
    private final String PRIORITY_TABLE_NAME = "priority";

    // Common column names
    private final String KEY_PRIORITY_ID = "priority_id";
    private final String KEY_LIST_ID = "list_id";

    // Notes column names
    private final String KEY_NOTE_ID = "note_id";
    private final String KEY_MODIFIED_AT = "modification_timestamp";
    private final String KEY_TEXT = "text";

    // Lists column names
    private final String KEY_LIST_NAME = "list_name";

    // Priority column names
    private final String KEY_PRIORITY_NAME = "priority_name";


    private final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private static DatabaseHelper databaseHelperInstance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.context = context;
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        createNotesTableIfNotExists(db);
        createListsTableIfNotExists(db);
        createPriorityTableIfNotExists(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LISTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRIORITY_TABLE_NAME);
        onCreate(db);
    }

    private void createNotesTableIfNotExists(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTES_TABLE_NAME + " ("
                + KEY_NOTE_ID + " INTEGER PRIMARY KEY, "  // SQLite points this column to ROWID column
                + KEY_MODIFIED_AT + " INTEGER, "
                + KEY_TEXT + " TEXT, "
                + KEY_PRIORITY_ID + " INTEGER, "
                + KEY_LIST_ID + " INTEGER)";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    private void createPriorityTableIfNotExists(SQLiteDatabase db) {
        String CREATE_PRIORITY_TABLE = "CREATE TABLE IF NOT EXISTS " + PRIORITY_TABLE_NAME + " ("
                + KEY_PRIORITY_ID + " INTEGER PRIMARY KEY, "
                + KEY_PRIORITY_NAME + " TEXT, "
                + "FOREIGN KEY(" + KEY_PRIORITY_ID + ") REFERENCES "
                + NOTES_TABLE_NAME + "(" + KEY_PRIORITY_ID + "))";
        db.execSQL(CREATE_PRIORITY_TABLE);
    }

    private void createListsTableIfNotExists(SQLiteDatabase db) {
        String CREATE_LISTS_TABLE = "CREATE TABLE IF NOT EXISTS " + LISTS_TABLE_NAME + " ("
                + KEY_LIST_ID + " INTEGER PRIMARY KEY, "
                + KEY_LIST_NAME + " TEXT, "
                + "FOREIGN KEY(" + KEY_LIST_ID + ") REFERENCES "
                + NOTES_TABLE_NAME + "(" + KEY_LIST_ID + "))";
        db.execSQL(CREATE_LISTS_TABLE);
    }


    private void dropAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LISTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRIORITY_TABLE_NAME);
        db.close();
    }


    private int getPriorityIdByName(String priorityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectPriorityIdQuery = "SELECT " + KEY_PRIORITY_ID
                + " FROM " + PRIORITY_TABLE_NAME
                + " WHERE " + KEY_PRIORITY_NAME + " = '" + priorityName + "'";
        Cursor cursor = db.rawQuery(selectPriorityIdQuery, null);

        int priorityId = -1;
        if (cursor.moveToFirst()) {
            priorityId = cursor.getInt(cursor.getColumnIndex(KEY_PRIORITY_ID));
        }

        cursor.close();
        db.close();
        return priorityId;
    }

    private int getListIdByName(String listName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectListIdQuery = "SELECT " + KEY_LIST_ID
                + " FROM " + LISTS_TABLE_NAME
                + " WHERE " + KEY_LIST_NAME + " = '" + listName + "'";
        Cursor cursor = db.rawQuery(selectListIdQuery, null);

        int listId = -1;
        if (cursor.moveToFirst()) {
            listId = cursor.getInt(cursor.getColumnIndex(KEY_LIST_ID));
        }

        cursor.close();
        db.close();
        return listId;
    }

    private void addPriority(String priorityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRIORITY_NAME, priorityName);
        db.insert(PRIORITY_TABLE_NAME, null, values);
        db.close();
        Log.d(LOG_TAG, "Priority inserted: " + priorityName);
    }

    private void addList(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LIST_NAME, listName);
        db.insert(LISTS_TABLE_NAME, null, values);
        db.close();
        Log.d(LOG_TAG, "List inserted: " + listName);
    }



    private void addNote(long modificationTimestamp, String text, int priorityId, int listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MODIFIED_AT, modificationTimestamp);
        values.put(KEY_TEXT, text);
        values.put(KEY_PRIORITY_ID, priorityId);
        values.put(KEY_LIST_ID, listId);
        db.insert(NOTES_TABLE_NAME, null, values);
        db.close();
        Log.d(LOG_TAG, "Note inserted: " + modificationTimestamp + ", " + text + ", " + priorityId + ", " + listId);
    }

    public void addNote(Note note) {
        long modificationTimestamp = note.getModificationTime();
        String text = note.getText();
        int priorityId = note.getPriorityId();
        if (priorityId == 0) {  //if not initialized
            priorityId = getPriorityIdByName(note.getPriorityName());
        }
        int listId = note.getListId();
        if (listId == 0) {
            listId = getListIdByName(note.getListName());
        }

        addNote(modificationTimestamp, text, priorityId, listId);
    }

    private void fillNotesTableWithTestData() {
        addNote(1310000000000L, "First note", 2, 1);
        addNote(1320000000000L, "Second note", 2, 1);
        addNote(1330000000000L, "Third note", 2, 1);
    }

    private void fillPriorityTableWithTestData() {
        addPriority("Important");
        addPriority("Normal");
        addPriority("Minor");
        addPriority("Trash");
    }

    private void fillListsTableWithTestData() {
        addList("All notes");
    }

    public void fillDatabaseWithTestData() {
        dropAllTables();
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        db.close();

        fillNotesTableWithTestData();
        fillListsTableWithTestData();
        fillPriorityTableWithTestData();
    }

    public void deleteAllRowsFromTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
        db.close();
        Log.d(LOG_TAG, "All rows deleted from table " + tableName);
    }

    public void performSqlRequest() {  //for debugging
        Log.d(LOG_TAG, "getPriorityIdByName(\"Important\") = " + getPriorityIdByName("Impoffrtant"));
    }

    private void logSelectResult(String selectQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d(LOG_TAG, "--------------");
        if (cursor.moveToFirst()) {
            do {

                String row = "";
                for (int i=0; i < cursor.getColumnCount(); i++) {
                    row += cursor.getString(i) + ", ";
                }
                Log.d(LOG_TAG, row);


            } while (cursor.moveToNext());
        }
        Log.d(LOG_TAG, "--------------");
        cursor.close();
        db.close();
    }


    public List<Note> getAllNotes() {
        List<Note> notesList = new ArrayList<>();
        String selectAllNotesQuery = "SELECT "
                + KEY_NOTE_ID + ", "
                + KEY_MODIFIED_AT + ", "
                + KEY_TEXT + ", "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + ", "
                + PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_NAME + ", "
                + NOTES_TABLE_NAME + "." + KEY_LIST_ID + ", "
                + LISTS_TABLE_NAME + "." + KEY_LIST_NAME
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " ORDER BY "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllNotesQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setModificationTime(cursor.getLong(1));
                note.setText(cursor.getString(2));
                note.setPriorityId(cursor.getInt(3));
                note.setPriorityName(cursor.getString(4));
                note.setListId(cursor.getInt(5));
                note.setListName(cursor.getString(6));
                notesList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return notesList;
    }

    public void printAll() {
        String selectAllNotesQuery = "SELECT * "
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " ORDER BY "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";
        logSelectResult(selectAllNotesQuery);
    }


    public void deleteNote(Note note) {
        int noteId = note.getId();
        Log.d(LOG_TAG,"Start deleting note from database:");
        note.printContentToLog();

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES_TABLE_NAME, KEY_NOTE_ID + " = ?",
                new String[] { String.valueOf(noteId) });
        Log.d(LOG_TAG,"note was deleted from database:");
        note.printContentToLog();

        db.close();
    }

    public int updateNote(Note note) {
        Log.d(LOG_TAG, "Updating note:");
        note.printContentToLog();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MODIFIED_AT, note.getModificationTime());
        values.put(KEY_TEXT, note.getText());

        int priorityId = note.getPriorityId();
        if (priorityId == 0) {  //if not initialized
            priorityId = getPriorityIdByName(note.getPriorityName());
        }
        values.put(KEY_PRIORITY_ID, priorityId);

        int listId = note.getListId();
        if (listId == 0) {
            listId = getListIdByName(note.getListName());
        }
        values.put(KEY_LIST_ID, listId);

        int result = db.update(NOTES_TABLE_NAME, values, KEY_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });

        db.close();
        return result;
    }












//
//    public void clearAllNotes() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NOTES_NAME, null, null);
//        db.close();
//    }
//


}




