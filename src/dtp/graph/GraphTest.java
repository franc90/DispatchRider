package dtp.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;

import dtp.commission.Commission;
import dtp.commission.TxtFileReader;
import dtp.jade.ProblemType;
import dtp.util.DirectoriesResolver;
import dtp.visualisation.VisGUI;

public class GraphTest {

	public void test() {

		ArrayList<GraphPoint> points;
		Graph graph;

		points = getPointsFromFile();
		System.out.println("Got points = " + points.size());

		graph = new GraphGenerator().generateRandom(points,
				(int) (0.8 * points.size()));

		GraphPoint point = new GraphPoint(38, 69, "new_38_69");

		GraphLink link = graph.getLinkByCoordinate(35, 69, 40, 69);

		graph.addPointOnLink(point, link);

		VisGUI visGUI2 = new VisGUI(graph, ProblemType.WITH_GRAPH);
		visGUI2.setVisible(true);

		graph.printLinks();
	}

	private ArrayList<GraphPoint> getPointsFromFile() {

		JFileChooser chooser;
		Commission[] commissions;
		ArrayList<GraphPoint> points;
		double px, py;

		chooser = new JFileChooser(DirectoriesResolver.getTxtCommisionsDir());
		int returnVal = chooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			commissions = TxtFileReader.getCommissions(chooser
					.getSelectedFile().getAbsolutePath());
		} else {

			System.out
					.println("getCommissions() - failed to read coms from file");
			System.exit(0);
			return null;
		}

		points = new ArrayList<GraphPoint>();

		// POINTS
		for (int i = 0; i < commissions.length; i++) {
			// for (int i = 0; i < getOnlyPoints; i++) {

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
		Point2D.Double base = TxtFileReader.getDepot(chooser.getSelectedFile()
				.getAbsolutePath());
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

	public static void main(String[] args) {

		new GraphTest().test();
	}
}
