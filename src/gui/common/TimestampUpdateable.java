package gui.common;

import java.util.Arrays;
import java.util.Vector;

import xml.elements.SimmulationData;
import dtp.simmulation.SimInfo;

/*
 * abstrakcyjna klasa, zapewnia hashmapê dla ka¿dego timestampa, mo¿na sobie wrzucaæ co siê chce
 */
public abstract class TimestampUpdateable implements Updateable {
	protected SimInfo simInfo;
	
	protected Vector<TimestampRecord> records = new Vector<TimestampRecord>();
	
	protected TimestampRecord	newRecord;
	public TimestampRecord 	visualisedRecord;
	
	protected int visualisedTimestamp = 0;
	protected int newTimestamp = 0;
	
	public TimestampUpdateable() {
		newRecord = new TimestampRecord(0, null);
		visualisedRecord = newRecord;
		records.add(newRecord);
	}
	
	public void newTimestampUpdate(int val){
		if(records.size() > 1 && visualisedRecord == records.get(records.size()-2)) {
			visualisedTimestamp = records.lastElement().timestamp;
			visualisedRecord = records.lastElement();
		}
		
		newRecord = new TimestampRecord(val, null);
		newTimestamp = val;
		records.add(newRecord);
	}
	
	abstract public void update(SimmulationData data);
	
	public void setDrawnTimestamp(int val) {
		TimestampRecord search = new TimestampRecord(val, null);
		try {
			visualisedRecord = records.get(Arrays.binarySearch(records.toArray(), search, new TimestampRecordComparator()));
			visualisedTimestamp = val;
		}
		catch(Exception e) {
			//out of bounds
		}
	}
	
	public void setSimInfo(SimInfo simInfo) {
		this.simInfo = simInfo;
	}
	
	public int getDrawnTimestamp() {
		return visualisedTimestamp;
	}
}
