package edu.uml.ArkTweetNLP;

import java.util.List;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class CommonTagPatternExtractor {

    private Tagger tagger;
    
    private static String patternsStr3 = "VDN DN, N,O DNP ,OV OVD NVP NPN DAN DNV NOV AN, OVO ### PDN ANP VDA VOV ,## OVV NPD VPV N,#";
    private static String[] patterns3 = patternsStr3.split(" ");
    
    private static String patternsStr2 = "DN OV ^^ VD NP ~@ LD ,O @V AN ^N NO VO N# VV ,L O, ## @~ PN N, V, PO RV DA VP @@ NV RA ,R VA ,V P^ ^V NN A, ,N N& PD ^, AP ,# VN PV VR";
    private static String[] patterns2 = patternsStr2.split(" ");

    public static void main(String[] args) {
        System.out.println(patterns3.length + patterns2.length);
    }
    
    public CommonTagPatternExtractor(Tagger tagger) {
        this.tagger = tagger;
    }
    
    public double[] extractFeatures(String s) {
        List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(s);
        
        double[] flags = new double[patterns3.length + patterns2.length];

        for(int i = 0; i < taggedTokens.size() - 2; i++) {
            String currentPattern = taggedTokens.get(i).tag + taggedTokens.get(i + 1).tag + taggedTokens.get(i + 2).tag;
            
            for(int j = 0; j < patterns3.length; j++) {
                String pattern = patterns3[j];
                
                if(pattern.equals(currentPattern)) {
                    flags[j] = 1.0;
                }
            }
        }
        
        for(int i = 0; i < taggedTokens.size() - 1; i++) {
            String currentPattern = taggedTokens.get(i).tag + taggedTokens.get(i + 1).tag;
            
            for(int j = 0; j < patterns2.length; j++) {
                String pattern = patterns2[j];
                
                if(pattern.equals(currentPattern)) {
                    flags[patterns3.length + j] = 1.0;
                }
            }
        }
        
        return flags;
    }
}
