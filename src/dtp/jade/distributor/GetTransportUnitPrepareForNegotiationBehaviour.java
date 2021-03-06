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
public class GetTransportUnitPrepareForNegotiationBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -3161653984029698226L;

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent from Action method
     * 
     * @param agent
     *        - Distributor Agent
     */
    public GetTransportUnitPrepareForNegotiationBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves EUnit offers and processes it
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_AGENT_PREPARED_TO_NEGOTIATION);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	distributorAgent.transportUnitPreparedForNegotiation();
        	
        } else {

            block();
        }
    }
}
