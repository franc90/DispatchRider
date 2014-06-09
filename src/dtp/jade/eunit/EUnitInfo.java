package dtp.jade.eunit;

import jade.core.AID;

import java.awt.geom.Point2D;
import java.io.Serializable;

import dtp.jade.agentcalendar.CalendarActionWithGraph;
import dtp.jade.agentcalendar.CalendarActionWithoutGraph;

public class EUnitInfo implements Serializable {

    private static final long serialVersionUID = 6611957729907620291L;

    private AID aid;

    private CalendarActionWithoutGraph currentCalendarActionWithoutGraph;

    private CalendarActionWithGraph currentCalendarActionWithGraph;

    private Point2D currentLocation;

    public EUnitInfo(AID aid) {

        this.aid = aid;
    }

    public AID getAID() {

        return aid;
    }

    public void setAID(AID aid) {

        this.aid = aid;
    }

    public CalendarActionWithoutGraph getCurrentCalendarActionWithoutGraph() {

        return currentCalendarActionWithoutGraph;
    }

    public void setCurrentCalendarActionWithoutGraph(CalendarActionWithoutGraph calendarAction) {

        this.currentCalendarActionWithoutGraph = calendarAction;
    }

    public CalendarActionWithGraph getCurrentCalendarActionWithGraph() {

        return currentCalendarActionWithGraph;
    }

    public void setCurrentCalendarActionWithGraph(CalendarActionWithGraph calendarAction) {

        this.currentCalendarActionWithGraph = calendarAction;
    }

    public Point2D getCurrentLocation() {

        return currentLocation;
    }

    public void setCurrentLocation(Point2D location) {

        this.currentLocation = location;
    }
}
