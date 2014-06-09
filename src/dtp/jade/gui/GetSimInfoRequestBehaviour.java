package dtp.jade.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

/**
 * @author kony.pl
 */
public class GetSimInfoRequestBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 1551427613144164700L;

    private GUIAgent guiAgent;

    public GetSimInfoRequestBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_INFO);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            guiAgent.sendSimInfo(msg.getSender());

        } else {

            block();
        }
    }
}
