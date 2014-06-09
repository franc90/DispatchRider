package dtp.xml;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import machineLearning.MLAlgorithm;
import machineLearning.MLAlgorithmFactory;
import machineLearning.qlearning.QLearning;
import measure.MeasureCalculatorsHolder;
import measure.printer.PrintersHolder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import algorithm.STLike.ExchangeAlgorithmsFactory;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.jade.crisismanager.crisisevents.CommissionDelayEvent;
import dtp.jade.crisismanager.crisisevents.CommissionWithdrawalEvent;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import dtp.jade.crisismanager.crisisevents.EUnitFailureEvent;
import dtp.jade.crisismanager.crisisevents.RoadTrafficExclusionEvent;
import dtp.jade.crisismanager.crisisevents.TrafficJamEvent;
import dtp.jade.dataCollector.agent.DataCollectionConf;
import dtp.jade.test.DefaultAgentsData;
import dtp.jade.test.TestConfiguration;

public class ConfigurationParser {

	public static class SimpleErrorHandler implements ErrorHandler {

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;

		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			throw e;
		}
	}

	/**
	 * Converts XML boolean attribute to Java boolean type
	 * 
	 * @param value
	 *            represent boolean attribute as returned by
	 *            org.w3c.dom.Element.getAttrbute()
	 * @return true if value is "true" or "1"
	 */
	private static boolean stringToBoolean(String value) {
		if ("true".equals(value))
			return true;
		else if ("1".equals(value))
			return true;
		else
			return false;
	}

	private static int attributeToInt(Node node, String attribute)
			throws ParseException {
		Node attr = node.getAttributes().getNamedItem(attribute);
		if (attr == null)
			throw new ParseException("No such attribute " + attribute);
		return Integer.valueOf(attr.getTextContent());
	}

	private static double attributeToDobule(Node node, String attribute)
			throws ParseException {
		Node attr = node.getAttributes().getNamedItem(attribute);
		if (attr == null)
			throw new ParseException("No such attribute " + attribute);
		return Double.valueOf(attr.getTextContent());
	}

	private static TestConfiguration parseTest(Element element)
			throws ParseException {
		TestConfiguration configuration = new TestConfiguration();
		
		// Added while merging with GUI:
		configuration.setGUI(stringToBoolean(element.getAttribute("gui")));

		Element commissions = (Element) element.getElementsByTagName(
				"commissions").item(0);
		if (element.getElementsByTagName("algorithmAgentsConfig").getLength() > 0) {
			Element algorithmAgentsConf = (Element) element
					.getElementsByTagName("algorithmAgentsConfig").item(0);

			parseAlgorithmAgentsConf(configuration, algorithmAgentsConf);

		}

		Element agents = (Element) element
				.getElementsByTagName("defaultAgents").item(0);
		Element results = (Element) element.getElementsByTagName("results")
				.item(0);
		Element configDir = (Element) element.getElementsByTagName(
				"configuration").item(0);
		Element events = (Element) element.getElementsByTagName("events").item(
				0);
		Element graph = (Element) element.getElementsByTagName("roadGraph")
				.item(0);

		Element measures = (Element) element.getElementsByTagName("measures")
				.item(0);

		Element punishment = (Element) element.getElementsByTagName(
				"punishment").item(0);

		Element ml = (Element) element.getElementsByTagName("machineLearning")
				.item(0);

		Element mlAlgorithm = (Element) element.getElementsByTagName(
				"mlAlgorithm").item(0);
		
		Element dataCollection = (Element) element.getElementsByTagName(
				"dataCollection").item(0);

		if (graph != null) {
			String graphFile = graph.getTextContent();
			Graph graphObj = new GraphParser().parse(graphFile.trim());
			String trackFinder = graph.getAttribute("trackFinder");
			String predictor = graph.getAttribute("predictor");
			String historySize = graph.getAttribute("historySize");
			boolean graphST = stringToBoolean(graph.getAttribute("ST"));
			configuration.setSTAfterGraphChange(graphST);
			configuration.setGraph(graphObj, trackFinder, predictor,
					Integer.parseInt(historySize));
			String graphChanges = graph.getAttribute("graphChanges");
			String changeTime = graph.getAttribute("changeTime");
			String notificationTime = graph.getAttribute("notificationTime");
			configuration.setGraphChangeTime(changeTime);
			if (notificationTime != null && notificationTime.length() > 0) {
				configuration.setGraphChangeFreq(Integer
						.parseInt(notificationTime));
			}
			if (graphChanges != null && graphChanges.length() > 0) {
				GraphChangesConfiguration graphConf = GraphChangesParser
						.parse(graphChanges);
				graphConf.setGraph(graphObj);
				configuration.setGraphChangesConf(graphConf);
			}
		}

		if (measures != null) {
			configuration = parseMeasures(measures, configuration);
		}

		if (punishment != null) {
			configuration = parsePunishment(punishment, configuration);
		}

		if (ml != null) {
			configuration = parseMachineLearningPart(configuration, ml);
		}

		if (mlAlgorithm != null) {
			configuration = parseMLAlgorithmPart(configuration, mlAlgorithm);
		}

		if (dataCollection != null){
			configuration = parseDataCollectionPart(configuration, dataCollection);
		}
		
		configuration.setCommisions(commissions.getTextContent());
		configuration.setCommissionsComparator(commissions
				.getAttribute("commissionsComparator"));
		configuration.setConfChange(stringToBoolean(commissions
				.getAttribute("confChange")));
		configuration.setAutoConfigure(stringToBoolean(commissions
				.getAttribute("autoConfig")));
		configuration.setRecording(stringToBoolean(commissions
				.getAttribute("recording")));
		configuration.setSTTimeGap(Integer.parseInt(commissions
				.getAttribute("STTimeGap")));
		configuration.setSTComissionGap(Integer.parseInt(commissions
				.getAttribute("STCommissionGap")));
		
		configuration.setAdapter(commissions.getAttribute("dynamic"));
		configuration
				.setDist(stringToBoolean(commissions.getAttribute("dist")));
		// only to mantain compatibility with older configurations
		String worstCommissionChoose = commissions
				.getAttribute("worstCommissionByGlobalTime");
		if (worstCommissionChoose != null) {
			boolean time = stringToBoolean(worstCommissionChoose);
			if (time) {
				configuration.setWorstComissionChoose("time");
			} else {
				configuration.setWorstComissionChoose("wTime");
			}
		} else {
			configuration.setWorstComissionChoose(commissions
					.getAttribute("chooseWorstCommission"));
		}
		configuration.setAlgorithm(commissions.getAttribute("algorithm"));
		configuration.setPackageSending(stringToBoolean(commissions
				.getAttribute("packageSending")));
		configuration.setChoosingByCost(stringToBoolean(commissions
				.getAttribute("choosingByCost")));
		configuration.setSimmulatedTrading(Integer.parseInt(commissions
				.getAttribute("simmulatedTrading")));
		int STDepth = Integer.parseInt(commissions.getAttribute("STDepth"));
		if (STDepth == 0) {
			System.err.println("STDepth nie moze byc rowne 0");
			System.exit(0);
		}
		configuration.setSTDepth(STDepth);
		configuration.setFirstComplexSTResultOnly(stringToBoolean(commissions
				.getAttribute("firstComplexSTResultOnly")));
		configuration.setDefaultAgentsData(parseAgents(agents));
		configuration.setConfigurationDirectory(configDir.getTextContent());
		configuration.setResults(results.getTextContent());
		configuration.setEvents(parseEvents(events));

		configuration = parseExchangeAlgorithms(configuration, element);

		return configuration;
	}
	/**
	 * Can be added new Agents configurations without any changes in this method
	 */
	private static TestConfiguration parseDataCollectionPart(
			TestConfiguration configuration, Element dataCollection) throws ParseException {	
		if(dataCollection == null) return null;
		DataCollectionConf conf;
		String str;
		int timeGap = -1, timeGapPeriod = -1;
		boolean timestsync = dataCollection.getAttribute("timestampSync").equals("true"); 
		str = dataCollection.getAttribute("timeGap");
		if (!str.equals("")) timeGap = Integer.parseInt(str);
		str = dataCollection.getAttribute("timeGapPeriod");
		if (!str.equals("")) timeGapPeriod = Integer.parseInt(str);
		conf = new DataCollectionConf(stringToBoolean(dataCollection.getAttribute("serialize")), 
				timestsync ,  timeGap, timeGapPeriod);
		NodeList nodes = dataCollection.getChildNodes();
		Node node;
		for (int i = 0; i < nodes.getLength(); ++i){
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				/*	node naming:
				 *		eunit - configuration for EUnit Agents
				 *		infoAgent - configuration for InfoAgent
				 *		distributorAgent - configuration for DistributorAgent
				 */
				int agentTimeGap, agentTimeGapPeriod;
				try {
					agentTimeGap = attributeToInt(node, "timeGap");
				} catch( ParseException e ) {
					agentTimeGap = -1;
				}
				try {
					agentTimeGapPeriod = attributeToInt(node, "timeGapPeriod");
				} catch ( ParseException e ) {
					agentTimeGapPeriod = -1;
				}
				conf.addAgentConfig(node.getNodeName(), agentTimeGap, agentTimeGapPeriod);
			}
		}
		configuration.setDataCollectionConf(conf);
		return configuration;
	}

	private static DefaultAgentsData parseAgents(Element element)
			throws ParseException {
		if (element == null)
			return null;

		int power = -1;
		int reliability = -1;
		int comfort = -1;
		int fuelConsumption = -1;
		int mass = -1;
		int capacity = -1;
		int cargoType = -1;
		int universality = -1;

		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String name = node.getNodeName();
				if ("truck".equals(name)) {
					power = attributeToInt(node, "power");
					reliability = attributeToInt(node, "reliability");
					comfort = attributeToInt(node, "comfort");
					fuelConsumption = attributeToInt(node, "fuelConsumption");
				} else if ("trailer".equals(name)) {
					mass = attributeToInt(node, "mass");
					capacity = attributeToInt(node, "capacity");
					cargoType = attributeToInt(node, "cargoType");
					universality = attributeToInt(node, "universality");
				} else {
					throw new ParseException("Unrecognized event " + name);
				}
			}
		}
		return new DefaultAgentsData(power, reliability, comfort,
				fuelConsumption, mass, capacity, cargoType, universality);
	}

	private static List<CrisisEvent> parseEvents(Element element)
			throws ParseException {
		List<CrisisEvent> events = new LinkedList<CrisisEvent>();

		if (element == null)
			return events;

		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			CrisisEvent event;
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String name = node.getNodeName();
				if ("commissionWithdrawal".equals(name))
					event = parseCommisionWithdrawal(node);
				else if ("commissionDelay".equals(name)) {
					event = parseCommisionDelay(node);
				} else if ("vehicleFailure".equals(name)) {
					event = parseVehicleFailure(node);
				} else if ("trafficJam".equals(name)) {
					event = parseTrafficJamEvent(node);
				} else if ("roadTrafficExclusion".equals(name)) {
					event = parseRoadTrafficExclusionEvent(node);
				} else {
					throw new ParseException("Unrecognized event " + name);
				}
				event.setEventID(events.size());
				events.add(event);
			}
		}

		return events;
	}

	private static CommissionWithdrawalEvent parseCommisionWithdrawal(Node node)
			throws ParseException {
		CommissionWithdrawalEvent event = new CommissionWithdrawalEvent();
		event.setEventTime(attributeToInt(node, "time"));
		event.setCommissionID(attributeToInt(node, "commission"));
		return event;
	}

	private static CommissionDelayEvent parseCommisionDelay(Node node)
			throws ParseException {
		CommissionDelayEvent event = new CommissionDelayEvent();
		event.setEventTime(attributeToInt(node, "time"));
		event.setCommissionID(attributeToInt(node, "commission"));
		event.setDelay(attributeToInt(node, "delay"));
		return event;
	}

	private static EUnitFailureEvent parseVehicleFailure(Node node)
			throws ParseException {
		EUnitFailureEvent event = new EUnitFailureEvent();
		event.setEventTime(attributeToInt(node, "time"));
		event.setEUnitID(attributeToInt(node, "vehicle"));
		event.setFailureDuration(attributeToDobule(node, "duration"));
		return event;
	}

	private static TrafficJamEvent parseTrafficJamEvent(Node node)
			throws ParseException {
		TrafficJamEvent event = new TrafficJamEvent();
		event.setEventTime(attributeToInt(node, "time"));
		event.setStartPoint(new Point2D.Double(
				attributeToDobule(node, "startX"), attributeToDobule(node,
						"startY")));
		event.setEndPoint(new Point2D.Double(attributeToDobule(node, "endX"),
				attributeToDobule(node, "endY")));
		event.setJamCost(attributeToDobule(node, "cost"));
		return event;
	}

	private static RoadTrafficExclusionEvent parseRoadTrafficExclusionEvent(
			Node node) throws ParseException {
		RoadTrafficExclusionEvent event = new RoadTrafficExclusionEvent();
		event.setEventTime(attributeToInt(node, "time"));
		event.setStartPoint(new Point2D.Double(
				attributeToDobule(node, "startX"), attributeToDobule(node,
						"startY")));
		event.setEndPoint(new Point2D.Double(attributeToDobule(node, "endX"),
				attributeToDobule(node, "endY")));
		return event;
	}

	/**
	 * 
	 * @param filename
	 *            name of XML file to read
	 * @return new TestConfiguration representing contents of given file
	 * @throws ParseException
	 */
	public static List<TestConfiguration> parse(String filename)
			throws ParseException {
		List<TestConfiguration> tests = new LinkedList<TestConfiguration>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);

			/* Use embedded location of XML schema to validate document */
			factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new SimpleErrorHandler());
			Document document = builder.parse(filename);

			NodeList nodes = document.getElementsByTagName("test");
			
			for (int i = 0; i < nodes.getLength(); ++i) {
				tests.add(parseTest((Element) nodes.item(i)));
			}
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}

		return tests;
	}

	public static TestConfiguration parseMeasures(Element measures,
			TestConfiguration conf) {
		String formats[] = measures.getAttribute("formats").split(" ");
		PrintersHolder printersHolder = new PrintersHolder();

		for (String extension : formats)
			printersHolder.addPrinter(extension);

		MeasureCalculatorsHolder calculatorsHolder = new MeasureCalculatorsHolder();
		calculatorsHolder.setTimeGap(Integer.parseInt(measures
				.getAttribute("timeGap")));

		NodeList measureList = measures.getElementsByTagName("measure");
		Element measure;
		boolean visualize;
		for (int i = 0; i < measureList.getLength(); i++) {
			measure = (Element) measureList.item(i);
			calculatorsHolder.addCalculator(measure.getTextContent());
			visualize = stringToBoolean(measure.getAttribute("visualize"));
			if (visualize) {
				calculatorsHolder.addVisualizationMeasuresNames(measure
						.getTextContent());
			}
		}

		conf.setCalculatorsHolder(calculatorsHolder);
		conf.setPrintersHolder(printersHolder);

		return conf;
	}

	public static TestConfiguration parsePunishment(Element punishment,
			TestConfiguration conf) {
		String fun = punishment.getAttribute("function");
		int holons = Integer.parseInt(punishment.getAttribute("holons"));
		conf.setHolons(holons);
		String defaultValues = punishment.getAttribute("default");
		String delayLimit = punishment.getAttribute("delayLimit");
		if (delayLimit != null && delayLimit.length() > 0)
			conf.setDelayLimit(new Double(delayLimit));
		Map<String, Double> defaults = new HashMap<String, Double>();
		String[] parts;
		try {
			if (defaultValues != null && defaultValues.length() > 0)
				for (String value : defaultValues.split(";")) {
					parts = value.split("=");
					if (parts.length != 2)
						throw new IllegalArgumentException();
					defaults.put(parts[0].trim(), new Double(parts[1].trim()));
				}
			conf.setPunishmentFunction(fun);
			conf.setDefaultPunishmentFunValues(defaults);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return conf;
	}

	private static TestConfiguration parseMachineLearningPart(
			TestConfiguration conf, Element ml) throws ParseException {
		boolean exploration = stringToBoolean(ml.getAttribute("exploration"));

		String path = ml.getTextContent();
		conf.setMlTableFileName(path);
		String params = ml.getAttribute("params");

		QLearning alg = new QLearning();
		alg.init(path);

		if (params != null && params.length() > 0) {
			String[] parts = params.split(";");
			String[] paramsParts;
			Map<String, Double> paramsMap = new HashMap<String, Double>();

			for (String param : parts) {
				paramsParts = param.trim().split("=");
				paramsMap.put(paramsParts[0], new Double(paramsParts[1]));
			}


			alg.setDefaultParams(paramsMap);
		}


		conf.setMlAlgorithm(alg);

		conf.setExploration(exploration);
		return conf;
	}

	private static TestConfiguration parseMLAlgorithmPart(
			TestConfiguration conf, Element ml) throws ParseException {

		boolean exploration = stringToBoolean(ml.getAttribute("exploration"));
		String algName = ml.getAttribute("algorithm");

		String path = ml.getAttribute("file");
		conf.setMlTableFileName(path);

		NodeList params = ml.getElementsByTagName("param");
		Element param;
		Map<String, String> parameters = new HashMap<String, String>();
		for (int i = 0; i < params.getLength(); i++) {
			param = (Element) params.item(i);
			parameters.put(param.getAttribute("name"),
					param.getAttribute("value"));
		}

		MLAlgorithm alg = MLAlgorithmFactory.createAlgorithm(algName);
		alg.init(path);
		alg.setAlgorithmParameters(parameters);

		conf.setMlAlgorithm(alg);

		conf.setMlAlgName(algName);
		conf.setMLTableParams(parameters);

		conf.setExploration(exploration);
		return conf;
	}

	@SuppressWarnings("unchecked")
	private static TestConfiguration parseExchangeAlgorithms(
			TestConfiguration conf, Element root) {

		ExchangeAlgorithmsFactory factory = new ExchangeAlgorithmsFactory();
		if (conf.getSimmulatedTrading() > 0) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("chooseWorstCommission", conf.getWorstComissionChoose());
			factory.setAlgAfterComAdd("SimulatedTrading", params);
			params = new HashMap<String, String>();
			params.put("maxFullSTDepth",
					new Integer(conf.getSTDepth()).toString());
			params.put("firstComplexSTResultOnly",
					new Boolean(conf.isFirstComplexSTResultOnly()).toString());
			factory.setAlgWhenCantAdd("SimulatedTrading", params);
		}

		NodeList list = root
				.getElementsByTagName("exchangeAlgorithmAfterComAdd");
		Element algEl;
		Object[] params;

		if (list != null && list.getLength() > 0) {
			algEl = (Element) list.item(0);
			params = parseExchangeAlgParams(algEl);
			factory.setAlgAfterComAdd((String) params[0],
					(Map<String, String>) params[1]);
		}
		list = root.getElementsByTagName("exchangeAlgorithmWhenCantAddCom");
		if (list != null && list.getLength() > 0) {
			algEl = (Element) list.item(0);
			params = parseExchangeAlgParams(algEl);
			factory.setAlgWhenCantAdd((String) params[0],
					(Map<String, String>) params[1]);
		}
		conf.setExchangeAlgFactory(factory);
		return conf;
	}

	private static Object[] parseExchangeAlgParams(Element algEl) {

		String name = algEl.getAttribute("name");
		Map<String, String> params = new HashMap<String, String>();
		NodeList list = algEl.getElementsByTagName("param");
		String paramName;
		String paramValue;
		Element paramEl;
		if (list != null)
			for (int i = 0; i < list.getLength(); i++) {
				paramEl = (Element) list.item(i);
				paramName = paramEl.getAttribute("name");
				paramValue = paramEl.getAttribute("value");
				params.put(paramName, paramValue);
			}

		return new Object[] { name, params };
	}

	private static void parseAlgorithmAgentsConf(
			TestConfiguration configuration, Element algorithmAgentsConf) {
		Map<String, String> params = new HashMap<String, String>();
		NodeList list = algorithmAgentsConf.getElementsByTagName("param");
		String paramName;
		String paramValue;
		Element paramEl;
		if (list != null)
			for (int i = 0; i < list.getLength(); i++) {
				paramEl = (Element) list.item(i);
				paramName = paramEl.getAttribute("name");
				paramValue = paramEl.getAttribute("value");
				params.put(paramName, paramValue);
			}
		configuration.setAlgorithmAgentsConfig(params);
		
	}

	public static void main(String args[]) throws Exception {

		List<TestConfiguration> lists = ConfigurationParser
				.parse("configuration.xml");
		for (TestConfiguration conf : lists) {
			System.out.println("Test:");
			System.out.format(" Commisions: %s%n", conf.getCommisions());
			System.out.format(" Dynamic: %b%n", conf.isDynamic());
			System.out.format(" Results: %s%n", conf.getResults());
			System.out
					.format(" Config: %s%n", conf.getConfigurationDirectory());
			System.out.format(" Truck Power: %d%n", conf.getDefaultAgentsData()
					.getPower());
			System.out.println(" Events:");
			for (CrisisEvent event : conf.getEvents())
				System.out.format("\t%s%n", event.toString());
		}
	}
}
