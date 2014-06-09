package dtp.jade.eunit.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.EUnitInitialData;
import dtp.jade.eunit.ExecutionUnitAgent;

public class GetInitialDataBehaviour extends CyclicBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -8392984544272799823L;
    private ExecutionUnitAgent eUnit;

    public GetInitialDataBehaviour(ExecutionUnitAgent eUnit) {
        this.eUnit = eUnit;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_INITIAL_DATA);
        ACLMessage msg = eUnit.receive(template);
        if (msg != null) {
            try {
                EUnitInitialData initialData = (EUnitInitialData) msg.getContentObject();
                eUnit.setInitialData(initialData);
            } catch (UnreadableException e) {
            }
        } else {
            block();
        }

    }

}
