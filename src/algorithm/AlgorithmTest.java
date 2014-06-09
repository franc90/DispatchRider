package algorithm;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
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
 * It's a simple class, which is used to test algorithms, which deploy
 * commissions. You don't need to use it. You should look at Tests class
 * 
 */
public class AlgorithmTest {

	public static String chooseWorstCommission = "time";

	private Map<Integer, AgentCalendarWithoutGraph> holons;
	private final List<Commission> commissions;
	private final int deadline;
	private final Point2D.Double depot;
	private final double maxLoad;
	private final ComparatorType comparator;
	private final String f;
	private final SimInfo simInfo;

	public AlgorithmTest(String fileName, ComparatorType comparator) {
		this.comparator = comparator;
		f = fileName;
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
		reset();
	}

	public int test() {
		AgentCalendarWithoutGraph calendar;
		boolean added;
		for (Commission com : commissions) {
			added = false;
			for (Integer holon : holons.keySet()) {
				calendar = holons.get(holon);
				if (calendar.addCommission(com, 0) == -1) {
					added = true;
					break;
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
			}
		}
		int result = holons.size();
		printStaus();
		return result;
	}

	public int test2() {
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
			}
		}

		int result = holons.size();
		printStaus();
		return result;
	}

	// TODO dopisac uwzglednienie bazy ;)
	public int newTest() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new ConstrctionAlgorithm(200, depot, deadline,
				comparator);
		Schedule schedule;
		Schedule tmpSchedule;
		boolean added;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		for (Commission com : commissions) {
			// System.out.println("new commission: "+com);
			added = false;
			for (Integer holon : holons.keySet()) {
				// System.out.println("**************** holon "+holon+" ***********************");
				schedule = holons.get(holon);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					schedule = tmpSchedule;
					holons.put(holon, schedule);
					// System.out.println("Commission was assign to holon "+holon);
					// System.out.println("///////////////////////////////////");
					// for(int i=0;i<schedule.size();i++) {
					// if(schedule.isPickup(i))
					// System.out.println("Pickup "+schedule.getAllCommissions().get(i));
					// else
					// System.out.println("Delivery "+schedule.getAllCommissions().get(i));
					// }
					added = true;
					break;
				}
			}
			if (added == false) {
				// System.out.println("new holon for commission "+com.getID());
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen "
							+ comparator.toString() + " " + f);
					System.exit(0);
				}
				holons.put(holons.size(), schedule);
				// System.out.println("///////////////////////////////////");
				// for(int i=0;i<schedule.size();i++) {
				// if(schedule.isPickup(i))
				// System.out.println("Pickup "+schedule.getAllCommissions().get(i));
				// else
				// System.out.println("Delivery "+schedule.getAllCommissions().get(i));
				// }
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int newTest2() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new ConstrctionAlgorithm(200, depot, deadline,
				comparator);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		for (Commission com : commissions) {
			added = false;
			bestCost = Double.MAX_VALUE;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
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

	public int new2Test() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		boolean added;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		for (Commission com : commissions) {
			// System.out.println("new commission: "+com);
			added = false;
			for (Integer holon : holons.keySet()) {
				// System.out.println("**************** holon "+holon+" ***********************");
				schedule = holons.get(holon);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					schedule = tmpSchedule;
					holons.put(holon, schedule);
					// System.out.println("Commission was assign to holon "+holon);
					// System.out.println("///////////////////////////////////");
					// for(int i=0;i<schedule.size();i++) {
					// if(schedule.isPickup(i))
					// System.out.println("Pickup "+schedule.getAllCommissions().get(i));
					// else
					// System.out.println("Delivery "+schedule.getAllCommissions().get(i));
					// }
					added = true;
					break;
				}
			}
			if (added == false) {
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen "
							+ comparator.toString() + " " + f);
					System.exit(0);
				}
				holons.put(holons.size(), schedule);
				// System.out.println("///////////////////////////////////");
				// for(int i=0;i<schedule.size();i++) {
				// if(schedule.isPickup(i))
				// System.out.println("Pickup "+schedule.getAllCommissions().get(i));
				// else
				// System.out.println("Delivery "+schedule.getAllCommissions().get(i));
				// }
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int new2Test2() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		// double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		for (Commission com : commissions) {
			added = false;
			bestCost = Double.MAX_VALUE;
			double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				extraTime = schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					extraTime = tmpSchedule.calculateTime(depot) - extraTime;
					// extraDistance =
					// tmpSchedule.getDistance(depot)-schedule.getDistance(depot);
					if (bestCost > extraTime) {// Helper.getRatio(extraDistance,
												// com)) {
						bestCost = tmpSchedule.calculateTime(depot);// Helper.getRatio(extraDistance,
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
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int new2Test2_dist() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		for (Commission com : commissions) {
			added = false;
			bestCost = Double.MAX_VALUE;
			// double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				// extraDistance=schedule.getDistance(depot);
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
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen");
					System.exit(0);
				}
				holons.put(holons.size(), schedule);
			} else {
				holons.put(bestHolon, bestSchedule);
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int new3Test() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm2(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		boolean added;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		for (Commission com : commissions) {
			// System.out.println("new commission: "+com);
			added = false;
			for (Integer holon : holons.keySet()) {
				// System.out.println("**************** holon "+holon+" ***********************");
				schedule = holons.get(holon);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					schedule = tmpSchedule;
					holons.put(holon, schedule);
					// System.out.println("Commission was assign to holon "+holon);
					// System.out.println("///////////////////////////////////");
					// for(int i=0;i<schedule.size();i++) {
					// if(schedule.isPickup(i))
					// System.out.println("Pickup "+schedule.getAllCommissions().get(i));
					// else
					// System.out.println("Delivery "+schedule.getAllCommissions().get(i));
					// }
					added = true;
					break;
				}
			}
			if (added == false) {
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen "
							+ comparator.toString() + " " + f);
					System.exit(0);
				}
				holons.put(holons.size(), schedule);
				// System.out.println("///////////////////////////////////");
				// for(int i=0;i<schedule.size();i++) {
				// if(schedule.isPickup(i))
				// System.out.println("Pickup "+schedule.getAllCommissions().get(i));
				// else
				// System.out.println("Delivery "+schedule.getAllCommissions().get(i));
				// }
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int new3Test2() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm2(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		// double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		for (Commission com : commissions) {
			added = false;
			bestCost = Double.MAX_VALUE;
			double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				extraTime = schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, schedule, 0);
				if (tmpSchedule != null) {
					extraTime = tmpSchedule.calculateTime(depot) - extraTime;
					// extraDistance =
					// tmpSchedule.getDistance(depot)-schedule.getDistance(depot);
					if (bestCost > extraTime) {// Helper.getRatio(extraDistance,
												// com)) {
						bestCost = tmpSchedule.calculateTime(depot);// Helper.getRatio(extraDistance,
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
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int new3Test2_dist() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm2(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));

		for (Commission com : commissions) {
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
				schedule = algorithm.makeSchedule(null, Commission.copy(com),
						null, null, 0);
				if (schedule == null) {
					System.err.println("Nie da sie zrealizowac zlecen");
					System.exit(0);
				}
				holons.put(holons.size(), schedule);
			} else {
				holons.put(bestHolon, bestSchedule);
			}
		}

		printStaus(holons);
		return holons.size();
	}

	public int STtest2() {
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

	public int STnew3Test2() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm2(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		Collections.sort(commissions, new CommissionsComparator(depot));
		for (Commission com : commissions) {
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

	private class CommissionsComparator implements Comparator<Commission> {
		private final Point2D.Double depot;

		public CommissionsComparator(Point2D.Double depot) {
			this.depot = depot;
		}

		public int compare(Commission com1, Commission com2) {
			double dist1;
			double dist2;
			double tmp;
			dist1 = Helper.calculateDistance(depot,
					new Point2D.Double(com1.getPickupX(), com1.getPickupY()));
			tmp = Helper
					.calculateDistance(
							depot,
							new Point2D.Double(com1.getDeliveryX(), com1
									.getDeliveryY()));
			if (tmp > dist1)
				dist1 = tmp;
			dist2 = Helper.calculateDistance(depot,
					new Point2D.Double(com2.getPickupX(), com2.getPickupY()));
			tmp = Helper
					.calculateDistance(
							depot,
							new Point2D.Double(com2.getDeliveryX(), com2
									.getDeliveryY()));
			if (tmp > dist2)
				dist2 = tmp;
			if (dist1 > dist2)
				return -1;
			else
				return 1;
		}
	}

	public int newST() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm2(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		Collections.sort(commissions, new CommissionsComparator(depot));
		Collections.sort(commissions, new CommissionsComparator(depot));

		for (Commission com : commissions) {
			added = false;
			bestCost = Double.MAX_VALUE;
			double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				// extraTime=schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, Schedule.copy(schedule), 0);
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
				Map<Integer, Schedule> tmp;

				tmp = SimmulatedTrading
						.simmulatedTrading(null, Helper.copy(holons),
								Commission.copy(com), algorithm, 0);
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
		}

		printStaus(holons);
		return holons.size();
	}

	public int newComplexST() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm2(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		Collections.sort(commissions, new CommissionsComparator(depot));
		Collections.sort(commissions, new CommissionsComparator(depot));

		for (Commission com : commissions) {
			added = false;
			bestCost = Double.MAX_VALUE;
			double extraTime;
			for (Integer holon : holons.keySet()) {
				schedule = holons.get(holon);
				// extraTime=schedule.calculateTime(depot);
				tmpSchedule = algorithm.makeSchedule(null,
						Commission.copy(com), null, Schedule.copy(schedule), 0);
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
				Map<Integer, Schedule> tmp;

				tmp = SimmulatedTrading.complexSimmulatedTrading(null,
						Helper.copy(holons), Commission.copy(com), algorithm,
						8, new TreeSet<Integer>(), 0, simInfo);
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
		return holons.size();
	}

	public int newComplexST_dist() {
		Map<Integer, Schedule> holons = new HashMap<Integer, Schedule>();
		Algorithm algorithm = new BruteForceAlgorithm2(200, simInfo);
		Schedule schedule;
		Schedule tmpSchedule;
		Schedule bestSchedule = null;
		boolean added;
		double extraDistance;
		Integer bestHolon = -1;
		double bestCost = Double.MAX_VALUE;

		// List<Commission> coms=new LinkedList<Commission>();
		// for(int i=0;i<53;i++) coms.add(commissions.get(i));
		Collections.sort(commissions, new CommissionsComparator(depot));
		Collections.sort(commissions, new CommissionsComparator(depot));

		for (Commission com : commissions) {
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
						9, new TreeSet<Integer>(), 0, simInfo);
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
		return holons.size();
	}

	public static void main(String args[]) {
		AlgorithmTest test = new AlgorithmTest("benchmarks/pdp_100/lrc101.txt",
				ComparatorType.time);
		// test.test2();
		// test.STtest2();
		// test.new3Test2_dist();
		// test.STnew3Test2();
		// test.newST();
		test.newComplexST_dist();
		// test.newHibridComplexST();
		// test.newTest();
		// test.new2Test2_dist();
	}
}
