package dtp.jade.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

public class GetNooneListBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 2899807166633545573L;

    private static Logger logger = Logger.getLogger(GetNooneListBehaviour.class);

    private final GUIAgent guiAgent;

    public GetNooneListBehaviour(GUIAgent agent) {

        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.NOONE_LIST);
        ACLMessage msg = myAgent.receive(template);

        int nooneListSize;

        if (msg != null) {

            try {
                nooneListSize = ((Integer) msg.getContentObject()).intValue();
                guiAgent.addNooneList(nooneListSize);

            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }

}
