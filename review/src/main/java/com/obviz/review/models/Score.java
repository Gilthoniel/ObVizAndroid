package com.obviz.review.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaylor on 21.07.15.
 * Score of an app in stars
 */
public class Score implements Parcelable {

    private int count;
    private int one;
    private int two;
    private int three;
    private int four;
    private int five;
    private float total;

    public Score(Parcel parcel) {
        count = parcel.readInt();
        one = parcel.readInt();
        two = parcel.readInt();
        three = parcel.readInt();
        four = parcel.readInt();
        five = parcel.readInt();

        total = parcel.readFloat();
    }

    public float getTotal() {
        return total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(count);
        parcel.writeInt(one);
        parcel.writeInt(two);
        parcel.writeInt(three);
        parcel.writeInt(four);
        parcel.writeInt(five);
        parcel.writeFloat(total);
    }

    public static final Parcelable.Creator<Score> CREATOR = new Parcelable.Creator<Score>() {

        @Override
        public Score createFromParcel(Parcel parcel) {
            return new Score(parcel);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };
}
