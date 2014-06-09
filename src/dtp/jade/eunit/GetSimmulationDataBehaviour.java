package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetSimmulationDataBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 4948182847977719740L;

	private final ExecutionUnitAgent eunitAgent;

	public GetSimmulationDataBehaviour(ExecutionUnitAgent agent) {

		this.eunitAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.SIMMULATION_DATA);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			eunitAgent.sendSimmulationData(msg.getSender());
		} else {
			block();
		}
	}
}
