package com.obviz.review;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

/**
 * Display the results of a search for an app
 * @intent INTENT_SEARCH value of the query if the activity is launched without the SearchView
 */
public class ActivitySearch extends AppCompatActivity {

    private AppBoxAdapter mAdapter;
    private String mQuery;

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Get the intent
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);

            // Add the query in the database
            DatabaseService.instance().insertHistoryEntry(mQuery);

        } else if (states != null) {

            mQuery = states.getString(Constants.STATE_SEARCH);
        } else {

            mQuery = intent.getStringExtra(Constants.INTENT_SEARCH);
        }

        GridRecyclerView grid = (GridRecyclerView) findViewById(R.id.grid_view);
        mAdapter = new AppBoxAdapter();
        mAdapter.addOnItemClickListener(new ItemClickListener());
        grid.setAdapter(mAdapter);

        // Perform the search
        GeneralWebService.instance().searchApp(mQuery, mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop all the possible web requests
        ConnectionService.instance.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {
        states.putString(Constants.STATE_SEARCH, mQuery);
        super.onSaveInstanceState(states);
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

    private class ItemClickListener implements GridAdapter.OnItemClickListener {
        @Override
        public void onClick(int position) {
            Intent intent = new Intent(ActivitySearch.this, DetailsActivity.class);
            intent.putExtra(Constants.INTENT_APP, (Parcelable) mAdapter.getItem(position));

            startActivity(intent);
        }
    }
}
