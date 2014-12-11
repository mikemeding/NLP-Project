package edu.uml.mpqa.SubjectiveLexicon;

public class SubjectiveLexiconFeature {

    private boolean containsStrongPositive;
    private boolean containsStrongNegative;
    private boolean containsWeakPositive;
    private boolean containsWeakNegative;
    
    private boolean containsStrongPositiveAdjective;
    private boolean containsStrongNegativeAdjective;
    private boolean containsWeakPositiveAdjective;
    private boolean containsWeakNegativeAdjective;
    
    public SubjectiveLexiconFeature(boolean containsStrongPositive, boolean containsStrongNegative,
            boolean containsWeakPositive, boolean containsWeakNegative,
            boolean containsStrongPositiveAdjective, boolean containsStrongNegativeAdjective,
            boolean containsWeakPositiveAdjective, boolean containsWeakNegativeAdjective) {
        this.containsStrongPositive = containsStrongPositive;
        this.containsStrongNegative = containsStrongNegative;
        this.containsWeakPositive = containsWeakPositive;
        this.containsWeakNegative = containsWeakNegative;
        this.containsStrongPositiveAdjective = containsStrongPositiveAdjective;
        this.containsStrongNegativeAdjective = containsStrongNegativeAdjective;
        this.containsWeakPositiveAdjective = containsWeakPositiveAdjective;
        this.containsWeakNegativeAdjective = containsWeakNegativeAdjective;
    }

    public boolean isContainsStrongPositive() {
        return containsStrongPositive;
    }

    public boolean isContainsStrongNegative() {
        return containsStrongNegative;
    }

    public boolean isContainsWeakPositive() {
        return containsWeakPositive;
    }

    public boolean isContainsWeakNegative() {
        return containsWeakNegative;
    }

    public boolean isContainsStrongPositiveAdjective() {
        return containsStrongPositiveAdjective;
    }

    public boolean isContainsStrongNegativeAdjective() {
        return containsStrongNegativeAdjective;
    }

    public boolean isContainsWeakPositiveAdjective() {
        return containsWeakPositiveAdjective;
    }

    public boolean isContainsWeakNegativeAdjective() {
        return containsWeakNegativeAdjective;
    }
}
