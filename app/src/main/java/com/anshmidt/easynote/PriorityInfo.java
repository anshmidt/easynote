package com.anshmidt.easynote;

import android.content.Context;

/**
 * Created by Ilya Anshmidt on 10.02.2018.
 */

public class PriorityInfo {

    //private ArrayList<Priority> priorities = new ArrayList<>();

    public Priority IMPORTANT;
    public Priority NORMAL;
    public Priority MINOR;

    public Priority DEFAULT;



    public PriorityInfo(Context context) {
        IMPORTANT = new Priority(1, context.getString(R.string.note_priority_important));
        NORMAL = new Priority(2, context.getString(R.string.note_priority_normal));
        MINOR = new Priority(3, context.getString(R.string.note_priority_minor));
        DEFAULT = NORMAL;
    }


}
