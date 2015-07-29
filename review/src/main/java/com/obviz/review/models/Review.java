package com.obviz.review.models;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.*;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Review {

    private ID _id;
    private String permalink;
    private String reviewBody;
    private int starRatings;
    private Date reviewDate;
    private String authorName;
    private String authorUrl;
    private String reviewTitle;
    private List<Sentence> parsed;
    private List<Sentence> parsedBody;
    private List<Sentence> parsedTitle;
    private Map<Integer, List<Opinion>> opinions;

    public Review() {

    }

    public String getID() {
        return _id.getValue();
    }

    public void setID(ID value) {
        _id = value;
    }

    public String getUrl() {
        return permalink;
    }

    public void setPermalink(String value) {
        permalink = value;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getReviewBody() {
        return reviewBody;
    }

    public void setReviewBody(String value) {
        reviewBody = value;
    }

    public String getTitle() {
        return reviewTitle;
    }

    public void setTitle(String value) {
        reviewTitle = value;
    }

    public int getScore() {
        return starRatings;
    }

    public void setScore(int value) {
        starRatings = value;
    }

    public String getDate() {
        return reviewDate.toString();
    }

    public void setDate(Date value) {
        reviewDate = value;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String value) {
        authorName = value;
    }

    public Map<Integer, List<Opinion>> getOpinions() {
        return opinions;
    }

    public void setOpinions(Map<Integer, List<Opinion>> value) {
        opinions = value;
    }

    public void setParsed(List<Sentence> value) {
        parsed = value;
    }

    public void setParsedBody(List<Sentence> value) {
        parsedBody = value;
    }

    public void setParsedTitle(List<Sentence> value) {
        parsedTitle = value;
    }

    public List<Sentence> getBodySentences() {
        if (parsedBody != null) {
            return parsedBody;
        } else if (parsed != null) {
            return parsed;
        } else {
            return new ArrayList<>();
        }
    }

    public List<Sentence> getTitleSentences() {
        if (parsedTitle != null) {
            return parsedTitle;
        } else {
            return new ArrayList<>();
        }
    }

    public SpannableStringBuilder getBody(int topicID) {

        List<Opinion> opinions = this.opinions.get(topicID);
        if (opinions != null) {

            Map<Integer, List<Opinion>> children = new TreeMap<>();
            for (Opinion child : opinions) {
                if (!children.containsKey(child.getSentenceID())) {
                    children.put(child.getSentenceID(), new ArrayList<Opinion>());
                }

                children.get(child.getSentenceID()).add(child);
            }

            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (Sentence sentence : parsed != null ? parsed : parsedBody) {
                boolean hasOpinions = children.containsKey(sentence.getID());

                Iterator<Clause> it = sentence.getChildren().iterator();
                while (it.hasNext()) {
                    Clause clause = it.next();

                    if (clause.getType() == Clause.ClauseType.PARAGRAPH) {
                        // Add paragraph unless it's the last clause
                        if (it.hasNext()) {
                            builder.append("\n\n");
                        }
                    } else {

                        if (hasOpinions) {
                            SpannableString content = new SpannableString(clause.getText());

                            // Search for aspect or opinion word in the clause and set the side color
                            Iterator<Opinion> itOpinions = children.get(sentence.getID()).iterator();
                            while (itOpinions.hasNext()) {
                                Opinion opinion = itOpinions.next();

                                if (!itOpinions.hasNext()) {
                                    // Get the color side of the opinion with the last opinion
                                    content.setSpan(new ForegroundColorSpan(opinion.getColor()), 0, content.length(), 0);
                                }

                                // Set the aspect and opinion word bold in the text
                                if (opinion.getAspectID() == clause.getID()) {
                                    boldify(content, opinion.getAspect());
                                }

                                if (opinion.getPolarityID() == clause.getID()) {
                                    boldify(content, opinion.getWord());
                                }
                            }

                            builder.append(content);
                        } else {

                            builder.append(clause.getText());
                        }
                    }
                }
            }

            return builder;

        } else {
            return new SpannableStringBuilder();
        }
    }

    @Override
    public boolean equals(Object object) {

        return object.getClass() == Review.class && ((Review) object).getID().equals(_id.getValue());
    }

    @Override
    public int hashCode() {

        return getID().hashCode();
    }

    /** PRIVATE **/

    private void boldify(SpannableString content, String word) {
        final StyleSpan bold = new StyleSpan(Typeface.BOLD);
        final int index = content.toString().toLowerCase().indexOf(word.toLowerCase());

        if (index >= 0) {
            content.setSpan(bold, index, index + word.length(), 0);
        }
    }
}
