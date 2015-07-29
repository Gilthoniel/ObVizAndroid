package com.obviz.review.models;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Clause {
    public enum ClauseType { CLAUSE, CONNECTOR, PUNCTUATION, PARAGRAPH }

    private String text;
    private int id;
    private int groupId;
    private ClauseType type;
    private boolean isHidden;

    public int getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    public ClauseType getType() {
        return type;
    }
}
