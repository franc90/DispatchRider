package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetInfoRequestBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 8177056328533300593L;

    private ExecutionUnitAgent eunitAgent;

    public GetInfoRequestBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_SEND_INFO);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            eunitAgent.sendInfo();

        } else {
            block();
        }
    }
}
