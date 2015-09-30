package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.obviz.review.Constants;
import com.obviz.review.DiscoverAppsActivity;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.SuperCategoryGridAdapter;
import com.obviz.review.models.Category;
import com.obviz.review.models.CategoryBase;
import com.obviz.review.models.Topic;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.reviews.R;



/**
 * Created by gaylor on 05-Aug-15.
 *
 */
public class DiscoverFragment extends Fragment implements HomeFragment {
    private SuperCategoryGridAdapter mDiscoverAdapter;


    public DiscoverFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {
        //Log.d("Pointer", "We are in the DiscoverFragment onCreateView");
        return inflater.inflate(R.layout.fragment_discover, parent, false);
    }

    @Override
    //TODO: @Gaylor the tutorial is just copied here. Must add relevant content - which?
    public void showTutorial() {}

    @Override
    public void refresh() {

        if (mDiscoverAdapter != null) {
            mDiscoverAdapter.onCategoriesLoaded();
        }
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
            // Grid adapter:
            final GridRecyclerView grid = (GridRecyclerView) getView().findViewById(R.id.grid_view);

            mDiscoverAdapter = new SuperCategoryGridAdapter();

            //final AppBoxAdapter trendingAdapter = new AppBoxAdapter();

            mDiscoverAdapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    CategoryBase cat = mDiscoverAdapter.getItem(position);

                    if (cat.getCategories().size() > 1) {

                        mDiscoverAdapter.clear();
                        //mDiscoverAdapter.getChildCategories(cat, mDialog.getSelectedTopicIds());
                        mDiscoverAdapter.getChildCategories(cat);

                    } else {

                        Intent intent = new Intent(getContext(), DiscoverAppsActivity.class);
                        intent.putExtra(Constants.INTENT_CATEGORY, (Parcelable) cat);
                        startActivity(intent);

                    }
                }
            });

            grid.setAdapter(mDiscoverAdapter);

        }
    }

}
