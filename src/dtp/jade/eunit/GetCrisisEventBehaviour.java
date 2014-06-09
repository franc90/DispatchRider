package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;

public class GetCrisisEventBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8580325412836224981L;

	private static Logger logger = Logger
			.getLogger(GetCrisisEventBehaviour.class);

	private final ExecutionUnitAgent eUnitAgent;

	public GetCrisisEventBehaviour(ExecutionUnitAgent agent) {

		eUnitAgent = agent;
	}

	// TODO implement
	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.CRISIS_EVENT);
		ACLMessage msg = myAgent.receive(template);

		CrisisEvent crisisEvent = null;

		if (msg != null) {

			try {
				crisisEvent = (CrisisEvent) msg.getContentObject();

				logger.info(eUnitAgent.getLocalName()
						+ " - got crisis event:\n" + "\t"
						+ crisisEvent.toString());
				/*
				 * if (crisisEvent.getEventType() == "Commission Withdrawal") {
				 * 
				 * new CrisisEventSolver(eUnitAgent).tryToSolve((
				 * CommissionWithdrawalEvent) crisisEvent);
				 * 
				 * } else if (crisisEvent.getEventType() == "Commission Delay")
				 * {
				 * 
				 * new
				 * CrisisEventSolver(eUnitAgent).tryToSolve((CommissionDelayEvent
				 * ) crisisEvent);
				 * 
				 * } else if (crisisEvent.getEventType() == "Vehicle Failure") {
				 * 
				 * new
				 * CrisisEventSolver(eUnitAgent).tryToSolve((EUnitFailureEvent)
				 * crisisEvent);
				 * 
				 * } else if (crisisEvent.getEventType() == "Traffic Jam") {
				 * 
				 * new
				 * CrisisEventSolver(eUnitAgent).tryToSolve((TrafficJamEvent)
				 * crisisEvent);
				 * 
				 * } else if (crisisEvent.getEventType() ==
				 * "Road Traffic Exclusion") {
				 * 
				 * new CrisisEventSolver(eUnitAgent).tryToSolve((
				 * RoadTrafficExclusionEvent) crisisEvent);
				 * 
				 * } else {
				 * 
				 * logger.info(eUnitAgent.getLocalName() +
				 * " - unknown crisis event type = " +
				 * crisisEvent.getEventType()); }
				 */
			} catch (UnreadableException e) {

				logger.error(eUnitAgent.getLocalName()
						+ " - UnreadableException " + e.getMessage());
			}

		} else {

			block();
		}
	}
}
