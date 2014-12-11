package edu.uml.TwitterBullingAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
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
import edu.uml.ArkTweetNLP.CommonTagPatternExtractor;
import edu.uml.EmoticonSentiment.EmoticonFeature;
import edu.uml.EmoticonSentiment.EmoticonFeatureExtraction;
import edu.uml.HarvardInquirer.HarvardInquirer;
import edu.uml.HarvardInquirer.HarvardInquirerFeature;
import edu.uml.HarvardInquirer.HarvardInquirerFeatureExtraction;
import edu.uml.SentiWordNet.SentiWordNet;
import edu.uml.SentiWordNet.SentiWordNetFeature;
import edu.uml.SentiWordNet.SentiWordNetFeatureExtraction;
import edu.uml.mpqa.SubjectiveLexicon.SubjectiveLexicon;
import edu.uml.mpqa.SubjectiveLexicon.SubjectiveLexiconFeature;
import edu.uml.mpqa.SubjectiveLexicon.SubjectiveLexiconFeatureExtractor;

public class MLTest {

    public static final int NUMBER_OF_FEATURES = 34 + 65;
    public static final int NUMBER_OF_HIDDEN_UNITS = NUMBER_OF_FEATURES * 2;
//    public static final int NUMBER_OF_HIDDEN_UNITS = 100;
    public static final int NUMBER_OF_OUTPUTS = 2;
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

        SubjectiveLexicon subjectiveLexicon = new SubjectiveLexicon(
                "../subjectivity_clues_hltemnlp05/subjclueslen1-HLTEMNLP05.tff");
        SubjectiveLexiconFeatureExtractor slfe = new SubjectiveLexiconFeatureExtractor(tagger,
                subjectiveLexicon);

        CommonTagPatternExtractor ctpe = new CommonTagPatternExtractor(tagger);

        // Split our annotated corpus into test and train sets
        ArrayList<TwitterData> trainingTweets = new ArrayList<>();
        ArrayList<TwitterData> testingTweets = new ArrayList<>();

        // Parse all data sets
        List<TwitterData> allTweets = new ArrayList<>();
        allTweets.addAll(DataParser.parseData("../Corpus/gold/tweets-annotated-large.txt"));
        allTweets.addAll(DataParser.parseData("../Corpus/gold/tweets-annotated.txt"));
        allTweets.addAll(DataParser.parseData("../Corpus/gold/bullying-tweets.txt"));
        removeDuplicateTweets(allTweets);
        
        splitData(allTweets, trainingTweets, testingTweets);

        // Set up machine learning
        BasicMLDataSet trainingData = new BasicMLDataSet();
        BasicMLDataSet testingData = new BasicMLDataSet();

        // Extract all our features from the given data
        extractFeatures(swnfe, hife, emoticonFeature, slfe, ctpe, trainingTweets, trainingData);
        extractFeatures(swnfe, hife, emoticonFeature, slfe, ctpe, testingTweets, testingData);

