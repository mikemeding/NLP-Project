/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.TwitterDataMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	static List<String> queryTerms;
	static Twitter twitter = new TwitterFactory().getInstance();

	/**
	 * FYI!!!!!!!!!!!!!!!!!!!!!! Requests to twitter are rate limited. Only 180
	 * requests per registered app every 15 minutes.
	 */
	public static void main(String[] args) {
		// query for a specific user example
//		String path = getPathToCorpus();
//		System.out.println(path);
//		queryTwitterUser("@ActuallyNPH", getPathToCorpus().concat("test.txt"));

		// my custom terms
		List<String> myTerms = new ArrayList<>();
		myTerms.add("#WTF");
		myTerms.add("#noob");
		myTerms.add("#sucks");
		myTerms.add("#shit");
		myTerms.add("#immature");
		myTerms.add("#loser");
		myTerms.add("#hater");
		myTerms.add("#douchebag");

		try {
			// Parse lexicon file for bad sentiment hashtags
			String lexiconPath = SearchTermFilter.getPathToLexicon();
			lexiconPath = lexiconPath.concat("unigrams-pmilexicon.txt");
			queryTerms = SearchTermFilter.parseSentimentLexicons(lexiconPath);

			// Add my own terms if they do not exist
			for (String myTerm : myTerms) {
				if (!queryTerms.contains(myTerm)) {
					queryTerms.add(myTerm);
				}
			}
			// Query each of the terms
			int queryCount = 0;
			List<Status> tweets = null;
			for (String term : queryTerms) {
				queryCount++;
				System.out.println("Searching: " + term);
				System.out.println("Query Count: " + queryCount + " of " + queryTerms.size());
				tweets = queryTwitter(term); // Creates an array of tweets 
				// resolve null pointer issue (cause: rate timeout error)
				if (tweets != null) {
					// Remove retweets.
					List<Status> retweets = new ArrayList<>();
					for (Status tweet : tweets) {
						if (tweet.isRetweet()) {
							retweets.add(tweet);
						}
					}
					tweets.removeAll(retweets);
					saveResults(tweets, getPathToCorpus().concat("rawTweets.txt")); // Save array of tweets
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Gets the relative file path for the given system to the corpus directory
	 * Will return null pointer if no tweets are found or if a rate limiting
	 * error occurs
	 *
	 * @return
	 */
	public static String getPathToCorpus() {
		// creates a relative file path into Corpus folder to store data
		Path path = Paths.get(new File("").getAbsolutePath());
		String separator = path.getFileSystem().getSeparator();
		return path.getParent().toString() + separator + "Corpus" + separator;
	}

	/**
	 * Search twitter with the given search term Results will be saved in a file
	 * with the same name as the query
	 *
	 * @param searchTerm The search term to be queried
	 * @return a list of tweets
	 */
	public static List<Status> queryTwitter(String searchTerm) {
		Query query = new Query(searchTerm);
		query.setLang("en"); // we only want english tweets
		// get and format date to get tweets no more than a year old
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(System.currentTimeMillis() - (long) (365 * 24 * 60 * 60 * 1000)));
		query.setSince(date);
		QueryResult result;
		List<Status> tweets = null;

		try {
			//ONLY GETTING FIRST SET OF RESULTS
			result = twitter.search(query);
			tweets = result.getTweets();
//			for (Status tweet : tweets) {
//				System.out.println("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText());
//			}

			// Wait loop for reset
		} catch (TwitterException te) {
			try { // try block for sleep thread
				if (!te.isCausedByNetworkIssue()) {
					// Not really checking for anything else but it is likely that we are out of requests
					int resetTime = te.getRateLimitStatus().getSecondsUntilReset();

					while (resetTime > 0) {
						Thread.sleep(1000); // 1 second stop
						System.out.println("seconds till reset: " + resetTime);
						--resetTime;
					}
				} else {
					te.printStackTrace();
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				System.exit(-1);
			}

		}
		return tweets;
	}

	/**
	 * Search a specific user name and get their most recent tweets. DO NOT
	 * INCLUDE THE @ SYMBOL BEFORE USERNAME
	 *
	 * @param Username
	 * @return a list of tweets
	 */
	public static List<Status> queryTwitterUser(String Username) {
		List<Status> tweets = null;

		try {

			tweets = twitter.getUserTimeline(Username);
//			for (Status tweet : tweets) {
//				System.out.println("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText());
//			}

			// Wait loop for reset
		} catch (TwitterException te) {
			try { // try block for sleep thread
				if (!te.isCausedByNetworkIssue()) {
					// Not really checking for anything else but it is likely that we are out of requests
					int resetTime = te.getRateLimitStatus().getSecondsUntilReset();

					while (resetTime > 0) {
						Thread.sleep(1000); // 1 second stop
						System.out.println("seconds till reset: " + resetTime);
						--resetTime;
					}
				} else {
					te.printStackTrace();
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				System.exit(-1);
			}

		}
		return tweets;
	}

	/**
	 * Save the results that we got from queryTwitter method This will append to
	 * a file if it already exists
	 *
	 * @param tweets
	 * @param filePath
	 */
	public static void saveResults(List<Status> tweets, String filePath) {
		try (FileWriter fw = new FileWriter(filePath, true)) { // try with resources (will close file pointers)
			for (Status tweet : tweets) {
				fw.write("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText() + "\n");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
