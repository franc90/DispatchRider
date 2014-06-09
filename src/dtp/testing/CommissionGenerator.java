package dtp.testing;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.optimization.TrackFinder;

/**
 * Used to generate random, though reasonable Commissions, for testing purposes.
 * 
 * @author lugh
 */
public class CommissionGenerator {

	private static Logger logger = Logger.getLogger(CommissionGenerator.class);

	private Distribution usedDistribution;
	private Graph world;
	private final Random rn;

	private TrackFinder finder;

	private int minLoadWindow, maxLoadWindow, minDelWindow, maxDelWindow,
			minWholeWindow, maxWholeWindow, minAdvance, maxAdvance,
			minLengthMultiplier, from, to, minWeight, maxWeight;

	public CommissionGenerator(Graph world, TrackFinder finder,
			int minLoadWindow, int maxLoadWindow, int minDelWindow,
			int maxDelWindow, int minWholeWindow, int maxWholeWindow,
			int minAdvance, int maxAdvance, int minLengthMultiplier, int from,
			int to, int minWeight, int maxWeight, Distribution usedDistribution) {
		super();
		this.world = world;
		this.finder = finder;
		this.minLoadWindow = minLoadWindow;
		this.maxLoadWindow = maxLoadWindow;
		this.minDelWindow = minDelWindow;
		this.maxDelWindow = maxDelWindow;
		this.minWholeWindow = minWholeWindow;
		this.maxWholeWindow = maxWholeWindow;
		this.minAdvance = minAdvance;
		this.maxAdvance = maxAdvance;
		this.minLengthMultiplier = minLengthMultiplier;
		this.from = from;
		this.to = to;
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
		this.usedDistribution = usedDistribution;
		rn = new Random(System.currentTimeMillis() - 666);
	}

	public CommissionGenerator(Graph world, TrackFinder finder) {
		super();
		this.world = world;
		this.finder = finder;
		this.minLoadWindow = 10;
		this.maxLoadWindow = 100;
		this.minDelWindow = 10;
		this.maxDelWindow = 100;
		this.minWholeWindow = 100;
		this.maxWholeWindow = 1000;
		this.minAdvance = 100;
		this.maxAdvance = 1000;
		this.minLengthMultiplier = 5;
		this.from = 1;
		this.to = 100000;
		this.minWeight = 1;
		this.maxWeight = 100;
		this.usedDistribution = new Regular();
		rn = new Random(System.currentTimeMillis() - 666);
	}

	public boolean parametersAcceptable() {
		if (minLoadWindow > minWholeWindow || minDelWindow > minWholeWindow
				|| maxLoadWindow > maxWholeWindow
				|| maxDelWindow > maxWholeWindow)
			return false;
		return true;
	}

	public boolean parametersAcceptable(int minAcceptableTime) {
		if (minLoadWindow > minWholeWindow || minDelWindow > minWholeWindow
				|| maxLoadWindow > maxWholeWindow
				|| maxDelWindow > maxWholeWindow
				|| minAcceptableTime > maxWholeWindow)
			return false;
		return true;
	}

