package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetEUnitCreatedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

	private DistributorAgent agent;

    public GetEUnitCreatedBehaviour(DistributorAgent agent) {
    	this.agent=agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EXECUTION_UNIT_CREATION);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	agent.eUnitCreated();
        } else {
            block();
        }
    }
}
