package com.anshmidt.easynote.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import com.anshmidt.easynote.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesListAdapter;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.SimpleDividerItemDecoration;

import java.util.List;

public class MainActivity extends BaseActivity {

    //private LinearLayout notesList;
//    private List<Note> notesList;
//    private RecyclerView rv;
//    private NotesListAdapter adapter;
    protected List<Note> notesList;
    protected RecyclerView rv;
    protected NotesListAdapter adapter;
    DatabaseHelper databaseHelper;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        forceUsingOverflowMenu();
        setContentView(R.layout.activity_main);
        //notesList = (LinearLayout) findViewById(R.id.notes_list);
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(this);
        //notesList = NoteDataHolder.getInstance().getNotesList();
        notesList = databaseHelper.getAllNotes();

        rv = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.addItemDecoration(new SimpleDividerItemDecoration(this));


        //notesList = getNotesList();
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
                Note noteToRemove = adapter.getItem(position);
                Log.d("TAG","Text of deleted item: "+adapter.getItem(position).getText());

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













}

