package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class EndOfSimulationBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 8689655268845736964L;

    private final ExecutionUnitAgent executionUnitAgent;

    public EndOfSimulationBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_END);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	executionUnitAgent.doDelete();

        } else {
            block();
        }
    }

}
