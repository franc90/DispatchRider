package adapter;

import java.util.List;

import dtp.commission.CommissionHandler;
import dtp.simmulation.SimInfo;

public interface Adapter {
	public List<CommissionHandler> readCommissions();
	public SimInfo getSimInfo();
}
