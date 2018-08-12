package com.anshmidt.easynote.activities;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Spinner;
import android.widget.Toast;

import com.anshmidt.easynote.NotesAdapter;
import com.anshmidt.easynote.NotesFormatter;
import com.anshmidt.easynote.SearchController;
import com.anshmidt.easynote.dialogs.ConfirmationDialogFragment;
import com.anshmidt.easynote.list_names_spinner.ListNamesSpinnerController;
import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.dialogs.RenameListDialogFragment;
import com.anshmidt.easynote.SharedPreferencesHelper;
import com.anshmidt.easynote.database.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.R;

import java.lang.reflect.Field;

/**
 * Created by Ilya Anshmidt on 04.09.2017.
 */

public abstract class BaseActivity extends AppCompatActivity
        implements RenameListDialogFragment.RenameListDialogListener,
        ListNamesSpinnerController.ListSelectedListener,
        ConfirmationDialogFragment.ConfirmationDialogListener,
        SearchController.OnSearchViewExpandListener
{

    private final String LOG_TAG = BaseActivity.class.getSimpleName();
    protected RecyclerView rv;
    protected LinearLayoutManager llm;
    private NotesAdapter notesAdapter;
    private DatabaseHelper databaseHelper;
    public final static String KEY_INTENT_SEARCH_REQUEST = "searchRequest";
    public final static String KEY_INTENT_ITEM_POSITION = "itemPosition";
//    public SearchView searchView;
//    private boolean searchViewIconified = true;
//    ImageView clearSearchButton;
//    EditText searchField;
//    public String searchRequest = "";
    FloatingActionButton addNoteButton;
    Toolbar toolbar;
    Spinner listNamesSpinner;
    ListNamesSpinnerController listNamesSpinnerController;
    public SearchController searchController;

    SharedPreferencesHelper sharPrefHelper;
    protected Toast movedToTrashToast = null;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchController = new SearchController();
        databaseHelper = DatabaseHelper.getInstance(BaseActivity.this);
        sharPrefHelper = new SharedPreferencesHelper(BaseActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        listNamesSpinner = (Spinner) findViewById(R.id.list_spinner);

        listNamesSpinnerController = new ListNamesSpinnerController(listNamesSpinner, BaseActivity.this);
        listNamesSpinnerController.init(databaseHelper.getAllListNames());
        listNamesSpinnerController.setListSelectedListener(this);

        addNoteButton = (FloatingActionButton) findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNoteButtonClicked();

            }
        });

        rv = (RecyclerView)findViewById(R.id.recyclerView);


        rv.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy < 0 && !addNoteButton.isShown() && searchController.isSearchViewIconified())
                    addNoteButton.show();
                else if (dy > 0 && addNoteButton.isShown())
                    addNoteButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

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


        searchController.setMenu(menu);
        searchController.setNotesAdapter(notesAdapter);
        searchController.setOnSearchViewExpandListener(this);
        searchController.setTextListener();
        searchController.setOnClickClearButtonListener();

//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        expandSearchViewToWholeBar(searchView, menu);
//
//        searchViewIconified = searchView.isIconified();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String searchRequest) {
//                notesAdapter.searchRequest = searchRequest;
//                notesAdapter.filter(searchRequest, searchViewIconified);
////                setSearchRequest(searchRequest);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String searchRequest) {
//                notesAdapter.searchRequest = searchRequest;
//                notesAdapter.filter(searchRequest, searchViewIconified);
////                if (! searchRequest.equals("")) {
////                    notesAdapter.filter(searchRequest);
////                }
////                setSearchRequest(searchRequest);
//                return false;
//            }
//        });

//        clearSearchButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
//        clearSearchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                notesAdapter.resetFilter();
//                notesAdapter.searchRequest = "";
//                notesAdapter.filterForEmptySearchRequest(searchViewIconified);
//                searchField = (EditText) findViewById(R.id.search_src_text);
//                searchField.setText("");
////                setSearchRequest("");
//            }
//        });

        String searchRequestFromIntent = getIntent().getStringExtra(KEY_INTENT_SEARCH_REQUEST);
        if ((searchRequestFromIntent != null) && (! searchRequestFromIntent.isEmpty())) {
            searchController.onReceivedIntentWithSearchRequest(searchRequestFromIntent);
//            notesAdapter.searchRequest = searchRequestFromIntent;
//            MenuItem searchMenuItem = menu.findItem(R.id.action_search);
//            MenuItemCompat.expandActionView(searchMenuItem);
//            searchField = (EditText) findViewById(R.id.search_src_text);
//            searchField.setText(searchRequestFromIntent);
//            searchViewIconified = false;
        }

        searchController.displayResultsIfNeeded();

