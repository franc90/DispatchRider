package dtp.testing;

public interface Distribution {

    public int[] getRandomNumbers(int numberOfResults, int min, int max) throws DistributionException;

    public int getRandomNumber(int min, int max) throws DistributionException;
}
