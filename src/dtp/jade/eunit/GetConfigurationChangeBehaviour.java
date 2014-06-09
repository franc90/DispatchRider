package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import measure.configuration.HolonConfiguration;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

public class GetConfigurationChangeBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8801119947778326131L;

	private static final Logger logger = Logger
			.getLogger(dtp.jade.distributor.GetConfigurationChangeBehaviour.class);
	private final ExecutionUnitAgent executionUnitAgent;

	public GetConfigurationChangeBehaviour(ExecutionUnitAgent agent) {

		this.executionUnitAgent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.CONFIGURATION_CHANGE);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			try {
				executionUnitAgent.configurationChanged(
						(HolonConfiguration) msg.getContentObject(),
						msg.getSender());
			} catch (UnreadableException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		} else {

			block();
		}
	}
}
