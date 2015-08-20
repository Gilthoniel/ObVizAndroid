package com.obviz.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.OpinionValue;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.List;


public class ReviewsActivity extends AppCompatActivity implements TopicsManager.TopicsObserver {

    private AndroidApp mApplication;
    private int topicID;
    private ReviewsAdapter mAdapter;
    private GridView mGridView;
    private Spinner mSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        // Intent management
        Intent intent = getIntent();
        mApplication = intent.getParcelableExtra(Constants.INTENT_APP);
        topicID = (int) intent.getLongExtra(Constants.INTENT_TOPIC_ID, -1);

        // Initiate the fragment list
        mGridView = (GridView) findViewById(R.id.grid_view);
        mGridView.setEmptyView(findViewById(android.R.id.empty));

        mAdapter = new ReviewsAdapter(getApplicationContext());
        mGridView.setAdapter(mAdapter);

        // Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setTitle(mApplication.getName());
        }

        mSpinner = (Spinner) findViewById(R.id.spinner);
        List<String> spinnerItems = new ArrayList<>();
        int spinnerIndex = 0;
        for (int i = 0; i < mApplication.getOpinions().size(); i++) {
            OpinionValue value = mApplication.getOpinions().get(i);
            spinnerItems.add(TopicsManager.instance().getTitle(value.topicID, this));

            if (value.topicID == topicID) {
                spinnerIndex = i;
            }
        }

        mSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, spinnerItems);
        mSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(spinnerIndex);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Stop the current requests
                ConnectionService.instance.cancel();

                // Get the good topic id related to the position of the item
                int topicID = mApplication.getOpinions().get(position).topicID;

                // Clear the adapter and get the new items
                mAdapter.clear();
                GeneralWebService.instance().getReviews(mApplication.getAppID(), topicID, 0, 10, mGridView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Well, we do anything in this case
            }
        });
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

        GeneralWebService.instance().getReviews(mApplication.getAppID(), topicID, 0, 10, mGridView);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mApplication = intent.getParcelableExtra(Constants.INTENT_APP);
        topicID = (int) intent.getLongExtra(Constants.INTENT_TOPIC_ID, -1);
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
    public void onTopicsLoaded() {

        List<String> spinnerItems = new ArrayList<>();
        for (int i = 0; i < mApplication.getOpinions().size(); i++) {

            OpinionValue value = mApplication.getOpinions().get(i);
            spinnerItems.add(TopicsManager.instance().getTitle(value.topicID, this));
        }

        mSpinnerAdapter.clear();
        mSpinnerAdapter.addAll(spinnerItems);
    }
}
