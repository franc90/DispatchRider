package dtp.optimization;

import org.apache.log4j.Logger;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;

/**
 * @author Szymon Borgosz
 */
public class SimulatedAnnealing implements TrackFinder {

	private static final long serialVersionUID = 5697709779534579298L;

	/** Logger. */
	private static Logger logger = Logger.getLogger(SimulatedAnnealing.class);

	/**
	 * quantity of temperatures
	 */
	private int imax;

	/**
	 * quantity of iterations for a single temperature value
	 */
	private int kmax;

	/**
	 * temperature is increased alfa-times (c=c*alfa)
	 */
	private double alfa;

	/**
	 * initial temperature: 0 <= c <= 1
	 */
	private double c;

	/**
	 * sets quantity of temperatures
	 * 
	 * @param val
	 */
	public void setimax(int val) {
		imax = val;
	}

	/**
	 * sets quantity of iterations for a single temperature value
	 * 
	 * @param val
	 */
	public void setkmax(int val) {
		kmax = val;
	}

	/**
	 * sets temperature is increased alfa-times (c=c*alfa)
	 * 
	 * @param val
	 */
	public void setalfa(double val) {
		alfa = val;
	}

	/**
	 * sets initial temperature: 0 <= c <= 1
	 * 
	 * @param val
	 */
	public void setc(double val) {
		c = val;
	}

	/**
	 * gets quantity of temperatures
	 * 
	 * @return
	 */
	public int getimax() {
		return imax;
	}

	/**
	 * gets quantity of iterations for a single temperature value
	 * 
	 * @return
	 */
	public int getkmax() {
		return kmax;
	}

	/**
	 * gets temperature is increased alfa-times (c=c*alfa)
	 * 
	 * @return
	 */
	public double getalfa() {
		return alfa;
	}

	/**
	 * initial temperature: 0 <= c <= 1
	 * 
	 * @return
	 */
	public double getc() {
		return c;
	}

	/**
	 * Creates a TrackFinder object with given parameters.
	 * 
	 * @param imax
	 *            - quantity of temperatures (main iterations)
	 * @param kmax
	 *            - quantity of iterations for a single temperature value
	 * @param c
	 *            - initial temperature: 0 ? c ? 1
	 * @param alfa
	 *            - // temperature is increased alfa-times (c=c*alfa)
	 */
	public SimulatedAnnealing(int kmax, int imax, double c, double alfa) {
		super();
		this.kmax = kmax;
		this.imax = imax;
		this.alfa = alfa;
		this.c = c;
	}

	/**
	 * Creates a TrackFinder object with default parameters.
	 */
	public SimulatedAnnealing() {
		super();
		this.kmax = 3;
		this.imax = 3;
		this.alfa = 0.5;
		this.c = 0.5;
	}

	/**
	 * Permutes (disturbs) a given track - movement by insertion. Solution:
	 * Insert a Point of this track after other Point.
	 * 
	 * @param pi
	 */
	public GraphTrack permute(GraphTrack pi) {
		return new GraphTrack(pi.getFirst(), pi.getLast());
	}

	/**
	 * Computes probability of accepting a worse solution (track with greater
	 * cost). Used to avoid blocking in a local minima.
	 * 
	 * @param pi
	 * @param piChanged
	 * @param temperature
	 */
	private double probabilityC(GraphTrack pi, GraphTrack piChanged,
			double temperature) {
		double cost = pi.getCost();
		double costChanged = piChanged.getCost();
		if (costChanged != 0)
			return (cost / costChanged) * temperature;
		else
			return 1;
	}

	/**
	 * Optimizes a given track 'pi' using Optimization by Simulated Annealing
	 * 
	 * @param pi
	 */
	public GraphTrack optimize(GraphTrack pi) {
		try {
			GraphTrack piChanged;
			int i = 0;
			int k;
			do {
				k = 0;
				kmax = 5;
				do {
					piChanged = permute(pi);
					if (piChanged.getCost() - pi.getCost() < 0
							|| Math.random() < probabilityC(pi, piChanged, c))
						pi = piChanged;
					k++;
				} while (k < kmax);
				c *= alfa;
				i++;
			} while (i < imax);
		} catch (Exception ex) {
			logger.error(ex);
		}
		return pi;
	}

	public GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint) {
		GraphTrack tr = new GraphTrack(startPoint, endPoint);
		return optimize(tr);
	}

	public void setGraph(Graph graph) {
		// do nothing
	}

	public Graph getGraph() {
		throw new IllegalStateException("class don't have graph");
	}
}
