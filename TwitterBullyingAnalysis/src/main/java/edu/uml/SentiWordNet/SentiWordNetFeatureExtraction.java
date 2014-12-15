package edu.uml.SentiWordNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class SentiWordNetFeatureExtraction {

    private Tagger tagger;
    private SentiWordNet sentiWordNet;
    private Map<String, String> arkTweetNLPTagToSentiWordNetTag;

    public SentiWordNetFeatureExtraction(Tagger tagger, SentiWordNet sentiWordNet) {
        this.tagger = tagger;
        this.sentiWordNet = sentiWordNet;

        arkTweetNLPTagToSentiWordNetTag = new HashMap<>();

        arkTweetNLPTagToSentiWordNetTag.put("A", "a"); // adjective
        arkTweetNLPTagToSentiWordNetTag.put("N", "n"); // noun
        arkTweetNLPTagToSentiWordNetTag.put("V", "v"); // verb
        arkTweetNLPTagToSentiWordNetTag.put("R", "r"); // adverb
    }

    public double[] extractFeatures(String tweet) {

        SentiWordNetFeature swnf = createFeaturesObject(tweet);
        ArrayList<Double> featureList = new ArrayList<>();

        featureList.add(swnf.getAveragePositiveScore());
        featureList.add(swnf.getAverageNegativeScore());
        featureList.add(swnf.getAverageObjectiveScore());

        featureList.add(swnf.getNonZeroAveragePositiveScore());
        featureList.add(swnf.getNonZeroAverageNegativeScore());
        featureList.add(swnf.getNonZeroAverageObjectiveScore());

        featureList.add(swnf.getNonZeroAveragePositiveAdjectiveScore());
        featureList.add(swnf.getNonZeroAverageNegativeAdjectiveScore());
        featureList.add(swnf.getNonZeroAverageObjectiveAdjectiveScore());

        boolean possitiveMajority = swnf.getNonZeroPositiveCount() > swnf.getNonZeroNegativeCount()
                && swnf.getNonZeroPositiveCount() > swnf.getNonZeroObjectiveCount();
        boolean negativeMajority = swnf.getNonZeroNegativeCount() > swnf.getNonZeroPositiveCount()
                && swnf.getNonZeroNegativeCount() > swnf.getNonZeroObjectiveCount();
        boolean objectiveMajority = possitiveMajority == false && negativeMajority == false;

        featureList.add(possitiveMajority ? 1.0 : 0.0);
        featureList.add(negativeMajority ? 1.0 : 0.0);
        featureList.add(objectiveMajority ? 1.0 : 0.0);

        boolean possitiveAdjectiveMajority = swnf.getNonZeroPositiveAdjectiveCount() > swnf
                .getNonZeroNegativeAdjectiveCount()
                && swnf.getNonZeroPositiveAdjectiveCount() > swnf
                        .getNonZeroObjectiveAdjectiveCount();
        boolean negativeAdjectiveMajority = swnf.getNonZeroNegativeAdjectiveCount() > swnf
                .getNonZeroPositiveAdjectiveCount()
                && swnf.getNonZeroNegativeAdjectiveCount() > swnf
                        .getNonZeroObjectiveAdjectiveCount();
        boolean objectiveAdjectiveMajority = possitiveAdjectiveMajority == false
                && negativeAdjectiveMajority == false;

        featureList.add(possitiveAdjectiveMajority ? 1.0 : 0.0);
        featureList.add(negativeAdjectiveMajority ? 1.0 : 0.0);
        featureList.add(objectiveAdjectiveMajority ? 1.0 : 0.0);

        boolean moreNegativesThanPositives = swnf.getNonZeroNegativeCount() > swnf
                .getNonZeroPositiveCount();
        boolean moreNegativeAdjectivesThanPositives = swnf.getNonZeroNegativeAdjectiveCount() > swnf
                .getNonZeroPositiveAdjectiveCount();

        featureList.add(moreNegativesThanPositives ? 1.0 : 0.0);
        featureList.add(moreNegativeAdjectivesThanPositives ? 1.0 : 0.0);

        double[] features = new double[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            features[i] = featureList.get(i);
        }

        return features;
    }

    public SentiWordNetFeature createFeaturesObject(String s) {

        int wordCount = 0;

        double sumPositiveScore = 0.0;
        double sumNegativeScore = 0.0;
        double sumObjectiveScore = 0.0;

        int nonZeroPositiveCount = 0;
        int nonZeroNegativeCount = 0;
        int nonZeroObjectiveCount = 0;

        double sumPositiveAdjectiveScore = 0.0;
        double sumNegativeAdjectiveScore = 0.0;
        double sumObjectiveAdjectiveScore = 0.0;

        int nonZeroPositiveAdjectiveCount = 0;
        int nonZeroNegativeAdjectiveCount = 0;
        int nonZeroObjectiveAdjectiveCount = 0;

        List<TaggedToken> tagged = tagger.tokenizeAndTag(s);

        for (TaggedToken taggedToken : tagged) {

            String token = taggedToken.token.toLowerCase().replace("#", "");

            List<SentiWordNetEntry> entries = sentiWordNet.getSentiWordNetEntries(
                    arkTweetNLPTagToSentiWordNetTag.get(taggedToken.tag), token);
            for (SentiWordNetEntry entry : entries) {
                wordCount++;
                sumPositiveScore += entry.getPositiveScore();
                sumNegativeScore += entry.getNegativeScore();
                sumObjectiveScore += entry.getObjectiveScore();

                if (entry.getPositiveScore() > 0.0) nonZeroPositiveCount++;
                if (entry.getNegativeScore() > 0.0) nonZeroNegativeCount++;
                if (entry.getObjectiveScore() > 0.0) nonZeroObjectiveCount++;

                if (taggedToken.tag.equals("A")) {
                    sumPositiveAdjectiveScore += entry.getPositiveScore();
                    sumNegativeAdjectiveScore += entry.getNegativeScore();
                    sumObjectiveAdjectiveScore += entry.getObjectiveScore();

                    if (entry.getPositiveScore() > 0.0) nonZeroPositiveAdjectiveCount++;
                    if (entry.getNegativeScore() > 0.0) nonZeroNegativeAdjectiveCount++;
                    if (entry.getObjectiveScore() > 0.0) nonZeroObjectiveAdjectiveCount++;
                }
            }
        }

        return new SentiWordNetFeature(wordCount, sumPositiveScore, sumNegativeScore,
                sumObjectiveScore, nonZeroPositiveCount, nonZeroNegativeCount,
                nonZeroObjectiveCount, sumPositiveAdjectiveScore, sumNegativeAdjectiveScore,
                sumObjectiveAdjectiveScore, nonZeroPositiveAdjectiveCount,
                nonZeroNegativeAdjectiveCount, nonZeroObjectiveAdjectiveCount);
    }
}
