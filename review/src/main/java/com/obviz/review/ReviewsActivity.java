package com.obviz.review;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.List;


public class ReviewsActivity extends AppCompatActivity {

    private String appID;
    private int topicID;
    private ReviewsAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        // Intent management
        Intent intent = getIntent();
        appID = intent.getStringExtra(Constants.INTENT_APP_ID);
        topicID = (int) intent.getLongExtra(Constants.INTENT_TOPIC_ID, -1);

        // Initiate the fragment list
        ListFragment fragment = (ListFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mListView = fragment.getListView();

        mAdapter = new ReviewsAdapter(getApplicationContext(), topicID);
        fragment.setListAdapter(mAdapter);

        // Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);

            List<String> spinnerItems = TopicsManager.instance.getTopicsTitle();

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, spinnerItems);
            adapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(adapter.getPosition(TopicsManager.instance.getTopicTitle(topicID)));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    // Stop the current requests
                    ConnectionService.instance.cancel();

                    // Get the good topic id related to the position of the item
                    mAdapter.setTopicID(TopicsManager.instance.getIDs().get(position));

                    // Clear the adapter and get the new items
                    mAdapter.clear();
                    GeneralWebService.getInstance().getReviews(appID, mListView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Well, we do anything in this case
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectionService.instance.cancel();
        mAdapter.clear();
    }

    @Override
    public void onResume() {
        super.onResume();

        GeneralWebService.getInstance().getReviews(appID, mListView);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        appID = intent.getStringExtra(Constants.INTENT_APP_ID);
        topicID = (int) intent.getLongExtra(Constants.INTENT_TOPIC_ID, -1);
        mAdapter.setTopicID(topicID);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reviews, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
