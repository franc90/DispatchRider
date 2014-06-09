package algorithm;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import adapter.Adapter;
import adapter.MitrovicMinic;
import algorithm.simmulatedTrading.SimmulatedTrading;
import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.simmulation.SimInfo;

public class DynamicAlgorithmsTest {

	private final Map<Integer, List<Commission>> commissionsMap;
	private int deadline;
	private Point2D.Double depot;
	private double maxLoad;
	private final int maxFullSTDepth;
	private final String chooseWorstCommission;
	private int commissionsCount;
	private SimInfo simInfo;

	public DynamicAlgorithmsTest(Adapter adapter, int maxFullSTDepth,
			String chooseWorstCommission) {
		commissionsMap = readCommissions(adapter);
		initSimInfo(adapter);
		this.maxFullSTDepth = maxFullSTDepth;
		this.chooseWorstCommission = chooseWorstCommission;
	}

	private Map<Integer, List<Commission>> readCommissions(Adapter adapter) {
		Map<Integer, List<Commission>> result = new HashMap<Integer, List<Commission>>();
		List<Commission> commissions;
		commissionsCount = 0;
		for (CommissionHandler handler : adapter.readCommissions()) {
			commissions = result.get(handler.getIncomeTime());
			if (commissions == null) {
				commissions = new LinkedList<Commission>();
				result.put(handler.getIncomeTime(), commissions);
			}
			commissions.add(handler.getCommission());
			commissionsCount++;
		}
		return result;
	}

	private void initSimInfo(Adapter adapter) {
		deadline = (int) adapter.getSimInfo().getDeadline();
		depot = adapter.getSimInfo().getDepot();
		maxLoad = adapter.getSimInfo().getMaxLoad();
		simInfo = new SimInfo(depot, deadline, maxLoad);
	}

	private void printStaus(Map<Integer, Schedule> holons) {
		int commsCount = 0;
		Schedule schedule;
		System.out.println("Test finish");
		System.out.println("Holons: " + holons.size());
		for (Integer holon : holons.keySet()) {
			schedule = holons.get(holon);
			commsCount += schedule.getCommissions().size();
			System.out
					.println("holon_" + holon + ": "
							+ schedule.getCommissions().size() + "/"
							+ commissionsCount);
		}
		System.out.println("deivered: " + commsCount + "/" + commissionsCount);
		if (commsCount != commissionsCount) {
			System.err.println("ERROR");
			System.exit(0);
		}
	}

	public double[] newComplexST(Algorithm algorithm) {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		algorithm.init(maxLoad, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		for (int h = 0; h < deadline; h++) {
			List<Commission> coms = commissionsMap.get(h);
			if (coms == null)
				continue;
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
							Commission.copy(com), null, schedule, h);
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
							Helper.copy(holons), Commission.copy(com),
							algorithm, maxFullSTDepth, new TreeSet<Integer>(),
							0, simInfo);
					if (tmp != null) {

						holons = tmp;

						continue;
					}

					schedule = new BasicSchedule(algorithm);
					schedule.setCreationTime(h);

					schedule = algorithm.makeSchedule(null,
							Commission.copy(com), null, schedule, h);
					if (schedule == null) {
						System.err.println("Nie da sie zrealizowac zlecen");
						System.exit(0);
					}

					schedule.setCreationTime(h);
					holons.put(holons.size(), schedule);

				} else {
					holons.put(bestHolon, bestSchedule);

					for (int i = 0; i < holons.size(); i++) {
						Map<Integer, Schedule> tmpMap = Helper.copy(holons);
						holons = SimmulatedTrading.fullSimmulatedTrading(null,
								holons, i, 1, simInfo, algorithm,
								new HashSet<Integer>(), chooseWorstCommission,
								0);
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
		}
		printStaus(holons);

		return new double[] { holons.size(),
				Helper.calculateSummaryDistance(holons, depot),
				Helper.calculateSummaryTime(holons, depot, false) };
	}

	public double[] newComplexST_dist(Algorithm algorithm) {
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

		for (int h = 0; h < deadline; h++) {
			List<Commission> coms = commissionsMap.get(h);
			if (coms == null)
				continue;
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
							Commission.copy(com), null, schedule, h);
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
							Helper.copy(holons), Commission.copy(com),
							algorithm, maxFullSTDepth, new TreeSet<Integer>(),
							0, simInfo);
					if (tmp != null) {

						holons = tmp;

						continue;
					}

					schedule = new BasicSchedule(algorithm);
					schedule.setCreationTime(h);
					schedule = algorithm.makeSchedule(null,
							Commission.copy(com), null, schedule, h);
					if (schedule == null) {
						System.err.println("Nie da sie zrealizowac zlecen");
						System.exit(0);
					}

					schedule.setCreationTime(h);
					holons.put(holons.size(), schedule);

				} else {
					holons.put(bestHolon, bestSchedule);

					for (int i = 0; i < holons.size(); i++) {
						Map<Integer, Schedule> tmpMap = Helper.copy(holons);
						holons = SimmulatedTrading.fullSimmulatedTrading(null,
								holons, i, 1, simInfo, algorithm,
								new HashSet<Integer>(), chooseWorstCommission,
								0);
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
		}
		printStaus(holons);

		return new double[] { holons.size(),
				Helper.calculateSummaryDistance(holons, depot),
				Helper.calculateSummaryTime(holons, depot, false) };
	}

	public static void main(String args[]) {
		try {
			System.out.println(Arrays.toString(new DynamicAlgorithmsTest(
					new MitrovicMinic(
							"benchmarks/Mitrovic-Minic/Rnd8_10h_100_000.txt"),
					8, "wTime").newComplexST_dist(new BruteForceAlgorithm())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
