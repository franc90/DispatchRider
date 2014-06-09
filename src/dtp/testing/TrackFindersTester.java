package dtp.testing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.optimization.Astar;
import dtp.optimization.Dijkstra;
import dtp.optimization.SimulatedAnnealing;
import dtp.optimization.TrackFinder;

public class TrackFindersTester {

    private static final Logger logger = Logger.getLogger(TrackFindersTester.class);

    public static final String chartsOutputPath = "charts/trackFinder/";

    private HashMap<TrackFinder, String> finders = new HashMap<TrackFinder, String>();

    public TrackFindersTester() {
        super();

        finders.put(new Astar(null), "A*");
        finders.put(new Dijkstra(null), "Dijkstra");
        finders.put(new SimulatedAnnealing(10, 25, 0.5, 0.5), "Simulated Annealing");
        /**/
        // public SimulatedAnnealing(int kmax, int imax, double c, double alfa)
        /*
         * temperatury: 10, pocz¹tkowa: 0.5, nastêpna = 0.5 poprzednia
         * 
         * finders.put(new SimulatedAnnealing(10,3,0.5,0.5), " 3 iter./temp."); finders.put(new
         * SimulatedAnnealing(10,10,0.5,0.5), "10 iter./temp."); finders.put(new SimulatedAnnealing(10,25,0.5,0.5),
         * "25 iter./temp.");
         * 
         * finders.put(new SimulatedAnnealing(10,3,0.5,0.5), " 3 iter./temp."); finders.put(new
         * SimulatedAnnealing(10,10,0.5,0.5), "10 iter./temp."); finders.put(new SimulatedAnnealing(10,25,0.5,0.5),
         * "25 iter./temp.");
         * 
         * @param imax - quantity of temperatures (main iterations) @param kmax - quantity of iterations for a single
         * temperature value @param c - initial temperature: 0 ? c ? 1 @param alfa - // temperature is increased
         * alfa-times (c=calfa)
         * 
         * 
         * 
         * finders.put(new SimulatedAnnealing( 3,10,0.5,0.5), "SA(3, 10, 0.5,
         * 0.5)"); finders.put(new SimulatedAnnealing(10,10,0.5,0.5), "SA(10, 10, 0.5, 0.5)"); finders.put(new
         * SimulatedAnnealing(25,10,0.5,0.5), "SA(25, 10, 0.5, 0.5)");
         * 
         * finders.put(new SimulatedAnnealing(10,10,0.5,0.5), "SA(10, 3, 0.5,
         * 0.5)"); finders.put(new SimulatedAnnealing(10,10,0.5,0.5), "SA(10, 25, 0.5, 0.5)"); finders.put(new
         * SimulatedAnnealing(10,10,0.5,0.5), "SA(10, 10, 0.2, 0.5)"); finders.put(new
         * SimulatedAnnealing(10,10,0.5,0.5), "SA(10, 10, 1, 0.5)"); finders.put(new SimulatedAnnealing(10,10,0.5,0.5),
         * "SA(10, 10, 0.5, 0.2)"); finders.put(new SimulatedAnnealing(10,10,0.5,0.5), "SA(10, 10, 0.5, 1)");
         */
    }

    public void add(TrackFinder finder, String name) {
        this.finders.put(finder, name);
    }

    public void remove(TrackFinder finder) {
        this.finders.remove(finder);
    }

    public void clear() {
        this.finders.clear();
    }

    public Iterator<TrackFinder> iterator() {
        return this.finders.keySet().iterator();
    }

    public int size() {
        return this.finders.size();
    }

