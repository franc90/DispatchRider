package algorithm.comparator;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;
import dtp.jade.algorithm.agent.AlgorithmAgent;
import dtp.simmulation.SimInfo;

public abstract class CommissionsComparator implements Serializable {

	private static final long serialVersionUID = -7240536033029036208L;

	public Object askAgent(Class<? extends AlgorithmAgent> agentClass,
			String operation, Map<String, ? extends Serializable> parameters,
			Agent agent) {
		String name = agentClass.getName();
		String parts[] = name.split("\\.");
		name = parts[parts.length - 1];

		AID[] aids = CommunicationHelper.findAgentByServiceName(agent, name);
		if (aids.length != 1)
			throw new IllegalStateException("There can be exacly one: " + name);
		ACLMessage msg = new ACLMessage(
				CommunicationHelper.ALGORITHM_AGENT_REQUEST);
		msg.addReceiver(aids[0]);

		try {
			msg.setContentObject(new Object[] { operation, parameters });
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		agent.send(msg);

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.ALGORITHM_AGENT_REQUEST);
		ACLMessage msg2 = agent.blockingReceive(template);
		if (msg2 != null) {
			try {
				Object result = msg2.getContentObject();
				return result;
			} catch (UnreadableException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		throw new NullPointerException("Algorithm agent response is null");
	}

	public abstract List<Class<? extends AlgorithmAgent>> getHelperAgentsClasses();

	public abstract List<Commission> sort(List<Commission> commissions,
			Agent agent, SimInfo simInfo);
}
