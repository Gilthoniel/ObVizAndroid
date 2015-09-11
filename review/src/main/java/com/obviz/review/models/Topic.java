package com.obviz.review.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Topic implements Serializable, Parcelable {

    // __FRIDAY_9112015__
    public enum Type { DEFINED, SPECIFIC, GENERAL, CATEGORY }

    private static final long serialVersionUID = 8634619334632273342L;

    private int _id;
    private Type type;
    private String title;
    private String[] keys;
    private boolean useSpecialGauge;
    private double  gaugeThreshold;

    public Topic(Parcel parcel) {
        _id = parcel.readInt();
        type = (Type) parcel.readSerializable();
        title = parcel.readString();
        parcel.readStringArray(keys);
        useSpecialGauge = parcel.readInt() == 1;
        gaugeThreshold = parcel.readDouble();
    }

    public int getID() {
        return _id;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {

        if (title != null) {
            return title;
        } else if (keys != null && keys.length > 0) {

            return keys[0];
        } else {
            return "Unknown";
        }
    }

    public boolean isSpecial() {
        return useSpecialGauge;
    }

    public double getThreshold() {
        return gaugeThreshold;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(_id);
        parcel.writeSerializable(type);
        parcel.writeString(title);
        parcel.writeStringArray(keys);
        parcel.writeInt(useSpecialGauge ? 1 : 0);
        parcel.writeDouble(gaugeThreshold);
    }

    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {

        @Override
        public Topic createFromParcel(Parcel parcel) {
            return new Topic(parcel);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
