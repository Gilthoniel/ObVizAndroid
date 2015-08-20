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
    private LayoutInflater mInflater;

    public ReviewsAdapter(Context context) {
        mReviews = new ArrayList<>();

        mInflater = LayoutInflater.from(context);
    }

    public void addAll(Collection<Review> collection) {

        mReviews.addAll(collection);
        notifyDataSetChanged();
    }

    public void clear() {
        mReviews.clear();
        notifyDataSetChanged();
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
        switch (review.getDisplayType()) {
            case 0:
                body.setText(review.getTitle().append("\n").append(review.getContent()));
                break;
            case 1:
                body.setText(review.getContent());
                break;
            default:
                body.setText(review.getTitle());
                break;
        }

        TextView author = (TextView) layout.findViewById(R.id.author);
        author.setText(review.authorName.trim());

        TextView date = (TextView) layout.findViewById(R.id.date);
        date.setText(review.reviewDate.toString());

        RatingBar rating = (RatingBar) layout.findViewById(R.id.rating);
        rating.setRating(review.starRatings);

        return layout;
    }
}
