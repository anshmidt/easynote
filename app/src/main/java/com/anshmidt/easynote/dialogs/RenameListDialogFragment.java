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
import android.view.WindowManager;
import android.widget.EditText;

import com.anshmidt.easynote.KeyboardHelper;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.activities.MainActivity;

/**
 * Created by Ilya Anshmidt on 17.02.2018.
 */

public class RenameListDialogFragment extends DialogFragment {


    public interface RenameListDialogListener {
        void onListRenamed(String listName);
        void onListAdded(String listName);
    }

    public final String KEY_CURRENT_LIST_NAME = "current_list_name";
    public final String FRAGMENT_TAG = "renameListDialog";
    EditText renameListEditText;
    KeyboardHelper keyboardHelper;
    boolean listNameValid = true;
    String CREATED_LIST_DEFAULT_NAME = "";
    enum Mode { adding, renaming }
    Mode mode;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
    

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_rename_list, null);
        keyboardHelper = new KeyboardHelper(getActivity());
        renameListEditText = (EditText)subView.findViewById(R.id.edittext_renamelistdialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(subView);

        Bundle bundle = getArguments();
        String currentListName = CREATED_LIST_DEFAULT_NAME;
        if (bundle != null) {
            mode = Mode.renaming;
            currentListName = bundle.getString(KEY_CURRENT_LIST_NAME);
        } else {
            mode = Mode.adding;
        }

        renameListEditText.setText(currentListName);
        keyboardHelper.moveCursorToEnd(renameListEditText);
//        if (getActivity() instanceof MainActivity) {
//            keyboardHelper.showKeyboard(renameListEditText);
//        }



        builder.setPositiveButton(R.string.rename_list_dialog_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String listName = renameListEditText.getText().toString();
                if (listNameValid) {
                    RenameListDialogListener activity = (RenameListDialogListener) getActivity();

                    if (mode == Mode.renaming) {
                        activity.onListRenamed(listName);
                    } else {
                        activity.onListAdded(listName);
                    }

                    if (getActivity() instanceof MainActivity) {
                        keyboardHelper.hideKeyboard(renameListEditText);
                    }
                }

            }
        });

        builder.setNegativeButton(R.string.rename_list_dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() instanceof MainActivity) {
                    keyboardHelper.hideKeyboard(renameListEditText);
                }
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
                if (isListNameValid(s.toString())) {
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


    private boolean isListNameValid(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            return true;
        }
    }


}
