package algorithm;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import algorithm.simmulatedTrading.OldSTSimmulator;
import algorithm.simmulatedTrading.SimmulatedTrading;
import dtp.commission.Commission;
import dtp.commission.TxtFileReader;
import dtp.jade.agentcalendar.AgentCalendarWithoutGraph;
import dtp.jade.eunit.LoadNotOkException;
import dtp.simmulation.SimInfo;

/**
 * This class is used to test new algorithms (both to deploy commissions and
 * Simmulated Trading). Tests with the similar names (one of them with _dist)
 * are different only in calculating cost of new commissions. It's realization
 * time, or it's ratio, which is calculated similar like in system (it uses
 * Helper class, so it's simpler)
 */
public class ConfigureSTAlgorithmTests {

	private Map<Integer, AgentCalendarWithoutGraph> holons;
	private final List<Commission> commissions;
	private final int deadline;
	private final Point2D.Double depot;
	private final double maxLoad;
	private final int maxFullSTDepth;
	private final String chooseWorstCommission;
	private final SimInfo simInfo;

	public ConfigureSTAlgorithmTests(String fileName, int maxFullSTDepth,
			String chooseWorstCommission) {
		this.maxFullSTDepth = maxFullSTDepth;
		this.chooseWorstCommission = chooseWorstCommission;
		Commission[] commissions = TxtFileReader.getCommissions(fileName);
		this.commissions = new LinkedList<Commission>();
		for (Commission com : commissions)
			this.commissions.add(com);

		int depotX;
		int depotY;

		depotX = (int) TxtFileReader.getDepot(fileName).getX();
		depotY = (int) TxtFileReader.getDepot(fileName).getY();

		this.deadline = TxtFileReader.getDeadline(fileName);
		this.depot = new Point2D.Double(depotX, depotY);
		this.maxLoad = TxtFileReader.getTruckCapacity(fileName);
		simInfo = new SimInfo(depot, deadline, maxLoad);
		holons = new HashMap<Integer, AgentCalendarWithoutGraph>();
	}

	private void reset() {
		holons = new HashMap<Integer, AgentCalendarWithoutGraph>();
		System.out.println();
	}

	private void printStaus() {
		AgentCalendarWithoutGraph calendar;
		int commsCount = 0;
		System.out.println("Test finish");
		System.out.println("Holons: " + holons.size());
		for (Integer holon : holons.keySet()) {
			calendar = holons.get(holon);
			commsCount += commissionsCount(calendar);
			System.out.println("holon_" + holon + ": "
					+ commissionsCount(calendar) + "/" + commissions.size());
		}
		System.out
				.println("deivered: " + commsCount + "/" + commissions.size());
		if (commsCount != commissions.size()) {
			System.err.println("ERROR");
			System.exit(0);
		}
		reset();
	}

	private void printStaus(Map<Integer, Schedule> holons) {
		int commsCount = 0;
		Schedule schedule;
		System.out.println("Test finish");
		System.out.println("Holons: " + holons.size());
		for (Integer holon : holons.keySet()) {
			schedule = holons.get(holon);
			commsCount += schedule.getCommissions().size();
			System.out.println("holon_" + holon + ": "
					+ schedule.getCommissions().size() + "/"
					+ commissions.size());
		}
		System.out
				.println("deivered: " + commsCount + "/" + commissions.size());
		if (commsCount != commissions.size()) {
			System.err.println("ERROR");
			System.exit(0);
		}
		reset();
	}

	public int OldSTtest() {
		AgentCalendarWithoutGraph calendar;
		boolean added;
		double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;
		for (Commission com : commissions) {
			added = false;
			bestCost = Double.MAX_VALUE;
			for (Integer holon : holons.keySet()) {
				calendar = holons.get(holon);
				if (calendar.addCommission(com, 0) == -1) {
					calendar.removeCommission(com);
					try {
						extraDistance = calendar.getExtraDistance(com, 0);
						if (bestCost > Helper.getRatio(extraDistance, com)) {
							bestCost = Helper.getRatio(extraDistance, com);
							bestHolon = holon;
						}
						added = true;
					} catch (LoadNotOkException e) {

					}
				}
			}
			if (added == false) {
				calendar = new AgentCalendarWithoutGraph(deadline, depot,
						maxLoad);
				if (calendar.addCommission(com, 0) != -1) {
					System.err.println("Nie da sie zrealizowac zlecen");
					System.exit(0);
				}
				holons.put(holons.size(), calendar);

			} else {
				calendar = holons.get(bestHolon);
				if (calendar.addCommission(com, 0) != -1) {
					System.err.println("Fatal error " + bestHolon + " "
							+ calendar.addCommission(com, 0));
					// System.exit(0);
					return -1;
				}

				double currentCost = Helper.calculateCost(holons, depot);
				Map<Integer, AgentCalendarWithoutGraph> tmp = new HashMap<Integer, AgentCalendarWithoutGraph>();
				for (Integer key : holons.keySet()) {
					tmp.put(key, holons.get(key).clone());
				}

				for (int holon = 0; holon < holons.size(); holon++) {
					holons = OldSTSimmulator
							.simmulatedTrading(holons, holon, 1);
				}
				if (currentCost < Helper.calculateCost(holons, depot)) {
					holons = tmp;
				}
			}
		}

		int result = holons.size();
		printStaus();
		return result;
	}

