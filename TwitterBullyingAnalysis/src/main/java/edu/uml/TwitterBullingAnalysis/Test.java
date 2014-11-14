package edu.uml.TwitterBullingAnalysis;

import java.util.List;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import edu.uml.SentiWordNet.SentiWordNet;

public class Test {
    
    public static void main(String[] args) throws Exception {
        
        SentiWordNet sentiWordNet = new SentiWordNet("files/SentiWordNet_3.0.0_20130122.txt");
        
        String modelFilename = "/cmu/arktweetnlp/model.20120919";
        
        Tagger tagger = new Tagger();
        tagger.loadModel(modelFilename);
        List<TaggedToken> tagged = tagger.tokenizeAndTag("@TomDaley1994 i'm going to find you and i'm going to drown you in the pool you cocky twat your a nobody people like you make me sick");
        
        
        
        for(TaggedToken taggedToken: tagged) {
            System.out.println(taggedToken.token + " " + taggedToken.tag);
            
            System.out.println(sentiWordNet.getSentiWordNetEntries(taggedToken.tag.toLowerCase(), taggedToken.token));
        }
    }
}
