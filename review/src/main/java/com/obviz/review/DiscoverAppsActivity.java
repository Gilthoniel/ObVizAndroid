package com.obviz.review;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.adapters.AppBoxFullAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.dialogs.TopicsDialog;
import com.obviz.review.json.MessageParser;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.managers.TutorialManager;
import com.obviz.review.models.AndroidFullApp;
import com.obviz.review.models.Category;
import com.obviz.review.models.Topic;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class DiscoverAppsActivity extends AppCompatActivity implements TopicsManager.TopicsObserver{

    private Category mCategory;

    //list of criteria that matter to the user. e.g. design and network
    private ArrayList<Integer> topicIDs;
    private ArrayList<AndroidFullApp> mApps;

    private AppBoxFullAdapter mAdapter;
    private GridRecyclerView mGridView;

    private TopicsDialog mDialog;
    private final List<Integer> DEFAULT_TOPICS = new ArrayList<>();
    private ArrayList<Topic> mTopics;

    @Override
    public void onTopicsLoaded() {
        mTopics = new ArrayList<>(TopicsManager.instance().getTopics(Topic.Type.DEFINED, this));
        topicIDs.clear();

        Log.i("MTOPICS", "Size: " + mTopics.size());
        createDialog();
        populateSelectedTopics(topicIDs);


        // tutorial for the user to explain how to open the dialog

        ViewGroup vg = (ViewGroup)findViewById(R.id.scroll_view);
        LinearLayout layout = (LinearLayout) vg.getChildAt(0);
        if(layout.getChildCount()>0) {
            TutorialManager.single(this)
                    .setTarget(layout.getChildAt(0))
                    .setContentText(R.string.tutorial_opinions)
                    .singleUse(Constants.TUTORIAL_TOPICS_KEY)
                    .setDelay(500)
                    .show();
        }


    }

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_apps_discover);

        //get the old IDs:
        topicIDs=new ArrayList<>();
        mApps = new ArrayList<>();
        DEFAULT_TOPICS.add(1);
        DEFAULT_TOPICS.add(2);

        //here: load the sharedPreferences:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<Integer> topics = MessageParser.fromJson(prefs.getString(Constants.PREFERENCES_SELECTED_TOPICS, "[]"),
                new TypeToken<Set<Integer>>(){}.getType());
        if (topics.size() >0) {
            topicIDs = new ArrayList<>(topics);
        }
        else{
            topicIDs.add(1);
            topicIDs.add(2);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(Constants.PREFERENCES_SELECTED_TOPICS, MessageParser.toJson(topicIDs));
            edit.apply();
        }

        mTopics = new ArrayList<>(TopicsManager.instance().getTopics(Topic.Type.DEFINED, this));
        createDialog();


        // Get intent or states
        if (states != null) {
            mCategory = states.getParcelable(Constants.STATE_CATEGORY);
        } else {
            Intent intent = getIntent();
            mCategory = intent.getParcelableExtra(Constants.INTENT_CATEGORY);
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
        mGridView = (GridRecyclerView) findViewById(R.id.grid_view_discover);
        mAdapter= new AppBoxFullAdapter();
        mAdapter.setCategory(mCategory);
        mAdapter.setTopics(topicIDs);


        //the list of chosen topics:
        populateSelectedTopics(topicIDs);


        // Launch the details activity when the user click on a app box
        mAdapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //TODO: verify with Gaylor - is the below action correct"?
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra(Constants.INTENT_APP, (Parcelable) mAdapter.getItem(position).getApp());

                startActivity(intent);
            }
        });

        //new : populate the grid:
        mAdapter.setState(GridAdapter.State.NONE);

        mAdapter.onLoadMore();

        //mAdapter.addAll(mApps);
        //mAdapter.shuffle(); // Random selection of the trending apps

        mGridView.setInfiniteAdapter(mAdapter);

        // open the dialog when the user click on the scroll view
        findViewById(R.id.topics_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("__DIALOG__", "Dialog is called");
                mDialog.show(getSupportFragmentManager(), "TOPICS");
            }
        });

        // tutorial for the user to explain how to open the dialog

        ViewGroup vg = (ViewGroup)findViewById(R.id.scroll_view);
        LinearLayout layout = (LinearLayout) vg.getChildAt(0);
        if(layout.getChildCount()>0) {
            TutorialManager.single((Activity) this)
                    .setTarget(layout.getChildAt(0))
                    .setContentText(R.string.tutorial_discover_opinions)
                    .singleUse(Constants.TUTORIAL_TOPICS_KEY)
                    .setDelay(500)
                    .show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectionService.instance().cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {

        states.putParcelable(Constants.STATE_CATEGORY, mCategory);
        states.putIntegerArrayList(Constants.STATE_TOPIC_IDS, topicIDs);
        super.onSaveInstanceState(states);
    }


    // __FRIDAY_9112015__

    private void createDialog() {
        // Instantiate the dialog
        mDialog = new TopicsDialog();

        Bundle args = new Bundle(); // put the list of topics for the dialog creation
        args.putParcelableArrayList(Constants.STATE_TOPIC, mTopics);

        mDialog.setArguments(args);
        mDialog.setOnDismissListener(new TopicsDialog.OnDismissListener() {
            @Override
            public void dialogDismiss(List<Topic> selectedItems) {

                // use the list to populate the Layout!
                topicIDs.clear();
                for (Topic t : selectedItems) {
                    topicIDs.add(t.getID());
                }
                //if they deselected everything , add everything!
                if(topicIDs.size()==0){
                    for(Topic t: mTopics)
                        topicIDs.add(t.getID());
                }

                // Populate the shared preferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(Constants.PREFERENCES_SELECTED_TOPICS, MessageParser.toJson(topicIDs));
                edit.apply();

                populateSelectedTopics(topicIDs);
                mAdapter.setTopics(topicIDs);

            }
        });

        if(mTopics.size()>1)
            for (Topic t : mTopics) {
                if (topicIDs.contains(t.getID()))
                    mDialog.addSelectedTopic(t);

            }
    }

    /**
     * Fill the horizontal scroll view with the selected buttons
     * @param ids IDs of the selected topics
     */


    private void populateSelectedTopics(List<Integer> ids) {

            // Horizontal linear layout where we populate the buttons
            LinearLayout layout = (LinearLayout) findViewById(R.id.topics_container);
            layout.removeAllViews();
            // Iteration over all the topics
            for (Topic topic : mTopics) {
                // We add the button only if the set contains the id
                if (ids.contains(topic.getID())) {

                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.label, layout, false);
                    ((TextView) view.findViewById(R.id.label_text)).setText(topic.getTitle());

                    layout.addView(view);
                }
            }

    }


}
