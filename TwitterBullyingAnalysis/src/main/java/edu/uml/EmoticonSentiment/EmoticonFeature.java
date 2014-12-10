/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.EmoticonSentiment;

import edu.uml.TwitterBullingAnalysis.TwitterData;

/**
 *
 * @author mike
 */
public class EmoticonFeature {

	private EmoticonFeatureExtraction efe;

	public EmoticonFeature(EmoticonFeatureExtraction efe) {
		this.efe = efe;
	}

	/**
	 * Scans a tweet and returns the number of negative emoticons.
	 *
	 * @param tweet the tweet to scan
	 * @return returns 0 if no emoticons are found
	 */
	public int getNegativeEmoticonScore(TwitterData tweet) {
		int score = 0;
		for (String emoticon : efe.negativeEmoticons.keySet()) {
			if (tweet.getTweet().contains(emoticon)) {
//				score++;
			    return 1;
			}
		}
		return score;
	}
	/**
	 * Scans a tweet and returns the number of neutral emoticons.
	 *
	 * @param tweet the tweet to scan
	 * @return returns 0 if no emoticons are found
	 */
	public int getNeutralEmoticonScore(TwitterData tweet) {
		int score = 0;
		for (String emoticon : efe.neutralEmoticons.keySet()) {
			if (tweet.getTweet().contains(emoticon)) {
//				score++;
			    return 1;
			}
		}
		return score;
	}
	/**
	 * Scans a tweet and returns the number of positive emoticons.
	 *
	 * @param tweet the tweet to scan
	 * @return returns 0 if no emoticons are found
	 */
	public int getPositiveEmoticonScore(TwitterData tweet) {
		int score = 0;
		for (String emoticon : efe.positiveEmoticons.keySet()) {
			if (tweet.getTweet().contains(emoticon)) {
//				score++;
			    return 1;
			}
		}
		return score;
	}


}
