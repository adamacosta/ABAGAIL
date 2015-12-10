package func.test;

import shared.ConvergenceTrainer;
import shared.DataSet;
import shared.reader.CSVDataSetReader;
import shared.reader.DataSetReader;
import shared.Instance;
import shared.SumOfSquaresError;
import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;
import func.nn.backprop.BatchBackPropagationTrainer;
import func.nn.backprop.RPROPUpdateRule;
import func.nn.activation.LogisticSigmoid;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * A simple classification test
 * @author Adam Acosta
 * @version 1.0
 */
public class RbtNN {

    private static Instance[] trainInstances = initializeInstances("rbtTrain.txt", "rbtTrainLabels.txt");
    private static Instance[] testInstances = initializeInstances("rbtTest.txt", "rbtTestLabels.txt");

    private static int inputLayer = 24, hiddenLayer = 12, outputLayer = 4;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

    private static DataSet set = new DataSet(trainInstances);
    private static String results = "";
    private static DecimalFormat df = new DecimalFormat("0.000");

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

            for (int i = 0; i < instances.length; i++) {
                instances[i].setLabel(new Instance(labels[i].getData()));
            }

            return instances;
        } catch (Exception e) {
            System.out.println("Failed to read input file");
            return null;
        }
    }

    /**
     * @param args ignored
     */
    public static void main(String[] args) {

        double start, end, predicted, actual, correct = 0, incorrect = 0;
        
        BackPropagationNetwork network = factory.createClassificationNetwork(
           new int[] { inputLayer, hiddenLayer, outputLayer },
           new LogisticSigmoid());
        
        ConvergenceTrainer trainer = new ConvergenceTrainer(
               new BatchBackPropagationTrainer(set, network,
                   new SumOfSquaresError(), new RPROPUpdateRule()));

        start = System.nanoTime();
        trainer.train();
        end = System.nanoTime();
        double trainTime = (end - start) / Math.pow(10, 9);

        System.out.println("Convergence in " + trainer.getIterations() + " iterations");
        System.out.println("Took " + df.format(trainTime) + " seconds");

        for (int i = 0; i < testInstances.length; i++) {
            network.setInputValues(testInstances[i].getData());
            network.run();

            predicted = testInstances[i].getLabel().getData().argMax();
            actual = network.getOutputValues().argMax();

            double trash = predicted == actual ? correct++ : incorrect++;
        }

        results += df.format(incorrect / (correct + incorrect));

        System.out.println(results);
    }
}
