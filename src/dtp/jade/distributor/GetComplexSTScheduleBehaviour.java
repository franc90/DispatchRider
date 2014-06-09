package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import algorithm.Schedule;
import dtp.jade.CommunicationHelper;

public class GetComplexSTScheduleBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -8900346333249476588L;

	private final DistributorAgent agent;

	public GetComplexSTScheduleBehaviour(DistributorAgent agent) {
		this.agent = agent;
	}

	@Override
	public void action() {

		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.HOLONS_CALENDAR);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			try {
				agent.addComplexSTSchedule((Schedule) msg.getContentObject(),
						msg.getSender());
			} catch (UnreadableException e) {
				e.printStackTrace();
				System.exit(0);
			}

		} else {
			block();
		}
	}
}
