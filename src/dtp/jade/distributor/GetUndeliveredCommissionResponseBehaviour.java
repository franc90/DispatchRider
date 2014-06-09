package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetUndeliveredCommissionResponseBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

	private final DistributorAgent agent;

	public GetUndeliveredCommissionResponseBehaviour(DistributorAgent agent) {
		this.agent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.UNDELIVERIED_COMMISSION);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			agent.undeliveredCommissionResponse();

		} else {
			block();
		}
	}
}
