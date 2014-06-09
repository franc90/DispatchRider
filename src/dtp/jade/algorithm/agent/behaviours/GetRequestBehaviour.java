package dtp.jade.algorithm.agent.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import dtp.jade.CommunicationHelper;
import dtp.jade.algorithm.agent.AlgorithmAgent;

public class GetRequestBehaviour extends CyclicBehaviour {

	/**
     * 
     */
	private static final long serialVersionUID = -8392984544272799823L;
	private final AlgorithmAgent algorithmAgent;

	public GetRequestBehaviour(AlgorithmAgent algorithmAgent) {
		this.algorithmAgent = algorithmAgent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.ALGORITHM_AGENT_REQUEST);
		ACLMessage msg = algorithmAgent.receive(template);
		if (msg != null) {
			try {

				Object[] request = (Object[]) msg.getContentObject();
				Serializable response = algorithmAgent.getResponse(
						(String) request[0], (Map<String, Object>) request[1]);

				ACLMessage resp = new ACLMessage(
						CommunicationHelper.ALGORITHM_AGENT_REQUEST);
				resp.addReceiver(msg.getSender());
				try {
					resp.setContentObject(response);
				} catch (IOException e) {
					e.printStackTrace();
				}

				algorithmAgent.send(resp);

			} catch (UnreadableException e) {
				e.printStackTrace();
				System.exit(0);
			}
		} else {
			block();
		}

	}

}
