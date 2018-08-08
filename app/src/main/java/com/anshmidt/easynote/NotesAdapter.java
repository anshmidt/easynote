package com.anshmidt.easynote;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.TextView;

import com.anshmidt.easynote.activities.EditNoteActivity;
import com.anshmidt.easynote.activities.MainActivity;
import com.anshmidt.easynote.activities.TrashActivity;
import com.anshmidt.easynote.database.DatabaseHelper;

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
    private ArrayList<Note> searchResultsList = new ArrayList<>();  //for search results
    private int selectedNotePosition = -1;
    public int longPressedNotePosition = -1;
    private final String LOG_TAG = NotesAdapter.class.getSimpleName();
    private boolean justCreated = true;


    public final int MAIN_CONTEXT_MENU_ITEM_MAKE_IMPORTANT_ID = 1;
    public final int MAIN_CONTEXT_MENU_ITEM_MAKE_NORMAL_ID = 2;
    public final int MAIN_CONTEXT_MENU_ITEM_MAKE_MINOR_ID = 3;
    public final int MAIN_CONTEXT_MENU_ITEM_MOVE_ID = 4;

    public final int TRASH_CONTEXT_MENU_ITEM_PUT_BACK_ID = 1;

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnCreateContextMenuListener {
        TextView noteTextView;
        TextView listNameTextView;
        EditText noteEditText;
        InputMethodManager imm;
//        View itemView;

        NoteViewHolder(View itemView) {
            super(itemView);
//            this.itemView = (TextView) itemView;
            listNameTextView = (TextView) itemView.findViewById(R.id.note_listname_textview);
            if (context instanceof EditNoteActivity) {
                imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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


            } else if (context instanceof MainActivity) {
                noteTextView = (TextView) itemView.findViewById(R.id.note_textview);
                listNameTextView.setVisibility(View.GONE);
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
                listNameTextView.setVisibility(View.VISIBLE);

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

//            itemView.setOnClickListener(this);
//            itemView.setOnCreateContextMenuListener(this);
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    longPressedNotePosition = getAdapterPosition();
//                    return false;
//                }
//            });
        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (context instanceof EditNoteActivity) {
                EditText noteEditText = (EditText) v;
                //imm.showSoftInput(noteEditText, InputMethodManager.SHOW_FORCED);
                //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if (hasFocus) {
                    int height = noteEditText.getHeight();
                    Log.d(LOG_TAG, "height: " + height);
                    selectedNotePosition = getAdapterPosition();
                    Log.i(LOG_TAG, "onFocusChange: selected item: position: " + selectedNotePosition + ", id in database: " + getNoteDbId(selectedNotePosition));

//                    imm.showSoftInput(noteEditText, InputMethodManager.SHOW_IMPLICIT);  //also works
//                    if (height == 0) {
                        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    }


                    if (noteEditText.getText().toString().equals("")) {
                        noteEditText.setHint(context.getString(R.string.new_note_hint));
                    }

                } else {
                    int height = noteEditText.getHeight();
                    Log.d(LOG_TAG, "height: " + height);
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
                displayMainContextMenu(v, longPressedNotePosition, menu);
            }
            if (context instanceof TrashActivity) {
                displayTrashContextMenu(v, longPressedNotePosition, menu);
            }
        }
    }


    public NotesAdapter(ArrayList<Note> notesList, Context context){
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
        if (context instanceof EditNoteActivity){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.editable_note, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note, viewGroup, false);
        }
        return new NoteViewHolder(view);
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(final NoteViewHolder noteViewHolder, final int i) {
        TextView noteView;

        if (context instanceof EditNoteActivity) {
            noteView = noteViewHolder.noteEditText;
        } else {
            noteView = noteViewHolder.noteTextView;
        }
        noteView.setText(notesList.get(i).text);

        Priority notePriority = notesList.get(i).priority;
        noteDecorator.displayPriority(noteView, notePriority);

        if (context instanceof EditNoteActivity) {

            if (selectedNotePosition == i) {
                EditText noteEditText = (EditText) noteView;

//                noteEditText.requestFocus();
//                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(noteEditText, InputMethodManager.SHOW_IMPLICIT);


//                noteEditText.requestFocus();
                noteEditText.setSelection(noteEditText.getText().length()); //move cursor_searchview to the end of the note

                //new
//                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(noteEditText, InputMethodManager.SHOW_FORCED);
            }
        }

//        //temp
//        if (i == 0) {
//            if (context instanceof EditNoteActivity) {
//                EditText noteEditText = (EditText) noteView;
//                noteEditText.requestFocus();
//            }
//        }
//        //end of temp

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

    private void displayMainContextMenu(View itemView, int longPressedNotePosition, ContextMenu menu) {
        //menu.setHeaderTitle("Select The Action");

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