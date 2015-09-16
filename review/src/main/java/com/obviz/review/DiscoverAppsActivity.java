package com.obviz.review;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.adapters.AppBoxFullAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.AndroidFullApp;
import com.obviz.review.models.Category;
import com.obviz.review.models.OpinionValue;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.tasks.HttpTask;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DiscoverAppsActivity extends AppCompatActivity  {

    private Category mCategory;

    //list of criteria that matter to the user. e.g. design and network
    private ArrayList<Integer> topicIDs;
    private ArrayList<AndroidFullApp> mApps;

    private HttpTask<?> request;
    private AppBoxFullAdapter mAdapter;
    private GridRecyclerView mGridView;

    @Override
    protected void onCreate(Bundle states) {

        super.onCreate(states);
        mApps = new ArrayList<>();

        setContentView(R.layout.activity_apps_discover);

        // Get intent or states
        if (states != null) {
            mCategory = states.getParcelable(Constants.STATE_CATEGORY);
            topicIDs = states.getIntegerArrayList(Constants.STATE_TOPIC_IDS);
            //mbestApps = states.getParcelableArrayList(Constants.STATE_APPS_BEST);
            //mworstApps = states.getParcelableArrayList(Constants.STATE_APPS_WORST);



        } else {
            Intent intent = getIntent();
            mCategory = intent.getParcelableExtra(Constants.INTENT_CATEGORY);
            topicIDs = intent.getIntegerArrayListExtra(Constants.INTENT_TOPIC_IDS);
            //mbestApps = intent.getParcelableArrayListExtra(Constants.INTENT_APPS_BEST);
            //mworstApps = intent.getParcelableArrayListExtra(Constants.INTENT_APPS_WORST);
        }



        // Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setTitle(mCategory.getTitle());
        }



        // Copied from the Trending FragmentP:
        // Initiate the fragment list
        mGridView = (GridRecyclerView) findViewById(R.id.grid_view);
        mAdapter= new AppBoxFullAdapter();
        mAdapter.setCategory(mCategory);
        mAdapter.setTopics(topicIDs);


        // Launch the details activity when the user click on a app box
        mAdapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //TODO: verify with Gaylor - is the below action correct"?
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra(Constants.INTENT_APP, (Parcelable) mAdapter.getItem(position));

                startActivity(intent);
            }
        });

        //new : populate the grid:
        mAdapter.setState(GridAdapter.State.NONE);

        mAdapter.onLoadMore();

        //mAdapter.addAll(mApps);
        //mAdapter.shuffle(); // Random selection of the trending apps

        mGridView.setInfiniteAdapter(mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectionService.instance().cancel();
        mAdapter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {

        states.putParcelable(Constants.STATE_CATEGORY, mCategory);
        states.putIntegerArrayList(Constants.STATE_TOPIC_IDS, topicIDs);


        super.onSaveInstanceState(states);
    }



}
