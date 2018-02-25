package com.anshmidt.easynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anshmidt.easynote.NotesList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 21.02.2018.
 */

public class ListsDao implements TableHelper {

    public static final String LISTS_TABLE_NAME = "lists";
    private final String LOG_TAG = ListsDao.class.getSimpleName();


    public static final String KEY_LIST_NAME = "list_name";
    public static final String KEY_LIST_ID = NotesDao.KEY_LIST_ID;
    public static final String KEY_IN_TRASH = "in_trash";

    private SQLiteDatabase db;

    public ListsDao(SQLiteDatabase db) {
        this.db = db;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LISTS_TABLE = "CREATE TABLE IF NOT EXISTS " + LISTS_TABLE_NAME + " ("
                + KEY_LIST_ID + " INTEGER PRIMARY KEY, "
                + KEY_LIST_NAME + " TEXT, "
                + KEY_IN_TRASH + " INTEGER, "
                + "FOREIGN KEY(" + KEY_LIST_ID + ") REFERENCES "
                + NotesDao.NOTES_TABLE_NAME + "(" + KEY_LIST_ID + "))";
        db.execSQL(CREATE_LISTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        drop(db);
        onCreate(db);
    }

    @Override
    public void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + LISTS_TABLE_NAME);
    }

    @Override
    public void fillWithDefaultData(SQLiteDatabase db, Context context) {
        drop(db);
        onCreate(db);
        DefaultData defaultData = new DefaultData(context);
        addLists(defaultData.getDefaultLists());
    }

    public void addList(NotesList notesList) {
        ContentValues values = new ContentValues();
        values.put(KEY_LIST_NAME, notesList.name);
        values.put(KEY_IN_TRASH, notesList.inTrash);
        db.insert(LISTS_TABLE_NAME, null, values);
        Log.d(LOG_TAG, "List inserted: name = " + notesList.name + ", inTrash = " + notesList.inTrash);
    }

    private void addLists(List<NotesList> lists) {
        for (int i = 0; i < lists.size(); i++) {
            addList(lists.get(i));
        }
    }


    public int getListIdByName(String listName) {
        String selectListIdQuery = "SELECT " + KEY_LIST_ID
                + " FROM " + LISTS_TABLE_NAME
                + " WHERE " + KEY_LIST_NAME + " = '" + listName + "'";
        Cursor cursor = db.rawQuery(selectListIdQuery, null);

        int listId = -1;
        if (cursor.moveToFirst()) {
            listId = cursor.getInt(cursor.getColumnIndex(KEY_LIST_ID));
        }

        cursor.close();
        return listId;
    }

//    public NotesList getList() {
//
//    }

    public List<String> getAllListNames() {
        ArrayList<String> listNamesList = new ArrayList<>();
        String selectAllListNamesQuery = "SELECT " + KEY_LIST_NAME + " FROM " + LISTS_TABLE_NAME;

        Cursor cursor = db.rawQuery(selectAllListNamesQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String listName = cursor.getString(0);
                listNamesList.add(listName);
                Log.d(LOG_TAG, "getAllListNames(): list: " + listName);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return listNamesList;
    }


    public void updateList() {

    }

    public void deleteList() {

    }


}
