package com.obviz.review.models;

import com.obviz.review.Constants;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Opinion implements Serializable {

    private static final long serialVersionUID = 8144194170894120352L;

    enum Type { CLASSIC, WEAK }

    public int topicID;
    public int nbOpinions;
    public int opinionValue;
    public List<OpinionDetail> opinions;

    public class OpinionDetail implements Serializable {

        private static final long serialVersionUID = 7726615802718595835L;
        public String polarity;
        public boolean isNegated;
        public boolean isInTitle;
        public Type type;
        List<String> polarityWords;
        List<String> aspects;
        List<String> otherWords;
        public int sentenceID;
        public int clauseID;
        String phrase;

        public List<String> getWords() {
            List<String> words = new ArrayList<>();
            words.addAll(polarityWords);

            if (aspects != null) {
                words.addAll(aspects);
            }

            if (otherWords != null) {
                words.addAll(otherWords);
            }

            return words;
        }

        public int getColor() {
            return polarity.equals("positive") ? Constants.POSITIVE_COLOR : Constants.NEGATIVE_COLOR;
        }
    }

    public static class ParsedOpinion extends HashMap<Integer, Map<Integer, List<OpinionDetail>>> {

        private static final long serialVersionUID = 2089526101092532249L;

        public void put(Opinion opinions, boolean isInTitle) {
            for (OpinionDetail opinion : opinions.opinions) {

                if (isInTitle == opinion.isInTitle) {

                    if (!containsKey(opinion.sentenceID)) {
                        super.put(opinion.sentenceID, new HashMap<Integer, List<OpinionDetail>>());
                    }

                    Map<Integer, List<OpinionDetail>> details = get(opinion.sentenceID);

                    int clauseID = opinion.clauseID > 0 ? opinion.clauseID : 0;
                    if (!details.containsKey(clauseID)) {
                        details.put(clauseID, new ArrayList<OpinionDetail>());
                    }

                    List<OpinionDetail> detail = details.get(clauseID);
                    detail.add(opinion);
                }
            }
        }

        public List<OpinionDetail> get(int sentenceID, int clauseID) {
            if (containsKey(sentenceID)) {

                Map<Integer, List<OpinionDetail>> details = get(sentenceID);
                if (details.containsKey(clauseID)) {

                    return details.get(clauseID);
                }
            }

            return Collections.emptyList();
        }
    }
}
