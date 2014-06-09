package measure.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;

import machineLearning.aggregator.AggregatorsManager;
import machineLearning.aggregator.MLAggregator;
import measure.Measure;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MeasureVisualizationPanel extends JPanel {
	private static final long serialVersionUID = -4117838550697318420L;
	private String name = "None";
	private JFreeChart freeChart;
	private final Map<String, XYSeries> holonSeries = new TreeMap<String, XYSeries>();
	private final XYSeriesCollection dataset = new XYSeriesCollection();
	private ChartPanel chartPanel;
	private final AggregatorsManager aggregatorsManager;
	private final Map<String, Integer> aggregatorsNrs = new HashMap<String, Integer>();

	public MeasureVisualizationPanel(String name) {
		this.name = name;
		aggregatorsManager = new AggregatorsManager();
		int i = aggregatorsManager.getAggregators().size();
		for (MLAggregator aggregator : aggregatorsManager.getAggregators()) {
			aggregatorsNrs.put(aggregator.getName(), -(i--));
		}
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100, 200));
		this.add(InitChart(), BorderLayout.CENTER);
	}

	private ChartPanel InitChart() {

		// Generate the graph
		freeChart = ChartFactory.createXYLineChart(name, // Title
				"measure calculation", // x-axis Label
				"measure value", // y-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
				);

		chartPanel = new ChartPanel(freeChart);
		return chartPanel;
	}

	private double index = 0.0;

	public synchronized void updateChart(Measure measure) {
		XYSeries series;
		// double timestamp = measure.getTimestamp();

		Map<String, Measure> tmp;
		Double value;
		for (MLAggregator aggregator : aggregatorsManager.getAggregators()) {
			tmp = new HashMap<String, Measure>();
			tmp.put(this.name, measure);
			aggregator.setMeasures(tmp);
			value = aggregator.aggregate(this.name);

			series = holonSeries.get(aggregator.getName());
			if (series == null) {
				series = new XYSeries(aggregator.getName());
				holonSeries.put(aggregator.getName(), series);
				dataset.addSeries(series);
			}
			series.add(index, value);

			// series.add(timestamp, value);
			aggregator.aggregationFinished();
		}

		for (String holon : measure.getValues().keySet()) {
			series = holonSeries.get(holon);
			if (series == null) {
				series = new XYSeries(holon);
				holonSeries.put(holon, series);
				dataset.addSeries(series);
			}
			series.add(index, measure.getValues().get(holon));
			// series.add(timestamp, measure.getValues().get(holon));
		}

		index += 1;

		freeChart.fireChartChanged();
	}

	@SuppressWarnings("unchecked")
	public synchronized void hide(String holon) {
		String name = holon;
		List<Object> seriesList = new LinkedList<Object>(dataset.getSeries());
		int index = 0;
		for (Object series : seriesList) {
			if (((XYSeries) series).getKey().equals(name))
				dataset.removeSeries(index);
			index++;
		}

		freeChart.fireChartChanged();
	}

	@SuppressWarnings("unchecked")
	public synchronized void show(String holon) {
		List<Object> seriesList = new LinkedList<Object>(dataset.getSeries());
		dataset.removeAllSeries();
		int refNr = getHolonNr(holon);
		boolean added = false;
		XYSeries xySeries;
		for (Object series : seriesList) {
			xySeries = (XYSeries) series;
			if (getHolonNr((String) xySeries.getKey()) < refNr) {
				dataset.addSeries(xySeries);
			} else {
				if (added == false) {
					dataset.addSeries(holonSeries.get(holon));
					dataset.addSeries(xySeries);
					added = true;
				} else {
					dataset.addSeries(xySeries);
				}
			}
		}

		if (added == false) {
			dataset.addSeries(holonSeries.get(holon));
		}

		freeChart.fireChartChanged();
	}

	private int getHolonNr(String str) {
		if (str.contains("#")) {
			return Integer.parseInt(str.split("#")[1]);
		} else {
			return aggregatorsNrs.get(str);
		}
	}
}
