package com.obviz.review.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Sentence implements Serializable {

    private static final long serialVersionUID = -8015591359725475521L;

    private List<Clause> children;
    private boolean isSpecialSentence;
    private boolean isHidden;
    private int id;

    public List<Clause> getChildren() {
        return children;
    }

    public String getGroupText(int clauseId){
        StringBuilder builder = new StringBuilder();

        for(Clause c: children)
            if(c.getGroupID()==clauseId || clauseId==0)
                builder.append(c.getText());


        return builder.toString().trim();
    }

    public boolean isHidden() {
        return isHidden;
    }

    public int getID() {
        return id;
    }
}
