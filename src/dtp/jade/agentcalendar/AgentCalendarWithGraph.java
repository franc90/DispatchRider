package dtp.jade.agentcalendar;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import dtp.commission.Commission;
import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.jade.crisismanager.crisisevents.EUnitFailureEvent;
import dtp.jade.eunit.LoadNotOkException;
import dtp.optimization.Astar;

public class AgentCalendarWithGraph implements AgentCalendar {

	private class Storage implements Comparable<Storage> {
		private final Double nr;
		private final Commission value;

		public Storage(Double nr, Commission value) {
			this.nr = nr;
			this.value = value;
		}

		public Double getNr() {
			return nr;
		}

		public Commission getValue() {
			return value;
		}

		public int compareTo(Storage s) {
			return nr.compareTo(s.getNr());
		}
	}

	Graph graph;

	@SuppressWarnings("rawtypes")
	private Hashtable commissions;

	@SuppressWarnings("rawtypes")
	private ActionQueueWithGraph schedule;

	private double maxLoad;

	public AgentCalendarWithGraph() {

	}

	@SuppressWarnings("unchecked")
	public List<CalendarAction> getSchedule() {
		return schedule;
	}

	@SuppressWarnings("rawtypes")
	public AgentCalendarWithGraph(Graph graph, double deadline, double maxLoad) {

		this.graph = graph;

		commissions = new Hashtable();

		this.maxLoad = maxLoad;

		schedule = new ActionQueueWithGraph<CalendarActionWithGraph>();
		initSchedule(deadline);
	}

	// dodaj 4 sztuczne akcje
	@SuppressWarnings("unchecked")
	private void initSchedule(double deadline) {

		CalendarActionWithGraph depotAction;

		depotAction = new CalendarActionWithGraph("DEPOT");
		depotAction.setCommissionID(-1);
		depotAction.setSourceCommissionID(0);
		depotAction.setSource(graph.getDepot());
		depotAction.setDestination(graph.getDepot());
		depotAction.setStartTime(deadline);
		depotAction.setEndTime(deadline);
		depotAction.setCurrentLoad(0);
		schedule.add(0, depotAction);

		depotAction = new CalendarActionWithGraph("WAIT");
		depotAction.setCommissionID(-1);
		depotAction.setSourceCommissionID(-1);
		depotAction.setSource(graph.getDepot());
		depotAction.setDestination(graph.getDepot());
		depotAction.setStartTime(0);
		depotAction.setEndTime(deadline);
		depotAction.setCurrentLoad(0);
		schedule.add(1, depotAction);

		depotAction = new CalendarActionWithGraph("DRIVE");
		depotAction.setCommissionID(-1);
		depotAction.setSourceCommissionID(-1);
		depotAction.setSource(graph.getDepot());
		depotAction.setDestination(graph.getDepot());
		depotAction.setTrack(new Astar(graph).findTrack(graph.getDepot(),
				graph.getDepot()));
		depotAction.setStartTime(0);
		depotAction.setEndTime(0);
		depotAction.setCurrentLoad(0);
		schedule.add(2, depotAction);

		depotAction = new CalendarActionWithGraph("DEPOT");
		depotAction.setCommissionID(-1);
		depotAction.setSourceCommissionID(0);
		depotAction.setSource(graph.getDepot());
		depotAction.setDestination(graph.getDepot());
		depotAction.setStartTime(0);
		depotAction.setEndTime(0);
		depotAction.setCurrentLoad(0);
		schedule.add(3, depotAction);
	}

	@SuppressWarnings("rawtypes")
	public void setCommissions(Hashtable commissions) {

		this.commissions = commissions;
	}

	@SuppressWarnings("rawtypes")
	public void setSchedule(ActionQueueWithGraph schedule) {

		this.schedule = schedule;
	}

	public void setMaxLoad(double maxLoad) {

		this.maxLoad = maxLoad;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}

	public Commission getCommissionByID(int comID) {

		return (Commission) commissions.get(String.valueOf(comID));
	}

	@SuppressWarnings("rawtypes")
	public double addCommission(Commission com, int timestamp) {

		ActionQueueWithGraph backupSchedulePickup;
		ActionQueueWithGraph backupScheduleDelivery;

		CalendarActionWithGraph curAction;
		CalendarActionWithGraph nextPDDAction;

		CalendarActionWithGraph backupCurActionPickup;
		CalendarActionWithGraph backupCurActionDelivery;
		CalendarActionWithGraph backupPickupAction;

		// load
		CalendarActionWithGraph pickupAction;
		CalendarActionWithGraph deliveryAction;

		ActionQueueWithGraph minSchedule;
		double minExtraDistance;
		double originalDistance;
		double loadDiffrence = 0;

		minSchedule = null;
		minExtraDistance = Double.MAX_VALUE;
		originalDistance = calculateWholeDistance();

		curAction = (CalendarActionWithGraph) schedule.getLast();
		nextPDDAction = getNextPDSDAction(curAction);

		do {

			int tmpIndex = schedule.indexOf(curAction);
			backupSchedulePickup = schedule.backup();
			backupCurActionPickup = (CalendarActionWithGraph) backupSchedulePickup
					.get(tmpIndex);

			boolean pickup;

			pickup = true;
			pickupAction = null;
			// pickupAction - zwraca stworzona akcje PICKUP
			pickupAction = putCom(com, pickup, curAction, nextPDDAction,
					timestamp);

			if (pickupAction == null) {

				elbowLeft(curAction, timestamp);
				elbowRight(curAction);
				pickupAction = putCom(com, pickup, curAction, nextPDDAction,
						timestamp);
			}

			// PICKUP OK
			if (pickupAction != null) {

				addComToHashtable(com);

				backupPickupAction = pickupAction.clone();

				curAction = getNextPDSDAction(curAction); // new PICKUP

				do {

					tmpIndex = schedule.indexOf(curAction);
					backupScheduleDelivery = schedule.backup();
					backupCurActionDelivery = (CalendarActionWithGraph) backupScheduleDelivery
							.get(tmpIndex);

					pickup = false;
					deliveryAction = null;
					deliveryAction = putCom(com, pickup, curAction,
							nextPDDAction, timestamp);

					if (deliveryAction == null) {

						elbowLeft(curAction, timestamp);
						elbowRight(curAction);
						deliveryAction = putCom(com, pickup, curAction,
								nextPDDAction, timestamp);
					}

					if (deliveryAction != null) {

						// update load
						double loadOK = addLoad(com, pickupAction,
								deliveryAction);

						if (loadOK <= 0) {

							// calculate extra distance
							double extraDistance = calculateWholeDistance()
									- originalDistance;

							// save schedule (if better)
							if (extraDistance < minExtraDistance) {

								minExtraDistance = extraDistance;
								minSchedule = schedule.backup();
							}
						} else {
							loadDiffrence = loadOK;
							System.out
									.println("AgentCalendar -> LOAD NOT OK >> "
											+ loadDiffrence);
						}

						schedule = backupScheduleDelivery;
						curAction = backupCurActionDelivery;
						pickupAction = backupPickupAction;
					}

					curAction = getNextPDSDAction(curAction);
					nextPDDAction = getNextPDSDAction(curAction);

				} while (nextPDDAction != null); // END do DELIVERY

				schedule = backupSchedulePickup;
				curAction = backupCurActionPickup;
			}

			curAction = getNextPDSDAction(curAction);
			nextPDDAction = getNextPDSDAction(curAction);

		} while (nextPDDAction != null); // END do PICKUP

		if (minSchedule != null) {

			schedule = minSchedule;
			optimizeSchedule(timestamp);

			return -1;

		} else {

			removeComFromHashtable(String.valueOf(com.getID()));

			return loadDiffrence;
		}
	}

