package dtp.jade.transport.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;

/**
 * Behaviour used by transport elements to receive commission information.
 * 
 * @author Michal Golacki
 */
public class GetTeamResponseBehaviour extends CyclicBehaviour {

	/** Serial version */
	private static final long serialVersionUID = -3640633318262710395L;

	/** Agent */
	private final TransportAgent agent;

	public GetTeamResponseBehaviour(TransportAgent transportAgent) {
		agent = transportAgent;
	}

	@Override
	public void action() {
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.TEAM_OFFER_RESPONSE);
		ACLMessage message = myAgent.receive(template);

		if (message != null) {
			try {
				String response = (String) message.getContentObject();
				if (response.equals("yes"))
					agent.response(message.getSender(), true);
				else if (response.equals("no"))
					agent.response(message.getSender(), false);
				else
					agent.response(message.getSender(), null);
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		} else {
			block();
		}

	}
}
