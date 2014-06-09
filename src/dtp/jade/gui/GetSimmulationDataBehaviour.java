package dtp.jade.gui;

import xml.elements.SimmulationData;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.jade.CommunicationHelper;

/**
 * @author kony.pl
 */
public class GetSimmulationDataBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 1551427613144164700L;

    private GUIAgent guiAgent;

    public GetSimmulationDataBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.SIMMULATION_DATA);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            try {
				guiAgent.addSimmulationData((SimmulationData)msg.getContentObject());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}

        } else {

            block();
        }
    }
}
