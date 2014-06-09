package dtp.jade.agentcalendar;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;

import dtp.commission.Commission;
import dtp.commission.TxtFileReader;
import dtp.graph.Graph;
import dtp.graph.GraphGenerator;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;
import dtp.jade.crisismanager.crisisevents.EUnitFailureEvent;
import dtp.util.DirectoriesResolver;

public class AgentCalendarWithGraphTest {

	public void test() {

		Graph graph;
		ArrayList<GraphPoint> points;
		AgentCalendarWithGraph calendar;
		Commission[] coms;
		JFileChooser chooser;

		chooser = new JFileChooser(DirectoriesResolver.getTxtCommisionsDir());
		chooser.showOpenDialog(null);

		points = getPointsFromFile(chooser.getSelectedFile().getAbsolutePath());
		System.out.println("Got points = " + points.size());

		graph = new GraphGenerator().generateRandom(points,
				(int) (0.9 * points.size()));
		// graph = new GraphGenerator().generateWithNeighbours(points, 50, 30);
		System.out.println("Got graph...");
		System.out.println("Points number = " + graph.getPointsSize()
				+ " links number = " + graph.getCollectionOfLinks().size());
		System.out.println("Is graph consistant = " + graph.isConsistant());

		coms = TxtFileReader.getCommissions(chooser.getSelectedFile()
				.getAbsolutePath());
		System.out.println("Got commissions = " + coms.length);

		calendar = new AgentCalendarWithGraph(graph, 1300, 200);

		System.out.println("\n*******************************************");
		System.out.println("addCommission:");
		coms[0].printCommision();
		calendar.addCommission(coms[0], 0);

		System.out.println("\n*******************************************");
		System.out.println("addCommission:");
		coms[1].printCommision();
		calendar.addCommission(coms[1], 0);

		// System.out.println("\n*******************************************");
		// System.out.println("addCommission:");
		// coms[2].printCommision();
		// calendar.addCommission(coms[2], 0);

		System.out.println("\n###########################################");
		System.out.println("Calendar after:");
		calendar.print();
		System.out.println("\n###########################################");

		System.out.println("\n\n\n");
		System.out.println("\n*******************************************");

		int timestamp = 1;

		ArrayList<Commission> comsAfter = calendar
				.getCommissionsAfter(timestamp);
		System.out.println("Coms after number = " + comsAfter.size());

		EUnitFailureEvent event = new EUnitFailureEvent();
		event.setEventID(0);
		event.setEUnitID(0);
		event.setEventTime(timestamp);
		event.setFailureDuration(100);

		// usun zlecenia....
		Iterator<Commission> iter = comsAfter.iterator();
		while (iter.hasNext()) {

			calendar.removeCommission(iter.next().getID(),
					calendar.getSetCurrentGraphPoint(timestamp), timestamp);
		}

		calendar.addActionBroken(event, timestamp);

		System.out.println("\n\n\n");
		System.out.println("\n###########################################");
		System.out.println("Calendar after:");
		calendar.print();
		System.out.println("\n###########################################");
	}

	private ArrayList<GraphPoint> getPointsFromFile(String filename) {

		Commission[] commissions;
		ArrayList<GraphPoint> points;
		double px, py;

		commissions = TxtFileReader.getCommissions(filename);

		points = new ArrayList<GraphPoint>();

		// POINTS
		for (int i = 0; i < commissions.length; i++) {

			px = commissions[i].getPickupX();
			py = commissions[i].getPickupY();

			if (!containsPoint(points, px, py))
				points.add(new GraphPoint(px, py, "pt_" + px + "_" + py));

			px = commissions[i].getDeliveryX();
			py = commissions[i].getDeliveryY();

			if (!containsPoint(points, px, py))
				points.add(new GraphPoint(px, py, "pt_" + px + "_" + py));
		}

		// BASE
		Point2D.Double base = TxtFileReader.getDepot(filename);
		if (!containsPoint(points, (int) base.getX(), (int) base.getY()))
			points.add(new GraphPoint((int) base.getX(), (int) base.getY(),
					"base_" + base.getX() + "_" + base.getY(), true, 0));

		return points;
	}

	private boolean containsPoint(ArrayList<GraphPoint> points, double px,
			double py) {

		Iterator<GraphPoint> iter;
		GraphPoint tmpPoint;

		iter = points.iterator();

		while (iter.hasNext()) {

			tmpPoint = iter.next();
			if (tmpPoint.getX() == px && tmpPoint.getY() == py)
				return true;
		}

		return false;
	}

	@SuppressWarnings("unused")
	private Point2D calculateCurrentLocationWithGraph(
			CalendarActionWithGraph currentCalendarAction, int timestamp) {

		GraphTrack track;

		if (currentCalendarAction.getType() != "DRIVE") {

			// eqauls destination location
			return new Point2D.Double(currentCalendarAction.getSource().getX(),
					currentCalendarAction.getSource().getY());
		}

		// DRIVE action

		track = currentCalendarAction.getTrack();

		return track.getCurrentLocation(currentCalendarAction.getStartTime(),
				currentCalendarAction.getEndTime(), timestamp);
	}

	public static void main(String[] args) {

		new AgentCalendarWithGraphTest().test();
	}
}
