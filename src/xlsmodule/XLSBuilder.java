package xlsmodule;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XLSBuilder {

	private final Workbook wb;
	private final Sheet sheet;
	private final List<String> columns;
	private List<List<Double>> rows;
	private boolean columnNamesWritten = false;
	private int offset = 0;

	public XLSBuilder() {
		wb = new HSSFWorkbook();
		sheet = wb.createSheet("sheet");
		columns = new LinkedList<String>();
		rows = new LinkedList<List<Double>>();
	}

	public void setColumns(Iterable<String> columns) {
		for (String col : columns)
			this.columns.add(col);
	}

	public void setColumns(String columns[]) {
		for (String col : columns)
			this.columns.add(col);
	}

	public void addRow(Iterable<Double> rowData) {
		List<Double> row = new LinkedList<Double>();
		for (Double obj : rowData)
			row.add(obj);
		if (row.size() != columns.size())
			throw new IllegalArgumentException(
					"Row size is different from number of columns");
		rows.add(row);
	}

	public void addRow(Double rowData[]) {
		List<Double> row = new LinkedList<Double>();
		for (Double obj : rowData)
			row.add(obj);
		if (row.size() != columns.size())
			throw new IllegalArgumentException(
					"Row size is different from number of columns");
		rows.add(row);
	}

	public void writePart(String fileName, String[] separators)
			throws IOException {
		Row row;
		Cell cell;
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		FileOutputStream fileOut;
		if (columnNamesWritten == false) {
			row = sheet.createRow(offset++);
			CellStyle columnStyle = wb.createCellStyle();
			columnStyle.setAlignment(CellStyle.ALIGN_CENTER);
			columnStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			Font font = wb.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			columnStyle.setFont(font);
			for (int colNr = 0; colNr < columns.size(); colNr++) {
				cell = row.createCell(colNr);
				cell.setCellValue(columns.get(colNr));
				cell.setCellStyle(columnStyle);
			}
			fileOut = new FileOutputStream(fileName, true);
			columnNamesWritten = true;
		}
		fileOut = new FileOutputStream(fileName);

		row = sheet.createRow(offset++);
		int colNr = 0;
		for (String separator : separators) {
			cell = row.createCell(colNr++);
			cell.setCellValue(separator);
			cell.setCellStyle(cellStyle);
		}

		for (int rowNr = 0; rowNr < rows.size(); rowNr++) {
			row = sheet.createRow(rowNr + offset);
			List<Double> rowData = rows.get(rowNr);
			for (colNr = 0; colNr < columns.size(); colNr++) {
				cell = row.createCell(colNr);
				cell.setCellValue(rowData.get(colNr));
				cell.setCellStyle(cellStyle);
			}
		}

		offset += rows.size();

		for (colNr = 0; colNr < columns.size(); colNr++)
			sheet.autoSizeColumn(colNr);

		offset++;
		wb.write(fileOut);
		fileOut.close();
		rows = new LinkedList<List<Double>>();
	}
}
