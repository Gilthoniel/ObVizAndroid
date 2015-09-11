package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.obviz.review.Constants;
import com.obviz.review.DiscoverAppsActivity;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.SuperCategoryGridAdapter;
import com.obviz.review.dialogs.TopicsDialog;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.Category;
import com.obviz.review.models.CategoryBase;
import com.obviz.review.models.Topic;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.reviews.R;

import java.util.*;


/**
 * Created by gaylor on 05-Aug-15.
 *
 */
public class DiscoverFragment extends Fragment implements HomeFragment, TopicsManager.TopicsObserver {

    // __FRIDAY_9112015__
    private final Set<Integer> DEFAULT_TOPICS = new HashSet<>();

    private SuperCategoryGridAdapter mDiscoverAdapter;
    private ArrayList<Topic> mTopics;
    private TopicsDialog mDialog;

    public DiscoverFragment() {
        super();

        DEFAULT_TOPICS.add(1);
        DEFAULT_TOPICS.add(2);
        DEFAULT_TOPICS.add(3);
        DEFAULT_TOPICS.add(4);
        DEFAULT_TOPICS.add(5);
    }

    // __FRIDAY_9112015__
    @Override
    public void onTopicsLoaded() {
        mTopics = new ArrayList<>(TopicsManager.instance().getTopics(Topic.Type.DEFINED, this));
        populateSelectedTopics(DEFAULT_TOPICS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {
        //Log.d("Pointer", "We are in the DiscoverFragment onCreateView");
        return inflater.inflate(R.layout.fragment_discover, parent, false);
    }

    @Override
    //TODO: the tutorial is just copied here. Must add relevant content
    public void showTutorial() {

    }

    @Override
    public String getTitle() {

        return "Discover";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_search_black_24dp;
    }

    public Boolean onBackPressed(){
        if( mDiscoverAdapter.getItemCount()>0)
            for(CategoryBase c: mDiscoverAdapter.getItems()){
                Log.d("Debug types",c.getClass().toString());
                if(c.getClass()== Category.class){
                    // re initialize the adapter content
                    mDiscoverAdapter.onCategoriesLoaded();
                    return false;
                }
            }

        return true;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() != null) {
            final GridRecyclerView grid = (GridRecyclerView) getView().findViewById(R.id.grid_view);

            mDiscoverAdapter = new SuperCategoryGridAdapter();

            //final AppBoxAdapter trendingAdapter = new AppBoxAdapter();

            mDiscoverAdapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    CategoryBase cat = mDiscoverAdapter.getItem(position);

                    if(cat.getCategories().size()>1) {

                        mDiscoverAdapter.clear();
                        mDiscoverAdapter.getChildCategories(cat);

                    } else {

                        Intent intent = new Intent(getContext(), DiscoverAppsActivity.class);

                        intent.putExtra(Constants.INTENT_CATEGORY, (Parcelable) cat);

                        //TODO: pass the real topics here
                        ArrayList<Integer> topics = new ArrayList<Integer>(Arrays.asList(1));
                        //int[] topics = new int[1];
                        //topics[0]=1;
                        intent.putIntegerArrayListExtra(Constants.INTENT_TOPIC_IDS, topics);

                        //TODO: this will crash if the contents of the best apps array is null!
                        intent.putParcelableArrayListExtra(Constants.INTENT_APPS_BEST, new ArrayList<AndroidApp>(mDiscoverAdapter.getBestApps(cat)));
                        intent.putParcelableArrayListExtra(Constants.INTENT_APPS_WORST, new ArrayList<AndroidApp>(mDiscoverAdapter.getWorstApps(cat)));

                        intent.putIntegerArrayListExtra(Constants.INTENT_TOPIC_IDS, topics);

                        startActivity(intent);

                    }
                }
            });

            grid.setAdapter(mDiscoverAdapter);

            // __FRIDAY_9112015__
            // get available topics or put the fragment in the observers list
            mTopics = new ArrayList<>(TopicsManager.instance().getTopics(Topic.Type.DEFINED, this));
            populateSelectedTopics(DEFAULT_TOPICS);

            // open the dialog when the user click on the scroll view
            // TODO: tutorial for the user to explain how to open the dialog
            getView().findViewById(R.id.topics_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("__DIALOG__", "Dialog is called");
                    mDialog.show(getActivity().getSupportFragmentManager(), "TOPICS");
                }
            });
        }
    }

    // __FRIDAY_9112015__

    /**
     * Fill the horizontal scroll view with the selected buttons
     * @param ids IDs of the selected topics
     */
    private void populateSelectedTopics(Set<Integer> ids) {

        // Instantiate the dialog
        mDialog = new TopicsDialog();

        Bundle args = new Bundle(); // put the list of topics for the dialog creation
        args.putParcelableArrayList(Constants.STATE_TOPIC, mTopics);
        mDialog.setArguments(args);

        if (getView() != null) {
            // Horizontal linear layout where we populate the buttons
            LinearLayout layout = (LinearLayout) getView().findViewById(R.id.topics_container);

            // Iteration over all the topics
            for (Topic topic : mTopics) {
                // We add the button only if the set contains the id
                if (ids.contains(topic.getID())) {
                    Button button = new Button(getContext());
                    button.setText(topic.getTitle());
                    button.setClickable(false);
                    button.setFocusable(false);

                    layout.addView(button);
                }
            }
        }
    }
}
