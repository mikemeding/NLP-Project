package edu.uml.HarvardInquirer;

public class HarvardInquirerFeature {

    private boolean containsPositiveWord;
    private boolean containsNegativeWord;
    private boolean containsHostileWord;
    private boolean containsFailWord;
    private boolean containsActiveWord;
    private boolean containsPassiveWord;
    private boolean containsPleasureWord;
    private boolean containsPainWord;
    private boolean containsStrongWord;
    private boolean containsWeakWord;
    private boolean containsVirtueWord;
    private boolean containsViceWord;

    public HarvardInquirerFeature(boolean containsPositiveWord, boolean containsNegativeWord,
            boolean containsHostileWord, boolean containsFailWord, boolean containsActiveWord,
            boolean containsPassiveWord, boolean containsPleasureWord, boolean containsPainWord,
            boolean containsStrongWord, boolean containsWeakWord, boolean containsVirtueWord,
            boolean containsViceWord) {
        this.containsPositiveWord = containsPositiveWord;
        this.containsNegativeWord = containsNegativeWord;
        this.containsHostileWord = containsHostileWord;
        this.containsFailWord = containsFailWord;
        this.containsActiveWord = containsActiveWord;
        this.containsPassiveWord = containsPassiveWord;
        this.containsPleasureWord = containsPleasureWord;
        this.containsPainWord = containsPainWord;
        this.containsStrongWord = containsStrongWord;
        this.containsWeakWord = containsWeakWord;
        this.containsVirtueWord = containsVirtueWord;
        this.containsViceWord = containsViceWord;
    }

    public boolean isContainsPositiveWord() {
        return containsPositiveWord;
    }

    public boolean isContainsNegativeWord() {
        return containsNegativeWord;
    }

    public boolean isContainsHostileWord() {
        return containsHostileWord;
    }

    public boolean isContainsFailWord() {
        return containsFailWord;
    }

    public boolean isContainsActiveWord() {
        return containsActiveWord;
    }

    public boolean isContainsPassiveWord() {
        return containsPassiveWord;
    }

    public boolean isContainsPleasureWord() {
        return containsPleasureWord;
    }

    public boolean isContainsPainWord() {
        return containsPainWord;
    }

    public boolean isContainsStrongWord() {
        return containsStrongWord;
    }

    public boolean isContainsWeakWord() {
        return containsWeakWord;
    }

    public boolean isContainsVirtueWord() {
        return containsVirtueWord;
    }

    public boolean isContainsViceWord() {
        return containsViceWord;
    }
}
