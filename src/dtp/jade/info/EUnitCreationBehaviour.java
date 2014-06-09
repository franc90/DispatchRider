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
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.eunit.ExecutionUnitAgent;

public class EUnitCreationBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 4963319841056188714L;

	private static Logger logger = Logger
			.getLogger(EUnitCreationBehaviour.class);

	private final InfoAgent infoAgent;

	/**
	 * Constructs a new behaviour and allows to access the Info Agent from
	 * Action method
	 * 
	 * @param agent
	 *            - Info Agent
	 */
	public EUnitCreationBehaviour(InfoAgent agent) {

		this.infoAgent = agent;
	}

	/**
	 * Creates a new eunit agent.
	 */
	@Override
	public void action() {

		/* -------- RECIEVING REQUESTS FOR EUNIT CREATION ------- */
		MessageTemplate template = MessageTemplate
				.MatchPerformative(CommunicationHelper.EXECUTION_UNIT_CREATION);
		ACLMessage msg = myAgent.receive(template);

		if (msg != null) {
			EUnitInitialData initialData = null;
			try {
				initialData = (EUnitInitialData) msg.getContentObject();
			} catch (UnreadableException e) {
			}
			AgentContainer container = myAgent.getContainerController();
			AgentController newAgent = null;
			AgentInfoPOJO newAgentInfo = new AgentInfoPOJO();

			/* -------- SETTING EUNIT AGENT NAME ------- */
			newAgentInfo.setName("EUnitAgent#"
					+ this.infoAgent.getEUnitAgentsNo());

			/* -------- EUNIT AGENT CREATION ------- */
			try {
				newAgent = container.createNewAgent(newAgentInfo.getName(),
						ExecutionUnitAgent.class.getName(), null);
				newAgent.start();
			} catch (StaleProxyException e) {
				logger.error(this.infoAgent.getLocalName()
						+ " - StaleProxyException " + e.getMessage());
			}

			/* -------- SETTING EUNIT AGENT CONTROLLER ------- */
			newAgentInfo.setAgentController(newAgent);

			/* -------- WAITING FOR AID FROM NEW EUNIT AGENT ------- */
			MessageTemplate template2 = MessageTemplate
					.MatchPerformative(CommunicationHelper.EXECUTION_UNIT_AID);
			// TODO czekanie okreslona ilosc czasu
			ACLMessage msg2 = myAgent.blockingReceive(template2);
			AID aid = null;
			try {
				aid = (AID) msg2.getContentObject();
				/* -------- SETTING EUNIT AGENT AID ------- */
				newAgentInfo.setAID(aid);
			} catch (UnreadableException e) {
				logger.error(this.infoAgent.getLocalName()
						+ " - UnreadableException " + e.getMessage());
			}

			/* -------- ADDING EUNIT AGENT INFO TO EUNITS INFO LIST ------- */
			this.infoAgent.addEUnitAgentInfo(newAgentInfo);
			// logger.info(this.infoAgent.getLocalName() + " - AID recieved from
			// "
			// + msg2.getSender().getLocalName());

			/* -------- INITIALIZE EUNIT AGENT BY EUNIT INFO DATA ------- */
			try {
				ACLMessage cfp = new ACLMessage(
						CommunicationHelper.EUNIT_INITIAL_DATA);
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
