package com.obviz.review.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import com.obviz.review.managers.TopicsManager;

import java.io.Serializable;

/**
 * Created by gaylor on 08/19/2015.
 *
 */
public class OpinionValue implements Serializable, Parcelable, Comparable<OpinionValue>, TopicsManager.TopicsObserver {
    private static final long serialVersionUID = -100817616366373955L;

    public int nbPositiveOpinions;
    public int nbNegativeOpinions;
    public int topicID;
    public int percent = -1;
    public int totalReviews;

    public OpinionValue(Parcel parcel) {
        nbPositiveOpinions = parcel.readInt();
        nbNegativeOpinions = parcel.readInt();
        topicID = parcel.readInt();
        totalReviews = parcel.readInt();
    }

    public int percentage(boolean firstTry) {
        if (nbPositiveOpinions <= 0 && nbNegativeOpinions <= 0) {
            return 0;
        }

        if (percent < 0 && firstTry) {
            Topic topic = TopicsManager.instance().getTopic(topicID, this);
            if (topic != null && topic.isSpecial()) {
                percent = (int) (100 * (totalReviews - ((nbNegativeOpinions - nbPositiveOpinions) / topic.getThreshold())) / totalReviews);
                percent = Math.min(100, Math.max(0, percent));
            } else {

                int temp = Math.round(nbPositiveOpinions * 100 / (nbNegativeOpinions + nbPositiveOpinions));
                if (topic == null) {
                    // Invalidate if we couldn't get the topic
                    percent = -1;
                } else {
                    percent = temp;
                }

                return temp;
            }
        }

        return Math.max(0, percent);
    }

    public int percentage() {
        return percentage(true);
    }

    public int getTotal() {
        return nbNegativeOpinions + nbPositiveOpinions;
    }

    public boolean isValid() {

        return topicID > 0 && (nbNegativeOpinions > 0 || nbPositiveOpinions > 0);
    }

    @Override
    public void onTopicsLoaded() {
        percentage(false);
    }

    @Override
    public int compareTo(@NonNull OpinionValue other) {

        if (getTotal() < other.getTotal()) {
            return -1;
        } else if (getTotal() == other.getTotal()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(nbPositiveOpinions);
        parcel.writeInt(nbNegativeOpinions);
        parcel.writeInt(topicID);
        parcel.writeInt(totalReviews);
    }

    /**
     * Create an Android Application from a parcel
     */
    public static final Parcelable.Creator<OpinionValue> CREATOR = new Parcelable.Creator<OpinionValue>() {

        @Override
        public OpinionValue createFromParcel(Parcel parcel) {
            return new OpinionValue(parcel);
        }

        @Override
        public OpinionValue[] newArray(int size) {
            return new OpinionValue[size];
        }
    };
}
