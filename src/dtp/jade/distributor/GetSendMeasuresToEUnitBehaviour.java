package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetSendMeasuresToEUnitBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -3161653984029698226L;

	private final DistributorAgent distributorAgent;

	public GetSendMeasuresToEUnitBehaviour(DistributorAgent agent) {

		this.distributorAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.MEASURES_TO_EUNIT_DATA);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			distributorAgent.measuresSendToEUnitResponse();
		} else {

			block();
		}
	}
}
