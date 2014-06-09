package dtp.jade.gui;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * @author KONY
 */
public class GetCalendarBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 5691205170181475287L;

    /** Logger. */
    private static Logger logger = Logger.getLogger(GetCalendarBehaviour.class);

    private GUIAgent guiAgent;

    public GetCalendarBehaviour(GUIAgent agent) {
        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_MY_CALENDAR);
        ACLMessage msg = myAgent.receive(template);

        String calendar;

        if (msg != null) {

            try {
                calendar = (String) msg.getContentObject();
                guiAgent.addCalendar(msg.getSender().getLocalName(), calendar);

            } catch (UnreadableException e) {
                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }
        } else {
            block();
        }
    }
}
