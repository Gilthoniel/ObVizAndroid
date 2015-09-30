package com.obviz.review.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.Favorite;
import com.obviz.reviews.R;

import java.util.*;

/**
 * Created by gaylor on 08/25/2015.
 * List adapter for the favorite
 */
public class FavoriteAdapter extends BaseAdapter
    implements ImageObserver {

    private Context mContext;
    private List<Favorite> mFavorites;
    private Map<String, Bitmap> mImages;

    public FavoriteAdapter(Context context) {

        mContext = context;
        mImages = new HashMap<>();
        mFavorites = Collections.emptyList();
    }

    public void update() {
        mFavorites = DatabaseService.instance().getFavorites();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFavorites.size();
    }

    @Override
    public Favorite getItem(int position) {
        return mFavorites.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view != null) {
            layout = (RelativeLayout) view;
        } else {
            layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.item_app_list, parent, false);
        }

        Favorite favorite = mFavorites.get(position);

        TextView name = (TextView) layout.findViewById(R.id.app_name);
        name.setText(favorite.name);

        ImageView logo = (ImageView) layout.findViewById(R.id.app_logo);
        if (mImages.containsKey(favorite.logo)) {
            logo.setImageBitmap(mImages.get(favorite.logo));
        } else {
            ImagesManager.instance().get(favorite.logo, this);
        }

        return layout;
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImages.put(url, bitmap);

        notifyDataSetChanged();
    }
}
