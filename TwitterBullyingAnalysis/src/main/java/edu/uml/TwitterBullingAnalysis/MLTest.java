package edu.uml.TwitterBullingAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import cmu.arktweetnlp.Tagger;
import edu.uml.EmoticonSentiment.EmoticonFeature;
import edu.uml.EmoticonSentiment.EmoticonFeatureExtraction;
import edu.uml.HarvardInquirer.HarvardInquirer;
import edu.uml.HarvardInquirer.HarvardInquirerFeature;
import edu.uml.HarvardInquirer.HarvardInquirerFeatureExtraction;
import edu.uml.SentiWordNet.SentiWordNet;
import edu.uml.SentiWordNet.SentiWordNetFeature;
import edu.uml.SentiWordNet.SentiWordNetFeatureExtraction;

public class MLTest {

	public static final int NUMBER_OF_FEATURES = 23;
	public static final double TRAINING_PERCENTAGE = 0.8;

	public static final Random RANDOM = new Random(0);

	public static void main(String[] args) throws Exception {

		/**
		 * INITALIZE all feature sets with given models
		 */
		// emoticon lexicon extraction
		EmoticonFeatureExtraction efe = new EmoticonFeatureExtraction("emoticons.txt");
		EmoticonFeature emoticonFeature = new EmoticonFeature(efe);

		// ark tweet TOKENIZER and POS TAGGER
		Tagger tagger = new Tagger();
		tagger.loadModel("/cmu/arktweetnlp/model.20120919");

		// Senti word net sentiment model
		SentiWordNet sentiWordNet = new SentiWordNet("files/SentiWordNet_3.0.0_20130122.txt");
		SentiWordNetFeatureExtraction swnfe = new SentiWordNetFeatureExtraction(tagger,
				  sentiWordNet);

		// Harvard inquirer feature
		HarvardInquirer harvardInquirer = new HarvardInquirer("../Harvard_inquirer/inqtabs.txt");
		HarvardInquirerFeatureExtraction hife = new HarvardInquirerFeatureExtraction(tagger,
				  harvardInquirer);

		// Split our annotated corpus into test and train sets
		ArrayList<TwitterData> trainingTweets = new ArrayList<>();
		ArrayList<TwitterData> testingTweets = new ArrayList<>();

		List<TwitterData> allTweets = new ArrayList<>();
		allTweets = DataParser.parseData("../Corpus/gold/tweets-annotated.txt");
		splitData(allTweets, trainingTweets, testingTweets);

		// Set up machine learning
		BasicMLDataSet trainingData = new BasicMLDataSet();
		BasicMLDataSet testingData = new BasicMLDataSet();

		// Extact all our features from the given data
		extractFeatures(swnfe, hife, emoticonFeature, trainingTweets, trainingData);
		extractFeatures(swnfe, hife, emoticonFeature, testingTweets, testingData);

		int numberOfIterations = 10000;
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, NUMBER_OF_FEATURES));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, NUMBER_OF_FEATURES * 2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 1));
//        network.addLayer(new BasicLayer(new ActivationSoftMax(), true, 2));
		network.getStructure().finalizeStructure();
		network.reset();
		MLRegression mlRegression = network;
		MLTrain train = new ResilientPropagation(network, trainingData);

