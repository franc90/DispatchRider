package dtp.jade.distributor;

import gui.main.SingletonGUI;
import gui.parameters.DRParams;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import machineLearning.MLAlgorithm;
import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureCalculatorsHolder;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
import measure.configuration.MeasureConfigurationChanger;
import measure.configuration.MeasureConfigurationChangerImpl;
import measure.printer.MeasureData;
import measure.visualization.MeasuresVisualizationRunner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import algorithm.Algorithm;
import algorithm.AllMeasuresData;
import algorithm.GraphSchedule;
import algorithm.Helper;
import algorithm.Schedule;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import algorithm.STLike.SimulatedTrading;
import algorithm.simmulatedTrading.SimmulatdTradingParameters;
import dtp.commission.Commission;
import dtp.graph.Graph;
import dtp.jade.BaseAgent;
import dtp.jade.CommunicationHelper;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.dataCollector.agent.DistributorCollectorMessage;
import dtp.jade.dataCollector.agent.InfoCollectorMessage;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.eunit.EUnitOffer;
import dtp.jade.eunit.GetCalendarStatsBehaviour;
import dtp.jade.gui.CalendarStatsHolder;
import dtp.jade.gui.CommissionsHolder;
import dtp.jade.test.DefaultAgentsData;
import dtp.jade.transport.NewHolonOffer;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;
import dtp.jade.transport.TransportType;
import dtp.simmulation.SimInfo;
import dtp.util.AIDsComparator;

/**
 * Represents a distributor as a jade agent.
 * 
 * @author kony.pl
 */
public class DistributorAgent extends BaseAgent {
	
	private   boolean DISABLE_HOLONS=false;

	private static final long serialVersionUID = -1319416006236942887L;

	private static Logger logger = Logger.getLogger(DistributorAgent.class);

	private final AllMeasuresData allMeasuresData = new AllMeasuresData();

	// kolejka zlecen do obsluzenia
	private LinkedList<Commission> commissionsQueue;

	private ArrayList<Commission> nooneList;

	private Auction currentAuction;

	// Simmulated Trading auction
	private AuctionST currentSTAuction;

	private List<Commission> commissions;

	private String chooseWorstCommission;

	private int timestamp;
	// private double pattern=0.0;

	private int STTimestampGap;
	private int nextSTTimestamp;
	private int nextMeasureTimestamp;

	private int STCommissionsionsGap;

	private MeasureData measureData;

	private final MeasureConfigurationChanger confChanger = new MeasureConfigurationChangerImpl();

	private boolean isConfigurationChangeable = true;

	private algorithm.comparator.CommissionsComparator commissionsComparator;

	@Override
	protected void setup() {

		PropertyConfigurator.configure("conf" + File.separator
				+ "Log4j.properties");

		logger.info(this.getLocalName() + " - Hello World!");

		/* -------- INITIALIZATION SECTION ------- */
		commissionsQueue = new LinkedList<Commission>();
		nooneList = new ArrayList<Commission>();
		currentAuction = null;
		currentSTAuction = null;

		/* -------- SERVICES SECTION ------- */
		registerServices();

		/* -------- BEHAVIOURS SECTION ------- */
		addBehaviour(new GetCommissionBehaviour(this));
		addBehaviour(new GetOfferBehaviour(this));
		addBehaviour(new GetResetRequestBehaviour(this));
		addBehaviour(new GetNooneRequestBehaviour(this));
		addBehaviour(new SimEndBehaviour(this));
		addBehaviour(new GetTransportUnitPrepareForNegotiationBehaviour(this));
		addBehaviour(new GetNewHolonOfferBehaviour(this));
		addBehaviour(new GetTransportAgentsDataBehaviour(this));
		addBehaviour(new GetHolonFeedbackOfferBehaviour(this));
		addBehaviour(new GetSimInfoBehaviour(this));
		addBehaviour(new GetEUnitCreatedBehaviour(this));
		addBehaviour(new GetCommissionSendedAgainBehaviour(this));
		addBehaviour(new GetCalendarStatsBehaviour(this));
		addBehaviour(new GetSTBeginResponseBehaviour(this));
		// addBehaviour(new GetComplexSTResponseBehaviour(this));
		// addBehaviour(new GetComplexSTFinishBehaviour(this));
		addBehaviour(new GetComplexSTScheduleBehaviour(this));
		addBehaviour(new GetComplexSTScheduleChangedBehaviour(this));
		addBehaviour(new GetChangeScheduleBehaviour(this));
		addBehaviour(new GetTimestampBehaviour(this));
		addBehaviour(new GetUndeliveredCommissionResponseBehaviour(this));

		addBehaviour(new GetMeasureDataBehaviour(this));
		addBehaviour(new GetConfigurationChangeBehaviour(this));
		addBehaviour(new GetMLTableBehaviour(this));

		addBehaviour(new GetGraphChangedBehaviour(this));
		addBehaviour(new GetSendMeasuresToEUnitBehaviour(this));
		
		addBehaviour(new GetCollectionAgentQuery(this));
		
		calendarStatsHolder = null;
		transportUnitsPrepare = 0;
		transportUnitCount = -1;
		simmulatedTradingCount = 0;
		nextSTTimestamp = 0;
		nextMeasureTimestamp = 0;
		measureData = new MeasureData();
		System.out.println("DistributorAgent - end of initialization");
	}

	public void simEnd() {
		resetAgent();
	}

	public void nextSimstep(int timestamp) {
		this.timestamp = timestamp;
		if (timestamp >= nextSTTimestamp + STTimestampGap) {
			nextSTTimestamp += STTimestampGap;
		}
		if (calculatorsHolder != null
				&& timestamp >= nextMeasureTimestamp
						+ calculatorsHolder.getTimeGap())
			nextMeasureTimestamp += calculatorsHolder.getTimeGap();

	}

	/**
	 * Registers Distributor Agent's services in a DF Service.
	 */
	public void registerServices() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		/* --------- COMMISSION SERVICE --------- */
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("CommissionService");
		sd1.setName("CommissionService");
		dfd.addServices(sd1);
		logger.info(this.getLocalName() + " - registering CommissionService");

