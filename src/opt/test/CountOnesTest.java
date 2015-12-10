package opt.test;

import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteBitFlipNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteBitFlipMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * A test using the flip flop evaluation function
 * @author Adam Acosta
 * @version 1.0
 */
public class CountOnesTest {

    private static Random random = new Random();
        
    public static void main(String[] args) {

        for (int i = 20; i <= 80; i += 20) {
            /** The n value */
            int N = i;
            
            int maxpos = N;

            int[] ranges = new int[N];
            for (int j = 0; j < N; j++) {
                ranges[j] = random.nextInt(2);
            }
            EvaluationFunction ef = new CountOnesEvaluationFunction();
            Distribution odd = new DiscreteUniformDistribution(ranges);
            NeighborFunction nf = new DiscreteBitFlipNeighbor(ranges);
            MutationFunction mf = new DiscreteBitFlipMutation(ranges);
            CrossoverFunction cf = new SingleCrossOver();
            Distribution df = new DiscreteDependencyTree(.1, ranges); 
            HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
            GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
            ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
            
            int curit = 0;
            RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
            FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 1);
            long start = System.currentTimeMillis();
            while (curit < 1E7 && ef.value(rhc.getOptimal()) < maxpos) {
                fit.train();
                curit++;
            }
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            System.out.println("RHC," + N + "," + ef.value(rhc.getOptimal()) + "," + curit + "," + elapsed);
            
            SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
            fit = new FixedIterationTrainer(sa, 1);
            curit = 0;
            start = System.currentTimeMillis();
            while (curit < 1E7 && ef.value(sa.getOptimal()) < maxpos) {
                fit.train();
                curit++;
            }
            end = System.currentTimeMillis();
            elapsed = end - start;
            System.out.println("SA," + N + "," + ef.value(sa.getOptimal()) + "," + curit + "," + elapsed);
            
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
            fit = new FixedIterationTrainer(ga, 1);
            curit = 0;
            start = System.currentTimeMillis();
            while (curit < 1E5 && ef.value(ga.getOptimal()) < maxpos) {
                fit.train();
                curit++;
            }
            end = System.currentTimeMillis();
            elapsed = end - start;
            System.out.println("GA," + N + "," + ef.value(ga.getOptimal()) + "," + curit + "," + elapsed);
            
            MIMIC mimic = new MIMIC(200, 20, pop);
            fit = new FixedIterationTrainer(mimic, 1);
            curit = 0;
            start = System.currentTimeMillis();
            while (curit < 2*1E3 && ef.value(mimic.getOptimal()) < maxpos) {
                fit.train();
                curit++;
            }
            end = System.currentTimeMillis();
            elapsed = end - start;
            System.out.println("MIMIC," + N + "," + ef.value(mimic.getOptimal()) + "," + curit + "," + elapsed);

        }
    }
    
}
