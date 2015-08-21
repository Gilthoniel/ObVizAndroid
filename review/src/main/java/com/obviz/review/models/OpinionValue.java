package com.obviz.review.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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

    public OpinionValue(Parcel parcel) {
        nbPositiveOpinions = parcel.readInt();
        nbNegativeOpinions = parcel.readInt();
        topicID = parcel.readInt();
    }

    public int percentage() {
        if (nbPositiveOpinions <= 0 && nbNegativeOpinions <= 0) {
            return 0;
        }

        int percent = Math.round(nbPositiveOpinions * 100 / (nbNegativeOpinions + nbPositiveOpinions));

        if (percent == 0) {
            return 1;
        } else {
            return percent;
        }
    }

    public int getTotal() {
        return nbNegativeOpinions + nbPositiveOpinions;
    }

    public boolean isValid() {

        return topicID > 0 && nbNegativeOpinions > 0 && nbPositiveOpinions > 0;
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
