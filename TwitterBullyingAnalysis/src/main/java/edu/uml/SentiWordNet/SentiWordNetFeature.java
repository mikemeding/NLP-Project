package edu.uml.SentiWordNet;

public class SentiWordNetFeature {

    private int wordCount;
    
    private double sumPositiveScore;
    private double sumNegativeScore;
    
    private int nonZeroPositiveCount;
    private int nonZeroNegativeCount;
    
    private double sumPositiveAdjectiveScore;
    private double sumNegativeAdjectiveScore;
    
    private int nonZeroPositiveAdjectiveCount;
    private int nonZeroNegativeAdjectiveCount;
    
    public SentiWordNetFeature(int wordCount, double sumPositiveScore, double sumNegativeScore,
            int nonZeroPositiveCount, int nonZeroNegativeCount, double sumPositiveAdjectiveScore,
            double sumNegativeAdjectiveScore, int nonZeroPositiveAdjectiveCount,
            int nonZeroNegativeAdjectiveCount) {
        this.wordCount = wordCount;
        this.sumPositiveScore = sumPositiveScore;
        this.sumNegativeScore = sumNegativeScore;
        this.nonZeroPositiveCount = nonZeroPositiveCount;
        this.nonZeroNegativeCount = nonZeroNegativeCount;
        this.sumPositiveAdjectiveScore = sumPositiveAdjectiveScore;
        this.sumNegativeAdjectiveScore = sumNegativeAdjectiveScore;
        this.nonZeroPositiveAdjectiveCount = nonZeroPositiveAdjectiveCount;
        this.nonZeroNegativeAdjectiveCount = nonZeroNegativeAdjectiveCount;
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
        return sumNegativeScore / wordCount;
    }

    public int getNonZeroPositiveCount() {
        return nonZeroPositiveCount;
    }

    public int getNonZeroNegativeCount() {
        return nonZeroNegativeCount;
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
}
