package com.obviz.review;

import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.obviz.review.adapters.ResultsAdapter;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

/**
 * Display the results of a search for an app
 */
public class ActivitySearch extends AppCompatActivity {

    private ResultsAdapter mAdapter;

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

        ListFragment fragment = (ListFragment) getFragmentManager().findFragmentById(R.id.fragment);

        mAdapter = new ResultsAdapter(getApplicationContext());
        fragment.setListAdapter(mAdapter);
        fragment.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ActivitySearch.this, DetailsActivity.class);

                String appID = mAdapter.getIDWithPosition(position);
                intent.putExtra(Constants.INTENT_APP_ID, appID);

                startActivity(intent);
            }
        });

        // Get the intent
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            GeneralWebService.getInstance().searchApp(query, fragment.getListView());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop all the possible web requests
        ConnectionService.instance.cancel();

        // Clear to free the memory of the images
        mAdapter.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
