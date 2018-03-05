package com.anshmidt.easynote.list_names_spinner;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.SharedPreferencesHelper;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.easynote.dialogs.RenameListDialogFragment;

import java.util.List;

/**
 * Created by Ilya Anshmidt on 28.02.2018.
 */

public class ListNamesSpinnerController {

    public interface ListSelectedListener {
        void onListSelected();
    }

    ListSelectedListener listSelectedListener;
    ListNamesSpinnerAdapter listNamesSpinnerAdapter;
    Spinner listNamesSpinner;
    List<String> listNamesList;
    SharedPreferencesHelper sharedPreferencesHelper;
    DatabaseHelper databaseHelper;
    Context context;
    private NotesList currentList;
    private final String LOG_TAG = ListNamesSpinnerController.class.getSimpleName();

    public ListNamesSpinnerController(Spinner listNamesSpinner, Context context) {
        this.context = context;
        this.listNamesSpinner = listNamesSpinner;
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
        this.databaseHelper = DatabaseHelper.getInstance(context);

    }

    public void init(final List<String> listNamesList) {
        this.listNamesList = listNamesList;
        //this.currentList = currentList;
        initAdapter(this.listNamesList);
        int currentListId = sharedPreferencesHelper.getLastOpenedListId();
        String currentListName = databaseHelper.getListNameById(currentListId);
        setCurrentList(new NotesList(currentListId, currentListName));

        initOnItemSelectedListener();
        setSpinnerPosition(listNamesSpinner, currentList);
    }

    public void setListSelectedListener(ListSelectedListener listSelectedListener) {
        this.listSelectedListener = listSelectedListener;
    }

    public NotesList getCurrentList() {
        return currentList;
    }

    public void setCurrentList(NotesList currentList) {
        this.currentList = currentList;
    }

    public void onListRenamed(NotesList renamedList) {
        //int listId = renamedList.id;
        String renamedListName = renamedList.name;
        int position = getPositionInSpinner(currentList);
        listNamesList.set(position, renamedListName);

        initAdapter(listNamesList);
    }

    public void onListAdded(NotesList newList) {
        listNamesList.add(newList.name);
        initAdapter(listNamesList);
    }

    public void onListMovedToTrash(NotesList list) {
        listNamesList.remove(list.name);
        initAdapter(listNamesList);
    }

    private void initAdapter(List<String> list) {
        listNamesSpinnerAdapter = new ListNamesSpinnerAdapter(
                context,
                R.layout.list_spinner_item,
                list
        );
        listNamesSpinnerAdapter.setDropDownViewResource(R.layout.list_spinner_dropdown_item);
        listNamesSpinner.setAdapter(listNamesSpinnerAdapter);
    }

    private void initOnItemSelectedListener() {
        listNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "spinner item with position " + position + " selected");
                if (isAddListItemSelected(position)) {
                    RenameListDialogFragment renameListDialogFragment = new RenameListDialogFragment();
                    FragmentManager manager = ((Activity) context).getFragmentManager();
                    renameListDialogFragment.show(manager, renameListDialogFragment.FRAGMENT_TAG);
                } else {
                    NotesList selectedList = getListByPosition(position);
                    setCurrentList(selectedList);
                    sharedPreferencesHelper.setLastOpenedList(selectedList);
                    listSelectedListener.onListSelected();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isAddListItemSelected(int position) {
        int listSize = listNamesList.size();
        if (position == listSize - 1) {
            return true;
        } else {
            return false;
        }
    }



//    public int getListIdByPosition(int position) {
//        String selectedListName = listNamesList.get(position);
//        return databaseHelper.getListIdByName(selectedListName);
//    }

    public NotesList getListByPosition(int position) {
        String selectedListName = listNamesList.get(position);
        int selectedListId = databaseHelper.getListIdByName(selectedListName);
        return new NotesList(selectedListId, selectedListName);
    }

    public void setSpinnerPosition(Spinner spinner, NotesList currentList) {
        int position = getPositionInSpinner(currentList);
        spinner.setSelection(position);
    }

    public int getPositionInSpinner(NotesList list) {
        return listNamesList.indexOf(list.name);
    }
}
