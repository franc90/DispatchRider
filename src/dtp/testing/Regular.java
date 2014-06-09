package dtp.testing;

import java.util.Random;

public class Regular implements Distribution {

    public int getRandomNumber(int min, int max) throws DistributionException {
        if (min > max)
            throw new DistributionException("min(" + min + ")>max(" + max + ") in normal distribution");
        if (min == max)
            return min;
        Random randomizer = new Random(System.currentTimeMillis());
        return randomizer.nextInt(max - min) + min;
    }

    public int[] getRandomNumbers(int numberOfResults, int min, int max) throws DistributionException {
        if (min > max)
            throw new DistributionException("min>max in normal distribution");
        Random randomizer = new Random(System.currentTimeMillis());
        int result[] = new int[numberOfResults];
        for (int resultIt = 0; resultIt < numberOfResults; resultIt++)
            result[resultIt] = randomizer.nextInt(max - min) + min;
        return null;
    }
}