	public boolean removeCommission(Commission com) {

		CalendarActionWithGraph pickupActionToRemove;
		CalendarActionWithGraph deliveryActionToRemove;

		// no such commission in hashtable
		if (getComFromHashtable(String.valueOf(com.getID())) == null) {

			System.out
					.println("AgentCalendar.removeCommission() -> no such commission in hashtable, com Id = "
							+ com.getID());
			return false;
		}

		pickupActionToRemove = getPickupAction(com);
		deliveryActionToRemove = getDeliveryAction(com);

		if (pickupActionToRemove == null || deliveryActionToRemove == null) {

			return false;
		}

		// update load
		removeLoad(com, pickupActionToRemove, deliveryActionToRemove);

		// remove PICKUP action
		removeAction(pickupActionToRemove);

		// remove DELIVERY action
		removeAction(deliveryActionToRemove);

		// remove com from hashtable
		removeComFromHashtable(String.valueOf(com.getID()));

		return true;
	}

	// na potrzeby sytuacji kryzysowych....
	public boolean removeCommission(int commissionID, GraphPoint graphPoint,
			int timestamp) {

		Commission com = getComFromHashtable(String.valueOf(commissionID));

		CalendarActionWithGraph pickupActionToRemove;
		CalendarActionWithGraph deliveryActionToRemove;

		// no such commission in hashtable
		if (com == null) {

			// System.out.println("AgentCalendar.removeCommission() -> no such commission in hashtable, com Id = "
			// + com.getID());
			return false;
		}

		pickupActionToRemove = getPickupAction(com);
		deliveryActionToRemove = getDeliveryAction(com);

		if (pickupActionToRemove == null || deliveryActionToRemove == null) {

			return false;
		}

		// update load
		removeLoad(com, pickupActionToRemove, deliveryActionToRemove);

		// remove PICKUP action
		removeAction(pickupActionToRemove, graphPoint, timestamp);

		// remove DELIVERY action
		removeAction(deliveryActionToRemove, graphPoint, timestamp);

		// remove com from hashtable
		removeComFromHashtable(String.valueOf(com.getID()));

		return true;
	}

	public double getExtraDistance(Commission com, int timestamp)
			throws LoadNotOkException {

		double oldDistance;
		double newDistance;

		double comAdded;

		oldDistance = getDistance();

		comAdded = addCommission(com, timestamp);

		if (comAdded < 0) {

			newDistance = getDistance();
			removeCommission(com);
			return newDistance - oldDistance;
		} else if (comAdded == 0) {
			return -1;
		} else {
			LoadNotOkException e = new LoadNotOkException();
			e.setLoadDiffrence(comAdded);
			throw e;
		}
	}

	// zwraca zlecenie, ktore powoduje najwiekszy dodatkowy koszt przejazdu
	// (dystans)
	// usuwa go!
	@SuppressWarnings("rawtypes")
	public Commission getWorstCommission(int timestamp, int STDepth) {

		if (STDepth > commissions.size())
			return null;
		Commission[] allCommissions;
		Hashtable comsBackup;
		ActionQueueWithGraph scheduleBackup;

		allCommissions = getComsTable();

		if (allCommissions.length == 0) {

			return null;
		}

		comsBackup = (Hashtable) commissions.clone();
		scheduleBackup = schedule.backup();

		List<Storage> extraDistances = new LinkedList<Storage>();

		for (int i = 0; i < allCommissions.length; i++) {

			double distanceWithCom;
			double distanceWithoutCom;

			distanceWithCom = getDistance();

			removeCommission(allCommissions[i]);

			distanceWithoutCom = getDistance();

			commissions = (Hashtable) comsBackup.clone();
			schedule = scheduleBackup.backup();

			extraDistances.add(new Storage(
					distanceWithoutCom - distanceWithCom, allCommissions[i]));
		}

		Collections.sort(extraDistances);
		Collections.reverse(extraDistances);

		for (int i = STDepth - 1; i < extraDistances.size(); i++) {
			// nie usuwaj zlecenia po dokonaniu PICKUP
			// -100 -> wymysl cos lepszego :|
			if (extraDistances.get(i).getValue().getPickupTime1() - 100 > timestamp) {
				removeCommission(extraDistances.get(i).getValue());
				return extraDistances.get(i).getValue();
			}
		}

		return null;
	}

	public double getDistance() {

		return calculateWholeDistance();
	}

	public double getWaitTime() {

		return calculateWholeWaitTime();
	}

	public CalendarActionWithGraph getCurrentCalendarAction(int timestamp) {

		CalendarActionWithGraph currentAction;

		if (timestamp < 0) {
			return null;
		}

		currentAction = (CalendarActionWithGraph) schedule.getFirst();

		while (currentAction != null) {

			if (currentAction.getStartTime() <= timestamp
					&& currentAction.getEndTime() >= timestamp) {

				return currentAction;
			}

			currentAction = schedule.getPrevAction(currentAction);
		}

		System.out
				.println("AgentCalendarWithGraph.getCurrentCalendarAction() -> null "
						+ timestamp);
		System.out.println(this.toString());

		return null;
	}

	public GraphPoint getCurrentGraphPoint(int timestamp) {

		CalendarActionWithGraph curAction;

		curAction = getCurrentCalendarAction(timestamp);

		if (curAction.getType() == "DRIVE") {

			return graph.getPointByCoordinates(curAction.getTrack()
					.getCurrentLocation(curAction.getStartTime(),
							curAction.getEndTime(), timestamp));

		} else if (curAction.getType() == "DEPOT"
				|| curAction.getType() == "PICKUP"
				|| curAction.getType() == "DELIVERY"
				|| curAction.getType() == "WAIT"
				|| curAction.getType() == "SWITCH"
				|| curAction.getType() == "BROKEN") {

			return graph.getPointByCoordinates(curAction.getSource().getX(),
					curAction.getSource().getY());

		} else {

			System.out
					.println("AgentCalendarWithGraph.getCurrentGraphPoint() -> "
							+ "unknown action type = " + curAction.getType());
		}

		return null;
	}

	public GraphLink getCurrentGraphLink(int timestamp) {

		CalendarActionWithGraph curAction;

		curAction = getCurrentCalendarAction(timestamp);

		if (curAction.getType() == "DRIVE") {

			return curAction.getTrack()
					.getCurrentGraphLink(curAction.getStartTime(),
							curAction.getEndTime(), timestamp);

		}

		System.out
				.println("AgentCalendarWithGraph.getCurrentGraphLink() -> sth wrong");
		return null;
	}

	public GraphPoint getSetCurrentGraphPoint(int timestamp) {

		GraphPoint currentGraphPoint;

		currentGraphPoint = getCurrentGraphPoint(timestamp);

		if (currentGraphPoint == null) {

			GraphLink tmpGraphLink = getCurrentGraphLink(timestamp);

			if (tmpGraphLink != null) {

				// dodaj nowy GraphPoint
				Point2D currentLocation = getCurrentLocation(timestamp);
				GraphPoint newGraphPoint = new GraphPoint(
						currentLocation.getX(), currentLocation.getY());
				graph.addPointOnLink(newGraphPoint, tmpGraphLink);
				currentGraphPoint = newGraphPoint;
			}
		}

		return currentGraphPoint;
	}

	public Point2D getCurrentLocation(int timestamp) {

		CalendarActionWithGraph currentCalendarAction;

		GraphTrack track;

		currentCalendarAction = getCurrentCalendarAction(timestamp);

		if (currentCalendarAction.getType() != "DRIVE") {

			// eqauls destination location
			return new Point2D.Double(currentCalendarAction.getSource().getX(),
					currentCalendarAction.getSource().getY());
		}

		// DRIVE action

		track = currentCalendarAction.getTrack();

		return track.getCurrentLocation(currentCalendarAction.getStartTime(),
				currentCalendarAction.getEndTime(), timestamp);
	}

	public boolean containsCommission(int commissionID) {

		return commissions.containsKey(String.valueOf(commissionID));
	}

	public boolean isCommissionAlreadyPickedUp(int commissionID, int timestamp) {

		if (!containsCommission(commissionID))
			return false;

		double pickupTime = getPickupActionStartTimeForCommission(commissionID);

		if (pickupTime > timestamp)
			return false;
		else
			return true;
	}

