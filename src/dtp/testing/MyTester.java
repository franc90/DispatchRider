package dtp.testing;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import dtp.commission.CommissionHandler;
import dtp.graph.Graph;
import dtp.graph.GraphTrack;
import dtp.optimization.Astar;
import dtp.optimization.Dijkstra;
import dtp.optimization.SimulatedAnnealing;
import dtp.optimization.TrackFinder;
import dtp.xml.CommissionsParser;
import dtp.xml.CommissionsWriter;
import dtp.xml.GraphParser;
import dtp.xml.ParseException;

public class MyTester {

    private Graph graph;

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public MyTester(String graphFile) throws ParseException {
        GraphParser graphParser = new GraphParser();
        graph = graphParser.parse(graphFile);

    }

    @SuppressWarnings("unused")
    private CommissionHandler[] generateComms(String nameRoot, int howMany) {
        CommissionGenerator cg = new CommissionGenerator(graph, new Astar(graph));
        CommissionHandler[] comms = cg.generateCommissionArray(1000);
        CommissionsWriter cw = new CommissionsWriter(comms);
        cw.saveAsXmlFile(new File("xml/commissions/" + nameRoot + ".xml"));
        return comms;
    }

    public void testRouteFinder() throws ParseException {

        System.out.println("testRouteFinder()");

        String nameRoot = "mytest[" + System.currentTimeMillis() + "]";

        CommissionsParser cp = new CommissionsParser();
        cp.parse("xml/commissions/gaussian.xml");
        CommissionHandler comms[] = cp.getCommissions();
        int commLenth = comms.length;

        System.out.println("commissions:");
        for (int i = 0; i < comms.length; i++)
            comms[i].toString();

        HashMap<String, TrackFinder> finderMap = new HashMap<String, TrackFinder>();
        finderMap.put("astar", new Astar(graph));
        finderMap.put("dijkstra", new Dijkstra(graph));
        finderMap.put("sa", new SimulatedAnnealing());

        HashMap<String, Double> costs = new HashMap<String, Double>();
        HashMap<String, Long> timesInMillis = new HashMap<String, Long>();

        Iterator<String> sit = finderMap.keySet().iterator();
        while (sit.hasNext()) {
            String str = sit.next();
            Double cost = 0.0;
            TrackFinder finder = finderMap.get(str);
            long begin = System.currentTimeMillis();
            for (int commIt = 0; commIt < commLenth; commIt++) {
                GraphTrack tr = finder.findTrack(graph.getPointByCoordinates(
                        comms[commIt].getCommission().getPickupX(), comms[commIt].getCommission().getPickupY()), graph
                        .getPointByCoordinates(comms[commIt].getCommission().getDeliveryX(), comms[commIt]
                                .getCommission().getDeliveryY()));

                cost += graph.costFunction(tr);
            }
            timesInMillis.put(str, System.currentTimeMillis() - begin);
            costs.put(str, cost);
        }

        double values[][] = new double[3][3];
        GnuplotScriptGenerator gen = new GnuplotScriptGenerator();
        values[0][1] = 1;
        values[1][1] = 2;
        values[2][1] = 3;

        values[0][2] = costs.get("astar");
        values[1][2] = costs.get("dijkstra");
        values[2][2] = costs.get("sa");
        gen.setTitleOfDiagram("effectiveness of track finding algorithms");
        gen.setTitleX("algorithm: (1) astar, (2)dijkstra, (3)sa");
        gen.setTitleY("sum of costs for " + commLenth + " commissions");
        gen.setValues(values);
        gen.writeToFiles("gnuplot/effectiveness-" + nameRoot);

        values[0][2] = timesInMillis.get("astar");
        values[1][2] = timesInMillis.get("dijkstra");
        values[2][2] = timesInMillis.get("sa");
        gen.setTitleOfDiagram("duration (msec) of track finding algorithms");
        gen.setTitleX("algorithm: (1) astar, (2)dijkstra, (3)sa");
        gen.setTitleY("sum of durations for " + commLenth + " commissions");
        gen.setValues(values);
        gen.writeToFiles("gnuplot/duration-" + nameRoot);
    }

