package dtp.jade.transport.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;


import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;

/**
 * Behaviour used by transport elements to receive commission information.
 * 
 * @author Michal Golacki
 */
public class GetCommisionBehaviour extends CyclicBehaviour {

    /** Serial version */
    private static final long serialVersionUID = -3640633318262710395L;

    /** Agent */
    private TransportAgent agent;

    /** Logger */
    private static Logger logger = Logger.getLogger(GetCommisionBehaviour.class);

    public GetCommisionBehaviour(TransportAgent transportAgent) {
        agent = transportAgent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.COMMISSION);
        ACLMessage message = myAgent.receive(template);

        if (message != null) {
            try {
                //logger.info("Got commision from e unit");
                Commission[] commissions = (Commission[]) message.getContentObject();
                if(commissions.length==1)
                	agent.setCommission(commissions[0]);
                else agent.setCommissions(commissions);
            } catch (UnreadableException e) {
                logger.error(agent.getLocalName() + " - UndeadableException - " + e.getMessage());
            }
        } else {
            block();
        }

    }
}
