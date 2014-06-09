package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetConfigurationChangeBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8801119947778326131L;

	private final DistributorAgent distributorAgent;

	public GetConfigurationChangeBehaviour(DistributorAgent agent) {

		this.distributorAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.CONFIGURATION_CHANGE);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			distributorAgent.configurationChanged();
		} else {

			block();
		}
	}
}
