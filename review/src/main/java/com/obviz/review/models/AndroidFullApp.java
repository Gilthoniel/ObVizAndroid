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
public class AndroidFullApp implements Serializable {

    private static final long serialVersionUID = 35011231237709951L;

    private AndroidApp app;
    private List<Review> mostRelevantReviews;

    public AndroidApp getApp() {return app;}
    public List<Review> getReviews(){return mostRelevantReviews;}

    private AndroidFullApp() {}


    public class Pager implements Serializable {
        public List<AndroidFullApp> apps;
        public int nbTotalPages;
    }

    public String getPolarizedOpinion(List<Integer> topics, String targetPolarity){
        for (Review review:mostRelevantReviews){
            for (Opinion.OpinionDetail opinion: review.opinions.opinions){
                if(opinion.polarity.equals(targetPolarity) && opinion.phrase!=null){
                    //return opinion.phrase;
                    Sentence s = review.getSentence(opinion.sentenceID);
                    if(s!=null){
                        Clause c = s.getClause(opinion.clauseID);
                        if(c!=null){
                            return c.getText();
                        }
                    }
                }
            }
        }
        return "";
    }


}
