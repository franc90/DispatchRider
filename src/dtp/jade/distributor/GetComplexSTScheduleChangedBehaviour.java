package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetComplexSTScheduleChangedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

	private DistributorAgent agent;

    public GetComplexSTScheduleChangedBehaviour(DistributorAgent agent) {
    	this.agent=agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.HOLONS_NEW_CALENDAR);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        
				agent.scheduleChanged();
	
        	
        } else {
            block();
        }
    }
}
