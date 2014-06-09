package measure.printer;

import java.io.Serializable;
import java.util.List;

import measure.Measure;

public interface MeasurePrinter extends Serializable {
	public void createDocument(String fileName) throws Exception;

	public void printColumns(List<String> columns);

	public void printNextPart(List<Measure> measures);

	public void finish() throws Exception;
}
