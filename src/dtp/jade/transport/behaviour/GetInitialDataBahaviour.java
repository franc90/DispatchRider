package dtp.jade.transport.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.apache.log4j.Logger;


import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportAgent;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;

/**
 * Behaviour for receiving initial data for transport element.
 * 
 * @author Michal Golacki
 */
public class GetInitialDataBahaviour extends CyclicBehaviour {

    /**
	 * 
	 */
    private static final long serialVersionUID = 9000853210256277234L;

    /** Logger */
    private static Logger logger = Logger.getLogger(GetInitialDataBahaviour.class);

    /** Agent */
    private TransportAgent agent;

    public GetInitialDataBahaviour(TransportAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_INITIAL_DATA);
        ACLMessage message = agent.receive(template);

        if (message != null) {
            try {
                TransportElementInitialData data = (TransportElementInitialData) message.getContentObject();
                
                agent.setTransportElementInitialData(data);
                
                
                String loggerMessage = agent.getName() + " initial values set to"
                			+ " DefaultCapacity: " + data.getDefaultCapacity() + " Depot: " + data.getDepot();
                
                switch (agent.getTransportType()){
                	case TRUCK:
                		TransportElementInitialDataTruck dataTruck = (TransportElementInitialDataTruck)data;
                		loggerMessage += " Reliability: " + dataTruck.getReliability();
                		loggerMessage += " Comfort: " + dataTruck.getComfort();
                		loggerMessage += " FuelConsuption: " + dataTruck.getFuelConsumption();
                	break;
                	
                	case TRAILER:
                		TransportElementInitialDataTrailer dataTrailer = (TransportElementInitialDataTrailer)data;
                		loggerMessage += " CargoType: " + dataTrailer.getCargoType();
                		loggerMessage += " ConnectorType: " + dataTrailer.getConnectorType();
                		loggerMessage += " Mass: " + dataTrailer.getMass();
                	break;
                	
                }
                logger.info(loggerMessage);
                
            } catch (UnreadableException e) {
                logger.error("Unreadable message exception " + e.getStackTrace());
            }
        } else {
            block();
        }

    }

}
