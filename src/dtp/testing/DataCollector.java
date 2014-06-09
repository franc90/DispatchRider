package dtp.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class DataCollector {

    private static Logger logger = Logger.getLogger(DataCollector.class);
    private int maxHolons, sumTime, sumCost, updateCounter;
    private HashMap<String, ArrayList<Integer>> costs, times;

    public DataCollector() {
        super();
        maxHolons = -1;
        sumCost = 0;
        sumTime = 0;
        updateCounter = 0;
        costs = new HashMap<String, ArrayList<Integer>>();
        times = new HashMap<String, ArrayList<Integer>>();
    }

    public void update(String reason, int cost, int time, int numberOfHolons) {
        if (reason == null)
            reason = "";
        if (!this.costs.containsKey(reason))
            this.costs.put(reason, new ArrayList<Integer>());
        if (!this.times.containsKey(reason))
            this.times.put(reason, new ArrayList<Integer>());
        if (cost > 0)
            this.costs.get(reason).add(cost);
        if (time > 0)
            this.times.get(reason).add(time);
        sumCost += cost;
        sumTime += time;
        if (numberOfHolons > maxHolons)
            maxHolons = numberOfHolons;
        updateCounter++;
        logger.info("New cost reported, reason: " + reason + ", cost: " + cost + ", time: " + time + ", holons: "
                + numberOfHolons);
        logger.info("Sum of costs: " + sumCost + ", time: " + time + ", max holons: " + numberOfHolons + " from "
                + updateCounter + " updates.");
    }

    public void update(String reason, int cost, int time) {
        update(reason, cost, time, -1);
    }

    public int getMaxHolons() {
        return maxHolons;
    }

    public int getSumCost() {
        return sumCost;
    }

    public int getSumTime() {
        return sumTime;
    }

    public int getUpdateCounter() {
        return updateCounter;
    }

    public int costSize() {
        return costs.size();
    }

    public int timeSize() {
        return times.size();
    }

    /**
     * @return
     */
    public String[] getCosts() {
        ArrayList<String> result = new ArrayList<String>();
        Iterator<String> sit = costs.keySet().iterator();
        while (sit.hasNext()) {
            String str = sit.next();
            int size = costs.get(str).size();
            int cost = 0;
            for (int i = 0; i < size; i++)
                cost += costs.get(str).get(i);
            result.add("Title: " + str + ", number of updates: " + size + ", sum of costs: " + cost);
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * @return
     */
    public String[] getTimes() {
        ArrayList<String> result = new ArrayList<String>();
        Iterator<String> sit = times.keySet().iterator();
        while (sit.hasNext()) {
            String str = sit.next();
            int size = times.get(str).size();
            int time = 0;
            for (int i = 0; i < size; i++)
                time += times.get(str).get(i);
            result.add("Title: " + str + ", number of updates: " + size + ", sum of times: " + time);
        }
        return result.toArray(new String[result.size()]);

    }

    public String toString() {
        String numberOfHolons;
        if (maxHolons < 0)
            numberOfHolons = "unknown number of";
        else
            numberOfHolons = "max " + maxHolons;
        return "Summary from " + updateCounter + " updates: cost=" + sumCost + ", time=" + sumTime + "with "
                + numberOfHolons + " holons";
    }
}
