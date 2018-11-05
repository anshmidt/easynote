package com.anshmidt.easynote;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.anshmidt.easynote.database.DatabaseHelper;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ilya Anshmidt on 25.02.2018.
 */

public class SharedPreferencesHelper {

    private SharedPreferences preferences;
    private Context context;
    private final String LOG_TAG = SharedPreferencesHelper.class.getSimpleName();

    private String KEY_LAST_OPENED_LIST;
    private final int DEFAULT_LAST_OPENED_LIST = 1;


    public SharedPreferencesHelper(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        KEY_LAST_OPENED_LIST = context.getResources().getString(R.string.shar_pref_key_last_opened_list);
    }

    public void setLastOpenedList(NotesList list) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_LAST_OPENED_LIST, list.id);
        Log.d(LOG_TAG, "last opened list set: " + list.name);
        editor.apply();
    }

    public int getLastOpenedListId() {
        return preferences.getInt(KEY_LAST_OPENED_LIST, DEFAULT_LAST_OPENED_LIST);
    }

    public String getLastOpenedListName() {
        int lastOpenedListId = getLastOpenedListId();
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        return databaseHelper.getListNameById(lastOpenedListId);
    }



    public void printAll() {
        Map<String, ?> keys = preferences.getAll();
        Log.d(LOG_TAG, "Printing all shared preferences...");
        if (keys != null) {
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                Log.d(LOG_TAG, entry.getKey() + ": " +
                        entry.getValue().toString());
            }
            Log.d(LOG_TAG, "End of all preferences.");
        } else {
            Log.d(LOG_TAG, "Shared preferences don't exist");
        }
    }

    public void deleteAll() {
        preferences.edit().clear().commit();
        Log.d(LOG_TAG, "Shared preferences are deleted");
    }
}
