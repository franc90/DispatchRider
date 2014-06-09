package gui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import xml.elements.SimmulationData;

import algorithm.GraphSchedule;
import dtp.commission.Commission;
import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.simmulation.SimInfo;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import gui.common.TimestampUpdateable;

public class MapHolder extends TimestampUpdateable {

	private edu.uci.ics.jung.graph.Graph<GraphPoint,GraphLink> graph;
	private VisualizationViewer <GraphPoint,GraphLink> vv;
	private Layout<GraphPoint,GraphLink> layout;
	private Boolean graphPresent;

	class LocationRecord {
		public LocationRecord(Integer timestamp, Double point) {
			super();
			this.timestamp = timestamp;
			this.point = point;
		}
		Integer timestamp;
		Point2D.Double point;
	}

	private Map <Integer, List<LocationRecord>> locations = new HashMap  <Integer,List<LocationRecord>>();

	@Override
	public void update(SimmulationData data) {
		if(newRecord.getData() == null)
			newRecord.setData(new HashSet<SimmulationData>());
		@SuppressWarnings("unchecked")
		Set<SimmulationData> extracted = (Set<SimmulationData>) newRecord.getData();
		extracted.add(data);
		ColorCreator.setMaxID(data.getHolonId());
		java.awt.geom.Point2D.Double currentLoc = data.getSchedule().getCurrentLocation();

		if (locations.containsKey(data.getHolonId()))
			locations.get(data.getHolonId()).add(new LocationRecord(visualisedRecord.getTimestamp(),new Point2D.Double(currentLoc.getX(), currentLoc.getY())));
		else{
			locations.put(data.getHolonId(), new java.util.LinkedList<LocationRecord>());
			locations.get(data.getHolonId()).add(new LocationRecord(visualisedRecord.getTimestamp(),new Point2D.Double(currentLoc.getX(), currentLoc.getY())));
		}
		vv.repaint();
	}

	public void repaint(){
		Object[] vertices = graph.getVertices().toArray();
		for (Object o : vertices){
			if (o.getClass()==HolonGraphPoint.class)
				graph.removeVertex((GraphPoint) o);
		}
		insertHolonVertices();
		if (!graphPresent)
			insertCommissionVertices();
		Object[] edges = graph.getEdges().toArray();
		for (Object o : edges){
			if (o.getClass()==HolonGraphLink.class)
				graph.removeEdge((GraphLink) o);
		}
		insertHolonEdges();

	}

	private void insertHolonEdges() {
		@SuppressWarnings("unchecked")
		Set<SimmulationData> toVisualise = (Set<SimmulationData>)visualisedRecord.getData();
		if (toVisualise==null)
			return;
		for (SimmulationData data: toVisualise){
			if (locations.containsKey(data.getHolonId())){
				List <LocationRecord> holonLoc = locations.get(data.getHolonId());
				ListIterator <LocationRecord> it =  holonLoc.listIterator();
				Point2D.Double endP = it.next().point;
				Point2D.Double startP;
				while (it.hasNext()){
					startP=endP;
					LocationRecord rec = it.next();
					if (rec.timestamp>visualisedRecord.getTimestamp())
						break;
					endP=rec.point;
					GraphPoint start = new InvisibleGraphPoint(startP.getX(), startP.getY());
					GraphPoint end=  new InvisibleGraphPoint(endP.getX(), endP.getY());

					if (!start.hasSameCoordinates(end)){//inaczej narysuje petle
						insertAndLockPoint(start);
						insertAndLockPoint(end);
						graph.addEdge(new HolonGraphLink(ColorCreator.createColor(data.getHolonId())), start, end);
					}
				}
			}
		}
		//przyszle zlecenia
		for (SimmulationData data: toVisualise){
			for (Commission c : data.getSchedule().getCommissions()){
				GraphPoint start = new InvisibleGraphPoint(c.getPickupX(), c.getPickupY());
				GraphPoint end=  new InvisibleGraphPoint(c.getDeliveryX(), c.getDeliveryY());
				if (!start.hasSameCoordinates(end)){//inaczej narysuje petle
					insertAndLockPoint(start);
					insertAndLockPoint(end);
					graph.addEdge(new CommissionGraphLink(ColorCreator.createColor(data.getHolonId())),start, end);
				}
			}
		}
	}

