package edu.uml.mpqa.SubjectiveLexicon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import edu.uml.mpqa.SubjectiveLexicon.Entry.PartOfSpeech;
import edu.uml.mpqa.SubjectiveLexicon.Entry.Polarity;

public class SubjectiveLexiconFeatureExtractor {

    private Tagger tagger;
    private SubjectiveLexicon subjectiveLexicon;

    private Map<String, PartOfSpeech> arkTweetNLPTagToSubjectiveLexiconTag;

    public SubjectiveLexiconFeatureExtractor(Tagger tagger, SubjectiveLexicon subjectiveLexicon) {
        this.tagger = tagger;
        this.subjectiveLexicon = subjectiveLexicon;

        arkTweetNLPTagToSubjectiveLexiconTag = new HashMap<>();
        arkTweetNLPTagToSubjectiveLexiconTag.put("A", PartOfSpeech.ADJECTIVE);
        arkTweetNLPTagToSubjectiveLexiconTag.put("N", PartOfSpeech.NOUN);
        arkTweetNLPTagToSubjectiveLexiconTag.put("V", PartOfSpeech.VERB);
        arkTweetNLPTagToSubjectiveLexiconTag.put("R", PartOfSpeech.ADVERB);
    }

    public double[] extractFeatures(String s) {
        List<TaggedToken> tagged = tagger.tokenizeAndTag(s);

        double[] features = new double[8];
        
        boolean strongPositive = false;
        boolean strongNegative = false;
        boolean weakPositive = false;
        boolean weakNegative = false;

        boolean strongPositiveAdjective = false;
        boolean strongNegativeAdjective = false;
        boolean weakPositiveAdjective = false;
        boolean weakNegativeAdjective = false;

        for (TaggedToken taggedToken : tagged) {
            PartOfSpeech tag = arkTweetNLPTagToSubjectiveLexiconTag.get(taggedToken.tag);

            String token = taggedToken.token.toLowerCase().replace("#", "");

            for (Entry entry : subjectiveLexicon.getEntries(tag, token)) {

                if (entry.isStrongSubjective()) {

                    if (entry.getPolarity() == Polarity.BOTH) {
                        strongPositive = true;
                        strongNegative = true;
                    } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                        strongNegative = true;
                    } else if (entry.getPolarity() == Polarity.POSITIVE) {
                        strongPositive = true;
                    }

                    if (tag == PartOfSpeech.ADJECTIVE) {
                        if (entry.getPolarity() == Polarity.BOTH) {
                            strongPositiveAdjective = true;
                            strongNegativeAdjective = true;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            strongNegativeAdjective = true;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            strongPositiveAdjective = true;
                        }
                    }

                } else {
                    if (entry.getPolarity() == Polarity.BOTH) {
                        weakPositive = true;
                        weakNegative = true;
                    } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                        weakNegative = true;
                    } else if (entry.getPolarity() == Polarity.POSITIVE) {
                        weakPositive = true;
                    }

                    if (tag == PartOfSpeech.ADJECTIVE) {
                        if (entry.getPolarity() == Polarity.BOTH) {
                            weakPositiveAdjective = true;
                            weakNegativeAdjective = true;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            weakNegativeAdjective = true;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            weakPositiveAdjective = true;
                        }
                    }
                }
            }
        }
        
        features[0] = strongPositive ? 1.0 : 0.0;
        features[0] = strongNegative ? 1.0 : 0.0;
        features[0] = weakPositive ? 1.0 : 0.0;
        features[0] = weakNegative ? 1.0 : 0.0;

        features[0] = strongPositiveAdjective ? 1.0 : 0.0;
        features[0] = strongNegativeAdjective ? 1.0 : 0.0;
        features[0] = weakPositiveAdjective ? 1.0 : 0.0;
        features[0] = weakNegativeAdjective ? 1.0 : 0.0;

        return features;
    }
}
