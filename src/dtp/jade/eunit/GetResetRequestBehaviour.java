package dtp.jade.eunit;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetResetRequestBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -8146818420592279797L;

    private static Logger logger = Logger.getLogger(GetResetRequestBehaviour.class);

    private ExecutionUnitAgent eunitAgent;

    public GetResetRequestBehaviour(ExecutionUnitAgent agent) {

        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.RESET);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            logger.info(myAgent.getLocalName() + " - RESET requested");

            eunitAgent.resetAgent();

        } else {
            block();
        }
    }
}
