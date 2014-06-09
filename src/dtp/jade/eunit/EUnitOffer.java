package dtp.jade.eunit;

import jade.core.AID;

import java.io.Serializable;

/**
 * Oferta eunita
 * 
 * @author kony.pl
 */
public class EUnitOffer implements Serializable, Comparable<EUnitOffer> {

	public int compareTo(EUnitOffer o) {
		return new Double(value).compareTo(o.getValue());
	}
	
    private static final long serialVersionUID = -6927401629446931973L;

    private AID agent;

    private double value;

    private Integer commissionsCount;
    
    public EUnitOffer(AID agent, double value, int commissionCount) {
    	this.commissionsCount=commissionCount;
        this.agent = agent;
        this.value = value;
    }
    
    
    
    public int getCommissionCount() {
    	return commissionsCount;
    }

    public void setAgent(AID agent) {

        this.agent = agent;
    }

    public AID getAgent() {

        return agent;
    }

    public void setValue(double value) {

        this.value = value;
    }

    public double getValue() {

        return value;
    }
}