//        if (notesAdapter.searchRequest != null) {
//            Log.d(LOG_TAG, "onCreateMenu: searchRequest: " + notesAdapter.searchRequest);
//            notesAdapter.filter(notesAdapter.searchRequest, searchViewIconified);
//        }

        return true;
    }

    @Override
    public void onListSelected() {
        NotesList currentList = listNamesSpinnerController.getCurrentList();
        notesAdapter.notesList = databaseHelper.getAllNotesFromList(currentList);
        notesAdapter.notifyDataSetChanged();

    }

    @Override
    public void onSearchViewCollapsed() {
        onListSelected();
        addNoteButton.show();
    }

    @Override
    public void onSearchViewExpanded() {
        addNoteButton.hide();
    }

//    protected void expandSearchViewToWholeBar(final SearchView searchView, final Menu menu) {
//        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
//
//        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                setMenuItemsVisibility(menu, searchMenuItem, true);
//                notesAdapter.searchRequest = "";
//                onListSelected();
//                searchViewIconified = true;
//                addNoteButton.show();
//                return true;
//            }
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                setMenuItemsVisibility(menu, searchMenuItem, false);
//                searchViewIconified = false;
//                addNoteButton.hide();
//                return true;
//            }
//        });
//    }

//    protected void setMenuItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
//        for (int i=0; i<menu.size(); ++i) {
//            MenuItem item = menu.getItem(i);
//            if (item != exception) {
//                item.setVisible(visible);
//            }
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
//            case R.id.action_add: {
//                //user is allowed to add more than 1 empty note, but they will be deleted when switching to MainActivity
//
//                int newNotePosition = getNotesAdapter().whereToAddNewNote();
//                if (this instanceof MainActivity) {
//                    openEditNoteActivity(newNotePosition);
//                }
//                Note newNote = new Note("", BaseActivity.this);
//                newNote.list = listNamesSpinnerController.getCurrentList();
//                newNote.id = databaseHelper.addNote(newNote);
//
//                notesAdapter.add(newNotePosition, newNote);
//                rv = (RecyclerView)findViewById(R.id.recyclerView);
//                rv.getLayoutManager().scrollToPosition(newNotePosition);
//                notesAdapter.setSelectedNotePosition(newNotePosition);
//                break;
//            }
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
            case R.id.action_copy_list_to_clipboard: {
                NotesFormatter notesFormatter = new NotesFormatter(BaseActivity.this);
                String textToCopy = notesFormatter.notesOfOneListToString(notesAdapter.notesList);

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, textToCopy);
                clipboard.setPrimaryClip(clip);

                String toastMessage = getString(R.string.list_copied_to_clipboard_toast, listNamesSpinnerController.getCurrentList().name);
                Toast.makeText(BaseActivity.this, toastMessage, Toast.LENGTH_LONG).show();
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
        intent.putExtra(KEY_INTENT_ITEM_POSITION, itemPosition);
        if (searchController.searchRequest != null) {
            intent.putExtra(KEY_INTENT_SEARCH_REQUEST, searchController.searchRequest);
        }
        startActivity(intent);
    }


//    public void setSearchRequest(String request) {
//        this.searchRequest = request;
//    }

    protected NotesAdapter getNotesAdapter() {
        return notesAdapter;
    }

    protected void setNotesAdapter(NotesAdapter adapter) {
        this.notesAdapter = adapter;
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
        onAddNoteButtonClicked(); //adding a first note to the list
        notesAdapter.notifyDataSetChanged();
        databaseHelper.addList(newList);
    }

    @Override
    public void onListMovedToTrashConfirmed() {
        NotesList list = listNamesSpinnerController.getCurrentList();
        databaseHelper.moveListToTrash(list);
        databaseHelper.moveAllNotesFromListToTrash(list);
        listNamesSpinnerController.onListMovedToTrash(list);
    }

    protected void setItemSwipeCallback(final NotesAdapter notesAdapter, RecyclerView rv) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Note noteToRemove = notesAdapter.getNote(position);

                notesAdapter.remove(position);
                databaseHelper.moveNoteToTrash(noteToRemove);
                if (movedToTrashToast != null) {
                    movedToTrashToast.cancel();
                }
                movedToTrashToast = Toast.makeText(BaseActivity.this, getString(R.string.note_moved_to_trash_toast), Toast.LENGTH_SHORT);
                movedToTrashToast.show();

            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    public void onAddNoteButtonClicked() {
        //user is allowed to add more than 1 empty note, but they will be deleted when switching to MainActivity
        int newNotePosition = getNotesAdapter().whereToAddNewNote();
        if (BaseActivity.this instanceof MainActivity) {
            openEditNoteActivity(newNotePosition);
        }
        Note newNote = new Note("", BaseActivity.this);
        newNote.list = listNamesSpinnerController.getCurrentList();
        newNote.id = databaseHelper.addNote(newNote);

        notesAdapter.add(newNotePosition, newNote);
        rv = (RecyclerView)findViewById(R.id.recyclerView);
        rv.getLayoutManager().scrollToPosition(newNotePosition);
        notesAdapter.setSelectedNotePosition(newNotePosition);
    }
}
