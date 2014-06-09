package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class SimEndBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -3161653984029698226L;

    private final DistributorAgent distributorAgent;

    public SimEndBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

   
    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_END);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            distributorAgent.simEnd();

        } else {

            block();
        }
    }
}
