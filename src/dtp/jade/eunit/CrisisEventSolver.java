package dtp.jade.eunit;

public class CrisisEventSolver {

	// private final ExecutionUnitAgent eUnit;

	public CrisisEventSolver(ExecutionUnitAgent executionUnitAgent) {

		// eUnit = executionUnitAgent;
	}
	// TODO implement
	/*
	 * public void tryToSolve(CommissionWithdrawalEvent crisisEvent) {
	 * 
	 * if (eUnit.getProblemType() == ProblemType.WITHOUT_GRAPH) {
	 * 
	 * logg("CM - cannon solve crisis event without graph"); return; }
	 * 
	 * 
	 * AgentCalendarWithGraph calendar = eUnit.getAgentCalendarWithGraph();
	 * 
	 * int timestamp = eUnit.getTimestamp();
	 * 
	 * if (calendar.containsCommission(crisisEvent.getCommissionID())) {
	 * 
	 * logg("CM - trying to solve crisis event \n" + "\t" + crisisEvent);
	 * sendGUIMessage("CM - trying to solve crisis event \n" + "\t" +
	 * crisisEvent);
	 * 
	 * } else {
	 * 
	 * logg("CM - no such commission in calendar \n" + "\t" + crisisEvent);
	 * return; }
	 * 
	 * // sprawdz, czy zlecenie nie jest w trakcie realizacji boolean
	 * alreadyPickedUp =
	 * calendar.isCommissionAlreadyPickedUp(crisisEvent.getCommissionID(),
	 * timestamp);
	 * 
	 * if (alreadyPickedUp) {
	 * 
	 * CrisisEventFinalFeedback feedback = new
	 * CrisisEventFinalFeedback(crisisEvent.getEventID(),
	 * "CM - Already picked up"); sendCrisisEventFinalFeedback(feedback);
	 * logg("CM - already picked up"); return; }
	 * 
	 * // sprawdz czy jednostka transportowa jest w punkcie grafu, jezeli jest
	 * // na linku - dodaj punkt grafu GraphPoint currentGraphPoint =
	 * calendar.getSetCurrentGraphPoint(timestamp);
	 * 
	 * calendar.removeCommission(crisisEvent.getCommissionID(),
	 * currentGraphPoint, timestamp);
	 * 
	 * eUnit.sendGraphUpdate(eUnit.getGraph());
	 * 
	 * CrisisEventFinalFeedback feedback = new
	 * CrisisEventFinalFeedback(crisisEvent.getEventID(),
	 * "CM - Commission withdrawn"); sendCrisisEventFinalFeedback(feedback);
	 * logg("CM - commission withdrawn");
	 * 
	 * }
	 * 
	 * public void tryToSolve(CommissionDelayEvent crisisEvent) {
	 * 
	 * if (eUnit.getProblemType() == ProblemType.WITHOUT_GRAPH) {
	 * 
	 * logg("cannon solve crisis event without graph"); return; }
	 * 
	 * AgentCalendarWithGraph calendar = eUnit.getAgentCalendarWithGraph();
	 * 
	 * int timestamp = eUnit.getTimestamp();
	 * 
	 * if (calendar.containsCommission(crisisEvent.getCommissionID())) {
	 * 
	 * logg("CM - trying to solve crisis event \n" + "\t" + crisisEvent);
	 * sendGUIMessage("CM - trying to solve crisis event \n" + "\t" +
	 * crisisEvent);
	 * 
	 * } else {
	 * 
	 * logg("CM - no such commission in calendar \n" + "\t" + crisisEvent);
	 * return; }
	 * 
	 * AgentCalendarWithGraph calendarBackup;
	 * 
	 * Commission comToDealy; Commission nextCom;
	 * 
	 * boolean comToDelayAlreadyPickedUp; boolean comToDelayRemoved; boolean
	 * nextComRemoved; boolean comToDelayAdded; boolean nextComAdded;
	 * 
	 * double startTimeAfterDelay;
	 * 
	 * comToDealy = calendar.getCommissionByID(crisisEvent.getCommissionID());
	 * 
	 * calendarBackup = calendar.clone();
	 * 
	 * // sprawdz, czy zlecenie nie jest w trakcie realizacji
	 * comToDelayAlreadyPickedUp =
	 * calendar.isCommissionAlreadyPickedUp(crisisEvent.getCommissionID(),
	 * timestamp);
	 * 
	 * if (comToDelayAlreadyPickedUp) {
	 * 
	 * // Commission to delay already picked up CrisisEventFinalFeedback
	 * feedback = new CrisisEventFinalFeedback(crisisEvent.getEventID(),
	 * "CM - Already picked up"); sendCrisisEventFinalFeedback(feedback);
	 * logg("CM - already picked up"); return; }
	 * 
	 * // sprawdz czy jednostka transportowa jest w punkcie grafu, jezeli jest
	 * // na linku - dodaj punkt grafu GraphPoint currentGraphPoint =
	 * calendar.getSetCurrentGraphPoint(timestamp);
	 * 
	 * startTimeAfterDelay =
	 * calendar.getPickupActionStartTimeForCommission(comToDealy.getID());
	 * 
	 * // wyjmij kolejne zlecenie z kalendarza nextCom =
	 * calendar.getCommissionAfter(comToDealy);
	 * 
	 * // jest next com if (nextCom != null) {
	 * 
	 * nextComRemoved = calendar.removeCommission(nextCom.getID(),
	 * currentGraphPoint, timestamp);
	 * 
	 * } // nie ma next com else {
	 * 
	 * nextComRemoved = false; }
	 * 
	 * // usun zlecenie-do-usuniecia z kalendarza comToDelayRemoved =
	 * calendar.removeCommission(comToDealy.getID(), currentGraphPoint,
	 * timestamp);
	 * 
	 * // nie wyjeto zlecenia do usuniecia z kalendarz if (comToDelayRemoved ==
	 * false) {
	 * 
	 * if (nextCom != null && nextComRemoved) {
	 * 
	 * calendar = calendarBackup; }
	 * 
	 * // feedback CrisisEventFinalFeedback feedback = new
	 * CrisisEventFinalFeedback(crisisEvent.getEventID(),
	 * "CM - cannot delay commission (cannot remove commission)");
	 * sendCrisisEventFinalFeedback(feedback);
	 * logg("CM - cannot delay commission (cannot remove commission)"); return;
	 * }
	 * 
	 * // wprowadz opoznienie comToDealy.setPickupTime1(startTimeAfterDelay +
	 * crisisEvent.getDelay());
	 * 
	 * // wstaw opoznione zlecenie comToDelayAdded =
	 * (calendar.addCommission(comToDealy, timestamp)<0);
	 * 
	 * // wstaw kolejne zlecenie nextComAdded = false; if (nextCom != null &&
	 * nextComRemoved) {
	 * 
	 * nextComAdded = (calendar.addCommission(nextCom, timestamp)<0); }
	 * 
	 * eUnit.sendGraphUpdate(eUnit.getGraph());
	 * 
	 * // feedback CrisisEventFinalFeedback feedback = new
	 * CrisisEventFinalFeedback();
	 * feedback.setEventID(crisisEvent.getEventID()); if (comToDelayAdded) {
	 * 
	 * if (nextComRemoved) {
	 * 
	 * if (nextComAdded) {
	 * 
	 * feedback.setMessage("CM - commission delayed");
	 * logg("CM - commission delayed");
	 * 
	 * } else {
	 * 
	 * feedback.setMessage("CM - commission delayed (cannot add next commission)"
	 * ); feedback.addCom4Auction(nextCom);
	 * logg("CM - commission delayed (cannot add next commission)"); }
	 * 
	 * } else {
	 * 
	 * feedback.setMessage("CM - commission delayed");
	 * logg("CM - commission delayed"); }
	 * 
	 * } else {
	 * 
	 * if (nextComRemoved) {
	 * 
	 * if (nextComAdded) {
	 * 
	 * feedback.setMessage("CM - cannot delay commission");
	 * feedback.addCom4Auction(comToDealy);
	 * logg("CM - cannot delay commission");
	 * 
	 * } else {
	 * 
	 * feedback.setMessage(
	 * "CM - cannot delay commission (cannot add next commission)");
	 * feedback.addCom4Auction(comToDealy); feedback.addCom4Auction(nextCom);
	 * logg("CM - cannot delay commission (cannot add next commission)"); }
	 * 
	 * } else {
	 * 
	 * feedback.setMessage("CM - cannot delay commission");
	 * feedback.addCom4Auction(comToDealy);
	 * logg("CM - cannot delay commission"); } }
	 * 
	 * sendCrisisEventFinalFeedback(feedback); }
	 * 
	 * public void tryToSolve(EUnitFailureEvent crisisEvent) {
	 * 
	 * if (eUnit.getProblemType() == ProblemType.WITHOUT_GRAPH) {
	 * 
	 * logg("cannon solve crisis event without graph"); return; }
	 * 
	 * AgentCalendarWithGraph calendar = eUnit.getAgentCalendarWithGraph();
	 * 
	 * int timestamp = eUnit.getTimestamp();
	 * 
	 * ArrayList<Commission> comsAfter = new ArrayList<Commission>();
	 * 
	 * if (AgentIDResolver.getEUnitIDFromName(eUnit.getLocalName()) !=
	 * crisisEvent.getEUnitID()) {
	 * 
	 * // to nie ja mam awarie ;) return; }
	 * 
	 * logg("CM - trying to solve crisis event \n" + "\t" + crisisEvent);
	 * sendGUIMessage("CM - trying to solve crisis event \n" + "\t" +
	 * crisisEvent);
	 * 
	 * // sprawdz czy jednostka transportowa jest w punkcie grafu, jezeli jest
	 * // na linku - dodaj punkt grafu GraphPoint currentGraphPoint =
	 * calendar.getSetCurrentGraphPoint(timestamp);
	 * 
	 * // kolejne zlecenia w kalendarza comsAfter =
	 * calendar.getCommissionsAfter(timestamp); Iterator<Commission> iter =
	 * comsAfter.iterator(); // wyjmij kolejne zlecenia z kalendarza while
	 * (iter.hasNext()) {
	 * 
	 * calendar.removeCommission(iter.next().getID(), currentGraphPoint,
	 * timestamp); }
	 * 
	 * // wstaw akcje BROKEN calendar.addActionBroken(crisisEvent, timestamp);
	 * 
	 * // feedback CrisisEventFinalFeedback feedback = new
	 * CrisisEventFinalFeedback();
	 * feedback.setEventID(crisisEvent.getEventID());
	 * feedback.setMessage("CM - BROKEN");
	 * 
	 * // wstaw akcje usuniete z kalendarza // jezeli nie da sie ich wstawic,
	 * wyslij na aukcje iter = comsAfter.iterator(); while (iter.hasNext()) {
	 * 
	 * Commission tmpCom = iter.next(); boolean comAdded; comAdded =
	 * (calendar.addCommission(tmpCom, timestamp)<0); if (!comAdded) {
	 * feedback.addCom4Auction(tmpCom); } }
	 * 
	 * sendCrisisEventFinalFeedback(feedback); }
	 * 
	 * public void tryToSolve(TrafficJamEvent crisisEvent) {
	 * 
	 * if (eUnit.getProblemType() == ProblemType.WITHOUT_GRAPH) {
	 * 
	 * logg("cannon solve crisis event without graph"); return; }
	 * 
	 * logg("CM - cannot solve this... yet\n" + "\t" + crisisEvent);
	 * sendGUIMessage("CM - cannot solve this... yet\n" + "\t" + crisisEvent);
	 * 
	 * return; }
	 * 
	 * public void tryToSolve(RoadTrafficExclusionEvent crisisEvent) {
	 * 
	 * if (eUnit.getProblemType() == ProblemType.WITHOUT_GRAPH) {
	 * 
	 * logg("cannon solve crisis event without graph"); return; }
	 * 
	 * @SuppressWarnings("unused") AgentCalendarWithGraph calendar =
	 * eUnit.getAgentCalendarWithGraph();
	 * 
	 * @SuppressWarnings("unused") int timestamp = eUnit.getTimestamp();
	 * 
	 * logg("CM - cannot solve this... yet\n" + "\t" + crisisEvent);
	 * sendGUIMessage("CM - cannot solve this... yet\n" + "\t" + crisisEvent);
	 * 
	 * return; }
	 * 
	 * private void sendCrisisEventFinalFeedback(CrisisEventFinalFeedback
	 * crisisEventFinalFeedback) {
	 * 
	 * AID[] aids = null; ACLMessage cfp = null;
	 * 
	 * aids = CommunicationHelper.findAgentByServiceName(eUnit,
	 * "CrisisManagementService");
	 * 
	 * if (aids.length == 1) {
	 * 
	 * cfp = new ACLMessage(CommunicationHelper.CRISIS_EVENT_FINAL_FEEDBACK);
	 * cfp.addReceiver(aids[0]); try {
	 * cfp.setContentObject(crisisEventFinalFeedback); } catch (IOException e) {
	 * logg("ERROR - IOException " + e.getMessage()); } eUnit.send(cfp);
	 * 
	 * } else { logg(
	 * "ERROR - none or more than one agent with CrisisManagementService in the system"
	 * ); } }
	 * 
	 * private void logg(String message) {
	 * 
	 * eUnit.logg(message); }
	 * 
	 * private void sendGUIMessage(String message) {
	 * 
	 * eUnit.sendGUIMessage(message); }
	 */
}
