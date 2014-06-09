package dtp.jade.eunit;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Map;

import org.apache.log4j.Logger;

import dtp.jade.CommunicationHelper;
import dtp.jade.transport.TransportOffer;

public class GetTransportOfferBahaviour extends CyclicBehaviour {

    /**
     * Generated serial version uid
     */
    private static final long serialVersionUID = 5029762940514560945L;

    private static Logger logger = Logger.getLogger(GetTransportOfferBahaviour.class);

    private ExecutionUnitAgent agent;

    public GetTransportOfferBahaviour(ExecutionUnitAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_OFFER);
        ACLMessage msg = agent.receive(template);

        TransportOffer offer = null;

        if (msg != null) {

            try {

                offer = (TransportOffer) msg.getContentObject();
                boolean end = false;
                if (agent.getAuction() != null) {
                    end = agent.getAuction().addOffer(offer);
                } else {
                    logger.fatal("AUCTION IS NULL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }

                // logger.info("got offer " + offer.getRatio());
                if (end) {

                    doTheMonkeyBussines();
                    logger.info("it is final offer");
                    TransportOffer[] offers = doTheMonkeyBussines();
                    if (offers != null) {

                        if (agent.getDriver() != null) {
                            TransportOffer[] backupTeam = new TransportOffer[3];
                            backupTeam[0] = agent.getDriver();
                            backupTeam[1] = agent.getTruck();
                            backupTeam[2] = agent.getTrailer();
                            agent.setBackupTeam(backupTeam);
                        }

                        if(offers[0]==null || offers[1]==null || offers[2]==null) {
                            agent.sendOfferToDistributor(new EUnitOffer(agent.getAID(), -1.0,0));
                     
                        } else {
                        agent.setDriver(offers[0]);
                        agent.setTruck(offers[1]);
                        agent.setTrailer(offers[2]);
                       
                        //MODIFICATION BY LP
                        if(agent.getTrailer()!=null && offers[2]!=null && offers[2].getTransportElementData() !=null) {
                        	agent.setMaxLoad(offers[2].getTransportElementData().getCapacity());
                        }else {
                        	agent.sendOfferToDistributor(new EUnitOffer(agent.getAID(), -1.0,0));
                        }
                        //END OF MODIFICATION

                        agent.checkNewCommission(agent.getAuction().getCommission());
                    }} else {
                        agent.sendOfferToDistributor(new EUnitOffer(agent.getAID(), -1.0,0));
                    }

                }
            } catch (UnreadableException e1) {
                logger.error(this.agent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }
        } else {
            block();
        }
    }

    private TransportOffer[] doTheMonkeyBussines() {
        Map<Integer, TransportOffer[]> teams = agent.getAuction().getBestTeams();
        return teams.get(agent.getInitialData().getDepot());
    }

}
