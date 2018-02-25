package com.anshmidt.easynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ilya Anshmidt on 21.02.2018.
 */

public interface TableHelper {

    void onCreate(SQLiteDatabase db);
    void onUpgrade(SQLiteDatabase db);
    void drop(SQLiteDatabase db);
    void fillWithDefaultData(SQLiteDatabase db, Context context);
}
