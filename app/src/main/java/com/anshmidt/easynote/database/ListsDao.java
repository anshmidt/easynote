package com.anshmidt.easynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.LabeledIntent;
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

    public final int IN_TRASH_TRUE = 1;
    public final int IN_TRASH_FALSE = 0;

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



    public NotesList getListById(int id) {
        String selectListQuery = "SELECT " + KEY_LIST_NAME + ", " + KEY_IN_TRASH
                + " FROM " + LISTS_TABLE_NAME
                + " WHERE " + KEY_LIST_ID + " = " + id;
        Cursor cursor = db.rawQuery(selectListQuery, null);
        NotesList list = null;
        if (cursor.moveToFirst()) {
            String listName = cursor.getString(cursor.getColumnIndex(KEY_LIST_NAME));
            int listInTrash = cursor.getInt(cursor.getColumnIndex(KEY_IN_TRASH));
            list = new NotesList(id, listName, listInTrash == IN_TRASH_TRUE);
        }
        cursor.close();
        return list;
    }



    public List<String> getAllListNamesNotFromTrash() {
        ArrayList<String> listNamesList = new ArrayList<>();
        String selectAllListNamesQuery = "SELECT " + KEY_LIST_NAME + " FROM " + LISTS_TABLE_NAME + " WHERE " + KEY_IN_TRASH + " = " + IN_TRASH_FALSE;

        Cursor cursor = db.rawQuery(selectAllListNamesQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String listName = cursor.getString(0);
                listNamesList.add(listName);
                Log.d(LOG_TAG, "getAllListNamesNotFromTrash(): list: " + listName);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return listNamesList;
    }



    public void updateList(NotesList list) {
        Log.d(LOG_TAG, "Updating list: id = " + list.id + ", name = '" + list.name + "'");
        ContentValues values = new ContentValues();
        values.put(KEY_LIST_NAME, list.name);
        values.put(KEY_IN_TRASH, list.inTrash);
        int result = db.update(LISTS_TABLE_NAME, values, KEY_LIST_ID + " = ?",
                new String[] { String.valueOf(list.id) });
    }

    public void moveListToTrash(NotesList list) {
        list.inTrash = true;
        updateList(list);
    }

    public void deleteTrashLists() {
        String query = "DELETE FROM " + LISTS_TABLE_NAME + " WHERE " + KEY_IN_TRASH + " = " + IN_TRASH_TRUE;
        db.execSQL(query);
        Log.d(LOG_TAG, "All trash lists deleted");
    }

    public void deleteList() {

    }


}
