package dtp.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;

/**
 * @author Grzegorz
 */
public class GraphWriter {

	/** Logger. */
	private static Logger logger = Logger.getLogger(GraphWriter.class);

	private final static String NEWLINE = "\r\n";
	private final static String HEADER1 = "<?xml version=\"1.0\"?>" + NEWLINE;
	private final static String HEADER2 = "<network xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";

	private final Graph graph;

	public GraphWriter(Graph graph) {
		this.graph = graph;
	}

	public void saveAsXmlFile(File file) {

		Iterator<GraphPoint> it;
		Iterator<GraphLink> it2;
		GraphPoint point;
		GraphLink link;

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(HEADER1);
			writer.write(HEADER2);

			HashMap<GraphPoint, Integer> pointMap = prepareStructure();

			for (it = this.graph.getPointsIterator(); it.hasNext();) {
				point = it.next();
				writer.write("\t<point>" + NEWLINE);
				writer.write("\t\t<id> " + pointMap.get(point) + " </id>"
						+ NEWLINE);
				writer.write("\t\t<name>" + point.toString() + "</name>"
						+ NEWLINE);
				writer.write("\t\t<position>" + NEWLINE);
				writer.write("\t\t\t<x> " + point.getX() + " </x>" + NEWLINE);
				writer.write("\t\t\t<y> " + point.getY() + " </y>" + NEWLINE);
				writer.write("\t\t</position>" + NEWLINE);
				writer.write("\t\t<isbase>" + point.isBase() + "</isbase>"
						+ NEWLINE);
				for (it2 = this.graph.getLinksIterator(); it2.hasNext();) {
					link = it2.next();
					if (link.getStartPoint().equals(point)) {
						writer.write("\t\t<route>" + NEWLINE);
						writer.write("\t\t\t<id_r> "
								+ pointMap.get(link.getEndPoint()) + " </id_r>"
								+ NEWLINE);
						writer.write("\t\t\t<cost> " + link.getCost()
								+ " </cost>" + NEWLINE);
						writer.write("\t\t</route>" + NEWLINE);
					}
				}

				writer.write("\t</point>" + NEWLINE);
			}

			writer.write("</network>");

			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error("IOException: " + e.getMessage());
		}

	}

	private HashMap<GraphPoint, Integer> prepareStructure() {

		HashMap<GraphPoint, Integer> pointMap = new HashMap<GraphPoint, Integer>();

		Iterator<GraphPoint> it;
		GraphPoint point;
		int counter = 1;

		for (it = this.graph.getPointsIterator(); it.hasNext();) {
			point = it.next();
			pointMap.put(point, new Integer(counter));
			counter++;
		}

		return pointMap;
	}

}
