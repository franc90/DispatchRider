package dtp.jade.eunit;

import java.io.Serializable;

import dtp.jade.distributor.NewTeamData;
import dtp.simmulation.SimInfo;

public class EUnitInitialData implements Serializable {

	private SimInfo simInfo;
	private NewTeamData data;
	
	public EUnitInitialData(SimInfo simInfo,NewTeamData data) {
		this.simInfo=simInfo;
		this.data=data;
	}
	
	public SimInfo getSimInfo() {
		return simInfo;
	}

	public NewTeamData getData() {
		return data;
	}
    /**
	 * 
	 */
	private static final long serialVersionUID = -3813858258020872735L;
	private int depot;

    /**
     * getter
     * 
     * @return the depot
     */
    public int getDepot() {
        return depot;
    }

    /**
     * setter
     * 
     * @param depot
     *        the depot to set
     */
    public void setDepot(int depot) {
        this.depot = depot;
    }
}
