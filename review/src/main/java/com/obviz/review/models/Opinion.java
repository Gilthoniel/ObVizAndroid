package com.obviz.review.models;

import com.google.gson.annotations.SerializedName;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Opinion {

    public enum Polarity {
        @SerializedName("negative")
        NEGATIVE,
        @SerializedName("positive")
        POSITIVE
    }

    private int id;
    private Polarity polarity;
    private boolean negation;
    private String polarityWord;
    private String aspect;
    private int polWordPosition;
    private int aspectPosition;
    private String phrase;
    private int sentenceId;
    private int polarityWordClauseId;
    private int aspectClauseId;

    public Polarity getPolarity() {
        return polarity;
    }

    public int getAspectID() {
        return aspectClauseId;
    }

    public int getPolarityID() {
        return polarityWordClauseId;
    }

    public int getSentenceID() {
        return sentenceId;
    }

    public String getWord() {
        return polarityWord;
    }

    public String getAspect() {
        return aspect;
    }

    public int getColor() {
        return polarity.name().toLowerCase().equals("positive") ? ChartUtils.COLOR_GREEN : ChartUtils.COLOR_RED;
    }
}
