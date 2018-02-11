package com.anshmidt.easynote;

import android.content.Context;
import android.util.Log;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

public class Note {
    private int id;
    private String text;
    //private int priorityId;
    //private String priorityName;  // IMPORTANT/NORMAL/MINOR/trash

    public Priority priority;
    private int listId;
    private String listName;
    private long modificationTime;  //ms

    //public final int DEFAULT_PRIORITY_ID = 2;  //NORMAL
    private final String LOG_TAG = Note.class.getSimpleName();

    public Note() {
    }

    public Note(String text, Context context) {
        this.text = text;
        PriorityInfo priorityInfo = new PriorityInfo(context);
        this.priority = priorityInfo.DEFAULT;
        this.modificationTime = System.currentTimeMillis();
    }

    public void printContentToLog() {
        Log.d(LOG_TAG, "id = '" + getId() + "', priority = '" + priority.name +
                "', modificationTime = '" + getModificationTime() +
                "', text = '" + getText() + "'");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    public int getPriorityId() {
//        return priorityId;
//    }

//    public void setPriorityId(int priorityId) {
//        this.priorityId = priorityId;
//    }

    public long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

//    public String getPriorityName() {
//        return priorityName;
//    }
//
//    public void setPriorityName(String priorityName) {
//        this.priorityName = priorityName;
//    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

//    public Priority getPriority() {
//        return priority;
//    }
//
//    public void setPriority(Priority priority) {
//        this.priority = priority;
//    }
}
