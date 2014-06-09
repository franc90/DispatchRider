package dtp.jade.dataCollector.agent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dtp.jade.info.AgentInfoPOJO;
import dtp.jade.info.InfoAgent;
import dtp.jade.transport.TransportAgentData;
import dtp.jade.transport.TransportType;

public class InfoCollectorMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	protected  ArrayList<AgentInfoPOJO> driverAgentsInfo;

	protected  int driverAgentsNo;

//	protected  ArrayList<AgentInfoPOJO> truckAgentsInfo;

	protected final int truckAgentsNo;

//	protected  ArrayList<AgentInfoPOJO> trailerAgentsInfo;

	protected  int trailerAgentsNo;

//	protected  ArrayList<AgentInfoPOJO> eunitAgentsInfo;

//	protected  ArrayList<AgentInfoPOJO> algorithmAgentsInfo;

//	protected  Map<TransportType, List<TransportAgentData>> agents;

	protected  int eunitAgentsNo;

	protected  int algorithmAgentsNo;
	
	protected  String readable_timestamp;
	protected  Date date;
	protected  DateFormat dateFormat;
	protected  Calendar cal;
	
	
	
	
	public InfoCollectorMessage(InfoAgent agent){

		dateFormat=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		cal=  Calendar.getInstance();
		date=cal.getTime();
		readable_timestamp=dateFormat.format(date);
		
		this.eunitAgentsNo=agent.getEUnitAgentsNo();
		this.trailerAgentsNo=agent.getTrailerAgentsNo();
		this.truckAgentsNo=agent.getTruckAgentsNo();
		this.driverAgentsNo=agent.getDriverAgentsNo();
		this.algorithmAgentsNo=agent.getAlgorithmAgentsNo();
		
	/*	this.driverAgentsInfo=agent.getDriverAgentsInfo();
		this.truckAgentsInfo=agent.getTruckAgentsInfo();
		this.trailerAgentsInfo=agent.getTrailerAgentsInfo();
		this.eunitAgentsInfo=agent.getEUnitAgentsInfo();
		this.algorithmAgentsInfo=agent.getAlgorithmAgentsInfo();
		
		*/
	}
	

	
}