	private void insertCommissionVertices() {
		@SuppressWarnings("unchecked")
		Set<SimmulationData> toVisualise = (Set<SimmulationData>)visualisedRecord.getData();
		if (toVisualise==null || toVisualise.isEmpty())
			return;
		List <Commission> lista=null;
		for (SimmulationData data: toVisualise){
			lista= data.getSchedule().getCommissions();//tylko pierwszy element ze zbioru SimmulationData

			if (lista == null || lista.isEmpty())
				return;
			for (Commission c : lista){
				insertAndLockPoint( new GraphPoint(c.getPickupX(),c.getPickupY()));
				insertAndLockPoint( new GraphPoint(c.getDeliveryX(),c.getDeliveryY()));
			}
		}
	}

	private void insertAndLockPoint(GraphPoint point){
		graph.addVertex(point);
		layout.setLocation(point,  new Point2D.Double(5*point.getX()+200, 5*point.getY()+50));
		layout.lock(point, true);
	}

	@Override
	public void setSimInfo(SimInfo simInfo) {
		this.simInfo = simInfo;
		initiateBasicGraph();

		createVisualizationViewer();
		//to sie wyswietla po najechaniu
		setEdgeToolTip();
		setVertexToolTip();
		setVertexPainter();
		setEdgePainter();
		setEdgeShape();
		setVertexShape();
		setEdgeThickness();
	}

