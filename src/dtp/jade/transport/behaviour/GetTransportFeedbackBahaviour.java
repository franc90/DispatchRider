package dtp.jade.transport.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;


import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportFeedback;

/**
 * Behaviour for receiving feedback from execution unit.
 * 
 * @author Michal Golacki
 */
public class GetTransportFeedbackBahaviour extends CyclicBehaviour {

    /** Serial version */
    private static final long serialVersionUID = -62181721920552323L;

    /** Logger */
    private static Logger logger = Logger.getLogger(GetTransportCommisionBehaviour.class);

    /** Agent */
    private TransportAgent agent;

    public GetTransportFeedbackBahaviour(TransportAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_FEEDBACK);
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            try {
                TransportFeedback content = (TransportFeedback) message.getContentObject();

                if (content != null) {
                    agent.setBooked(true);
                } else {
                    agent.setBooked(false);
                }
            } catch (UnreadableException e) {
                logger.error(agent.getLocalName() + " - UnreadableException - " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
