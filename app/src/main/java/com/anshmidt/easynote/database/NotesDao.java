package com.anshmidt.easynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.Priority;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 21.02.2018.
 */

public class NotesDao implements TableHelper {
    public static final String NOTES_TABLE_NAME = "notes";
    private final String LOG_TAG = NotesDao.class.getSimpleName();

    public static final String KEY_NOTE_ID = "note_id";
    public static final String KEY_MODIFIED_AT = "modification_timestamp";
    public static final String KEY_TEXT = "text";
    public static final String KEY_IN_TRASH = "in_trash";  // SQLite doesn't have boolean, so it's int
    public static final String KEY_PRIORITY_ID = "priority_id";
    public static final String KEY_LIST_ID = "list_id";

    public final int IN_TRASH_TRUE = 1;
    public final int IN_TRASH_FALSE = 0;

    private SQLiteDatabase db;

    public NotesDao(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTES_TABLE_NAME + " ("
                + KEY_NOTE_ID + " INTEGER PRIMARY KEY, "  // SQLite points this column to ROWID column
                + KEY_MODIFIED_AT + " INTEGER, "
                + KEY_TEXT + " TEXT, "
                + KEY_IN_TRASH + " INTEGER, "
                + KEY_PRIORITY_ID + " INTEGER, "
                + KEY_LIST_ID + " INTEGER)";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db) {
//        drop(db);
//        onCreate(db);
    }