	/**
	 * Generates a single commission
	 * 
	 * @param from
	 *            - begin of interesting time period
	 * @param to
	 *            - end of interesting time period
	 * @param minWeight
	 *            - min possible weight
	 * @param maxWeight
	 *            - max possible weight
	 * @return random Commission, or null if to-from is too low, or there isn't
	 *         enough points in <b>world</b>.
	 */
	public CommissionHandler generateCommission(int id) {
		try {
			Distribution reg = new Regular();
			if (to - from < 100)
				return null;

			GraphPoint[] pts = world.getCollectionOfPoints().toArray(
					new GraphPoint[world.getCollectionOfPoints().size()]);

			if (pts.length < 2)
				return null;

			@SuppressWarnings("unused")
			GraphTrack tr;
			GraphPoint pt1 = pts[rn.nextInt(pts.length)];
			GraphPoint pt2;
			do
				pt2 = pts[rn.nextInt(pts.length)];
			while (pt2 == null || pt2.equals(pt1)
					|| !(tr = finder.findTrack(pt1, pt2)).isPossible());

			// int minAcceptableTime = minLengthMultiplier * (tr.getTime());
			int minAcceptableTime = minLengthMultiplier * 5;

			int commTime = 0, pickupTime1 = 0, pickupTime2 = 0, deliveryTime1 = 0, deliveryTime2 = 0, load = 0, serviceTime = 0;

			// ugly code... tries until succeeds. if can't find consistant
			// values
			// 10000 times in a row, throws exception
			int tryIt = 0, tryLimit = 10000;
			// CAUTION: i don't check whether parameters min* and max* are
			// correct
			do {
				try {
					commTime = usedDistribution.getRandomNumber(from, to
							- maxAdvance - maxWholeWindow);
					pickupTime1 = reg.getRandomNumber(commTime + minAdvance, to
							- minAcceptableTime);
					pickupTime2 = reg.getRandomNumber(pickupTime1 + 1,
							pickupTime1 + maxLoadWindow);
					deliveryTime1 = reg.getRandomNumber(pickupTime1 + 1,
							pickupTime1 + minAcceptableTime - minDelWindow);
					deliveryTime2 = reg.getRandomNumber(deliveryTime1 + 1,
							deliveryTime1 + minDelWindow);
					tryIt++;
				} catch (DistributionException dex) {
					commTime = -1;
					if (tryIt >= tryLimit)
						throw dex;
				}
			} while (commTime <= 0
					|| deliveryTime2 - pickupTime1 > maxWholeWindow
					|| deliveryTime2 - pickupTime1 < minWholeWindow);
			// this do..while isn't very pretty, but it seems to work and it's
			// easy

			load = reg.getRandomNumber(minWeight, maxWeight);

			serviceTime = Math.min(
					reg.getRandomNumber(pickupTime1, pickupTime2),
					reg.getRandomNumber(deliveryTime1, deliveryTime2));

			if (!parametersAcceptable(minAcceptableTime))
				return null;

			Commission com = new Commission(id, (int) pt1.getX(),
					(int) pt1.getY(), pickupTime1, pickupTime2,
					(int) pt2.getX(), (int) pt2.getY(), deliveryTime1,
					deliveryTime2, load, serviceTime, serviceTime);

			return new CommissionHandler(com, commTime);
		} catch (Exception ex) {
			logger.error("CommissionGenerator.generateCommission():" + ex);
			return null;
		}
	}

	/**
	 * Generates Commissions
	 * 
	 * @param howMany
	 *            - quantity of Commissions to be generated
	 * @param from
	 *            - begin of interesting time period
	 * @param to
	 *            - end of interesting time period
	 * @param minWeight
	 *            - min possible weight
	 * @param maxWeight
	 *            - max possible weight
	 * @return Commission[howMany] if ok, or null if to-from is too low, or
	 *         there isn't enough points in <b>world</b>.
	 */
	public CommissionHandler[] generateCommissionArray(int howMany) {
		try {
			if (to - from < 100 && world.getCollectionOfPoints().size() < 2)
				return null;

			CommissionHandler[] result = new CommissionHandler[howMany];
			CommissionHandler tmpComm;

			int i = 0, counter = 0, maxCounter = 1000 * howMany + 100;
			while (i < howMany) {
				if (counter > maxCounter)
					throw new Exception(
							"generateCommissionArray(int howMany): Eeeeep! Something's wrong with the parameters");
				tmpComm = generateCommission(i);
				if (tmpComm != null) {
					result[i] = tmpComm;
					i++;
				}
			}

			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			// logger.error("CommissionGenerator.generateCommissionArray():");
			// logger.error(ex);
			return null;
		}

	}

	private void merge(ArrayList<CommissionHandler> commsList,
			CommissionHandler newComms[]) {
		for (int i = 0, ln = newComms.length; i < ln; i++)
			commsList.add(newComms[i]);
	}

