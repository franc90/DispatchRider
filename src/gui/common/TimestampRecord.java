package gui.common;

public class TimestampRecord {
	int timestamp;
	private Object data;
	
	public void setData(Object data) {
		this.data = data;
	}


	TimestampRecord() {
		timestamp = 0;
		data = null;
	}
	
	
	public int getTimestamp() {
		return timestamp;
	}


	TimestampRecord(int ts, Object data) {
		timestamp = ts;
		this.data = data;
	}
	
	public Object getData() {
		return data;
	}
}

