package com.anshmidt.easynote.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.anshmidt.easynote.NotesAdapter;
import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.PriorityInfo;
import com.anshmidt.easynote.R;

import com.anshmidt.easynote.dialogs.MoveNoteDialogFragment;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements MoveNoteDialogFragment.MoveNoteDialogListener {

    protected ArrayList<Note> notesList;
    protected RecyclerView rv;
    LinearLayoutManager llm;
    protected NotesAdapter notesAdapter;
    DatabaseHelper databaseHelper;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private Toast movedToTrashToast = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        forceUsingOverflowMenu();
        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(this);



        //temp
//        databaseHelper.printAllNotes();
        //end of temp



        databaseHelper.deleteAllEmptyNotes();
        notesList = databaseHelper.getAllNotesFromList(listNamesSpinnerController.getCurrentList());

        rv = (RecyclerView)findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);


        notesAdapter = new NotesAdapter(notesList, this);
        setNotesAdapter(notesAdapter);
        rv.setAdapter(notesAdapter);

        setItemSwipeCallback(notesAdapter, rv);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = notesAdapter.longPressedNotePosition;
        PriorityInfo priorityInfo = new PriorityInfo(MainActivity.this);
        Note longPressedNote = notesAdapter.getNote(position);
        Log.d(LOG_TAG, "Long pressed note before changing: ");
        longPressedNote.printContentToLog();

        if (item.getItemId() == notesAdapter.MAIN_CONTEXT_MENU_ITEM_MAKE_IMPORTANT_ID) {
            longPressedNote.priority = priorityInfo.IMPORTANT;
        }
        if (item.getItemId() == notesAdapter.MAIN_CONTEXT_MENU_ITEM_MAKE_NORMAL_ID) {
            longPressedNote.priority = priorityInfo.NORMAL;
        }
        if (item.getItemId() == notesAdapter.MAIN_CONTEXT_MENU_ITEM_MAKE_MINOR_ID) {
            longPressedNote.priority = priorityInfo.MINOR;
        }

        if (item.getItemId() == notesAdapter.MAIN_CONTEXT_MENU_ITEM_MOVE_ID) {
            MoveNoteDialogFragment moveNoteDialogFragment = new MoveNoteDialogFragment();
            Bundle selectedNoteBundle = new Bundle();
            selectedNoteBundle.putInt(moveNoteDialogFragment.KEY_SELECTED_NOTE_ID, longPressedNote.id);
            moveNoteDialogFragment.setArguments(selectedNoteBundle);
            FragmentManager manager = getFragmentManager();
            moveNoteDialogFragment.show(manager, moveNoteDialogFragment.FRAGMENT_TAG);
        }

        databaseHelper.updateNote(longPressedNote);
        notesAdapter.sortNotes(notesAdapter.notesList);
        notesAdapter.notifyDataSetChanged();

        int newPosition = notesAdapter.getPosition(longPressedNote);
        llm.scrollToPosition(newPosition);

        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestinationListChosen(int chosenListId, String chosenListName, int noteId) {

        int position = notesAdapter.getPositionById(noteId);
        Note movedNote = notesAdapter.getNote(position);
        notesAdapter.remove(position);

        String noteMovedToastText = getString(R.string.note_moved_toast, chosenListName);
        Toast.makeText(MainActivity.this, noteMovedToastText, Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "Note '"+movedNote.text+"' moved to list '"+chosenListName+"', listId = '"+chosenListId+"'");

        databaseHelper.moveNoteToAnotherList(movedNote, new NotesList(chosenListId));
    }


}

