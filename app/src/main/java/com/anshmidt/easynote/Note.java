package com.anshmidt.easynote;

import android.util.Log;

/**
 * Created by Sigurd Sigurdsson on 02.09.2017.
 */

public class Note {
    private int id;
    private String text;
    private int priority;           // 0 - important, 1 - normal, 2 - minor, 3 - trash
    private long modificationTime;  //ms
    public final int DEFAULT_PRIORITY = 1;

    public Note() {
    }

    public Note(String text) {
        this.text = text;
        this.priority = DEFAULT_PRIORITY;
        this.modificationTime = System.currentTimeMillis();
    }

    public Note(int priority, long modificationTime, String text) {
        this.text = text;
        this.priority = priority;
        this.modificationTime = modificationTime;
    }

    public Note(int id, String text, int priority, long modificationTime) {
        this.id = id;
        this.text = text;
        this.priority = priority;
        this.modificationTime = modificationTime;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public void printContent() {
        Log.d("NOTEDATA", "id = '"+getId()+"', priority = '"+getPriority()+
                "', modificationTime = '"+getModificationTime()+
                "', text = '"+getText()+"'");
    }



}
