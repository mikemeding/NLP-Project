package edu.uml.HarvardInquirer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HarvardInquirer {
    
    private Map<String, List<Map<String, String>>> data;
    
    public static void main(String[] args) throws Exception {
        
        HarvardInquirer inquirer = new HarvardInquirer("../Harvard_inquirer/inqtabs.txt");
        
        System.out.println(inquirer.getEntriesForWord("hang"));
    }

    public HarvardInquirer(String fileName) throws IOException {
        data = new HashMap<>();
        
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        
        String[] attributes = line.split("\t");
        
        while((line = br.readLine()) != null) {
            String[] split = line.split("\t");
            
            String word = split[0];
            word = word.toLowerCase();
            
            HashMap<String, String> entry = new HashMap<>();
            
            int indexOfHash = word.indexOf("#");
            if(indexOfHash > -1) {
                word = word.substring(0, indexOfHash);
            }
            
            for(int i = 0; i < split.length; i++) {
                String attribute = attributes[i];
                String value = split[i];
                
                if(value.length() > 0) {
                    entry.put(attribute, value);
                }
            }
            
            addEntry(word, entry);
        }
        
        br.close();
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getEntriesForWord(String word) {
        List<Map<String, String>> entries = data.get(word);
        
        if(entries != null) return entries;
            
        return Collections.EMPTY_LIST;
    }
    
    private void addEntry(String word, Map<String, String> entry) {
        
        List<Map<String, String>> entries = data.get(word);
        
        if(entries == null) {
            entries = new ArrayList<>();
            
            data.put(word, entries);
        }
        
        entries.add(entry);
    }
}
