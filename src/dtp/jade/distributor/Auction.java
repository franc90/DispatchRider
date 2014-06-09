package dtp.jade.distributor;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dtp.commission.Commission;
import dtp.jade.eunit.EUnitOffer;

public class Auction {

    private Commission commission;

    // liczba ofert wyslanych do EUnitow
    private int sentOffersNo;

    // oferty zgloszone przez EUnity
    private List<EUnitOffer> offers = new LinkedList<EUnitOffer>();

    public Auction() {

        this.sentOffersNo = 0;
        offers = new LinkedList<EUnitOffer>();
    }

    public void setCommission(Commission commission) {

        this.commission = commission;
    }

    public Commission getCommission() {

        return commission;
    }

    public void setSentOffersNo(int sentOffersNo) {

        this.sentOffersNo = sentOffersNo;
    }

    public int getSentOffersNo() {

        return sentOffersNo;
    }

    public void addOffer(EUnitOffer offer) {
        for (EUnitOffer off : offers) {
            if (off.getAgent().equals(offer)) {
                System.err
                        .println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ SAME AGENT ADDED TWICE");
            }
        }
        if(!offers.contains(offer)) offers.add(offer);

    }

    public int getOffersNo() {

        return offers.size();
    }

    public EUnitOffer[] getOffers() {

        Iterator<EUnitOffer> iter = offers.iterator();
        EUnitOffer[] out = new EUnitOffer[offers.size()];
        int count = 0;

        while (iter.hasNext())
            out[count++] = iter.next();

        return out;
    }

    public boolean gotAllOffers() {
    	if(offers.size()==sentOffersNo) {
    		Collections.sort(offers);
    		return true;
    	} 
        return false;
    }
}
