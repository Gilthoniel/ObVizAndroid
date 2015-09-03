package com.obviz.review.models;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import com.obviz.reviews.R;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Review implements Serializable {

    private static final long serialVersionUID = -5938468708180459968L;

    public ID _id;
    public String permalink;
    public String reviewBody;
    public String reviewTitle;
    public int starRatings;
    public Date reviewDate;
    public String authorName;
    public String authorUrl;
    public boolean parsed;
    public boolean isQuestionable;
    public List<Sentence> parsedBody;
    public List<Sentence> parsedTitle;
    public Opinion opinions;

    private SpannableStringBuilder parsedBodyContent;
    private SpannableStringBuilder parsedTitleContent;

    public String getID() {
        return _id.getValue();
    }

    public SpannableStringBuilder getTitle() {

        if (parsedTitleContent != null) {

            return parsedTitleContent;
        }

        if (opinions != null && parsedTitle != null) {

            return (parsedTitleContent = parseContent(parsedTitle, true));
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (reviewTitle != null) {
                builder.append(reviewTitle);
            }

            return builder;
        }
    }

    public SpannableStringBuilder getContent() {

        if (parsedBodyContent != null) {
            return parsedBodyContent;
        }

        if (opinions != null && parsedBody != null) {

            return (parsedBodyContent = parseContent(parsedBody, false));

        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (reviewBody != null) {
                builder.append(reviewBody);
            }

            return builder;
        }
    }

    public int getDisplayType() {

        if (reviewBody != null && reviewBody.length() > 0 && reviewTitle != null && reviewTitle.length() > 0) {

            return 0;
        } else if (reviewBody != null && reviewBody.length() > 0) {

            return 1;
        } else {

            return 2;
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

    private SpannableStringBuilder parseContent(List<Sentence> sentences, boolean isInTitle) {

        // Sort the opinions by sentence and clause IDs
        Opinion.ParsedOpinion parsedOpinion = new Opinion.ParsedOpinion();
        parsedOpinion.put(opinions, isInTitle);

        // Build the body or the title sentence by sentence
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (Sentence sentence : sentences) {

            Iterator<Clause> it = sentence.getChildren().iterator();
            while (it.hasNext()) {
                Clause clause = it.next();

                if (clause.getType() == Clause.ClauseType.PARAGRAPH) {

                    if (it.hasNext()) {
                        builder.append("\n");
                    }

                } else {

                    List<Opinion.OpinionDetail> details = parsedOpinion.get(sentence.getID(), clause.getGroupID());
                    if (details.isEmpty()) {
                        // If there's no opinions, we check if there's a global for the entire sentence
                        details = parsedOpinion.get(sentence.getID(), 0);
                    }

                    SpannableString content = new SpannableString(clause.getText());
                    if (details.size() > 0) {
                        Opinion.OpinionDetail detail = details.get(0);

                        for (String word : detail.getWords()) {
                            boldify(content, word);
                        }

                        content.setSpan(new ForegroundColorSpan(detail.getColor()), 0, content.length(), 0);
                        content.setSpan(new AbsoluteSizeSpan(14, true), 0, content.length(), 0);
                    } else if (isInTitle) {

                        content.setSpan(new AbsoluteSizeSpan(16, true), 0, content.length(), 0);
                    }

                    builder.append(content);
                }
            }
        }

        return builder;
    }

    private void boldify(SpannableString content, String word) {
        final StyleSpan bold = new StyleSpan(Typeface.BOLD);
        final int index = content.toString().toLowerCase().indexOf(word.toLowerCase());

        if (index >= 0) {
            content.setSpan(bold, index, index + word.length(), 0);
        }
    }

    public class Pager implements Serializable {

        private static final long serialVersionUID = 2891725959638437593L;
        public List<Review> reviews;
        public int nbTotalPages;
    }
}
