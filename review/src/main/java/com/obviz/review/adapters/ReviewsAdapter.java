package com.obviz.review.adapters;

import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.obviz.review.Constants;
import com.obviz.review.models.Review;
import com.obviz.review.views.InfiniteScrollable;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class ReviewsAdapter extends GridAdapter<Review> implements InfiniteScrollable {

    private int mPage;
    private int mMaxPage;
    private String mAppID;
    private int mTopicID;

    public ReviewsAdapter(String appID, int topicID) {
        mPage = 0;
        mMaxPage = 1;

        mAppID = appID;
        mTopicID = topicID;
    }

    public void setMaxPage(int value) {
        mMaxPage = value;
    }

    public void setTopicID(int value) {
        mTopicID = value;
    }

    @Override
    public void clear() {
        super.clear();

        mPage = 0;
        mMaxPage = 1;
    }

    @Override
    public void onLoadMore() {
        if (mPage < mMaxPage) {
            GeneralWebService.instance().getReviews(mAppID, mTopicID, mPage, Constants.NUMBER_REVIEW_PER_BLOCK, this);
            mPage++;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            return new ReviewHolder(v);
        } else {

            return super.onCreateViewHolder(parent, viewType);
        }
    }

    public class ReviewHolder extends GridAdapter<Review>.ViewHolder {

        public TextView body;
        public TextView author;
        public TextView date;
        public RatingBar rating;

        public ReviewHolder(View v) {
            super(v);

            body = (TextView) v.findViewById(R.id.review_content);
            author = (TextView) v.findViewById(R.id.author);
            date = (TextView) v.findViewById(R.id.date);
            rating = (RatingBar) v.findViewById(R.id.rating);
        }

        @Override
        public void onPopulate(Review review) {
            switch (review.getDisplayType()) {
                case 0:
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append(review.getTitle()).append(": ").append(review.getContent());
                    body.setText(builder);
                    break;
                case 1:
                    body.setText(review.getContent());
                    break;
                default:
                    body.setText(review.getTitle());
                    break;
            }

            author.setText(review.authorName.trim());

            date.setText(review.reviewDate.toString());

            rating.setRating(review.starRatings);
        }
    }
}
