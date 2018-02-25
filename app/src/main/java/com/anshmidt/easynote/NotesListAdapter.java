package com.anshmidt.easynote;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.anshmidt.easynote.activities.EditNoteActivity;
import com.anshmidt.easynote.activities.MainActivity;
import com.anshmidt.easynote.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder> {

    private Context context;
    private DatabaseHelper databaseHelper;
    private NoteDecorator noteDecorator;
    private PriorityInfo priorityInfo;
    public ArrayList<Note> notesList;
    private ArrayList<Note> searchResultsList = new ArrayList<>();  //for search results
    private int selectedNotePosition = -1;
    public int longPressedNotePosition = -1;
    private final String LOG_TAG = NotesListAdapter.class.getSimpleName();

    public final int CONTEXT_MENU_ITEM_MAKE_IMPORTANT = 1;
    public final int CONTEXT_MENU_ITEM_MAKE_NORMAL = 2;
    public final int CONTEXT_MENU_ITEM_MAKE_MINOR = 3;

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnCreateContextMenuListener {
        TextView noteTextView;
        EditText noteEditText;
//        View itemView;

        NoteViewHolder(View itemView) {
            super(itemView);
//            this.itemView = (TextView) itemView;

            if (context instanceof MainActivity) {
                noteTextView = (TextView) itemView.findViewById(R.id.note_textview);
            } else {
                noteEditText = (EditText) itemView.findViewById(R.id.note_edittext);
                noteEditText.setOnFocusChangeListener(this);
                noteEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // it's also happens when activity starts
                        //I have to check if text has actually changed
                        String newText = s.toString();
                        if (noteEditText.hasFocus()) {
                            Note selectedNote = notesList.get(selectedNotePosition);
                            selectedNote.text = newText;
                            notesList.get(selectedNotePosition).text = newText;
                            selectedNote.modificationTime = System.currentTimeMillis();
                            databaseHelper.updateNote(selectedNote);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longPressedNotePosition = getAdapterPosition();
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (context instanceof EditNoteActivity) {
                if (hasFocus) {
                    selectedNotePosition = getAdapterPosition();
                    Log.i(LOG_TAG, "onFocusChange: selected item: position: " + selectedNotePosition + ", id in database: " + getNoteDbId(selectedNotePosition));

                    EditText noteEditText = (EditText) v;
                    if (noteEditText.getText().toString().equals("")) {
                        noteEditText.setHint(context.getString(R.string.new_note_hint));
                    }
                } else {
                    EditText noteEditText = (EditText) v;
                    if (noteEditText.getText().toString().equals("")) {
                        noteEditText.setHint("");
                    }
                }
//                else {  //switching from current item to next
//                    String text = ((EditText) v).getText().toString();
//                    notesList.get(selectedNotePosition).setText(text);
//                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (context instanceof MainActivity) {
                displayContextMenu(v, longPressedNotePosition, menu);
            }
        }
    }


    public NotesListAdapter(ArrayList<Note> notesList, Context context){
        this.notesList = notesList;
        this.context = context;
        this.searchResultsList.addAll(notesList);
        databaseHelper = DatabaseHelper.getInstance(this.context);
        noteDecorator = new NoteDecorator(context);
        priorityInfo = new PriorityInfo(context);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)  {
        View view;
        if (context instanceof MainActivity){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.editable_note, viewGroup, false);
        }
        return new NoteViewHolder(view);
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(final NoteViewHolder noteViewHolder, final int i) {
        TextView noteView;

        if (context instanceof MainActivity) {
            noteView = noteViewHolder.noteTextView;
            //noteViewHolder.noteTextView.setText(notesList.get(i).getText());
        } else {
            noteView = noteViewHolder.noteEditText;
            //noteViewHolder.noteEditText.setText(notesList.get(i).getText());
        }
        noteView.setText(notesList.get(i).text);

        Priority notePriority = notesList.get(i).priority;
        noteDecorator.displayPriority(noteView, notePriority);
//        //debug
//        if (notePriority.equals(priorityInfo.IMPORTANT)) {
//            Log.d(LOG_TAG, "Displaying important priority for note: ");
//            notesList.get(i).printContentToLog();
//        }
//        if (notesList.get(i).text.equals("12 note")) {
//            notesList.get(i).printContentToLog();
//        }


        if (selectedNotePosition == i) {
            if (context instanceof EditNoteActivity) {
                //noteViewHolder.noteEditText.requestFocus();
                EditText noteEditText = (EditText) noteView;
                noteEditText.requestFocus();
                //noteViewHolder.noteEditText.setSelection(noteViewHolder.noteEditText.getText().length()); //move cursor_searchview to the end of the note
                noteEditText.setSelection(noteEditText.getText().length()); //move cursor_searchview to the end of the note
            }
        }
    }

    @Override
    public int getItemCount() {
        if (notesList != null) {
            return notesList.size();
        } else {
            Log.i(LOG_TAG,"getItemCount(): notesList is null");
            return 0;
        }
    }


    public void onItemClick(View view, int position) {
        Log.i(LOG_TAG, "onItemClick: You clicked item with position: " + position + ", id in database: " + getNoteDbId(position));
        if (context instanceof MainActivity){
            ((MainActivity) context).openEditNoteActivity(position);
        }
    }

    public void add(int position, Note item) {
        notesList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        notesList.remove(position);
        notifyItemRemoved(position);
    }

    public Note getNote(int position) {
        return notesList.get(position);
    }

    public void setSelectedNotePosition(int position) {
        selectedNotePosition = position;
    }

    public int getSelectedNotePosition() {
        return selectedNotePosition;
    }

    public int getNoteDbId(int positionInList) {
        Note note = getNote(positionInList);
        int noteDbId = note.id;
        note.printContentToLog();
        return noteDbId;
    }

    public int getPosition(Note note) {
        return notesList.indexOf(note);
    }

    public void filter(String searchRequest) {
        notesList.clear();
        if (searchRequest.isEmpty()) {
            notesList.addAll(searchResultsList);
        } else {
            searchRequest = searchRequest.toLowerCase();
            for (Note item: searchResultsList){
                if (item.text.toLowerCase().contains(searchRequest)){
                    notesList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void resetFilter() {
        notesList.clear();
        notesList.addAll(searchResultsList);
        notifyDataSetChanged();
    }

    public String getNoteText(int position) {
        return notesList.get(position).text;
    }

    public String getSelectedItemText() {
        return getNoteText(selectedNotePosition);
    }

    private void displayContextMenu(View itemView, int longPressedNotePosition, ContextMenu menu) {
        //menu.setHeaderTitle("Select The Action");

        String titlePrefix = context.getString(R.string.note_context_menu_change_priority_title) + " ";
        String titleSetImportantPriority = titlePrefix + priorityInfo.IMPORTANT.name.toLowerCase();
        String titleSetNormalPriority = titlePrefix + priorityInfo.NORMAL.name.toLowerCase();
        String titleSetMinorPriority = titlePrefix + priorityInfo.MINOR.name.toLowerCase();

        Note longPressedNote = getNote(longPressedNotePosition);
        Priority currentPriority = longPressedNote.priority;

        if (! currentPriority.equals(priorityInfo.IMPORTANT)) {
            menu.add(0, CONTEXT_MENU_ITEM_MAKE_IMPORTANT, 0, titleSetImportantPriority);
        }
        if (! currentPriority.equals(priorityInfo.NORMAL)) {
            menu.add(0, CONTEXT_MENU_ITEM_MAKE_NORMAL, 0, titleSetNormalPriority);
        }
        if (! currentPriority.equals(priorityInfo.MINOR)) {
            menu.add(0, CONTEXT_MENU_ITEM_MAKE_MINOR, 0, titleSetMinorPriority);
        }
    }

    public int whereToAddNewNote() {
        Log.d(LOG_TAG, "whereToAddNewNote: " + getImportantNotesCount());
        return getImportantNotesCount();
    }

    private int getImportantNotesCount() {
        int count = 0;
        for (Note note : notesList) {
            if (note.priority.equals(priorityInfo.IMPORTANT)) {
                count++;
            }
        }
        return count;
    }

    public void sortNotes(ArrayList<Note> notesList) {
        Collections.sort(notesList, new Comparator<Note>() {

            public int compare(Note note1, Note note2) {
                Integer priorityId1 = note1.priority.id;
                Integer priorityId2 = note2.priority.id;
                if ( !(priorityId1.equals(priorityId2))) {
                    return priorityId1.compareTo(priorityId2);
                }

                Long modTime1 = note1.modificationTime;
                Long modTime2 = note2.modificationTime;

//                return modTime1.compareTo(modTime2);
                return modTime2.compareTo(modTime1);
            }});
    }

}
