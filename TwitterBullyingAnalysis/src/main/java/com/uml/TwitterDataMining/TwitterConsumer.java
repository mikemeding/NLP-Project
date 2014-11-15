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
	 * FYI!!!!!!!!!!!!!!!!!!!!!! Requests to twitter are rate limited. Only 15
	 * requests per registered app every 15 minutes.
	 */
	public static void main(String[] args) {
		Twitter twitter = new TwitterFactory().getInstance(); // Huge...

		//TODO: we should be thinking of antagonistic tweet searches to build our corpus
		String[] queryTerms = {"#WTF", "#noob", "#sucks", "#crappy", "#shit"}; // generally negative sentiment.

		// creates a relative file path into Corpus folder to store data
		String pathToCorpus = new File("").getAbsolutePath();
		String[] split = pathToCorpus.split("TwitterBullyingAnalysis");
		pathToCorpus = split[0];
		pathToCorpus = pathToCorpus.concat("/Corpus/" + "negativeTweets" + ".txt");
		System.out.println(pathToCorpus);

		for (String queryTerm : queryTerms) {
			try {
				// query and save to file 
				queryTwitter(queryTerm, twitter, pathToCorpus);
			} catch (TwitterException te) { // if we encounter an error with twitter
				try {
					// Not really checking for anything else but it is likely that we are out of requests
					int resetTime = te.getRateLimitStatus().getSecondsUntilReset();

					while (resetTime > 0) {
						Thread.sleep(1000); // 1 second stop
						System.out.println("seconds till reset: " + resetTime);
						--resetTime;
					}
				} catch (InterruptedException ie) {
					ie.printStackTrace();
					System.exit(-1);
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.err.println("Corpus write failure");
				System.exit(-1);
			}
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
		query.setLang("en"); // we only want english tweets
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
