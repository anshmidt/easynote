package com.anshmidt.easynote.database;

import android.content.Context;

import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.Priority;
import com.anshmidt.easynote.PriorityInfo;
import com.anshmidt.easynote.R;

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
        long modTimeOfFirstNote = 1310000000000L;
        defaultNotes.add(new Note(modTimeOfFirstNote, context.getString(R.string.default_first_note_name), false, 2, 1));
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
        defaultLists.add(new NotesList(context.getString(R.string.default_first_list_name), false));
        return defaultLists;
    }

}