    public String getTimeString() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        String DATE_FORMAT = "yyyy-MM-dd_HH.mm.ss";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);

        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(cal.getTime());
    }

    private void setPoints(Graph graph, GraphPoint[] sources, GraphPoint[] targets) {
        ArrayList<GraphPoint> all = new ArrayList<GraphPoint>();

        Iterator<GraphPoint> pit = graph.getPointsIterator();
        while (pit.hasNext())
            all.add(pit.next());

        Random random = new Random(831210 + System.currentTimeMillis());
        for (int s = 0; s < sources.length; s++)
            sources[s] = all.get(random.nextInt(all.size()));

        for (int t = 0; t < targets.length; t++)
            do {
                targets[t] = all.get(random.nextInt(all.size()));
            } while (targets[t].equals(sources[t]));
    }

    public void plotBySize(int minSize, int maxSize, int plusSize, double linksRatio) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        HashMap<TrackFinder, XYSeries> costs = new HashMap<TrackFinder, XYSeries>(), effectiveness = new HashMap<TrackFinder, XYSeries>();
        XYSeries AstarEffectiveness = new XYSeries("Astar");

        Iterator<TrackFinder> fit = finders.keySet().iterator();
        while (fit.hasNext()) {
            TrackFinder finder = fit.next();
            String name = finders.get(finder);
            costs.put(finder, new XYSeries(name));
            if (!finder.getClass().equals(Astar.class))
                effectiveness.put(finder, new XYSeries(name));
        }

        for (int size = minSize; size <= maxSize; size += plusSize) {
            Graph graph = null;
            int limit = 1000, index = 0;
            do {
                graph = GraphGenerator.generate(size, linksRatio, 0, 100, 0, 100, 1);
            } while (!graph.isConsistant() && limit > index++);
            if (index >= limit) {
                logger.error("index>=limit for size: " + size);
                return;
            }
            int tracks = 50;
            GraphPoint[] sources = new GraphPoint[tracks], targets = new GraphPoint[tracks];
            setPoints(graph, sources, targets);
            fit = finders.keySet().iterator();
            while (fit.hasNext()) {
                TrackFinder finder = fit.next();
                finder.setGraph(graph);
                ArrayList<GraphTrack> results = new ArrayList<GraphTrack>();
                long begin = System.currentTimeMillis();
                for (int t = 0; t < tracks; t++)
                    results.add(finder.findTrack(sources[t], targets[t]));
                if (finder.getClass().equals(Astar.class))
                    AstarEffectiveness.add(size, (System.currentTimeMillis() - begin) / tracks);
                else
                    effectiveness.get(finder).add(size, (System.currentTimeMillis() - begin) / tracks);
                double cost = 0;
                Iterator<GraphTrack> trIt = results.iterator();
                while (trIt.hasNext())
                    cost += graph.costFunction(trIt.next());
                costs.get(finder).add(size, cost / tracks);
            }
        }

        String timeString = getTimeString();

        Iterator<XYSeries> sit = costs.values().iterator();
        while (sit.hasNext())
            dataset.addSeries(sit.next());

        JFreeChart chart = ChartFactory
                .createXYLineChart(
                        "koszty w zale¿noœci od rozmiaru grafu, \ntemperatury: 10, temp.pocz. 0.5, nastêpna = 0.5 * poprzednia",
                        "iloœæ punktów w grafie", "wartoœæ funkcji kosztu", dataset, PlotOrientation.VERTICAL, true,
                        true, false);
        String filename = chartsOutputPath + timeString + "_cost_by_size.jpg";
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException e) {
            logger.error("error while saving " + filename + e);
        }

        dataset.removeAllSeries();

        sit = effectiveness.values().iterator();
        while (sit.hasNext())
            dataset.addSeries(sit.next());

        chart = ChartFactory
                .createXYLineChart(
                        "czas wyszukiwania w zale¿noœci od rozmiaru grafu, \ntemperatury: 10, temp.pocz. 0.5, nastêpna = 0.5 * poprzednia",
                        "iloœæ punktów w grafie", "czas wyszukiwania trasy [ms]", dataset, PlotOrientation.VERTICAL,
                        true, true, false);
        filename = chartsOutputPath + timeString + "_effectiveness_by_size.jpg";
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException e) {
            logger.error("error while saving " + filename + e);
        }

        dataset.removeAllSeries();

        dataset.addSeries(AstarEffectiveness);

        chart = ChartFactory.createXYLineChart("czas wyszukiwania w zale¿noœci od rozmiaru grafu",
                "iloœæ punktów w grafie", "czas wyszukiwania trasy [ms]", dataset, PlotOrientation.VERTICAL, true,
                true, false);
        filename = chartsOutputPath + timeString + "_effectiveness_by_size_astar.jpg";
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException e) {
            logger.error("error while saving " + filename + e);
        }

    }

    public void plotByLinksRatio(double minRatio, double maxRatio, double plusRatio, int size) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        HashMap<TrackFinder, XYSeries> costs = new HashMap<TrackFinder, XYSeries>(), effectiveness = new HashMap<TrackFinder, XYSeries>();
        XYSeries AstarEffectiveness = new XYSeries("Astar");

        Iterator<TrackFinder> fit = finders.keySet().iterator();
        while (fit.hasNext()) {
            TrackFinder finder = fit.next();
            String name = finders.get(finder);
            costs.put(finder, new XYSeries(name));
            if (!finder.getClass().equals(Astar.class))
                effectiveness.put(finder, new XYSeries(name));
        }

        for (double ratio = minRatio; ratio <= maxRatio + 0.001; ratio += plusRatio) {

            Graph graph = null;
            int limit = 1000, index = 0;
            do {
                graph = GraphGenerator.generate(size, ratio, 0, 100, 0, 100, 1);
            } while (!graph.isConsistant() && limit > index++);
            if (index >= limit) {
                logger.error("index>=limit for size: " + size);
                return;
            }

            int tracks = 50;
            GraphPoint[] sources = new GraphPoint[tracks], targets = new GraphPoint[tracks];
            setPoints(graph, sources, targets);
            fit = finders.keySet().iterator();
            while (fit.hasNext()) {
                TrackFinder finder = fit.next();
                finder.setGraph(graph);
                ArrayList<GraphTrack> results = new ArrayList<GraphTrack>();
                long begin = System.currentTimeMillis();
                for (int t = 0; t < tracks; t++)
                    results.add(finder.findTrack(sources[t], targets[t]));
                if (finder.getClass().equals(Astar.class))
                    AstarEffectiveness.add(ratio, (System.currentTimeMillis() - begin) / tracks);
                else
                    effectiveness.get(finder).add(ratio, (System.currentTimeMillis() - begin) / tracks);
                double cost = 0;
                Iterator<GraphTrack> trIt = results.iterator();
                while (trIt.hasNext())
                    cost += graph.costFunction(trIt.next());
                costs.get(finder).add(ratio, cost / tracks);
            }
        }

        String timeString = getTimeString();

        Iterator<XYSeries> sit = costs.values().iterator();
        while (sit.hasNext())
            dataset.addSeries(sit.next());

        JFreeChart chart = ChartFactory.createXYLineChart(
                "koszty w zale¿noœci od gêstosci ³¹cz, \ntemperatury: 10, temp.pocz. 0.5, nastêpna = 0.5 * poprzednia",
                "gêstoœæ ³¹cz", "wartoœæ funkcji kosztu", dataset, PlotOrientation.VERTICAL, true, true, false);

        String filename = chartsOutputPath + timeString + "_cost_by_ratio.jpg";
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException e) {
            logger.error("error while saving " + filename + e);
        }

        dataset.removeAllSeries();

        sit = effectiveness.values().iterator();
        while (sit.hasNext())
            dataset.addSeries(sit.next());

        chart = ChartFactory
                .createXYLineChart(
                        "czas wyszukiwania w zale¿noœci od gêstosci ³¹cz, \ntemperatury: 10, temp.pocz. 0.5, nastêpna = 0.5 * poprzednia",
                        "gêstoœæ ³¹cz", "czas wyszukiwania trasy [ms]", dataset, PlotOrientation.VERTICAL, true, true,
                        false);
        filename = chartsOutputPath + timeString + "_effectiveness_by_ratio.jpg";
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException e) {
            logger.error("error while saving " + filename + e);
        }

        dataset.removeAllSeries();

        dataset.addSeries(AstarEffectiveness);

        chart = ChartFactory.createXYLineChart("czas wyszukiwania w zale¿noœci od gêstosci ³¹cz", "gêstoœæ ³¹cz",
                "czas wyszukiwania trasy [ms]", dataset, PlotOrientation.VERTICAL, true, true, false);
        filename = chartsOutputPath + timeString + "_effectiveness_by_size_astar.jpg";
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, 500, 300);
        } catch (IOException e) {
            logger.error("error while saving " + filename + e);
        }

    }

    public void test() {
        plotByLinksRatio(0.30, 1, 0.1, 50);
        plotBySize(10, 100, 10, 0.4);
    }
}
