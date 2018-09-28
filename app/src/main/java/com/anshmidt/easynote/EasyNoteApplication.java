package com.anshmidt.easynote;

import android.app.Application;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ilya Anshmidt on 29.09.2018.
 */

public class EasyNoteApplication extends Application {
    private Timer activityTransitionTimer;
    private TimerTask activityTransitionTimerTask;
    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 10000;

    public void startActivityTransitionTimer() {
        this.activityTransitionTimer = new Timer();
        this.activityTransitionTimerTask = new TimerTask() {
            public void run() {
                EasyNoteApplication.this.wasInBackground = true;
            }
        };

        this.activityTransitionTimer.schedule(activityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
        Log.d("Application", "startActivityTransitionTimer");
    }

    public void stopActivityTransitionTimer() {
        if (this.activityTransitionTimerTask != null) {
            this.activityTransitionTimerTask.cancel();
        }

        if (this.activityTransitionTimer != null) {
            this.activityTransitionTimer.cancel();
        }

        this.wasInBackground = false;
        Log.d("Application", "stopActivityTransitionTimer");
    }



}
