package dtp.jade.dataCollectorAgent.agent.behaviour;

import dtp.jade.dataCollector.agent.*;
import jade.core.behaviours.CyclicBehaviour;

public class GetDataFromInfoAgentBehaviour extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2710745755697441794L;
	private DataCollectionAgent dcAgent;
	
	private int SLEEP_TIME;
	
	public GetDataFromInfoAgentBehaviour(DataCollectionAgent dcAgent,int SLEEP_TIME)
	{
		this.dcAgent=dcAgent;
		this.SLEEP_TIME=SLEEP_TIME;
	}
	
	
	@Override
	public void action() {
		
		dcAgent.collectDataFromInfoAgent();
		
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


}
