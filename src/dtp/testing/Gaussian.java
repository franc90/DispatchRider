package dtp.testing;

import java.util.Random;

public class Gaussian implements Distribution {

    /**
     * point, close to which there's most of randoms with normal distribution
     */
    private int mean;

    /**
     * "width" factor of the gaussian distribusion. bigger variance = wider range
     */
    private int variance;

    /**
     * @param mean
     *        point, close to which there's most of randoms with normal distribution
     * @param variance
     *        "width" factor of the gaussian distribusion. bigger variance = wider range
     */
    public Gaussian(int mean, int variance) {
        super();
        this.mean = mean;
        this.variance = variance;
    }

    public int getRandomNumber(int min, int max) throws DistributionException {

        if (min > max)
            throw new DistributionException("min>max in gaussian distribution");
        Random randomizer = new Random(System.currentTimeMillis());

        int tryIt = 0, maxTries = 100;
        int result;
        // Box-Muller transform, used to generate random numbers with normal (gaussian) distribution
        // using two independent random numbers from [0;1) - here rand1 and rand2
        // http://en.wikipedia.org/wiki/Box-Muller_transform
        do {
            double rand1 = randomizer.nextDouble(), rand2 = randomizer.nextDouble();
            Double resultAsDouble = Math.sin(2 * Math.PI * rand1) * Math.sqrt(-2 * Math.log(rand2)) * mean + variance;
            result = resultAsDouble.intValue();
            tryIt++;
        } while ((result < min || result > max) && tryIt < maxTries);

        if (tryIt == maxTries)
            throw new DistributionException("gaussian mean (" + mean + ") value probably out of [" + min + ";" + max
                    + "]");

        return result;
    }

    /**
     * Returns array of specified size containing random numbers with normal distribution
     */
    public int[] getRandomNumbers(int numberOfResults, int min, int max) throws DistributionException {
        int[] result = new int[numberOfResults];
        for (int resultIt = 0; resultIt < numberOfResults; resultIt++)
            result[resultIt] = getRandomNumber(min, max);
        return result;
    }

}
