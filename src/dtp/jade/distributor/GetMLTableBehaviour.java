package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

import machineLearning.MLAlgorithm;
import dtp.jade.CommunicationHelper;

public class GetMLTableBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -1055128511429775074L;

	private final DistributorAgent agent;

	public GetMLTableBehaviour(DistributorAgent agent) {
		this.agent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.MLTable);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			MLAlgorithm table = agent.getMLAlgorithm();
			ACLMessage resp = new ACLMessage(CommunicationHelper.MLTable);
			resp.addReceiver(msg.getSender());
			try {
				resp.setContentObject(table);
			} catch (IOException e) {
				e.printStackTrace();
			}
			agent.send(resp);

		} else {
			block();
		}
	}
}
