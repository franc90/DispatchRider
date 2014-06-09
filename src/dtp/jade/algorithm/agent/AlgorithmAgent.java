package dtp.jade.algorithm.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import dtp.jade.CommunicationHelper;
import dtp.jade.algorithm.agent.behaviours.GetEndOfSimBehaviour;
import dtp.jade.algorithm.agent.behaviours.GetInitialDataBehaviour;
import dtp.jade.algorithm.agent.behaviours.GetRequestBehaviour;
import dtp.simmulation.SimInfo;

public abstract class AlgorithmAgent extends Agent {

	private static final long serialVersionUID = -339056596576630532L;

	protected static final Logger logger = Logger
			.getLogger(AlgorithmAgent.class);

	protected SimInfo simInfo;

	@Override
	protected void setup() {

		PropertyConfigurator.configure("conf" + File.separator
				+ "Log4j.properties");

		logger.info(this.getLocalName() + " - Hello World!");

		/* -------- INITIALIZATION SECTION ------- */
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		/* -------- SERVICES SECTION ------- */
		registerServices();

		/* -------- BEHAVIOURS SECTION ------- */
		addBehaviour(new GetInitialDataBehaviour(this));
		addBehaviour(new GetRequestBehaviour(this));
		addBehaviour(new GetEndOfSimBehaviour(this));

		sendAIDToInfoAgent();
	}

	private void sendAIDToInfoAgent() {
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"AgentCreationService");

		if (aids.length == 1) {
			AID infoAgentAID = aids[0];
			ACLMessage cfp = new ACLMessage(
					CommunicationHelper.ALGORITHM_AGENT_AID);
			cfp.addReceiver(infoAgentAID);
			try {
				cfp.setContentObject(this.getAID());
			} catch (IOException e) {
				logger.error(this.getLocalName() + " - IOException "
						+ e.getMessage());
			}
			send(cfp);

		} else {

			logger.error("None or more than one Info Agent in the system");
		}
	}

	protected void registerServices() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		/* -------- ALGORITHM SERVICE ------- */
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType(getServiceName());
		sd1.setName(getServiceName());
		dfd.addServices(sd1);
		logger.info(this.getLocalName() + " - registering " + getServiceName());

		/* -------- REGISTRATION ------- */
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.error(this.getLocalName() + " - FIPAException "
					+ fe.getMessage());
		}
	}

	protected String getServiceName() {
		String result = this.getClass().getName();
		String parts[] = result.split("\\.");
		return parts[parts.length - 1];
	}

	public abstract void init(Map<String, String> initParams);

	public abstract Serializable getResponse(String operation,
			Map<String, Object> params);

}
