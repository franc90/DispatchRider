package dtp.jade.algorithm.agent.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;
import dtp.jade.algorithm.agent.AlgorithmAgent;

public class GetEndOfSimBehaviour extends CyclicBehaviour {

	/**
     * 
     */
	private static final long serialVersionUID = -8392984544272799823L;
	private final AlgorithmAgent algorithmAgent;

	public GetEndOfSimBehaviour(AlgorithmAgent algorithmAgent) {
		this.algorithmAgent = algorithmAgent;
	}

	@Override
	public void action() {
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.SIM_END);
		ACLMessage msg = algorithmAgent.receive(template);
		if (msg != null) {

			algorithmAgent.doDelete();

		} else {
			block();
		}

	}

}
