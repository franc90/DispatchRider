package algorithm;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;
import dtp.jade.algorithm.agent.AlgorithmAgent;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

/**
 * Interface for algorithms, which are responsible for deployment of commissions
 * in local holon (eunit) schedule. It is important that any new algorithm
 * should implement it, because this interface is used in system. Apart from
 * that after implementing new algorithm you should add it in configuration (xsm
 * schedma and configuration parser)
 * 
 */
public abstract class Algorithm implements Serializable {

	private static final long serialVersionUID = -4957812658675017145L;

	public Object askAgent(Class<? extends AlgorithmAgent> agentClass,
			String operation, Map<String, ? extends Serializable> parameters,
			AlgorithmAgentParent agent) {
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

	/**
	 * Main method which is responsible for create new schedule and add
	 * commissions. If commission can't be add this method should return null.
	 * 
	 * @param commissionToAdd
	 *            new commissions to add
	 * @param currentLocation
	 *            current Holon location. It is used in dynamic problems
	 * @param currentSchedule
	 *            current Holon schedule. You have to add new commission to this
	 *            schedule
	 * @param timestamp
	 *            timestamp
	 * @return new schedule, or null if new commission cannot be added to
	 *         schedule
	 */
	public abstract Schedule makeSchedule(AlgorithmAgentParent agent,
			Commission commissionToAdd, Point2D.Double currentLocation,
			Schedule currentSchedule, int timestamp);

	public abstract void setMaxLoad(double maxLoad);

	public abstract void init(double maxLoad, SimInfo simInfo);

	public abstract Point2D.Double getDepot();

	public abstract SimInfo getSimInfo();

	public abstract List<Class<? extends AlgorithmAgent>> getHelperAgentsClasses();
}