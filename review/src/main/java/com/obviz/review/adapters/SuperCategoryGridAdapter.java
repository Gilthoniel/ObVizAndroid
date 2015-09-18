package com.obviz.review.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.obviz.review.models.Review;
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
public class SuperCategoryGridAdapter extends GridAdapter<CategoryBase> implements  ImageObserver, CategoryManager.CategoryObserver {

    private Map<String,Bitmap> mImages;

    public SuperCategoryGridAdapter() {
        mImages = new HashMap<>();
    }

    public void getChildCategories(CategoryBase categoryBase){
        //relaunch the adapter with the new contents
        for (Category category : categoryBase.getCategories()) {
            super.add(category);
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

            //then we gotta fill the sub categories:
            if(categoryBase.getCategories().size()>1)
                fillSubCategoryList(categoryBase);
            else{
                RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.subcat_list);
                relativeLayout.setVisibility(View.INVISIBLE);
            }


        }
    }
}
