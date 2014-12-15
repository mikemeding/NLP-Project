package edu.uml.mpqa.SubjectiveLexicon;

import java.util.ArrayList;
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

        ArrayList<Double> featureList = new ArrayList<>();

        int strongPositiveCount = 0;
        int strongNegativeCount = 0;
        int weakPositiveCount = 0;
        int weakNegativeCount = 0;

        int strongPositiveAdjectiveCount = 0;
        int strongNegativeAdjectiveCount = 0;
        int weakPositiveAdjectiveCount = 0;
        int weakNegativeAdjectiveCount = 0;

        for (TaggedToken taggedToken : tagged) {
            PartOfSpeech tag = arkTweetNLPTagToSubjectiveLexiconTag.get(taggedToken.tag);

            String token = taggedToken.token.toLowerCase().replace("#", "");

            for (Entry entry : subjectiveLexicon.getEntries(tag, token)) {

                if (entry.isStrongSubjective()) {

                    if (entry.getPolarity() == Polarity.BOTH) {
                        strongPositiveCount++;
                        strongNegativeCount++;
                    } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                        strongNegativeCount++;
                    } else if (entry.getPolarity() == Polarity.POSITIVE) {
                        strongPositiveCount++;
                    }

                    if (tag == PartOfSpeech.ADJECTIVE) {
                        if (entry.getPolarity() == Polarity.BOTH) {
                            strongPositiveAdjectiveCount++;
                            strongNegativeAdjectiveCount++;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            strongNegativeAdjectiveCount++;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            strongPositiveAdjectiveCount++;
                        }
                    }

                } else {
                    if (entry.getPolarity() == Polarity.BOTH) {
                        weakPositiveCount++;
                        weakNegativeCount++;
                    } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                        weakNegativeCount++;
                    } else if (entry.getPolarity() == Polarity.POSITIVE) {
                        weakPositiveCount++;
                    }

                    if (tag == PartOfSpeech.ADJECTIVE) {
                        if (entry.getPolarity() == Polarity.BOTH) {
                            weakPositiveAdjectiveCount++;
                            weakNegativeAdjectiveCount++;
                        } else if (entry.getPolarity() == Polarity.NEGATIVE) {
                            weakNegativeAdjectiveCount++;
                        } else if (entry.getPolarity() == Polarity.POSITIVE) {
                            weakPositiveAdjectiveCount++;
                        }
                    }
                }
            }
        }

        featureList.add(strongPositiveCount > 0 ? 1.0 : 0.0);
        featureList.add(strongNegativeCount > 0 ? 1.0 : 0.0);
        featureList.add(weakPositiveCount > 0 ? 1.0 : 0.0);
        featureList.add(weakNegativeCount > 0 ? 1.0 : 0.0);

        featureList.add(strongPositiveAdjectiveCount > 0 ? 1.0 : 0.0);
        featureList.add(strongNegativeAdjectiveCount > 0 ? 1.0 : 0.0);
        featureList.add(weakPositiveAdjectiveCount > 0 ? 1.0 : 0.0);
        featureList.add(weakNegativeAdjectiveCount > 0 ? 1.0 : 0.0);

        featureList.add(strongNegativeCount > strongPositiveCount ? 1.0 : 0.0);
        featureList.add(weakNegativeCount > weakPositiveCount ? 1.0 : 0.0);
        featureList.add(strongNegativeAdjectiveCount > strongPositiveAdjectiveCount ? 1.0 : 0.0);
        featureList.add(weakNegativeAdjectiveCount > weakPositiveAdjectiveCount ? 1.0 : 0.0);
        
        featureList.add((weakNegativeCount + weakPositiveCount) > (strongNegativeCount + strongPositiveCount) ? 1.0 : 0.0);
        featureList.add((strongPositiveCount + weakPositiveCount) > (strongNegativeCount + weakNegativeCount) ? 1.0 : 0.0);
        featureList.add((weakNegativeAdjectiveCount + weakPositiveAdjectiveCount) > (strongNegativeAdjectiveCount + strongPositiveAdjectiveCount) ? 1.0 : 0.0);
        featureList.add((strongPositiveAdjectiveCount + weakPositiveAdjectiveCount) > (strongNegativeAdjectiveCount + weakNegativeAdjectiveCount) ? 1.0 : 0.0);

        double[] features = new double[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            features[i] = featureList.get(i);
        }

        return features;
    }
}
