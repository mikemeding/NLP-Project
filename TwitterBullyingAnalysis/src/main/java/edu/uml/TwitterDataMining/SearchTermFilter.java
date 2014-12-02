/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.TwitterDataMining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mike
 */
public class SearchTermFilter {

	/**
	 * Gets the relative file path for the given system to the lexicon directory
	 *
	 * @return
	 */
	public static String getPathToLexicon() {
		// creates a relative file path into Corpus folder to store data
		Path path = Paths.get(new File("").getAbsolutePath());
		String separator = path.getFileSystem().getSeparator();
		return path.getParent().toString() + separator + "Corpus" + separator + "searchTermLexicon" + separator + "HashtagSentiment" + separator;
	}

	/**
	 * The file must be TSV in the format:
	 * term<tab>sentimentScore<tab>numPositive<tab>numNegative
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> parseSentimentLexicons(String path) throws IOException {
		List<String> words = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String line;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);

				line = line.trim();
				// TSV with 4 values
				String[] split = line.split("\t");

				// we only want strong negative words.
				if (Double.parseDouble(split[1]) <= -4.999) {
//				if (Double.parseDouble(split[1]) <= -5) {
					System.out.println(line);
					words.add(split[0]);
				}
			}
		}
		return words;
	}
}
