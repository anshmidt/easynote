package com.anshmidt.easynote.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesListAdapter;
import com.anshmidt.easynote.PriorityInfo;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.SimpleDividerItemDecoration;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.easynote.dialogs.ConfirmationDialogFragment;
import com.anshmidt.easynote.dialogs.RenameListDialogFragment;

import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 03.03.2018.
 */

public class TrashActivity extends AppCompatActivity {

    private final String LOG_TAG = TrashActivity.class.getSimpleName();
    protected RecyclerView rv;
    protected LinearLayoutManager llm;
    private NotesListAdapter notesListAdapter;
    private DatabaseHelper databaseHelper;
    SearchView searchView;
    ImageView clearSearchButton;
    EditText searchField;
    String searchRequest;
    Toolbar toolbar;
    protected NotesListAdapter adapter;
    protected ArrayList<Note> notesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        databaseHelper = DatabaseHelper.getInstance(TrashActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_trash);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.activity_trash_title));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv = (RecyclerView)findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.addItemDecoration(new SimpleDividerItemDecoration(this));

        notesList = databaseHelper.getAllNotesFromTrash();
        adapter = new NotesListAdapter(notesList, this);
        setNotesListAdapter(adapter);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trash, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_empty_trash: {
                //delete all notes from db
                //delete all lists which don't have notes with inTrash = false
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }



    protected NotesListAdapter getNotesListAdapter() {
        return notesListAdapter;
    }

    protected void setNotesListAdapter(NotesListAdapter adapter) {
        this.notesListAdapter = adapter;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = adapter.longPressedNotePosition;
        //PriorityInfo priorityInfo = new PriorityInfo(MainActivity.this);
        Note longPressedNote = adapter.getNote(position);

        if (item.getItemId() == adapter.TRASH_CONTEXT_MENU_ITEM_PUT_BACK_ID) {
            longPressedNote.inTrash = false;
            longPressedNote.list = databaseHelper.getListById(longPressedNote.list.id);
            if (longPressedNote.list.inTrash) {
                longPressedNote.list.inTrash = false;
                databaseHelper.updateList(longPressedNote.list);
            }
        }

        databaseHelper.updateNote(longPressedNote);
//        adapter.sortNotes(adapter.notesList);
        adapter.notesList.remove(position);
        adapter.notifyDataSetChanged();

//        int newPosition = adapter.getPosition(longPressedNote);
//        llm.scrollToPosition(newPosition);

        return super.onContextItemSelected(item);
    }
}
