package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.LinkedList;

import dtp.graph.GraphLink;
import dtp.jade.CommunicationHelper;

/**
 * @author KONY
 */
public class GetGraphLinkChangedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 8689655268845736964L;

	private final ExecutionUnitAgent executionUnitAgent;

	public GetGraphLinkChangedBehaviour(ExecutionUnitAgent agent) {
		this.executionUnitAgent = agent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.GRAPH_LINK_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			try {
				executionUnitAgent.changeGraphLinks(
						(LinkedList<GraphLink>) msg.getContentObject(),
						msg.getSender());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}

		} else {

			block();
		}
	}
}
