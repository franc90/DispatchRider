package gui.map;

import java.awt.Color;

import dtp.graph.GraphLink;

public class HolonGraphLink extends GraphLink {

	private static final long serialVersionUID = 1L;
	private Color color;
	public Color getColor() {
		return color;
	}
	public HolonGraphLink(Color color) {
		super();
		this.color = color;
	}
}
