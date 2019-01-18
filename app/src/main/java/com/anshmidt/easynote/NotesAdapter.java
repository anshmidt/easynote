package com.anshmidt.easynote;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anshmidt.easynote.activities.BaseActivity;
import com.anshmidt.easynote.activities.EditNoteActivity;
import com.anshmidt.easynote.activities.MainActivity;
import com.anshmidt.easynote.activities.TrashActivity;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.easynote.dialogs.BottomSheetFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context context;
    private View contentView;
    private DatabaseHelper databaseHelper;
    private NoteDecorator noteDecorator;
    private PriorityInfo priorityInfo;
    public ArrayList<Note> notesList;
    private int selectedNotePosition = -1;
    public int longPressedNotePosition = -1;
    private final String LOG_TAG = NotesAdapter.class.getSimpleName();


    public final int MAIN_CONTEXT_MENU_ITEM_MAKE_IMPORTANT_ID = 1;
    public final int MAIN_CONTEXT_MENU_ITEM_MAKE_NORMAL_ID = 2;
    public final int MAIN_CONTEXT_MENU_ITEM_MAKE_MINOR_ID = 3;
    public final int MAIN_CONTEXT_MENU_ITEM_MOVE_ID = 4;

    public final int TRASH_CONTEXT_MENU_ITEM_PUT_BACK_ID = 1;

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnCreateContextMenuListener {
        TextView noteTextView;
        TextView listNameTextView;
        EditText noteEditText;
        ImageButton moreButton;
        InputMethodManager imm;

        NoteViewHolder(View itemView) {
            super(itemView);
            listNameTextView = (TextView) itemView.findViewById(R.id.note_listname_textview);
            moreButton = (ImageButton) itemView.findViewById(R.id.note_more_button);
            setListNamesVisibility(listNameTextView);
            setNoteTextViewsVisibility(itemView);



            if (context instanceof EditNoteActivity) {
                imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                noteEditText = (EditText) itemView.findViewById(R.id.note_edittext);

                moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KeyboardHelper keyboardHelper = new KeyboardHelper(context);
                        keyboardHelper.hideKeyboard((EditText) noteEditText.findViewById(R.id.note_edittext));
                        displayMainBottomSheet(selectedNotePosition);
                    }
                });

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

            } else if (context instanceof MainActivity) {
                noteTextView = (TextView) itemView.findViewById(R.id.note_textview);

                itemView.setOnClickListener(this);

                itemView.setOnCreateContextMenuListener(this);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longPressedNotePosition = getAdapterPosition();
                        return false;
                    }
                });
            } else if (context instanceof TrashActivity) {
                noteTextView = (TextView) itemView.findViewById(R.id.note_textview);

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

        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (context instanceof EditNoteActivity) {
                EditText noteEditText = (EditText) v;
                if (hasFocus) {
                    int height = noteEditText.getHeight();
                    Log.d(LOG_TAG, "height: " + height);
                    selectedNotePosition = getAdapterPosition();
                    Log.i(LOG_TAG, "onFocusChange: selected item: position: " + selectedNotePosition + ", id in database: " + getNoteDbId(selectedNotePosition));

                    moreButton.setVisibility(View.VISIBLE);

                    if (noteEditText.getText().toString().equals("")) {
                        noteEditText.setHint(context.getString(R.string.new_note_hint));
                    }

                } else {
                    int height = noteEditText.getHeight();
                    Log.d(LOG_TAG, "height: " + height);
                    if (noteEditText.getText().toString().equals("")) {
                        noteEditText.setHint("");
                    }
                    moreButton.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (context instanceof MainActivity) {
//                displayMainContextMenu(v, longPressedNotePosition, menu);
                displayMainBottomSheet(longPressedNotePosition);
            }
            if (context instanceof TrashActivity) {
                displayTrashContextMenu(v, longPressedNotePosition, menu);
            }
        }
    }


    public NotesAdapter(ArrayList<Note> notesList, Context context){
        this.notesList = notesList;
        this.context = context;
        databaseHelper = DatabaseHelper.getInstance(this.context);
        noteDecorator = new NoteDecorator(context);
        priorityInfo = new PriorityInfo(context);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)  {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note, viewGroup, false);
        return new NoteViewHolder(view);
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(final NoteViewHolder noteViewHolder, final int i) {
        TextView noteView;
        TextView listNameTextView;

        if (context instanceof EditNoteActivity) {
            noteView = noteViewHolder.noteEditText;
        } else {
            noteView = noteViewHolder.noteTextView;
        }
        noteView.setText(notesList.get(i).text);

        listNameTextView = noteViewHolder.listNameTextView;
        setListNamesVisibility(listNameTextView);
        setPriorityButtonsVisibility(noteViewHolder, i);

        Priority notePriority = notesList.get(i).priority;
        noteDecorator.displayPriority(noteView, notePriority);

        if (context instanceof EditNoteActivity) {

            if (selectedNotePosition == i) {
                EditText noteEditText = (EditText) noteView;
                noteEditText.setSelection(noteEditText.getText().length()); //move cursor_searchview to the end of the note

            }
        }



        if (noteViewHolder.listNameTextView != null) {
            noteViewHolder.listNameTextView.setText(notesList.get(i).list.name);
        }
    }


    @Override
    public void onViewAttachedToWindow(NoteViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.noteEditText != null) {
            // fixes Android bug with some edittext not selectable: TextView does not support text selection: Selection cancelled.
            holder.noteEditText.setEnabled(false);
            holder.noteEditText.setEnabled(true);

//            Log.d(LOG_TAG, "Info from onViewAttachedToWindow: " + holder.getAdapterPosition() + ", "
//                    + holder.getItemId() + ", " + holder.getLayoutPosition() + ", " + holder.noteEditText.getText());

            if (selectedNotePosition == holder.getAdapterPosition()) {
                holder.noteEditText.requestFocus();
            }
        }
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
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

    public int getPositionById(int noteId) {
        for (Note noteFromList : notesList) {
            if (noteFromList.id == noteId) {
                return getPosition(noteFromList);
            }
        }
        return -1;
    }

    public void filter(String searchRequest, boolean isSearchViewIconified) {  //isSearchViewIconified = is it collapsed
        if (! isSearchViewIconified) {
            notesList.clear();
            if (!searchRequest.isEmpty()) {
                if (context instanceof TrashActivity) {
                    notesList = databaseHelper.getSearchResultsFromTrash(searchRequest);
                } else {
                    notesList = databaseHelper.getSearchResultsFromAllLists(searchRequest);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void filterForEmptySearchRequest(boolean isSearchViewIconified) {
        filter("", isSearchViewIconified);
    }

//    public void resetFilter() {
//        notesList.clear();
////        notesList.addAll(searchResultsList);
//        notifyDataSetChanged();
//    }

    public String getNoteText(int position) {
        return notesList.get(position).text;
    }

    public Note getNoteById(int noteId) {
        for (Note note : notesList) {
            if (noteId == note.id) {
                return note;
            }
        }
        return null;
    }

    public String getSelectedItemText() {
        return getNoteText(selectedNotePosition);
    }

    private void displayMainBottomSheet(int longPressedNotePosition) {
        Note longPressedNote = getNote(longPressedNotePosition);
        Log.d(LOG_TAG, "Long pressed note before changing: ");
        longPressedNote.printContentToLog();
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        Bundle selectedNoteBundle = new Bundle();
        selectedNoteBundle.putInt(bottomSheetFragment.KEY_SELECTED_NOTE_ID, longPressedNote.id);
        selectedNoteBundle.putInt(bottomSheetFragment.KEY_SELECTED_NOTE_POSITION, longPressedNotePosition);
        selectedNoteBundle.putString(bottomSheetFragment.KEY_SELECTED_NOTE_PRIORITY_NAME, longPressedNote.priority.name);
        bottomSheetFragment.setArguments(selectedNoteBundle);
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.FRAGMENT_TAG);
    }

    private void displayMainContextMenu(View itemView, int longPressedNotePosition, ContextMenu menu) {
        String titlePrefix = context.getString(R.string.note_context_menu_change_priority_title) + " ";
        String titleSetImportantPriority = titlePrefix + priorityInfo.IMPORTANT.name.toLowerCase();
        String titleSetNormalPriority = titlePrefix + priorityInfo.NORMAL.name.toLowerCase();
        String titleSetMinorPriority = titlePrefix + priorityInfo.MINOR.name.toLowerCase();

        Note longPressedNote = getNote(longPressedNotePosition);
        Priority currentPriority = longPressedNote.priority;

        if (! currentPriority.equals(priorityInfo.IMPORTANT)) {
            menu.add(0, MAIN_CONTEXT_MENU_ITEM_MAKE_IMPORTANT_ID, 0, titleSetImportantPriority);
        }
        if (! currentPriority.equals(priorityInfo.NORMAL)) {
            menu.add(0, MAIN_CONTEXT_MENU_ITEM_MAKE_NORMAL_ID, 0, titleSetNormalPriority);
        }
        if (! currentPriority.equals(priorityInfo.MINOR)) {
            menu.add(0, MAIN_CONTEXT_MENU_ITEM_MAKE_MINOR_ID, 0, titleSetMinorPriority);
        }

        menu.add(0, MAIN_CONTEXT_MENU_ITEM_MOVE_ID, 0, context.getString(R.string.note_context_menu_move));
    }

    private void displayTrashContextMenu(View itemView, int longPressedNotePosition, ContextMenu menu) {
        String title = context.getString(R.string.note_context_menu_put_back_from_trash);
        menu.add(0, TRASH_CONTEXT_MENU_ITEM_PUT_BACK_ID, 0, title);
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
                Long modTime1 = note1.modificationTime;
                Long modTime2 = note2.modificationTime;

                if (context instanceof TrashActivity) {
                    return modTime2.compareTo(modTime1);
                } else {
                    if ( !(priorityId1.equals(priorityId2))) {
                        return priorityId1.compareTo(priorityId2);
                    }
                    return modTime2.compareTo(modTime1);
                }
            }
        });
    }

    public void setListNamesVisibility(TextView listNameTextView) {
        if (context instanceof TrashActivity) {
            listNameTextView.setVisibility(View.VISIBLE);

        } else {
            SearchController searchController = ((BaseActivity) context).searchController;
            if (listNameTextView == null) {
                Log.d(LOG_TAG, "listNameTextView == null");
                return;
            }
            if (searchController == null || searchController.isSearchViewNull() || searchController.isSearchViewIconified()) {
                listNameTextView.setVisibility(View.GONE);
            } else {
                listNameTextView.setVisibility(View.VISIBLE);
            }
        }
    }



    public void setNoteTextViewsVisibility(View itemView) {
        EditText noteEditText = (EditText) itemView.findViewById(R.id.note_edittext);
        TextView noteTextView = (TextView) itemView.findViewById(R.id.note_textview);
        if (context instanceof EditNoteActivity) {
            noteEditText.setVisibility(View.VISIBLE);
            noteTextView.setVisibility(View.GONE);
        } else {
            noteEditText.setVisibility(View.GONE);
            noteTextView.setVisibility(View.VISIBLE);
        }
    }


    public void setPriorityButtonsVisibility(final NoteViewHolder noteViewHolder, final int i) {
        ImageButton changePriorityButton = noteViewHolder.moreButton;
        if (context instanceof EditNoteActivity) {
            if (selectedNotePosition == i) {
                changePriorityButton.setVisibility(View.VISIBLE);
            } else {
                changePriorityButton.setVisibility(View.GONE);
            }
        } else {
            changePriorityButton.setVisibility(View.GONE);
        }
    }


}
