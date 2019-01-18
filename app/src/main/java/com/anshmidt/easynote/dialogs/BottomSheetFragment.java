package com.anshmidt.easynote.dialogs;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anshmidt.easynote.KeyboardHelper;
import com.anshmidt.easynote.Priority;
import com.anshmidt.easynote.PriorityInfo;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.activities.EditNoteActivity;
import com.anshmidt.easynote.activities.MainActivity;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.oneline_list_entry_selector.OneLineListEntrySelector;

import java.util.ArrayList;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    public final String FRAGMENT_TAG = "bottomSheetFragment";
    public final String KEY_SELECTED_NOTE_ID = "selected_note_id";
    public final String KEY_SELECTED_NOTE_POSITION = "selected_note_position";
    public final String KEY_SELECTED_NOTE_PRIORITY_NAME = "selected_note_priority_id";
    private TextView moveNoteTextView;
    private int selectedNoteId;
    private int selectedNotePosition;
    private String selectedNotePriorityName;
    private OneLineListEntrySelector oneLineListEntrySelector;
    private DatabaseHelper databaseHelper;
    private Priority changedPriority;

    public interface BottomSheetListener {
        void onPriorityChanged(int selectedNoteId, Priority newPriority);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet, container,
                false);

        databaseHelper = DatabaseHelper.getInstance(getContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedNoteId = bundle.getInt(KEY_SELECTED_NOTE_ID);
            selectedNotePosition = bundle.getInt(KEY_SELECTED_NOTE_POSITION);
            selectedNotePriorityName = bundle.getString(KEY_SELECTED_NOTE_PRIORITY_NAME);
        } else {
            selectedNoteId = -1;
        }

        moveNoteTextView = (TextView) view.findViewById(R.id.bottom_sheet_move_textview);

        moveNoteTextView.setText(R.string.bottom_sheet_move_title);

        moveNoteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();



                MoveNoteDialogFragment moveNoteDialogFragment = new MoveNoteDialogFragment();
                Bundle selectedNoteBundle = new Bundle();
                selectedNoteBundle.putInt(moveNoteDialogFragment.KEY_SELECTED_NOTE_ID, selectedNoteId);
                moveNoteDialogFragment.setArguments(selectedNoteBundle);
                FragmentManager manager = getActivity().getFragmentManager();
                moveNoteDialogFragment.show(manager, moveNoteDialogFragment.FRAGMENT_TAG);
            }
        });

        oneLineListEntrySelector = view.findViewById(R.id.bottom_sheet_priority_selector);
        PriorityInfo priorityInfo = new PriorityInfo(getContext());
        ArrayList<String> priorities = new ArrayList<>();
        priorities.add(priorityInfo.MINOR.name);
        priorities.add(priorityInfo.NORMAL.name);
        priorities.add(priorityInfo.IMPORTANT.name);
        oneLineListEntrySelector.setList(priorities);
        oneLineListEntrySelector.setInitialEntryNumber(priorities.indexOf(selectedNotePriorityName));
        setPriorityAppearance(selectedNotePriorityName);

        oneLineListEntrySelector.setOnValueChangeListener(new OneLineListEntrySelector.OnValueChangeListener() {
            @Override
            public void onValueChange(OneLineListEntrySelector oneLineListEntrySelector, String oldValue, String newValue) {
                changedPriority = new Priority(newValue, getContext());
                setPriorityAppearance(newValue);
            }
        });

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        if (getActivity() instanceof EditNoteActivity) {
            KeyboardHelper keyboardHelper = new KeyboardHelper(getContext());
            keyboardHelper.showKeyboard();
        }
        super.onDismiss(dialog);
        if (changedPriority != null) {
            BottomSheetListener bottomSheetListener = (BottomSheetListener) getActivity();
            bottomSheetListener.onPriorityChanged(selectedNoteId, changedPriority);
        }
    }

    public void setPriorityAppearance(String currentPriority) {
        PriorityInfo priorityInfo = new PriorityInfo(getContext());
        if (currentPriority.equals(priorityInfo.IMPORTANT.name)) {
            oneLineListEntrySelector.setTextStyle(Typeface.BOLD);
            oneLineListEntrySelector.setTextColor(ContextCompat.getColor(getContext(), R.color.notesTextColor));
        }
        if (currentPriority.equals(priorityInfo.NORMAL.name)) {
            oneLineListEntrySelector.setTextStyle(Typeface.NORMAL);
            oneLineListEntrySelector.setTextColor(ContextCompat.getColor(getContext(), R.color.notesTextColor));
        }
        if (currentPriority.equals(priorityInfo.MINOR.name)) {
            oneLineListEntrySelector.setTextStyle(Typeface.ITALIC);
            oneLineListEntrySelector.setTextColor(ContextCompat.getColor(getContext(), R.color.minorNotesTextColor));
        }


    }
}
