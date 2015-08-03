package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.models.HistoryItem;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaylor on 8/3/2015.
 *
 */
public class HistoryAdapter extends BaseAdapter {

    private List<HistoryItem> items;
    private Context mContext;

    public HistoryAdapter(Context context) {

        mContext = context;
        items = new ArrayList<>();
    }

    public void add(HistoryItem item) {

        items.add(item);
        notifyDataSetChanged();
    }

    public void clear() {

        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int i) {
        return items.get(i).getQuery();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.history_row_item, parent, false);
        }

        HistoryItem item = items.get(i);

        TextView query = (TextView) layout.findViewById(R.id.query);
        query.setText(item.getQuery());

        TextView date = (TextView) layout.findViewById(R.id.date);
        date.setText(item.getDate());

        return layout;
    }
}
