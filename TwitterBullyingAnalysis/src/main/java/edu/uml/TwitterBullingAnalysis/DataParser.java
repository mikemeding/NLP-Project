package edu.uml.TwitterBullingAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataParser {

	public static void parseData(String fileName, ArrayList<TwitterData> bullyingTweets,
			  ArrayList<TwitterData> nonBullyingTweets) throws IOException {

		for (TwitterData twitterData : parseData(fileName)) {
			if (twitterData.isBullying()) {
				bullyingTweets.add(twitterData);
			} else {
				nonBullyingTweets.add(twitterData);
			}
		}
	}

	public static List<TwitterData> parseData(String fileName) throws IOException {

		ArrayList<TwitterData> data = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		int lineNumber = 1;
		while ((line = br.readLine()) != null) {
			lineNumber++;

			line = line.trim();

			if (line.length() == 0) {
				continue;
			}
			if (line.charAt(0) == '#') {
				continue;
			}

			String[] split = line.split("\t");

			if (split.length < 3) {
				continue;
			}

			boolean bullying = false;

			if (split[2].equals("bullying")) {
				bullying = true;
				data.add(new TwitterData(split[0], split[1], bullying));
			} else if (split[2].equals("not_bullying")) {
				data.add(new TwitterData(split[0], split[1], bullying));
			} else {
				System.out.println(String.format("Dropped \"%s\" because \"%s\" is not a valid flag", split[1], split[2]) + " Line Number: " + lineNumber);
			}
		}

		br.close();

		return data;
	}
}
