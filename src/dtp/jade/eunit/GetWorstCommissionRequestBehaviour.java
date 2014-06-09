package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.commission.Commission;
import dtp.jade.CommunicationHelper;

public class GetWorstCommissionRequestBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 4948182847977719740L;

    private ExecutionUnitAgent eunitAgent;

    public GetWorstCommissionRequestBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.WORST_COMMISSION_COST);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	try {
				eunitAgent.sendWorstCommissionCost((Commission)msg.getContentObject(), msg.getSender());
			} catch (UnreadableException e) {
				e.printStackTrace();
				System.exit(0);
			}
        } else {
            block();
        }
    }
}
