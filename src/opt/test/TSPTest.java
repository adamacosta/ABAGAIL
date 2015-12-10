package opt.test;

import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Adam Acosta
 * @version 1.0
 */
public class TSPTest {
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {

        for (int i = 50; i <= 100; i += 10) {

            int N = i;

            Random random = new Random();
            // create the random points
            double[][] points = new double[N][2];
            for (int j = 0; j < points.length; j++) {
                points[j][0] = random.nextDouble();
                points[j][1] = random.nextDouble();   
            }
            // for rhc, sa, and ga we use a permutation based encoding
            TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
            Distribution odd = new DiscretePermutationDistribution(N);
            NeighborFunction nf = new SwapNeighbor();
            MutationFunction mf = new SwapMutation();
            CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
            HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
            GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
            
            RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
            FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 100000);
            fit.train();
            System.out.print("RHC," + N + ",");
            System.out.println(1 / ef.value(rhc.getOptimal()));
            
            SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .95, hcp);
            fit = new FixedIterationTrainer(sa, 100000);
            fit.train();
            System.out.print("SA," + N + ",");
            System.out.println(1 / ef.value(sa.getOptimal()));
            
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 20, gap);
            fit = new FixedIterationTrainer(ga, 5000);
            fit.train();
            System.out.print("GA," + N + ",");
            System.out.println(1 / ef.value(ga.getOptimal()));
            
            // for mimic we use a sort encoding
            ef = new TravelingSalesmanSortEvaluationFunction(points);
            int[] ranges = new int[N];
            Arrays.fill(ranges, N);
            odd = new  DiscreteUniformDistribution(ranges);
            Distribution df = new DiscreteDependencyTree(.1, ranges); 
            ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
            
            MIMIC mimic = new MIMIC(200, 20, pop);
            fit = new FixedIterationTrainer(mimic, 500);
            fit.train();
            System.out.print("MIMIC," + N + ",");
            System.out.println(1 / ef.value(mimic.getOptimal()));

        }
        
    }
}