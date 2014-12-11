package edu.uml.mpqa.SubjectiveLexicon;

public class Entry {
    
    private String word;
    private boolean strongSubjective;
    private PartOfSpeech pos;
    private boolean stemmed;
    private Polarity polarity;
    
    public Entry(String word, boolean strongSubjective, PartOfSpeech pos, boolean stemmed,
            Polarity polarity) {
        this.word = word;
        this.strongSubjective = strongSubjective;
        this.pos = pos;
        this.stemmed = stemmed;
        this.polarity = polarity;
    }

    public String getWord() {
        return word;
    }

    public boolean isStrongSubjective() {
        return strongSubjective;
    }

    public PartOfSpeech getPos() {
        return pos;
    }

    public boolean isStemmed() {
        return stemmed;
    }

    public Polarity getPolarity() {
        return polarity;
    }

    @Override
    public String toString() {
        return "Entry [word=" + word + ", strongSubjective=" + strongSubjective + ", pos=" + pos
                + ", stemmed=" + stemmed + ", polarity=" + polarity + "]";
    }

    public static enum PartOfSpeech {
        
        ADJECTIVE("adj"),
        NOUN("noun"),
        VERB("verb"),
        ADVERB("adverb"),
        ANY("anypos");
        
        private String type;
        
        private PartOfSpeech(String type) {
            this.type = type;
        }
        
        public static PartOfSpeech getPartOfSpeechFromString(String type) {
            
            for(PartOfSpeech entryType: values()) {
                
                if(entryType.type.equals(type)) {
                    return entryType;
                }
            }

            return null;
        }
    }
    
    public static enum Polarity {
        
        POSITIVE("positive"),
        NEGATIVE("negative"),
        NEUTRAL("neutral"),
        BOTH("both");
        
        private String type;
        
        private Polarity(String type) {
            this.type = type;
        }
        
        public static Polarity getPolarityFromString(String type) {
            
            for(Polarity polarity: values()) {
                
                if(polarity.type.equals(type)) {
                    return polarity;
                }
            }

            return null;
        }
    }
}
