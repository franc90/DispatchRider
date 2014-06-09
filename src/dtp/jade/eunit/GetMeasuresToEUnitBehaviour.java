package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.HashMap;

import measure.Measure;
import dtp.jade.CommunicationHelper;

public class GetMeasuresToEUnitBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 8689655268845736964L;

	private final ExecutionUnitAgent executionUnitAgent;

	public GetMeasuresToEUnitBehaviour(ExecutionUnitAgent agent) {
		this.executionUnitAgent = agent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.MEASURES_TO_EUNIT_DATA);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			try {
				executionUnitAgent
						.addMeasuresData((HashMap<String, Measure>) msg
								.getContentObject());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}

			ACLMessage response = new ACLMessage(
					CommunicationHelper.MEASURES_TO_EUNIT_DATA);
			response.addReceiver(msg.getSender());
			try {
				response.setContentObject("");
			} catch (IOException e) {
				e.printStackTrace();
			}
			executionUnitAgent.send(response);
		} else {
			block();
		}
	}
}
