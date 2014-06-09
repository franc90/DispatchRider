package dtp.jade.algorithm.agent.behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import dtp.jade.CommunicationHelper;
import dtp.jade.algorithm.agent.AlgorithmAgent;
import dtp.jade.algorithm.agent.AlgorithmAgentInitData;

public class GetInitialDataBehaviour extends CyclicBehaviour {

	/**
     * 
     */
	private static final long serialVersionUID = -8392984544272799823L;
	private final AlgorithmAgent algorithmAgent;

	public GetInitialDataBehaviour(AlgorithmAgent algorithmAgent) {
		this.algorithmAgent = algorithmAgent;
	}

	@Override
	public void action() {
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.ALGORITHM_AGENT_INITIAL_DATA);
		ACLMessage msg = algorithmAgent.receive(template);
		if (msg != null) {
			try {
				AlgorithmAgentInitData initialData = (AlgorithmAgentInitData) msg
						.getContentObject();
				algorithmAgent.init(initialData.getInitParams());

				ACLMessage resp = new ACLMessage(
						CommunicationHelper.ALGORITHM_AGENT_CREATION);
				AID testAgent = CommunicationHelper.findAgentByServiceName(
						algorithmAgent, "GUIService")[0];
				resp.addReceiver(testAgent);
				try {
					resp.setContentObject("");
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}

				algorithmAgent.send(resp);

			} catch (UnreadableException e) {
			}
		} else {
			block();
		}

	}

}
