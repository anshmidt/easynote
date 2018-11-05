package com.anshmidt.easynote.dialogs;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.anshmidt.easynote.KeyboardHelper;
import com.anshmidt.easynote.Priority;
import com.anshmidt.easynote.PriorityInfo;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.activities.EditNoteActivity;
import com.anshmidt.oneline_list_entry_selector.OneLineListEntrySelector;

import java.util.ArrayList;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    public final String FRAGMENT_TAG = "bottomSheetFragment";
    public final String KEY_SELECTED_NOTE_ID = "selected_note_id";
    private TextView moveNoteTextView;
    private int selectedNoteId;
    private OneLineListEntrySelector oneLineListEntrySelector;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet, container,
                false);



        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedNoteId = bundle.getInt(KEY_SELECTED_NOTE_ID);
        } else {
            selectedNoteId = -1;
        }

        moveNoteTextView = (TextView) view.findViewById(R.id.bottom_sheet_move_textview);

        moveNoteTextView.setText("Move (selectedNoteId = "+selectedNoteId+")"); //temp for debugging

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
        oneLineListEntrySelector.setInitialEntryNumber(1);

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (getActivity() instanceof EditNoteActivity) {
            KeyboardHelper keyboardHelper = new KeyboardHelper(getContext());
            keyboardHelper.showKeyboard();
        }
        super.onDismiss(dialog);
    }
}
