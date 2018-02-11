package com.anshmidt.easynote;

import android.content.Context;

/**
 * Created by Sigurd Sigurdsson on 10.02.2018.
 */

public class PriorityInfo {

    //private ArrayList<Priority> priorities = new ArrayList<>();

    public Priority IMPORTANT;
    public Priority NORMAL;
    public Priority MINOR;
    public Priority TRASH;

    public Priority DEFAULT;



//    private ArrayList<String> priorityNames = new ArrayList<>();
    private Context context;
//    private final int DEFAULT_PRIORITY_ID = 2;


    public PriorityInfo(Context context) {
        IMPORTANT = new Priority(1, context.getString(R.string.note_priority_important));
        NORMAL = new Priority(2, context.getString(R.string.note_priority_normal));
        MINOR = new Priority(3, context.getString(R.string.note_priority_minor));
        TRASH = new Priority(4, context.getString(R.string.note_priority_trash));
        DEFAULT = NORMAL;
    }


//    public Priority(Context context) {
//        priorityNames.add(1, context.getString(R.string.note_priority_important));
//        priorityNames.add(2, context.getString(R.string.note_priority_normal));
//        priorityNames.add(3, context.getString(R.string.note_priority_minor));
//        priorityNames.add(4, context.getString(R.string.note_priority_trash));
//    }
//
//    public int getId(String priorityName) {
//        return priorityNames.indexOf(priorityName);
//    }
//
//    public String getName(int priorityId) {
//        return priorityNames.get(priorityId);
//    }
}
