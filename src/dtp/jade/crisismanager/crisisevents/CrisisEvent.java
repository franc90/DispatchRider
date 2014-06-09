package dtp.jade.crisismanager.crisisevents;

import java.io.Serializable;

public abstract class CrisisEvent implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 367414131396890673L;

	private int eventID;

    private int eventTime;

    public int getEventID() {

        return eventID;
    }

    public void setEventID(int id) {

        eventID = id;
    }

    public int getEventTime() {

        return eventTime;
    }

    public void setEventTime(int time) {

        eventTime = time;
    }

    public abstract String getEventType();

    public abstract String toString();
}
