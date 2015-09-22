package com.obviz.review.adapters;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.Constants;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.*;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by gaylor on 08/31/2015.
 * Adapter for the super categories
 */
public class SuperCategoryGridAdapter extends GridAdapter<CategoryBase>
        implements  ImageObserver, CategoryManager.CategoryObserver, TopicsManager.TopicsObserver
{

    private Map<String,Bitmap> mImages;
    private Map<CategoryBase, Headline> mHeadlines;

    public SuperCategoryGridAdapter() {
        mImages = new HashMap<>();
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

        private void fillSubCategoryImage(ImageView imageView, String icon){
            if(mImages.containsKey(icon))
                imageView.setImageBitmap(mImages.get(icon));
            else{
                mImages.put(icon, null);
                ImagesManager.instance().get(icon, SuperCategoryGridAdapter.this);
            }

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);

            //older settings
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(bestAppsSet.getHeight(), bestAppsSet.getHeight());
            //appLogo.setLayoutParams(params);
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
            if (mImages.containsKey(categoryBase.getImage(mView.getContext()))) {
                logo.setImageBitmap(mImages.get(categoryBase.getImage(mView.getContext())));
            } else {
                mImages.put(categoryBase.getImage(mView.getContext()), null);
                ImagesManager.instance().get(categoryBase.getImage(mView.getContext()), SuperCategoryGridAdapter.this);
            }

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

                        if (headline.getTopicID() != null) {
                            data.setText(TopicsManager.instance().getTitle(headline.getTopicID(), SuperCategoryGridAdapter.this));
                            arrow.setValue(app.getOpinion(headline.getTopicID()).percentage());
                        } else {
                            data.setText("Global");
                            arrow.setValue(app.getGlobalOpinion());
                        }

                        data.addArrow(arrow);
                        chart.setData(data);

                        ImageView appLogo = (ImageView) mView.findViewById(R.id.app_logo);
                        if (mImages.containsKey(app.getLogo())) {
                            appLogo.setImageBitmap(mImages.get(app.getLogo()));
                        } else {
                            mImages.put(app.getLogo(), null);
                            ImagesManager.instance().get(app.getLogo(), SuperCategoryGridAdapter.this);
                        }
                    }
                } else {
                    mHeadlines.put(categoryBase, null);
                    GeneralWebService.instance().getHeadline(new RequestCallback<Headline>() {
                        @Override
                        public void onSuccess(Headline result) {
                            mHeadlines.put(categoryBase, result);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Errors error) {
                            Log.e("--HEADLINE--", "can't load headline: " + error.name());
                        }

                        @Override
                        public Type getType() {
                            return Headline.class;
                        }
                    });
                }
            }
        }
    }
}
