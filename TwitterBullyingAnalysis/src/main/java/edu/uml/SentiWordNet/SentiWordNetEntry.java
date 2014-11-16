package edu.uml.SentiWordNet;

public class SentiWordNetEntry {

    private String pos;
    private int id;
    
    private double positiveScore;
    private double negativeScore;
    private double objectiveScore;
    
    private String synsetTerms;
    private String gloss;
    
    public SentiWordNetEntry(String pos, int id, double positiveScore, double negativeScore,
            String synsetTerms, String gloss) {
        this.pos = pos;
        this.id = id;
        this.positiveScore = positiveScore;
        this.negativeScore = negativeScore;
        this.objectiveScore = 1.0 - positiveScore - negativeScore;
        this.synsetTerms = synsetTerms;
        this.gloss = gloss;
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

    public double getObjectiveScore() {
        return objectiveScore;
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

    @Override
    public String toString() {
        return "SentiWordNetEntry [pos=" + pos + ", id=" + id + ", positiveScore=" + positiveScore
                + ", negativeScore=" + negativeScore + ", objectiveScore=" + objectiveScore
                + ", synsetTerms=" + synsetTerms + ", gloss=" + gloss + "]";
    }
}
