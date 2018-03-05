package com.anshmidt.easynote.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anshmidt.easynote.KeyboardHelper;
import com.anshmidt.easynote.R;

/**
 * Created by Ilya Anshmidt on 17.02.2018.
 */

public class ConfirmationDialogFragment extends DialogFragment {


    public interface ConfirmationDialogListener {
        void onListMovedToTrashConfirmed();
    }

    public final String KEY_CURRENT_LIST_NAME = "current_list_name";
    public final String FRAGMENT_TAG = "confirmationDialog";
    TextView confirmationTextView;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_confirmation, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(subView);

        String currentListName = getArguments().getString(KEY_CURRENT_LIST_NAME);
        confirmationTextView = (TextView) subView.findViewById(R.id.textview_confirmationdialog_title);

        String confirmationText = getString(R.string.delete_list_confirmation_dialog_text, currentListName);
        confirmationTextView.setText(confirmationText);


        builder.setPositiveButton(R.string.rename_list_dialog_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConfirmationDialogListener activity = (ConfirmationDialogListener) getActivity();
                activity.onListMovedToTrashConfirmed();
//                String listName = renameListEditText.getText().toString();
//                if (listNameValid) {
//                    RenameListDialogListener activity = (RenameListDialogListener) getActivity();
//
//                    if (mode == Mode.renaming) {
//                        activity.onListRenamed(listName);
//                    } else {
//                        activity.onListAdded(listName);
//                    }
//
//                }

            }
        });

        builder.setNegativeButton(R.string.rename_list_dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                keyboardHelper.hideKeyboard(renameListEditText);
            }
        });

        final AlertDialog dialog = builder.create();


        return dialog;
    }





}
