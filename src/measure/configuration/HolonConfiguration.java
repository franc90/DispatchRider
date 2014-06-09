package measure.configuration;

import java.io.Serializable;

import algorithm.Algorithm;

/**
 * This class contains parameters, which can be changed in specified holon.
 */
public class HolonConfiguration implements Serializable {

	private static final long serialVersionUID = 1475331998306180955L;

	private Boolean simmulatedTrading;

	/**
	 * Algorithm which is responsible for inserting new commissions into
	 * schedule
	 */
	private Algorithm algorithm;

	/**
	 * This parameters determines how cost of new commission should be
	 * calculated by holon. If you set it to false, cost of commission is equal
	 * to increase of time (summary time of realization all commissions in
	 * schedule) . If you set it to true, first increase of distance is
	 * calculated and then sum of cost functions of transport agents are used.
	 */
	private Boolean dist;

	public Boolean getSimmulatedTrading() {
		return simmulatedTrading;
	}

	public void setSimmulatedTrading(Boolean simmulatedTrading) {
		this.simmulatedTrading = simmulatedTrading;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
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

	public Boolean isDist() {
		return dist;
	}

	public void setDist(Boolean dist) {
		this.dist = dist;
	}

}
