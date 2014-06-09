package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.graph.Graph;
import dtp.jade.CommunicationHelper;

/**
 * Represents a behaviour of acceptance of new commission(s) from GUI Agent
 * 
 * @author KONY
 */
public class GetGraphChangedBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8801119947778326131L;

	private static Logger logger = Logger
			.getLogger(GetGraphChangedBehaviour.class);

	private final DistributorAgent distributorAgent;

	/**
	 * Constructs a new behaviour and allows to access the Distributor Agent
	 * from Action method
	 * 
	 * @param agent
	 *            - Distributor Agent
	 */
	public GetGraphChangedBehaviour(DistributorAgent agent) {

		this.distributorAgent = agent;
	}

	/**
	 * Recieves commissions and processes it
	 */
	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.GRAPH_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			try {
				distributorAgent.graphChanged((Graph) msg.getContentObject(),
						msg.getSender());

			} catch (UnreadableException e1) {
				e1.printStackTrace();
				logger.error(this.distributorAgent.getLocalName()
						+ " - UnreadableException " + e1.getMessage());
			}

		} else {

			block();
		}
	}
}