    @Override
    public void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
    }

    @Override
    public void fillWithDefaultData(SQLiteDatabase db, Context context) {
        drop(db);
        onCreate(db);
        DefaultData defaultData = new DefaultData(context);
        addNotes(defaultData.getDefaultNotes());
    }

    private void addNotes(List<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            addNote(notes.get(i));
        }
    }

    public int addNote(Note note) {  //returns note.id in db
        ContentValues values = new ContentValues();
        values.put(KEY_MODIFIED_AT, note.modificationTime);
        values.put(KEY_TEXT, note.text);
        values.put(KEY_IN_TRASH, note.inTrash);

        int priorityId = note.priority.id;
        if (priorityId == 0) {  //if not initialized
            PriorityDao priorityDao = new PriorityDao(db);
            priorityId = priorityDao.getPriorityIdByName(note.priority.name);
        }
        values.put(KEY_PRIORITY_ID, priorityId);

        int listId = note.list.id;
        if (listId == 0) {
            ListsDao listsDao = new ListsDao(db);
            listId = listsDao.getListIdByName(note.list.name);
        }
        values.put(KEY_LIST_ID, note.list.id);

        long newNoteId = db.insert(NOTES_TABLE_NAME, null, values);
        note.id = (int) newNoteId;
        Log.d(LOG_TAG, "Note inserted:");
        note.printContentToLog();
        return note.id;
    }

    public Note getNoteById(int id) {
        String selectNoteQuery = "SELECT "
                + KEY_NOTE_ID + ", "
                + KEY_MODIFIED_AT + ", "
                + KEY_TEXT + ", "
                + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + ", "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + ", "
                + PriorityDao.PRIORITY_TABLE_NAME + "." + PriorityDao.KEY_PRIORITY_NAME + ", "
                + NOTES_TABLE_NAME + "." + KEY_LIST_ID + ", "
                + ListsDao.LISTS_TABLE_NAME + "." + ListsDao.KEY_LIST_NAME
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + ListsDao.LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + ListsDao.LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PriorityDao.PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PriorityDao.PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " WHERE " + NOTES_TABLE_NAME + "." + KEY_NOTE_ID + " = " + id
                + " ORDER BY "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";
        Cursor cursor = db.rawQuery(selectNoteQuery, null);
        Note note = null;
        if (cursor.moveToFirst()) {
            int noteId = cursor.getInt(0);
            long noteModificationTime = cursor.getLong(1);
            String noteText = cursor.getString(2);
            boolean noteInTrash = cursor.getInt(3) == IN_TRASH_TRUE;
            int notePriorityId = cursor.getInt(4);
            String priorityName = cursor.getString(5);
            Priority notePriority = new Priority(notePriorityId, priorityName);

            int noteListId = cursor.getInt(6);
            String noteListName = cursor.getString(7);
            NotesList list = new NotesList(noteListId, noteListName);

            note = new Note(noteId, noteModificationTime, noteText, noteInTrash, notePriority, list);

            Log.d(LOG_TAG, "getNote(): ");
            note.printContentToLog();
        }
        cursor.close();
        return note;
    }

    public ArrayList<Note> getAllNotes() {
        String selectAllNotesQuery = "SELECT "
                + KEY_NOTE_ID + ", "
                + KEY_MODIFIED_AT + ", "
                + KEY_TEXT + ", "
                + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + ", "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + ", "
                + PriorityDao.PRIORITY_TABLE_NAME + "." + PriorityDao.KEY_PRIORITY_NAME + ", "
                + NOTES_TABLE_NAME + "." + KEY_LIST_ID + ", "
                + ListsDao.LISTS_TABLE_NAME + "." + ListsDao.KEY_LIST_NAME
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + ListsDao.LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + ListsDao.LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PriorityDao.PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PriorityDao.PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " ORDER BY "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";

        Cursor cursor = db.rawQuery(selectAllNotesQuery, null);
        ArrayList<Note> notesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int noteId = cursor.getInt(0);
                long noteModificationTime = cursor.getLong(1);
                String noteText = cursor.getString(2);
                boolean noteInTrash = cursor.getInt(3) == IN_TRASH_TRUE;
                int notePriorityId = cursor.getInt(4);
                String priorityName = cursor.getString(5);
                Priority notePriority = new Priority(notePriorityId, priorityName);

                int noteListId = cursor.getInt(6);
                String noteListName = cursor.getString(7);
                NotesList list = new NotesList(noteListId, noteListName);

                Note note = new Note(noteId, noteModificationTime, noteText, noteInTrash, notePriority, list);

                notesList.add(note);
                Log.d(LOG_TAG, "getAllNotes(): note: ");
                note.printContentToLog();
            } while (cursor.moveToNext());
        }
        cursor.close();

        return notesList;
    }

    public ArrayList<Note> getAllNotesFromList(NotesList fromThisList) {
        String selectAllNotesQuery = "SELECT "
                + KEY_NOTE_ID + ", "
                + KEY_MODIFIED_AT + ", "
                + KEY_TEXT + ", "
                + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + ", "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + ", "
                + PriorityDao.PRIORITY_TABLE_NAME + "." + PriorityDao.KEY_PRIORITY_NAME + ", "
                + NOTES_TABLE_NAME + "." + KEY_LIST_ID + ", "
                + ListsDao.LISTS_TABLE_NAME + "." + ListsDao.KEY_LIST_NAME
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + ListsDao.LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + ListsDao.LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PriorityDao.PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PriorityDao.PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " WHERE " + ListsDao.LISTS_TABLE_NAME + "." + ListsDao.KEY_LIST_ID + " = " + fromThisList.id
                + " AND " + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + " = " + IN_TRASH_FALSE
                + " ORDER BY "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";

        Cursor cursor = db.rawQuery(selectAllNotesQuery, null);
        return readNotesWithCursor(cursor);
    }


    public ArrayList<Note> getSearchResults(String searchRequest, boolean fromTrash) {
        String selectNotesBySearchRequestQuery = "SELECT "
                + KEY_NOTE_ID + ", "
                + KEY_MODIFIED_AT + ", "
                + KEY_TEXT + ", "
                + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + ", "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + ", "
                + PriorityDao.PRIORITY_TABLE_NAME + "." + PriorityDao.KEY_PRIORITY_NAME + ", "
                + NOTES_TABLE_NAME + "." + KEY_LIST_ID + ", "
                + ListsDao.LISTS_TABLE_NAME + "." + ListsDao.KEY_LIST_NAME
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + ListsDao.LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + ListsDao.LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PriorityDao.PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PriorityDao.PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " WHERE " + NOTES_TABLE_NAME + "." + KEY_TEXT + " LIKE '%" + searchRequest + "%'"
                + " AND " + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + " = " + boolToNumeralString(fromTrash)
                + " ORDER BY "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";

        Cursor cursor = db.rawQuery(selectNotesBySearchRequestQuery, null);
        return readNotesWithCursor(cursor);
    }

    public ArrayList<Note> getAllNotesFromTrash() {
        String selectAllNotesQuery = "SELECT "
                + KEY_NOTE_ID + ", "
                + KEY_MODIFIED_AT + ", "
                + KEY_TEXT + ", "
                + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + ", "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + ", "
                + PriorityDao.PRIORITY_TABLE_NAME + "." + PriorityDao.KEY_PRIORITY_NAME + ", "
                + NOTES_TABLE_NAME + "." + KEY_LIST_ID + ", "
                + ListsDao.LISTS_TABLE_NAME + "." + ListsDao.KEY_LIST_NAME
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + ListsDao.LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + ListsDao.LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PriorityDao.PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PriorityDao.PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " WHERE " + NOTES_TABLE_NAME + "." + KEY_IN_TRASH + " = " + IN_TRASH_TRUE
                + " ORDER BY "
                + NOTES_TABLE_NAME + "."
//                + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";

        Cursor cursor = db.rawQuery(selectAllNotesQuery, null);
        return readNotesWithCursor(cursor);
    }

    private ArrayList<Note> readNotesWithCursor(Cursor cursor) {
        ArrayList<Note> notesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int noteId = cursor.getInt(0);
                long noteModificationTime = cursor.getLong(1);
                String noteText = cursor.getString(2);
                boolean noteInTrash = cursor.getInt(3) == IN_TRASH_TRUE;
                int notePriorityId = cursor.getInt(4);
                String priorityName = cursor.getString(5);
                Priority notePriority = new Priority(notePriorityId, priorityName);

                int noteListId = cursor.getInt(6);
                String noteListName = cursor.getString(7);
                NotesList list = new NotesList(noteListId, noteListName);

                Note note = new Note(noteId, noteModificationTime, noteText, noteInTrash, notePriority, list);

                notesList.add(note);
//                Log.d(LOG_TAG, "note: ");
//                note.printContentToLog();
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notesList;
    }

    public void updateNote(Note note) {
        Log.d(LOG_TAG, "Updating note:");
        note.printContentToLog();

        Note thisNoteBeforeUpdate = getNoteById(note.id);
        if (shouldModificationTimeBeUpdated(thisNoteBeforeUpdate, note)) {
            note.modificationTime = System.currentTimeMillis();
        }

        ContentValues values = new ContentValues();
        values.put(KEY_MODIFIED_AT, note.modificationTime);
        values.put(KEY_TEXT, note.text);
        values.put(KEY_IN_TRASH, note.inTrash);

        int priorityId = note.priority.id;
        if (priorityId == 0) {  //if not initialized
            PriorityDao priorityDao = new PriorityDao(db);
            priorityId = priorityDao.getPriorityIdByName(note.priority.name);
        }
        values.put(KEY_PRIORITY_ID, priorityId);

        int listId = note.list.id;
        if (listId == 0) {
            ListsDao listsDao = new ListsDao(db);
            listId = listsDao.getListIdByName(note.list.name);
        }
        values.put(KEY_LIST_ID, listId);

        int result = db.update(NOTES_TABLE_NAME, values, KEY_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.id) });

        //temp: check if note really updated
        Note thisNoteAfterUpdate = getNoteById(note.id);
        Log.d(LOG_TAG, "note in db after update:");
        if (thisNoteAfterUpdate != null) {
            thisNoteAfterUpdate.printContentToLog();
        } else {
            Log.d(LOG_TAG, "note in db is null");
        }
    }

    public void deleteNote(Note note) {
        db.delete(NOTES_TABLE_NAME, KEY_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.id) });
        Log.d(LOG_TAG,"note was deleted from database:");
        note.printContentToLog();
    }

    public int getEmptyNotesCountInList(NotesList list) {
        String query = "SELECT COUNT(" + KEY_NOTE_ID + ") FROM " + NOTES_TABLE_NAME + " WHERE " + KEY_TEXT + " = '' AND " + KEY_LIST_ID + " = " + list.id;
        Cursor cursor = db.rawQuery(query, null);
        int emptyNotesCount = -1;
        if (cursor.moveToFirst()) {
            emptyNotesCount = cursor.getInt(0);
        }
        cursor.close();
        Log.d(LOG_TAG, "Empty notes count: " + emptyNotesCount);
        return emptyNotesCount;
    }

    public void deleteEmptyNotesFromList(NotesList list) {
        getEmptyNotesCountInList(list);
        String query = "DELETE FROM " + NOTES_TABLE_NAME + " WHERE trim(" + KEY_TEXT + ")='' AND " + KEY_LIST_ID + " = " + list.id;
        db.execSQL(query);
        getEmptyNotesCountInList(list);
    }

    public void deleteTrashNotes() {
        String query = "DELETE FROM " + NOTES_TABLE_NAME + " WHERE " + KEY_IN_TRASH + " = " + IN_TRASH_TRUE;
        db.execSQL(query);
        Log.d(LOG_TAG, "All trash notes deleted");
    }

    public void moveNoteToTrash(Note note) {
        note.inTrash = true;
        updateNote(note);
    }

    public void moveNoteToAnotherList(Note noteToMove, NotesList destinationList) {
        noteToMove.list.id = destinationList.id;
        updateNote(noteToMove);
    }

    private boolean shouldModificationTimeBeUpdated(Note noteBeforeUpdate, Note noteAfterUpdate) {
        if (noteBeforeUpdate == null) {
            return true;
        }
        if (! noteBeforeUpdate.text.equals(noteAfterUpdate.text)) {
            return true;
        }
        if (noteBeforeUpdate.list.id != noteAfterUpdate.list.id) {
            return true;
        }
        if (noteBeforeUpdate.inTrash != noteAfterUpdate.inTrash) {
            return true;
        }
        return false;
    }

    public void deleteAllEmptyNotes() {
        db.delete(NOTES_TABLE_NAME, "trim(" + KEY_TEXT + ")=''", null);
        Log.d(LOG_TAG, "All empty notes deleted");
    }

    private String boolToNumeralString(final boolean input) {
        int resultInt = input ? 1 : 0;
        return String.valueOf(resultInt);
    }



}
