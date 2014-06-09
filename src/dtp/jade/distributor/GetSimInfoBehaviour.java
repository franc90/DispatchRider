package dtp.jade.distributor;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.jade.CommunicationHelper;
import dtp.simmulation.SimInfo;

public class GetSimInfoBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -1055128511429775074L;

    private final DistributorAgent agent;

    public GetSimInfoBehaviour(DistributorAgent agent) {
        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_INFO);
        ACLMessage msg = myAgent.receive(template);

        SimInfo simConstrains;

        if (msg != null) {

            try {

                simConstrains = (SimInfo) msg.getContentObject();

                agent.setSimInfo(simConstrains); 
                
            } catch (UnreadableException e1) {
            	e1.printStackTrace();
            }

        } else {
            block();
        }
    }
}
