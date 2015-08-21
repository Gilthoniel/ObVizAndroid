package com.obviz.review;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.obviz.review.adapters.AppBaseAdapter;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

/**
 * Display the results of a search for an app
 * @intent INTENT_SEARCH value of the query if the activity is launched without the SearchView
 */
public class ActivitySearch extends AppCompatActivity {

    private AppBaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Get the intent
        Intent intent = getIntent();
        String query;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            // Add the query in the database
            DatabaseService.instance().insertHistoryEntry(query);
        } else {

            query = intent.getStringExtra(Constants.INTENT_SEARCH);
        }

        GridView grid = (GridView) findViewById(R.id.grid_view);
        mAdapter = new AppBoxAdapter(getApplicationContext());
        grid.setAdapter(mAdapter);
        grid.setEmptyView(findViewById(R.id.empty_view));
        grid.setOnItemClickListener(new ItemClickListener());

        // Perform the search
        GeneralWebService.instance().searchApp(query, grid);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop all the possible web requests
        ConnectionService.instance.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent intent = new Intent(ActivitySearch.this, DetailsActivity.class);
            intent.putExtra(Constants.INTENT_APP, (Parcelable) mAdapter.getItem(position));

            startActivity(intent);
        }
    }
}
