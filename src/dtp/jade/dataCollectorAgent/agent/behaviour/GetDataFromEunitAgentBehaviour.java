package dtp.jade.dataCollectorAgent.agent.behaviour;

import dtp.jade.dataCollector.agent.DataCollectionAgent;
import jade.core.behaviours.CyclicBehaviour;

public class GetDataFromEunitAgentBehaviour extends CyclicBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1376126415918876395L;

	private DataCollectionAgent dcAgent;
	
	private int SLEEP_TIME;
	
	public GetDataFromEunitAgentBehaviour(DataCollectionAgent dcAgent,int SLEEP_TIME)
	{
		this.dcAgent=dcAgent;
		this.SLEEP_TIME=SLEEP_TIME;
	}
	
	
	@Override
	public void action() {
		
		dcAgent.GetDataFromEunitAgent();
		
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
