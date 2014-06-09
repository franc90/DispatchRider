package dtp.jade.transport.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;


import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportCommission;

/**
 * Behaviour used by transport elements to receive commission information.
 * 
 * @author Michal Golacki
 */
public class GetTransportReorganizeBehaviour extends CyclicBehaviour {

    /** Serial version */
    private static final long serialVersionUID = -3640633318262710395L;

    /** Agent */
    private TransportAgent agent;

    /** Logger */
    private static Logger logger = Logger.getLogger(GetTransportReorganizeBehaviour.class);

    public GetTransportReorganizeBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_REORGANIZE);
        ACLMessage message = myAgent.receive(template);

        TransportCommission transportCommision = null;

        if (message != null) {
            try {
                //logger.info("Got reorganize from e unit");
                transportCommision = (TransportCommission) message.getContentObject();
                agent.checkReorganize(transportCommision);
            } catch (UnreadableException e) {
                logger.error(agent.getLocalName() + " - UndeadableException - " + e.getMessage());
            }
        } else {
            block();
        }

    }
}
