package dtp.jade.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

/**
 * @author KONY
 */
public class GetGraphLinkChangedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -5847182259803791026L;

	private final GUIAgent guiAgent;

	public GetGraphLinkChangedBehaviour(GUIAgent agent) {

		this.guiAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.GRAPH_LINK_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			guiAgent.linkChanged();
		} else {

			block();
		}
	}
}
