package dtp.jade.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import machineLearning.MLAlgorithm;
import measure.MeasureCalculatorsHolder;
import measure.printer.PrintersHolder;
import adapter.Adapter;
import algorithm.Algorithm;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import algorithm.comparator.CommissionsComparator;
import dtp.graph.Graph;
import dtp.graph.GraphChangesConfiguration;
import dtp.graph.predictor.GraphLinkPredictor;
import dtp.jade.crisismanager.crisisevents.CrisisEvent;
import dtp.jade.dataCollector.agent.DataCollectionConf;
import dtp.optimization.TrackFinder;
import dtp.xml.ParseException;

public class TestConfiguration {

	public static enum GraphType {
		NONE, RANDOM, NEIGHBOURS
	};

	private String adapterName;
	private Adapter adapter;
	private String commisions;
	private String configurationDirectory;
	private String results;
	private List<CrisisEvent> events;

	private boolean packageSending;
	private boolean choosingByCost;
	private int simmulatedTrading;
	private int STDepth;
	private DefaultAgentsData defaultAgentsData;
	private Algorithm algorithm;
	private boolean dist;
	private boolean autoConfigure;
	private boolean recording;
	private int STTimeGap;
	private int STComissionGap;
	private MeasureCalculatorsHolder calculatorsHolder;
	private PrintersHolder printersHolder;
	private boolean confChange;
	private String punishmentFunction;
	private Map<String, Double> defaultPunishmentFunValues;
	private Double delayLimit;
	private int holons;
	private String worstComissionChoose;
	private boolean firstComplexSTResultOnly;
	private TrackFinder trackFinder;
	private GraphLinkPredictor graphLinkPredictor;

	private boolean exploration;
	private String mlTableFileName;
	private Map<String, String> MLTableParams;
	private String mlAlgName;
	private GraphChangesConfiguration graphChangesConf;

	private MLAlgorithm mlAlgorithm;

	private Map<String, String> algorithmAgentsConfig = new HashMap<String, String>();

	private boolean STAfterGraphChange;

	private CommissionsComparator commissionsComparator;
	
	private DataCollectionConf dataCollectionConf;
	
	public MLAlgorithm getMlAlgorithm() {
		return mlAlgorithm;
	}

	public void setMlAlgorithm(MLAlgorithm mlAlgorithm) {
		this.mlAlgorithm = mlAlgorithm;
	}
	private ExchangeAlgorithmsFactory exchangeAlgFactory;

	private String graphChangeTime;

	private int graphChangeFreq;
	private boolean gui;

	public String getMlAlgName() {
		return mlAlgName;
	}

	public void setMlAlgName(String mlAlgName) {
		this.mlAlgName = mlAlgName;
	}

	public int getGraphChangeFreq() {
		return graphChangeFreq;
	}

	public void setGraphChangeFreq(int graphChangeFreq) {
		this.graphChangeFreq = graphChangeFreq;
	}

	public String getGraphChangeTime() {
		return graphChangeTime;
	}

	public void setGraphChangeTime(String graphChangeTime) {
		this.graphChangeTime = graphChangeTime;
	}

	public ExchangeAlgorithmsFactory getExchangeAlgFactory() {
		return exchangeAlgFactory;
	}

	public void setExchangeAlgFactory(
			ExchangeAlgorithmsFactory exchangeAlgFactory) {
		this.exchangeAlgFactory = exchangeAlgFactory;
	}

	public boolean isSTAfterGraphChange() {
		return STAfterGraphChange;
	}

	public void setSTAfterGraphChange(boolean sTAfterGraphChange) {
		STAfterGraphChange = sTAfterGraphChange;
	}

	public GraphChangesConfiguration getGraphChangesConf() {
		return graphChangesConf;
	}

	public void setGraphChangesConf(GraphChangesConfiguration graphChangesConf) {
		this.graphChangesConf = graphChangesConf;
	}

	public TrackFinder getTrackFinder() {
		return trackFinder;
	}

