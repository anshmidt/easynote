package com.anshmidt.easynote;

import android.content.Context;
import android.util.Log;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

public class Note {
    public int id;
    public String text;
    //private int priorityId;
    //private String priorityName;  // IMPORTANT/NORMAL/MINOR/trash

    public Priority priority;
    public NotesList list;
//    public int listId;
//    public String listName;
    public long modificationTime;  //ms
    public boolean inTrash = false;

    //public final int DEFAULT_PRIORITY_ID = 2;  //NORMAL
    private final String LOG_TAG = Note.class.getSimpleName();

    public Note(int id, long modificationTime, String text, Priority priority, NotesList list) {  // for getAllNotes in DbHelper
        this.id = id;
        this.modificationTime = modificationTime;
        this.text = text;
//        this.inTrash = inTrash;
        this.priority = priority;
        this.list = list;
    }

    public Note(long modificationTime, String text, boolean inTrash, int priorityId, int listId) {  // for DefaultData
        this.modificationTime = modificationTime;
        this.text = text;
        this.inTrash = inTrash;
        this.priority = new Priority(priorityId);
        this.list = new NotesList(listId);
    }

    public Note(String text, Context context) {  // for adding new note in Activity
        this.text = text;
        PriorityInfo priorityInfo = new PriorityInfo(context);
        this.priority = priorityInfo.DEFAULT;
        this.modificationTime = System.currentTimeMillis();
    }


    public void printContentToLog() {
        Log.d(LOG_TAG, "id = '" + id + "', priority = '" + priority.name +
                "', modificationTime = '" + modificationTime +
                "', inTrash = " + inTrash + ", text = '" + text + "', list = " + list.id);
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }



//    public long getModificationTime() {
//        return modificationTime;
//    }
//
//    public void setModificationTime(long modificationTime) {
//        this.modificationTime = modificationTime;
//    }



//    public int getListId() {
//        return listId;
//    }
//
//    public void setListId(int listId) {
//        this.listId = listId;
//    }
//
//    public String getListName() {
//        return listName;
//    }
//
//    public void setListName(String listName) {
//        this.listName = listName;
//    }


}
