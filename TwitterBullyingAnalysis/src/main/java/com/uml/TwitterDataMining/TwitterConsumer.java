/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uml.TwitterDataMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 *
 * @author mike
 */
public class TwitterConsumer {

	/**
	 * FYI!!!!!!!!!!!!!!!!!!!!!! Requests to twitter are rate limited. Only 15 requests per registered app every 15 minutes.
	 */
	public static void main(String[] args) {
		//TODO: we should be thinking of antagonistic tweet searches to build our corpus
		String queryTerm = "#noob";

		// creates a relative file path into Corpus folder to store data
		String pathToCorpus = new File("").getAbsolutePath();
		String[] split = pathToCorpus.split("TwitterBullyingAnalysis");
		pathToCorpus = split[0];
		pathToCorpus = pathToCorpus.concat("/Corpus/" + queryTerm + ".txt");
		System.out.println(pathToCorpus);

		Twitter twitter = new TwitterFactory().getInstance();
		try {
			// query and save to file 
			queryTwitter(queryTerm, twitter, pathToCorpus);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.println("Corpus write failure");
			System.exit(-1);
		}
	}

	/**
	 * Search twitter with the given search term Results will be saved in a file
	 * with the same name as the query
	 *
	 * @param searchTerm The search term to be queried
	 * @param twitter The twitter factory from twitter4j
	 * @param filePath The fully qualified path to store the data if found
	 */
	public static void queryTwitter(String searchTerm, Twitter twitter, String filePath) throws TwitterException, IOException {
		Query query = new Query(searchTerm);
		QueryResult result;
		do {
			result = twitter.search(query);
			List<Status> tweets = result.getTweets();
			for (Status tweet : tweets) {
				System.out.println("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText());
			}
			saveResults(tweets, filePath);
		} while ((query = result.nextQuery()) != null); // for all pages of this occuring (CAN BE A LOT)	
	}

	/**
	 * Save the results that we got from queryTwitter method
	 *
	 * @param tweets
	 * @param filePath
	 */
	public static void saveResults(List<Status> tweets, String filePath) throws IOException {
		try (FileWriter fw = new FileWriter(filePath, true)) { // try with resources (will close file pointers)
			for (Status tweet : tweets) {
				fw.write("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText() + "\n");
			}
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}

}
