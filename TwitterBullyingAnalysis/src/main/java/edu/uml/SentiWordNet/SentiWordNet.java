package edu.uml.SentiWordNet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentiWordNet {

    public static void main(String[] args) throws Exception {
        new SentiWordNet("files/SentiWordNet_3.0.0_20130122.txt");
    }
    
    private Map<String, Map<String, List<SentiWordNetEntry>>> posMap;
    
    public SentiWordNet(String fileName) throws IOException {
        posMap = new HashMap<>();
        readFile(fileName);
    }
    
    public List<SentiWordNetEntry> getSentiWordNetEntries(String tag, String word) {
        
        Map<String, List<SentiWordNetEntry>> wordMap = posMap.get(tag);
        
        if(wordMap != null) {
            return wordMap.get(word);
        }
        
        return null;
    }
    
    private void readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        
        String line = null;
        while((line = br.readLine()) != null) {
            
            line = line.trim();
            
            if(line.length() == 0) {
                continue;
            }
            
            if(line.charAt(0) == '#') {
                // comment so skip
                continue;
            }
            
            String[] fields = line.split("\t");
            
            String pos = fields[0];
            int id = Integer.parseInt(fields[1]);
            
            double positiveScore = Double.parseDouble(fields[2]);
            double negativeScore = Double.parseDouble(fields[3]);
            
            String synsetTerms = fields[4];
            String gloss = fields[5];
            
            String[] splitTerms = synsetTerms.split(" ");
            
            
            SentiWordNetEntry entry = new SentiWordNetEntry(pos, id, positiveScore, negativeScore, synsetTerms, gloss, splitTerms);
            addToMap(entry);
        }
        
        br.close();
    }
    
    private void addToMap(SentiWordNetEntry entry) {
        
        Map<String, List<SentiWordNetEntry>> wordMap = posMap.get(entry.getPos());
        
        if(wordMap == null) {
            wordMap = new HashMap<>();
            
            posMap.put(entry.getPos(), wordMap);
        }
        
        for(String word: entry.getSplitTerms()) {
            word = word.substring(0, word.length() - 2);
            
            List<SentiWordNetEntry> entries = wordMap.get(word);
            if(entries == null) {
                entries = new ArrayList<>();
                
                wordMap.put(word, entries);
            }
            
            entries.add(entry);
        }
    }
}
