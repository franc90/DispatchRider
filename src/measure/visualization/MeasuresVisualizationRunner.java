package measure.visualization;

import jade.core.AID;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import measure.Measure;
import measure.MeasureCalculatorsHolder;
import dtp.gui.ExtensionFilter;
import dtp.xml.MeasuresParser;

public class MeasuresVisualizationRunner {

	private final Map<String, MeasureVisualizationWindow> panels = new HashMap<String, MeasureVisualizationWindow>();
	private MeasureVisualizationControl control;

	public MeasuresVisualizationRunner(List<String> names) {

		for (final String name : names) {

			MeasureVisualizationWindow window = new MeasureVisualizationWindow(
					name);
			window.setDefaultCloseOperation(MeasureVisualizationWindow.DISPOSE_ON_CLOSE);
			window.setTitle(name);
			window.getContentPane().setPreferredSize(window.getSize());
			window.pack();
			window.setLocationRelativeTo(null);
			window.setVisible(true);
			panels.put(name, window);
		}

		MeasureVisualizationControl window = new MeasureVisualizationControl(
				panels);
		window.setDefaultCloseOperation(MeasureVisualizationWindow.EXIT_ON_CLOSE);
		window.setTitle("MeasureVisualizationControl");
		window.getContentPane().setPreferredSize(window.getSize());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		control = window;
	}

	public MeasuresVisualizationRunner(MeasureCalculatorsHolder holder) {
		init(holder.getVisualizationMeasuresNames());
	}

	private void init(List<String> names) {
		for (final String name : names) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					MeasureVisualizationWindow window = new MeasureVisualizationWindow(
							name);
					window.setDefaultCloseOperation(MeasureVisualizationWindow.DISPOSE_ON_CLOSE);
					window.setTitle(name);
					window.getContentPane().setPreferredSize(window.getSize());
					window.pack();
					window.setLocationRelativeTo(null);
					window.setVisible(true);
					panels.put(name, window);
				}
			});
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MeasureVisualizationControl window = new MeasureVisualizationControl(
						panels);
				window.setDefaultCloseOperation(MeasureVisualizationWindow.DISPOSE_ON_CLOSE);
				window.setTitle("MeasureVisualizationControl");
				window.getContentPane().setPreferredSize(window.getSize());
				window.pack();
				window.setLocationRelativeTo(null);
				window.setVisible(true);
				control = window;
			}
		});
	}

	public synchronized void update(Measure measure, String name) {
		MeasureVisualizationWindow window = panels.get(name);
		if (window != null) {
			window.update(measure, name);
		}
	}

	public synchronized void setCurrentHolons(Set<AID> aids) {
		for (AID aid : aids)
			control.register(aid.getLocalName());
	}

	public synchronized void setCurrentHolonsNames(Set<String> aids) {
		for (String aid : aids)
			control.register(aid);
	}

	public static void main(String args[]) {
		try {
			try {
				javax.swing.UIManager
						.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
			} catch (Exception e) {
				e.printStackTrace();
			}

			JFileChooser chooser = new JFileChooser(".");
			chooser.setDialogTitle("Choose measures xml file");
			chooser.setFileFilter(new ExtensionFilter(new String[] { "xml" }));
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				final String fileName = chooser.getSelectedFile()
						.getAbsolutePath();

				Map<String, List<Measure>> measures = MeasuresParser
						.parse(fileName);
				MeasuresVisualizationRunner runner = new MeasuresVisualizationRunner(
						new LinkedList<String>(measures.keySet()));

				for (String name : measures.keySet()) {
					for (Measure measure : measures.get(name)) {
						runner.setCurrentHolonsNames(measure.getValues()
								.keySet());
						runner.update(measure, name);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
