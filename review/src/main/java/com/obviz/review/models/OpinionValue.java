package com.obviz.review.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.obviz.review.managers.TopicsManager;

import java.io.Serializable;

/**
 * Created by gaylor on 08/19/2015.
 *
 */
public class OpinionValue implements Serializable, Parcelable, Comparable<OpinionValue> {
    private static final long serialVersionUID = -100817616366373955L;

    public int nbPositiveOpinions;
    public int nbNegativeOpinions;
    public int topicID;
    public int totalReviews;
    public Double generalOpinionValue;

    public OpinionValue(Parcel parcel) {
        nbPositiveOpinions = parcel.readInt();
        nbNegativeOpinions = parcel.readInt();
        topicID = parcel.readInt();
        totalReviews = parcel.readInt();
        generalOpinionValue = parcel.readDouble();
    }

    public int percentage() {
        return (int) Math.floor(100 * generalOpinionValue);
    }

    public int getTotal() {
        return nbNegativeOpinions + nbPositiveOpinions;
    }

    public boolean isValid() {

        return topicID > 0 && generalOpinionValue != null;
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
        parcel.writeDouble(generalOpinionValue);
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
