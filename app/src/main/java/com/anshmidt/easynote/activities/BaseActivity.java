package com.anshmidt.easynote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.anshmidt.easynote.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesListAdapter;
import com.anshmidt.easynote.R;

import java.lang.reflect.Field;

/**
 * Created by Sigurd Sigurdsson on 04.09.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private RecyclerView rv;
    private NotesListAdapter adapter;
    private DatabaseHelper databaseHelper;
    SearchView searchView;
    ImageView clearSearchButton;
    EditText searchField;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(BaseActivity.this);

        //temp
        databaseHelper.printAll();
        //databaseHelper.clearAllNotes();
        //databaseHelper.fillDatabaseWithTestData();
        //end of temp

        //NoteDataHolder.getInstance().setNotesList(databaseHelper.getAllNotes());
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
        getMenuInflater().inflate(R.menu.menu_main,menu);
        
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        expandSearchViewToWholeBar(searchView, menu);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchRequest) {
                adapter.filter(searchRequest);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchRequest) {
                if (! searchRequest.equals("")) {
                    adapter.filter(searchRequest);
                }
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
                if (this instanceof MainActivity) {
                    openEditNoteActivity(0);
                }
                Note newNote = new Note("");

                getNotesListAdapter().add(0, newNote);
                rv = (RecyclerView)findViewById(R.id.recyclerView);
                rv.getLayoutManager().scrollToPosition(0);
                getNotesListAdapter().setSelectedItem(0);
                databaseHelper.addNote(newNote);
                break;
            }
            case R.id.action_open_trash: {
                Toast.makeText(BaseActivity.this, getString(R.string.open_trash_title), Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.action_recreate_db: {  //for debug purposes only
                databaseHelper.clearAllNotes();
                databaseHelper.fillDatabaseWithTestData();
                recreate();
                break;
            }
            case R.id.action_settings: {
                Toast.makeText(BaseActivity.this, getString(R.string.settings_title), Toast.LENGTH_LONG).show();
                break;
            }
            case android.R.id.home: {  // "Up" button is clicked
                int selectedItem = adapter.getSelectedItemPosition();
                Note selectedNote = adapter.getItem(selectedItem);
                Log.d("TAG", "Id of selectedNote item in database: id = "+selectedNote.getId());
                selectedNote.printContent();
                //Note tempSelectedNoteData = adapter.notesList.get(selectedItem);
                databaseHelper.updateNote(selectedNote);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }



    public void openEditNoteActivity(final int itemPosition) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("itemPosition", itemPosition);
        startActivity(intent);
    }

    protected NotesListAdapter getNotesListAdapter() {
        return adapter;
    }

    protected void setNotesListAdapter(NotesListAdapter adapter) {
        this.adapter = adapter;
    }

//    private ItemTouchHelper initItemTouchHelper() {
//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                final int position = viewHolder.getAdapterPosition();
//                getNotesListAdapter().remove(position);
//                Toast.makeText(BaseActivity.this, "Item with position = "+position+" deleted", Toast.LENGTH_SHORT).show();
//                Log.i("TAG","Item with position = "+position+" deleted");
//            }
//        };
//        return new ItemTouchHelper(simpleItemTouchCallback);
//    }

}
