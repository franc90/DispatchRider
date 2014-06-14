package algorithm.AdaptiveLargeNeighbourhoodSearch.removal.method.shawcomparators;

import java.util.Comparator;

import dtp.commission.Commission;

public interface CommissionComparator extends Comparator<Commission> {
	public void setCurrent(Commission current);
}
