package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;
import dtp.jade.gui.CommissionsHolder;

/**
 * Represents a behaviour of acceptance of new commission(s) from GUI Agent
 * 
 * @author KONY
 */
public class GetCommissionBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -8801119947778326131L;

    private static Logger logger = Logger.getLogger(GetCommissionBehaviour.class);

    private final DistributorAgent distributorAgent;

    /**
     * Constructs a new behaviour and allows to access the Distributor Agent from Action method
     * 
     * @param agent
     *        - Distributor Agent
     */
    public GetCommissionBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    /**
     * Recieves commissions and processes it
     */
    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.COMMISSION);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

        	Commission[] commissions=null;
            try {

                CommissionsHolder holder = (CommissionsHolder) msg.getContentObject();
                commissions=holder.getCommissions();
                System.out.println("Commissions received ********************************");
                distributorAgent.setCommissions(commissions,holder);
                logger.info(this.distributorAgent.getLocalName() + " - got " + commissions.length
                        + " new commission(s)");

            } catch (UnreadableException e1) {

                logger.error(this.distributorAgent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }

            //for (int i = 0; i < commissions.length; i++)
            //    distributorAgent.addCommission(commissions[i]);
            
            
        } else {

            block();
        }
    }
}
