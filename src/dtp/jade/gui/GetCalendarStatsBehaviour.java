package dtp.jade.gui;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;
import dtp.jade.agentcalendar.CalendarStats;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * @author KONY
 */
public class GetCalendarStatsBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -5847182259803791026L;

    private static Logger logger = Logger.getLogger(GetCalendarStatsBehaviour.class);

    private GUIAgent guiAgent;

    public GetCalendarStatsBehaviour(GUIAgent agent) {

        this.guiAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_MY_STATS);
        ACLMessage msg = myAgent.receive(template);

        CalendarStats calendarStats;

        if (msg != null) {

            try {

                calendarStats = (CalendarStats) msg.getContentObject();
                guiAgent.addCalendarStats(calendarStats);

            } catch (UnreadableException e) {

                logger.error(this.guiAgent.getLocalName() + " - UnreadableException " + e.getMessage());
            }

        } else {

            block();
        }
    }
}
