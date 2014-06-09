package dtp.optimization;

import java.io.Serializable;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;

/**
 * @author Grzegorz
 */
public interface TrackFinder extends Serializable {

	public GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint);

	public void setGraph(Graph graph);

	public Graph getGraph();
}
