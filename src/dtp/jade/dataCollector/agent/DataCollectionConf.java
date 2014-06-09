package dtp.jade.dataCollector.agent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class DataCollectionConf implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5982075832794185183L;

	private class AgentConf implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3174313488662739549L;
		private final String id; // agent's name
		private final int timeGap;
		private final int timeGapPeriod;
		
		public AgentConf(String id, int timeGap, int timeGapPeriod) {
			super();
			this.id = id;
			this.timeGap = timeGap;
			this.timeGapPeriod = timeGapPeriod; 
		}

		public String getId() {
			return id;
		}
		
		public int getTimeGap() {
			return timeGap;
		}
		
		public int getTimeGapPeriod() {
			return this.timeGapPeriod;
		}
	}
	
	private boolean serialize;
	private boolean timestampSync;
	private int defTimeGap;
	private int defTimeGapPeriod;
	private List<AgentConf> agentsConfs;
	
	public DataCollectionConf(boolean serialize, boolean timestampSync,
			int defTimeGap, int defTimeGapPeriod) {
		super();
		this.serialize = serialize;
		this.timestampSync = timestampSync;
		if(defTimeGap >= 0)	this.defTimeGap = defTimeGap;
		else 				this.defTimeGap = 1;
		if(defTimeGapPeriod >= 0)	this.defTimeGapPeriod = defTimeGapPeriod;
		else 						this.defTimeGapPeriod = 1000;
		this.agentsConfs = new LinkedList<AgentConf>();
	}

	public void addAgentConfig(String name, int timeGap, int timeGapPeriod){
		AgentConf conf = new AgentConf(name, timeGap, timeGapPeriod);
		this.agentsConfs.add(conf);
	}
	
	public int getTimeGap(String name){
		int result = defTimeGap;
		for(AgentConf conf : agentsConfs)
			if ( conf.getId().equals(name) ) result = conf.getTimeGap();
		return result;
	}
	
	public int getTimeGapPeriod(String name) {
		int result = defTimeGapPeriod;
		for(AgentConf conf : agentsConfs)
			if ( conf.getId().equals(name) ) result = conf.getTimeGapPeriod();
		return result;
	}
	
	public boolean getSerialize(){
		return this.serialize;
	}
	
	public boolean getTimestampSync(){
		return this.timestampSync;
	}
	
}
