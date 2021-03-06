package com.obviz.review.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.json.MessageParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String price;
    private String minimumOSVersion;
    private String installations;
    private Date publicationDate;
    private String category;
    private String description;
    private List<String> alternativeApps;
    private List<OpinionValue> opinionsSummary;
    private int nbParsedReviews;
    private double overallOpinionValue;

    private AndroidApp() {}

    public AndroidApp(Parcel parcel) {
        coverImgUrl = parcel.readString();
        appID = parcel.readString();
        name = parcel.readString();
        score = parcel.readParcelable(Score.class.getClassLoader());
        developer = parcel.readString();
        isFree = parcel.readInt() == 1;
        price = parcel.readString();
        minimumOSVersion = parcel.readString();
        installations = parcel.readString();
        publicationDate = parcel.readParcelable(Date.class.getClassLoader());
        category = parcel.readString();
        description = parcel.readString();
        alternativeApps = new ArrayList<>();
        parcel.readStringList(alternativeApps);
        opinionsSummary = new ArrayList<>();
        parcel.readTypedList(opinionsSummary, OpinionValue.CREATOR);
        nbParsedReviews = parcel.readInt();
        overallOpinionValue = parcel.readDouble();
    }

    public String getAppID() {
        return appID != null ? appID : "";
    }

    public String getDeveloper() {
        return developer;
    }

    public String getLogo() {
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

    public String getPrice() {
        return price;
    }

    public String getInstallations() {
        return installations;
    }

    public String getDate() {
        return publicationDate.toString();
    }

    public String getCategory() {

        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getDescription(int maxCharacters) {
        if (description.length() > maxCharacters) {
            return description.substring(0, maxCharacters) + " ...";
        } else {
            return description;
        }
    }

    public List<String> getAlternativeApps() {
        if (alternativeApps == null) {
            alternativeApps = new ArrayList<>();
        }

        return alternativeApps;
    }

    public List<OpinionValue> getOpinions() {
        if (opinionsSummary == null) {
            opinionsSummary = new ArrayList<>();
        }

        return opinionsSummary;
    }

    public OpinionValue getOpinion(int topicID) {
        for (OpinionValue opinion : opinionsSummary) {
            if (opinion.topicID == topicID) {
                return opinion;
            }
        }

        return null;
    }

    public int getGlobalOpinion() {

        return (int) Math.floor(overallOpinionValue * 100);
    }

    public int getBestOpinion() {

        if (opinionsSummary != null && opinionsSummary.size() > 0) {

            return opinionsSummary.get(0).topicID;
        } else {

            return -1;
        }
    }

    public int getWorstOpinion() {

        if (opinionsSummary != null && opinionsSummary.size() > 0) {

            return opinionsSummary.get(opinionsSummary.size() - 1).topicID;
        } else {

            return -1;
        }
    }

    public boolean isParsed() {

        return opinionsSummary != null && opinionsSummary.size() > 0;
    }

    public int getNbParsedReviews() {
        return nbParsedReviews;
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
        parcel.writeString(price);
        parcel.writeString(minimumOSVersion);
        parcel.writeString(installations);
        parcel.writeParcelable(publicationDate, -1);
        parcel.writeString(category);
        parcel.writeString(description);
        parcel.writeStringList(alternativeApps);
        parcel.writeTypedList(opinionsSummary);
        parcel.writeInt(nbParsedReviews);
        parcel.writeDouble(overallOpinionValue);
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

    public static AndroidApp fromJson(JsonObject object) {

        AndroidApp app = new AndroidApp();
        app.coverImgUrl = object.get("coverImgUrl").getAsString();
        app.appID = object.get("appID").getAsString();
        app.name = object.get("name") != null ? object.get("name").getAsString() : "Unknown";
        app.score = MessageParser.fromJson(object.get("score"), Score.class);
        app.developer = object.get("developer").getAsString();
        app.isFree = object.get("isFree").getAsBoolean();
        app.minimumOSVersion = object.get("minimumOSVersion").getAsString();
        app.installations = object.get("installations").getAsString();
        app.publicationDate = MessageParser.fromJson(object.get("publicationDate"), Date.class);
        app.category = object.get("category").getAsString();
        app.description = object.get("description").getAsString();
        if (object.has("alternativeApps")) {
            app.alternativeApps = MessageParser.fromJson(object.get("alternativeApps"), new TypeToken<List<String>>() {
            }.getType());
        } else if (object.has("relatedUrls")) {
            app.alternativeApps = MessageParser.fromJson(object.get("relatedUrls"), new TypeToken<List<String>>() {}.getType());
        }
        app.opinionsSummary = MessageParser.fromJson(object.get("opinionsSummary"), new TypeToken<List<OpinionValue>>(){}.getType());

        if (object.has("nbParsedReviews")) {
            app.nbParsedReviews = object.get("nbParsedReviews").getAsInt();
        } else {
            app.nbParsedReviews = 0;
        }
        app.overallOpinionValue = object.has("overallOpinionValue") ? object.get("overallOpinionValue").getAsDouble() : 0.0;

        return app;
    }

    public class Pager implements Serializable {
        public List<AndroidApp> apps;
        public int nbTotalPages;
    }
}
