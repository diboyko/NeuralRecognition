package main;

import exceptions.ConfigNotLoadedException;
import exceptions.ImageReadingException;
import gui.MainWindow;
import network.NeuralNetwork;
import utility.Letters;
import utility.TestCaseLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static utility.NetworkStatus.ConfigurationLoaded;

/**
 * Created by Me on 04.06.2016.
 */
public class Controller {

    private static NeuralNetwork network;
    private static MainWindow mainWindow;

    public static NeuralNetwork getNetwork() {
        return network;
    }

    public static void main(String[] args) {
        network = new NeuralNetwork();
        mainWindow = new MainWindow();
    }

    public static void saveNetworkWeights() throws IOException, ConfigNotLoadedException {
        if (network.getNetworkStatus() != ConfigurationLoaded)
            throw new ConfigNotLoadedException("You choose config file in Configure menu before saving");
        network.saveWeightsToFile();
    }

    public static void restoreNetworkWeights(File path) throws ConfigNotLoadedException {
        try {
            network.loadWeightsFromFile(path);
        } catch (Exception e) {
            throw new ConfigNotLoadedException("Failed to load configuration");
        }
    }

    public static Letters identifyLetter(File letterPath) throws ImageReadingException, ConfigNotLoadedException {
        if (network.getNetworkStatus() != ConfigurationLoaded) {
            throw new ConfigNotLoadedException("Before recognizing a letter, please load configuration file or create new one " +
                    "with \"Configure network\" menu");
        }
        if (letterPath == null) {
            throw new ImageReadingException("No letter loaded. Please choose a letter first");
        }
        return network.identifyLetter(letterPath.getPath());
    }

    public static void trainWithLetter(String momentum, String learning, String letter) throws ImageReadingException, ConfigNotLoadedException {
        if (network.getNetworkStatus() != ConfigurationLoaded) {
            throw new ConfigNotLoadedException("Before recognizing a letter, please load configuration file or create new one " +
                    "with \"Configure network\" menu");
        }
        String inputPath = mainWindow.getLetterPath().getAbsolutePath();
        double momentumFactor;
        if (momentum.length() != 0) {
            momentumFactor = Double.parseDouble(momentum);
            if (momentumFactor > 1 || momentumFactor < 0) {
                throw new IllegalArgumentException("Wrong momentum");
            } else network.setMomentumFactor(momentumFactor);
        }
        double learningRate;
        if (learning.length() != 0) {
            learningRate = Double.parseDouble(learning);
            if (learningRate > 1 || learningRate < 0) {
                throw new IllegalArgumentException("Wrong learning rate!");
            } else network.setLearningRate(learningRate);
        }
        Character letterOutput = letter.toUpperCase().charAt(0);
        if (!Arrays.asList(Letters.Alphabet).contains(letter.toUpperCase()))
            throw new IllegalArgumentException("Wrong letter");
        network.recalculateNetworkWeightsAndValues(inputPath, letterOutput);
    }

    public static void loadTestCases(File testcasesFolder, int numberOfIterations) throws ImageReadingException {
        TestCaseLoader loader = new TestCaseLoader();
        HashMap<String, ArrayList<File>> testCases = loader.readCases(testcasesFolder, true);
        for (String testcasename : testCases.keySet()) {
            network.trainNetworkWithTestcase(testCases.get(testcasename), numberOfIterations);
        }
        network.setConfigFile(new File("config\\config.conf"));

    }

}
