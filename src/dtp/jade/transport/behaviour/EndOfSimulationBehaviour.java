package dtp.jade.transport.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;

public class EndOfSimulationBehaviour extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6621892750942005635L;
	private TransportAgent agent;
	
	public EndOfSimulationBehaviour(TransportAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_END);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            agent.doDelete();

        } else {
            block();
        }
		
	}

}
