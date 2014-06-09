package dtp.simmulation;

import java.io.File;
import java.util.Calendar;

import javax.swing.Timer;

import dtp.commission.Commission;
import dtp.graph.Graph;
import dtp.jade.gui.CalendarStatsHolder;

public class Simmulation {

    File comsSetFile;

    Commission[] commissions;

    SimInfo simInfo;

    private Graph graph;

    private Timer timer;

    private CalendarStatsHolder calendarStats;

    private int nooneListSize;

    private long simStartTime;

    public Simmulation() {

        simStartTime = Calendar.getInstance().getTimeInMillis();
    }

    public void setComsSetFile(File file) {

        this.comsSetFile = file;
    }

    public File getComsSetFile() {

        return comsSetFile;
    }

    public void setGraph(Graph graph) {

        this.graph = graph;
    }

    public Graph getGraph() {

        return graph;
    }

    public void setCommissions(Commission[] coms) {

        this.commissions = coms;
    }

    public Commission[] getCommissions() {

        return commissions;
    }

    public void setSimInfo(SimInfo simInfo) {

        this.simInfo = simInfo;
    }

    public SimInfo getSimInfo() {

        return simInfo;
    }

    public void setTimer(Timer timer) {

        this.timer = timer;
    }

    public long getSimStartTime() {

        return simStartTime;
    }

    public Timer getTimer() {

        return timer;
    }

    public void simStart() {

        System.out.println("########## GOD ########## " + "timer.start()");

        timer.start();
    }

    public void simStop() {

        System.out.println("########## GOD ########## " + "timer.stop()");

        timer.stop();
    }

    public CalendarStatsHolder getCalendarStasHolder() {

        return calendarStats;
    }

    public void setCalendarStatsHolder(CalendarStatsHolder calendarStats) {

        this.calendarStats = calendarStats;
    }

    public int getNooneListSize() {

        return nooneListSize;
    }

    public void setNooneListSize(int nooneListSize) {

        this.nooneListSize = nooneListSize;
    }
}
