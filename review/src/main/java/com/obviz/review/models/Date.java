package com.obviz.review.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by gaylor on 22.07.15.
 *
 */
public class Date implements Parcelable {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

    private long $date;

    public Date(Parcel parcel) {
        $date = parcel.readLong();
    }

    public String toString() {
        return formatter.format(new java.util.Date($date));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong($date);
    }

    public static final Parcelable.Creator<Date> CREATOR = new Parcelable.Creator<Date>() {

        @Override
        public Date createFromParcel(Parcel parcel) {
            return new Date(parcel);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };
}
