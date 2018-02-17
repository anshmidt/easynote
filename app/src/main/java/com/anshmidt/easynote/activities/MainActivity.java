package com.anshmidt.easynote.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.anshmidt.easynote.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NoteDecorator;
import com.anshmidt.easynote.NotesListAdapter;
import com.anshmidt.easynote.PriorityInfo;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    protected ArrayList<Note> notesList;
    protected RecyclerView rv;
    LinearLayoutManager llm;
    protected NotesListAdapter adapter;
    DatabaseHelper databaseHelper;
    private final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        forceUsingOverflowMenu();
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle("");



        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(this);
        notesList = databaseHelper.getAllNotes();

        rv = (RecyclerView)findViewById(R.id.recyclerView);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.addItemDecoration(new SimpleDividerItemDecoration(this));


        adapter = new NotesListAdapter(notesList, this);
        setNotesListAdapter(adapter);
        rv.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Note noteToRemove = adapter.getNote(position);
                Log.d("TAG","Text of deleted item: "+adapter.getNote(position).getText());

                adapter.remove(position);

                databaseHelper.deleteNote(noteToRemove);
                //DeleteNoteTask deleteNoteTask = new DeleteNoteTask(MainActivity.this);
                //deleteNoteTask.execute(position);  //not position, but id!
                //deleteNoteTask.execute(idOfDeletedNote);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = adapter.longPressedNotePosition;
        PriorityInfo priorityInfo = new PriorityInfo(MainActivity.this);
        Note longPressedNote = adapter.getNote(position);
        Log.d(LOG_TAG, "Long pressed note before changing: ");
        longPressedNote.printContentToLog();

        if (item.getItemId() == adapter.CONTEXT_MENU_ITEM_MAKE_IMPORTANT) {
            longPressedNote.priority = priorityInfo.IMPORTANT;
        }
        if (item.getItemId() == adapter.CONTEXT_MENU_ITEM_MAKE_NORMAL) {
            longPressedNote.priority = priorityInfo.NORMAL;
        }
        if (item.getItemId() == adapter.CONTEXT_MENU_ITEM_MAKE_MINOR) {
            longPressedNote.priority = priorityInfo.MINOR;
        }
        

        databaseHelper.updateNote(longPressedNote);
        adapter.sortNotes(adapter.notesList);
        adapter.notifyDataSetChanged();

        int newPosition = adapter.getPosition(longPressedNote);
        llm.scrollToPosition(newPosition);
//        adapter.notifyDataSetChanged();

        return super.onContextItemSelected(item);
    }













}

