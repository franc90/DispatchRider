package dtp.jade.eunit;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;
import dtp.jade.agentcalendar.CalendarStats;
import dtp.jade.distributor.DistributorAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


public class GetCalendarStatsBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = -5847182259803791026L;

    private static Logger logger = Logger.getLogger(GetCalendarStatsBehaviour.class);

    private DistributorAgent agent;

    public GetCalendarStatsBehaviour(DistributorAgent agent) {

        this.agent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_MY_STATS);
        ACLMessage msg = myAgent.receive(template);

        CalendarStats calendarStats;

        if (msg != null) {

            try {

                calendarStats = (CalendarStats) msg.getContentObject();
                agent.addCalendarStats(calendarStats);

            } catch (UnreadableException e) {

                logger.error(this.agent.getLocalName() + " - UnreadableException " + e.getMessage());
            }

        } else {

            block();
        }
    }
}
