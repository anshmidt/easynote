package com.anshmidt.easynote.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesAdapter;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 03.03.2018.
 */

public class TrashActivity extends AppCompatActivity {

    private final String LOG_TAG = TrashActivity.class.getSimpleName();
    protected RecyclerView rv;
    protected LinearLayoutManager llm;
    private DatabaseHelper databaseHelper;
    SearchView searchView;
    ImageView clearSearchButton;
    EditText searchField;
    String searchRequest;
    Toolbar toolbar;
    protected NotesAdapter notesAdapter;
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

        notesList = databaseHelper.getAllNotesFromTrash();
        notesAdapter = new NotesAdapter(notesList, this);
        setNotesAdapter(notesAdapter);
        rv.setAdapter(notesAdapter);
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
                databaseHelper.deleteTrashNotes();
                databaseHelper.deleteTrashLists();
                notesAdapter.notesList = databaseHelper.getAllNotesFromTrash();
                notesAdapter.notifyDataSetChanged();
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }



    protected NotesAdapter getNotesAdapter() {
        return notesAdapter;
    }

    protected void setNotesAdapter(NotesAdapter adapter) {
        this.notesAdapter = adapter;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = notesAdapter.longPressedNotePosition;
        Note longPressedNote = notesAdapter.getNote(position);

        if (item.getItemId() == notesAdapter.TRASH_CONTEXT_MENU_ITEM_PUT_BACK_ID) {
            longPressedNote.inTrash = false;
            longPressedNote.list = databaseHelper.getListById(longPressedNote.list.id);
            if (longPressedNote.list.inTrash) {
                longPressedNote.list.inTrash = false;
                databaseHelper.updateList(longPressedNote.list);
            }
        }

        databaseHelper.updateNote(longPressedNote);
        notesAdapter.notesList.remove(position);
        notesAdapter.notifyDataSetChanged();


        return super.onContextItemSelected(item);
    }
}
