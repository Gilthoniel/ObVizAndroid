package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 21.07.15.
 * Adapter for the drawer menu
 */
public class DrawerAdapter extends BaseAdapter {

    public static final String[] TITLES = new String[] {
            "Trending", "My Applications", "Search History"
    };
    public static final int[] ICONS = new int[] {
            R.drawable.drawer_trending,
            R.drawable.drawer_apps,
            R.drawable.drawer_history
    };

    private Context mContext;

    public DrawerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Object getItem(int i) {
        return TITLES[i];
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
            layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.drawer_item_row, parent, false);
        }

        TextView title = (TextView) layout.findViewById(R.id.drawer_item_text);
        title.setText(TITLES[position]);

        ImageView icon = (ImageView) layout.findViewById(R.id.drawer_item_image);
        icon.setImageResource(ICONS[position]);

        return layout;
    }
}
