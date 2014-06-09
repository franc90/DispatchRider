package measure.printer;

import java.util.LinkedList;
import java.util.List;

import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureCalculatorsHolder;

public class PrintersHolder {

	private final List<MeasurePrinter> printers = new LinkedList<MeasurePrinter>();

	public void addPrinter(String extension) throws IllegalArgumentException {
		String ext = extension.toUpperCase();
		try {
			MeasurePrinter printer = (MeasurePrinter) Class.forName(
					"measure.printer." + ext + "Printer").newInstance();
			printers.add(printer);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Wrong parameter at 'formats' attribute: " + extension);
		}
	}

	public List<MeasurePrinter> getPrinters() {
		return printers;
	}

	private List<String> getColumns(MeasureCalculatorsHolder holder) {
		List<String> columns = new LinkedList<String>();
		columns.add("holonId");
		for (MeasureCalculator calc : holder.getCalculators())
			columns.add(calc.getName());
		return columns;
	}

	public void print(String fileName, MeasureData data,
			MeasureCalculatorsHolder holder) throws Exception {

		List<String> columns = getColumns(holder);
		for (MeasurePrinter printer : printers) {
			printer.createDocument(fileName);
			printer.printColumns(columns);
			for (List<Measure> measuresList : data) {
				printer.printNextPart(measuresList);
			}
			printer.finish();
		}
	}
}