//         int numberOfIterations = 1;
//         SVM method = new SVM(NUMBER_OF_FEATURES, SVMType.EpsilonSupportVectorRegression,
//         KernelType.Linear);
//         MLRegression mlRegression = method;
//         MLTrain train = new SVMTrain(method, trainingData);
		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch <= numberOfIterations);
		train.finishTraining();

		System.out.println("Training Results: ");
		printAnalysis(mlRegression, trainingTweets, trainingData);

		System.out.println("------------------------------");

		System.out.println("Testing Results: ");
		printAnalysis(mlRegression, testingTweets, testingData);

		Encog.getInstance().shutdown();
	}

	public static void printAnalysis(MLRegression mlRegression, ArrayList<TwitterData> tweets,
			  BasicMLDataSet dataSet) {
		int correct = 0;
		double tp = 0;
		double fp = 0;
		double fn = 0;
		double tn = 0;

		// test the neural network
		for (int i = 0; i < dataSet.size(); i++) {

			MLDataPair pair = dataSet.get(i);
			MLData output = mlRegression.compute(pair.getInput());

			int ideal = (int) pair.getIdeal().getData(0);
			int actual = output.getData(0) > 0.5 ? 1 : 0;

			// System.out.println(tweets.get(i)[1]);
			// System.out.println("actual=" + output.getData(0) + ", ideal="+
			// pair.getIdeal().getData(0));
			if (actual == ideal) {
				correct++;
			}
			if (actual == 1 && ideal == 1) {
				tp += 1.0;
			}
			if (actual == 1 && ideal == 0) {
				fp += 1.0;
			}
			if (actual == 0 && ideal == 1) {
				fn += 1.0;
			}
			if (actual == 0 && ideal == 0) {
				tn += 1.0;
			}
		}

		System.out.println("tp = " + tp + ", tn = " + tn + ", fp = " + fp + ", fn = " + fn);

		double precision = (tp / (tp + fp));
		double recall = (tp / (tp + fn));

		System.out.println(correct + " of " + dataSet.size());
		System.out.println("precision: " + precision);
		System.out.println("recall: " + recall);
	}

	public static void splitData(List<TwitterData> allTweets, List<TwitterData> trainingTweets,
			  List<TwitterData> testingTweets) {

		ArrayList<TwitterData> bullyingTweets = new ArrayList<>();
		ArrayList<TwitterData> nonBullyingTweets = new ArrayList<>();

		for (TwitterData twitterData : allTweets) {
			if (twitterData.isBullying()) {
				bullyingTweets.add(twitterData);
			} else {
				nonBullyingTweets.add(twitterData);
			}
		}

		Collections.shuffle(bullyingTweets, RANDOM);
		Collections.shuffle(nonBullyingTweets, RANDOM);

		int numberOfBullyingTweetsForTraining = (int) Math.floor(TRAINING_PERCENTAGE
				  * bullyingTweets.size());
		int numberOfNonBullyingTweetsForTraining = (int) Math.floor(TRAINING_PERCENTAGE
				  * nonBullyingTweets.size());

//        int numberOfNonBullyingTweetsForTraining = numberOfBullyingTweetsForTraining;
		for (int i = 0; i < numberOfBullyingTweetsForTraining; i++) {
			trainingTweets.add(bullyingTweets.remove(0));
		}

		for (int i = 0; i < numberOfNonBullyingTweetsForTraining; i++) {
			trainingTweets.add(nonBullyingTweets.remove(0));
		}

		testingTweets.addAll(bullyingTweets);
		testingTweets.addAll(nonBullyingTweets);

		Collections.shuffle(trainingTweets, RANDOM);
		Collections.shuffle(testingTweets, RANDOM);
	}

	public static void extractFeatures(SentiWordNetFeatureExtraction swnfe,
			  HarvardInquirerFeatureExtraction hife, EmoticonFeature ef, ArrayList<TwitterData> tweets,
			  BasicMLDataSet dataSet) {

		for (TwitterData tweetData : tweets) {
			String tweet = tweetData.getTweet();

			SentiWordNetFeature sentiWordNetFeature = swnfe.extractFeatures(tweet);
			HarvardInquirerFeature harvardInquirerFeature = hife.extractFeatures(tweet);
			double[] feature = new double[NUMBER_OF_FEATURES];

			feature[0] = sentiWordNetFeature.getAveragePositiveScore();
			feature[1] = sentiWordNetFeature.getAverageNegativeScore();

			feature[2] = sentiWordNetFeature.getNonZeroAveragePositiveScore();
			feature[3] = sentiWordNetFeature.getNonZeroAverageNegativeScore();

			feature[4] = sentiWordNetFeature.getNonZeroAveragePositiveAdjectiveScore();
			feature[5] = sentiWordNetFeature.getNonZeroAverageNegativeAdjectiveScore();

			feature[6] = sentiWordNetFeature.getNonZeroNegativeCount() > sentiWordNetFeature
					  .getNonZeroPositiveCount() ? 1.0 : 0.0;
			feature[7] = sentiWordNetFeature.getNonZeroNegativeAdjectiveCount() > sentiWordNetFeature
					  .getNonZeroPositiveAdjectiveCount() ? 1.0 : 0.0;

			feature[8] = harvardInquirerFeature.isContainsPositiveWord() ? 1.0 : 0.0;
			feature[9] = harvardInquirerFeature.isContainsNegativeWord() ? 1.0 : 0.0;
			feature[10] = harvardInquirerFeature.isContainsHostileWord() ? 1.0 : 0.0;
			feature[11] = harvardInquirerFeature.isContainsFailWord() ? 1.0 : 0.0;
			feature[12] = harvardInquirerFeature.isContainsActiveWord() ? 1.0 : 0.0;
			feature[13] = harvardInquirerFeature.isContainsPassiveWord() ? 1.0 : 0.0;
			feature[14] = harvardInquirerFeature.isContainsPleasureWord() ? 1.0 : 0.0;
			feature[15] = harvardInquirerFeature.isContainsPainWord() ? 1.0 : 0.0;
			feature[16] = harvardInquirerFeature.isContainsStrongWord() ? 1.0 : 0.0;
			feature[17] = harvardInquirerFeature.isContainsWeakWord() ? 1.0 : 0.0;
			feature[18] = harvardInquirerFeature.isContainsVirtueWord() ? 1.0 : 0.0;
			feature[19] = harvardInquirerFeature.isContainsViceWord() ? 1.0 : 0.0;
			feature[20] = ef.getNegativeEmoticonScore(tweetData);
			feature[21] = ef.getNeutralEmoticonScore(tweetData);
			feature[22] = ef.getPositiveEmoticonScore(tweetData);

			double[] labelArray = new double[1];
			if (tweetData.isBullying()) {
				labelArray[0] = 1.0;
			}
//            else {
//                labelArray[1] = 1.0;
//            }

			dataSet.add(new BasicMLData(feature), new BasicMLData(labelArray));
		}
	}
}