	public int newST(Algorithm algorithm) {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		// Algorithm algorithm=new BruteForceAlgorithm2(200,depot,deadline);
		algorithm.init(maxLoad, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		List<Commission> coms = Helper.copy(commissions);
		Collections.sort(coms, new CommissionsComparator(depot));

		for (Commission com : coms) {
			added = false;
			bestCost = Double.MAX_VALUE;
			double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				// extraTime=schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					extraTime = tmpSchedule.calculateTime(depot);// -extraTime;
					if (bestCost > extraTime) {// Helper.getRatio(extraDistance,
												// com)) {
						bestCost = extraTime;// -Helper.getRatio(extraDistance,
												// com);
						bestHolon = holon;
						bestSchedule = tmpSchedule;
					}
					added = true;
				}
			}
			if (added == false) {
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen");
					System.exit(0);
				}
				holons.put(holons.size(), schedule);
			} else {
				holons.put(bestHolon, bestSchedule);

				for (int i = 0; i < holons.size(); i++) {
					Map<Integer, Schedule> tmpMap = Helper.copy(holons);
					holons = SimmulatedTrading.fullSimmulatedTrading(null,
							holons, i, 1, simInfo, algorithm,
							new HashSet<Integer>(), chooseWorstCommission, 0);
					if (Helper.calculateCalendarCost(holons, depot) > Helper
							.calculateCalendarCost(tmpMap, depot)) {
						holons = tmpMap;
					}
				}
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int newST_dist(Algorithm algorithm) {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		// Algorithm algorithm=new BruteForceAlgorithm2(200,depot,deadline);
		algorithm.init(maxLoad, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		List<Commission> coms = Helper.copy(commissions);
		Collections.sort(coms, new CommissionsComparator(depot));

		for (Commission com : coms) {
			added = false;
			bestCost = Double.MAX_VALUE;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				// extraTime=schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					extraDistance = tmpSchedule.getDistance(depot)
							- schedule.getDistance(depot);
					if (bestCost > Helper.getRatio(extraDistance, com)) {
						bestCost = Helper.getRatio(extraDistance, com);
						bestHolon = holon;
						bestSchedule = tmpSchedule;
					}
					added = true;
				}
			}
			if (added == false) {
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen");
					System.exit(0);
				}
				holons.put(holons.size(), schedule);
			} else {
				holons.put(bestHolon, bestSchedule);

				for (int i = 0; i < holons.size(); i++) {
					Map<Integer, Schedule> tmpMap = Helper.copy(holons);
					holons = SimmulatedTrading.fullSimmulatedTrading(null,
							holons, i, 1, simInfo, algorithm,
							new HashSet<Integer>(), chooseWorstCommission, 0);
					if (Helper.calculateCalendarCost(holons, depot) > Helper
							.calculateCalendarCost(tmpMap, depot)) {
						holons = tmpMap;
					}
				}
			}
		}

