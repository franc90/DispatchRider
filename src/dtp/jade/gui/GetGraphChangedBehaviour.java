package dtp.jade.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.jade.CommunicationHelper;

/**
 * @author KONY
 */
public class GetGraphChangedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -5847182259803791026L;

	private final GUIAgent guiAgent;

	public GetGraphChangedBehaviour(GUIAgent agent) {

		this.guiAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.GRAPH_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			try {
				guiAgent.graphChanged((Boolean) msg.getContentObject());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		} else {

			block();
		}
	}
}
