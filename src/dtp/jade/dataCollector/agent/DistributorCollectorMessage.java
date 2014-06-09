package dtp.jade.dataCollector.agent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import measure.printer.MeasureData;


import dtp.jade.distributor.DistributorAgent;

public class DistributorCollectorMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2027998230860712506L;
	protected  String readable_timestamp;
	protected  Date date;
	protected  DateFormat dateFormat;
	protected  Calendar cal;
	protected MeasureData data;
	
	
	public DistributorCollectorMessage(DistributorAgent agent){

		dateFormat=  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		cal=  Calendar.getInstance();
		date=cal.getTime();
		readable_timestamp=dateFormat.format(date);

		
		data=agent.getMeasureData();
		
		
		
		
	}

}
