package dtp.jade.test;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;

public class GetTransportAgentCreatedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

	private static Logger logger = Logger.getLogger(GetTransportAgentCreatedBehaviour.class);

    private GUIAgent guiAgent;

    public GetTransportAgentCreatedBehaviour(GUIAgent agent) {

        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_AGENT_CREATED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	guiAgent.transportAgentCreated();
        	logger.info("new TransportAgent was created ");
        } else {
            block();
        }
    }
}
