package dtp.graph.translator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.gui.ExtensionFilter;
import dtp.xml.GraphWriter;
import dtp.xml.ParseException;

public class TranslateGraph {

	public static class SimpleErrorHandler implements ErrorHandler {

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;

		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			throw e;
		}
	}

	private static int attributeToInt(Node node, String attribute)
			throws ParseException {
		Node attr = node.getAttributes().getNamedItem(attribute);
		if (attr == null)
			throw new ParseException("No such attribute " + attribute);
		return Integer.valueOf(attr.getTextContent());
	}

	private static Graph parseGraph(Element element) throws ParseException {
		Graph graph = new Graph();

		Map<String, GraphPoint> points = addNodes(graph, (Element) element
				.getElementsByTagName("nodes").item(0));
		addRoads(graph,
				(Element) element.getElementsByTagName("roads").item(0), points);
		return graph;
	}

	private static Map<String, GraphPoint> addNodes(Graph g, Element nodesEl)
			throws ParseException {
		NodeList nodes = nodesEl.getChildNodes();
		Element nodeEl;
		GraphPoint point;
		Map<String, GraphPoint> points = new HashMap<String, GraphPoint>();
		int px;
		int py;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				nodeEl = (Element) nodes.item(i);
				px = attributeToInt(nodeEl, "x");
				py = attributeToInt(nodeEl, "y");
				point = new GraphPoint(px, py, "pt_" + px + "_" + py);
				g.addPoint(point);
				points.put(nodeEl.getAttribute("id"), point);
			}
		}
		return points;
	}

	private static void addRoads(Graph g, Element roadsEl,
			Map<String, GraphPoint> points) throws ParseException {

		GraphLink link;
		Element roadEl;
		Element costEl;
		NodeList roads = roadsEl.getElementsByTagName("road");
		NodeList costElements;
		String from;
		String to;
		GraphPoint fromPoint;
		GraphPoint toPoint;
		double cost;
		for (int i = 0; i < roads.getLength(); i++) {
			roadEl = (Element) roads.item(i);
			from = roadEl.getAttribute("from");
			to = roadEl.getAttribute("to");
			fromPoint = points.get(from);
			toPoint = points.get(to);

			costElements = roadEl.getChildNodes();
			for (int j = 0; j < costElements.getLength(); j++) {
				if (costElements.item(j).getNodeType() == Node.ELEMENT_NODE) {
					costEl = (Element) costElements.item(j);
					if (costEl.getNodeName().equals("uplink")) {
						cost = Double.parseDouble(((Element) costEl
								.getElementsByTagName("main").item(0))
								.getAttribute("length"));
						link = new GraphLink(fromPoint, toPoint, cost);
						g.addLink(link);
						fromPoint.addElementToListOut(link);
						toPoint.addElementToListIn(link);
					} else if (costEl.getNodeName().equals("downlink")) {
						cost = Double.parseDouble(((Element) costEl
								.getElementsByTagName("main").item(0))
								.getAttribute("length"));
						link = new GraphLink(toPoint, fromPoint, cost);
						g.addLink(link);
						toPoint.addElementToListOut(link);
						fromPoint.addElementToListIn(link);
					} else
						throw new ParseException(
								"uplink/downlink expected but found: "
										+ costEl.getNodeName());
				}
			}

		}
	}

	public static Graph parse(String filename) throws ParseException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			// factory.setAttribute(
			// "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
			// "http://www.w3.org/2001/XMLSchema");

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new SimpleErrorHandler());
			Document document = builder.parse(filename);

			NodeList nodes = document.getElementsByTagName("RoadNet");

			return parseGraph((Element) nodes.item(0));
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}
	}

	public static Object[] getPoints(String filename) throws ParseException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			// factory.setAttribute(
			// "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
			// "http://www.w3.org/2001/XMLSchema");

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new SimpleErrorHandler());
			Document document = builder.parse(filename);

			NodeList nodes = document.getElementsByTagName("RoadNet");

			Graph graph = new Graph();
			Map<String, GraphPoint> points = addNodes(
					graph,
					(Element) ((Element) nodes.item(0)).getElementsByTagName(
							"nodes").item(0));
			addRoads(graph, (Element) ((Element) nodes.item(0))
					.getElementsByTagName("roads").item(0), points);
			return new Object[] { graph, points };
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}
	}

	public static void main(String args[]) throws Exception {

		System.out.println("Wybierz plik z grafem systemu KrakSim");
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("Open graph file");
		chooser.setFileFilter(new ExtensionFilter(new String[] { "xml" }));
		String graphFile = null;
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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
		Graph graph = TranslateGraph.parse(graphFile);

		System.out.println("Wybierz gdzie zapisaæ przekonwertowany graf");
		chooser = new JFileChooser(".");
		chooser.setDialogTitle("Save graph file");
		chooser.setFileFilter(new ExtensionFilter(new String[] { "xml" }));
		chooser.setSelectedFile(new File("graph.xml"));
		graphFile = null;
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

		new GraphWriter(graph).saveAsXmlFile(new File(graphFile));
	}
}
