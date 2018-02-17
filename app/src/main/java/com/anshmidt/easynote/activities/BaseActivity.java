package com.anshmidt.easynote.activities;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.anshmidt.easynote.DatabaseHelper;
import com.anshmidt.easynote.ListsSpinnerAdapter;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesListAdapter;
import com.anshmidt.easynote.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 04.09.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected RecyclerView rv;
    protected LinearLayoutManager llm;
    private NotesListAdapter adapter;
    private DatabaseHelper databaseHelper;
    SearchView searchView;
    ImageView clearSearchButton;
    EditText searchField;
    String searchRequest;
    Toolbar toolbar;
    private final String LOG_TAG = BaseActivity.class.getSimpleName();




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(BaseActivity.this);

        //temp
        databaseHelper.printAll();
        //end of temp


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                adapter.filter(searchRequest);
                setSearchRequest(searchRequest);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchRequest) {
                if (! searchRequest.equals("")) {
                    adapter.filter(searchRequest);
                }
                setSearchRequest(searchRequest);
                return false;
            }
        });

        clearSearchButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resetFilter();
                searchField = (EditText) findViewById(R.id.search_src_text);
                searchField.setText("");
            }
        });

        if (searchRequest != null) {
            Log.d(LOG_TAG, "onCreateMenu: searchRequest: " + searchRequest);
            adapter.filter(searchRequest);
        }


//        Spinner spinner = (Spinner) findViewById(R.id.list_spinner);
//        List<String> list = new ArrayList<String>();
//        list.add("My notes");
//        list.add("To do");
//        list.add("Some other things");
//        list.add("+ Add New List");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.list_spinner_item, list);
//        dataAdapter.setDropDownViewResource(R.layout.list_spinner_dropdown_item);
//        spinner.setAdapter(dataAdapter);

        List<String> list = new ArrayList<String>();
        list.add("My notes");
        list.add("To do");
        list.add("Some other things");
//        list.add("+ Add New List");


        Spinner spinner = (Spinner) findViewById(R.id.list_spinner);
        ListsSpinnerAdapter listsSpinnerAdapter = new ListsSpinnerAdapter(this, R.layout.list_spinner_item, list);
        listsSpinnerAdapter.setDropDownViewResource(R.layout.list_spinner_dropdown_item);
        spinner.setAdapter(listsSpinnerAdapter);


//        spinner.setOnItemSelectedListener(onItemSelectedListener);


        return true;
    }

    protected void expandSearchViewToWholeBar(final SearchView searchView, final Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, searchItem, true);
                adapter.resetFilter();
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
                if (databaseHelper.getEmptyNotesCount() > 0) {
                    return super.onOptionsItemSelected(item);
                }

                int newNotePosition = getNotesListAdapter().whereToAddNewNote();
                if (this instanceof MainActivity) {
                    openEditNoteActivity(newNotePosition);
                }
                Note newNote = new Note("", getBaseContext());

                adapter.add(newNotePosition, newNote);
                rv = (RecyclerView)findViewById(R.id.recyclerView);
                rv.getLayoutManager().scrollToPosition(newNotePosition);
                adapter.setSelectedNotePosition(newNotePosition);
                databaseHelper.addNote(newNote);
                break;
            }
            case R.id.action_open_trash: {
                Toast.makeText(BaseActivity.this, getString(R.string.menu_open_trash_title), Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.action_recreate_db: {  //for debug purposes only
                databaseHelper.fillDatabaseWithTestData();
                recreate();
                break;
            }
            case R.id.action_perform_sql_request: {  //for debug purposes only
                databaseHelper.performSqlRequest();
                recreate();
                break;
            }
            case R.id.action_settings: {
                Toast.makeText(BaseActivity.this, getString(R.string.menu_settings_title), Toast.LENGTH_LONG).show();
                break;
            }
            case android.R.id.home: {  // "Up" button
                databaseHelper.deleteEmptyNotes();
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
        return adapter;
    }

    protected void setNotesListAdapter(NotesListAdapter adapter) {
        this.adapter = adapter;
    }


}
