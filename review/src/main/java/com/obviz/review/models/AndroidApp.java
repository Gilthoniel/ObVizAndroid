package com.obviz.review.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.obviz.review.Constants;
import com.obviz.review.managers.TopicsManager;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gaylor on 26.06.15.
 * Represent an application of the Google Play Store
 */
public class AndroidApp implements Parcelable, Serializable {

    private static final long serialVersionUID = 350181469147709951L;

    private String coverImgUrl;
    private String appID;
    private String name;
    private Score score;
    private String developer;
    private boolean isFree;
    private String minimumOSVersion;
    private String installations;
    private Date publicationDate;
    private Constants.Category category;
    private List<String> relatedIDs;
    private Bundle topics;

    public AndroidApp(Parcel parcel) {
        coverImgUrl = parcel.readString();
        appID = parcel.readString();
        name = parcel.readString();
        score = parcel.readParcelable(Score.class.getClassLoader());
        developer = parcel.readString();
        isFree = parcel.readInt() == 1;
        minimumOSVersion = parcel.readString();
        installations = parcel.readString();
        publicationDate = parcel.readParcelable(Date.class.getClassLoader());
        category = Constants.Category.fromName(parcel.readString());
        relatedIDs = new ArrayList<>();
        parcel.readStringList(relatedIDs);
        topics = parcel.readBundle();
    }

    public String getAppID() {
        return appID;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getImage() {
        return coverImgUrl;
    }

    public String getName() {
        return name;
    }

    public Score getScore(){
        return score;
    }

    public boolean isFree() {
        return isFree;
    }

    public String getInstallations() {
        return installations;
    }

    public String getDate() {
        return publicationDate.toString();
    }

    public Constants.Category getCategory() {
        if (category == null) {
            category = Constants.Category.DEFAULT;
        }

        return category;
    }

    public List<String> getRelatedIDs() {
        if (relatedIDs != null) {
            return relatedIDs;
        } else {
            return new ArrayList<>();
        }
    }

    public Bundle getTopics() {
        if (topics == null) {
            // TODO
            topics = new Bundle();
            Random random = new Random();
            for (Integer id : TopicsManager.instance().getIDs()) {
                topics.putInt(String.valueOf(id), random.nextInt(100));
            }
        }

        return topics;
    }

    /**
     * Parcelable implementation
     * @return integer
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Convert the object into a parcelable form
     * @param parcel Parcel with data
     * @param flags parcelable flags
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(coverImgUrl);
        parcel.writeString(appID);
        parcel.writeString(name);
        parcel.writeParcelable(score, -1);
        parcel.writeString(developer);
        parcel.writeInt(isFree ? 1 : 0);
        parcel.writeString(minimumOSVersion);
        parcel.writeString(installations);
        parcel.writeParcelable(publicationDate, -1);
        parcel.writeString(category.name());
        parcel.writeStringList(relatedIDs);
        parcel.writeBundle(topics);
    }

    /**
     * Create an Android Application from a parcel
     */
    public static final Parcelable.Creator<AndroidApp> CREATOR = new Parcelable.Creator<AndroidApp>() {

        @Override
        public AndroidApp createFromParcel(Parcel parcel) {
            return new AndroidApp(parcel);
        }

        @Override
        public AndroidApp[] newArray(int size) {
            return new AndroidApp[size];
        }
    };
}
