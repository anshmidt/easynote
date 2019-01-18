package com.anshmidt.easynote;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 10.02.2018.
 */

public class PriorityInfo {

    //private ArrayList<Priority> priorities = new ArrayList<>();

    public Priority IMPORTANT;
    public Priority NORMAL;
    public Priority MINOR;

    public Priority DEFAULT;

    private ArrayList<Priority> priorities = new ArrayList<>();



    public PriorityInfo(Context context) {
        IMPORTANT = new Priority(1, context.getString(R.string.note_priority_important));
        NORMAL = new Priority(2, context.getString(R.string.note_priority_normal));
        MINOR = new Priority(3, context.getString(R.string.note_priority_minor));
        DEFAULT = NORMAL;

        priorities.add(IMPORTANT);
        priorities.add(NORMAL);
        priorities.add(MINOR);
    }

    public int getIdByName(String name) {
        for (Priority priority : priorities) {
            if (priority.name.equals(name)) {
                return priority.id;
            }
        }
        return 0;
    }


}
