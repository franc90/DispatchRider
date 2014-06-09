package dtp.jade.info;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import dtp.jade.BaseAgent;
import dtp.jade.CommunicationHelper;
import dtp.jade.dataCollector.agent.InfoCollectorMessage;
import dtp.jade.info.behaviour.DriverCreationBehaviour;
import dtp.jade.info.behaviour.EndOfSimulationBehaviour;
import dtp.jade.info.behaviour.GetAgentsDataBehaviour;
import dtp.jade.info.behaviour.GetCollectionAgentQuery;
import dtp.jade.info.behaviour.TrailerCreationBeaviour;
import dtp.jade.info.behaviour.TruckCreationBehaviour;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportAgentsMessage;
import dtp.jade.transport.TransportType;

public class InfoAgent extends BaseAgent {

	private static final long serialVersionUID = -6570228860367529292L;

	/** Logger. */
	private static Logger logger = Logger.getLogger(InfoAgent.class);

	private ArrayList<AgentInfoPOJO> driverAgentsInfo;

	private int driverAgentsNo;

	private ArrayList<AgentInfoPOJO> truckAgentsInfo;

	private int truckAgentsNo;

	private ArrayList<AgentInfoPOJO> trailerAgentsInfo;

	private int trailerAgentsNo;

	private ArrayList<AgentInfoPOJO> eunitAgentsInfo;

	private ArrayList<AgentInfoPOJO> algorithmAgentsInfo;

	private Map<TransportType, List<TransportAgentData>> agents;

	private int eunitAgentsNo;

	private int algorithmAgentsNo;

	@Override
	protected void setup() {

		agents = new HashMap<TransportType, List<TransportAgentData>>();

		PropertyConfigurator.configure("conf" + File.separator
				+ "Log4j.properties");

		logger.info(this.getLocalName() + " - Hello World!");

		/*-------- INITIALIZATION SECTION -------*/
		this.driverAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.truckAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.trailerAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.eunitAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.algorithmAgentsInfo = new ArrayList<AgentInfoPOJO>();

		/*-------- SERVICES SECTION -------*/
		registerServices();

		/*-------- BEHAVIOURS SECTION -------*/
		addBehaviour(new EUnitCreationBehaviour(this));
		addBehaviour(new AlgorithmAgentCreationBehaviour(this));
		addBehaviour(new DriverCreationBehaviour(this));
		addBehaviour(new TruckCreationBehaviour(this));
		addBehaviour(new TrailerCreationBeaviour(this));
		addBehaviour(new EndOfSimulationBehaviour(this));
		addBehaviour(new GetAgentsDataBehaviour(this));
		addBehaviour(new GetCollectionAgentQuery(this));


		System.out.println("InfoAgent - end of initialization");
	}

	void registerServices() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		/*-------- AGENT CREATION SERVICE SECTION -------*/
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("AgentCreationService");
		sd1.setName("AgentCreationService");
		dfd.addServices(sd1);
		logger.info(this.getLocalName() + " - registering AgentCreationService");

		/*-------- AGENT DELETION SERVICE SECTION -------*/
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("AgentDeletionService");
		sd2.setName("AgentDeletionService");
		dfd.addServices(sd2);
		logger.info(this.getLocalName() + " - registering AgentDeletionService");

		/*-------- INFO AGENT SERVICE SECTION -------*/
		ServiceDescription sd3 = new ServiceDescription();
		sd3.setType("InfoAgentService");
		sd3.setName("InfoAgentService");
		dfd.addServices(sd3);
		logger.info(this.getLocalName() + " - registering AgentAgentService");

		/*-------- REGISTRATION SECTION -------*/
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.error(this.getLocalName() + " - FIPAException "
					+ fe.getMessage());
		}
	}

	public void addTransportAgentData(TransportAgentData data,
			TransportType type) {
		List<TransportAgentData> transportData = agents.get(type);
		if (transportData == null) {
			transportData = new LinkedList<TransportAgentData>();
			agents.put(type, transportData);
		}
		transportData.add(data);
	}

	public Map<TransportType, List<TransportAgentData>> getAgentsData() {
		return agents;
	}

	public int getDriverAgentsNo() {
		return this.driverAgentsNo;
	}

	public void addDriverAgentInfo(AgentInfoPOJO info) {
		this.driverAgentsInfo.add(info);
		this.driverAgentsNo++;
	}

	public int getTruckAgentsNo() {
		return this.truckAgentsNo;
	}

	public void addTruckAgentInfo(AgentInfoPOJO info) {
		this.truckAgentsInfo.add(info);
		this.truckAgentsNo++;
	}

	public int getTrailerAgentsNo() {
		return this.trailerAgentsNo;
	}

	public void addTrailerAgentInfo(AgentInfoPOJO info) {
		this.trailerAgentsInfo.add(info);
		this.trailerAgentsNo++;
	}

	public int getEUnitAgentsNo() {
		return this.eunitAgentsNo;
	}

	public void addEUnitAgentInfo(AgentInfoPOJO info) {
		this.eunitAgentsInfo.add(info);
		this.eunitAgentsNo++;
	}

	public int getAlgorithmAgentsNo() {
		return this.algorithmAgentsNo;
	}

	public void addAlgorithmAgentInfo(AgentInfoPOJO info) {
		this.algorithmAgentsInfo.add(info);
		this.algorithmAgentsNo++;
	}

	public void simEnd() {
		this.driverAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.truckAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.trailerAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.eunitAgentsInfo = new ArrayList<AgentInfoPOJO>();
		this.algorithmAgentsInfo = new ArrayList<AgentInfoPOJO>();
		eunitAgentsNo = 0;
		algorithmAgentsNo = 0;
		driverAgentsNo = 0;
		trailerAgentsNo = 0;
		truckAgentsNo = 0;
		agents = new HashMap<TransportType, List<TransportAgentData>>();
	}

	public void sendDataToAgents() {
		ACLMessage cfp = null;
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"TransportUnitService");

		logger.info("InfoAgent - sending agents data to agents");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(
						CommunicationHelper.AGENTS_DATA_FOR_TRANSPORTUNITS);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject(new TransportAgentsMessage(agents));
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				send(cfp);
			}
		}

		aids = CommunicationHelper.findAgentByServiceName(this,
				"CommissionService");

		logger.info("InfoAgent - sending agents data to Distributor");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(
						CommunicationHelper.AGENTS_DATA_FOR_TRANSPORTUNITS);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject(new TransportAgentsMessage(agents));
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				send(cfp);
			}
		}
	}
	
	public void sendDataToCollector()
	{

		
		ACLMessage cfp = null;
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,"DataCollectorService");

		logger.info("InfoAgent - sending agents data to Collector");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(CommunicationHelper.DATA_COLLECTION_FROM_INFO_REPLY);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject(new InfoCollectorMessage(this));
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				send(cfp);
				
			}
		}
		
		
		
		
		
	}
	
	
	public ArrayList<AgentInfoPOJO> getDriverAgentsInfo() {
		return driverAgentsInfo;
	}

	public ArrayList<AgentInfoPOJO> getTruckAgentsInfo() {
		return truckAgentsInfo;
	}

	public ArrayList<AgentInfoPOJO> getTrailerAgentsInfo() {
		return trailerAgentsInfo;
	}

	public ArrayList<AgentInfoPOJO> getEUnitAgentsInfo() {
		return eunitAgentsInfo;
	}

	public ArrayList<AgentInfoPOJO> getAlgorithmAgentsInfo() {
		return algorithmAgentsInfo;
	}
}
