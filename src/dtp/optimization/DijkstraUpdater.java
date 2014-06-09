package dtp.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;


public class DijkstraUpdater   {

	private static final long serialVersionUID = -8457848592314334382L;

	private static Logger logger = Logger.getLogger(DijkstraUpdater.class);

	private static Graph graph;
	//private Graph oldgraph;
	
	//static PathCache cache;


	public DijkstraUpdater(Graph world) {
		
		this.graph = world;

	}




	private int getWeight(int cost) {
		return (int) ((Math.pow(graph.getCostMul() * cost + graph.getCostSum(),
				graph.getCostPow()) + graph.getFreeSum())// *
		// distance(startPoint,endPoint)
		);
	}

	public GraphTrack findTrack(GraphPoint startPoint, GraphPoint endPoint) {
		//Badanie czasu:
//		Timer timer=new Timer();
//		timer.startTimer();
		
		try {
		
			GraphTrack result = new GraphTrack();
			HashMap<GraphPoint, Integer> d = new HashMap<GraphPoint, Integer>();
			HashMap<GraphPoint, GraphPoint> precedessor = new HashMap<GraphPoint, GraphPoint>();
			ArrayList<GraphPoint> pointList = new ArrayList<GraphPoint>();
			pointList.addAll(graph.getCollectionOfPoints());

			Iterator<GraphPoint> pit = pointList.iterator();
			while (pit.hasNext()) {
				GraphPoint tmp = pit.next();
				d.put(tmp, Integer.MAX_VALUE);
				precedessor.put(tmp, null);
			}
			d.put(startPoint, 0);
		//	HashSet<GraphPoint> S = new HashSet<GraphPoint>();
//			FibonacciHeap<GraphPoint> Qh = new FibonacciHeap<GraphPoint>();
			
			HashSet<GraphPoint> Q = new HashSet<GraphPoint>();
			Q.addAll(pointList);
			while (!Q.isEmpty()) {

				// get point with least d[point]
				pit = Q.iterator();
				GraphPoint least = null;
				while (pit.hasNext()) {
					GraphPoint tmp = pit.next();
					if (least == null || d.get(tmp) < d.get(least))
						least = tmp;
				}
				Q.remove(least);

			//	S.add(least);

				Iterator<GraphLink> lit = least.getLinksOutIterator();
				while (lit.hasNext()) {
					GraphLink ln = lit.next();
					GraphPoint pt = ln.getEndPoint();
					int weight = getWeight((int) ln.getCost());
					if (d.get(pt) > d.get(least) + weight) {
						d.put(pt, d.get(least) + weight);
						precedessor.put(pt, least);
					}
				}
				
				if(least.equals(endPoint)){
					break;
				}
				
				
			}

			if (precedessor.get(endPoint) == null)
				return result;

			GraphPoint pt = endPoint;
			while (pt != startPoint) {
				result.addPointAtPosition(0, pt);
				pt = precedessor.get(pt);
			}
			result.addPointAtPosition(0, pt); 

			result.setPossible(true);

			
			return result;
		} catch (Exception ex) {
			//logger.error("Dijkstra: ");
			//logger.error(ex);
			return null;
		}
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph world) {
		this.graph = world;
	}

}



