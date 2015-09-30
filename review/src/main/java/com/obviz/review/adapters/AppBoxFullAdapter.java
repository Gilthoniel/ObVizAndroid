package com.obviz.review.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.Constants;
import com.obviz.review.DiscoverAppsActivity;
import com.obviz.review.json.MessageParser;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.managers.TutorialManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.AndroidFullApp;
import com.obviz.review.models.Category;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.views.InfiniteScrollable;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.*;

/**
 * Created by gaylor on 05-Aug-15.
 * Adapter for AndroidApp in a RecyclerView
 */
public class AppBoxFullAdapter extends GridAdapter<AndroidFullApp> implements TopicsManager.TopicsObserver, ImageObserver, InfiniteScrollable {

    private Map<String,Bitmap> mImages;
    private int mPage;
    private int mMaxPage;
    private Category mCategory;
    private List<Integer> mTopicIDs;





    public AppBoxFullAdapter() {
        mPage = 0;
        mMaxPage = 9;
        mImages = new HashMap<>();
        mTopicIDs = new ArrayList<>();
    }


    public void setCategory(Category c){
        mCategory=c;
    }




    /**
     * Maximum number of page to display with the infinite scroller
     * WARNING : It's the webservice which take care of that in most case
     * @param value new maximum
     */
    public void setMaxPage(int value) {
        mMaxPage = value;
    }

    /**
     * Create the item view for the child or let the superclass return the good one
     * @param parent Parent of the Grid
     * @param viewType Type of the item which must be created
     * @return the child
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_rev_box, parent, false);
            return new AppFullHolder(v);
        } else {

            return super.onCreateViewHolder(parent, viewType);
        }
    }

    /**
     * Update the view when the Topics are loaded
     */
    @Override
    public void onTopicsLoaded() {

        notifyDataSetChanged();
    }

    /**
     * Update the view when images are loaded
     * @param url of the image
     * @param bitmap the image itself
     */
    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImages.put(url, bitmap); // fill the map
        notifyDataSetChanged(); // update the view
    }


    /**
     * Implementation of the InfiniteScrollbar
     * Occurred when the user is at the end of the scrollbar
     */
    @Override
    public void onLoadMore() {
        if (mPage < mMaxPage) {
            // Load and update the page if we aren't at the last page
            Log.d("Getting apps", "");
            GeneralWebService.instance().getApps(mCategory, mPage, Constants.NUMBER_APPS_PER_BLOCK, this, mTopicIDs);

            mPage++;
        }
    }



    /**
     * Child of the adapter
     */
    public class AppFullHolder extends GridAdapter<AndroidFullApp>.ViewHolder {

        private View mView;

        public AppFullHolder(View v) {
            super(v);
            mView = v;
        }

        /**
         * Populate an AndroidApp item of a RecyclerView
         * @param fullApp AndroidFullApp item for this child
         */
        @Override
        public void onPopulate(AndroidFullApp fullApp) {
            AndroidApp app = fullApp.getApp();
            TextView name = (TextView) mView.findViewById(R.id.app_name);
            name.setText(app.getName());

            ImageView logo = (ImageView) mView.findViewById(R.id.app_logo);
            if (mImages.containsKey(app.getLogo())) {
                logo.setImageBitmap(mImages.get(app.getLogo()));
            } else {
                mImages.put(app.getLogo(), null);
                ImagesManager.instance().get(app.getLogo(), AppBoxFullAdapter.this);
            }

            //best and worst opinions have to be among the topics we have selected, not all of them.

            TextView bestOpinion = (TextView) mView.findViewById(R.id.mostOpinion);

            SpannableStringBuilder bestTitle = fullApp.getPolarizedOpinion(mTopicIDs, "positive");
            if(bestTitle.length()>0)
                bestOpinion.setText(bestTitle);
            else
                bestOpinion.setVisibility(View.GONE);


            TextView worstOpinion = (TextView) mView.findViewById(R.id.worstOpinion);

            SpannableStringBuilder worstTitle = fullApp.getPolarizedOpinion(mTopicIDs, "negative");
            if(worstTitle.length()>0)
                worstOpinion.setText(worstTitle);
            else
                worstOpinion.setVisibility(View.GONE);


            // Fill the gauges with selected topics
            LinearLayout gauges = (LinearLayout) mView.findViewById(R.id.gauges_container);
            gauges.removeAllViews();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mView.getContext());
            Set<Integer> topics = MessageParser.fromJson(prefs.getString(Constants.PREFERENCES_SELECTED_TOPICS, "[]"),
                    new TypeToken<Set<Integer>>(){}.getType());

            if (topics.size() < 4) {
                createGaugeChart(gauges, app.getGlobalOpinion(), "Global");
            }

            for (Integer topicID : topics) {
                if(app.getOpinion(topicID)!=null)
                    createGaugeChart(gauges, app.getOpinion(topicID).percentage(),
                        TopicsManager.instance().getTitle(topicID, AppBoxFullAdapter.this));
            }

            if (position == 0) {
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        //tutorial:
                        TutorialManager.single((Activity) mView.getContext())
                                .setTarget(mView.findViewById(R.id.app_logo))
                                .setContentText(R.string.tutorial_fullapp_opinions)
                                .singleUse(Constants.TUTORIAL_FULLAPP_KEY)
                                .setDelay(500)
                                .show();
                    }
                });
            }


        }
    }

    public void setTopics(ArrayList<Integer> topicIDs){
       //if the new topics are actually new:
        mTopicIDs=topicIDs;

        getNewApps();
    }

    public void getNewApps(){

        Log.d("AppBox FULL ADAPTER ","Getting new apps for the new topic selection");
        this.clear();
        mPage=0;
        onLoadMore();

        notifyDataSetChanged();
    }

    private void createGaugeChart(LinearLayout layout, int value, String title) {
        GaugeChart chart = new GaugeChart(layout.getContext(), 210);
        GaugeChart.GaugeChartData data = new GaugeChart.GaugeChartData(100);
        data.addSegments(Constants.CHART_SEGMENTS);
        data.setText(title);
        data.setTextSize(10);

        GaugeChart.Arrow arrow = new GaugeChart.Arrow();
        arrow.setValue(value);
        arrow.setHeight(1.0f);
        arrow.setInnerRadius(0.2f);
        arrow.setBaseLength(3);
        arrow.setColor(0xff393939);
        data.addArrow(arrow);

        int size = (int) (layout.getContext().getResources().getDisplayMetrics().scaledDensity * 50);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.weight = 1.0f;
        chart.setLayoutParams(params);
        chart.setPadding(5, 5, 5, 5);

        chart.setData(data);
        layout.addView(chart);
    }

}