		printStaus(holons);
		return holons.size();
	}

	private int commissionsCount(AgentCalendarWithoutGraph calendar) {
		int result = 0;
		for (Commission com : commissions) {
			if (calendar.containsCommission(com.getID()))
				result++;
		}
		return result;
	}

	public double[] newComplexST(Algorithm algorithm, boolean construction) {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		// Algorithm algorithm=new BruteForceAlgorithm2(200,depot,deadline);
		algorithm.init(maxLoad, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		List<Commission> coms = Helper.copy(commissions);
		// Collections.sort(commissions, new CommissionsComparator(depot));
		Collections.sort(coms, new CommissionsComparator(depot));

		for (Commission com : coms) {
			added = false;
			bestCost = Double.MAX_VALUE;
			double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				extraTime = schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					extraTime = tmpSchedule.calculateTime(depot);// -extraTime;
					if (bestCost > extraTime) {
						bestCost = extraTime;
						bestHolon = holon;
						bestSchedule = tmpSchedule;
					}
					added = true;
				}
			}

			if (added == false) {
				Map<Integer, Schedule> tmp;

				tmp = SimmulatedTrading.complexSimmulatedTrading(null,
						Helper.copy(holons), Commission.copy(com), algorithm,
						maxFullSTDepth, new TreeSet<Integer>(), 0, simInfo);
				if (tmp != null) {

					holons = tmp;

					continue;
				}
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen");
					System.exit(0);
				}

				holons.put(holons.size(), schedule);

			} else {
				holons.put(bestHolon, bestSchedule);

				for (int i = 0; i < holons.size(); i++) {
					Map<Integer, Schedule> tmpMap = Helper.copy(holons);
					holons = SimmulatedTrading.fullSimmulatedTrading(null,
							holons, i, 1, simInfo, algorithm,
							new HashSet<Integer>(), chooseWorstCommission, 0);
					if (Helper.calculateCalendarCost(holons, depot) > Helper
							.calculateCalendarCost(tmpMap, depot)) {
						holons = tmpMap;
					}
				}
			}

			Map<Integer, Schedule> map = new HashMap<Integer, Schedule>();
			int i = 0;
			for (Integer key : holons.keySet()) {
				if (holons.get(key).size() > 0)
					map.put(i++, holons.get(key));
			}
			holons = map;
		}

		printStaus(holons);

		return new double[] { holons.size(),
				Helper.calculateSummaryDistance(holons, depot),
				Helper.calculateSummaryTime(holons, depot, construction) };
	}

	public double[] newComplexST_dist(Algorithm algorithm, boolean construction) {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		// Algorithm algorithm=new BruteForceAlgorithm2(200,depot,deadline);
		algorithm.init(maxLoad, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		List<Commission> coms = Helper.copy(commissions);
		// Collections.sort(commissions, new CommissionsComparator(depot));
		Collections.sort(coms, new CommissionsComparator(depot));

		for (Commission com : coms) {
			added = false;
			bestCost = Double.MAX_VALUE;
			// double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				// extraTime=schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					// extraTime=tmpSchedule.calculateTime(depot)-extraTime;
					extraDistance = tmpSchedule.getDistance(depot)
							- schedule.getDistance(depot);
					if (bestCost > Helper.getRatio(extraDistance, com)) {
						bestCost = Helper.getRatio(extraDistance, com);
						bestHolon = holon;
						bestSchedule = tmpSchedule;
					}
					added = true;
				}
			}

			if (added == false) {
				Map<Integer, Schedule> tmp;

				tmp = SimmulatedTrading.complexSimmulatedTrading(null,
						Helper.copy(holons), Commission.copy(com), algorithm,
						maxFullSTDepth, new TreeSet<Integer>(), 0, simInfo);
				if (tmp != null) {

					holons = tmp;

					continue;
				}
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen");
					System.exit(0);
				}

				holons.put(holons.size(), schedule);

			} else {
				holons.put(bestHolon, bestSchedule);

				for (int i = 0; i < holons.size(); i++) {
					Map<Integer, Schedule> tmpMap = Helper.copy(holons);
					holons = SimmulatedTrading.fullSimmulatedTrading(null,
							holons, i, 1, simInfo, algorithm,
							new HashSet<Integer>(), chooseWorstCommission, 0);
					if (Helper.calculateCalendarCost(holons, depot) > Helper
							.calculateCalendarCost(tmpMap, depot)) {
						holons = tmpMap;
					}
				}
			}

			Map<Integer, Schedule> map = new HashMap<Integer, Schedule>();
			int i = 0;
			for (Integer key : holons.keySet()) {
				if (holons.get(key).size() > 0)
					map.put(i++, holons.get(key));
			}
			holons = map;
		}

		printStaus(holons);

		return new double[] { holons.size(),
				Helper.calculateSummaryDistance(holons, depot),
				Helper.calculateSummaryTime(holons, depot, construction) };
	}

	public static void main(String args[]) {
		ConfigureSTAlgorithmTests test = new ConfigureSTAlgorithmTests(
				"benchmarks/pdp_100/lr102.txt", 8, "wTime");
		double[] res = test
				.newComplexST_dist(new BruteForceAlgorithm2(), false);
		System.out.println(res[1] + " " + res[2]);
	}
}
