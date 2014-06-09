package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

public class GetNooneRequestBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -7734621332149610018L;

    private static Logger logger = Logger.getLogger(GetNooneRequestBehaviour.class);

    private final DistributorAgent distributorAgent;

    public GetNooneRequestBehaviour(DistributorAgent agent) {

        this.distributorAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.DISTRIBUTOR_SHOW_NOONE_LIST);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            logger.info(myAgent.getLocalName() + " - got nooneList request behaviour");
            distributorAgent.sendNooneList();

        } else {

            block();
        }
    }
}
