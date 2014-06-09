package dtp.jade.crisismanager.crisisevents;

import jade.core.AID;
import dtp.jade.CommunicationHelper;
import dtp.jade.crisismanager.CrisisManagerAgent;

public class EUnitFailureEventSolver {

    private EUnitFailureEvent event;

    private CrisisManagerAgent CMAgent;

    public EUnitFailureEventSolver(EUnitFailureEvent event, CrisisManagerAgent agent) {

        this.event = event;
        CMAgent = agent;
    }

    public EUnitFailureEvent getEvent() {

        return event;
    }

    public void solve() {

        sendVehicleFailureInfoToEUnits();
    }

    private void sendVehicleFailureInfoToEUnits() {

        AID[] aids;

        aids = CommunicationHelper.findAgentByServiceName(CMAgent, "ExecutionUnitService");

        for (int i = 0; i < aids.length; i++) {

            CMAgent.sentCrisisEvent(aids[i], CommunicationHelper.CRISIS_EVENT, event);
        }
    }
}
