package dtp.jade.test;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;
import dtp.jade.gui.GUIAgent;

public class SimInfoReceivedBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 8689655268845736964L;

    private final GUIAgent agent;

    public SimInfoReceivedBehaviour(GUIAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_INFO_RECEIVED);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	((TestAgent)agent).simInfoReceived();

        } else {
            block();
        }
    }

}
