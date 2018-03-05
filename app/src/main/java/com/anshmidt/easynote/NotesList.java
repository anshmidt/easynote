package com.anshmidt.easynote;

/**
 * Created by Ilya Anshmidt on 20.02.2018.
 */

public class NotesList {

    public String name;
    public int id;
    public boolean inTrash;
    public final boolean DEFAULT_IN_TRASH = false;

    public NotesList(int id) {
        this.id = id;
        this.inTrash = DEFAULT_IN_TRASH;
    }

    public NotesList(String name, boolean inTrash) {
        this.name = name;
        this.inTrash = inTrash;
    }

    public NotesList(String name) {
        this.name = name;
        this.inTrash = DEFAULT_IN_TRASH;
    }

    public NotesList(int id, String name) {
        this.id = id;
        this.name = name;
        this.inTrash = DEFAULT_IN_TRASH;
    }

    public NotesList(int id, String name, boolean inTrash) {
        this.id = id;
        this.name = name;
        this.inTrash = inTrash;
    }
}
