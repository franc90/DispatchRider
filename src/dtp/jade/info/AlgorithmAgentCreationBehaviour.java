package dtp.jade.info;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;
import dtp.jade.algorithm.agent.AlgorithmAgentInitData;

public class AlgorithmAgentCreationBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 4963319841056188714L;

	private static Logger logger = Logger
			.getLogger(AlgorithmAgentCreationBehaviour.class);

	private final InfoAgent infoAgent;

	/**
	 * Constructs a new behaviour and allows to access the Info Agent from
	 * Action method
	 * 
	 * @param agent
	 *            - Info Agent
	 */
	public AlgorithmAgentCreationBehaviour(InfoAgent agent) {

		this.infoAgent = agent;
	}

	/**
	 * Creates a new algorithm agent.
	 */
	@Override
	public void action() {

		/* -------- RECIEVING REQUESTS FOR ALGORITHM AGENT CREATION ------- */
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.ALGORITHM_AGENT_CREATION);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			AlgorithmAgentInitData initialData = null;
			try {
				initialData = (AlgorithmAgentInitData) msg.getContentObject();
			} catch (UnreadableException e) {
			}
			AgentContainer container = myAgent.getContainerController();
			AgentController newAgent = null;
			AgentInfoPOJO newAgentInfo = new AgentInfoPOJO();

			/* -------- SETTING ALGORITHM AGENT NAME ------- */
			newAgentInfo.setName("AlgorithmAgent(" + initialData.getAgentName()
					+ ")#" + this.infoAgent.getAlgorithmAgentsNo());

			/* -------- ALGORITHM AGENT CREATION ------- */
			try {
				newAgent = container.createNewAgent(newAgentInfo.getName(),
						initialData.getAgentClass().getName(), null);
				newAgent.start();
			} catch (StaleProxyException e) {
				logger.error(this.infoAgent.getLocalName()
						+ " - StaleProxyException " + e.getMessage());
			}

			/* -------- SETTING ALGORITHM AGENT CONTROLLER ------- */
			newAgentInfo.setAgentController(newAgent);

			/* -------- WAITING FOR AID FROM NEW ALGORITHM AGENT ------- */
			MessageTemplate template2 = MessageTemplate
					.MatchPerformative(CommunicationHelper.ALGORITHM_AGENT_AID);
			// TODO czekanie okreslona ilosc czasu
			ACLMessage msg2 = myAgent.blockingReceive(template2);
			AID aid = null;
			try {
				aid = (AID) msg2.getContentObject();
				/* -------- SETTING ALGORITHM AGENT AID ------- */
				newAgentInfo.setAID(aid);
			} catch (UnreadableException e) {
				logger.error(this.infoAgent.getLocalName()
						+ " - UnreadableException " + e.getMessage());
			}

			/* -------- ADDING ALGORITHM AGENT INFO TO EUNITS INFO LIST ------- */
			this.infoAgent.addAlgorithmAgentInfo(newAgentInfo);

			/*
			 * INITIALIZE ALGORITHM AGENT BY ALGORITHM AGENT INFO DATA -------
			 */
			try {
				ACLMessage cfp = new ACLMessage(
						CommunicationHelper.ALGORITHM_AGENT_INITIAL_DATA);
				cfp.addReceiver(aid);
				cfp.setContentObject(initialData);
				this.infoAgent.send(cfp);
			} catch (IOException e2) {
				logger.error(this.infoAgent.getLocalName() + " - IOException "
						+ e2.getMessage());
			}

		} else {
			block();
		}
	}
}
