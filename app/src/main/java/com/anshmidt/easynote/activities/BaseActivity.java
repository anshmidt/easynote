package com.anshmidt.easynote.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.anshmidt.easynote.dialogs.ConfirmationDialogFragment;
import com.anshmidt.easynote.list_names_spinner.ListNamesSpinnerController;
import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.dialogs.RenameListDialogFragment;
import com.anshmidt.easynote.SharedPreferencesHelper;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesListAdapter;
import com.anshmidt.easynote.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 04.09.2017.
 */

public abstract class BaseActivity extends AppCompatActivity
        implements RenameListDialogFragment.RenameListDialogListener,
        ListNamesSpinnerController.ListSelectedListener,
        ConfirmationDialogFragment.ConfirmationDialogListener
{

    private final String LOG_TAG = BaseActivity.class.getSimpleName();
    protected RecyclerView rv;
    protected LinearLayoutManager llm;
    private NotesListAdapter notesListAdapter;
    private DatabaseHelper databaseHelper;
    SearchView searchView;
    ImageView clearSearchButton;
    EditText searchField;
    String searchRequest;
    Toolbar toolbar;
    Spinner listNamesSpinner;
//    ListNamesSpinnerAdapter listNamesSpinnerAdapter;
    ListNamesSpinnerController listNamesSpinnerController;


    //NotesList currentList;
    SharedPreferencesHelper sharPrefHelper;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(BaseActivity.this);

        //temp
        databaseHelper.printAllNotes();
        //end of temp

        sharPrefHelper = new SharedPreferencesHelper(BaseActivity.this);
//        int currentListId = sharPrefHelper.getLastOpenedListId();
//        currentList = new NotesList(currentListId);


        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        listNamesSpinner = (Spinner) findViewById(R.id.list_spinner);

        listNamesSpinnerController = new ListNamesSpinnerController(listNamesSpinner, BaseActivity.this);
        listNamesSpinnerController.init(databaseHelper.getAllListNames());
        listNamesSpinnerController.setListSelectedListener(this);
    }


    protected void forceUsingOverflowMenu() {  //not using Menu button for devices with Menu button
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        expandSearchViewToWholeBar(searchView, menu);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchRequest) {
                notesListAdapter.filter(searchRequest);
                setSearchRequest(searchRequest);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchRequest) {
                if (! searchRequest.equals("")) {
                    notesListAdapter.filter(searchRequest);
                }
                setSearchRequest(searchRequest);
                return false;
            }
        });

        clearSearchButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListAdapter.resetFilter();
                searchField = (EditText) findViewById(R.id.search_src_text);
                searchField.setText("");
            }
        });

        if (searchRequest != null) {
            Log.d(LOG_TAG, "onCreateMenu: searchRequest: " + searchRequest);
            notesListAdapter.filter(searchRequest);
        }

        return true;
    }

    @Override
    public void onListSelected() {
        NotesList currentList = listNamesSpinnerController.getCurrentList();
        ArrayList<Note> notes = databaseHelper.getAllNotesFromList(currentList);
        notesListAdapter.notesList = notes;
        notesListAdapter.notifyDataSetChanged();

    }


    protected void expandSearchViewToWholeBar(final SearchView searchView, final Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, searchItem, true);
                notesListAdapter.resetFilter();
                return true;
            }
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setItemsVisibility(menu, searchItem, false);
                return true;
            }
        });
    }

    protected void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) {
                item.setVisible(visible);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add: {
                if (databaseHelper.getEmptyNotesCountInList(listNamesSpinnerController.getCurrentList()) > 0) {
                    return super.onOptionsItemSelected(item);
                }

                int newNotePosition = getNotesListAdapter().whereToAddNewNote();
                if (this instanceof MainActivity) {
                    openEditNoteActivity(newNotePosition);
                }
                Note newNote = new Note("", getBaseContext());

                notesListAdapter.add(newNotePosition, newNote);
                rv = (RecyclerView)findViewById(R.id.recyclerView);
                rv.getLayoutManager().scrollToPosition(newNotePosition);
                notesListAdapter.setSelectedNotePosition(newNotePosition);
                newNote.list = listNamesSpinnerController.getCurrentList();
                databaseHelper.addNote(newNote);
                break;
            }
            case R.id.action_rename_list: {
                RenameListDialogFragment renameListDialogFragment = new RenameListDialogFragment();
                Bundle currentListBundle = new Bundle();
                currentListBundle.putString(renameListDialogFragment.KEY_CURRENT_LIST_NAME, sharPrefHelper.getLastOpenedListName());
                renameListDialogFragment.setArguments(currentListBundle);
                FragmentManager manager = getFragmentManager();
                renameListDialogFragment.show(manager, renameListDialogFragment.FRAGMENT_TAG);
                break;
            }
            case R.id.action_delete_list: {
                ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
                Bundle currentListBundle = new Bundle();
                currentListBundle.putString(confirmationDialogFragment.KEY_CURRENT_LIST_NAME, sharPrefHelper.getLastOpenedListName());
                confirmationDialogFragment.setArguments(currentListBundle);
                FragmentManager manager = getFragmentManager();
                confirmationDialogFragment.show(manager, confirmationDialogFragment.FRAGMENT_TAG);
                break;
            }
            case R.id.action_open_trash: {
                startActivity(new Intent(this, TrashActivity.class));
                break;
            }
//            case R.id.action_recreate_db: {  //for debug purposes only
//                databaseHelper.fillDatabaseWithDefaultData();
//                recreate();
//                break;
//            }
//            case R.id.action_perform_sql_request: {  //for debug purposes only
//                databaseHelper.performSqlRequest();
//                recreate();
//                break;
//            }
            case R.id.action_settings: {
                Toast.makeText(BaseActivity.this, getString(R.string.menu_settings_title), Toast.LENGTH_LONG).show();
                break;
            }
            case android.R.id.home: {  // "Up" button
                //databaseHelper.deleteEmptyNotesFromList(listNamesSpinnerController.getCurrentList());
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }



    public void openEditNoteActivity(final int itemPosition) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("itemPosition", itemPosition);
        if (searchRequest != null) {
            intent.putExtra("searchRequest", searchRequest);
        }
        startActivity(intent);
    }


    public void setSearchRequest(String request) {
        this.searchRequest = request;
    }

    protected NotesListAdapter getNotesListAdapter() {
        return notesListAdapter;
    }

    protected void setNotesListAdapter(NotesListAdapter adapter) {
        this.notesListAdapter = adapter;
    }

    @Override
    public void onListRenamed(String listName) {
        int currentListId = listNamesSpinnerController.getCurrentList().id;
        NotesList renamedList = new NotesList(currentListId, listName);
        listNamesSpinnerController.onListRenamed(renamedList);
        databaseHelper.updateList(renamedList);
    }

    @Override
    public void onListAdded(String listName) {
        NotesList newList = new NotesList(listName);
        listNamesSpinnerController.onListAdded(newList);
        listNamesSpinnerController.setSpinnerPosition(listNamesSpinner, newList);
        databaseHelper.addList(newList);
    }

    @Override
    public void onListMovedToTrashConfirmed() {
        NotesList list = listNamesSpinnerController.getCurrentList();
        databaseHelper.moveListToTrash(list);
        databaseHelper.moveAllNotesFromListToTrash(list);
        listNamesSpinnerController.onListMovedToTrash(list);
    }
}
