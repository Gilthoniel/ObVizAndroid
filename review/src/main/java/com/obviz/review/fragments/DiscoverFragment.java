package com.obviz.review.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.obviz.review.Constants;
import com.obviz.review.DiscoverAppsActivity;
import com.obviz.review.ReviewsActivity;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.SuperCategoryGridAdapter;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.managers.TutorialManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.Category;
import com.obviz.review.models.CategoryBase;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by gaylor on 05-Aug-15.
 *
 */
public class DiscoverFragment extends Fragment implements HomeFragment {

    private boolean mTutoRequested = false;
    private Context mContext;
    SuperCategoryGridAdapter mDiscoverAdapter;
    List<Topic> mTopics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {
        //Log.d("Pointer", "We are in the DiscoverFragment onCreateView");
        return inflater.inflate(R.layout.fragment_discover, parent, false);
    }

    @Override
    //TODO: the tutorial is just copied here. Must add relevant content
    public void showTutorial() {
        if (getView() == null) {
            mTutoRequested = true;
            return;
        } else {
            mTutoRequested = false;
        }

        /*TutorialManager.single((Activity) mContext)
                .setTarget(getView().findViewById(R.id.spinner))
                .setContentText(getResources().getString(R.string.tutorial_home_content))
                .singleUse(Constants.TUTORIAL_HOME_KEY)
                .show();*/
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


            //get available topics
            TopicsManager.instance().getTopics("DEFINED");

            //horizontal scroll
            for
            Button btnTopic = new Button(getContext());
            btnTopic.setText("");


        }
    }
}