        int numberOfIterations = 10000;
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, NUMBER_OF_FEATURES));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, NUMBER_OF_HIDDEN_UNITS));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, NUMBER_OF_HIDDEN_UNITS));
//        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, NUMBER_OF_OUTPUTS));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), true, NUMBER_OF_OUTPUTS));
        network.getStructure().finalizeStructure();
        network.reset();
        MLRegression mlRegression = network;
        MLTrain train = new ResilientPropagation(network, trainingData);

        // int numberOfIterations = 1;
        // SVM method = new SVM(NUMBER_OF_FEATURES, SVMType.EpsilonSupportVectorRegression,
        // KernelType.Linear);
        // MLRegression mlRegression = method;
        // MLTrain train = new SVMTrain(method, trainingData);
        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error: " + train.getError());
            epoch++;
        } while (train.getError() > 0.0001 && epoch <= numberOfIterations);
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

            if (actual == ideal) {
                correct++;
            }
            if (actual == 1 && ideal == 1) {
                tp += 1.0;
            }
            if (actual == 1 && ideal == 0) {
                fp += 1.0;
//                System.out.println("fp \t\t" + tweets.get(i).getTweet());
            }
            if (actual == 0 && ideal == 1) {
                fn += 1.0;
//                System.out.println("fn \t\t" + tweets.get(i).getTweet());
            }
            if (actual == 0 && ideal == 0) {
                tn += 1.0;
            }
        }

        System.out.println("tp = " + tp + ", tn = " + tn + ", fp = " + fp + ", fn = " + fn);

        double precision = (tp / (tp + fp));
        double recall = (tp / (tp + fn));

        System.out.println(correct + " of " + dataSet.size() + " correctly tagged");
        System.out.println("precision: " + precision);
        System.out.println("recall: " + recall);
    }
    
    public static void removeDuplicateTweets(List<TwitterData> tweets) {
        
        HashSet<String> uniqueTweetSet = new HashSet<>();
        ArrayList<TwitterData> newList = new ArrayList<>();

        int count = 0;
        
        for(TwitterData twitterData: tweets) {
            if(!uniqueTweetSet.contains(twitterData.getTweet())) {
                uniqueTweetSet.add(twitterData.getTweet());
                newList.add(twitterData);
            } else {
                count++;
            }
        }
        
        tweets.clear();
        tweets.addAll(newList);
        
        System.out.println("Removed " + count + " duplicates");
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

        // int numberOfNonBullyingTweetsForTraining = numberOfBullyingTweetsForTraining;
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
            HarvardInquirerFeatureExtraction hife, EmoticonFeature ef,
            SubjectiveLexiconFeatureExtractor slfe, CommonTagPatternExtractor ctpe,
            ArrayList<TwitterData> tweets, BasicMLDataSet dataSet) {

        for (TwitterData tweetData : tweets) {
            String tweet = tweetData.getTweet();

            SentiWordNetFeature sentiWordNetFeature = swnfe.extractFeatures(tweet);
            HarvardInquirerFeature harvardInquirerFeature = hife.extractFeatures(tweet);
            SubjectiveLexiconFeature subjectiveLexiconFeature = slfe.extractFeatures(tweet);
            double[] matchedPatterns = ctpe.extractFeatures(tweet);

            double[] feature = new double[NUMBER_OF_FEATURES];
            int featureIndex = 0;

            feature[featureIndex++] = sentiWordNetFeature.getAveragePositiveScore();
            feature[featureIndex++] = sentiWordNetFeature.getAverageNegativeScore();
            feature[featureIndex++] = sentiWordNetFeature.getAverageObjectiveScore();

            feature[featureIndex++] = sentiWordNetFeature.getNonZeroAveragePositiveScore();
            feature[featureIndex++] = sentiWordNetFeature.getNonZeroAverageNegativeScore();
            feature[featureIndex++] = sentiWordNetFeature.getNonZeroAverageObjectiveScore();

            feature[featureIndex++] = sentiWordNetFeature.getNonZeroAveragePositiveAdjectiveScore();
            feature[featureIndex++] = sentiWordNetFeature.getNonZeroAverageNegativeAdjectiveScore();
            feature[featureIndex++] = sentiWordNetFeature
                    .getNonZeroAverageObjectiveAdjectiveScore();

            feature[featureIndex++] = sentiWordNetFeature.getNonZeroNegativeCount() > sentiWordNetFeature
                    .getNonZeroPositiveCount() ? 1.0 : 0.0;
            feature[featureIndex++] = sentiWordNetFeature.getNonZeroNegativeAdjectiveCount() > sentiWordNetFeature
                    .getNonZeroPositiveAdjectiveCount() ? 1.0 : 0.0;

            feature[featureIndex++] = harvardInquirerFeature.isContainsPositiveWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsNegativeWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsHostileWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsFailWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsActiveWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsPassiveWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsPleasureWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsPainWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsStrongWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsWeakWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsVirtueWord() ? 1.0 : 0.0;
            feature[featureIndex++] = harvardInquirerFeature.isContainsViceWord() ? 1.0 : 0.0;

            feature[featureIndex++] = ef.getNegativeEmoticonScore(tweetData);
            feature[featureIndex++] = ef.getNeutralEmoticonScore(tweetData);
            feature[featureIndex++] = ef.getPositiveEmoticonScore(tweetData);

            feature[featureIndex++] = subjectiveLexiconFeature.isContainsStrongPositive() ? 1.0
                    : 0.0;
            feature[featureIndex++] = subjectiveLexiconFeature.isContainsStrongNegative() ? 1.0
                    : 0.0;
            feature[featureIndex++] = subjectiveLexiconFeature.isContainsWeakPositive() ? 1.0 : 0.0;
            feature[featureIndex++] = subjectiveLexiconFeature.isContainsWeakNegative() ? 1.0 : 0.0;
            feature[featureIndex++] = subjectiveLexiconFeature.isContainsStrongPositiveAdjective() ? 1.0
                    : 0.0;
            feature[featureIndex++] = subjectiveLexiconFeature.isContainsStrongNegativeAdjective() ? 1.0
                    : 0.0;
            feature[featureIndex++] = subjectiveLexiconFeature.isContainsWeakPositiveAdjective() ? 1.0
                    : 0.0;
            feature[featureIndex++] = subjectiveLexiconFeature.isContainsWeakNegativeAdjective() ? 1.0
                    : 0.0;
            
            for(double flag: matchedPatterns) {
                feature[featureIndex++] = flag;
            }

            double[] labelArray = new double[NUMBER_OF_OUTPUTS];
            if (tweetData.isBullying()) {
                labelArray[0] = 1.0;
            } else if (!tweetData.isBullying() && NUMBER_OF_OUTPUTS > 1) {
                labelArray[1] = 1.0;
            }

            dataSet.add(new BasicMLData(feature), new BasicMLData(labelArray));
        }
    }
}
