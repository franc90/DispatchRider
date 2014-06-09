package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

public class GetResetRequestBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -551491709082846729L;

    private static Logger logger = Logger.getLogger(GetResetRequestBehaviour.class);

    private final DistributorAgent distributorAgent;

    public GetResetRequestBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.RESET);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            logger.info(myAgent.getLocalName() + " - RESET requested");

            distributorAgent.resetAgent();

        } else {
            block();
        }
    }
}
