package dtp.jade.crisismanager;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dtp.jade.CommunicationHelper;

public class EndOfSmiulationBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 2376188079027170331L;

    private CrisisManagerAgent crisisManagerAgent;

    public EndOfSmiulationBehaviour(CrisisManagerAgent agent) {

        this.crisisManagerAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIM_END);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {
        	crisisManagerAgent.simEnd();
        } else {

            block();
        }
    }

}
