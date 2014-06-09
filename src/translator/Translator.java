package translator;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;

import algorithm.Helper;
import dtp.commission.Commission;
import dtp.commission.TxtFileReader;
import dtp.gui.ExtensionFilter;

public class Translator {

	private static double calculateFurthestCommissionDriveTime(
			List<Commission> coms, Point2D.Double depot) {
		double result = 0.0;
		double dist;
		for (Commission com : coms) {
			dist = Helper.calculateDistance(depot,
					new Point2D.Double(com.getPickupX(), com.getPickupY()));
			if (dist > result)
				result = dist;
		}
		return result;
	}

	private static double calculateFurthestTwoCommissionsDriveTime(
			List<Commission> coms, Point2D.Double depot) {
		double result = 0.0;
		double dist;
		Commission com1;
		Commission com2;
		for (int i = 0; i < coms.size(); i++) {
			com1 = coms.get(i);
			for (int j = i + 1; j < coms.size(); j++) {
				com2 = coms.get(j);
				dist = Helper
						.calculateDistance(
								depot,
								new Point2D.Double(com1.getPickupX(), com1
										.getPickupX()));
				dist += Helper
						.calculateDistance(
								new Point2D.Double(com1.getPickupX(), com1
										.getPickupX()),
								new Point2D.Double(com2.getPickupX(), com2
										.getPickupY()));
				if (dist > result)
					result = dist;
			}
		}
		return result;
	}

	private static List<Commission> readCommissions(String fileName) {
		List<Commission> commissions = new LinkedList<Commission>();
		for (Commission com : TxtFileReader.getCommissions(fileName))
			commissions.add(com);
		return commissions;
	}

	private static Point2D.Double readDepot(String fileName) {
		int depotX = (int) TxtFileReader.getDepot(fileName).getX();
		int depotY = (int) TxtFileReader.getDepot(fileName).getY();

		return new Point2D.Double(depotX, depotY);
	}

	public static void translateA(String from, String to) {// String fileName,
															// String sourceDir,
															// String destDir) {
		List<Integer> incomeTimes = new LinkedList<Integer>();
		List<Commission> coms = readCommissions(from);// sourceDir+File.separator+fileName);
		Point2D.Double depot = readDepot(from);// sourceDir+File.separator+fileName);
		double time = calculateFurthestCommissionDriveTime(coms, depot);
		int incomeTime;
		for (Commission com : coms) {
			incomeTime = (int) (com.getPickupTime1() - time);
			if (incomeTime < 0)
				incomeTime = 0;
			incomeTimes.add(incomeTime);
		}
		saveResults(from, to, incomeTimes);// fileName,sourceDir,destDir,incomeTimes);
	}

	public static void translateB(String from, String to) {// String fileName,
															// String sourceDir,
															// String destDir) {
		List<Integer> incomeTimes = new LinkedList<Integer>();
		List<Commission> coms = readCommissions(from);// sourceDir+File.separator+fileName);
		Point2D.Double depot = readDepot(from);// sourceDir+File.separator+fileName);
		double time = calculateFurthestTwoCommissionsDriveTime(coms, depot);
		int incomeTime;
		for (Commission com : coms) {
			incomeTime = (int) (com.getPickupTime1() - time);
			if (incomeTime < 0)
				incomeTime = 0;
			incomeTimes.add(incomeTime);
		}
		saveResults(from, to, incomeTimes);// fileName, sourceDir,destDir,
											// incomeTimes);
	}

	private static void saveResults(String from, String to,
			List<Integer> incomeTimes) {// String fileName, String sourceDir,
										// String destDir, List<Integer>
										// incomeTimes) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(from));// sourceDir+File.separator+fileName));
			BufferedWriter writer = new BufferedWriter(new FileWriter(to));// destDir+File.separator+fileName));
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
			writer.flush();
			reader.close();
			writer.close();
			writer = new BufferedWriter(new FileWriter(to/*
														 * destDir+File.separator
														 * +fileName
														 */+ ".income_times"));
			for (Integer time : incomeTimes) {
				writer.write(time.toString());
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {
		System.out.println("Podaj lokalizacje benchmarku do skonwertowania");
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("Open benchmark filee");
		chooser.setFileFilter(new ExtensionFilter(new String[] { "txt" }));
		String file = null;
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile().getAbsolutePath();
		} else {
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		System.out
				.print("Ktory algorytm uzyc (1 - oba, 2 - dynamicA, 3 - dynamicB): ");
		String input = reader.readLine();
		reader.close();
		if (input.length() == 0 || input.equals("1")) {
			translateDynamicA(file);
			translateDynamicB(file);
		} else if (input.equals("2")) {
			translateDynamicA(file);
		} else if (input.equals("3")) {
			translateDynamicB(file);
		} else {
			translateDynamicA(file);
			translateDynamicB(file);
		}

	}

	private static void translateDynamicA(String from) {
		System.out
				.println("Podaj nazwe pliku gdzie zapisac wynik dla dynamicA");
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("Save dynamic benchmark");
		chooser.setFileFilter(new ExtensionFilter(new String[] { "txt" }));
		String outFile = null;
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			outFile = chooser.getSelectedFile().getAbsolutePath();
			Translator.translateA(from, outFile);
		} else {
			return;
		}
	}

	private static void translateDynamicB(String from) {
		System.out
				.println("Podaj nazwe pliku gdzie zapisac wynik dla dynamicB");
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("Save dynamic benchmark");
		chooser.setFileFilter(new ExtensionFilter(new String[] { "txt" }));
		String outFile = null;
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			outFile = chooser.getSelectedFile().getAbsolutePath();
			Translator.translateB(from, outFile);
		} else {
			return;
		}
	}
}
