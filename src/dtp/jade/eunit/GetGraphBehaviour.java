package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.graph.Graph;
import dtp.jade.CommunicationHelper;

public class GetGraphBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 7206193333979034390L;

    private static Logger logger = Logger.getLogger(GetGraphBehaviour.class);

    private final ExecutionUnitAgent eunitAgent;

    public GetGraphBehaviour(ExecutionUnitAgent agent) {
        this.eunitAgent = agent;
    }

    public void action() {

        /*-------- RECIEVING GRAPH SECTION -------*/
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.GRAPH);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                Graph graph = (Graph) msg.getContentObject();

                logger.info(this.eunitAgent.getLocalName() + " - graph received");
                if (graph == null)
                    logger.info(this.eunitAgent.getLocalName() + " - graph is null");
                else
                    logger.info(this.eunitAgent.getLocalName() + " - graph is not null");

                eunitAgent.setGraph(graph);

            } catch (UnreadableException e) {
                logger.error(this.eunitAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }

        } else {

            block();
        }
    }
}
