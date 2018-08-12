package com.anshmidt.easynote;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Ilya Anshmidt on 11.08.2018.
 */

public class SearchController {

    public interface OnSearchViewExpandListener {
        void onSearchViewCollapsed();
        void onSearchViewExpanded();
    }

    private OnSearchViewExpandListener onSearchViewExpandListener;
    private boolean searchViewIconified = true;
    private SearchView searchView;
    private ImageView clearSearchButton;
    private EditText searchField;
    public String searchRequest = "";
    private Menu menu;
    private NotesAdapter notesAdapter;

//    public SearchController() {
//
//    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    }

    public void setNotesAdapter(NotesAdapter notesAdapter) {
        this.notesAdapter = notesAdapter;
    }

    public void setOnSearchViewExpandListener(OnSearchViewExpandListener onSearchViewExpandListener) {
        this.onSearchViewExpandListener = onSearchViewExpandListener;
    }

    public void setTextListener() {
        expandSearchViewToWholeBar(searchView, menu);

        searchViewIconified = searchView.isIconified();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String enteredSearchRequest) {
                searchRequest = enteredSearchRequest;
                notesAdapter.filter(enteredSearchRequest, searchViewIconified);
//                setSearchRequest(searchRequest);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String enteredSearchRequest) {
                searchRequest = enteredSearchRequest;
                notesAdapter.filter(enteredSearchRequest, searchViewIconified);
//                if (! searchRequest.equals("")) {
//                    notesAdapter.filter(searchRequest);
//                }
//                setSearchRequest(searchRequest);
                return false;
            }
        });
    }

    public void setOnClickClearButtonListener() {
        clearSearchButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRequest = "";
                notesAdapter.filterForEmptySearchRequest(searchViewIconified);
                searchField = (EditText) searchView.findViewById(R.id.search_src_text);
                searchField.setText("");
            }
        });
    }

    public void onReceivedIntentWithSearchRequest(String searchRequestFromIntent) {
        searchRequest = searchRequestFromIntent;
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.expandActionView(searchMenuItem);
        searchField = (EditText) searchView.findViewById(R.id.search_src_text);
        searchField.setText(searchRequestFromIntent);
        searchViewIconified = false;
    }

    public void displayResultsIfNeeded() {
        if (searchRequest != null) {
            notesAdapter.filter(searchRequest, searchViewIconified);
        }
    }


    public boolean isSearchViewIconified() {
        return searchView.isIconified();
    }

    public boolean isSearchViewNull() {
        return searchView == null;
    }

    private void expandSearchViewToWholeBar(final SearchView searchView, final Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);


        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setMenuItemsVisibility(menu, item, true);
                searchRequest = "";
                searchViewIconified = true;
                if (onSearchViewExpandListener != null) {
                    onSearchViewExpandListener.onSearchViewCollapsed();
                }
                return true;
            }
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setMenuItemsVisibility(menu, item, false);
                searchViewIconified = false;
                if (onSearchViewExpandListener != null) {
                    onSearchViewExpandListener.onSearchViewExpanded();
                }
                return true;
            }
        });
    }


    private void setMenuItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) {
                item.setVisible(visible);
            }
        }
    }

}
