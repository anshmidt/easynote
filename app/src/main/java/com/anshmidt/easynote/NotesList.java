package com.anshmidt.easynote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigurd Sigurdsson on 25.12.2017.
 */

public class NotesList {
    private List<Note> notesList;

    public NotesList() {
        this.notesList = new ArrayList<Note>();
    }

    public void add(Note note) {
        notesList.add(note);
    }

    public void addAll(NotesList listToAdd) {
        notesList.addAll(listToAdd.notesList);
    }

    public Note get(int i) {
        return notesList.get(i);
    }

    public int size() {
        return notesList.size();
    }

}