		/* --------- REGISTRATION --------- */
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.error(this.getLocalName() + " - FIPAException "
					+ fe.getMessage());
		}
	}

	Map<TransportType, List<TransportAgentData>> agents;

	private SimInfo simInfo;
	private MeasureCalculatorsHolder calculatorsHolder;
	private MLAlgorithm mlAlgorithm;
	private MeasuresVisualizationRunner measuresVisualizationRunner;

	public synchronized void setSimInfo(SimInfo simInfo) {
		this.simInfo = simInfo;
		this.commissionsComparator = simInfo.getComparator();

		this.calculatorsHolder = simInfo.getCalculatorsHolder();

		if (calculatorsHolder != null) {

			if (calculatorsHolder.getVisualizationMeasuresNames().size() > 0)
				measuresVisualizationRunner = new MeasuresVisualizationRunner(
						calculatorsHolder);
			else
				measuresVisualizationRunner = null;

		}

		this.mlAlgorithm = simInfo.getMlAlgorithm();
		if (calculatorsHolder != null)
			this.calculatorsHolder.setSimInfo(simInfo);
		if (mlAlgorithm != null)
			this.mlAlgorithm.setSimInfo(simInfo);

		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"GUIService");
		ACLMessage cfp = new ACLMessage(CommunicationHelper.SIM_INFO_RECEIVED);

		cfp.addReceiver(aids[0]);
		try {
			cfp.setContentObject("");
			this.send(cfp);
		} catch (IOException e) {
			logger.error("EunitCreationBehaviour - IOException "
					+ e.getMessage());
		}
	}

	public synchronized void setAgentsData(
			Map<TransportType, List<TransportAgentData>> agents) {
		this.agents = agents;

		ACLMessage cfp = null;

		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"GUIService");

		if (aids.length == 1) {
			cfp = new ACLMessage(
					CommunicationHelper.TRANSPORT_AGENT_CONFIRMATION);
			cfp.addReceiver(aids[0]);
			try {
				cfp.setContentObject("");
			} catch (IOException e) {
				logger.error(getLocalName() + " - IOException "
						+ e.getMessage());
			}
			send(cfp);
		} else {
			logger.error(getLocalName()
					+ " - none or more than one agent with GUIService in the system");
		}
	}

	private TransportElementInitialDataTruck getTruck(AID aid) {
		for (TransportAgentData agent : agents.get(TransportType.TRUCK)) {
			if (agent.getAid().equals(aid))
				return (TransportElementInitialDataTruck) agent.getData();
		}
		return null;
	}

	private TransportElementInitialDataTrailer getTrailer(AID aid) {
		for (TransportAgentData agent : agents.get(TransportType.TRAILER)) {
			if (agent.getAid().equals(aid))
				return (TransportElementInitialDataTrailer) agent.getData();
		}
		return null;
	}

	private TransportElementInitialData getDriver(AID aid) {
		for (TransportAgentData agent : agents.get(TransportType.DRIVER)) {
			if (agent.getAid().equals(aid))
				return agent.getData();
		}
		return null;
	}

	private int handeledCommissionsCount = 0;
	private boolean commissionSendingType;
	private boolean choosingByCost;
	private int simmulatedTradingCount;
	private int STDepth;
	private Commission[] commsGroup;
	private DefaultAgentsData defaultAgentsData;
	private boolean dist;
	private List<Commission> commissionsToCarry;
	private boolean confSet = false;

	/**
	 * Wolana po przeslaniu zlecen przez dystrybutora
	 * 
	 * @param com
	 * @param type
	 * @param choosingByCost
	 * @param data
	 */
	public synchronized void setCommissions(Commission[] com,
			CommissionsHolder holder) {

		if (confSet == false) {
			this.commissionSendingType = holder.getType();
			this.dist = holder.isDist();
			this.choosingByCost = holder.isChoosingByCost();
			this.simmulatedTradingCount = holder.getSimmulatedTrading();
			this.commsGroup = com;
			this.defaultAgentsData = holder.getDefaultAgentsData();
			this.chooseWorstCommission = holder.getChooseWorstCommission();
			this.algorithm = holder.getAlgorithm();
			this.maxFullSTDepth = holder.getSTDepth();
			this.STTimestampGap = holder.getSTTimestampGap();
			this.STCommissionsionsGap = holder.getSTCommissionGap();
			this.isConfigurationChangeable = holder.isConfChange();
			confSet = true;
			
			if (SingletonGUI.getShallWork()){
				DRParams params = new DRParams();
				params.setChoosingByCost(this.choosingByCost);
				params.setCommissionSendingType(this.commissionSendingType);
				params.setDist(this.dist);
				params.setAlgorithm(""+this.algorithm);
				params.setChooseWorstCommission(this.chooseWorstCommission);
				params.setMaxFullSTDepth(this.maxFullSTDepth);
				params.setSimmulatedTradingCount(this.simmulatedTradingCount);
				params.setSTCommissionsionsGap(this.STCommissionsionsGap);
				params.setSTTimestampGap(this.STTimestampGap);
				SingletonGUI.getInstance().update(params);
			}
		}

		commissions = new LinkedList<Commission>();
		commissionsToCarry = new LinkedList<Commission>();
		simmulatedTrading = false;
		for (Commission c : com) {
			commissions.add(c);
			commissionsToCarry.add(Commission.copy(c));
		}

		// pattern=new PatternCalculator(commissions).pattern1();

		commissions = commissionsComparator.sort(commissions, this, simInfo);

		if (commissions.size() > 0)
			if (commissionSendingType == false)
				addCommission(commissions.remove(0));
			else
				addCommission(null);
	}

	// odbiera zlecenie od GUIAgenta...
	public synchronized void addCommission(Commission commission) {
		if (commissionSendingType == false) {
			handeledCommissionsCount++;
			sendGUIMessage("new commission added to the queue (id = "
					+ commission.getID() + ")");
			logger.info(getLocalName()
					+ " - new commission added to the queue (id = "
					+ commission.getID() + ")");
		} else {
			sendGUIMessage("new commission package added - "
					+ commissions.size());
			logger.info(getLocalName() + " - new commission package added - "
					+ commissions.size());
		}
		commissionsQueue.add(commission);

		if (commissionSendingType == false)
			carryCommission();
		else
			carryCommissions();
	}

	private Commission currentCommission;

	// jezeli kolejka zlecen nie jest pusta i nie jest wlasnie przeprowadzana
	// jakas aukcja, pobiera zlecenie i rozpoczyna aukcje
	private synchronized void carryCommission() {

		if (simInfo.getPunishmentFunction() != null) {
			int holons = CommunicationHelper.findAgentByServiceName(this,
					"ExecutionUnitService").length;
			if (holons < simInfo.getHolons())
				choosingByCost = true;
			else
				choosingByCost = false;

		}

		simmulatedTrading = false;
		currentCommission = commissionsQueue.poll();

		commissionsToCarry.remove(currentCommission);
		eUnitOffers = new LinkedList<EUnitOffer>();
		eUnitsCount = sendOffers(currentCommission);
		if (eUnitsCount == 0) {
			beginTransportUnitsNegotiation();
		}

	}

	private synchronized void carryCommissions() {
		simmulatedTrading = false;

		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"TransportUnitService");

		logger.info(getLocalName()
				+ " - sending commission(s) to Transport Agents");

		ACLMessage cfp = new ACLMessage(CommunicationHelper.COMMISSION);

		for (int i = 0; i < aids.length; i++) {
			cfp.addReceiver(aids[i]);
		}
		try {
			cfp.setContentObject(commsGroup);
		} catch (IOException e) {
			logger.error("IOException " + e.getMessage());
		}
		send(cfp);
	}

	// wysyla oferty do wszystkich EUnitow
	private int sendOffers(Commission commission) {

		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"ExecutionUnitService");

		if (aids.length != 0) {

			ACLMessage cfp = new ACLMessage(
					CommunicationHelper.COMMISSION_OFFER_REQUEST);

			for (int i = 0; i < aids.length; i++)
				cfp.addReceiver(aids[i]);

			try {
				cfp.setContentObject(commission);
			} catch (IOException e) {
				logger.error(this.getLocalName() + " - IOException "
						+ e.getMessage());
			}
			send(cfp);
		}

		return aids.length;
	}

	public synchronized void addOffer(EUnitOffer offer) {

		if (simmulatedTrading == false) {
			eUnitsCount--;
			if (offer.getValue() > 0)
				eUnitOffers.add(offer);
			if (eUnitsCount == 0) {
				sendGUIMessage("eUnits offers are collected");
				if (choosingByCost == false && eUnitOffers.size() > 0)
					chooseBestOffer();
				else
					beginTransportUnitsNegotiation();
			}
		} else {
			currentAuction.addOffer(offer);
			if (currentAuction.gotAllOffers()) {
				sendGUIMessage("all EUnit offres has been collected (com id = "
						+ currentAuction.getCommission().getID() + ")");
				logger.info(getLocalName()
						+ " - all EUnit offres has been collected (com id = "
						+ currentAuction.getCommission().getID() + ")");

				chooseBestSTOffer();
			}
		}
	}

	private void beginTransportUnitsNegotiation() {
		
		if(DISABLE_HOLONS==false)
		{
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"TransportUnitService");

		logger.info(getLocalName()
				+ " - sending commission(s) to Transport Agents");

		ACLMessage cfp = new ACLMessage(CommunicationHelper.COMMISSION);

		for (int i = 0; i < aids.length; i++) {
			cfp.addReceiver(aids[i]);
		}
		try {
			cfp.setContentObject(new Commission[] { currentCommission });
		} catch (IOException e) {
			logger.error("IOException " + e.getMessage());
		}
		send(cfp);
		}
		else
		{
			
		//EUnitOffer tmp_offer = new EUnitOffer(myAID, bestCost, STCommissionsionsGap);
			AID[] eunit_aids = CommunicationHelper.findAgentByServiceName(this,"ExecutionUnitService");
			for(int i=0; i<eunit_aids.length;i++){
				addOffer(new EUnitOffer(eunit_aids[i],1,1));
			}
		chooseBestOffer();	
		}
		
	}

	// TODO algorithm
	private void chooseBestSTOffer() {
		EUnitOffer[] offers = currentAuction.getOffers();
		double bestValue = Double.MAX_VALUE;
		AID bestHolon = null;

		for (EUnitOffer offer : offers) {
			if (offer.getValue() < bestValue && offer.getValue() >= 0) {
				bestValue = offer.getValue();
				bestHolon = offer.getAgent();
			}
		}

		if (bestHolon != null) {
			sendGUIMessage("commission goes to " + bestHolon.getLocalName()
					+ " (com id = " + currentAuction.getCommission().getID()
					+ ")");
			logger.info(getLocalName() + " - commission goes to "
					+ bestHolon.getLocalName() + " (com id = "
					+ currentAuction.getCommission().getID() + ")");

			sendGUIMessage("sending feedback to EUnit Agents (com id = "
					+ currentAuction.getCommission().getID() + ")");
			logger.info(getLocalName()
					+ " - sending feedback to EUnit Agents (com id = "
					+ currentAuction.getCommission().getID() + ")");

			sendFeedback(bestHolon, currentAuction.getCommission());
		} else {

			// TODO
			// createDefaultHolon(currentAuction.getCommission());
			sendFeedback(currentSTAuction.getOwnerAID(),
					currentAuction.getCommission());
		}

	}

	private void checkSTStatus() {
		AID[] aids = null;
		ACLMessage cfp = null;

		aids = CommunicationHelper.findAgentByServiceName(this,
				"ExecutionUnitService");

		if (aids.length > 0) {
			calendarStatsHolder = new CalendarStatsHolder(aids.length);

			cfp = new ACLMessage(CommunicationHelper.EUNIT_SHOW_STATS);

			for (int i = 0; i < aids.length; i++) {

				cfp.addReceiver(aids[i]);
			}

			try {
				cfp.setContentObject("");
			} catch (IOException e) {
				logger.error(getLocalName() + " - IOException "
						+ e.getMessage());
			}
			send(cfp);
		}
	}

	private CalendarStatsHolder calendarStatsHolder;

	public synchronized void addCalendarStats(CalendarStats calendarStats) {

		if (calendarStatsHolder == null) {

			logger.error(getLocalName()
					+ " - no calendarStatsHolder to add stats to");
			System.exit(0);
		}

		calendarStatsHolder.addCalendarStats(calendarStats);
	}

	private boolean simmulatedTrading;

	private void simmulatedTrading() {

		System.out.println("fullSimmulatedTrading");
		fullSimmulatedTrading = true;
		complexSimmulatedTrading(null);

	}

	private void fullSimmulatedTrading(Map<AID, Schedule> holons) {

		Map<AID, Schedule> tmpMapForMeasure = Helper.copyAID(holons);

		for (AID i : holons.keySet()) {
			Map<AID, Schedule> tmpMap = Helper.copyAID(holons);

			holons = simInfo
					.getExchangeAlgFactory()
					.getAlgAfterComAdd()
					.doExchangesAfterComAdded(this, holons.keySet(), holons, i,
							simInfo, timestamp);

			// holons = SimmulatedTrading.fullSimmulatedTrading(holons.keySet(),
			// holons, i, 1, simInfo, new HashSet<Integer>(),
			// chooseWorstCommission, timestamp);

			if (Helper.calculateCalendarCost(holons, simInfo.getDepot()) > Helper
					.calculateCalendarCost(tmpMap, simInfo.getDepot())) {
				holons = tmpMap;
			}
		}

		oldSchedule = tmpMapForMeasure;
		newSchedule = holons;
		// calculateMeasure(tmpMapForMeasure, holons);

		eUnitsCount = holons.size();// getEUnitsAids().length;
		fullSimmulatedTrading = false;
		for (AID aid : holons.keySet()) {// getEUnitsAids()) {
			ACLMessage msg = new ACLMessage(
					CommunicationHelper.HOLONS_NEW_CALENDAR);
			msg.addReceiver(aid);
			try {
				msg.setContentObject(holons.get(aid));
				send(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (eUnitsCount == 0) {
			newSchedule = null;
			scheduleChanged();
		}

	}

	public synchronized void STBeginResponse() {
		eUnitsCount--;
		if (eUnitsCount == 0) {
			checkSTStatus();
		}
	}

	private CalendarStats bestStat;

	public synchronized void addWorstCommissionCost(CalendarStats stat) {
		eUnitsCount--;
		if (stat.getCost() > 0) {
			if (bestStat == null)
				bestStat = stat;
			else {
				if (bestStat.getCost() > stat.getCost())
					bestStat = stat;
			}
		}
		if (eUnitsCount == 0) {
			if (bestStat.getCost() < 0) {
				// TODO koniec
			} else {
				ACLMessage msg = new ACLMessage(
						CommunicationHelper.CHANGE_SCHEDULE);
				msg.addReceiver(stat.getAID());
				try {
					msg.setContentObject(stat.getSchedule2());
					send(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void changeSchedule() {
		checkSTStatus();
	}

	/*
	 * private void sendRequestForEUnitsInfo() {
	 * 
	 * AID[] aids = null; ACLMessage cfp = null;
	 * 
	 * aids = CommunicationHelper.findAgentByServiceName(this,
	 * "ExecutionUnitService");
	 * 
	 * if (aids.length != 0) {
	 * 
	 * for (int i = 0; i < aids.length; i++) {
	 * 
	 * cfp = new ACLMessage(CommunicationHelper.EUNIT_SEND_INFO);
	 * cfp.addReceiver(aids[i]); try { cfp.setContentObject(""); } catch
	 * (IOException e) { logger.error(getLocalName() + " - IOException " +
	 * e.getMessage()); } send(cfp); } } }
	 */

	private void sendFeedback(AID aid, Commission commission) {

		ACLMessage cfp = new ACLMessage(CommunicationHelper.FEEDBACK);

		cfp.addReceiver(aid);
		try {
			SimmulatdTradingParameters params = currentSTAuction.getParams();
			params.commission = commission;
			cfp.setContentObject(params);
		} catch (IOException e) {
			logger.error(this.getLocalName() + " - IOException "
					+ e.getMessage());
		}
		send(cfp);
	}

	private void sendGUIMessage(String messageText) {

		if (messageText.equals("NEXT_SIMSTEP") && timestamp >= nextSTTimestamp)
			nextSTTimestamp += STTimestampGap;

		if (messageText.equals("NEXT_SIMSTEP") && calculatorsHolder != null
				&& timestamp >= nextMeasureTimestamp)
			nextMeasureTimestamp += calculatorsHolder.getTimeGap();

		AID[] aids = null;

		aids = CommunicationHelper.findAgentByServiceName(this, "GUIService");

		if (aids.length == 1) {
			send(aids[0], getLocalName() + " - " + messageText,
					CommunicationHelper.GUI_MESSAGE);
		} else {
			logger.error(getLocalName()
					+ " - none or more than one agent with GUIService in the system");
		}
	}

	public synchronized void sendNooneList() {

		AID[] aids = null;
		// ACLMessage cfp = null;

		aids = CommunicationHelper.findAgentByServiceName(this, "GUIService");

		if (aids.length == 1) {
			Serializable content = null;
			if (nooneList == null) {
				content = new Integer(0);
			} else {
				content = new Integer(nooneList.size());
			}
			send(aids[0], content, CommunicationHelper.NOONE_LIST);
		} else {
			logger.error(getLocalName()
					+ " - none or more than one agent with GUIService in the system");
		}
	}

	// zwraca POSORTOWANE identyfikatory AID EUnitow
	@SuppressWarnings("unchecked")
	private AID[] getEUnitsAids() {

		AID[] aids;
		ArrayList<AID> aidsList;
		Iterator<AID> iter;
		int count;

		aids = CommunicationHelper.findAgentByServiceName(this,
				"ExecutionUnitService");

		aidsList = new ArrayList<AID>();
		for (int i = 0; i < aids.length; i++) {

			aidsList.add(aids[i]);
		}

		Collections.sort(aidsList, new AIDsComparator());

		iter = aidsList.iterator();
		aids = new AID[aids.length];
		count = 0;
		while (iter.hasNext()) {

			aids[count++] = iter.next();
		}

		return aids;
	}

	public synchronized void resetAgent() {

		commissionsQueue = new LinkedList<Commission>();
		nooneList = new ArrayList<Commission>();
		currentAuction = null;
		currentSTAuction = null;
		transportUnitsPrepare = 0;
		transportUnitCount = -1;
		measureData = new MeasureData();
		confSet = false;
		nextSTTimestamp = 0;
		nextMeasureTimestamp = 0;
	}

	private int transportUnitsPrepare;
	private int transportUnitCount;

	/**
	 * Wprowadzona w celach synchronizacyjnych
	 */
	public synchronized void transportUnitPreparedForNegotiation() {
		transportUnitsPrepare++;

		if (transportUnitCount == -1)
			transportUnitCount = CommunicationHelper.findAgentByServiceName(
					this, "TransportUnitService").length;
		if (transportUnitsPrepare == transportUnitCount) {
			transportUnitsPrepare = 0;

			System.out.println("START");
			AID[] aids = CommunicationHelper.findAgentByServiceName(this,
					"TransportUnitService");

			logger.info(getLocalName()
					+ " - sending signal to Transport Agents to start negotiation process");

			newHolonOffers = new TreeSet<NewHolonOffer>();

			ACLMessage cfp = new ACLMessage(
					CommunicationHelper.START_NEGOTIATION);

			for (int i = 0; i < aids.length; i++) {
				cfp.addReceiver(aids[i]);
			}
			try {
				cfp.setContentObject("");
			} catch (IOException e) {
				logger.error("IOException " + e.getMessage());
			}
			send(cfp);

		}
	}

	private Set<NewHolonOffer> newHolonOffers;
	private List<NewHolonOffer> newHolonOffersList;

	/**
	 * Wolana po przybyciu nowej oferty przez TransportUnity
	 * 
	 * @param offer
	 */
	public synchronized void newHolonOffer(NewHolonOffer offer) {
		newHolonOffers.add(offer);
		transportUnitsPrepare++;
		if (transportUnitsPrepare == transportUnitCount) {
			transportUnitsPrepare = 0;
			if (commissionSendingType == false)
				chooseBestHolon();
			else {
				List<NewHolonOffer> offers = new LinkedList<NewHolonOffer>();
				for (NewHolonOffer o : newHolonOffers) {
					if (o.isValid())
						offers.add(o);
				}
				newHolonOffersList = offers;
				sendCommissionsToBestHolons();
			}
		}
	}

	private NewHolonOffer bestOffer;
	private double bestCost;
	private int eUnitsCount;
	private List<EUnitOffer> eUnitOffers;

	public static double calculateCost(
			TransportElementInitialDataTruck truckData,
			TransportElementInitialDataTrailer trailerData,
			TransportElementInitialData driver, double distance,
			Commission commission) {
		double truckValue = TransportAgent.costFunctionValue(
				truckData.getCostFunction(), distance, driver, truckData,
				trailerData, commission, null);
		double trailerValue = TransportAgent.costFunctionValue(
				trailerData.getCostFunction(), distance, driver, truckData,
				trailerData, commission, null);
		double driverValue = TransportAgent.costFunctionValue(
				driver.getCostFunction(), distance, driver, truckData,
				trailerData, commission, null);
		return truckValue + trailerValue + driverValue;
	}

	/**
	 * Metoda wolana po tym jak wszystkie TransportUnity zakonczyli nagocjacje,
	 * w trybie wysylania zlecen jeden po drugim
	 */
	private void chooseBestHolon() {
		bestOffer = null;
		bestCost = Double.MAX_VALUE;

		double dist = (currentCommission.getPickupX() - currentCommission
				.getDeliveryX())
				* (currentCommission.getPickupX() - currentCommission
						.getDeliveryX())
				+ (currentCommission.getPickupY() - currentCommission
						.getDeliveryY())
				* (currentCommission.getPickupY() - currentCommission
						.getDeliveryY());

		// pattern=new PatternCalculator(commissionsToCarry).pattern1();
		// bestOffer=HolonPatternChooser.getBestOffer(newHolonOffers, pattern,
		// dist, currentCommission);
		double cost;
		for (NewHolonOffer offer : newHolonOffers) {
			if (offer.isValid() == false)
				continue;
			cost = calculateCost(getTruck(offer.getTruck()),
					getTrailer(offer.getTrailer()),
					getDriver(offer.getDriver()), dist, currentCommission);
			if (cost < bestCost) {
				bestCost = cost;
				bestOffer = offer;
			}

		}
		if (bestOffer != null) {
			bestCost = calculateCost(bestOffer.getTruckData(),
					bestOffer.getTrailerData(), bestOffer.getDriverData(),
					dist, currentCommission);
			System.out.println(newHolonOffers.size());
			System.out.println("Najlepsza oferta o koszcie " + bestCost
					+ " to:");
			System.out.println(bestOffer.getDriver() + " "
					+ bestOffer.getTruck() + " " + bestOffer.getTrailer());
		} else {
			bestCost = Double.MAX_VALUE;
		}

		chooseBestOffer();
	}

	/**
	 * Metoda wolana po tym jak wszystkie TransportUnity zakonczyli nagocjacje,
	 * w trybie wysylania paczkami
	 */
	private synchronized void sendCommissionsToBestHolons() {
		if (simInfo.getPunishmentFunction() != null) {
			int holons = CommunicationHelper.findAgentByServiceName(this,
					"ExecutionUnitService").length;
			if (holons < simInfo.getHolons())
				choosingByCost = true;
			else
				choosingByCost = false;
		}

		if (newHolonOffersList == null || newHolonOffersList.size() == 0) {
			sendNextCommissionToEUnit();
		} else {
			transportAgentsCount = 3;

			NewHolonOffer offer = newHolonOffersList.remove(0);
			bestOffer = offer;
			sendGUIMessage("new holon: [" + offer.getDriver().getLocalName()
					+ ", " + offer.getTrailer().getLocalName() + ", "
					+ offer.getTruck().getLocalName() + "]");
			NewTeamData data = new NewTeamData(offer.getTruck(),
					getTruck(offer.getTruck()), offer.getTrailer(),
					getTrailer(offer.getTrailer()), offer.getDriver(),
					getDriver(offer.getDriver()), null, STDepth, algorithm,
					dist, timestamp);
			createNewEUnit(data);
		}
	}

	/**
	 * Przesyla zlecenia do EUnitow, w trybie przesylania paczkami
	 */
	private synchronized void sendNextCommissionToEUnit() {
		handeledCommissionsCount++;
		simmulatedTrading = false;
		if (commissions.size() == 0) {
			System.out.println("Zlecenia przyznane");
			sendGUIMessage("NEXT_SIMSTEP");
			return;
		}
		eUnitOffers = new LinkedList<EUnitOffer>();
		bestCost = Double.MAX_VALUE;
		currentCommission = commissions.remove(0);
		sendGUIMessage("search for EUnit to carry commission (id="
				+ currentCommission.getID() + ")");
		eUnitsCount = sendOffers(currentCommission);
		if (eUnitsCount == 0) {
			createDefaultHolon(currentCommission);
		}
	}

	/**
	 * Wybor oferty w trybie wysylania zlecenia po zleceniu
	 */
	private void chooseBestOffer() {
		transportAgentsCount = 1;
		Collections.sort(eUnitOffers);
		if (eUnitOffers.size() == 0) {
			sendTeamToEUnit();
			return;
		}

		if (choosingByCost == false) {
			sendCommissionToEUnit();
			return;
		}

		if (bestCost < eUnitOffers.get(0).getValue()) {
			sendTeamToEUnit();
		} else {
			sendCommissionToEUnit();
		}

	}

	private int transportAgentsCount;

	/**
	 * Inicjuje tworzenie nowego EUnita
	 */
	private void sendTeamToEUnit() {
		if (commissionSendingType) {
			createDefaultHolon(currentCommission);
			return;
		}

		System.out.println("Send Team");
		if (bestOffer == null) {
			createDefaultHolon(currentCommission);
			/*
			 * System.err.println("Brak wolnych EUnit�w!!!");
			 * nooneList.add(currentCommission);
			 * 
			 * if (commissions.size() > 0) addCommission(commissions.remove(0));
			 * else sendGUIMessage("NEXT_SIMSTEP");
			 */
			return;
		}

		NewTeamData data = new NewTeamData(bestOffer.getTruck(),
				getTruck(bestOffer.getTruck()), bestOffer.getTrailer(),
				getTrailer(bestOffer.getTrailer()), bestOffer.getDriver(),
				getDriver(bestOffer.getDriver()), currentCommission, STDepth,
				algorithm, dist, timestamp);

		transportAgentsCount = 3;

		createNewEUnit(data);

	}

	/**
	 * 
	 */
	private void sendCommissionToEUnit() {
		System.out.println("SendCommission to "
				+ eUnitOffers.get(0).getAgent().getLocalName() + ", cost = "
				+ eUnitOffers.get(0).getValue());
		sentCommissionToEUnit();
	}

	/**
	 * Potwierdzenie dla TransportUnita, ze jest czescia holonu
	 * 
	 * @param aid
	 */
	private void sentConfirmationToTransportUnit(AID aid) {
		ACLMessage cfp = new ACLMessage(
				CommunicationHelper.CONFIRMATIO_FROM_DISTRIBUTOR);

		cfp.addReceiver(aid);
		try {
			cfp.setContentObject("");
		} catch (IOException e) {
			logger.error(this.getLocalName() + " - IOException "
					+ e.getMessage());
		}
		send(cfp);
	}

	/**
	 * 
	 */
	private void sentCommissionToEUnit() {
		ACLMessage cfp = new ACLMessage(
				CommunicationHelper.COMMISSION_FOR_EUNIT);

		cfp.addReceiver(eUnitOffers.get(0).getAgent());
		try {
			cfp.setContentObject(currentCommission);
		} catch (IOException e) {
			logger.error(this.getLocalName() + " - IOException "
					+ e.getMessage());
		}
		send(cfp);

	}

	/**
	 * Uzywana w celach synchronizacyjnych
	 */
	public synchronized void feedbackFromEUnit() {
		transportAgentsCount--;
		if (transportAgentsCount == 0) {

			// TODO
			// tutaj!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

			if (simmulatedTradingCount != 0 && checkSTCondition()) {
				if (commissionSendingType == false)
					simmulatedTrading();
				else if (currentCommission != null)
					simmulatedTrading();
				else
					getCalendarsForMesure();
			} else {
				getCalendarsForMesure();
			}
		}
	}

	private NewTeamData newTeamData;

	/**
	 * Tworzenie nowego EUnita
	 * 
	 * @param data
	 */
	protected void createNewEUnit(NewTeamData data) {
		newTeamData = data;
		if (checkSTCondition()) {
			complexSimmulatedTrading(Commission.copy(data.getCommission()));
		} else {
			createNewEUnit();
		}
	}

	private synchronized void createNewEUnit() {
		sendGUIMessage("create new EUnit");
		NewTeamData data = newTeamData;

		if (!checkCommission(data)) {
			AID aids[] = CommunicationHelper.findAgentByServiceName(this,
					"GUIService");
			ACLMessage message = new ACLMessage(
					CommunicationHelper.UNDELIVERIED_COMMISSION);
			message.addReceiver(aids[0]);
			try {
				message.setContentObject(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(message);

			return;
		}

		EUnitInitialData initialData = new EUnitInitialData(simInfo, data);

		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"AgentCreationService");

		if (aids.length == 1) {

			ACLMessage cfp = new ACLMessage(
					CommunicationHelper.EXECUTION_UNIT_CREATION);
			cfp.addReceiver(aids[0]);
			try {
				cfp.setContentObject(initialData);
			} catch (IOException e) {
				logger.error("IOException " + e.getMessage());
			}
			send(cfp);
		} else {
			logger.error("None or more than one Info Agent in the system");
		}
	}

	public synchronized void undeliveredCommissionResponse() {
		if (commissionSendingType == false) {
			if (commissions.size() > 0)
				addCommission(commissions.remove(0));
			else
				sendGUIMessage("NEXT_SIMSTEP");
		} else {
			sendCommissionsToBestHolons();
		}
	}

	private synchronized boolean checkCommission(NewTeamData data) {
		Algorithm algorithm = data.getAlgorithm();
		algorithm.init(data.getTrailer().getCapacity(), simInfo);
		Schedule schedule = simInfo.createSchedule(null);
		schedule.setAlgorithm(algorithm);
		schedule.setCreationTime(data.getCreationTime());

		if (data.getCommission() != null
				&& data.getTrailer().getCapacity() < data.getCommission()
						.getLoad()) {
			System.err
					.println("Ustawiono za mala pojemnosc domyslnej przyczepy (zlecenie id="
							+ data.getCommission().getID()
							+ " nie moze byc zrealizowane)");
			System.exit(0);
		}
		if (data.getCommission() != null) {
			Schedule tmpSchedule = algorithm.makeSchedule(this,
					data.getCommission(), null, schedule, timestamp);
			if (tmpSchedule == null) {
				return false;
			}
		}
		return true;
	}

	private boolean defaultEUnitCreation;

	/**
	 * Powiadomienie o stworzeniu EUnita
	 */
	public synchronized void eUnitCreated() {
		if (defaultEUnitCreation) {
			defaultEUnitCreation = false;
			if (simmulatedTradingCount == 0 || !checkSTCondition()) {
				getCalendarsForMesure();
			} else
				simmulatedTrading();
			return;
		}
		sentConfirmationToTransportUnit(bestOffer.getTruck());
		sentConfirmationToTransportUnit(bestOffer.getTrailer());
		sentConfirmationToTransportUnit(bestOffer.getDriver());
	}

	/**
	 * Tworzy nowego wypozyczonego holona
	 * 
	 * @param commission
	 */
	private synchronized void createDefaultHolon(Commission commission) {
		sendGUIMessage("create new default EUnit");
		defaultEUnitCreation = true;
		TransportElementInitialData truck = new TransportElementInitialDataTruck(
				null, defaultAgentsData.getPower(), 0, 0,
				defaultAgentsData.getPower(),
				defaultAgentsData.getReliability(),
				defaultAgentsData.getComfort(),
				defaultAgentsData.getFuelConsumption(), 0);
		TransportElementInitialDataTrailer trailer = new TransportElementInitialDataTrailer(
				null, defaultAgentsData.getCapacity(), 0, 0,
				defaultAgentsData.getMass(), defaultAgentsData.getCapacity(),
				defaultAgentsData.getCargoType(),
				defaultAgentsData.getUniversality(), 0);
		NewTeamData data = new NewTeamData(null, truck, null, trailer, null,
				null, commission, STDepth, algorithm, dist, timestamp);
		createNewEUnit(data);
	}

	/* Complex SimmulatedTrading Part */
	private AuctionST complexST;
	private Commission complexSTCommission;

	public synchronized AID getNextUnit() {
		AID result = complexST.getCurrentAID();
		complexST.increaseCurrentAIDNumber();
		return result;
	}

	private Map<AID, Schedule> holons;
	private Set<AID> holonsAIDs;

	public synchronized void complexSimmulatedTrading(Commission com) {
		holons = new HashMap<AID, Schedule>();
		holonsAIDs = new HashSet<AID>();
		AID aids[] = getEUnitsAids();
		eUnitsCount = aids.length;
		if (com != null)
			complexSTCommission = Commission.copy(com);
		if (eUnitsCount == 0) {
			createNewEUnit();
			return;
		}
		ACLMessage msg = new ACLMessage(CommunicationHelper.HOLONS_CALENDAR);
		for (AID aid : aids) {
			holonsAIDs.add(aid);
			msg.addReceiver(aid);
		}

		try {
			msg.setContentObject("");
			send(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * complexSTCommission=Commission.copy(com); complexST=new
		 * AuctionST(getEUnitsAids()); SimmulatdTradingParameters params=new
		 * SimmulatdTradingParameters(); params.commission=Commission.copy(com);
		 * params.STDepth=maxComplexSTDepth; params.commissionsId=new
		 * TreeSet<Integer>(); if(complexST.allDone()) { closeComplexST(false);
		 * return; } sendComplexSTRequest(getNextUnit(), params);
		 */
	}

	private boolean complexSTStatus;

	public synchronized void finishComplexSimmulatedTrading() {
		eUnitsCount--;
		if (eUnitsCount == 0) {
			if (!complexSTStatus) {
				createNewEUnit();
			} else {

				if (commissionSendingType == false) {
					if (commissions.size() > 0)
						addCommission(commissions.remove(0));
					else
						sendGUIMessage("NEXT_SIMSTEP");
				} else {
					sendCommissionsToBestHolons();
				}

			}
		}
	}

	private int maxFullSTDepth = 8;
	private boolean fullSimmulatedTrading = false;
	private Algorithm algorithm;

	public synchronized void addComplexSTSchedule(Schedule schedule, AID sender) {
		if (schedule == null)
			eUnitsCount--;
		else
			holons.put(sender, schedule);

		if (holons.size() == eUnitsCount) {
			if (calendarsForMeasures) {
				calculateMeasure(holons, null);
				return;
			}

			Map<AID, Schedule> map = new HashMap<AID, Schedule>();
			for (AID key : holons.keySet()) {
				if (holons.get(key).size() > 0)
					map.put(key, holons.get(key));
			}
			holons = map;
			if (fullSimmulatedTrading) {
				fullSimmulatedTrading(holons);
				return;
			}

			Map<AID, Schedule> tmp = simInfo
					.getExchangeAlgFactory()
					.getAlgWhenCantAdd()
					.doExchangesWhenCantAddCom(this, holons.keySet(),
							Helper.copyAID(holons),
							Commission.copy(complexSTCommission), simInfo,
							timestamp);

			// Map<AID, Schedule> tmp = SimmulatedTrading
			// .complexSimmulatedTrading(holons.keySet(),
			// Helper.copyAID(holons),
			// Commission.copy(complexSTCommission),
			// maxFullSTDepth, new TreeSet<Integer>(), timestamp,
			// simInfo, simInfo.isFirstComplexSTResultOnly());

			if (tmp != null) {
				oldSchedule = holons;
				newSchedule = tmp;
				// calculateMeasure(holons, tmp);
				eUnitsCount = tmp.size();// getEUnitsAids().length;
				for (AID aid : getEUnitsAids()) {
					ACLMessage msg = new ACLMessage(
							CommunicationHelper.HOLONS_NEW_CALENDAR);
					msg.addReceiver(aid);
					try {
						msg.setContentObject(tmp.get(aid));
						send(msg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (eUnitsCount == 0)
					scheduleChanged();
			} else {
				createNewEUnit();
			}
		}
	}

	public synchronized void scheduleChanged() {
		eUnitsCount--;
		if (eUnitsCount <= 0) {
			calculateMeasure(oldSchedule, newSchedule);
		}
	}

	private boolean checkSTCondition() {
		if (timestamp >= nextSTTimestamp
				&& handeledCommissionsCount % STCommissionsionsGap == 0) {
			return true;
		}
		return false;
	}

	private boolean calendarsForMeasures = false;

	private void getCalendarsForMesure() {
		calendarsForMeasures = true;
		complexSimmulatedTrading(null);
	}

	private Map<AID, Schedule> oldSchedule;
	private Map<AID, Schedule> newSchedule;

	private Map<AID, Schedule> calculateMeasuresOldSchedule;
	private Map<AID, Schedule> calculateMeasuresNewSchedule;

	private void calculateMeasure(Map<AID, Schedule> oldSchedule,
			Map<AID, Schedule> newSchedule) {

		allMeasuresData.setTimestamp(timestamp);
		allMeasuresData.setSimInfo(simInfo);
		allMeasuresData.setCommissions(commissions);

		allMeasuresData.calculateMeasures(oldSchedule, newSchedule, this);

		calculateMeasuresOldSchedule = oldSchedule;
		calculateMeasuresNewSchedule = newSchedule;

		sendMeasuresToEUnits(allMeasuresData);

	}

	private void calculateMeasures() {
		Map<AID, Schedule> oldSchedule = calculateMeasuresOldSchedule;
		Map<AID, Schedule> newSchedule = calculateMeasuresNewSchedule;

		if (calculatorsHolder != null && timestamp >= nextMeasureTimestamp) {

			List<Measure> measures = new LinkedList<Measure>();

			Measure measure;

			calculatorsHolder.setTimestamp(timestamp);
			calculatorsHolder.setCommissions(commissions);

			if (measuresVisualizationRunner != null)
				measuresVisualizationRunner.setCurrentHolons(oldSchedule
						.keySet());

			for (MeasureCalculator measureCalc : calculatorsHolder
					.getCalculators()) {

				measure = measureCalc.calculateMeasure(oldSchedule,
						newSchedule, this);

				measure.setTimestamp(timestamp);
				measure.setComId(this.currentCommission.getID());

				measures.add(measure);

				if (measuresVisualizationRunner != null)
					measuresVisualizationRunner.update(measure,
							measureCalc.getName());
			}

			measureData.addMeasures(measures);

			if (isConfigurationChangeable) {
				changeConfiguration();
				return;
			}
		}

		if (mlAlgorithm != null) {
			mlAlgorithm.setTimestamp(timestamp);
			mlAlgorithm.setCommissions(commissions);

			Map<AID, Schedule> copyOfNewSchedule = null;
			if (newSchedule != null) {
				if (newSchedule.size() == 0)
					newSchedule = null;
				else {
					copyOfNewSchedule = new HashMap<AID, Schedule>();
					for (AID key : newSchedule.keySet()) {
						copyOfNewSchedule.put(key,
								Schedule.copy(newSchedule.get(key)));
					}
				}
			}
			Map<AID, Schedule> copyOfOldSchedule = new HashMap<AID, Schedule>();
			for (AID key : oldSchedule.keySet()) {
				copyOfOldSchedule.put(key, Schedule.copy(oldSchedule.get(key)));
			}

			GlobalConfiguration globalConf = null;
			if (oldSchedule.size() > 0) {
				globalConf = mlAlgorithm.getGlobalConfiguration(
						copyOfOldSchedule, copyOfNewSchedule, simInfo,
						simInfo.isExploration(), this);
			}
			Map<AID, HolonConfiguration> holonConfigurations = mlAlgorithm
					.getHolonsConfiguration(oldSchedule, newSchedule, simInfo,
							simInfo.isExploration(), this);
			changeConfiguration(globalConf, holonConfigurations);
			return;

		}

		continueAfterMeasureGathered();
	}

	private void sendMeasuresToEUnits(AllMeasuresData data) {
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"ExecutionUnitService");
		eUnitsCount = aids.length;
		ACLMessage msg = new ACLMessage(
				CommunicationHelper.MEASURES_TO_EUNIT_DATA);
		for (AID aid : aids)
			msg.addReceiver(aid);
		try {
			msg.setContentObject(data.getMeasures());
		} catch (IOException e) {
			e.printStackTrace();
		}
		send(msg);
	}

	private synchronized void continueAfterMeasureGathered() {
		if (calendarsForMeasures) {
			calendarsForMeasures = false;
			if (defaultEUnitCreation) {
				defaultEUnitCreation = false;
				if (simmulatedTradingCount == 0 || !checkSTCondition()) {
					if (commissionSendingType == false) {
						if (commissions.size() > 0)
							addCommission(commissions.remove(0));
						else {
							if (graphChanged) {
								graphChanged = false;
								ACLMessage response = new ACLMessage(
										CommunicationHelper.GRAPH_CHANGED);
								AID sender = CommunicationHelper
										.findAgentByServiceName(this,
												"GUIService")[0];
								response.addReceiver(sender);
								try {
									response.setContentObject(false);
								} catch (IOException e) {
									e.printStackTrace();
								}
								this.send(response);
							} else
								sendGUIMessage("NEXT_SIMSTEP");
						}
					} else {
						sendCommissionsToBestHolons();
					}
				}
			} else {
				if (simmulatedTradingCount != 0 && checkSTCondition()) {
					sendCommissionsToBestHolons();
				} else {
					if (commissionSendingType == false) {
						if (commissions.size() > 0)
							addCommission(commissions.remove(0));
						else {
							if (graphChanged) {
								graphChanged = false;
								ACLMessage response = new ACLMessage(
										CommunicationHelper.GRAPH_CHANGED);
								AID sender = CommunicationHelper
										.findAgentByServiceName(this,
												"GUIService")[0];
								response.addReceiver(sender);
								try {
									response.setContentObject(false);
								} catch (IOException e) {
									e.printStackTrace();
								}
								this.send(response);
							} else
								sendGUIMessage("NEXT_SIMSTEP");
						}
					} else {
						sendCommissionsToBestHolons();
					}
				}
			}
		} else {
			if (commissionSendingType == false) {
				if (commissions.size() > 0)
					addCommission(commissions.remove(0));
				else
					sendGUIMessage("NEXT_SIMSTEP");
			} else {
				sendCommissionsToBestHolons();
			}
		}
	}

	public synchronized MeasureData getMeasureData() {
		return measureData;
	}

	private void changeConfiguration() {
		confChanger.setMeasureData(measureData);

		GlobalConfiguration globalConf = confChanger
				.getNewGlobalConfiguration();
		Map<AID, HolonConfiguration> conf = confChanger
				.getNewHolonsConfigurations();

		changeConfiguration(globalConf, conf);

	}

	private void changeConfiguration(GlobalConfiguration globalConf,
			Map<AID, HolonConfiguration> conf) {

		if (globalConf != null) {
			if (globalConf.isType() != null)
				this.commissionSendingType = globalConf.isType();
			if (globalConf.isChoosingByCost() != null)
				this.choosingByCost = globalConf.isChoosingByCost();
			if (globalConf.getSimmulatedTrading() != null)
				this.simmulatedTradingCount = globalConf.getSimmulatedTrading();

			ExchangeAlgorithmsFactory factory = simInfo.getExchangeAlgFactory();
			if (factory.getAlgAfterComAdd().getClass()
					.equals(SimulatedTrading.class)) {
				if (globalConf.getChooseWorstCommission() != null) {
					this.chooseWorstCommission = globalConf
							.getChooseWorstCommission();
					factory.getAlgAfterComAdd()
							.getParameters()
							.put("chooseWorstCommission",
									this.chooseWorstCommission);
				}
			}
			if (factory.getAlgWhenCantAdd().getClass()
					.equals(SimulatedTrading.class)) {
				if (globalConf.getSTDepth() != null) {
					this.maxFullSTDepth = globalConf.getSTDepth();
					factory.getAlgAfterComAdd()
							.getParameters()
							.put("maxFullSTDepth",
									new Integer(this.maxFullSTDepth).toString());
				}
			}

		}

		if (conf != null) {
			ACLMessage msg;

			eUnitsCount = conf.keySet().size();
			for (AID aid : conf.keySet()) {
				msg = new ACLMessage(CommunicationHelper.CONFIGURATION_CHANGE);
				msg.addReceiver(aid);
				try {
					msg.setContentObject(conf.get(aid));
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				this.send(msg);
			}

			if (eUnitsCount == 0)
				continueAfterMeasureGathered();
		} else {
			continueAfterMeasureGathered();
		}
	}

	public synchronized void configurationChanged() {
		eUnitsCount--;
		if (eUnitsCount <= 0)
			continueAfterMeasureGathered();
	}

	public MLAlgorithm getMLAlgorithm() {
		return mlAlgorithm;
	}

	private boolean graphChanged = false;

	public synchronized void graphChanged(Graph graph, AID sender) {
		((GraphSchedule) this.simInfo.getScheduleCreator()).getTrackFinder()
				.setGraph(graph);

		if (simInfo.isSTAfterGraphChange()) {
			graphChanged = true;

			simmulatedTrading();
		} else {
			ACLMessage response = new ACLMessage(
					CommunicationHelper.GRAPH_CHANGED);
			response.addReceiver(sender);
			try {
				response.setContentObject(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.send(response);
		}
	}

	public synchronized void measuresSendToEUnitResponse() {
		eUnitsCount--;
		if (eUnitsCount == 0) {
			calculateMeasures();
		}
	}

	public void sendDataToCollector() {

		ACLMessage cfp = null;
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,"DataCollectorService");

		logger.info("DistributorAgent - sending agents data to Collector");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(CommunicationHelper.DATA_COLLECTION_FROM_DISTRIBUTOR_REPLY);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject(new DistributorCollectorMessage(this));
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				send(cfp);
				
			}
		}
		
		
	}

	public void sendTimestampToCollector(Integer time) {

		ACLMessage cfp = null;
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,"DataCollectorService");

		logger.info("DistributorAgent - sending TIMESTAMP to Collector");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(CommunicationHelper.DATA_COLLECTION_FROM_DISTRIBUTOR_TIMESTAMP);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject(time);
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				send(cfp);
				
			}
		}
		
	}
}
