package com.anshmidt.easynote;

import java.util.List;

/**
 * Created by Sigurd Sigurdsson on 13.09.2017.
 */

public class NoteDataHolder {

    //maybe it's better to pass notesList between activities using Intent and Parceaable, and startActivityForResult

    //maybe I don't need to write to DB in a different thread since my DB is small and fast? So there is
    // no need for passing notedata between activities

    private static NoteDataHolder instance;

    private NoteDataHolder() {}

    public static NoteDataHolder getInstance(){
        if (instance == null) {
            synchronized (NoteDataHolder.class) {
                if (instance == null) {
                    instance = new NoteDataHolder();
                }
            }
        }
        return instance;
    }

    private List<Note> notesList;

    public List<Note> getNotesList() {
        return notesList;
    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
    }
}
