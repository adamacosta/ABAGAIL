package opt.test;

import java.util.Arrays;
import java.util.Random;

import opt.ga.NQueensFitnessFunction;
import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteBitFlipNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.SwapNeighbor;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteBitFlipMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.SwapMutation;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * @author Adam Acosta
 * @version 1.0
 */
public class NQueensTest {
    
    public static void main(String[] args) {

        for (int i = 8; i <= 40; i += 8) {

            int N = i;
            NQueensFitnessFunction ef = new NQueensFitnessFunction();
            Distribution odd = new DiscretePermutationDistribution(N);
            NeighborFunction nf = new SwapNeighbor();
            MutationFunction mf = new SwapMutation();
            CrossoverFunction cf = new SingleCrossOver();
            Distribution df = new DiscreteDependencyTree(.1); 
            HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
            GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
            ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
            
            RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
            FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 1);
            int curit = 0;
            int inarow = 0;
            double curval = 0.0;
            double lastval = ef.value(rhc.getOptimal());
            long starttime = System.currentTimeMillis();
            while (curit < 1E7 && ef.value(rhc.getOptimal()) < 0) {
                fit.train();
                curit++;
                curval = ef.value(rhc.getOptimal());
                if (curval <= lastval) {
                    inarow++;
                } else {
                    inarow = 0;
                }
                if (inarow == 100) {
                    break;
                }
                lastval = curval;
            }
            long elapsed = System.currentTimeMillis() - starttime;
            System.out.println("RHC," + i + "," + Math.abs(ef.value(rhc.getOptimal())) + "," + curit + "," + elapsed);
            
            SimulatedAnnealing sa = new SimulatedAnnealing(1E1, .1, hcp);
            fit = new FixedIterationTrainer(sa, 1);
            curit = 0;
            inarow = 0;
            curval = 0.0;
            lastval = ef.value(sa.getOptimal());
            starttime = System.currentTimeMillis();
            while (curit < 1E7 && ef.value(sa.getOptimal()) < 0) {
                fit.train();
                curit++;
                curval = ef.value(sa.getOptimal());
                if (curval <= lastval) {
                    inarow++;
                } else {
                    inarow = 0;
                }
                if (inarow == 100) {
                    break;
                }
                lastval = curval;
            }
            elapsed = System.currentTimeMillis() - starttime;
            System.out.println("SA," + i + "," + Math.abs(ef.value(sa.getOptimal())) + "," + curit + "," + elapsed);
            
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
            fit = new FixedIterationTrainer(ga, 1);
            curit = 0;
            inarow = 0;
            curval = 0.0;
            lastval = ef.value(ga.getOptimal());
            starttime = System.currentTimeMillis();
            while (curit < 1E5 && ef.value(ga.getOptimal()) < 0) {
                fit.train();
                curit++;
                curval = ef.value(ga.getOptimal());
                if (curval <= lastval) {
                    inarow++;
                } else {
                    inarow = 0;
                }
                if (inarow == 100) {
                    break;
                }
                lastval = curval;
            }
            elapsed = System.currentTimeMillis() - starttime;
            System.out.println("GA," + i + "," + Math.abs(ef.value(ga.getOptimal())) + "," + curit + "," + elapsed);
            
            MIMIC mimic = new MIMIC(200, 20, pop);
            fit = new FixedIterationTrainer(mimic, 1);
            curit = 0;
            inarow = 0;
            curval = 0.0;
            lastval = ef.value(mimic.getOptimal());
            starttime = System.currentTimeMillis();
            while (curit < 1E3 && ef.value(mimic.getOptimal()) < 0) {
                fit.train();
                curit++;
                curval = ef.value(mimic.getOptimal());
                if (curval <= lastval) {
                    inarow++;
                } else {
                    inarow = 0;
                }
                if (inarow == 100) {
                    break;
                }
                lastval = curval;
            }
            elapsed = System.currentTimeMillis() - starttime;
            System.out.println("MIMIC," + i + "," + Math.abs(ef.value(mimic.getOptimal())) + "," + curit + "," + elapsed);
        }
    }
}
