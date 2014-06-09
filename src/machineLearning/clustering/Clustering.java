package machineLearning.clustering;

import jade.core.AID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import machineLearning.MLAlgorithm;
import machineLearning.xml.ClusTableStructureParser;
import machineLearning.xml.ClusTableToXMLWriter;
import measure.Measure;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;

import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;

import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;
import dtp.xml.ParseException;

public class Clustering extends MLAlgorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5292306365307759129L;

	private boolean learning;
	private boolean useTrees;
	private boolean overwriteConf;
	private String minClusCount;
	private String maxClusCount;
	private boolean usePam;

	private String schema;
	private ClusTableGlobalMeasures globalMeasures;
	private ClusTableHolonMeasures holonMeasures;
	private ClusTableStates globalStates;
	private ClusTableStates holonStates;
	private final ClusTableActions<GlobalConfiguration> globalActions = new ClusTableGlobalActions();
	private final ClusTableActions<HolonConfiguration> holonActions = new ClusTableHolonActions();

	private ClusTableObservations globalObservations = new ClusTableObservations();
	private ClusTableObservations holonObservations = new ClusTableObservations();

	private RewardFunction globalRewardFunction;
	private double globalFactor;
	private RewardFunction holonRewardFunction;
	private double holonsFactor;
	private boolean globalDeterministic = true;
	private boolean holonDeterministic = true;
	private Map<String, Double> defaultParams;

	List<Map<String, Measure>> measurmentsHistory = new LinkedList<Map<String, Measure>>();

	private static final Logger log = Logger.getLogger(Clustering.class);
	private final ClusLogger logger = new ClusLogger();

	public Clustering() {
		log.info("Clustering initialization");
		System.out
				.println("---------------------------------Clustering initialization");
		logger.init();
	}

	public boolean isLearning() {
		return learning;
	}

	public void setLearning(boolean learning) {
		this.learning = learning;
	}

	public boolean isUseTrees() {
		return useTrees;
	}

	public void setUseTrees(boolean useTrees) {
		this.useTrees = useTrees;
	}

	public boolean isOverwriteConf() {
		return overwriteConf;
	}

	public void setOverwriteConf(boolean overwriteConf) {
		this.overwriteConf = overwriteConf;
	}

	public String getMinClusCount() {
		return minClusCount;
	}

	public void setMinClusCount(String minClusCount) {
		this.minClusCount = minClusCount;
	}

	public String getMaxClusCount() {
		return maxClusCount;
	}

	public void setMaxClusCount(String maxClusCount) {
		this.maxClusCount = maxClusCount;
	}

	public boolean isUsePam() {
		return usePam;
	}

	public void setUsePam(boolean usePam) {
		this.usePam = usePam;
	}

	public void setDefaultParams(Map<String, Double> defaultParams) {
		this.defaultParams = defaultParams;
	}

	public void setGlobalDeterministic(boolean globalDeterministic) {
		this.globalDeterministic = globalDeterministic;
	}

	public void setHolonDeterministic(boolean holonDeterministic) {
		this.holonDeterministic = holonDeterministic;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public double getGlobalFactor() {
		return globalFactor;
	}

	public void setGlobalFactor(double globalFactor) {
		this.globalFactor = globalFactor;
	}

	public double getHolonsFactor() {
		return holonsFactor;
	}

	public void setHolonsFactor(double holonsFactor) {
		this.holonsFactor = holonsFactor;
	}

	public ClusTableGlobalMeasures getGlobalMeasures() {
		return globalMeasures;
	}

	public ClusTableHolonMeasures getHolonMeasures() {
		return holonMeasures;
	}

	public void setGlobalMeasures(ClusTableGlobalMeasures globalMeasures) {
		this.globalMeasures = globalMeasures;
	}

	public void setHolonMeasures(ClusTableHolonMeasures holonMeasures) {
		this.holonMeasures = holonMeasures;
	}

	public void setGlobalStates(ClusTableStates globalStates) {
		this.globalStates = globalStates;
	}

	public void setHolonStates(ClusTableStates holonStates) {
		this.holonStates = holonStates;
	}

	public void setGlobalActionsFunction(String globalActionsFunction) {
		this.globalRewardFunction = new GlobalRewardFunction(
				globalActionsFunction);
	}

	public void setHolonActionsFunction(String holonActionsFunction) {
		this.holonRewardFunction = new HolonRewardFunction(holonActionsFunction);
	}

	public void addGlobalAction(String name, GlobalConfiguration value) {
		globalActions.addAction(name, value);
	}

	public void addHolonAction(String name, HolonConfiguration value) {
		holonActions.addAction(name, value);
	}

	public ClusTableStates getGlobalStates() {
		return globalStates;
	}

	public ClusTableStates getHolonStates() {
		return holonStates;
	}

	public ClusTableActions<GlobalConfiguration> getGlobalActions() {
		return globalActions;
	}

	public ClusTableActions<HolonConfiguration> getHolonActions() {
		return holonActions;
	}

	public void setGlobalObservations(ClusTableObservations globalObservations) {
		this.globalObservations = globalObservations;
	}

	public void setHolonObservations(ClusTableObservations holonObservations) {
		this.holonObservations = holonObservations;
	}

	public ClusTableObservations getGlobalObservations() {
		return globalObservations;
	}

	public ClusTableObservations getHolonObservations() {
		return holonObservations;
	}

	public RewardFunction getGlobalActionsFunction() {
		return globalRewardFunction;
	}

	public RewardFunction getHolonActionsFunction() {
		return holonRewardFunction;
	}

	public ClusLogger getLogger() {
		return logger;
	}

	public boolean isGlobalDeterministic() {
		return globalDeterministic;
	}

	public boolean isHolonDeterministic() {
		return holonDeterministic;
	}

	public void init(ClusTableStates states, ClusTableActions<?> actions) {
		if (states == null || actions == null)
			return;
		ClusTableCell cell;
		List<ClusTableCell> cells;
		for (String name : states.getValues().keySet()) {
			cells = new ArrayList<ClusTableCell>();
			for (String actionName : actions.getActions().keySet()) {
				cell = new ClusTableCell();
				cell.setState(name);
				cell.setAction(actionName);
				cell.setValue(0.0);
				cells.add(cell);
			}
			states.getRows().put(name, cells);
		}
	}

	@Override
	public GlobalConfiguration getGlobalConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, boolean exploration, AlgorithmAgentParent agent) {

		log.info("Getting global conf, learning mode enabled ? " + isLearning());

		if (exploration)
			return getGlobalConfiguration(oldSchedules, newSchedules, info,
					agent);
		else {
			if (globalStates == null)
				return null;
			Map<String, Measure> measures = calculateMeasures(oldSchedules,
					newSchedules, agent);
			String currentState = globalStates.getCurrentState(measures);
			String action = globalStates.getActionWithMaxValue(currentState);
			return globalActions.getActions().get(action);
		}
	}

	private ClusTableCell previousCell;
	private Map<String, Double> previousParameters;

	private GlobalConfiguration getGlobalConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, AlgorithmAgentParent agent) {

		if (globalStates == null)
			return null;
		Map<String, Measure> measures = calculateMeasures(oldSchedules,
				newSchedules, agent);
		String currentState = globalStates.getCurrentState(measures);
		String action = globalStates.getAction(currentState);
		if (previousCell == null) {
			previousCell = globalStates.getCell(currentState, action);
			// globalStates.updateCellUseCount(currentState, action,
			// previousCell.getUseCount() + 1);
			previousParameters = Helper.getParameters(oldSchedules,
					newSchedules, info, timestamp);
			return globalActions.getActions().get(action);
		}

		Map<String, Double> parameters = Helper.getParameters(oldSchedules,
				newSchedules, info, timestamp);

		double value = calculateCellValue(globalStates, globalRewardFunction,
				parameters, previousParameters, measures, null, globalFactor,
				currentState, previousCell, globalDeterministic);

		globalStates.updateCellValue(previousCell.getState(),
				previousCell.getAction(), value);
		globalStates.updateCellUseCount(previousCell.getState(),
				previousCell.getAction(), previousCell.getUseCount() + 1);

		previousCell = globalStates.getCell(currentState, action);
		previousParameters = parameters;

		return globalActions.getActions().get(action);
	}

	private final Map<AID, ClusTableCell> previousCells = new HashMap<AID, ClusTableCell>();
	private final Map<AID, Map<String, Double>> previousHolonParameters = new HashMap<AID, Map<String, Double>>();

	@Override
	public Map<AID, HolonConfiguration> getHolonsConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, boolean exploration, AlgorithmAgentParent agent) {

		log.info("Getting holon conf, learning mode enabled ? " + isLearning());

		if (exploration)
			return getHolonsConfiguration(oldSchedules, newSchedules, info,
					agent);
		else {
			if (holonStates == null)
				return null;
			Map<AID, HolonConfiguration> configurations = new HashMap<AID, HolonConfiguration>();
			Map<String, Measure> measures = calculateMeasures(oldSchedules,
					newSchedules, agent);
			Set<AID> aids;
			if (newSchedules != null && newSchedules.size() > 0)
				aids = newSchedules.keySet();
			else
				aids = oldSchedules.keySet();

			for (AID holon : aids) {
				String currentState = holonStates.getCurrentState(measures,
						holon);
				String action = holonStates.getActionWithMaxValue(currentState);
				configurations
						.put(holon, holonActions.getActions().get(action));

			}
			return configurations;
		}

	}

	private Map<AID, HolonConfiguration> getHolonsConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, AlgorithmAgentParent agent) {

		if (holonStates == null)
			return null;
		Map<AID, HolonConfiguration> configurations = new HashMap<AID, HolonConfiguration>();
		Map<String, Measure> measures = calculateMeasures(oldSchedules,
				newSchedules, agent);
		ClusTableCell previousHolonCell;
		Map<AID, Map<String, Double>> holonParams = Helper.getHolonParameters(
				oldSchedules, newSchedules, info, timestamp);
		Set<AID> aids;
		if (newSchedules != null && newSchedules.size() > 0)
			aids = newSchedules.keySet();
		else
			aids = oldSchedules.keySet();
		for (AID holon : aids) {
			String currentState = holonStates.getCurrentState(measures, holon);
			String action = holonStates.getAction(currentState);
			previousHolonCell = previousCells.get(holon);
			if (previousHolonCell == null) {
				previousHolonCell = holonStates.getCell(currentState, action);
				previousCells.put(holon, previousHolonCell);
				holonStates.updateCellUseCount(currentState, action,
						previousHolonCell.getUseCount() + 1);
				configurations
						.put(holon, holonActions.getActions().get(action));
				previousHolonParameters.put(holon, holonParams.get(holon));
				continue;
			}
			Map<String, Double> parameters = holonParams.get(holon);

			double value = calculateCellValue(holonStates, holonRewardFunction,
					parameters, previousHolonParameters.get(holon), measures,
					holon, holonsFactor, currentState, previousHolonCell,
					holonDeterministic);

			holonStates.updateCellValue(previousHolonCell.getState(),
					previousHolonCell.getAction(), value);
			holonStates.updateCellUseCount(previousHolonCell.getState(),
					previousHolonCell.getAction(),
					previousHolonCell.getUseCount() + 1);

			previousHolonCell = holonStates.getCell(currentState, action);
			previousCells.put(holon, previousHolonCell);
			previousHolonParameters.put(holon, parameters);

			configurations.put(holon, holonActions.getActions().get(action));

		}
		return configurations;
	}

	@Override
	public void setAlgorithmParameters(Map<String, String> parameters) {
		Map<String, Double> dParams = new HashMap<String, Double>();
		for (String key : parameters.keySet()) {
			dParams.put(key, Double.valueOf(parameters.get(key)));
		}
		setDefaultParams(dParams);

	}

	private double calculateCellValue(ClusTableStates states,
			RewardFunction function, Map<String, Double> parameters,
			Map<String, Double> prevParameters, Map<String, Measure> measures,
			AID aid, double factor, String currentState,
			ClusTableCell previousCell, boolean deterministic) {
		double value;

		if (defaultParams != null)
			parameters.putAll(defaultParams);

		double reward = function.getValue(parameters, prevParameters, measures,
				aid);

		logger.log(states, function, parameters, prevParameters, measures, aid,
				factor, currentState, previousCell, reward);

		if (deterministic)
			value = reward + factor * states.getMaxActionValue(currentState);
		else {
			double alpha = 1 / (1 + previousCell.getUseCount());
			value = (1 - alpha)
					* previousCell.getValue()
					+ alpha
					* (reward + factor * states.getMaxActionValue(currentState));
		}

		return value;
	}

	@Override
	public void save(String clusTableFileName, String saveFileName)
			throws Exception {
		log.info("Saving configuration");

		if (overwriteConf) {
			ClusTableToXMLWriter.writeToXML(clusTableFileName, this);
		}
		String parts[] = clusTableFileName.split("\\\\");

		ClusTableToXMLWriter.writeToXML(saveFileName + "_"
				+ parts[parts.length - 1], this);

		this.getLogger().save(
				saveFileName + "_" + parts[parts.length - 1] + "_log.txt");

	}

	@Override
	public void init(String fileName) {
		try {
			ClusTableStructureParser.parse(fileName, this);
			log.info("Init cluse table, useTrees: " + this.isUseTrees());

			if (this.isUseTrees()) {
				initTrees();
			} else {
				initCentres();
			}

		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	private void initCentres() {
		// build centres here
		log.info("Building centres...");
		final RUtils rutils = new RUtils();
		rutils.start();

		rutils.buildCentres(getHolonStates().getValues(), getGlobalStates()
				.getValues());

	}

	private void initTrees() {
		// build tree here
		log.info("Building tree...");
		final RUtils rutils = new RUtils();
		rutils.start();

		rutils.buildDecisionTrees(getGlobalObservations(),
				getHolonObservations());
	}

	public void clustering() {
		// constants
		final String statePrefix = "S";
		final RUtils rutils = new RUtils();

		// tmp variables
		List<String> rNames = null;
		List<String> cNames = null;
		double[] matrixValues = null;
		Integer nr = 0;
		int measureNr = 0;
		REXP kmeans = null;
		List<Map<String, Double>> centres = null;

		rutils.start();

		log.info("Clustering started...");

		// GLOBAL STATES
		if (globalStates != null) {
			List<Map<String, Double>> globalHistory = globalStates
					.getMeasurmentsHistory();
			rNames = new LinkedList<String>();
			cNames = new LinkedList<String>();

			// fill the names
			for (String mn : globalHistory.get(0).keySet()) {
				cNames.add(mn);
			}

			matrixValues = new double[globalHistory.size() * cNames.size()];

			nr = 0;
			measureNr = 0;
			for (Map<String, Double> values : globalHistory) {
				rNames.add((nr++).toString());

				for (String measure : values.keySet()) {
					matrixValues[measureNr++] = values.get(measure);
				}
			}

			log.info("Global history:\n" + globalHistory);
			log.info("Global rNames:\n" + rNames);
			log.info("Global cNames:\n" + cNames);

			kmeans = rutils.kmeans(matrixValues,
					rNames.toArray(new String[] {}),
					cNames.toArray(new String[] {}), usePam, minClusCount,
					maxClusCount);

			centres = rutils
					.getCentres(kmeans, cNames.toArray(new String[] {}));

			// remove learning states
			globalStates.removeStates();

			// add generated clusters
			List<String> sNames = new ArrayList<String>();

			for (int s = 0; s < centres.size(); s++) {
				String sName = statePrefix + s;
				sNames.add(sName);
				globalStates.addState(sName, centres.get(s));
			}

			globalObservations.clean();
			addObservations(globalObservations, kmeans, cNames, sNames);

			// remove all action cells
			globalStates.removeActions();

			// add actions with default values
			globalStates.addDefaultActionCellForEachState(globalActions
					.getActions().keySet());
		}

		// HOLON STATES
		if (holonStates != null) {
			List<Map<String, Double>> holonHistory = holonStates
					.getMeasurmentsHistory();

			rNames = new LinkedList<String>();
			cNames = new LinkedList<String>();

			// fill the names
			for (String mn : holonHistory.get(0).keySet()) {
				cNames.add(mn);
			}

			matrixValues = new double[holonHistory.size() * cNames.size()];

			nr = 0;
			measureNr = 0;
			for (Map<String, Double> values : holonHistory) {
				rNames.add((nr++).toString());

				for (String measure : values.keySet()) {
					matrixValues[measureNr++] = values.get(measure);
				}
			}

			log.info("Holon history:\n" + holonHistory);
			log.info("Holon rNames:\n" + rNames);
			log.info("Holon cNames:\n" + cNames);

			kmeans = rutils.kmeans(matrixValues,
					rNames.toArray(new String[] {}),
					cNames.toArray(new String[] {}));

			centres = rutils
					.getCentres(kmeans, cNames.toArray(new String[] {}));

			// remove learning states
			holonStates.removeStates();

			// add generated clusters
			List<String> sNames = new ArrayList<String>();

			for (int s = 0; s < centres.size(); s++) {
				String sName = statePrefix + s;
				sNames.add(sName);
				holonStates.addState(sName, centres.get(s));
			}

			holonObservations.clean();
			addObservations(holonObservations, kmeans, cNames, sNames);

			// remove all action cells
			holonStates.removeActions();

			// add actions with default values
			holonStates.addDefaultActionCellForEachState(holonActions
					.getActions().keySet());
		}
		log.info("Clustering completed...");

	}

	// cNames - names of the columns - measures in fact
	// sNames - names of the states
	private void addObservations(ClusTableObservations clusTableObservations,
			REXP clusteringResult, List<String> cNames, List<String> sNames) {
		// rUtils must be started at this point
		final RUtils rutils = new RUtils();

		Map<String, List<List<Double>>> assignment = rutils
				.getStatesAssignment(clusteringResult,
						sNames.toArray(new String[] {}));

		// key in the entry is name of the state
		// value in the entry are list of measures set assigned to the state
		for (Entry<String, List<List<Double>>> entry : assignment.entrySet()) {

			for (List<Double> point : entry.getValue()) {
				ClusTableObservation nextObservation = new ClusTableObservation(
						entry.getKey());
				for (int i = 0; i < point.size(); i++) {
					nextObservation.addMeasureElement(cNames.get(i),
							point.get(i));
				}
				clusTableObservations.addObservation(nextObservation);
			}

		}

	}

}
