package dtp.jade.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.graph.GraphLink;
import dtp.jade.CommunicationHelper;

public class GetAskForGraphChangesBehavoiur extends CyclicBehaviour {

	private static final long serialVersionUID = 3501116012294115968L;

	/** Logger. */
	private static Logger logger = Logger
			.getLogger(GetAskForGraphChangesBehavoiur.class);

	private final GUIAgent guiAgent;

	public GetAskForGraphChangesBehavoiur(GUIAgent agent) {
		this.guiAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.ASK_IF_GRAPH_LINK_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			try {
				guiAgent.addChangedLink((GraphLink) msg.getContentObject());

			} catch (UnreadableException e) {
				logger.error(this.guiAgent.getLocalName()
						+ " - UnreadableException " + e.getMessage());
			}
		} else {
			block();
		}
	}
}
