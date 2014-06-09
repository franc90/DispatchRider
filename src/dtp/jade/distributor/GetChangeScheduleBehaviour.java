package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetChangeScheduleBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 4948182847977719740L;

	private final DistributorAgent agent;

	public GetChangeScheduleBehaviour(DistributorAgent agent) {

		this.agent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.CHANGE_SCHEDULE);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			agent.changeSchedule();

		} else {
			block();
		}
	}
}
