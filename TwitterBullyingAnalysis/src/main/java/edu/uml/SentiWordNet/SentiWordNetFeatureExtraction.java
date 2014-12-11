package edu.uml.SentiWordNet;

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
    
    public SentiWordNetFeature extractFeatures(String s) {
        
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
        
        for(TaggedToken taggedToken: tagged) {
            
            String token = taggedToken.token.toLowerCase().replace("#", "");
            
            List<SentiWordNetEntry> entries = sentiWordNet.getSentiWordNetEntries(arkTweetNLPTagToSentiWordNetTag.get(taggedToken.tag), token);
            for(SentiWordNetEntry entry: entries) {
                wordCount++;
                sumPositiveScore += entry.getPositiveScore();
                sumNegativeScore += entry.getNegativeScore();
                sumObjectiveScore += entry.getObjectiveScore();
                
                if(entry.getPositiveScore() > 0.0) nonZeroPositiveCount++;
                if(entry.getNegativeScore() > 0.0) nonZeroNegativeCount++;
                if(entry.getObjectiveScore() > 0.0) nonZeroObjectiveCount++;

                if(taggedToken.tag.equals("A")) {
                    sumPositiveAdjectiveScore += entry.getPositiveScore();
                    sumNegativeAdjectiveScore += entry.getNegativeScore();
                    sumObjectiveAdjectiveScore += entry.getObjectiveScore();
                    
                    if(entry.getPositiveScore() > 0.0) nonZeroPositiveAdjectiveCount++;
                    if(entry.getNegativeScore() > 0.0) nonZeroNegativeAdjectiveCount++;
                    if(entry.getObjectiveScore() > 0.0) nonZeroObjectiveAdjectiveCount++;
                }
            }
        }
        
        return new SentiWordNetFeature(wordCount, sumPositiveScore, sumNegativeScore, sumObjectiveScore, nonZeroPositiveCount, nonZeroNegativeCount, nonZeroObjectiveCount, sumPositiveAdjectiveScore, sumNegativeAdjectiveScore, sumObjectiveAdjectiveScore, nonZeroPositiveAdjectiveCount, nonZeroNegativeAdjectiveCount, nonZeroObjectiveAdjectiveCount);
    }
}
