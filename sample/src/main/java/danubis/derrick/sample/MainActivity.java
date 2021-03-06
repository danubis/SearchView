package danubis.derrick.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import danubis.derrick.searchview.SearchAdapter;
import danubis.derrick.searchview.SearchItem;
import danubis.derrick.searchview.SearchView;


public class MainActivity extends AppCompatActivity {

    protected SearchView mSearchView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSearchView();
    }

    protected void setSearchView() {

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setBackground(R.drawable.search_view_bg);
        mSearchView.setOpenedBackground(R.drawable.search_view_opened_bg);
        mSearchView.setHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.close();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
            @Override
            public boolean onClose() {
                Log.e("testing", "onClose...");
                return false;
            }

            @Override
            public boolean onOpen() {
                Log.e("testing", "onOpen...");
                return false;
            }
        });

        List<SearchItem> suggestionsList = new ArrayList<>();
        suggestionsList.add(new SearchItem("search1"));
        suggestionsList.add(new SearchItem("search2"));
        suggestionsList.add(new SearchItem("search3"));

        SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
        searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                String query = textView.getText().toString();
                mSearchView.setQuery(query, true);
                mSearchView.close();
            }
        });
        mSearchView.setAdapter(searchAdapter);
    }
}
