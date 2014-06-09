package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

/**
 * Represents a behaviour of acceptance of new offer from EUnit Agent
 * 
 * @author KONY
 */
public class GetHolonFeedbackOfferBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -3161653984029698226L;

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent from Action method
     * 
     * @param agent
     *        - Distributor Agent
     */
    public GetHolonFeedbackOfferBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves EUnit offers and processes it
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.HOLON_FEEDBACK);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            distributorAgent.feedbackFromEUnit();

        } else {

            block();
        }
    }
}
