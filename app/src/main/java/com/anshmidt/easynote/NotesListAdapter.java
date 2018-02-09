package com.anshmidt.easynote;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.anshmidt.easynote.activities.EditNoteActivity;
import com.anshmidt.easynote.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder> {

    private Context context;
    private DatabaseHelper databaseHelper;
    private List<Note> notesList;
    private List<Note> searchResultsList = new ArrayList<>();  //for search results
    private int selectedItem = -1;
    private final String LOG_TAG = NotesListAdapter.class.getSimpleName();

    // stores and recycles views as they are scrolled off screen
    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {
        TextView noteText;
        EditText editNoteText;

        NoteViewHolder(View itemView) {
            super(itemView);
            if (context instanceof MainActivity) {
                noteText = (TextView) itemView.findViewById(R.id.note_textview);
            } else {
                editNoteText = (EditText) itemView.findViewById(R.id.note_edittext);
                editNoteText.setOnFocusChangeListener(this);

                editNoteText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // it's also happens when activity starts
                        //I have to check if text has actually changed
                        String newText = s.toString();
                        if (editNoteText.hasFocus()) {
                            Note selectedNote = notesList.get(selectedItem);
                            selectedNote.setText(newText);
                            notesList.get(selectedItem).setText(newText);
                            databaseHelper.updateNote(selectedNote);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (context instanceof EditNoteActivity) {
                if (hasFocus) {
                    selectedItem = getAdapterPosition();
                    Log.i(LOG_TAG, "onFocusChange: selected item: position: " + selectedItem + ", id in database: " + getItemDbId(selectedItem));
                }
//                else {  //switching from current item to next
//                    String text = ((EditText) v).getText().toString();
//                    notesList.get(selectedItem).setText(text);
//                }
            }
        }
    }


    public NotesListAdapter(List<Note> notesList, Context context){
        this.notesList = notesList;
        this.context = context;
        this.searchResultsList.addAll(notesList);
        databaseHelper = DatabaseHelper.getInstance(this.context);
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
        if (context instanceof MainActivity){
            noteViewHolder.noteText.setText(notesList.get(i).getText());
        } else {
            noteViewHolder.editNoteText.setText(notesList.get(i).getText());
        }

        if (selectedItem == i) {
            if (context instanceof EditNoteActivity) {
                noteViewHolder.editNoteText.requestFocus();
                noteViewHolder.editNoteText.setSelection(noteViewHolder.editNoteText.getText().length()); //move cursor_searchview to the end of the note
            }
        }
    }

    @Override
    public int getItemCount() {  // Total number of cells
        if (notesList != null) {
            return notesList.size();
        } else {
            Log.i(LOG_TAG,"getItemCount(): notesList is null");
            return 0;
        }
    }


    public void onItemClick(View view, int position) {
        Log.i(LOG_TAG, "onItemClick: You clicked item with position: " + position + ", id in database: " + getItemDbId(position));
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

    public Note getItem(int position) {
        return notesList.get(position);
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    public int getSelectedItemPosition() {
        return selectedItem;
    }

    public int getItemDbId(int positionInList) {
        Note note = getItem(positionInList);
        int itemDbId = note.getId();
        note.printContentToLog();
        return itemDbId;
    }

    public void filter(String searchRequest) {
        notesList.clear();
        if (searchRequest.isEmpty()) {
            notesList.addAll(searchResultsList);
        } else {
            searchRequest = searchRequest.toLowerCase();
            for (Note item: searchResultsList){
                if (item.getText().toLowerCase().contains(searchRequest)){
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

    public String getItemText(int position) {
        return notesList.get(position).getText();
    }

    public String getSelectedItemText() {
        return getItemText(selectedItem);
    }

}
