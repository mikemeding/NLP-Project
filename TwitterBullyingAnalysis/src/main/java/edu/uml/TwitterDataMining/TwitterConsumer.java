/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.TwitterDataMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 *
 * @author mike
 */
public class TwitterConsumer {

	//TODO: we should be thinking of antagonistic tweet searches to build our corpus
	static String[] queryTerms = {"#WTF", "#noob", "#sucks", "#crappy", "#shit", "#ego", "#immature"};

	/**
	 * FYI!!!!!!!!!!!!!!!!!!!!!! Requests to twitter are rate limited. Only 15
	 * requests per registered app every 15 minutes.
	 */
	public static void main(String[] args) {
		Twitter twitter = new TwitterFactory().getInstance(); // Huge...

		for (String queryTerm : queryTerms) {
			String pathToCorpus = getPathToCorpus();
			pathToCorpus = pathToCorpus.concat(queryTerm + ".txt");

			// query and save to file 
			queryTwitter(queryTerm, twitter, pathToCorpus);

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
	public static void queryTwitter(String searchTerm, Twitter twitter, String filePath) {
		Query query = new Query(searchTerm);
		query.setLang("en"); // we only want english tweets
		// get and format date to get tweets no more than a year old
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(System.currentTimeMillis() - (long) (365 * 24 * 60 * 60 * 1000)));
		query.setSince(date);// since january last year
		QueryResult result;

		int queryCount = 0;

		try {
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText());
				}
				saveResults(tweets, filePath);

				// we only want 5 quires for now
				if (queryCount > 5) {
					queryCount = 0;
					break;
				} else {
					queryCount++;
				}
			} while ((query = result.nextQuery()) != null); // for all pages of this occuring (CAN BE A LOT)	

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

	}

	/**
	 * Search a specific user name and get their most recent
	 *
	 * @param searchUser
	 * @param twitter
	 * @param filePath
	 * @throws TwitterException
	 * @throws IOException
	 */
	public static void queryTwitterUser(String searchUser, Twitter twitter, String filePath) throws TwitterException, IOException {
		Query query = new Query(searchUser);
		query.setLang("en"); // we only want english tweets
		QueryResult result;

		do {
			result = twitter.search(query);
			List<Status> tweets = result.getTweets();
			List<Status> usableTweets = new ArrayList<>();
			for (Status tweet : tweets) {
				if (tweet.getUser().getScreenName().equals(searchUser)) {

					System.out.println("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText());
					usableTweets.add(tweet);
				}
			}

			if (usableTweets.size() > 0) {
				saveResults(usableTweets, filePath);
			}

		} while ((query = result.nextQuery()) != null); // for all pages of this occuring (CAN BE A LOT)	
	}

	/**
	 * Save the results that we got from queryTwitter method
	 *
	 * @param tweets
	 * @param filePath
	 */
	public static void saveResults(List<Status> tweets, String filePath) {
		try (FileWriter fw = new FileWriter(filePath, true)) { // try with resources (will close file pointers)
			for (Status tweet : tweets) {
				// user filter for checking only their tweets 
//				if (tweet.getUser().getScreenName().equals(queryTerms[0])) {
				fw.write("@" + tweet.getUser().getScreenName() + "\t" + tweet.getText() + "\n");
//				}
			}
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}

	public static String getPathToCorpus() {
		// creates a relative file path into Corpus folder to store data
		String path = new File("").getAbsolutePath();
		String[] split = path.split("TwitterBullyingAnalysis");
		path = split[0];
		path = path.concat("/Corpus/Untagged/");
		return path;
	}

}
