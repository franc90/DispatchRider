package dtp.jade.dataCollector.agent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import xml.elements.SimmulationData;

import dtp.jade.eunit.ExecutionUnitAgent;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;


public class EunitCollectorMessage implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -611986556108829239L;
	protected  String readable_timestamp;
	protected  Date date;
	protected  DateFormat dateFormat;
	protected  Calendar cal;
	protected SimmulationData data;
	
	
	
	public EunitCollectorMessage(ExecutionUnitAgent agent){

		dateFormat=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		cal=  Calendar.getInstance();
		date=cal.getTime();
		readable_timestamp=dateFormat.format(date);

		
		data = agent.prepareSimmulationData();
		
		
		
		
		
	}

}
