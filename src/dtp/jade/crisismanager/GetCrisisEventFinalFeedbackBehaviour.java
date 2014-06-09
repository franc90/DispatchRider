package dtp.jade.crisismanager;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

public class GetCrisisEventFinalFeedbackBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 4904514794425938421L;

    private static Logger logger = Logger.getLogger(GetCrisisEventFinalFeedbackBehaviour.class);

    private CrisisManagerAgent crisisManagerAgent;

    public GetCrisisEventFinalFeedbackBehaviour(CrisisManagerAgent agent) {

        this.crisisManagerAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.CRISIS_EVENT_FINAL_FEEDBACK);
        ACLMessage msg = myAgent.receive(template);

        CrisisEventFinalFeedback crisisEventFinalFeedback;

        if (msg != null) {

            try {

                crisisEventFinalFeedback = (CrisisEventFinalFeedback) msg.getContentObject();

                crisisManagerAgent.addCrisisEventFinalFeedback(crisisEventFinalFeedback);

            } catch (UnreadableException e) {

                logger.error(crisisManagerAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }

        } else {

            block();
        }
    }
}
