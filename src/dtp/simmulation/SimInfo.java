package dtp.simmulation;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Map;

import machineLearning.MLAlgorithm;
import measure.MeasureCalculatorsHolder;
import punishment.PunishmentFunction;
import algorithm.Algorithm;
import algorithm.BasicSchedule;
import algorithm.GraphSchedule;
import algorithm.Schedule;
import algorithm.STLike.ExchangeAlgorithmsFactory;
import algorithm.comparator.CommissionsComparator;
import dtp.graph.predictor.GraphLinkPredictor;
import dtp.jade.gui.CommissionsHolder;
import dtp.optimization.TrackFinder;

public class SimInfo implements Serializable {

	private static final long serialVersionUID = -8359759972505849788L;

	private Point2D.Double depot;

	private double deadline;

	private double maxLoad;

	private CommissionsHolder holder;

	private MeasureCalculatorsHolder calculatorsHolder;

	private PunishmentFunction punishmentFunction;
	private Map<String, Double> defaultPunishmentFunValues;
	private Double delayLimit;
	private int holons;
	private boolean firstComplexSTResultOnly;

	private MLAlgorithm mlAlgorithm;
	private boolean exploration;

	private boolean STAfterGraphChange;

	private ExchangeAlgorithmsFactory exchangeAlgFactory;

	private Boolean updateAfterArrival;

	private CommissionsComparator comparator;

	public CommissionsComparator getComparator() {
		return comparator;
	}

	public void setComparator(CommissionsComparator comparator) {
		this.comparator = comparator;
	}

	public Boolean getUpdateAfterArrival() {
		return updateAfterArrival;
	}

	public void setUpdateAfterArrival(Boolean updateAfterArrival) {
		this.updateAfterArrival = updateAfterArrival;
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

	private Schedule scheduleCreator = new BasicSchedule(null);

	public SimInfo(Point2D.Double depot, double deadline, double maxLoad) {

		this.depot = depot;
		this.deadline = deadline;
		this.maxLoad = maxLoad;
	}

	public void setTrackFinder(TrackFinder finder, GraphLinkPredictor predictor) {
		if (finder == null) {
			this.scheduleCreator = new BasicSchedule(null);
		} else {
			this.scheduleCreator = new GraphSchedule(null, finder, predictor);
		}
	}

	public MLAlgorithm getMlAlgorithm() {
		return mlAlgorithm;
	}

	public void setMlAlgorithm(MLAlgorithm mlTable) {
		this.mlAlgorithm = mlTable;
	}

	public boolean isExploration() {
		return exploration;
	}

	public void setExploration(boolean exploration) {
		this.exploration = exploration;
	}

	public Schedule getScheduleCreator() {
		return scheduleCreator;
	}

	public Schedule createSchedule(Algorithm algorithm) {
		return scheduleCreator.createSchedule(algorithm);
	}

	public Schedule createSchedule(Algorithm algorithm, int currentCommission,
			double creationTime) {
		return scheduleCreator.createSchedule(algorithm, currentCommission,
				creationTime);
	}

	public boolean isFirstComplexSTResultOnly() {
		return firstComplexSTResultOnly;
	}

	public void setFirstComplexSTResultOnly(boolean firstComplexSTResultOnly) {
		this.firstComplexSTResultOnly = firstComplexSTResultOnly;
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

	public PunishmentFunction getPunishmentFunction() {
		return punishmentFunction;
	}

	public void setPunishmentFunction(String punishmentFunction) {
		if (punishmentFunction == null)
			return;
		this.punishmentFunction = new PunishmentFunction(punishmentFunction);
	}

	public CommissionsHolder getHolder() {
		return holder;
	}

	public void setHolder(CommissionsHolder holder) {
		this.holder = holder;
	}

	public Point2D.Double getDepot() {

		return depot;
	}

	public void setDepot(Point2D.Double depot) {

		this.depot = depot;
	}

	public double getDeadline() {

		return deadline;
	}

	public void setDeadline(double deadline) {

		this.deadline = deadline;
	}

	public double getMaxLoad() {

		return maxLoad;
	}

	public void setMaxLoad(double maxLoad) {

		this.maxLoad = maxLoad;
	}

	public MeasureCalculatorsHolder getCalculatorsHolder() {
		return calculatorsHolder;
	}

	public void setCalculatorsHolder(MeasureCalculatorsHolder calculatorsHolder) {
		this.calculatorsHolder = calculatorsHolder;
	}

}
