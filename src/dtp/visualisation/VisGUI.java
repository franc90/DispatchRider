package dtp.visualisation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dtp.graph.Graph;
import dtp.graph.GraphLink;
import dtp.graph.GraphPoint;
import dtp.jade.eunit.EUnitInfo;

public class VisGUI extends JFrame {

	private static final long serialVersionUID = 129380124L;
	// panel zewnetrzny
	private JPanel outerPanel = null;
	// panel do rysowania
	private VisPanel aVisPanel = null;
	private Graph aGraph;

	private final int problemType;

	public VisGUI(Graph aGraph, int problemType) {
		super();

		this.aGraph = aGraph;
		try {
			this.setSize(662, 522);
			setTitle("Dispatch Rider - Network graph");
			outerPanel = new JPanel();
			getContentPane().add(outerPanel, BorderLayout.CENTER);
			outerPanel.setSize(660, 510);
			outerPanel.setPreferredSize(new Dimension(654, 510));
			aVisPanel = new VisPanel(aGraph, this);
			aVisPanel.setSize(640, 480);
			aVisPanel.setBackground(Color.white);
			aVisPanel.setPreferredSize(new Dimension(640, 480));
			aVisPanel.setOpaque(true);
			outerPanel.add(aVisPanel);
			aVisPanel.repaint();
		} catch (Exception ex) {
		}

		this.problemType = problemType;
	}

	public void setLinkActive(GraphLink ln) {
		InfoPopup popup = new InfoPopup(aGraph, ln);
		popup.setVisible(true);
	}

	public void setPointActive(GraphPoint pt) {
		InfoPopup popup = new InfoPopup(aGraph, pt);
		popup.setVisible(true);
	}

	public Dimension getPanelDimension() {

		// :(
		return new Dimension(640, 480);
	}

	public int getProblemType() {

		return problemType;
	}

	public void updateGraph(Graph graph) {

		aGraph = graph;
		aVisPanel.updateGraph(graph);
	}

	public void updateEUnitsInfo(EUnitInfo eUnitInfo) {

		aVisPanel.updateEunitInfo(eUnitInfo);
	}

	public void updateMany(EUnitInfo[] eUnitInfos) {

		aVisPanel.updateMany(eUnitInfos);
	}
}
