package network;

import exceptions.ConfigNotLoadedException;
import exceptions.ImageReadingException;
import utility.Letters;
import utility.ImageReader;
import utility.NetworkStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import static utility.NetworkStatus.NoConfigurationLoaded;
import static utility.NetworkStatus.ConfigurationLoaded;

public class NeuralNetwork {

    private final int LETTERSAMOUNT = 26;
    private final int HIDDENLAYERNODESAMOUNT = 26;
    private final int IMAGERESOLUTION = 32 * 32;
    private double momentumFactor = 0.15;
    private double learningRate = 0.2;

    private File configFile;
    private NetworkStatus networkStatus;
    private double[] inputValues;
    private double[] outputLayerNodes;
    private double[] hiddenLayerNodes, outputLayerBiases, hiddenLayerBiases;
    private double[] outputLayerBiasChange, hiddenLayerBiasChange;
    private double[] outputLayerError, hiddenLayerError;
    private double[][] inputLayerWeights, hiddenLayerWeights, inputLayerWeightChange, hiddenLayerWeightChange;


    public double getMomentumFactor() {
        return momentumFactor;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setMomentumFactor(double momentumFactor) {
        this.momentumFactor = momentumFactor;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    private void setInputValues(double[] inputValues) {
        this.inputValues = inputValues;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }

    public NetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    public NeuralNetwork() {
        initializeNetwork();
    }

    private void initializeNetwork() {
        initializeNetworkArrays();
        fillNetworkArraysWithRandoms();
    }

    private void initializeNetworkArrays() {
        inputValues = new double[IMAGERESOLUTION];
        hiddenLayerNodes = new double[HIDDENLAYERNODESAMOUNT];
        outputLayerNodes = new double[LETTERSAMOUNT];
        inputLayerWeights = new double[inputValues.length][hiddenLayerNodes.length];
        hiddenLayerWeights = new double[hiddenLayerNodes.length][outputLayerNodes.length];
        inputLayerWeightChange = new double[inputValues.length][hiddenLayerNodes.length];
        hiddenLayerWeightChange = new double[hiddenLayerNodes.length][outputLayerNodes.length];
        hiddenLayerBiases = new double[hiddenLayerNodes.length];
        outputLayerBiases = new double[outputLayerNodes.length];
        hiddenLayerBiasChange = new double[hiddenLayerNodes.length];
        outputLayerBiasChange = new double[outputLayerNodes.length];
        outputLayerError = new double[outputLayerNodes.length];
        hiddenLayerError = new double[hiddenLayerNodes.length];
    }

    private void fillNetworkArraysWithRandoms() {
        Random random = new Random(System.currentTimeMillis());
        for (int j = 0; j < hiddenLayerNodes.length; j++) {
            for (int i = 0; i < inputValues.length; i++)
                inputLayerWeights[i][j] = random.nextGaussian() * .01;
            hiddenLayerBiases[j] = random.nextGaussian() * .01;
        }
        for (int j = 0; j < outputLayerNodes.length; j++) {
            for (int i = 0; i < hiddenLayerNodes.length; i++)
                hiddenLayerWeights[i][j] = random.nextGaussian() * .01;
            outputLayerBiases[j] = random.nextGaussian() * .01;
        }
    }

    private double functionToCalculateOutputs(double input) {
        return 1.0 / (1.0 + Math.exp(-input));
    }

    private double functionToCalculateOutputsDerivative(double input) {
        return Math.exp(input) / Math.pow((1.0 + Math.exp(input)), 2);
    }

    private void calculateNetwokValues() {
        for (int j = 0; j < hiddenLayerNodes.length; j++) {
            double sum = 0;
            for (int i = 0; i < inputValues.length; i++)
                sum += inputValues[i] * inputLayerWeights[i][j];

            hiddenLayerNodes[j] = functionToCalculateOutputs(sum + hiddenLayerBiases[j]);
        }
        for (int j = 0; j < outputLayerNodes.length; j++) {
            double sum = 0;
            for (int i = 0; i < hiddenLayerNodes.length; i++)
                sum += hiddenLayerNodes[i] * hiddenLayerWeights[i][j];

            outputLayerNodes[j] = functionToCalculateOutputs(sum + outputLayerBiases[j]);
        }
    }

    private double calculateNetworkErrors(double[] desiredOutput) {

        double rmse = 0;
        for (int j = 0; j < outputLayerNodes.length; j++) {
            double diff = desiredOutput[j] - outputLayerNodes[j];
            outputLayerError[j] = diff * functionToCalculateOutputsDerivative(outputLayerNodes[j]);
            rmse += diff * diff;
        }
        rmse = Math.sqrt(rmse / outputLayerNodes.length);
        for (int i = 0; i < hiddenLayerNodes.length; i++) {
            double sum = 0;
            for (int j = 0; j < outputLayerNodes.length; j++)
                sum += hiddenLayerWeights[i][j] * outputLayerError[j];

            hiddenLayerError[i] = functionToCalculateOutputsDerivative(hiddenLayerNodes[i]) * sum;
        }
        return rmse;
    }

    private void adjustNetworkWeights() {

        for (int i = 0; i < hiddenLayerNodes.length; i++) {
            for (int j = 0; j < outputLayerNodes.length; j++) {
                double weight = outputLayerError[j] * hiddenLayerNodes[i] * learningRate;
                hiddenLayerWeights[i][j] += momentumFactor * hiddenLayerWeightChange[i][j] + weight;
                hiddenLayerWeightChange[i][j] = weight;
            }
        }
        for (int i = 0; i < outputLayerNodes.length; i++) {
            double weight = outputLayerError[i] * outputLayerBiases[i] * learningRate;
            outputLayerBiases[i] += momentumFactor * outputLayerBiasChange[i] + weight;
            outputLayerBiasChange[i] = weight;
        }
        for (int i = 0; i < inputValues.length; i++) {
            for (int j = 0; j < hiddenLayerNodes.length; j++) {
                double weight = hiddenLayerError[j] * inputValues[i] * learningRate;
                inputLayerWeights[i][j] += momentumFactor * inputLayerWeightChange[i][j] + weight;
                inputLayerWeightChange[i][j] = weight;
            }
        }
        for (int i = 0; i < hiddenLayerNodes.length; i++) {
            double weight = hiddenLayerError[i] * hiddenLayerBiases[i] * learningRate;
            hiddenLayerBiases[i] += momentumFactor * hiddenLayerBiasChange[i] + weight;
            hiddenLayerBiasChange[i] = weight;
        }
    }

    public void recalculateNetworkWeightsAndValues(String inputLetterPath, char correctLetterName) throws ImageReadingException {
        double[] input = ImageReader.getInputs(inputLetterPath);
        double[] output = Letters.convertLetterToArray(correctLetterName);
        setInputValues(input);
        calculateNetwokValues();
        calculateNetworkErrors(output);
        adjustNetworkWeights();
    }

    public void trainNetworkWithTestcase(ArrayList<File> letters, int amountOfRepeats) throws ImageReadingException {
        while (amountOfRepeats-- > 0) {
            for (File letter : letters) {
                char correctLetterName = letter.getName().toLowerCase().toCharArray()[0];
                String inputLetterPath = letter.getPath();
                recalculateNetworkWeightsAndValues(inputLetterPath, correctLetterName);
                System.out.println(inputLetterPath + " " + correctLetterName);
            }
        }
        networkStatus = ConfigurationLoaded;
    }

    public Letters identifyLetter(String inputPath) throws ImageReadingException {
        double[] inputs = ImageReader.getInputs(inputPath);
        setInputValues(inputs);
        calculateNetwokValues();
        return Letters.convertArrayToLetter(outputLayerNodes);
    }


    public double[] getOutputLayerNodes() {
        return outputLayerNodes;
    }

    public void loadWeightsFromFile(File configFile) throws ConfigNotLoadedException {

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFile));
            inputLayerWeights = (double[][]) ois.readObject();
            hiddenLayerWeights = (double[][]) ois.readObject();
            hiddenLayerBiases = (double[]) ois.readObject();
            outputLayerBiases = (double[]) ois.readObject();
            this.configFile = configFile;
            networkStatus = ConfigurationLoaded;

        } catch (Exception e) {
            networkStatus = NoConfigurationLoaded;
            throw new ConfigNotLoadedException("Failed to load config file!");
        }

    }

    public void saveWeightsToFile() throws IOException {

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(configFile));
        out.writeObject(inputLayerWeights);
        out.writeObject(hiddenLayerWeights);
        out.writeObject(hiddenLayerBiases);
        out.writeObject(outputLayerBiases);

    }
}
