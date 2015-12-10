package opt.test;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;
import func.nn.activation.*;

import shared.DataSet;
import shared.DataSetDescription;
import shared.reader.CSVDataSetReader;
import shared.reader.DataSetReader;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying credit applications as either 
 * success or failure. 
 *
 * @author Adam Acosta
 * @version 1.0
 */
public class RbtTest {
    private static Instance[] trainInstances = initializeInstances("rbtTrain.txt", "rbtTrainLabels.txt");
    private static Instance[] testInstances = initializeInstances("rbtTest.txt", "rbtTestLabels.txt");

    private static int inputLayer = 24, hiddenLayer = 12, outputLayer = 4;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(trainInstances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) {

        int it = args.length > 0 ? Integer.parseInt(args[0]): 1000;

        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer},
                new LogisticSigmoid());
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }

        oa[0] = new RandomizedHillClimbing(nnop[0]);
        oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
        oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);

        for(int i = 0; i < oa.length; i++) {
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            train(oa[i], networks[i], oaNames[i], it);
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            int predicted, actual;
            start = System.nanoTime();
            for(int j = 0; j < trainInstances.length; j++) {
                networks[i].setInputValues(trainInstances[j].getData());
                networks[i].run();

                predicted = trainInstances[j].getLabel().getData().argMax();
                actual = networks[i].getOutputValues().argMax();

                double trash = predicted == actual ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime = end - start;

            results +=  oaNames[i] + "," + it + "," + df.format(incorrect / (correct + incorrect)) + ","; 

            correct = 0;
            incorrect = 0;
            start = System.nanoTime();
            for(int j = 0; j < testInstances.length; j++) {
                networks[i].setInputValues(testInstances[j].getData());
                networks[i].run();

                predicted = testInstances[j].getLabel().getData().argMax();
                actual = networks[i].getOutputValues().argMax();

                double trash = predicted == actual ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime += end - start;
            testingTime /= 2.0;
            testingTime /= Math.pow(10, 9);

            results += df.format(incorrect / (correct + incorrect)) + ","
                     + df.format(trainingTime) + "," + df.format(testingTime) + "\n";

        }

        System.out.print(results);
    }

    private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName, int it) {
        
        for(int i = 0; i < it; i++) {
            oa.train();

            double error = 0;
            for(int j = 0; j < trainInstances.length; j++) {
                network.setInputValues(trainInstances[j].getData());
                network.run();

                Instance output = trainInstances[j].getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(network.getOutputValues()));
                error += measure.value(output, example);
            }

        }
    }

    private static Instance[] initializeInstances(String dataFile, String labelFile) {

        DataSetReader dsr = new CSVDataSetReader(new File("").getAbsolutePath() + "/src/opt/test/" + dataFile);
        DataSetReader lsr = new CSVDataSetReader(new File("").getAbsolutePath() + "/src/opt/test/" + labelFile);
        DataSet ds;
        DataSet labs;

        try {
            ds = dsr.read();
            labs = lsr.read();
            Instance[] instances = ds.getInstances();
            Instance[] labels = labs.getInstances();

            for(int i = 0; i < instances.length; i++) {
                instances[i].setLabel(new Instance(labels[i].getData()));
            }

            return instances;
        } catch (Exception e) {
            System.out.println("Failed to read input file");
            return null;
        }

    }
}