	public double getPickupActionStartTimeForCommission(int commissionID) {

		if (!containsCommission(commissionID))
			return -1;

		CalendarActionWithGraph tmpAction;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);
			if (tmpAction.getType().equals("PICKUP")
					&& tmpAction.getCommissionID() == commissionID) {

				return tmpAction.getStartTime();
			}
		}

		return -1;
	}

	/**
	 * Zwraca kolejne zlecenie (akcja PICKUP) w kalendarzu za commission. Jezeli
	 * commission jest ostatnie, zwraca null
	 */
	public Commission getCommissionAfter(Commission commission) {

		if (!containsCommission(commission.getID()))
			return null;

		CalendarActionWithGraph thisComPickupAction = null;
		CalendarActionWithGraph nextPDDAction = null;
		CalendarActionWithGraph tmpAction = null;

		// wyciagnij akcje PICKUP dla zlecenia commission
		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);
			if (tmpAction.getType().equals("PICKUP")
					&& tmpAction.getCommissionID() == commission.getID()) {

				thisComPickupAction = tmpAction;
				continue;
			}
		}

		// wyciagnij nastepna akcje PICKUP, tymsamym kolejne zlecenie
		nextPDDAction = getNextPDSDAction(thisComPickupAction);
		while (nextPDDAction.getType() != "DEPOT") {

			if (nextPDDAction.getType() == "PICKUP") {

				return (Commission) commissions.get(String
						.valueOf(nextPDDAction.getCommissionID()));
			}

			nextPDDAction = getNextPDSDAction(nextPDDAction);

		}

		return null;
	}

	public ArrayList<Commission> getCommissionsAfter(int timestamp) {

		ArrayList<Commission> coms;
		CalendarActionWithGraph nextPDDAction = null;
		CalendarActionWithGraph tmpAction = null;

		coms = new ArrayList<Commission>();
		tmpAction = getCurrentCalendarAction(timestamp);

		// wyciagnij nastepna akcje PICKUP, tymsamym kolejne zlecenie
		nextPDDAction = getNextPDSDAction(tmpAction);
		while (nextPDDAction.getType() != "DEPOT") {

			if (nextPDDAction.getType() == "PICKUP") {

				coms.add((Commission) commissions.get(String
						.valueOf(nextPDDAction.getCommissionID())));
			}

			nextPDDAction = getNextPDSDAction(nextPDDAction);

		}

		return coms;
	}

	public void addActionBroken(EUnitFailureEvent crisisEvent, int timestamp) {

		CalendarActionWithGraph brokenAction;
		CalendarActionWithGraph curAction;
		CalendarActionWithGraph nextPDSDAction;

		GraphPoint curGraphPoint;
		GraphTrack track;
		double travelTime;

		curGraphPoint = getSetCurrentGraphPoint(timestamp);
		curAction = getCurrentCalendarAction(timestamp);
		nextPDSDAction = getNextPDSDAction(curAction);

		brokenAction = new CalendarActionWithGraph();
		brokenAction.setType("BROKEN");
		brokenAction.setCurrentLoad(curAction.getCurrentLoad());
		brokenAction.setSource(curGraphPoint);
		brokenAction.setDestination(curGraphPoint);

		// dla akcji DRIVE zmodyfikuj trase i czas
		if (curAction.getType() == "DRIVE") {

			GraphTrack tmpTrack = curAction.getTrack()
					.getGraphTractToTimestamp(curAction.getStartTime(),
							curAction.getEndTime(), timestamp, curGraphPoint);
			curAction.setTrack(tmpTrack);
			curAction.setDestination(curGraphPoint);
			curAction.setEndTime(timestamp);

		}

		// dla akcji PICKUP lub DELIVERY ustaw inne czasy dla akcji BROKEN
		if (curAction.getType() == "PICKUP"
				|| curAction.getType() == "DELIVERY"
				|| curAction.getType() == "SWITCH"
				|| curAction.getType() == "BROKEN") {

			brokenAction.setStartTime(curAction.getEndTime());
			brokenAction.setEndTime(curAction.getEndTime()
					+ crisisEvent.getFailureDuration());

		} else if (curAction.getType() == "DEPOT"
				|| curAction.getType() == "DRIVE"
				|| curAction.getType() == "WAIT") {

			if (curAction.getType() == "WAIT") {

				curAction.setEndTime(timestamp);
			}

			brokenAction.setStartTime(timestamp);
			brokenAction.setEndTime(timestamp
					+ crisisEvent.getFailureDuration());

		} else {

			System.out.println("AgentCalendarWithGraph.addActionBroken() -> "
					+ "unknown action type = " + curAction.getType());
		}

		// usun wszystkie akcje pomiedzy brokenAction a nextPDSDAction
		CalendarActionWithGraph tmpNextAction = schedule
				.getNextAction(curAction);
		while (!tmpNextAction.equals(nextPDSDAction)) {

			CalendarActionWithGraph toRemove = tmpNextAction;
			tmpNextAction = schedule.getNextAction(tmpNextAction);
			schedule.remove(toRemove);
		}

		// wstaw akcje BROKEN
		schedule.putActionAfter(brokenAction, curAction);

		track = findTrack(brokenAction.getDestination(),
				nextPDSDAction.getSource());
		travelTime = track.getCost();

		// zrob miejsce na dojazd do nastepnej akcji PDD
		elbowRight(brokenAction);

		// usun wszystkie akcje pomiedzy brokenAction a nextPDSDAction
		// jeszcze raz, poniewaz elbowRight wstawia akcje WAIT pomiedzy BROKEN a
		// next PDD
		tmpNextAction = schedule.getNextAction(brokenAction);
		while (!tmpNextAction.equals(nextPDSDAction)) {

			CalendarActionWithGraph toRemove = tmpNextAction;
			tmpNextAction = schedule.getNextAction(tmpNextAction);
			schedule.remove(toRemove);
		}

		// jezeli nie zdaze dostarczyc przesylki, ustawiam jej okno czasowe oraz
		// wszystkich kolejnych (moze niepotrzebnie !!!) na max
		if (nextPDSDAction.getStartTime() - brokenAction.getEndTime() < travelTime) {

			CalendarActionWithGraph tmpAction = nextPDSDAction;
			while (tmpAction.getType() != "DEPOT") {

				if (tmpAction.getType() == "DELIVERY") {

					getComFromHashtable(tmpAction.getCommissionID())
							.setDeliveryTime2(Double.MAX_VALUE);
				}

				tmpAction = getNextPDSDAction(tmpAction);
			}
		}

		// ZNOWU zrob miejsce na dojazd do nastepnej akcji PDD
		elbowRight(brokenAction);

		// ZNOWU usun wszystkie akcje pomiedzy brokenAction a nextPDSDAction
		// jeszcze raz, poniewaz elbowRight wstawia akcje WAIT pomiedzy BROKEN a
		// next PDD
		tmpNextAction = schedule.getNextAction(brokenAction);
		while (!tmpNextAction.equals(nextPDSDAction)) {

			CalendarActionWithGraph toRemove = tmpNextAction;
			tmpNextAction = schedule.getNextAction(tmpNextAction);
			schedule.remove(toRemove);
		}

		// teraz problemem jest deadline
		if (nextPDSDAction.getStartTime() - brokenAction.getEndTime() < travelTime) {

			System.out.println("AgentCalendarWithGraph.addActionBroken() -> "
					+ "deadline reached :(");

		} else {

			// wstaw akcje DRIVE
			CalendarActionWithGraph newDriveNextAction = new CalendarActionWithGraph(
					-1, -1, "DRIVE", brokenAction.getDestination(),
					nextPDSDAction.getSource(), track,
					brokenAction.getEndTime(), brokenAction.getEndTime()
							+ travelTime, curAction.getCurrentLoad());

			schedule.putActionAfter(newDriveNextAction, brokenAction);

			// wstaw akcje WAIT
			CalendarActionWithGraph newWaitBeforeNextAction = new CalendarActionWithGraph(
					-1, -1, "WAIT", nextPDSDAction.getSource(),
					nextPDSDAction.getSource(), null,
					newDriveNextAction.getEndTime(),
					nextPDSDAction.getStartTime(), curAction.getCurrentLoad());

			schedule.putActionAfter(newWaitBeforeNextAction, newDriveNextAction);

		}

		optimizeSchedule(timestamp);
	}

	public void print() {

		CalendarActionWithGraph tmpAction;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);
			tmpAction.print();
		}
		System.out.println("distance = " + getDistance());
	}

	@SuppressWarnings("rawtypes")
	public void printComs() {

		Enumeration keys = commissions.keys();
		String key;

		while (keys.hasMoreElements()) {

			key = (String) keys.nextElement();
			System.out.println("key = " + key);
			System.out.println("com = \n" + commissions.get(key).toString());
		}
	}

	@Override
	public String toString() {

		CalendarActionWithGraph tmpAction;
		StringBuilder str;

		str = new StringBuilder();

		str.append("-----------------------------------------------\n");

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);
			str.append(tmpAction.toString());
		}

		str.append("-----------------------------------------------\n");
		str.append("distance = " + getDistance() + "\n");
		str.append("-----------------------------------------------\n");

		return str.toString();
	}

	/**
	 * Dla pustych kalendarzy zwraca odpowiedni napis
	 * 
	 * @return
	 */
	public String toShortString() {

		CalendarActionWithGraph tmpAction;
		StringBuilder str;

		str = new StringBuilder();

		// Calendar is empty
		if (schedule.size() == 4) {

			str.append("-----------------------------------------------\n");
			str.append("Calendar is empty...\n");
			str.append("-----------------------------------------------\n");

			return str.toString();
		}

		str.append("-----------------------------------------------\n");

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);
			str.append(tmpAction.toString());
		}

		str.append("-----------------------------------------------\n");
		str.append("distance = " + getDistance() + "\n");
		str.append("-----------------------------------------------\n");

		return str.toString();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public AgentCalendarWithGraph clone() {

		AgentCalendarWithGraph tmpAgentCalendar = new AgentCalendarWithGraph();

		tmpAgentCalendar.setCommissions((Hashtable) commissions.clone());
		tmpAgentCalendar.setSchedule(schedule.backup());
		tmpAgentCalendar.setMaxLoad(maxLoad);
		tmpAgentCalendar.setGraph(this.graph);

		return tmpAgentCalendar;
	}

	// sprawdz czy mozna zrobic pickup/delivery
	// pomiedzy prevAction, a nextAction
	// prevAction i nextAction to PICKUP, DELIVERY lub DEPOT
	// zwraca najwczesniejszy czas w jakim moze rozpoczac sie zlecenie
	// (uwzglednia czasy dojazdu)
	private double comFits(Commission com, boolean isPickup,
			CalendarActionWithGraph prevPDDAction,
			CalendarActionWithGraph nextPDDAction, int timestamp) {

		GraphTrack trackFromPrev;
		GraphTrack trackToNext;

		double timeDriveFromPrev;
		double timeDriveToNext;
		double timeService;
		double timeTotal;

		if (!prevPDDAction.isPDD()) {

			System.out
					.println("AgentCalendar.comFits -> !prevActionPDD.isPDD()");
		}

		if (!nextPDDAction.isPDD()) {

			System.out
					.println("AgentCalendar.comFits -> !nextActionPDD.isPDD()");
		}

		// dla PICKUP, DELIVERY i DEPOT source location = destination location
		if (isPickup) {

			timeDriveFromPrev = 0;
			timeDriveToNext = 0;

			trackFromPrev = findTrack(
					prevPDDAction.getSource(),
					graph.getPointByCoordinates(com.getPickupX(),
							com.getPickupY()));

			timeDriveFromPrev = calculateTime(trackFromPrev);

			trackToNext = findTrack(
					graph.getPointByCoordinates(com.getPickupX(),
							com.getPickupY()), nextPDDAction.getSource());
			timeDriveToNext = calculateTime(trackToNext);

			timeService = com.getPickUpServiceTime();
		} else {

			trackFromPrev = findTrack(
					prevPDDAction.getSource(),
					graph.getPointByCoordinates(com.getDeliveryX(),
							com.getDeliveryY()));
			timeDriveFromPrev = calculateTime(trackFromPrev);

			trackToNext = findTrack(
					graph.getPointByCoordinates(com.getDeliveryX(),
							com.getDeliveryY()), nextPDDAction.getSource());
			timeDriveToNext = calculateTime(trackToNext);
			timeService = com.getDeliveryServiceTime();
		}

		timeTotal = timeDriveFromPrev + timeService + timeDriveToNext;

		// ograniczenie czasow dojazdu
		if (timeTotal > nextPDDAction.getStartTime()
				- prevPDDAction.getEndTime()) {

			return -1;
		}

		// wyznaczenie najwczesniejszego czasu w jakim moze rozpoczac sie
		// pickup/delivery
		double earliestTime = prevPDDAction.getEndTime() + timeDriveFromPrev;
		double latestTime = nextPDDAction.getStartTime() - timeService
				- timeDriveToNext;

		// uwzglednij timestamp
		if (earliestTime - timeDriveFromPrev < timestamp) {

			earliestTime = earliestTime + timestamp
					- (earliestTime - timeDriveFromPrev);
		}

		// ograniczenie okien czasowych
		double timeWindow1 = 0;
		double timeWindow2 = 0;
		if (isPickup) {

			timeWindow1 = com.getPickupTime1();
			timeWindow2 = com.getPickupTime2();

		} else {

			timeWindow1 = com.getDeliveryTime1();
			timeWindow2 = com.getDeliveryTime2();

		}

		return calculateEarliestPossibleTime(earliestTime, latestTime,
				timeWindow1, timeWindow2);
	}

	// wklada zlecenie pomiedzy prevActionPDD a nextActionPDD
	// przy zalozeniu, ze obie sa PDD
	private CalendarActionWithGraph putCom(Commission com, boolean isPickup,
			CalendarActionWithGraph prevPDDAction,
			CalendarActionWithGraph nextPDDAction, int timestamp) {

		CalendarActionWithGraph createdPDAction;

		// czas kiedy moze rozpoczac sie PICKUP lub DELIVERY przy zalozeniu, ze
		// DRIVE rozpocznie sie teraz (timestamp)
		double PDTime = comFits(com, isPickup, prevPDDAction, nextPDDAction,
				timestamp);
		double time;
		GraphTrack track;

		if (PDTime < 0) {

			return null;
		}

		// add DRIVE action
		CalendarActionWithGraph newDriveToAction;
		double earliestDriveStartTime;

		if (isPickup) {

			track = findTrack(
					prevPDDAction.getDestination(),
					graph.getPointByCoordinates(com.getPickupX(),
							com.getPickupY()));
			time = calculateTime(track);

			earliestDriveStartTime = Math.max(prevPDDAction.getEndTime(),
					timestamp);

			newDriveToAction = new CalendarActionWithGraph(-1, -1, "DRIVE",
					prevPDDAction.getDestination(),
					graph.getPointByCoordinates(com.getPickupX(),
							com.getPickupY()), track, earliestDriveStartTime,
					earliestDriveStartTime + time,
					prevPDDAction.getCurrentLoad());

		} else {

			track = findTrack(
					prevPDDAction.getDestination(),
					graph.getPointByCoordinates(com.getDeliveryX(),
							com.getDeliveryY()));
			time = calculateTime(track);

			earliestDriveStartTime = Math.max(prevPDDAction.getEndTime(),
					timestamp);

			newDriveToAction = new CalendarActionWithGraph(-1, -1, "DRIVE",
					prevPDDAction.getDestination(),
					graph.getPointByCoordinates(com.getDeliveryX(),
							com.getDeliveryY()), track, earliestDriveStartTime,
					earliestDriveStartTime + time,
					prevPDDAction.getCurrentLoad());
		}

		schedule.putActionAfter(newDriveToAction, prevPDDAction);

		// add WAIT action
		CalendarActionWithGraph newWaitBeforeAction;

		if (isPickup) {

			newWaitBeforeAction = new CalendarActionWithGraph(-1, -1, "WAIT",
					graph.getPointByCoordinates(com.getPickupX(),
							com.getPickupY()), graph.getPointByCoordinates(
							com.getPickupX(), com.getPickupY()), null,
					newDriveToAction.getEndTime(), PDTime,
					newDriveToAction.getCurrentLoad());
		} else {

			newWaitBeforeAction = new CalendarActionWithGraph(-1, -1, "WAIT",
					graph.getPointByCoordinates(com.getDeliveryX(),
							com.getDeliveryY()), graph.getPointByCoordinates(
							com.getDeliveryX(), com.getDeliveryY()), null,
					newDriveToAction.getEndTime(), PDTime,
					newDriveToAction.getCurrentLoad());
		}

		schedule.putActionAfter(newWaitBeforeAction, newDriveToAction);

		// add PICKUP/DELIVERY action
		CalendarActionWithGraph newPDAction = null;

		if (isPickup) {

			newPDAction = new CalendarActionWithGraph(com.getID(),
					com.getPickUpId(), "PICKUP", graph.getPointByCoordinates(
							com.getPickupX(), com.getPickupY()),
					graph.getPointByCoordinates(com.getPickupX(),
							com.getPickupY()), null, PDTime, PDTime
							+ com.getPickUpServiceTime(),
					newDriveToAction.getCurrentLoad());

			createdPDAction = newPDAction;

			schedule.putActionAfter(newPDAction, newWaitBeforeAction);

		} else {

			newPDAction = new CalendarActionWithGraph(com.getID(),
					com.getDeliveryId(), "DELIVERY",
					graph.getPointByCoordinates(com.getDeliveryX(),
							com.getDeliveryY()), graph.getPointByCoordinates(
							com.getDeliveryX(), com.getDeliveryY()), null,
					PDTime, PDTime + com.getDeliveryServiceTime(),
					newDriveToAction.getCurrentLoad());

			createdPDAction = newPDAction;

			schedule.putActionAfter(newPDAction, newWaitBeforeAction);
		}

		double oldDriveLoad;
		double oldWaitLoad;

		// wymien DRIVE i WAIT na nowe
		// usun DRIVE
		oldDriveLoad = schedule.getNextAction(newPDAction).getCurrentLoad();
		schedule.removeAction(schedule.getNextAction(newPDAction));
		// usun WAIT
		oldWaitLoad = schedule.getNextAction(newPDAction).getCurrentLoad();
		schedule.removeAction(schedule.getNextAction(newPDAction));

		CalendarActionWithGraph nextAction = schedule
				.getNextAction(newPDAction);

		track = findTrack(newPDAction.getDestination(), nextAction.getSource());
		time = calculateTime(track);

		CalendarActionWithGraph newDriveNextAction = new CalendarActionWithGraph(
				-1, -1, "DRIVE", newPDAction.getDestination(),
				nextAction.getSource(), track, newPDAction.getEndTime(),
				newPDAction.getEndTime() + time, oldDriveLoad);

		schedule.putActionAfter(newDriveNextAction, newPDAction);

		CalendarActionWithGraph newWaitBeforeNextAction = new CalendarActionWithGraph(
				-1, -1, "WAIT", nextAction.getSource(), nextAction.getSource(),
				null, newDriveNextAction.getEndTime(),
				nextAction.getStartTime(), oldWaitLoad);

		schedule.putActionAfter(newWaitBeforeNextAction, newDriveNextAction);

		return createdPDAction;
	}

	// rozepchnij w lewo wszystkie zlecenia przed curAction
	// (lacznie z curAction)
	private void elbowLeft(CalendarActionWithGraph curAction, int timestamp) {

		// current PD action to elbow left
		CalendarActionWithGraph tmpPDDAction;
		CalendarActionWithGraph tmpPDDActionNext;
		CalendarActionWithGraph tmpPDDActionPrev;

		Commission tmpCom;

		GraphTrack trackFromPrev;
		GraphTrack trackToNext;

		double timeDriveFromPrev;
		double timeDriveToNext;
		double timeService;

		double earliestPossibleTime;

		if (schedule.indexOf(curAction) >= schedule.size() - 1)
			return;

		// first DEPOT
		tmpPDDActionPrev = (CalendarActionWithGraph) schedule.getLast();
		// first PD
		tmpPDDAction = getNextPDSDAction(tmpPDDActionPrev);
		tmpPDDActionNext = getNextPDSDAction(tmpPDDAction);

		// jezeli w schedule sa tylko akcje DEPOT
		// nie mozna ich rozpychac
		if (tmpPDDActionNext == null) {

			return;
		}

		while (!tmpPDDActionPrev.equals(curAction)) {

			// nie ruszaj akcji SWITCH ani BROKEN
			// nie przesuwaj akcji ktora juz sie rozpoczela
			if (tmpPDDAction.getType() != "SWITCH"
					&& tmpPDDAction.getType() != "BROKEN"
					&& (tmpPDDAction.getStartTime() > timestamp)) {

				// dla PICKUP, DELIVERY i DEPOT source location = destination
				// location
				trackFromPrev = findTrack(tmpPDDActionPrev.getSource(),
						tmpPDDAction.getSource());
				timeDriveFromPrev = calculateTime(trackFromPrev);

				trackToNext = findTrack(tmpPDDAction.getSource(),
						tmpPDDActionNext.getSource());
				timeDriveToNext = calculateTime(trackToNext);

				timeService = tmpPDDAction.getEndTime()
						- tmpPDDAction.getStartTime();

				// wyznaczenie najwczesniejszego i najpozniejszego czasu w jakim
				// moze rozpoczac sie pickup/delivery
				double earliestTime;

				if (timestamp > tmpPDDActionPrev.getEndTime()) {

					// TODO timeDriveFromPrev trzeba zamienic na czas dojazdu z
					// biezacej lokalizacji do miejsca PICKUP/DELIVERY
					earliestTime = timestamp + timeDriveFromPrev;
				} else {
					earliestTime = tmpPDDActionPrev.getEndTime()
							+ timeDriveFromPrev;
				}

				double latestTime = tmpPDDActionNext.getStartTime()
						- timeService - timeDriveToNext;

				// ograniczenie okien czasowych
				tmpCom = getComFromHashtable(String.valueOf(tmpPDDAction
						.getCommissionID()));
				double timeWindow1 = 0;
				double timeWindow2 = 0;
				if (tmpPDDAction.getType().equals("PICKUP")) {

					timeWindow1 = tmpCom.getPickupTime1();
					timeWindow2 = tmpCom.getPickupTime2();

				} else if (tmpPDDAction.getType().equals("DELIVERY")) {

					timeWindow1 = tmpCom.getDeliveryTime1();
					timeWindow2 = tmpCom.getDeliveryTime2();

				} else {

					System.out
							.println("AgentCalendar.elbowLeft() -> sth wrong!");
				}

				earliestPossibleTime = Double.MAX_VALUE;

				if (latestTime < earliestTime) {

					return;

				} else if (earliestTime > timeWindow2) {

					return;

				} else if (earliestTime >= timeWindow1) {

					earliestPossibleTime = earliestTime;

				} else if (earliestTime < timeWindow1) {

					if (latestTime >= timeWindow1) {

						earliestPossibleTime = timeWindow1;

					} else {

						return;
					}

				} else {

					System.out.println("AgentCalendar.elbowLeft -> sth wrong!");
					// return;
				}

				double extraTime = tmpPDDAction.getStartTime()
						- earliestPossibleTime;

				if (extraTime > 0) {

					CalendarActionWithGraph tmpPrevWaitAction;
					CalendarActionWithGraph tmpNextDriveAction;
					CalendarActionWithGraph tmpNextWaitAction;

					// update tmpAction (PICKUP or DELIVERY)
					tmpPDDAction.setStartTime(earliestPossibleTime);
					tmpPDDAction.setEndTime(earliestPossibleTime + timeService);

					// update previous action (WAIT)
					tmpPrevWaitAction = schedule
							.getPreviousAction(tmpPDDAction);
					tmpPrevWaitAction.setEndTime(tmpPrevWaitAction.getEndTime()
							- extraTime);

					// update next action (DRIVE)
					tmpNextDriveAction = schedule.getNextAction(tmpPDDAction);
					tmpNextDriveAction.setStartTime(tmpNextDriveAction
							.getStartTime() - extraTime);
					tmpNextDriveAction.setEndTime(tmpNextDriveAction
							.getEndTime() - extraTime);

					// update action after next action :) (WAIT)
					tmpNextWaitAction = schedule
							.getNextAction(tmpNextDriveAction);
					tmpNextWaitAction.setStartTime(tmpNextWaitAction
							.getStartTime() - extraTime);
				}
			}

			tmpPDDActionPrev = tmpPDDAction;
			tmpPDDAction = tmpPDDActionNext;
			tmpPDDActionNext = getNextPDSDAction(tmpPDDActionNext);
		}
	}

	// rozepchnij w prawo wszystkie zlecenia po curAction
	private void elbowRight(CalendarActionWithGraph curAction) {

		// current PD action to elbow right
		CalendarActionWithGraph tmpPDDAction;
		CalendarActionWithGraph tmpPDDActionNext;
		CalendarActionWithGraph tmpPDDActionPrev;

		Commission tmpCom;

		GraphTrack trackFromPrev;
		GraphTrack trackToNext;

		double timeDriveFromPrev;
		double timeDriveToNext;
		double timeService;
		double timeTotal;

		double latestPossibleTime;

		if (schedule.indexOf(curAction) <= 1)
			return;

		// last DEPOT
		tmpPDDActionNext = (CalendarActionWithGraph) schedule.getFirst();
		// last PD
		tmpPDDAction = getPrevPDSDAction(tmpPDDActionNext);
		tmpPDDActionPrev = getPrevPDSDAction(tmpPDDAction);

		// w schedule sa tylko akcje DEPOT
		// nie mozna ich rozpychac
		if (tmpPDDActionPrev == null) {

			return;
		}

		while (!tmpPDDAction.equals(curAction)) {

			// nie ruszaj akcji SWITCH i BROKEN
			if (tmpPDDAction.getType() != "SWITCH"
					&& tmpPDDAction.getType() != "BROKEN") {

				// dla PICKUP, DELIVERY i DEPOT source location = destination
				// location
				trackFromPrev = findTrack(tmpPDDActionPrev.getSource(),
						tmpPDDAction.getSource());
				timeDriveFromPrev = calculateTime(trackFromPrev);

				trackToNext = findTrack(tmpPDDAction.getSource(),
						tmpPDDActionNext.getSource());
				timeDriveToNext = calculateTime(trackToNext);

				timeService = tmpPDDAction.getEndTime()
						- tmpPDDAction.getStartTime();

				timeTotal = timeDriveFromPrev + timeService + timeDriveToNext;

				// ograniczenie czasow dojazdu
				if (timeTotal > tmpPDDActionNext.getStartTime()
						- tmpPDDActionPrev.getEndTime()) {
				}

				// wyznaczenie najwczesniejszego i najpozniejszego czasu w jakim
				// moze rozpoczac sie pickup/delivery
				double earliestTime = tmpPDDActionPrev.getEndTime()
						+ timeDriveFromPrev;
				double latestTime = tmpPDDActionNext.getStartTime()
						- timeService - timeDriveToNext;

				// ograniczenie okien czasowych
				tmpCom = getComFromHashtable(String.valueOf(tmpPDDAction
						.getCommissionID()));
				double timeWindow1 = 0;
				double timeWindow2 = 0;
				if (tmpPDDAction.getType().equals("PICKUP")) {

					timeWindow1 = tmpCom.getPickupTime1();
					timeWindow2 = tmpCom.getPickupTime2();

				} else if (tmpPDDAction.getType().equals("DELIVERY")) {

					timeWindow1 = tmpCom.getDeliveryTime1();
					timeWindow2 = tmpCom.getDeliveryTime2();

				} else {

					System.out
							.println("AgentCalendar.elbowRight() -> sth wrong!");
				}

				latestPossibleTime = Double.MIN_VALUE;

				if (latestTime < earliestTime) {

					return;

				} else if (latestTime < timeWindow1) {

					return;

				} else if (latestTime <= timeWindow2) {

					latestPossibleTime = latestTime;

				} else if (latestTime > timeWindow2) {

					if (earliestTime <= timeWindow2) {

						latestPossibleTime = timeWindow2;

					} else {

						return;
					}

				} else {

					System.out
							.println("AgentCalendar.elbowRight -> sth wrong!");
					// return;
				}

				double extraTime = latestPossibleTime
						- tmpPDDAction.getStartTime();

				if (extraTime > 0) {

					// update tmpAction (PICKUP or DELIVERY)
					tmpPDDAction.setStartTime(latestPossibleTime);
					tmpPDDAction.setEndTime(latestPossibleTime + timeService);

					// wklej wypelniacz
					CalendarActionWithGraph tmpPrevAction = schedule
							.getPreviousAction(tmpPDDAction);
					if (tmpPrevAction.getType() == "WAIT") {

						tmpPrevAction.setEndTime(tmpPrevAction.getEndTime()
								+ extraTime);

					} else {

						CalendarActionWithGraph newWaitAction = new CalendarActionWithGraph(
								-1, -1, "WAIT", tmpPrevAction.getDestination(),
								tmpPDDAction.getSource(), null,
								tmpPrevAction.getEndTime(),
								tmpPDDAction.getStartTime(),
								tmpPrevAction.getCurrentLoad());

						schedule.putActionAfter(newWaitAction, tmpPrevAction);
					}

					// usun wszystkie akcje pomiedzy tmpPDDAction a
					// tmpPDDActionNext
					CalendarActionWithGraph tmpNextAction = schedule
							.getNextAction(tmpPDDAction);
					while (!tmpNextAction.equals(tmpPDDActionNext)) {

						CalendarActionWithGraph toRemove = tmpNextAction;
						tmpNextAction = schedule.getNextAction(tmpNextAction);
						schedule.remove(toRemove);
					}

					// wstaw akcje DRIVE
					CalendarActionWithGraph newDriveNextAction = new CalendarActionWithGraph(
							-1, -1, "DRIVE", tmpPDDAction.getDestination(),
							tmpPDDActionNext.getSource(), trackToNext,
							tmpPDDAction.getEndTime(),
							tmpPDDAction.getEndTime() + timeDriveToNext,
							tmpPDDAction.getCurrentLoad());

					schedule.putActionAfter(newDriveNextAction, tmpPDDAction);

					// wstaw akcje WAIT
					CalendarActionWithGraph newWaitBeforeNextAction = new CalendarActionWithGraph(
							-1, -1, "WAIT", tmpPDDActionNext.getSource(),
							tmpPDDActionNext.getSource(), null,
							newDriveNextAction.getEndTime(),
							tmpPDDActionNext.getStartTime(),
							tmpPDDAction.getCurrentLoad());

					schedule.putActionAfter(newWaitBeforeNextAction,
							newDriveNextAction);
				}
			}

			tmpPDDActionNext = tmpPDDAction;
			tmpPDDAction = tmpPDDActionPrev;
			tmpPDDActionPrev = getPrevPDSDAction(tmpPDDActionPrev);
		}
	}

	// dopycha wszystkie zlecenia do lewej
	// (aby wykonaly sie jak najwczesniej)
	private void optimizeSchedule(int timestamp) {

		CalendarActionWithGraph tmpAction;

		// last DEPOT
		tmpAction = (CalendarActionWithGraph) schedule.getFirst();
		// ostatnia akcja PD
		tmpAction = getPrevPDSDAction(tmpAction);

		if (tmpAction.getType().equals("DEPOT")) {

			return;
		}

		elbowLeft(tmpAction, timestamp);
	}

	// poprzednia akcja PICKUP, DELIVERY, SWITCH lub DEPOT
	private CalendarActionWithGraph getPrevPDSDAction(
			CalendarActionWithGraph action) {

		CalendarActionWithGraph tmpAction = action;

		do {

			tmpAction = schedule.getPreviousAction(tmpAction);

			if (tmpAction == null) {

				return null;
			}

			if (tmpAction.isPDD()) {

				return tmpAction;
			}

		} while (schedule.indexOf(tmpAction) < schedule.size());

		System.out
				.println("AgentCalendar.getPrevPDDAction() -> first DEPOT action not found");
		return null;
	}

	// nastepna akcja: PICKUP, DELIVERY, SWITCH lub DEPOT
	private CalendarActionWithGraph getNextPDSDAction(
			CalendarActionWithGraph action) {

		CalendarActionWithGraph tmpAction = action;

		do {

			tmpAction = schedule.getNextAction(tmpAction);

			if (tmpAction == null) {

				return null;
			}

			if (tmpAction.isPDD()) {

				return tmpAction;
			}

		} while (schedule.indexOf(tmpAction) > 0);

		System.out
				.println("AgentCalendar.getPrevPDDAction -> sth wrong! last DEPOT action not found");
		return null;
	}

	private GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint) {

		GraphTrack track;

		track = new Astar(graph).findTrack(startPoint, endPoint);

		if (track == null)
			System.out.println("findTrack returns null (" + startPoint + " .. "
					+ endPoint + ")");

		return track;
	}

	/**
	 * Wyznacza najwczesniejszy czas w ktorym moze rozpoczac sie realizacja
	 * zlecenia
	 */
	private double calculateEarliestPossibleTime(double earliestTime,
			double latestTime, double timeWindow1, double timeWindow2) {

		double actionTime;

		if (latestTime < earliestTime) {

			return -1;

		} else if (latestTime < timeWindow1) {

			return -1;

		} else if (earliestTime < timeWindow1 && latestTime >= timeWindow1) {

			actionTime = timeWindow1;

		} else if (earliestTime >= timeWindow1 && latestTime <= timeWindow2) {

			actionTime = earliestTime;

		} else if (earliestTime <= timeWindow2 && latestTime >= timeWindow2) {

			actionTime = earliestTime;

		} else if (earliestTime > timeWindow2) {

			return -1;

		} else if (earliestTime < timeWindow1 && latestTime > timeWindow2) {

			actionTime = timeWindow1;

		} else {

			return -1;
		}

		return actionTime;
	}

	private double calculateTime(GraphTrack track) {

		return track.getCost();
	}

	private double calculateWholeDistance() {

		CalendarActionWithGraph tmpAction;
		double wholeDistance;

		wholeDistance = 0;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);

			if (tmpAction.getType() == "DRIVE") {

				wholeDistance += tmpAction.getTrack().getCost();
			}
		}

		return wholeDistance;
	}

	private double calculateWholeWaitTime() {

		CalendarActionWithGraph tmpAction;
		CalendarActionWithGraph tmpActionNext;
		double wholeTime;

		wholeTime = 0;

		for (int i = schedule.size() - 1; i > 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);

			tmpActionNext = (CalendarActionWithGraph) schedule.get(i - 1);

			if (tmpAction.getType() == "WAIT"
					&& tmpActionNext.getType() != "DEPOT") {

				wholeTime += tmpAction.getEndTime() - tmpAction.getStartTime();
			}
		}

		return wholeTime;
	}

	@SuppressWarnings("unchecked")
	private void addComToHashtable(Commission com) {

		commissions.put(Integer.toString(com.getID()), com);
	}

	private Commission getComFromHashtable(String key) {

		return (Commission) commissions.get(key);
	}

	private Commission getComFromHashtable(int comID) {

		return (Commission) commissions.get(String.valueOf(comID));
	}

	private void removeComFromHashtable(String key) {

		commissions.remove(key);
	}

	private double addLoad(Commission com,
			CalendarActionWithGraph pickupAction,
			CalendarActionWithGraph deliveryAction) {

		CalendarActionWithGraph tmpAction;
		boolean isBetween;
		double loadOK = 0;

		isBetween = false;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);

			if (tmpAction.equals(pickupAction)) {

				isBetween = true;

			} else if (tmpAction.equals(deliveryAction)) {

				isBetween = false;
			}

			if (isBetween) {

				tmpAction.setCurrentLoad(tmpAction.getCurrentLoad()
						+ com.getLoad());

				if (tmpAction.getCurrentLoad() - maxLoad > loadOK) {

					loadOK = tmpAction.getCurrentLoad() - maxLoad;
				}
			}
		}

		return loadOK;
	}

	private void removeLoad(Commission com,
			CalendarActionWithGraph pickupAction,
			CalendarActionWithGraph deliveryAction) {

		CalendarActionWithGraph tmpAction;
		boolean isBetween;

		isBetween = false;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);

			if (tmpAction.equals(pickupAction)) {

				isBetween = true;

			} else if (tmpAction.equals(deliveryAction)) {

				isBetween = false;
			}

			if (isBetween) {

				tmpAction.setCurrentLoad(tmpAction.getCurrentLoad()
						- com.getLoad());

			}
		}
	}

	private CalendarActionWithGraph getPickupAction(Commission com) {

		CalendarActionWithGraph tmpAction;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);
			if (tmpAction.getType().equals("PICKUP")
					&& tmpAction.getCommissionID() == com.getID()) {

				return tmpAction;
			}
		}

		return null;
	}

	private CalendarActionWithGraph getDeliveryAction(Commission com) {

		CalendarActionWithGraph tmpAction;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);
			if (tmpAction.getType().equals("DELIVERY")
					&& tmpAction.getCommissionID() == com.getID()) {

				return tmpAction;
			}
		}

		return null;
	}

	private boolean removeAction(CalendarActionWithGraph action) {

		CalendarActionWithGraph tmpPrevPDDAction;
		CalendarActionWithGraph tmpNextPDDAction;

		CalendarActionWithGraph tmpPrevDriveAction;
		CalendarActionWithGraph tmpPrevWaitAction;
		CalendarActionWithGraph tmpNextDriveAction;
		CalendarActionWithGraph tmpNextWaitAction;

		CalendarActionWithGraph newDriveAction;
		CalendarActionWithGraph newWaitAction;

		GraphTrack track;
		double time;

		if (!schedule.contains(action)) {

			return false;
		}

		tmpPrevPDDAction = getPrevPDSDAction(action);
		tmpNextPDDAction = getNextPDSDAction(action);

		tmpPrevDriveAction = schedule.getNextAction(tmpPrevPDDAction);
		tmpPrevWaitAction = schedule.getPreviousAction(action);

		tmpNextDriveAction = schedule.getNextAction(action);
		tmpNextWaitAction = schedule.getPreviousAction(tmpNextPDDAction);

		schedule.remove(tmpPrevDriveAction);
		schedule.remove(tmpPrevWaitAction);
		schedule.remove(tmpNextDriveAction);
		schedule.remove(tmpNextWaitAction);

		schedule.remove(action);

		track = findTrack(tmpPrevPDDAction.getDestination(),
				tmpNextPDDAction.getSource());
		time = calculateTime(track);

		// add DRIVE action
		newDriveAction = new CalendarActionWithGraph(-1, -1, "DRIVE",
				tmpPrevPDDAction.getDestination(),
				tmpNextPDDAction.getSource(), track,
				tmpPrevPDDAction.getEndTime(), tmpPrevPDDAction.getEndTime()
						+ time, tmpPrevPDDAction.getCurrentLoad());

		schedule.putActionAfter(newDriveAction, tmpPrevPDDAction);

		// add WAIT action
		newWaitAction = new CalendarActionWithGraph(-1, -1, "WAIT",
				newDriveAction.getDestination(),
				newDriveAction.getDestination(), null,
				newDriveAction.getEndTime(), tmpNextPDDAction.getStartTime(),
				newDriveAction.getCurrentLoad());

		schedule.putActionAfter(newWaitAction, newDriveAction);

		return true;
	}

	private boolean removeAction(CalendarActionWithGraph action,
			GraphPoint currentLocation, int timestamp) {

		CalendarActionWithGraph tmpPrevPDDAction;
		CalendarActionWithGraph tmpNextPDDAction;

		CalendarActionWithGraph tmpDriveAfterPrevPDSDAction;
		CalendarActionWithGraph tmpWaitAfterDriveAfterPrevPDSDAction;

		CalendarActionWithGraph newDriveAction;
		CalendarActionWithGraph newWaitAction;

		if (!schedule.contains(action)) {

			return false;
		}

		tmpPrevPDDAction = getPrevPDSDAction(action);
		tmpNextPDDAction = getNextPDSDAction(action);

		tmpDriveAfterPrevPDSDAction = schedule.getNextAction(tmpPrevPDDAction);
		tmpWaitAfterDriveAfterPrevPDSDAction = schedule
				.getNextAction(tmpDriveAfterPrevPDSDAction);

		// usun wszystkie akcje pomiedzy action a tmpPrevPDSDAction
		CalendarActionWithGraph tmpPrevAction = schedule
				.getPreviousAction(action);
		while (!tmpPrevAction.equals(tmpPrevPDDAction)) {

			CalendarActionWithGraph toRemove = tmpPrevAction;
			tmpPrevAction = schedule.getPreviousAction(tmpPrevAction);
			schedule.remove(toRemove);
		}

		// usun wszystkie akcje pomiedzy action a tmpNextPDSDAction
		CalendarActionWithGraph tmpNextAction = schedule.getNextAction(action);
		while (!tmpNextAction.equals(tmpNextPDDAction)) {

			CalendarActionWithGraph toRemove = tmpNextAction;
			tmpNextAction = schedule.getNextAction(tmpNextAction);
			schedule.remove(toRemove);
		}

		// usun sama akcje action
		schedule.remove(action);

		if (tmpPrevPDDAction.getEndTime() >= timestamp) {

			GraphTrack trackFromPrev = findTrack(
					tmpPrevPDDAction.getDestination(),
					tmpNextPDDAction.getSource());
			double timeFromPrev = calculateTime(trackFromPrev);

			// add DRIVE action
			newDriveAction = new CalendarActionWithGraph(-1, -1, "DRIVE",
					tmpPrevPDDAction.getDestination(),
					tmpNextPDDAction.getSource(), trackFromPrev,
					tmpPrevPDDAction.getEndTime(),
					tmpPrevPDDAction.getEndTime() + timeFromPrev,
					tmpPrevPDDAction.getCurrentLoad());

			schedule.putActionAfter(newDriveAction, tmpPrevPDDAction);

		} else {

			// add DRIVE to SWITCH location
			GraphTrack trackToSwitch = null;
			// przerwano podczas DRIVE
			// trasa do SWITCH obliczana na podstawie przerwanej trasy (od
			// poczatku do timestamp)
			CalendarActionWithGraph newDriveFromPrevAction = null;
			if (tmpDriveAfterPrevPDSDAction.getStartTime() <= timestamp
					&& tmpDriveAfterPrevPDSDAction.getEndTime() >= timestamp) {

				trackToSwitch = tmpDriveAfterPrevPDSDAction.getTrack()
						.getGraphTractToTimestamp(
								tmpDriveAfterPrevPDSDAction.getStartTime(),
								tmpDriveAfterPrevPDSDAction.getEndTime(),
								timestamp, currentLocation);

				newDriveFromPrevAction = new CalendarActionWithGraph(-1, -1,
						"DRIVE", tmpPrevPDDAction.getDestination(),
						currentLocation, trackToSwitch,
						tmpPrevPDDAction.getEndTime(), timestamp,
						tmpPrevPDDAction.getCurrentLoad());

				schedule.putActionAfter(newDriveFromPrevAction,
						tmpPrevPDDAction);
			}
			// przerwano podczas WAIT
			// trasa do SWITCH to ta sama trasa co dla poprz akcji DRIVE
			else if (tmpWaitAfterDriveAfterPrevPDSDAction.getStartTime() <= timestamp
					&& tmpWaitAfterDriveAfterPrevPDSDAction.getEndTime() >= timestamp) {

				// wloz stara akcje DRIVE
				newDriveFromPrevAction = tmpDriveAfterPrevPDSDAction;
				schedule.putActionAfter(newDriveFromPrevAction,
						tmpPrevPDDAction);

			} else {

				System.out
						.println("AgentCalWithGraph.removeAction() -> sth wrong!");
			}

			// add WAIT action
			CalendarActionWithGraph newWaitBeforeSwitchAction;
			newWaitBeforeSwitchAction = new CalendarActionWithGraph(-1, -1,
					"WAIT", newDriveFromPrevAction.getDestination(),
					currentLocation, null, newDriveFromPrevAction.getEndTime(),
					timestamp, newDriveFromPrevAction.getCurrentLoad());

			schedule.putActionAfter(newWaitBeforeSwitchAction,
					newDriveFromPrevAction);

			// add SWITCH action
			CalendarActionWithGraph newSwitchAction;
			newSwitchAction = new CalendarActionWithGraph(-1, -1, "SWITCH",
					currentLocation, currentLocation, null, timestamp,
					timestamp, tmpPrevPDDAction.getCurrentLoad());

			schedule.putActionAfter(newSwitchAction, newWaitBeforeSwitchAction);

			// add DRIVE to next action
			GraphTrack trackToNext = findTrack(currentLocation,
					tmpNextPDDAction.getSource());
			double timeToNext = calculateTime(trackToNext);
			newDriveAction = new CalendarActionWithGraph(-1, -1, "DRIVE",
					currentLocation, tmpNextPDDAction.getSource(), trackToNext,
					timestamp, timestamp + timeToNext,
					tmpPrevPDDAction.getCurrentLoad());

			schedule.putActionAfter(newDriveAction, newSwitchAction);
		}

		// add WAIT action
		newWaitAction = new CalendarActionWithGraph(-1, -1, "WAIT",
				newDriveAction.getDestination(),
				newDriveAction.getDestination(), null,
				newDriveAction.getEndTime(), tmpNextPDDAction.getStartTime(),
				newDriveAction.getCurrentLoad());

		schedule.putActionAfter(newWaitAction, newDriveAction);

		return true;
	}

	@SuppressWarnings("rawtypes")
	private Commission[] getComsTable() {

		Commission[] commissionsTable;
		Enumeration enumeration;
		int count;

		commissionsTable = new Commission[commissions.size()];
		enumeration = commissions.elements();
		count = 0;
		while (enumeration.hasMoreElements()) {

			commissionsTable[count++] = (Commission) enumeration.nextElement();
		}

		return commissionsTable;
	}

	public double calculateWholeLoad(int startTime) {

		CalendarActionWithGraph tmpAction;
		double wholeLoad;

		wholeLoad = 0;

		for (int i = schedule.size() - 1; i >= 0; i--) {

			tmpAction = (CalendarActionWithGraph) schedule.get(i);

			if (tmpAction.getEndTime() >= startTime
					&& tmpAction.getType() == "PICKUP") {
				wholeLoad += tmpAction.getCurrentLoad();
			} else if (tmpAction.getEndTime() <= startTime
					&& tmpAction.getType() == "DELIVERY") {
				wholeLoad -= tmpAction.getCurrentLoad();
			}
		}

		return wholeLoad;
	}
}
