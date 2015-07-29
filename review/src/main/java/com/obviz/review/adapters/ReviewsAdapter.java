package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.models.Review;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class ReviewsAdapter extends BaseAdapter {

    private List<Review> mReviews;
    private int mTopicID;
    private LayoutInflater mInflater;

    public ReviewsAdapter(Context context, int topicID) {
        mReviews = new ArrayList<>();

        mTopicID = topicID;

        mInflater = LayoutInflater.from(context);
    }

    public void addAll(Collection<Review> collection) {
        for (Review review : collection) {
            if (review.getOpinions().containsKey(mTopicID)) {
                mReviews.add(review);
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mReviews.clear();
        notifyDataSetChanged();
    }

    public void setTopicID(int topicID) {
        mTopicID = topicID;
    }

    @Override
    public int getCount() {
        return mReviews.size();
    }

    @Override
    public Object getItem(int i) {
        return mReviews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view != null) {
            layout = (RelativeLayout) view;
        } else {
            layout = (RelativeLayout) mInflater.inflate(R.layout.reviews_item_row, parent, false);
        }

        Review review = mReviews.get(i);

        TextView body = (TextView) layout.findViewById(R.id.review_content);
        body.setText(review.getBody(mTopicID));

        TextView author = (TextView) layout.findViewById(R.id.author);
        author.setText(review.getAuthorName());

        TextView date = (TextView) layout.findViewById(R.id.date);
        date.setText(review.getDate());

        TextView title = (TextView) layout.findViewById(R.id.title);
        title.setText(review.getTitle());

        RatingBar rating = (RatingBar) layout.findViewById(R.id.rating);
        rating.setRating(review.getScore());

        return layout;
    }
}
