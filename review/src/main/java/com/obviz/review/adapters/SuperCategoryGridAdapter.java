package com.obviz.review.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.Constants;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.ImageLoader;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.*;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.reviews.R;

import java.util.*;

/**
 * Created by gaylor on 08/31/2015.
 * Adapter for the super categories
 */
public class SuperCategoryGridAdapter extends GridAdapter<CategoryBase>
        implements  CategoryManager.CategoryObserver, TopicsManager.TopicsObserver
{

    private Map<CategoryBase, Headline> mHeadlines;

    public SuperCategoryGridAdapter() {
        mHeadlines = new HashMap<>();
    }

    public void getChildCategories(CategoryBase categoryBase){
        //relaunch the adapter with the new contents
        for (Category category : categoryBase.getCategories()) {
            super.add(category);
        }
        notifyDataSetChanged();
    }


    //this is where we actually get the stuff from the Category manager!
    @Override
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
            }
        }


        notifyDataSetChanged();
    }

    @Override
    public void onTopicsLoaded() {
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

    public class SuperCategoryHolder extends GridAdapter<CategoryBase>.ViewHolder {

        private View mView;

        public SuperCategoryHolder(View v) {
            super(v);

            mView = v;
        }

        private void fillSubCategoryImage(ImageView imageView, String icon){
            ImageLoader.instance().get(icon, imageView);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        }

        private void fillSubCategoryName(TextView textView, String text){
            textView.setText(text);
            textView.setPadding(5, 5, 5, 5);
        }


        private void fillSubCategoryList(CategoryBase categoryBase){
            RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.subcat_list);
            relativeLayout.setVisibility(View.VISIBLE);

            if(categoryBase.getCategories().size()<2) {
                TextView points = (TextView) mView.findViewById(R.id.more_categs);
                points.setVisibility(View.INVISIBLE);
            }

            if(categoryBase.getCategories().size()>0) {
                fillSubCategoryImage((ImageView) mView.findViewById(R.id.subcat_1), categoryBase.getCategories().get(0).getIcon());
                fillSubCategoryName((TextView)mView.findViewById(R.id.cat1_name),categoryBase.getCategories().get(0).getTitle());
            }

            if(categoryBase.getCategories().size()>1) {
                fillSubCategoryImage((ImageView) mView.findViewById(R.id.subcat_2), categoryBase.getCategories().get(1).getIcon());
                fillSubCategoryName((TextView) mView.findViewById(R.id.cat2_name), categoryBase.getCategories().get(1).getTitle());
            }
        }

        @Override
        public void onPopulate(final CategoryBase categoryBase) {
            TextView name = (TextView) mView.findViewById(R.id.cat_name);
            name.setText(categoryBase.getTitle());
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            ImageView logo = (ImageView) mView.findViewById(R.id.cat_image);
            ImageLoader.instance().get(categoryBase.getIcon(), logo);

            //if we still have no image -> make invisible
            if (logo.getDrawable() == null) {
                logo.setVisibility(View.GONE);
            }

            RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.subcat_list);
            relativeLayout.setVisibility(View.GONE);

            RelativeLayout subcatLayout = (RelativeLayout) mView.findViewById(R.id.subcat_body);
            subcatLayout.setVisibility(View.GONE);

            //then we gotta fill the sub categories:
            if (categoryBase.getCategories().size() > 1) {

                relativeLayout.setVisibility(View.VISIBLE);
                fillSubCategoryList(categoryBase);
            } else {

                subcatLayout.setVisibility(View.VISIBLE);
                if (mHeadlines.containsKey(categoryBase)) {

                    Headline headline = mHeadlines.get(categoryBase);
                    if (headline != null) {
                        AndroidApp app = headline.getApps().get(0);

                        // HeadLine text
                        ((TextView) mView.findViewById(R.id.know_title)).setText(headline.getTitle());

                        // HeadLine Gauge
                        GaugeChart chart = (GaugeChart) mView.findViewById(R.id.gauge_chart);
                        GaugeChart.GaugeChartData data = new GaugeChart.GaugeChartData(100);
                        data.addSegments(Constants.CHART_SEGMENTS);
                        data.setTextSize(8);

                        GaugeChart.Arrow arrow = new GaugeChart.Arrow();
                        arrow.setHeight(1.0f);
                        arrow.setInnerRadius(0.4f);
                        arrow.setBaseLength(5);
                        arrow.setColor(mView.getResources().getColor(R.color.appColor));

                        if (headline.getTopicID() != null && app.getOpinion(headline.getTopicID())!=null) {
                        //if (headline.getTopicID() != null ) {
                            data.setText(TopicsManager.instance().getTitle(headline.getTopicID(), SuperCategoryGridAdapter.this));
                            arrow.setValue(app.getOpinion(headline.getTopicID()).percentage());
                        } else {
                            data.setText("Global");
                            arrow.setValue(app.getGlobalOpinion());
                        }

                        data.addArrow(arrow);
                        chart.setData(data);

                        ImageView appLogo = (ImageView) mView.findViewById(R.id.app_logo);
                        ImageLoader.instance().get(app.getLogo(), appLogo);
                    }
                } else {
                    mHeadlines.put(categoryBase, null);
                    GeneralWebService.instance().getHeadline(categoryBase, new RequestCallback<Headline>() {
                        @Override
                        public void onSuccess(Headline result) {
                            mHeadlines.put(categoryBase, result);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Errors error) {
                            Log.e("--HEADLINE--", "can't load headline: " + error.name());
                        }
                    });
                }
            }
        }
    }
}
