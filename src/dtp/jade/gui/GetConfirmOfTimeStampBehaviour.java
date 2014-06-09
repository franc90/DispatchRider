package dtp.jade.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetConfirmOfTimeStampBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

    private GUIAgent guiAgent;

    public GetConfirmOfTimeStampBehaviour(GUIAgent agent) {

        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TIME_STAMP_CONFIRM);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	guiAgent.stampConfirmed();
        } else {
            block();
        }
    }
}
