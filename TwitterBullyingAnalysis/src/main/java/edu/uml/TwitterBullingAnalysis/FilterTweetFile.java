package edu.uml.TwitterBullingAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

import edu.uml.SentiWordNet.SentiWordNet;
import edu.uml.SentiWordNet.SentiWordNetEntry;

public class FilterTweetFile {

    public static void main(String[] args) throws Exception {
        
//        String fileName = "../Corpus/negativeTweets.txt";
        String fileName = "../Corpus/#noob.txt";
        String filteredFileName = fileName + "-filtered";
        
        File filteredFile = new File(filteredFileName);
        
        if(filteredFile.exists()) throw new RuntimeException("Filtered file already exists");
        
        SentiWordNet sentiWordNet = new SentiWordNet("files/SentiWordNet_3.0.0_20130122.txt");
        Tagger tagger = new Tagger();
        tagger.loadModel("/cmu/arktweetnlp/model.20120919");
        
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        FileWriter fw = new FileWriter(filteredFile);
        
        int linesRead = 0;
        int linesKept = 0;
        
        String line;
        while((line = br.readLine()) != null) {
            
            line = line.trim();
            
            if(line.length() == 0) continue;
            if(line.charAt(0) == '#') continue;
            
            String[] split = line.split("\t");
            
            if(split.length != 2) continue;
            
            boolean containsWordsInSentiWordNet = false;
            List<TaggedToken> tagged = tagger.tokenizeAndTag(split[1]);
            
            for(TaggedToken taggedToken: tagged) {
                
                if(taggedToken.token.length() > 3 && sentiWordNet.containsWord(taggedToken.token.toLowerCase())) {
                    containsWordsInSentiWordNet = true;
//                    System.out.println(taggedToken.token.toLowerCase());
                    break;
                }
            }
            
            if(containsWordsInSentiWordNet) {
                fw.write(split[0]);
                fw.write("\t");
                fw.write(split[1]);
                fw.write("\r\n");
                linesKept++;
            }
            
            linesRead++;
        }
        
        System.out.println("Read " + linesRead);
        System.out.println("Kept " + linesKept);
        
        br.close();
        fw.flush();
        fw.close();
    }

}
