package measure.visualization;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import machineLearning.aggregator.AggregatorsManager;
import machineLearning.aggregator.MLAggregator;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MeasureVisualizationControl extends JFrame {

	private static final long serialVersionUID = 1L;
	private final Map<String, MeasureVisualizationWindow> panels;
	private final Set<String> aids = new TreeSet<String>();
	private JPanel panel;

	public MeasureVisualizationControl(
			Map<String, MeasureVisualizationWindow> panels) {
		this.panels = panels;
		initComponents();
		AggregatorsManager manager = new AggregatorsManager();
		for (MLAggregator aggegator : manager.getAggregators())
			register(aggegator.getName());
	}

	private void initComponents() {
		setLayout(new FlowLayout());
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(170, 280));
		JScrollPane scroll = new JScrollPane(panel);
		scroll.setPreferredSize(new Dimension(180, 280));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll);
		setSize(200, 300);
	}

	public void register(String holon) {
		if (aids.contains(holon))
			return;
		aids.add(holon);
		JCheckBox box = new JCheckBox(holon);
		box.setMargin(new Insets(10, 10, 0, 0));
		box.setSelected(true);
		box.addActionListener(new BoxListener(box, holon));
		panel.add(box);
		int newHeight = 10 + aids.size() * 30;
		if (newHeight > panel.getPreferredSize().getHeight()) {
			panel.setPreferredSize(new Dimension((int) panel.getPreferredSize()
					.getWidth(), newHeight));
		}
		panel.revalidate();
	}

	private class BoxListener implements ActionListener {

		private final JCheckBox box;
		private final String holon;

		public BoxListener(JCheckBox box, String holon) {
			this.box = box;
			this.holon = holon;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (box.isSelected()) {
				for (MeasureVisualizationWindow window : panels.values())
					window.show(holon);
			} else {
				for (MeasureVisualizationWindow window : panels.values())
					window.hide(holon);
			}
		}
	}
}