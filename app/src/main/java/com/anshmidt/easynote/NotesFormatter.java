package com.anshmidt.easynote;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 07.03.2018.
 */

public class NotesFormatter {

    private Context context;

    public NotesFormatter(Context context) {
        this.context = context;
    }

    public String notesOfOneListToString(ArrayList<Note> notes) {
        String listName = notes.get(0).list.name;
        String result = listName + ": \n";
        int lineNumber = 1;
        PriorityInfo priorityInfo = new PriorityInfo(context);
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            if ((note.priority.equals(priorityInfo.IMPORTANT)) || (note.priority.equals(priorityInfo.NORMAL))) {
                result += lineNumber + ") " + note.text + "\n";
            }
            lineNumber++;
        }
        return result;
    }
}
