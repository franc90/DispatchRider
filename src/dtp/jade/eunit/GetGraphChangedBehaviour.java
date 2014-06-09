package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.graph.Graph;
import dtp.jade.CommunicationHelper;

/**
 * @author KONY
 */
public class GetGraphChangedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 8689655268845736964L;

	private final ExecutionUnitAgent executionUnitAgent;

	public GetGraphChangedBehaviour(ExecutionUnitAgent agent) {
		this.executionUnitAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.GRAPH_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			try {
				Object[] data = (Object[]) msg.getContentObject();
				executionUnitAgent.graphChanged((Graph) data[0],
						(Boolean) data[1], msg.getSender());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}

		} else {

			block();
		}
	}
}
