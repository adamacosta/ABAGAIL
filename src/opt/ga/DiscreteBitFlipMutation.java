package opt.ga;

import dist.Distribution;

import shared.Instance;

/**
 * A mutation function for changing a single value
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class DiscreteBitFlipMutation implements MutationFunction {    
    /**
     * The ranges of the different values
     */
    private int[] ranges;
    
    /**
     * Make a new discrete change one mutation function
     * @param ranges the ranges of the data
     */
    public DiscreteBitFlipMutation(int[] ranges) { 
        this.ranges = ranges;
    }

    /**
     * @see opt.ga.MutationFunction#mutate(opt.OptimizationData)
     */
    public void mutate(Instance d) {
        int i = Distribution.random.nextInt(d.size());
        d.getData().set(i, i == 0 ? 1 : 0);
    }

}
