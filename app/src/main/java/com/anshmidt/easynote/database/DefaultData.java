package com.anshmidt.easynote.database;

import android.content.Context;

import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.Priority;
import com.anshmidt.easynote.PriorityInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 22.02.2018.
 */

public class DefaultData {  // what is in the db when user installs the app

    private List<Note> defaultNotes;
    private List<Priority> defaultPriorities;
    private List<NotesList> defaultLists;
    private Context context;

    public DefaultData(Context context) {
        this.context = context;
    }

    public List<Note> getDefaultNotes() {
        defaultNotes = new ArrayList<>();
        defaultNotes.add(new Note(1310000000000L, "First note", false, 2, 1));
        defaultNotes.add(new Note(1320000000000L, "Second note", false, 2, 1));
        defaultNotes.add(new Note(1330000000000L, "Third note", false, 2, 1));
        defaultNotes.add(new Note(1340000000000L, "Fourth minor note", false, 3, 1));
        defaultNotes.add(new Note(1350000000000L, "Fifth important note", false, 1, 1));
        defaultNotes.add(new Note(1360000000000L, "Sixth note", false, 2, 1));
        defaultNotes.add(new Note(1380000000000L, "7 note", false, 2, 1));
        defaultNotes.add(new Note(1390000000000L, "8 note", false, 2, 1));
        defaultNotes.add(new Note(1400000000000L, "9 note", false, 2, 1));
        defaultNotes.add(new Note(1410000000000L, "10 no\nte", false, 2, 1));
        defaultNotes.add(new Note(1420000000000L, "11 no\nte", false, 2, 1));
        defaultNotes.add(new Note(1430000000000L, "12 note", false, 2, 1));
        defaultNotes.add(new Note(1440000000000L, "13 no\nte", false, 2, 1));
        defaultNotes.add(new Note(1450000000000L, "14 no\nte", false, 2, 1));
        defaultNotes.add(new Note(1460000000000L, "15 note", false, 2, 1));
        defaultNotes.add(new Note(1470000000000L, "16 note", false, 2, 1));
        defaultNotes.add(new Note(1480000000000L, "17 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000018L, "18 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000019L, "19 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000020L, "20 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000021L, "21 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000022L, "22 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000023L, "23 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000024L, "24 (list2) note", false, 2, 2));
        defaultNotes.add(new Note(1490000000025L, "25 (list2) note", false, 2, 2));
        defaultNotes.add(new Note(1490000000026L, "26 (list2) note", false, 2, 2));
        defaultNotes.add(new Note(1490000000027L, "27 (list3) note", false, 2, 3));
        defaultNotes.add(new Note(1490000000028L, "28 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000029L, "29 note", false, 2, 1));
        defaultNotes.add(new Note(1490000000030L, "30 note", false, 2, 1));

        return defaultNotes;
    }

    public List<Priority> getDefaultPriorities() {
        defaultPriorities = new ArrayList<>();
        PriorityInfo priorityInfo = new PriorityInfo(context);
        defaultPriorities.add(priorityInfo.IMPORTANT);
        defaultPriorities.add(priorityInfo.NORMAL);
        defaultPriorities.add(priorityInfo.MINOR);

        return defaultPriorities;
    }

    public List<NotesList> getDefaultLists() {
        defaultLists = new ArrayList<>();
        defaultLists.add(new NotesList("My notes", false));
        defaultLists.add(new NotesList("Second list", false));
        defaultLists.add(new NotesList("Third list", false));

        return defaultLists;
    }

}
