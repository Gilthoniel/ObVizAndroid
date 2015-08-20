package com.obviz.review.models;

import java.io.Serializable;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Clause implements Serializable {

    private static final long serialVersionUID = -7592305999775288161L;

    public enum ClauseType { CLAUSE, CONNECTOR, PUNCTUATION, PARAGRAPH }

    private String text;
    private int id;
    private int groupId;
    private ClauseType type;
    private boolean isHidden;

    public int getID() {
        return id;
    }

    public int getGroupID() {
        return groupId;
    }

    public String getText() {
        return text;
    }

    public ClauseType getType() {
        return type;
    }
}
