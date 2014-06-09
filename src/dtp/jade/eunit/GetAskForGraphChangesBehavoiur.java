package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetAskForGraphChangesBehavoiur extends CyclicBehaviour {

	private static final long serialVersionUID = 7206193333979034390L;

	private final ExecutionUnitAgent eunitAgent;

	public GetAskForGraphChangesBehavoiur(ExecutionUnitAgent agent) {
		this.eunitAgent = agent;
	}

	@Override
	public void action() {

		/*-------- RECIEVING GRAPH SECTION -------*/
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.ASK_IF_GRAPH_LINK_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			eunitAgent.askForGraphChanges(msg.getSender());

		} else {

			block();
		}
	}
}
