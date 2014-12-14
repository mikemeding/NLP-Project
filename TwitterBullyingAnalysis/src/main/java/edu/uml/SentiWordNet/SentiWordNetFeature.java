package edu.uml.SentiWordNet;

public class SentiWordNetFeature {

    private int wordCount;
    
    private double sumPositiveScore;
    private double sumNegativeScore;
    private double sumObjectiveScore;
    
    private int nonZeroPositiveCount;
    private int nonZeroNegativeCount;
    private int nonZeroObjectiveCount;
    
    private double sumPositiveAdjectiveScore;
    private double sumNegativeAdjectiveScore;
    private double sumObjectiveAdjectiveScore;
    
    private int nonZeroPositiveAdjectiveCount;
    private int nonZeroNegativeAdjectiveCount;
    private int nonZeroObjectiveAdjectiveCount;
    
    public SentiWordNetFeature(int wordCount, double sumPositiveScore, double sumNegativeScore,
            double sumObjectiveScore, int nonZeroPositiveCount, int nonZeroNegativeCount,
            int nonZeroObjectiveCount, double sumPositiveAdjectiveScore,
            double sumNegativeAdjectiveScore, double sumObjectiveAdjectiveScore,
            int nonZeroPositiveAdjectiveCount, int nonZeroNegativeAdjectiveCount,
            int nonZeroObjectiveAdjectiveCount) {
        this.wordCount = wordCount;
        this.sumPositiveScore = sumPositiveScore;
        this.sumNegativeScore = sumNegativeScore;
        this.sumObjectiveScore = sumObjectiveScore;
        this.nonZeroPositiveCount = nonZeroPositiveCount;
        this.nonZeroNegativeCount = nonZeroNegativeCount;
        this.nonZeroObjectiveCount = nonZeroObjectiveCount;
        this.sumPositiveAdjectiveScore = sumPositiveAdjectiveScore;
        this.sumNegativeAdjectiveScore = sumNegativeAdjectiveScore;
        this.sumObjectiveAdjectiveScore = sumObjectiveAdjectiveScore;
        this.nonZeroPositiveAdjectiveCount = nonZeroPositiveAdjectiveCount;
        this.nonZeroNegativeAdjectiveCount = nonZeroNegativeAdjectiveCount;
        this.nonZeroObjectiveAdjectiveCount = nonZeroObjectiveAdjectiveCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public double getSumPositiveScore() {
        return sumPositiveScore;
    }
    
    public double getNonZeroAveragePositiveScore() {
        if(nonZeroPositiveCount == 0) return 0.0;
        return sumPositiveScore / nonZeroPositiveCount;
    }
    
    public double getAveragePositiveScore() {
        if(wordCount == 0) return 0.0;
        return sumPositiveScore / wordCount;
    }
    
    public double getSumNegativeScore() {
        return sumNegativeScore;
    }
    
    public double getNonZeroAverageNegativeScore() {
        if(nonZeroNegativeCount == 0) return 0.0;
        return sumNegativeScore / nonZeroNegativeCount;
    }
    
    public double getAverageNegativeScore() {
        if(wordCount == 0) return 0.0;
        return sumNegativeScore / wordCount;
    }

    public double getSumObjectiveScore() {
        return sumObjectiveScore;
    }
    
    public double getNonZeroAverageObjectiveScore() {
        if(nonZeroObjectiveCount == 0) return 0.0;
        return sumObjectiveScore / nonZeroObjectiveCount;
    }
    
    public double getAverageObjectiveScore() {
        if(wordCount == 0) return 0.0;
        return sumObjectiveScore / wordCount;
    }
    
    public int getNonZeroPositiveCount() {
        return nonZeroPositiveCount;
    }

    public int getNonZeroNegativeCount() {
        return nonZeroNegativeCount;
    }
    
    public int getNonZeroObjectiveCount() {
        return nonZeroObjectiveCount;
    }

    public double getSumPositiveAdjectiveScore() {
        return sumPositiveAdjectiveScore;
    }
    
    public double getNonZeroAveragePositiveAdjectiveScore() {
        if(nonZeroPositiveAdjectiveCount == 0) return 0.0;
        return sumPositiveAdjectiveScore / nonZeroPositiveAdjectiveCount;
    }

    public double getSumNegativeAdjectiveScore() {
        return sumNegativeAdjectiveScore;
    }
    
    public double getNonZeroAverageNegativeAdjectiveScore() {
        if(nonZeroNegativeAdjectiveCount == 0) return 0;
        return sumNegativeAdjectiveScore / nonZeroNegativeAdjectiveCount;
    }

    public int getNonZeroPositiveAdjectiveCount() {
        return nonZeroPositiveAdjectiveCount;
    }

    public int getNonZeroNegativeAdjectiveCount() {
        return nonZeroNegativeAdjectiveCount;
    }
    
    public double getNonZeroAverageObjectiveAdjectiveScore() {
        if(nonZeroObjectiveAdjectiveCount == 0) return 0;
        return sumObjectiveAdjectiveScore / nonZeroObjectiveAdjectiveCount;
    }

    public int getNonZeroObjectiveAdjectiveCount() {
        return nonZeroObjectiveAdjectiveCount;
    }
}
