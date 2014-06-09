package gui.common;

import dtp.simmulation.SimInfo;
import xml.elements.SimmulationData;

public interface Updateable {
	public void newTimestampUpdate(int val);
	public void setDrawnTimestamp(int val);
	public int getDrawnTimestamp();
	public void update(SimmulationData data);
	public void setSimInfo(SimInfo simInfo);
}
