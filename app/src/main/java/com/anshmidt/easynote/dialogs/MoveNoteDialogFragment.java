package com.anshmidt.easynote.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 11.05.2018.
 */

public class MoveNoteDialogFragment extends DialogFragment {

    public final String FRAGMENT_TAG = "moveNoteDialog";
    public final String KEY_SELECTED_NOTE_ID = "selected_note_id";
    private DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getActivity());

    public interface MoveNoteDialogListener {
        void onDestinationListChosen(int chosenListId, String chosenListName, int noteId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.move_note_dialog_title));

        Bundle bundle = getArguments();
        final int selectedNoteId;
        if (bundle != null) {
            selectedNoteId = bundle.getInt(KEY_SELECTED_NOTE_ID);
        } else {
            selectedNoteId = -1;
        }


        final List<String> allListNames = databaseHelper.getAllListNames();
        CharSequence items[] = allListNames.toArray(new CharSequence[allListNames.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String chosenListName = allListNames.get(which);
                int chosenListId = databaseHelper.getListIdByName(chosenListName);

                MoveNoteDialogListener activity = (MoveNoteDialogListener) getActivity();
                activity.onDestinationListChosen(chosenListId, chosenListName, selectedNoteId);
            }
        });


        final AlertDialog dialog = builder.create();
        return dialog;
    }
}
