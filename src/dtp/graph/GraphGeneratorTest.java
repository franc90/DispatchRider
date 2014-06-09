package dtp.graph;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;

import dtp.commission.Commission;
import dtp.commission.TxtFileReader;
import dtp.gui.ExtensionFilter;
import dtp.jade.ProblemType;
import dtp.util.DirectoriesResolver;
import dtp.visualisation.VisGUI;
import dtp.xml.GraphWriter;

public class GraphGeneratorTest {

	private void test() {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));

			System.out.println("Podaj parametry generatora:");
			System.out.print("1. Max ilosc sasiadow wierzcholka: ");
			int howManyNeighbours = Integer.parseInt(reader.readLine());
			System.out.print("2. Ilosc polaczen z sasiadami: ");
			int howManyPoints = Integer.parseInt(reader.readLine());

			if (howManyPoints > howManyNeighbours)
				howManyPoints = howManyNeighbours;

			System.out.print("3. Wybierz gdzie zapisac wynik: ");
			JFileChooser chooser = new JFileChooser(".");
			chooser.setDialogTitle("Save graph file");
			chooser.setFileFilter(new ExtensionFilter(new String[] { "xml" }));
			chooser.setSelectedFile(new File("graph.xml"));
			String graphFile = null;
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				graphFile = chooser.getSelectedFile().getAbsolutePath();
				if (!graphFile.endsWith(".xml")) {
					graphFile += ".xml";
				}
			}
			if (graphFile == null) {
				System.out.println("\n");
				System.out.println("Generacja przerwana przez uzytkownika");
				return;
			}
			System.out.println(graphFile);

			GraphGenerator generator;
			ArrayList<GraphPoint> points;
			Graph graph;

			System.out.println("\nGeneracja rozpoczeta");
			System.out.println("Wybierz plik z opisem zlecen");
			points = getPointsFromFile();
			System.out.println("Got points = " + points.size());

			generator = new GraphGenerator();

			boolean isConsistant;

			do {
				// graph = generator.generateRandom(points, 2);
				graph = generator.generateWithNeighbours(points,
						howManyNeighbours, howManyPoints);

				System.out.println("Got graph...");
				System.out.println("Points number = " + graph.getPointsSize()
						+ " links number = "
						+ graph.getCollectionOfLinks().size());

				isConsistant = graph.isConsistant();
				System.out.println("Is consistant = " + isConsistant);

			} while (isConsistant == false);

			final Graph g = graph;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					VisGUI visGUI = new VisGUI(g, ProblemType.WITH_GRAPH);
					visGUI.setVisible(true);
				}
			});
			new GraphWriter(graph).saveAsXmlFile(new File(graphFile));
			System.out
					.println("Generacja zakonczona - wcisnij Ctrl+c aby wyjsc");

		} catch (NumberFormatException e) {
			System.out.println("\nWprowadzono niepoprawny format liczbowy");
			System.out
					.println("Generacja przerwana z powodu wyst¹pienia bledu");
		} catch (Exception e) {
			System.out.println("Wystapil blad:");
			e.printStackTrace();
		}
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

		new GraphGeneratorTest().test();
	}
}