    @SuppressWarnings("unused")
    private static int individuals(Object[] tabulka) {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < tabulka.length; i++)
            if (!set.contains(tabulka[i].toString()))
                set.add(tabulka[i].toString());
        return set.size();
    }

    @SuppressWarnings("unused")
    private static String[] getIndividualsStringSet(Object[] tabulka) {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < tabulka.length; i++)
            if (!set.contains(tabulka[i].toString()))
                set.add(tabulka[i].toString());
        return set.toArray(new String[set.size()]);
    }

    @SuppressWarnings("unused")
    private static boolean allIdentical(Object[] a) {
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++)
                if (!a[i].toString().equals(a[j].toString()))
                    return false;
        return true;
    }

    private static Logger logger = Logger.getLogger(MyTester.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("conf/Log4j.properties");

        TrackFindersTester tester = new TrackFindersTester();
        tester.test();

        // Graph graph = GraphGenerator.generate(10, 0.5, 10, 250, 20, 300, 0.5);
        // logger.info(graph.getPointsSize());

        try {
            // MyTester tester = new MyTester("xml/maps/polska.xml");

            // tester.testRouteFinder();
            /*
             * Commission c = new Commission(1, 35, 56, 1, 100, 94, 23, 50, 150, 25, 10); CommissionHandler ch = new
             * CommissionHandler(c, 1); CommissionsWriter cw = new CommissionsWriter(); cw.addCommission(ch);
             * cw.saveAsXmlFile(new File("xml/commissions/single.xml"));
             * 
             * 
             * FailuresParser fp = new FailuresParser(); fp.parse("xml/failures/regular10.xml"); Failure fails[] =
             * fp.getFailures(); logger.info("chtab.length="+fails.length);
             */
            // CommissionsWriter cw = new CommissionsWriter(comms);
            // cw.saveAsXmlFile(new File("xml/commissions/mixed.xml"));
            /*
             * 
             * 
             * 
             * FailureGenerator fg = new FailureGenerator(10, 20, 30, new Regular()); FailuresWriter fw = new
             * FailuresWriter(fg.generateFailureArray(10, 100, 15)); fw.saveAsXmlFile(new
             * File("xml/failures/regular10.xml"));
             * 
             * 
             * JamsParser jp = new JamsParser(tester.graph); jp.parse("xml/jams/regular10.xml"); Jam[] jams =
             * jp.getCommissions();
             * 
             * JamGenerator jg = new JamGenerator(1, 1000, 10, Integer.MAX_VALUE, tester.graph, new Regular()); Jam
             * jtab[] = jg.generateJamArray(10); JamsWriter jw = new JamsWriter(tester.graph,jtab); jw.saveAsXmlFile(new
             * File("xml/jams/regular10.xml"));
             * 
             * CommissionsParser cp = new CommissionsParser(); cp.parse("xml/commissions/single.xml");
             * CommissionHandler[] chtab = cp.getCommissions(); logger.info("chtab.length="+chtab.length);
             * 
             * CommissionHandler comms[] = tester.generateComms("test", 10);
             * 
             * Dijkstra dij = new Dijkstra(tester.graph); for(int i=0 ; i<10 ; i++){
             * 
             * Point from = tester.graph.getPointByCoordinates( comms[i].getCommission().getPickupX(),
             * comms[i].getCommission().getPickupY()); Point to = tester.graph.getPointByCoordinates(
             * comms[i].getCommission().getDeliveryX(), comms[i].getCommission().getDeliveryY());
             * System.out.println(from.toString()+" -> "+to.toString()); dij.findTrack(from,to).showTrack(); }
             * 
             * //tester.testRouteFinder();
             * 
             * 
             * 
             * 
             * 
             * System.out.println(comms.length);
             * 
             * 
             * 
             * Distribution dist = new Gaussian(30, 10);
             * 
             * JamGenerator jg = new JamGenerator(1, 1000, 1, 100, tester.graph, dist); Jam jams[] =
             * jg.generateJamArray(100); System.out.println(allIdentical(jams));
             * 
             * FailureGenerator fg = new FailureGenerator(); fg.setDist(dist); fg.setNumberOfDrivers(10);
             * fg.setNumberOfTrailers(20); fg.setNumberOfTrucks(30); Failure fail[] = fg.generateFailureArray(1, 1000,
             * 100); System.out.println(allIdentical(fail));
             */
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("MyTester.main(): ");
            logger.error(ex);
        }
    }
}
