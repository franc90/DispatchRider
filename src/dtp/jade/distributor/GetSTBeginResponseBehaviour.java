package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class GetSTBeginResponseBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -1055128511429775074L;

    private final DistributorAgent agent;

    public GetSTBeginResponseBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.ST_BEGIN);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            agent.STBeginResponse();

        } else {
            block();
        }
    }
}
