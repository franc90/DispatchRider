package algorithm.simmulatedTrading;

import java.io.Serializable;
import java.util.Set;

import dtp.commission.Commission;

public class SimmulatdTradingParameters implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int STDepth;
	public Set<Integer> commissionsId;
	public Commission commission;
	public String msg;
}
