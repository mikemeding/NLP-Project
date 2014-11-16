package edu.uml.TwitterBullingAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import edu.uml.HarvardInquirer.HarvardInquirer;

public class HarvardInquirerTest {

    public static void main(String[] args) throws Exception {
        HarvardInquirer harvardInquirer = new HarvardInquirer("../Harvard_inquirer/inqtabs.txt");

        Tagger tagger = new Tagger();
        tagger.loadModel("/cmu/arktweetnlp/model.20120919");

        BufferedReader br = new BufferedReader(new FileReader("../Corpus/#noob.txt-filtered"));
//        BufferedReader br = new BufferedReader(new FileReader("../Corpus/negativeTweets.txt-filtered"));
//         BufferedReader br = new BufferedReader(new FileReader("../Corpus/bullying_tweets.txt"));
        String line;
        while ((line = br.readLine()) != null) {

            line = line.trim();

            if (line.length() == 0) continue;
            if (line.charAt(0) == '#') continue;

            String[] split = line.split("\t");

            if (split.length != 2) continue;
            
            printBinaryFeature(tagger, harvardInquirer, split[1]);
        }

         
//        line = "@MsCharlotteD please put your face into a toaster. #diecharlotte";
//        printBinaryFeature(tagger, harvardInquirer, line);
        br.close();
    }

    private static void printBinaryFeature(Tagger tagger, HarvardInquirer harvardInquirer, String s) {
        List<TaggedToken> tagged = tagger.tokenizeAndTag(s);

        boolean containsHostileWord = false;
        boolean containsNegativeWord = false;
        boolean containsFailWord = false;

//        System.out.println(s);

        for (TaggedToken taggedToken : tagged) {

            for (Map<String, String> entry : harvardInquirer.getEntriesForWord(taggedToken.token.toLowerCase())) {
//                System.out.println(taggedToken.token + " " + taggedToken.tag + " " + entry);
                
                if (hasTag(taggedToken.tag, entry)) {
                    
                    // http://www.wjh.harvard.edu/~inquirer/homecat.htm
                    
                    if (entry.containsKey("Hostile")) containsHostileWord = true;

                    if (entry.containsKey("Negativ")) containsNegativeWord = true;
                    
                    if (entry.containsKey("Fail")) containsFailWord = true;
                }
            }
        }

//        System.out.println(containsHostileWord + " " + containsNegativeWord + " " + containsFailWord);
        
        if(containsFailWord || containsHostileWord || containsNegativeWord) {
            System.out.println(s);
        }
    }

    private static boolean hasTag(String tag, Map<String, String> entry) {

        if (!entry.containsKey("Othtags")) return false;

        String[] othtags = entry.get("Othtags").split(" ");
        for (int i = 0; i < othtags.length; i++) {
            othtags[i] = othtags[i].toUpperCase();
        }

        if (tag.equals("A")) {
            // adjective
            for (String othtag : othtags) {
                if (othtag.equals("MODIF")) return true;
            }
        } else if (tag.equals("N")) {
            // noun
            for (String othtag : othtags) {
                if (othtag.equals("NOUN")) return true;
            }
        } else if (tag.equals("V")) {
            // verb
            for (String othtag : othtags) {
                if (othtag.equals("VERB") || othtag.equals("SUPV")) return true;
            }
        } else if (tag.equals("R")) {
            // adverb
            for (String othtag : othtags) {
                if (othtag.equals("LY")) return true;
            }
        } else if (tag.equals("O")) {
            // pronoun
            for (String othtag : othtags) {
                if (othtag.equals("PRON")) return true;
            }
        } else if (tag.equals("P")) {
            // preposition
            for (String othtag : othtags) {
                if (othtag.equals("PREP")) return true;
            }
        } else if (tag.equals("S") || tag.equals("Z")) {
            //
            for (String othtag : othtags) {
                if (othtag.equals("DET")) return true;
            }
        }

        return false;
    }
}
