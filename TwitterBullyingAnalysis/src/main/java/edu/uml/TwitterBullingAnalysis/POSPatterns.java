package edu.uml.TwitterBullingAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class POSPatterns {

    public static void main(String[] args) throws Exception {
        Tagger tagger = new Tagger();
        tagger.loadModel("/cmu/arktweetnlp/model.20120919");
        
        HashMap<String, Integer[]> patterns = new HashMap<>();
        
        List<TwitterData> allTweets = new ArrayList<>();
        allTweets.addAll(DataParser.parseData("../Corpus/gold/tweets-annotated-large.txt"));
        allTweets.addAll(DataParser.parseData("../Corpus/gold/tweets-annotated.txt"));
        allTweets.addAll(DataParser.parseData("../Corpus/gold/bullying-tweets.txt"));
        
        int count = 0;
        int bullyingCount = 0;
        int nonBullyingCount = 0;
        
        for(TwitterData twitterData: allTweets) {
            List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(twitterData.getTweet());
                
            count++;
            if(twitterData.isBullying()) {
                bullyingCount++;
            } else {
                nonBullyingCount++;
            }
            
            for(int i = 0; i < taggedTokens.size() - 1; i++) {
                String key = taggedTokens.get(i).tag + taggedTokens.get(i + 1).tag;// + taggedTokens.get(i + 2).tag;
                
                if(!patterns.containsKey(key)) {
                    Integer[] counts = new Integer[3];
                    counts[0] = 0;
                    counts[1] = 0;
                    counts[2] = 0;
                    patterns.put(key, counts);
                }
                
                Integer[] counts = patterns.get(key);
                counts[0]++;
                
                if(twitterData.isBullying()) {
                    counts[1]++;
                } else {
                    counts[2]++;
                }
            }
        }
        
        final int bc = bullyingCount;
        final int nbc = nonBullyingCount;
        
        List<Entry<String, Integer[]>> entries = new ArrayList<>(patterns.entrySet());
        Collections.sort(entries, new Comparator<Entry<String, Integer[]>>() {

            final int indexToSort = 1;
            @Override
            public int compare(Entry<String, Integer[]> o1, Entry<String, Integer[]> o2) {
                
                Integer n1 = (o1.getValue()[1] - o1.getValue()[2]);
                Integer n2 = (o2.getValue()[1] - o2.getValue()[2]);
                
                Double d1 = bullyingRationVSNonBullyingRatio(o1, bc, nbc);
                Double d2 = bullyingRationVSNonBullyingRatio(o2, bc, nbc);
                
                return d2.compareTo(d1);
                
//                return o2.getValue()[indexToSort].compareTo(o1.getValue()[indexToSort]);
            }});
        
        System.out.printf("%d %d %d \n", count, bullyingCount, nonBullyingCount);
        
        for(int i = 0; i < entries.size(); i++) {
            Entry<String, Integer[]> entry = entries.get(i);
            int fullPatternCount = entry.getValue()[0];
            int bullyPatternCount = entry.getValue()[1];
            int nonBullyPatternCount = entry.getValue()[2];
            
            double fullRatio = ((double)fullPatternCount) / count;
            double bullyRatio = ((double)bullyPatternCount) / bullyingCount;
            double nonBullyRatio = ((double)nonBullyPatternCount) / nonBullyingCount;
            
            if(
//                    ((nonBullyRatio > 0.1 || bullyRatio > 0.1) && Math.abs(bullyRatio - nonBullyRatio) > 0.05) 
//                    || 
                    (nonBullyRatio > 0.1 || bullyRatio > 0.1)
                    ) {
//                System.out.printf("%s: %d %d %d %f %f %f\n", entry.getKey(), fullPatternCount, bullyPatternCount, nonBullyingCount, fullRatio, bullyRatio, nonBullyRatio);
                
                System.out.print(entry.getKey() + " ");
            }
        }
    }
    
    private static double bullyingRationVSNonBullyingRatio(Entry<String, Integer[]> entry, int bullyingCount, int nonBullyingCount) {
        
        int bullyPatternCount = entry.getValue()[1];
        int nonBullyPatternCount = entry.getValue()[2];
        
        double bullyRatio = ((double)bullyPatternCount) / bullyingCount;
        double nonBullyRatio = ((double)nonBullyPatternCount) / nonBullyingCount;
        
        return Math.abs(bullyRatio - nonBullyRatio);
    }
}
