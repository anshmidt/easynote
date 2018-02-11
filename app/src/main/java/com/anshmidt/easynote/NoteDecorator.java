package com.anshmidt.easynote;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Sigurd Sigurdsson on 10.02.2018.
 */

public class NoteDecorator {

    private Context context;
    private final String LOG_TAG = NoteDecorator.class.getSimpleName();

    public NoteDecorator(Context context) {
        this.context = context;
    }

    public void displayPriority(View view, Priority priority) {
        if (view instanceof TextView) {
            PriorityInfo priorityInfo = new PriorityInfo(context);
            TextView textview = (TextView) view;

            if (priority.equals(priorityInfo.IMPORTANT)) {
                setTextviewStyle(textview, R.style.importantNote, context);
            }

            if (priority.equals(priorityInfo.MINOR)) {
                setTextviewStyle(textview, R.style.minorNote, context);
            }

            if (priority.equals(priorityInfo.NORMAL)) {
                setTextviewStyle(textview, R.style.normalNote, context);
            }



        }
    }

    private void setTextviewStyle(TextView textview, int style, Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            textview.setTextAppearance(context, style);
        } else {
            textview.setTextAppearance(style);
        }
    }

}
