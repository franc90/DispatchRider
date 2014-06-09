package dtp.jade.transport;

import jade.core.AID;

import java.io.Serializable;

/**
 * Commission data for transport element
 * 
 * @author Michal Golacki
 */
public class TransportCommission implements Serializable {

    /**
     * Generated serial version uid
     */
    private static final long serialVersionUID = -7280735114056344523L;

    /**
     * Id of sender of this commission
     */
    AID senderId;

    /**
     * Load to be carried
     */
    int load;

    /**
     * @return the senderId
     */
    public AID getSenderId() {
        return senderId;
    }

    /**
     * @param senderId
     *        the senderId to set
     */
    public void setSenderId(AID senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the load
     */
    public int getLoad() {
        return load;
    }

    /**
     * @param load
     *        the load to set
     */
    public void setLoad(int load) {
        this.load = load;
    }

}
