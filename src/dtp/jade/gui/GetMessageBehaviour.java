package dtp.jade.gui;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GetMessageBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 5691205170181475287L;

    private static Logger logger = Logger.getLogger(GetMessageBehaviour.class);

    private GUIAgent guiAgent;

    public GetMessageBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        /*-------- RECIEVING REQUESTS FOR DISPLAYING MESSAGE IN GUI -------*/
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.GUI_MESSAGE);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            /*-------- DISPLAYING MESSAGE -------*/
            try {

                String message = (String) msg.getContentObject();

                // TODO temporary
                if (message.equals("DistributorAgent - NEXT_SIMSTEP")) {
                	guiAgent.nextAutoSimStep();
                    // wykorzystywane do sim GOD
                    // startuje timer, zeby ten zrobil nextSimstep i statystyki
                    // zaraz potem timer trzeba zatrzymac
                }

                guiAgent.displayMessage(message);

            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {

            block();
        }
    }
}
