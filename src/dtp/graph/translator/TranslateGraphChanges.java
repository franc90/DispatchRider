package dtp.graph.translator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.gui.ExtensionFilter;
import dtp.xml.ParseException;

class Change {

	public String from;
	public String to;
	public String value;
}

public class TranslateGraphChanges {

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

	private static Map<GraphPoint, Integer> prepareStructure(Graph graph) {

		Map<GraphPoint, Integer> pointMap = new HashMap<GraphPoint, Integer>();

		Iterator<GraphPoint> it;
		GraphPoint point;
		int counter = 1;

		for (it = graph.getPointsIterator(); it.hasNext();) {
			point = it.next();
			pointMap.put(point, new Integer(counter));
			counter++;
		}

		return pointMap;
	}

	private static String readAndRepairChangesXMLContent(String fileName)
			throws Exception {
		StringBuilder result = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line;
		result.append("<changes>");
		while ((line = reader.readLine()) != null) {
			if (line.contains("<period")) {
				line = line.replace(">", "/>");
			}
			result.append(line).append("\n");
		}
		reader.close();
		result.append("</changes>");
		return result.toString();
	}

	private static Map<Integer, List<Change>> parse(String filename)
			throws ParseException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new SimpleErrorHandler());
			String xmlContent = readAndRepairChangesXMLContent(filename);

			InputStream is = new ByteArrayInputStream(
					xmlContent.getBytes("UTF-8"));

			Document document = builder.parse(is);

			NodeList nodes = ((Element) document
					.getElementsByTagName("changes").item(0))
					.getElementsByTagName("link");
			Map<Integer, List<Change>> changes = new HashMap<Integer, List<Change>>();
			Element linkEl;
			Element periodEl;
			String from;
			String to;
			Change change;
			NodeList periodNodes;
			List<Change> changesList;
			int timestamp;
			String value;
			for (int i = 0; i < nodes.getLength(); i++) {
				linkEl = (Element) nodes.item(i);
				from = linkEl.getAttribute("from");
				to = linkEl.getAttribute("to");
				periodNodes = linkEl.getElementsByTagName("period");
				for (int p = 0; p < periodNodes.getLength(); p++) {
					periodEl = (Element) periodNodes.item(p);
					value = periodEl.getAttribute("avg_velocity");
					if (value.equals("NaN"))
						continue;

					timestamp = Integer
							.parseInt(periodEl.getAttribute("begin"));
					changesList = changes.get(timestamp);
					if (changesList == null) {
						changesList = new LinkedList<Change>();
						changes.put(timestamp, changesList);
					}
					change = new Change();
					change.from = from;
					change.to = to;
					change.value = value;

					changesList.add(change);
				}

			}
			return changes;
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void convert(String graphFileName, String changesFileName,
			String newChangesFileName) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.newDocument();
		dom.setXmlVersion("1.0");
		Element graphChangesEl = dom.createElement("graphChanges");
		graphChangesEl.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		graphChangesEl.setAttribute("xsi:noNamespaceSchemaLocation",
				"xml/schemes/graphChanges.xsd");

		Map<Integer, List<Change>> changes = parse(changesFileName);
		Object[] tmp = TranslateGraph.getPoints(graphFileName);

		Map<String, GraphPoint> points = (Map<String, GraphPoint>) tmp[1];
		Map<GraphPoint, Integer> ids = prepareStructure((Graph) tmp[0]);

		Element changeEl;
		Element linkEl;

		List<Integer> keys = new LinkedList<Integer>(changes.keySet());
		Collections.sort(keys);

		GraphLink link;
		GraphPoint sPoint;
		GraphPoint ePoint;
		for (Integer timestamp : keys) {
			changeEl = dom.createElement("change");
			changeEl.setAttribute("time", timestamp.toString());
			for (Change change : changes.get(timestamp)) {
				linkEl = dom.createElement("link");
				linkEl.setAttribute("both", "false");
				sPoint = points.get(change.from);
				ePoint = points.get(change.to);
				link = sPoint.getLinkTo(ePoint);
				linkEl.setAttribute(
						"cost",
						new Double(link.getCost()
								/ Double.parseDouble(change.value.replace(',',
										'.'))).toString());
				linkEl.setAttribute("sPoint", ids.get(sPoint).toString());
				linkEl.setAttribute("ePoint", ids.get(ePoint).toString());
				changeEl.appendChild(linkEl);
			}
			graphChangesEl.appendChild(changeEl);
		}

		dom.appendChild(graphChangesEl);

		Source source = new DOMSource(dom);

		File file = new File(newChangesFileName);
		Result result = new StreamResult(file);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
				"4");

		xformer.transform(source, result);
	}

	public static void main(String args[]) throws Exception {
		System.out.println("Wybierz graf programu KrakSim");
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

		System.out
				.println("Wybierz plik z opisem zmian wygenerowany programem KrakSim");
		chooser = new JFileChooser(".");
		chooser.setDialogTitle("Open graph changes file");
		chooser.setFileFilter(new ExtensionFilter(new String[] { "link" }));
		String changesFile = null;
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			changesFile = chooser.getSelectedFile().getAbsolutePath();
		}
		if (changesFile == null) {
			System.out.println("\n");
			System.out.println("Generacja przerwana przez uzytkownika");
			return;
		}

		System.out
				.println("Wybierz gdzie zapisaæ przekonwertowany plik ze zmianami");
		chooser = new JFileChooser(".");
		chooser.setDialogTitle("Save graph file");
		chooser.setFileFilter(new ExtensionFilter(new String[] { "xml" }));
		chooser.setSelectedFile(new File("changes.xml"));
		String newChangesFile = null;
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			newChangesFile = chooser.getSelectedFile().getAbsolutePath();
			if (!newChangesFile.endsWith(".xml")) {
				newChangesFile += ".xml";
			}
		}
		if (newChangesFile == null) {
			System.out.println("\n");
			System.out.println("Generacja przerwana przez uzytkownika");
			return;
		}

		TranslateGraphChanges.convert(graphFile, changesFile, newChangesFile);
	}

}
