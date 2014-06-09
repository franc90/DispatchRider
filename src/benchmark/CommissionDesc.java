package benchmark;

import dtp.graph.GraphPoint;

class CommissionDesc {
	private GraphPoint point;
	private boolean isPickup;
	private int beginOfTW;
	private int endOfTW;
	private CommissionDesc secondPart;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public GraphPoint getPoint() {
		return point;
	}

	public void setPoint(GraphPoint point) {
		this.point = point;
	}

	public boolean isPickup() {
		return isPickup;
	}

	public void setPickup(boolean isPickup) {
		this.isPickup = isPickup;
	}

	public int getBeginOfTW() {
		return beginOfTW;
	}

	public void setBeginOfTW(int beginOfTW) {
		this.beginOfTW = beginOfTW;
	}

	public int getEndOfTW() {
		return endOfTW;
	}

	public void setEndOfTW(int endOfTW) {
		this.endOfTW = endOfTW;
	}

	public CommissionDesc getSecondPart() {
		return secondPart;
	}

	public void setSecondPart(CommissionDesc secondPart) {
		this.secondPart = secondPart;
	}
}
