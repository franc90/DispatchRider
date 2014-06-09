package dtp.jade.dataCollector.agent;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import dtp.jade.BaseAgent;
import dtp.jade.CommunicationHelper;
import dtp.jade.dataCollectorAgent.agent.behaviour.*;

public class DataCollectionAgent extends BaseAgent {

	private boolean SERIALIZE=false;
	private boolean SYNC=true;
	private boolean REALTIME=true;
	private int INFO_SLEEP_TIME=50000;
	private int EUNIT_SLEEP_TIME=5000;
	private int DISTRIBUTOR_SLEEP_TIME=5000;

	
	/*Collected Data*/
	protected List<InfoCollectorMessage> infoAgentData;
	protected List<EunitCollectorMessage> eunitAgentData;
	protected List<DistributorCollectorMessage> distributorAgentData;

	
	
	/*-------------*/
	
	private static final long serialVersionUID = -4528325669072103917L;
	private static Logger logger = Logger.getLogger(DataCollectionAgent.class);
	private static Logger dataLogger = Logger.getLogger("DataLogger");
	
	
	@Override
	protected void setup() 
	{
		
		PropertyConfigurator.configure("conf" + File.separator
				+ "Log4j.properties");
		


		logger.info(this.getLocalName() + " - Hello World!");
		dataLogger.info("Data Logging started");
		
		
		infoAgentData=new LinkedList<InfoCollectorMessage>();
		eunitAgentData=new LinkedList<EunitCollectorMessage>();
		distributorAgentData=new LinkedList<DistributorCollectorMessage>();
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("DataCollectorService");
		sd1.setName("DataCollectorService");
		dfd.addServices(sd1);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		logger.info(this.getLocalName() + " - registering DataCollectorService");
		
		
		if(REALTIME==true)
		{
		this.addBehaviour(new GetDataFromInfoAgentBehaviour(this,INFO_SLEEP_TIME));
		this.addBehaviour(new GetInfoReply(this));
		this.addBehaviour(new GetDataFromEunitAgentBehaviour(this,EUNIT_SLEEP_TIME));
		this.addBehaviour(new GetEunitReply(this));
		this.addBehaviour(new GetDataFromDistributorBehaviour(this,DISTRIBUTOR_SLEEP_TIME));
		this.addBehaviour(new GetDistributorReply(this));
		}
		
		this.addBehaviour(new GetTimestamp(this));


		
	}
	
	@Override
	protected void takeDown() {
		try {
			logger.info(this.getLocalName() + " - Goodbye World!");
			

			
			DFService.deregister(this);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void collectDataFromInfoAgent()
	{

		
		
		
		ACLMessage cfp = null;
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"InfoAgentService");

		logger.info("Collector Agent - collecting data from InfoAgent");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(CommunicationHelper.DATA_COLLECTION_FROM_INFO);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject("");
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				send(cfp);
			}
		
		
		
		}
		
		
	}
	
	
	
	public void dataFromInfoAgentReceived(ACLMessage tmp)
	{
		
		InfoCollectorMessage message=null;
		
		try {
			message=(InfoCollectorMessage) tmp.getContentObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(message!=null)
		{
			infoAgentData.add(message);
			
			
			if(SERIALIZE==true){
				try
				{
			      FileOutputStream out = new FileOutputStream("tmp_tests/CollectorData/"+message.toString()+System.currentTimeMillis()+".out");
			      ObjectOutputStream oos = new ObjectOutputStream(out);
			      oos.writeObject(message);
			      oos.flush();
			      oos.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("Info agent messages count: " + infoAgentData.size());

		
		}

		
	}

	public void GetDataFromEunitAgent() {

		
		
		
		ACLMessage cfp = null;
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,
				"ExecutionUnitService");

		logger.info("Collector Agent - collecting data from EunitAgent");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(CommunicationHelper.DATA_COLLECTION_FROM_EUNIT);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject("");
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				
				
				send(cfp);
			}
		
		
		
		}
		
	}

	public void dataFromEunitAgentReceived(ACLMessage tmp) {
		

		
		EunitCollectorMessage message=null;
		
		try {
			message=(EunitCollectorMessage) tmp.getContentObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(message!=null)
		{
			eunitAgentData.add(message);
			
			System.out.println(message.date+" Eunit: "+message.data.getHolonId()+" " + message.data.getLocation());
			
			if(SERIALIZE==true){
				try
				{
			      FileOutputStream out = new FileOutputStream("tmp_tests/CollectorData/"+message.toString()+System.currentTimeMillis()+".out");
			      ObjectOutputStream oos = new ObjectOutputStream(out);
			      oos.writeObject(message);
			      oos.flush();
			      oos.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		
		}
		System.out.println("Eunit agent messages count: " + eunitAgentData.size());

	}
	
	
	public void setSleepTimes(int infosleep,int eunitsleep,int distributorsleep)
	{
		this.EUNIT_SLEEP_TIME=eunitsleep;
		this.INFO_SLEEP_TIME=infosleep;
		this.DISTRIBUTOR_SLEEP_TIME=distributorsleep;
	}
	
	public void setPersistence(boolean set)
	{
		this.SERIALIZE=set;
	}

	
	
	public void GetDataFromDistributorAgent() {
		ACLMessage cfp = null;
		AID[] aids = CommunicationHelper.findAgentByServiceName(this,"CommissionService");

		logger.info("Collector Agent - collecting data from DistributorAgent");
		if (aids.length != 0) {

			for (int i = 0; i < aids.length; i++) {

				cfp = new ACLMessage(CommunicationHelper.DATA_COLLECTION_FROM_DISTRIBUTOR);
				cfp.addReceiver(aids[i]);
				try {
					cfp.setContentObject("");
				} catch (IOException e) {
					logger.error(getLocalName() + " - IOException "
							+ e.getMessage());
				}
				
				
				send(cfp);
			}
		
		
		
		}
		
	}

	public void dataFromDistributorAgentReceived(ACLMessage tmp) {
		
		
		DistributorCollectorMessage message=null;
		
		try {
			message=(DistributorCollectorMessage) tmp.getContentObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(message!=null)
		{
			distributorAgentData.add(message);
			
			
			if(SERIALIZE==true){
				try
				{
			      FileOutputStream out = new FileOutputStream("tmp_tests/CollectorData/"+message.toString()+System.currentTimeMillis()+".out");
			      ObjectOutputStream oos = new ObjectOutputStream(out);
			      oos.writeObject(message);
			      oos.flush();
			      oos.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		
		}
		System.out.println("Distributor agent messages count: " + distributorAgentData.size());

		
	}

	public void timestampChangeNotificationReceived(ACLMessage tmp) {
		Integer message=null;
		
		try {
			message=(Integer) tmp.getContentObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(message!=null)
		{
			if(SYNC==true)
			{
				GetDataFromDistributorAgent();
				GetDataFromEunitAgent();
				collectDataFromInfoAgent();
			}
			
		
		}
		
	}
	
}
