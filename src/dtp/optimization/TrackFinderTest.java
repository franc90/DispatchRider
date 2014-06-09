package dtp.optimization;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.xml.GraphParser;
import dtp.xml.ParseException;

public class TrackFinderTest {

    Graph graph;
    GraphPoint point1;
    GraphPoint point2;
    GraphTrack track;

    public void test() {

        try {

            graph = new GraphParser().parse("E:\\prog\\Java\\DispatchRider\\xml\\maps\\polska.xml");

        } catch (ParseException e) {
            System.out.println("ParseException occured");
            e.printStackTrace();
        }

        System.out.println("Graph:");
        System.out.println("points size = " + graph.getPointsSize());
        System.out.println("links size  = " + graph.getCollectionOfLinks().size());

        point1 = graph.getPoint(0);
        point2 = graph.getPoint(1);

        System.out.println("Start = " + point1.toString());
        System.out.println("End   = " + point2.toString());

        track = new Astar(graph).findTrack(point1, point2);

        track.print();
    }

    public static void main(String[] args) {

        new TrackFinderTest().test();
    }
}
