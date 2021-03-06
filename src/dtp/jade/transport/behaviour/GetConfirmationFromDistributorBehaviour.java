package dtp.jade.transport.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;

/**
 * Behaviour used by transport elements to receive commission information.
 * 
 * @author Michal Golacki
 */
public class GetConfirmationFromDistributorBehaviour extends CyclicBehaviour {

    /** Serial version */
    private static final long serialVersionUID = -3640633318262710395L;

    /** Agent */
    private TransportAgent agent;

    public GetConfirmationFromDistributorBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.CONFIRMATIO_FROM_DISTRIBUTOR);
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
                agent.confirmationFromDistributor();
        } else {
            block();
        }

    }
}
