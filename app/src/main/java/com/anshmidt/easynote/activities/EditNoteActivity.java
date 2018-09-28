package com.anshmidt.easynote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.anshmidt.easynote.EasyNoteApplication;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesAdapter;
import com.anshmidt.easynote.R;


import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

public class EditNoteActivity extends BaseActivity {

    protected ArrayList<Note> notesList;
    protected RecyclerView rv;
    protected NotesAdapter notesAdapter;

    public int positionInList;
    public View contentView;
    private DatabaseHelper databaseHelper;
    private final String LOG_TAG = EditNoteActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editnote);
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = DatabaseHelper.getInstance(EditNoteActivity.this);
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        notesList = databaseHelper.getAllNotesFromList(listNamesSpinnerController.getCurrentList());
        notesAdapter = new NotesAdapter(notesList, this);
        setNotesAdapter(notesAdapter);
        rv.setAdapter(notesAdapter);

        positionInList = getIntent().getIntExtra(KEY_INTENT_ITEM_POSITION, 0);
        searchController.searchRequest = getIntent().getStringExtra(KEY_INTENT_SEARCH_REQUEST); //or null if it's not in intent
        if (searchController.searchRequest != null) {
            Log.d(LOG_TAG, "searchRequest '" + searchController.searchRequest + "' was found in the intent");
//            if (! notesAdapter.searchRequest.isEmpty()) {
//                searchView.onActionViewExpanded();
//            }
        }

        contentView = findViewById(R.id.main_layout_editnoteactivity);
        notesAdapter.setContentView(contentView);

        llm.setStackFromEnd(true);  //fixes issue with 3 last items covered with a keyboard

        setItemSwipeCallback(notesAdapter, rv);

    }


    @Override
    protected void onResume() {
        super.onResume();

        notesAdapter.setSelectedNotePosition(positionInList);
        //llm.scrollToPositionWithOffset(positionInList, 0);   //also works, but a bit different
        llm.scrollToPosition(positionInList);

    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


}