	private void setEdgeShape() {
		/*AbstractEdgeShapeTransformer<GraphPoint, GraphLink> edgeShape = new AbstractEdgeShapeTransformer <GraphPoint, GraphLink>(){
			@Override
			public Shape transform(
					Context<edu.uci.ics.jung.graph.Graph<GraphPoint, GraphLink>, GraphLink> arg0) {
				if (arg0.element.getClass()==HolonGraphLink.class)
					return new Edge;
			}
		};*/
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<GraphPoint, GraphLink>());//proste krawedzie
	}

	private void setEdgeThickness() {
		Transformer<GraphLink, Stroke> edgeStroke = new Transformer<GraphLink, Stroke>() {
			public Stroke transform(GraphLink s) {
				float dash[] = {10.0f};
				if (s.getClass()==CommissionGraphLink.class)
					return new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 6.0f, dash, 0.0f);
				else if (s.getClass()==HolonGraphLink.class)
					return new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
				else
					return new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
			}
		};
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStroke);

	}

	private void setEdgePainter() {
		Transformer<GraphLink, Paint> edgePaint = new Transformer<GraphLink, Paint>() {

			@Override
			public Paint transform(GraphLink arg0) {
				if (arg0.getClass()==HolonGraphLink.class || arg0.getClass()==CommissionGraphLink.class)
					return ((HolonGraphLink)arg0).getColor();
				return Color.black;
			}
		};	
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
	}

	public VisualizationViewer <GraphPoint,GraphLink> getMap() {
		return vv;
	}

	private void createVisualizationViewer() {
		// Layout<V, E>, VisualizationComponent<V,E>
		vv = new VisualizationViewer<GraphPoint,GraphLink>(layout);
		vv.setPreferredSize(new Dimension(850,850));
		//to jest to co wyswietla sie zawsze przy wierzcholku / krawedzi
		vv.getRenderContext().setVertexLabelTransformer(new  Transformer<GraphPoint, String>() {
			@Override
			public String transform(GraphPoint arg0) {
				return null;
			}
		});
		vv.getRenderContext().setEdgeLabelTransformer(new Transformer<GraphLink, String>() {

			@Override
			public String transform(GraphLink arg0) {
				return null;
			}
		});
		// Create a graph mouse and add it to the visualization component
		@SuppressWarnings("rawtypes")
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
	}

	//wstawia wierzcholki z visualisedRecord
	private void insertHolonVertices() {
		@SuppressWarnings("unchecked")
		Set<SimmulationData> toVisualise = (Set<SimmulationData>)visualisedRecord.getData();
		if (toVisualise==null)
			return;
		for (SimmulationData data: toVisualise){
			HolonGraphPoint point = new HolonGraphPoint(data.getLocation().x, data.getLocation().y);
			point.setHolonID(data.getHolonId());
			point.setHolonCreationTime(data.getHolonCreationTime());
			point.setDriver(data.getDriver().getAID().getLocalName());
			point.setTruckComfort(data.getTruck().getComfort());
			point.setTrailerCapacity(data.getTrailer().getCapacity());
			insertAndLockPoint(point);
		}
	}

	private void initiateBasicGraph() {
		if (simInfo.getScheduleCreator().getClass()==GraphSchedule.class){
			graphPresent=true;
			GraphSchedule schedule = (GraphSchedule) simInfo.getScheduleCreator();
			Graph g = schedule.getTrackFinder().getGraph();

			graph = new SparseMultigraph<GraphPoint,GraphLink>();

			layout = new FRLayout<GraphPoint,GraphLink>(graph);

			layout.setSize(new Dimension(800,800));
			/*for (GraphPoint p: graph.getVertices()){
				layout.setLocation(p, new Point2D.Double(p.getX(), p.getY()));
				layout.lock(p, true);
			}*/

			for(GraphPoint p : g.getCollectionOfPoints()){
				insertAndLockPoint(p);
			}
			for(GraphLink l : g.getCollectionOfLinks()){
				graph.addEdge(l, l.getStartPoint(), l.getEndPoint(), EdgeType.DIRECTED);
			}
		}
		else{
			graphPresent=false;
			graph = new SparseMultigraph<GraphPoint,GraphLink>();
			layout = new FRLayout<GraphPoint,GraphLink>(graph);
			layout.setSize(new Dimension(800,800));
		}

	}

	private void setVertexShape() {
		Transformer<GraphPoint, Shape> vertexShape = 
				new Transformer<GraphPoint, Shape>() {

			@Override
			public Shape transform(GraphPoint p) {
				if (p.getClass()==HolonGraphPoint.class)
					return new Rectangle(-8, -8, 16, 16);
				else if (p.getClass()==InvisibleGraphPoint.class)
					return new Rectangle(0,0,0,0);
				return new Ellipse2D.Float(-6.0f, -6.0f, 12.0f, 12.0f);

			}
		};  
		vv.getRenderContext().setVertexShapeTransformer(vertexShape);
	}

	private void setVertexToolTip() {
		Transformer<GraphPoint, String> vertexToolTipTransformer = new Transformer<GraphPoint, String>() {

			@Override
			public String transform(GraphPoint arg0) {
				if (arg0.getClass()!=HolonGraphPoint.class)
					return "Node: ID: "+arg0.getId().toString()+", Coordinates: ["+arg0.getX()+", "+arg0.getY()+"]\n";
				else{
					HolonGraphPoint point = (HolonGraphPoint) arg0;
					return "Holon: ID: "+point.getHolonID().toString()+
							", Creation time: " +point.getHolonCreationTime()+
							", Coordinates: ["+arg0.getX()+", "+arg0.getY()+"]" +
							", Truck comfort: "+point.getTruckComfort()+
							", Trailer capacity: "+point.getTrailerCapacity()+
							", Driver: "+point.getDriver();
				}

			}
		};
		vv.setVertexToolTipTransformer(vertexToolTipTransformer);

	}

	private void setEdgeToolTip() {

		Transformer<GraphLink, String> edgeToolTipTransformer = new Transformer<GraphLink, String>() {
			@Override
			public String transform(GraphLink arg0) {
				StringBuilder builder = new StringBuilder();
				builder.append("Road: \nStart point: \n");
				builder.append("["+arg0.getStartPoint().getX()+" , "+arg0.getStartPoint().getY()+"], \n");
				builder.append("End point: \n");
				builder.append("["+arg0.getEndPoint().getX()+" , "+arg0.getEndPoint().getY()+"], \n");
				builder.append("Distance: \n");
				//builder.append(arg0.distance()+"\n");
				//builder.append("Travel time:\n");
				builder.append(arg0.getCost());
				//System.out.println(builder.toString());
				return builder.toString();
			}
		};
		vv.setEdgeToolTipTransformer(edgeToolTipTransformer);

	}

	private void setVertexPainter() {
		Transformer<GraphPoint, Paint> vertexPaint = new Transformer<GraphPoint, Paint>() {

			public Paint transform(GraphPoint point) {
				if (point.getClass()==HolonGraphPoint.class)
					return ColorCreator.createColor(((HolonGraphPoint)point).getHolonID());
				return Color.red;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
	}
}
