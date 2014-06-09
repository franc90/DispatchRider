package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetComplexSTScheduleBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

	private ExecutionUnitAgent agent;

    public GetComplexSTScheduleBehaviour(ExecutionUnitAgent agent) {
    	this.agent=agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.HOLONS_CALENDAR);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	
			agent.sendSchedule(msg.getSender());
        	
        } else {
            block();
        }
    }
}
