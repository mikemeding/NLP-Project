package edu.uml.mpqa.SubjectiveLexicon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uml.mpqa.SubjectiveLexicon.Entry.PartOfSpeech;
import edu.uml.mpqa.SubjectiveLexicon.Entry.Polarity;

public class SubjectiveLexicon {

    public static void main(String[] args) throws Exception {
      //type=weaksubj len=1 word1=abandoned pos1=adj stemmed1=n priorpolarity=negative
        
        SubjectiveLexicon lexicon = new SubjectiveLexicon("../subjectivity_clues_hltemnlp05/subjclueslen1-HLTEMNLP05.tff");
        
        System.out.println(lexicon.getEntries(PartOfSpeech.ADJECTIVE, "ashamed").iterator().next());
    }

    private Map<PartOfSpeech, Map<String, Set<Entry>>> posMap;
    
    public SubjectiveLexicon(String fileName) throws IOException {
        posMap = new HashMap<>();
        read(fileName);
    }
    
    public void read(String fileName) throws IOException {
        
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(fileName));

            String line;
            while((line = br.readLine()) != null) {
                
                String[] attributes = line.split(" ");
                
                String subjectiveStr = attributes[0].split("=")[1];
                //String lenStr = attributes[1].split("=")[1]; // is always 1 so ignore
                String word = attributes[2].split("=")[1];
                String posStr = attributes[3].split("=")[1];
                String stemmedStr = attributes[4].split("=")[1];
                String polarityStr = attributes[5].split("=")[1];
                
                PartOfSpeech pos = PartOfSpeech.getPartOfSpeechFromString(posStr);
                Polarity polarity = Polarity.getPolarityFromString(polarityStr);

                if(pos == null) throw new RuntimeException("Could not handle pos value: " + posStr);
                if(polarity == null) throw new RuntimeException("Could not handle polarity value: " + polarityStr);
                
                boolean strongSubjective;
                if(subjectiveStr.equals("strongsubj")) {
                    strongSubjective = true;
                } else if(subjectiveStr.equals("weaksubj")) {
                    strongSubjective = false;
                } else {
                    throw new RuntimeException(subjectiveStr + " is neither strong nor weak");
                }
                
                boolean stemmed;
                if(stemmedStr.equals("y")) {
                    stemmed = true;
                } else if(stemmedStr.equals("n")) {
                    stemmed = false;
                } else {
                    throw new RuntimeException("stemmed value is incorrect: " + stemmedStr);
                }
                
                Entry entry = new Entry(word, strongSubjective, pos, stemmed, polarity);
                addToMap(entry);
            }
            
            
        } finally {
            
            if(br != null) {
                br.close();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public Set<Entry> getEntries(PartOfSpeech tag, String word) {
        
        Map<String, Set<Entry>> wordMap = posMap.get(tag);
        
        if(wordMap != null && wordMap.get(word) != null) {
            return wordMap.get(word);
        }
        
        return Collections.EMPTY_SET;
    }
    
    private void addToMap(Entry entry) {
        
        Map<String, Set<Entry>> wordMap = posMap.get(entry.getPos());
        if(wordMap == null) {
            wordMap = new HashMap<>();
            
            posMap.put(entry.getPos(), wordMap);
        }
        
        Set<Entry> entries = wordMap.get(entry.getWord());
        if(entries == null) {
            entries = new HashSet<>();
            
            wordMap.put(entry.getWord(), entries);
        }
        
        entries.add(entry);
    }
}
