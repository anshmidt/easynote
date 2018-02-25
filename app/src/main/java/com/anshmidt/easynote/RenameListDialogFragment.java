package com.anshmidt.easynote;

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

/**
 * Created by Ilya Anshmidt on 17.02.2018.
 */

public class RenameListDialogFragment extends DialogFragment {


    public interface RenameListDialogListener {
        void onListRenamed(String listName);
    }

    EditText renameListEditText;
    KeyboardHelper keyboardHelper;
    boolean listNameValid = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_rename_list, null);
        keyboardHelper = new KeyboardHelper(getActivity());
        renameListEditText = (EditText)subView.findViewById(R.id.edittext_renamelistdialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(subView);

//        String getArgument = getArguments().getString("number_of_alarms");
//        renameListEditText.setText(getArgument);
        keyboardHelper.moveCursorToEnd(renameListEditText);
        keyboardHelper.showKeyboard(renameListEditText);


        builder.setPositiveButton(R.string.rename_list_dialog_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String numberStr = renameListEditText.getText().toString();
                if (listNameValid) {
                    RenameListDialogListener activity = (RenameListDialogListener) getActivity();
                    activity.onListRenamed(numberStr);
                    keyboardHelper.hideKeyboard(renameListEditText);
                }

//                RenameListDialogListener activity = (RenameListDialogListener) getActivity();
//                activity.onNumberOfAlarmsChanged(renameListEditText.getText().toString());
//                keyboardHelper.hideKeyboard(renameListEditText);
            }
        });

        builder.setNegativeButton(R.string.rename_list_dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                keyboardHelper.hideKeyboard(renameListEditText);
            }
        });

        final AlertDialog dialog = builder.create();

        renameListEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isValid(s.toString())) {
                    listNameValid = true;
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    listNameValid = false;
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        return dialog;
    }


    private boolean isValid(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            return true;
        }
    }


}
