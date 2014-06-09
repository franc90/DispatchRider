package dtp.xml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import measure.Measure;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MeasuresParser {

	public static Map<String, List<Measure>> parse(String fileName)
			throws ParseException {
		Map<String, List<Measure>> result;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder builder = factory.newDocumentBuilder();
			// builder.setErrorHandler(new SimpleErrorHandler());
			Document document = builder.parse(fileName);
			Element root = (Element) document.getElementsByTagName(
					"simulation_measures").item(0);
			result = parseMeasures(root);
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}
		return result;
	}

	private static Map<String, List<Measure>> parseMeasures(Element root) {
		NodeList nodes = root.getElementsByTagName("measures");
		Element measuresEl;
		Element holonEl;
		Measure measure;
		NodeList holons;
		String holonId;
		int timestamp;
		Map<String, Measure> measures;
		Map<String, Double> holonMeasuresValues = new HashMap<String, Double>();
		Map<String, List<Measure>> result = new HashMap<String, List<Measure>>();
		List<Measure> measuresList;

		for (int i = 0; i < nodes.getLength(); i++) {
			measuresEl = (Element) nodes.item(i);
			timestamp = Integer.parseInt(measuresEl.getAttribute("timestamp"));
			holons = measuresEl.getElementsByTagName("holon");
			measures = new HashMap<String, Measure>();
			for (int j = 0; j < holons.getLength(); j++) {
				holonEl = (Element) holons.item(j);
				holonId = holonEl.getAttribute("id");
				holonMeasuresValues = parseHolonMeasures(holonEl);
				for (String measureName : holonMeasuresValues.keySet()) {
					measure = measures.get(measureName);
					if (measure == null) {
						measure = new Measure();
						measure.setTimestamp(timestamp);
						measures.put(measureName, measure);
					}
					measure.put(createAID(holonId),
							holonMeasuresValues.get(measureName));
				}
			}
			for (String measureName : holonMeasuresValues.keySet()) {
				measuresList = result.get(measureName);
				if (measuresList == null) {
					measuresList = new LinkedList<Measure>();
					result.put(measureName, measuresList);
				}
				measuresList.add(measures.get(measureName));
			}
		}
		return result;
	}

	private static Map<String, Double> parseHolonMeasures(Element holonEl) {
		Map<String, Double> result = new HashMap<String, Double>();
		NodeList nodes = holonEl.getElementsByTagName("measure");
		Element measureEl;
		for (int i = 0; i < nodes.getLength(); i++) {
			measureEl = (Element) nodes.item(i);
			result.put(measureEl.getAttribute("name"),
					Double.parseDouble(measureEl.getTextContent()));
		}
		return result;
	}

	private static String createAID(String nr) {
		return "ExecutionUnit#" + nr;
	}
}