	public GraphLinkPredictor getGraphLinkPredictor() {
		return graphLinkPredictor;
	}

	public void setGraph(Graph graph, String trackFinder, String predictor,
			int historySize) throws ParseException {
		try {
			Class<?> clazz = Class.forName("dtp.optimization." + trackFinder);
			TrackFinder finder = (TrackFinder) clazz
					.getConstructor(Graph.class).newInstance(graph);
			this.trackFinder = finder;

			clazz = Class.forName("dtp.graph.predictor." + predictor
					+ "GraphLinkPredictor");
			this.graphLinkPredictor = (GraphLinkPredictor) clazz.newInstance();
			this.graphLinkPredictor.setHistoryMaxSize(historySize);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage());
		}
	}

	public String getMlTableFileName() {
		return mlTableFileName;
	}

	public Map<String, String> getMLTableParams() {
		return MLTableParams;
	}

	public void setMLTableParams(Map<String, String> defaultMLTableParams) {
		this.MLTableParams = defaultMLTableParams;
	}

	public void setMlTableFileName(String mlTableFileName) {
		if (confChange == true)
			throw new IllegalStateException(
					"confChange cannot be true if you want use machine learning");
		this.mlTableFileName = mlTableFileName;
	}

	public boolean isExploration() {
		return exploration;
	}

	public void setExploration(boolean exploration) {
		this.exploration = exploration;
	}

	public boolean isFirstComplexSTResultOnly() {
		return firstComplexSTResultOnly;
	}

	public void setFirstComplexSTResultOnly(boolean firstComplexSTResultOnly) {
		this.firstComplexSTResultOnly = firstComplexSTResultOnly;
	}

	public String getWorstComissionChoose() {
		return worstComissionChoose;
	}

	public void setWorstComissionChoose(String worstComissionChoose) {
		this.worstComissionChoose = worstComissionChoose;
	}

	public int getHolons() {
		return holons;
	}

	public void setHolons(int holons) {
		this.holons = holons;
	}

	public Double getDelayLimit() {
		return delayLimit;
	}

	public void setDelayLimit(Double delayLimit) {
		this.delayLimit = delayLimit;
	}

	public Map<String, Double> getDefaultPunishmentFunValues() {
		return defaultPunishmentFunValues;
	}

	public void setDefaultPunishmentFunValues(
			Map<String, Double> defaultPunishmentFunValues) {
		this.defaultPunishmentFunValues = defaultPunishmentFunValues;
	}

	public String getPunishmentFunction() {
		return punishmentFunction;
	}

	public void setPunishmentFunction(String punishmentFunction) {
		this.punishmentFunction = punishmentFunction;
	}

	public int getSTTimeGap() {
		return STTimeGap;
	}

	public boolean isConfChange() {
		return confChange;
	}

	public void setConfChange(boolean confChange) {
		this.confChange = confChange;
	}

	public void setSTTimeGap(int sTTimeGap) {
		STTimeGap = sTTimeGap;
	}

	public int getSTComissionGap() {
		return STComissionGap;
	}

	public void setSTComissionGap(int sTComissionGap) {
		STComissionGap = sTComissionGap;
	}

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	public boolean isAutoConfigure() {
		return autoConfigure;
	}

	public void setAutoConfigure(boolean autoConfigure) {
		this.autoConfigure = autoConfigure;
	}

	public boolean isDist() {
		return dist;
	}

	public void setDist(boolean isDist) {
		dist = isDist;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	@SuppressWarnings("rawtypes")
	public void setAlgorithm(String algorithmName) {
		try {
			Class algorithmClass = Class.forName("algorithm." + algorithmName);
			this.algorithm = (Algorithm) algorithmClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public int getSTDepth() {
		return STDepth;
	}

	public void setSTDepth(int sTDepth) {
		STDepth = sTDepth;
	}

	public int getSimmulatedTrading() {
		return simmulatedTrading;
	}

	public void setSimmulatedTrading(int simmulatedTrading) {
		this.simmulatedTrading = simmulatedTrading;
	}

	public DefaultAgentsData getDefaultAgentsData() {
		return defaultAgentsData;
	}

	public void setDefaultAgentsData(DefaultAgentsData defaultAgentsData) {
		this.defaultAgentsData = defaultAgentsData;
	}

	public boolean isChoosingByCost() {
		return choosingByCost;
	}

	public void setChoosingByCost(boolean choosingByCost) {
		this.choosingByCost = choosingByCost;
	}

	public boolean isPackageSending() {
		return packageSending;
	}

	public void setPackageSending(boolean packageSending) {
		this.packageSending = packageSending;
	}

	/**
	 * @param dynamic
	 *            the dynamic to set
	 */
	public void setAdapter(String adapter) {
		adapterName = adapter;
	}

	/**
	 * @return true if test is dynamic
	 */
	public boolean isDynamic() {
		if (adapterName.equals("false"))
			return false;
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Adapter getAdapter() {
		if (adapterName.equals("false") || adapterName.equals("true"))
			this.adapter = null;
		else {
			try {
				Class c = Class.forName("adapter." + adapterName);
				this.adapter = (Adapter) c.getConstructor(String.class)
						.newInstance(getCommisions());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return adapter;
	}

	/**
	 * @param commisions
	 *            the name of commissions configuration file to set
	 */
	public void setCommisions(String commisions) {
		this.commisions = commisions;
	}

	/**
	 * @return the name of file containing commissions configuration
	 */
	public String getCommisions() {
		return commisions;
	}

	/**
	 * @param configurationDirectory
	 *            location of configuration directory to set
	 */
	public void setConfigurationDirectory(String configurationDirectory) {
		this.configurationDirectory = configurationDirectory;
	}

	/**
	 * @return location of configuration directory
	 */
	public String getConfigurationDirectory() {
		return configurationDirectory;
	}

	/**
	 * @param results
	 *            the location of test results
	 */
	public void setResults(String results) {
		this.results = results;
	}

	/**
	 * @return name of file to store results to
	 */
	public String getResults() {
		return results;
	}

	/**
	 * @param events
	 *            the list of crisis events to set
	 */
	public void setEvents(List<CrisisEvent> events) {
		this.events = events;
	}

	/**
	 * @return the list of crisis events
	 */
	public List<CrisisEvent> getEvents() {
		return events;
	}

	public MeasureCalculatorsHolder getCalculatorsHolder() {
		return calculatorsHolder;
	}

	public void setCalculatorsHolder(MeasureCalculatorsHolder calculatorsHolder) {
		this.calculatorsHolder = calculatorsHolder;
	}

	public PrintersHolder getPrintersHolder() {
		return printersHolder;
	}

	public void setPrintersHolder(PrintersHolder printersHolder) {
		this.printersHolder = printersHolder;
	}

	public Map<String, String> getAlgorithmAgentsConfig() {
		return algorithmAgentsConfig;
	}

	public void setAlgorithmAgentsConfig(
			Map<String, String> algorithmAgentsConfig) {
		this.algorithmAgentsConfig = algorithmAgentsConfig;
	}

	public CommissionsComparator getCommissionsComparator() {
		return commissionsComparator;
	}

	@SuppressWarnings("rawtypes")
	public void setCommissionsComparator(String name) {
		try {
			Class compClass = Class.forName("algorithm.comparator." + name
					+ "CommissionsComparator");
			this.commissionsComparator = (CommissionsComparator) compClass
					.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void setGUI(boolean gui) {
		this.gui = gui;
	}
	
	public boolean getGUI(){
		return gui;
	}
	public void setDataCollectionConf(DataCollectionConf dataCollectionConf){
		this.dataCollectionConf = dataCollectionConf;
	}
	public DataCollectionConf getDataCollectionConf(){
		return this.dataCollectionConf;
	}
}
