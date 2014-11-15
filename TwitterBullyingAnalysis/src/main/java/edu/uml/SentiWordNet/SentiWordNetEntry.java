package edu.uml.SentiWordNet;

public class SentiWordNetEntry {

    private String pos;
    private int id;
    
    private double positiveScore;
    private double negativeScore;
    
    private String synsetTerms;
    private String gloss;
    
    private String[] splitTerms;

    public SentiWordNetEntry(String pos, int id, double positiveScore, double negativeScore,
            String synsetTerms, String gloss, String[] splitTerms) {
        this.pos = pos;
        this.id = id;
        this.positiveScore = positiveScore;
        this.negativeScore = negativeScore;
        this.synsetTerms = synsetTerms;
        this.gloss = gloss;
        this.splitTerms = splitTerms;
    }

    public String getPos() {
        return pos;
    }

    public int getId() {
        return id;
    }

    public double getPositiveScore() {
        return positiveScore;
    }

    public double getNegativeScore() {
        return negativeScore;
    }

    public String getSynsetTerms() {
        return synsetTerms;
    }

    public String getGloss() {
        return gloss;
    }

    public String[] getSplitTerms() {
        return splitTerms;
    }

    @Override
    public String toString() {
        return "SentiWordNetEntry [pos=" + pos + ", id=" + id + ", positiveScore=" + positiveScore
                + ", negativeScore=" + negativeScore + ", synsetTerms=" + synsetTerms + ", gloss="
                + gloss + "]";
    }
}
