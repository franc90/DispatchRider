package dtp.jade.dataCollectorAgent.agent.behaviour;

import dtp.jade.dataCollector.agent.DataCollectionAgent;
import jade.core.behaviours.CyclicBehaviour;

public class GetDataFromDistributorBehaviour extends CyclicBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -106814159993678971L;
	
	
	private DataCollectionAgent dcAgent;
	
	private int SLEEP_TIME;
	
	public GetDataFromDistributorBehaviour(DataCollectionAgent dcAgent,int SLEEP_TIME)
	{
		this.dcAgent=dcAgent;
		this.SLEEP_TIME=SLEEP_TIME;
	}
	
	
	@Override
	public void action() {
		
		dcAgent.GetDataFromDistributorAgent();
		
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
