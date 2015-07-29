package com.obviz.review.adapters;

import android.accounts.Account;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.obviz.review.Constants;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 21.07.15.
 * Adapter for the drawer menu
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private Context mContext;

    public DrawerAdapter(Context context) {

        mContext = context;
    }

    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_row, parent, false);
            return new ViewHolder(v, viewType);
        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            return new ViewHolder(v, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        if (holder.getHolderID() == TYPE_ITEM) {

            holder.textView.setText(Constants.DRAWER_TITLES[position - 1]);
            holder.imageView.setImageResource(Constants.DRAWER_ICONS[position - 1]);
        } else {


        }
    }

    @Override
    public int getItemCount() {
        return Constants.DRAWER_TITLES.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {

            return TYPE_HEADER;
        } else {

            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int holderID;

        private TextView textView;
        private ImageView imageView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            if (viewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.item_text);
                imageView = (ImageView) itemView.findViewById(R.id.item_image);
            } else {

            }

            holderID = viewType;
        }

        public int getHolderID() {
            return holderID;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(imageView.getContext(), "Test", Toast.LENGTH_LONG).show();
        }
    }
}
