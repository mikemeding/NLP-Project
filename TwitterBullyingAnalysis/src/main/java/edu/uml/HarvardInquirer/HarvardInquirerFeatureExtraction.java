package edu.uml.HarvardInquirer;

import java.util.List;
import java.util.Map;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class HarvardInquirerFeatureExtraction {

    private Tagger tagger;
    private HarvardInquirer harvardInquirer;

    public HarvardInquirerFeatureExtraction(Tagger tagger, HarvardInquirer harvardInquirer) {
        this.tagger = tagger;
        this.harvardInquirer = harvardInquirer;
    }

    public HarvardInquirerFeature extractFeatures(String tweet) {
        List<TaggedToken> tagged = tagger.tokenizeAndTag(tweet);

        boolean containsPositiveWord = false;
        boolean containsNegativeWord = false;
        boolean containsHostileWord = false;
        boolean containsFailWord = false;
        boolean containsActiveWord = false;
        boolean containsPassiveWord = false;
        boolean containsPleasureWord = false;
        boolean containsPainWord = false;
        boolean containsStrongWord = false;
        boolean containsWeakWord = false;
        boolean containsVirtueWord = false;
        boolean containsViceWord = false;

        for (TaggedToken taggedToken : tagged) {

            for (Map<String, String> entry : harvardInquirer.getEntriesForWord(taggedToken.token
                    .toLowerCase())) {
                if (hasTag(taggedToken.tag, entry)) {

                    // http://www.wjh.harvard.edu/~inquirer/homecat.htm
                    if (entry.containsKey("Positiv")) containsPositiveWord = true;
                    if (entry.containsKey("Negativ")) containsNegativeWord = true;

                    if (entry.containsKey("Hostile")) containsHostileWord = true;
                    if (entry.containsKey("Fail")) containsFailWord = true;

                    if (entry.containsKey("Active")) containsActiveWord = true;
                    if (entry.containsKey("Passive")) containsPassiveWord = true;

                    if (entry.containsKey("Pleasur")) containsPleasureWord = true;
                    if (entry.containsKey("Pain")) containsPainWord = true;

                    if (entry.containsKey("Strong")) containsStrongWord = true;
                    if (entry.containsKey("Weak")) containsWeakWord = true;

                    if (entry.containsKey("Virtue")) containsVirtueWord = true;
                    if (entry.containsKey("Vice")) containsViceWord = true;
                }
            }
        }

        return new HarvardInquirerFeature(containsPositiveWord, containsNegativeWord,
                containsHostileWord, containsFailWord, containsActiveWord, containsPassiveWord,
                containsPleasureWord, containsPainWord, containsStrongWord, containsWeakWord,
                containsVirtueWord, containsViceWord);
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
