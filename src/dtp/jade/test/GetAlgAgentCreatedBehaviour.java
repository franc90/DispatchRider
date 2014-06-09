package dtp.jade.test;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetAlgAgentCreatedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

	private final TestAgent agent;

	public GetAlgAgentCreatedBehaviour(TestAgent agent) {

		this.agent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.ALGORITHM_AGENT_CREATION);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			agent.algorithmAgentCreated();
		} else {
			block();
		}
	}
}
