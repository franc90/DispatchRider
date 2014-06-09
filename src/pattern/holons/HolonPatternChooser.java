package pattern.holons;

import java.util.Set;

import dtp.commission.Commission;
import dtp.jade.transport.NewHolonOffer;

public class HolonPatternChooser {
	
	public static NewHolonOffer getBestOffer(Set<NewHolonOffer> offers, double pattern, double dist, Commission currentCommission) {
		NewHolonOffer result=null;
		double bestOffer=Double.MAX_VALUE;
		for(NewHolonOffer offer:offers) {
			if (offer.isValid() == false)
				continue;
			if(bestOffer>offer.getTrailerData().getCapacity()) {
				result=offer;
				bestOffer=offer.getTrailerData().getCapacity();
				
			}
		}
		return result;
	}
}
