/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.EmoticonSentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 *
 * @author mike
 */
public class EmoticonFeatureExtraction {

	public HashMap<String, String> positiveEmoticons = new HashMap<>();
	public HashMap<String, String> negativeEmoticons = new HashMap<>();
	public HashMap<String, String> neutralEmoticons = new HashMap<>();
	private static String path;

	public EmoticonFeatureExtraction(String filename) {
		try {
			EmoticonFeatureExtraction.path = getPathToLexicon(filename);
			this.positiveEmoticons = extractMatchingEmoticons("positive");
			this.negativeEmoticons = extractMatchingEmoticons("negative");
			this.neutralEmoticons = extractMatchingEmoticons("neutral");

		} catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.exit(-1);
		}
	}

	/**
	 * Gets the relative file path for the given system to a file in the lexicon
	 * directory. Also checks if the file exists.
	 *
	 * @throws IOException if the file does not exist
	 * @param filename the filename to search for
	 * @return the absolute file path of the given file
	 */
	private static String getPathToLexicon(String filename) throws IOException {
		// creates a relative file path into Corpus folder to store data
		Path testPath = Paths.get(new File("").getAbsolutePath());
		String separator = testPath.getFileSystem().getSeparator();
		String lexiconPath = testPath.getParent().toString() + separator + "Corpus" + separator
				  + "searchTermLexicon" + separator;
		// check to see if file exists
		// http://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-java-on-windows
		File f = new File(lexiconPath + filename);
		if (f.exists() && !f.isDirectory()) {
			return lexiconPath.concat(filename);
		} else {
			throw new IOException("Invalid file name given. (Does not exist)");
		}
	}

	/**
	 * Searches the file described by path for the given senti tag
	 *
	 * @param sentiment the sentiment tag you are looking for (positive,negative)
	 * @return A hashmap of the matching tags found
	 * @throws IOException
	 */
	public static HashMap<String, String> extractMatchingEmoticons(String sentiment) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		HashMap<String, String> emoticonMap = new HashMap<>();
		String line;
		while ((line = br.readLine()) != null) {
			// process the line.
			String[] TSline = line.split("\t");
			// check TSV format
			if (TSline.length != 2) {
				throw new IOException("Invalid file format. Must be TSV <emoticons>tab<senti value>");
			}
			// we only want the lines that match the sentiment we are looking for
			if (TSline[1].equals(sentiment)) {
				String[] emoticons = TSline[0].split(" "); // individual emoticons are separated by space
				for (String emoj : emoticons) {
					emoticonMap.put(emoj, sentiment);
				}
			}
		}
		if (emoticonMap.isEmpty()) {
			throw new IOException("No elements found with that senti tag");
		}
		return emoticonMap;
	}

}
