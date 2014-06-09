package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;

import dtp.graph.Graph;
import dtp.jade.CommunicationHelper;

public class GetGraphUpdateBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 5738306218037902738L;

    private static Logger logger = Logger.getLogger(GetGraphBehaviour.class);

    private final ExecutionUnitAgent eunitAgent;

    public GetGraphUpdateBehaviour(ExecutionUnitAgent agent) {
        this.eunitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.GRAPH_UPDATE);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
                Graph graph = (Graph) msg.getContentObject();

                logger.info(this.eunitAgent.getLocalName() + " - graph update received");

                eunitAgent.updateGraph(graph);

            } catch (UnreadableException e) {
                logger.error(this.eunitAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }

        } else {

            block();
        }
    }
}
