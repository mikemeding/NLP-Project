package edu.uml.TwitterBullingAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;

import cmu.arktweetnlp.Tagger;
import edu.uml.SentiWordNet.SentiWordNet;
import edu.uml.SentiWordNet.SentiWordNetFeature;
import edu.uml.SentiWordNet.SentiWordNetFeatureExtraction;

public class SentiWordTest {

    public static void main(String[] args) throws Exception {

        SentiWordNet sentiWordNet = new SentiWordNet("files/SentiWordNet_3.0.0_20130122.txt");

        Tagger tagger = new Tagger();
        tagger.loadModel("/cmu/arktweetnlp/model.20120919");

        SentiWordNetFeatureExtraction featureExtraction = new SentiWordNetFeatureExtraction(tagger,
                sentiWordNet);

        BufferedReader br = new BufferedReader(new FileReader("../Corpus/#noob.txt-filtered"));
//        BufferedReader br = new BufferedReader(new FileReader("../Corpus/negativeTweets.txt-filtered"));
        // BufferedReader br = new BufferedReader(new FileReader("../Corpus/bullying_tweets.txt"));
        String line;
        while ((line = br.readLine()) != null) {

            line = line.trim();

            if (line.length() == 0) continue;
            if (line.charAt(0) == '#') continue;

            String[] split = line.split("\t");

            if (split.length != 2) continue;

            SentiWordNetFeature feature = featureExtraction.extractFeatures(split[1]);

            printBinaryFeatures(feature, split[1]);
        }

        br.close();
    }

    public static void printBinaryFeatures(SentiWordNetFeature feature, String tweet) {

        boolean higherNegativeScore = feature.getSumNegativeScore() > feature.getSumPositiveScore();
        boolean higherNonZeroNegativeAverage = feature.getNonZeroAverageNegativeScore() > feature
                .getNonZeroAveragePositiveScore();
        boolean moreNegativeCounts = feature.getNonZeroNegativeCount() > feature
                .getNonZeroPositiveCount();

        boolean higherNegativeAdjective = feature.getSumNegativeAdjectiveScore() > feature
                .getSumPositiveAdjectiveScore();
        boolean higherNonZeroNegativeAdjectiveAverage = feature
                .getNonZeroAverageNegativeAdjectiveScore() > feature
                .getNonZeroAveragePositiveAdjectiveScore();
        boolean moreNegativeAdjectiveCount = feature.getNonZeroNegativeAdjectiveCount() > feature
                .getNonZeroPositiveAdjectiveCount();

        String scoreTemplate = "%5.3f \t%5.3f \t%5d";

        System.out.println(tweet);
        System.out.println("All " + higherNegativeScore + " " + moreNegativeCounts + " "
                + higherNonZeroNegativeAverage);
        // System.out.println(String.format(scoreTemplate, feature.getSumPositiveScore(),
        // feature.getNonZeroAveragePositiveScore(), feature.getNonZeroPositiveCount()));
        // System.out.println(String.format(scoreTemplate, feature.getSumNegativeScore(),
        // feature.getNonZeroAverageNegativeScore(), feature.getNonZeroNegativeCount()));
        System.out.println("Adjective " + higherNegativeAdjective + " "
                + higherNonZeroNegativeAdjectiveAverage + " " + moreNegativeAdjectiveCount);
        // System.out.println(String.format(scoreTemplate, feature.getSumPositiveAdjectiveScore(),
        // feature.getNonZeroAveragePositiveAdjectiveScore(),
        // feature.getNonZeroPositiveAdjectiveCount()));
        // System.out.println(String.format(scoreTemplate, feature.getSumNegativeAdjectiveScore(),
        // feature.getNonZeroAverageNegativeAdjectiveScore(),
        // feature.getNonZeroNegativeAdjectiveCount()));
    }
}
