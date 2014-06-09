package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

public class GetTimestampBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 7551640951018449071L;

	private static Logger logger = Logger
			.getLogger(GetTimestampBehaviour.class);

	private final DistributorAgent DistributorAgent;

	public GetTimestampBehaviour(DistributorAgent agent) {

		DistributorAgent = agent;
	}

	@Override
	public void action() {

		/*-------- RECIEVING CURRENT TIME STAMP -------*/
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.TIME_CHANGED);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {

			Integer time = null;
			try {

				time = (Integer) msg.getContentObject();

			} catch (UnreadableException e) {

				logger.error(this.DistributorAgent.getLocalName()
						+ " - IOException " + e.getMessage());
			}

			logger.info(myAgent.getLocalName() + "\t- got time stamp ["
					+ time.toString() + "]");

			DistributorAgent.nextSimstep(time);
			DistributorAgent.sendTimestampToCollector(time);

			ACLMessage cfp = new ACLMessage(
					CommunicationHelper.TIME_STAMP_CONFIRM);

			cfp.addReceiver(msg.getSender());
			try {
				cfp.setContentObject("");
				DistributorAgent.send(cfp);
			} catch (IOException e) {
				logger.error("GetTimestampBehaviour (CrisisManager) - IOException "
						+ e.getMessage());
			}

		} else {

			block();
		}
	}
}
