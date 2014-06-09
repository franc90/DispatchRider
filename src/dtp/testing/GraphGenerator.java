package dtp.testing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;

public abstract class GraphGenerator {

    /**
     * Generates new graph and it's points and links
     * 
     * @param points
     *        number of points to be generateds
     * @param linksRatio
     *        existing/possible links ratio. <=0 no links, (0,1) partly filled graph, >=1 complete graph
     * @param xmin
     *        left border of map
     * @param xmax
     *        right border of map
     * @param ymin
     *        bottom of map
     * @param ymax
     *        top of map
     * @param costToTime
     *        cost to time (distance) ratio
     * @return graph with given number points and links
     */
    public static Graph generate(int points, double linksRatio, int xmin, int xmax, int ymin, int ymax,
            double costToTime) {

        Graph graph = new Graph(false);

        int minSpaceRange = (int) (distance(xmin, ymin, xmax, ymax) / Math.sqrt(points) / 10);

        ArrayList<GraphPoint> pointList = new ArrayList<GraphPoint>();

        for (int p = 0; p < points; p++)
            pointList.add(generatePoint(graph, minSpaceRange));

        Iterator<GraphPoint> pit = pointList.iterator();
        while (pit.hasNext()) {
            GraphPoint point = pit.next();
            int howMany = (int) (graph.getPointsSize() * linksRatio);
            setLinksIn(graph, point, getPoints(graph, howMany), costToTime);
            setLinksOut(graph, point, getPoints(graph, howMany), costToTime); // other
            // random
            // set,
            // has
            // tha
            // same
            // size
            // like
            // linksIn
            // but
            // points
            // may
            // be
            // different
        }

        return graph;
    }

    /**
     * Distance in Euclidean space between a (ax, ay) & b (bx, by)
     * 
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @return Distance in Euclidean space between a (ax, ay) & b (bx, by)
     */
    private static double distance(double ax, double ay, double bx, double by) {
        return Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }

    /**
     * Sets appriopriate links out to targets and link in for those targets
     * 
     * @param point
     * @param targets
     * @param costToTime
     */
    private static void setLinksOut(Graph graph, GraphPoint point, ArrayList<GraphPoint> targets, double costToTime) {
        Iterator<GraphPoint> pit = targets.iterator();
        while (pit.hasNext()) {
            GraphPoint target = pit.next();
            int time = (int) (distance(point.getX(), point.getY(), target.getX(), target.getY()));
            GraphLink link = new GraphLink(point, target, time);
            graph.addLink(link);
            point.addElementToListOut(link);
            target.addElementToListIn(link);
        }
    }

    /**
     * Sets appriopriate links in from source and links out for those sources
     * 
     * @param point
     * @param sources
     * @param costToTime
     */
    private static void setLinksIn(Graph graph, GraphPoint point, ArrayList<GraphPoint> sources, double costToTime) {
        Iterator<GraphPoint> pit = sources.iterator();
        while (pit.hasNext()) {
            GraphPoint source = pit.next();
            int time = (int) (distance(point.getX(), point.getY(), source.getX(), source.getY()));
            GraphLink link = new GraphLink(source, point, time);
            graph.addLink(link);
            point.addElementToListIn(link);
            source.addElementToListOut(link);
        }
    }

    /**
     * @param graph
     * @param howMany
     * @return arraylist <i>howMany</i> randomly chosen points from <i>graph</i>
     */
    private static ArrayList<GraphPoint> getPoints(Graph graph, int howMany) {

        // get list of all points
        ArrayList<GraphPoint> result = new ArrayList<GraphPoint>();
        Iterator<GraphPoint> pit = graph.getPointsIterator();
        while (pit.hasNext())
            result.add(pit.next());

        result.addAll(graph.getCollectionOfPoints());

        // russian roulette - remove unnecessary from it
        int victimsLeft = Math.min(Math.max(graph.getPointsSize() - howMany, 0), graph.getPointsSize());
        while (victimsLeft > 0) {
            Random random = new Random(System.currentTimeMillis() + 3382654);
            int happyWinner = random.nextInt(result.size());
            result.remove(happyWinner);
            victimsLeft--;
        }

        // return list of points of wanted size
        return result;
    }

    /**
     * @param graph
     * @param minSpaceRange
     *        new point can't neigbours closer than that value
     * @return generated point, that has already been added to
     */
    private static GraphPoint generatePoint(Graph graph, int minSpaceRange) {
        Random random = new Random(83121015852L);
        int x, y;
        GraphPoint point = null;
        do {
            x = random.nextInt((int) (graph.getXmax() - graph.getXmin())) + (int) graph.getXmin();
            y = random.nextInt((int) (graph.getYmax() - graph.getYmin())) + (int) graph.getYmin();
            point = new GraphPoint(x, y, "pt_" + x + "_" + y);
        } while (tooClose(point, graph, minSpaceRange));
        graph.addPoint(point);
        return point;
    }

    /**
     * @param point
     * @param graph
     * @param minSpaceRange
     * @return true if there are neighbours closer than <i>minSpaceRange</i>, false otherwise
     */
    private static boolean tooClose(GraphPoint point, Graph graph, int minSpaceRange) {
        Iterator<GraphPoint> pit = graph.getPointsIterator();
        while (pit.hasNext()) {
            GraphPoint otherPoint = pit.next();
            if (distance(point.getX(), point.getY(), otherPoint.getX(), otherPoint.getY()) < minSpaceRange)
                return true;
        }
        return false;
    }

    /**
     * @param points
     *        number of points to be generateds
     * @return complete graph of size=<i>points</i> with dimensions [0:100], [0:100] and link time==cost
     */
    public static Graph generate(int points) {
        return generate(points, 100, 0, 100, 0, 100, 1);
    }

}