	public CommissionHandler[] generate(int numberOfRegular,
			int numberOfClusters, int minClusterSize, int maxClusterSize,
			int minVariance, int maxVariance) throws IllegalArgumentException {

		if (from > to)
			throw new IllegalArgumentException(
					"minTime is greater than maxTime");
		if (minVariance > maxVariance)
			throw new IllegalArgumentException(
					"minVariance is greater than maxVariance");
		if (minClusterSize > maxClusterSize)
			throw new IllegalArgumentException(
					"minClusterSize is greater than maxClusterSize");

		ArrayList<CommissionHandler> result = new ArrayList<CommissionHandler>();

		Distribution tmp = this.usedDistribution;
		this.usedDistribution = new Regular();
		CommissionHandler regulars[] = generateCommissionArray(numberOfRegular);
		merge(result, regulars);
		for (int i = 0; i < numberOfClusters; i++) {
			Random rn = new Random(System.currentTimeMillis() - 999);

			int mean = from;
			if (to > from)
				mean = rn.nextInt(to - from) + from;
			// max>=min - checked on the beginning of this method

			int variance = minVariance;
			if (maxVariance > minVariance)
				variance = rn.nextInt(maxVariance - minVariance) + minVariance;
			// max>=min - checked on the beginning of this method

			int clusterSize = minClusterSize;
			if (maxClusterSize > minClusterSize)
				clusterSize = rn.nextInt(maxClusterSize - minClusterSize)
						+ minClusterSize;
			// max>=min - checked on the beginning of this method

			this.usedDistribution = new Gaussian(mean, variance);
			CommissionHandler tab[] = generateCommissionArray(clusterSize);
			merge(result, tab);
		}
		this.usedDistribution = tmp;
		return result.toArray(new CommissionHandler[result.size()]);
	}

	/**
	 * @return Locally used Graph
	 */
	public Graph getWorld() {
		return world;
	}

	/**
	 * Set locally used Graph.
	 * 
	 * @param world
	 */
	public void setWorld(Graph world) {
		this.world = world;
	}

	public int getMaxAdvance() {
		return maxAdvance;
	}

	public void setMaxAdvance(int maxAdvance) {
		this.maxAdvance = maxAdvance;
	}

	public int getMaxDelWindow() {
		return maxDelWindow;
	}

	public void setMaxDelWindow(int maxDelWindow) {
		this.maxDelWindow = maxDelWindow;
	}

	public int getMaxLoadWindow() {
		return maxLoadWindow;
	}

	public void setMaxLoadWindow(int maxLoadWindow) {
		this.maxLoadWindow = maxLoadWindow;
	}

	public int getMaxWholeWindow() {
		return maxWholeWindow;
	}

	public void setMaxWholeWindow(int maxWholeWindow) {
		this.maxWholeWindow = maxWholeWindow;
	}

	public int getMinAdvance() {
		return minAdvance;
	}

	public void setMinAdvance(int minAdvance) {
		this.minAdvance = minAdvance;
	}

	public int getMinDelWindow() {
		return minDelWindow;
	}

	public void setMinDelWindow(int minDelWindow) {
		this.minDelWindow = minDelWindow;
	}

	public int getMinLoadWindow() {
		return minLoadWindow;
	}

	public void setMinLoadWindow(int minLoadWindow) {
		this.minLoadWindow = minLoadWindow;
	}

	public int getMinWholeWindow() {
		return minWholeWindow;
	}

	public void setMinWholeWindow(int minWholeWindow) {
		this.minWholeWindow = minWholeWindow;
	}

	public int getMinLengthMultiplier() {
		return minLengthMultiplier;
	}

	public void setMinLengthMultiplier(int minLengthMultiplier) {
		this.minLengthMultiplier = minLengthMultiplier;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	public int getMinWeight() {
		return minWeight;
	}

	public void setMinWeight(int minWeight) {
		this.minWeight = minWeight;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public TrackFinder getFinder() {
		return finder;
	}

	public void setFinder(TrackFinder finder) {
		this.finder = finder;
	}

	public Distribution getUsedDistribution() {
		return usedDistribution;
	}

	public void setUsedDistribution(Distribution usedDistribution) {
		this.usedDistribution = usedDistribution;
	}
}
