package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.jade.CommunicationHelper;
import dtp.jade.agentcalendar.CalendarStats;

public class GetWorstCommissionRequestBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 4948182847977719740L;

	private final DistributorAgent agent;

	public GetWorstCommissionRequestBehaviour(DistributorAgent agent) {

		this.agent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.WORST_COMMISSION_COST);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			try {
				agent.addWorstCommissionCost((CalendarStats) msg
						.getContentObject());
			} catch (UnreadableException e) {
				e.printStackTrace();
				System.exit(0);
			}
		} else {
			block();
		}
	}
}
