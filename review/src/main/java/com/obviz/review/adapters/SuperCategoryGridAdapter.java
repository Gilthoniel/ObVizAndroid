package com.obviz.review.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.obviz.review.dialogs.TopicsDialog;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.AndroidFullApp;
import com.obviz.review.models.Category;
import com.obviz.review.models.CategoryBase;
import com.obviz.review.models.SuperCategory;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.tasks.HttpTask;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by gaylor on 08/31/2015.
 * Adapter for the super categories
 */
public class SuperCategoryGridAdapter extends GridAdapter<CategoryBase> implements TopicsManager.TopicsObserver, ImageObserver, CategoryManager.CategoryObserver {

    private Map<String,Bitmap> mImages;
    private Map<CategoryBase, List<AndroidFullApp>> mAppsBest;
    private Map<CategoryBase, List<AndroidFullApp>> mAppsWorst;
    private Map<CategoryBase, HttpTask<?>> requestsBest;
    private Map<CategoryBase, HttpTask<?>> requestsWorst;
    private TopicsDialog mDialog;

    //private List<Integer> mTopicIds;

    public List<AndroidFullApp> getBestApps(CategoryBase categoryBase){
        return mAppsBest.get(categoryBase);
    }

    public List<AndroidFullApp> getWorstApps(CategoryBase categoryBase){
        return mAppsWorst.get(categoryBase);
    }

    public void setTopicsDialog(TopicsDialog topicsDialog){mDialog=topicsDialog;}

    //public void setTopics(List<Integer> topicIds){mTopicIds=topicIds;}

    public SuperCategoryGridAdapter() {
        requestsBest = new HashMap<>();
        requestsWorst = new HashMap<>();
        mAppsBest = new HashMap<>();
        mAppsWorst = new HashMap<>();
        mImages = new HashMap<>();
        //mTopicIds = new ArrayList<>();
    }

    public void addAlltoMap(List<AndroidFullApp> appList, CategoryBase category, String appType){
        if(appType.equals("best"))
            this.mAppsBest.put(category,appList);


        if(appType.equals("worst"))
            this.mAppsWorst.put(category, appList);

        notifyDataSetChanged();
    }


    private void launchRequests(Map<CategoryBase, HttpTask<?>> requests, CategoryBase category, String requestType, List<Integer> topicIds){
        HttpTask<?> request=null;
        if(requests.containsKey(category)){
            request = requests.get(category);
        }
        // If THIS category's request is already launched, we kill it before relaunching
        if (request != null) {
            request.cancel();
        }

        request = GeneralWebService.instance().getTopApps(this, category, requestType, topicIds);
    }

    public void getChildCategories(CategoryBase categoryBase, List<Integer> topicIds){
        //relaunch the adapter with the new contents
        for (Category category : categoryBase.getCategories()) {
            super.add(category);
            launchRequests(requestsBest, category, "best", topicIds);
            launchRequests(requestsWorst, category, "worst", topicIds);
        }
        notifyDataSetChanged();
    }


    //this is where we actually get the stuff from the Category manager!
    //@Override

    public void onCategoriesLoaded() {
        super.clear();
        List<SuperCategory> categoryList = new ArrayList<>(CategoryManager.instance().getSupers(this));
        Collections.sort(categoryList, new Comparator<SuperCategory>() {
            @Override
            public int compare(SuperCategory sc1, SuperCategory sc2) {
                return (sc1.getTitle().compareTo(sc2.getTitle()));
            }
        });

        for (SuperCategory category : categoryList)  {
            if (category.active) {
                super.add(category);

                launchRequests(requestsBest, category, "best", mDialog.getSelectedTopicIds());

                launchRequests(requestsWorst, category, "worst", mDialog.getSelectedTopicIds());

            }
        }


        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_super_cat_box, parent, false);
            return new SuperCategoryHolder(v);
        } else {

            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onTopicsLoaded() {
        notifyDataSetChanged();
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImages.put(url, bitmap);
        notifyDataSetChanged();
    }



    public class SuperCategoryHolder extends GridAdapter<CategoryBase>.ViewHolder {

        private View mView;

        public SuperCategoryHolder(View v) {
            super(v);

            mView = v;
        }

        private void fillAppLogos(CategoryBase categoryBase, int viewId, Map<CategoryBase, List<AndroidFullApp>> mApps){
            LinearLayout bestAppsSet = (LinearLayout) mView.findViewById(viewId);
            bestAppsSet.removeAllViews();
            if(mApps.containsKey(categoryBase)){
                for (AndroidFullApp fullApp: mApps.get(categoryBase)){
                    AndroidApp a = fullApp.getApp();
                    ImageView appLogo = new ImageView(mView.getContext());
                    appLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    appLogo.setPadding(5, 5, 5, 5);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(bestAppsSet.getHeight(), bestAppsSet.getHeight());
                    appLogo.setLayoutParams(params);
                    if(mImages.containsKey(a.getLogo()))
                        appLogo.setImageBitmap(mImages.get(a.getLogo()));
                    else{
                        mImages.put(a.getLogo(), null);
                        ImagesManager.instance().get(a.getLogo(),SuperCategoryGridAdapter.this);
                    }

                    //TODO: tackle case click on each app:
                    //bestAppsSet.setOnClickListener( ... );

                    bestAppsSet.addView(appLogo);
                }

            }


        }

        @Override
        public void onPopulate(CategoryBase categoryBase) {
            TextView name = (TextView) mView.findViewById(R.id.cat_name);
            name.setText(categoryBase.getTitle());
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            ImageView logo = (ImageView) mView.findViewById(R.id.cat_image);
            if (mImages.containsKey(categoryBase.getImage(mView.getContext()))) {
                logo.setImageBitmap(mImages.get(categoryBase.getImage(mView.getContext())));
            } else {
                mImages.put(categoryBase.getImage(mView.getContext()), null);
                ImagesManager.instance().get(categoryBase.getImage(mView.getContext()), SuperCategoryGridAdapter.this);
            }

            //if we still have no image -> make invisible
            if(logo.getDrawable() == null) {
                logo.setVisibility(View.INVISIBLE);
            }

            //then we gotta fill in the best and worst performers:

            fillAppLogos(categoryBase, R.id.best_apps, mAppsBest);
            fillAppLogos(categoryBase, R.id.worst_apps, mAppsWorst);

            //pre-fill the horizontal scroll with two topics from:
            // http://ns3369837.ip-37-187-91.eu/ObVizServiceAdmin?cmd=Get_App_Topics&type=DEFINED


        }
    }
}
