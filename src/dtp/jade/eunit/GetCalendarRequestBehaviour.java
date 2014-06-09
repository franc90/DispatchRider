package dtp.jade.eunit;

import dtp.jade.CommunicationHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author KONY
 */
public class GetCalendarRequestBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 8689655268845736964L;

    private final ExecutionUnitAgent executionUnitAgent;

    public GetCalendarRequestBehaviour(ExecutionUnitAgent agent) {
        this.executionUnitAgent = agent;
    }

    public void action() {

        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_SHOW_CALENDAR);
        ACLMessage msg = myAgent.receive(template);

        if (msg != null) {

            executionUnitAgent.sendCalendar();

        } else {
            block();
        }
    }
}
