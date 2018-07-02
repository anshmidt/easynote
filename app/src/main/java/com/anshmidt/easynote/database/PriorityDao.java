package com.anshmidt.easynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anshmidt.easynote.Priority;

import java.util.List;

/**
 * Created by Ilya Anshmidt on 21.02.2018.
 */

public class PriorityDao implements TableHelper {

    public static final String PRIORITY_TABLE_NAME = "priority";
    private final String LOG_TAG = PriorityDao.class.getSimpleName();

    public static final String KEY_PRIORITY_ID = NotesDao.KEY_PRIORITY_ID;
    public static final String KEY_PRIORITY_NAME = "priority_name";

    private SQLiteDatabase db;

    public PriorityDao(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRIORITY_TABLE = "CREATE TABLE IF NOT EXISTS " + PRIORITY_TABLE_NAME + " ("
                + KEY_PRIORITY_ID + " INTEGER PRIMARY KEY, "
                + KEY_PRIORITY_NAME + " TEXT, "
                + "FOREIGN KEY(" + KEY_PRIORITY_ID + ") REFERENCES "
                + NotesDao.NOTES_TABLE_NAME + "(" + KEY_PRIORITY_ID + "))";
        db.execSQL(CREATE_PRIORITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db) {
//        drop(db);
//        onCreate(db);
    }

    @Override
    public void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + PRIORITY_TABLE_NAME);
    }

    @Override
    public void fillWithDefaultData(SQLiteDatabase db, Context context) {
        drop(db);
        onCreate(db);
        DefaultData defaultData = new DefaultData(context);
        addPriorities(defaultData.getDefaultPriorities());
    }

    private void addPriorities(List<Priority> priorities) {
        for (int i = 0; i < priorities.size(); i++) {
            addPriority(priorities.get(i));
        }
    }

    public void addPriority(Priority priority) {
        ContentValues values = new ContentValues();
        values.put(KEY_PRIORITY_NAME, priority.name);
        db.insert(PRIORITY_TABLE_NAME, null, values);
        Log.d(LOG_TAG, "Priority inserted: " + priority.name);
    }

    public int getPriorityIdByName(String priorityName) {
        String selectPriorityIdQuery = "SELECT " + KEY_PRIORITY_ID
                + " FROM " + PRIORITY_TABLE_NAME
                + " WHERE " + KEY_PRIORITY_NAME + " = '" + priorityName + "'";
        Cursor cursor = db.rawQuery(selectPriorityIdQuery, null);

        int priorityId = -1;
        if (cursor.moveToFirst()) {
            priorityId = cursor.getInt(cursor.getColumnIndex(KEY_PRIORITY_ID));
        }

        cursor.close();
        return priorityId;
    }

//    public Priority getPriority() {
//
//    }

    public void updatePriority() {

    }

    public void deletePriority() {

    }
}
