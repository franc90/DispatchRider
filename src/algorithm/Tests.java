package algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * It's a very important class, which is used to test new algorithms.
 * 
 */
public class Tests {
	private final String benchmarksPath = "benchmarks";
	private final String pdp_100[] = { "lc106.txt", "lc107.txt", "lc108.txt",
			"lc109.txt", "lr106.txt", "lr107.txt", "lr108.txt", "lr109.txt",
			"lr110.txt", "lr111.txt", "lr112.txt", "lrc106.txt", "lrc107.txt",
			"lrc108.txt" };
	private final String best_pdp_100[] = { "10", "10", "10", "9", "12", "10",
			"9", "11", "10", "10", "9", "11", "11", "10" };
	private final String pdp_100_2[] = { "lc206.txt", "lc207.txt", "lc208.txt",
			"lr206.txt", "lr207.txt", "lr208.txt", "lr209.txt", "lr2010.txt",
			"lr211.txt", "lrc206.txt", "lrc207.txt", "lrc208.txt" };
	private final String best_pdp_100_2[] = { "3", "3", "3", "3", "2", "2",
			"3", "3", "2", "3", "3", "3" };
	private final String pdp_200[] = { "LC1_2_6.txt", "LC1_2_7.txt",
			"LC1_2_8.txt", "LC1_2_9.txt", "LC1_2_10.txt", "LR1_2_6.txt",
			"LR1_2_7.txt", "LR1_2_8.txt", "LR1_2_9.txt", "LR1_2_10.txt",
			"LRC1_2_6.txt", "LRC1_2_7.txt", "LRC1_2_8.txt", "LRC1_2_9.txt",
			"LRC1_2_10.txt" };
	private final String best_pdp_200[] = { "20", "20", "20", "18", "17", "14",
			"12", "9", "14", "11", "17", "14", "13", "13", "12" };
	private final String pdp_200_2[] = { "LC2_2_6.txt", "LC2_2_7.txt",
			"LC2_2_8.txt", "LC2_2_9.txt", "LC2_2_10.txt", "LR2_2_6.txt",
			"LR2_2_7.txt", "LR2_2_8.txt", "LR2_2_9.txt", "LR2_2_10.txt",
			"LRC2_2_6.txt", "LRC2_2_7.txt", "LRC2_2_8.txt", "LRC2_2_9.txt",
			"LRC2_2_10.txt" };
	private final String best_pdp_200_2[] = { "6", "6", "6", "6", "6", "4",
			"3", "2", "3", "3", "5", "4", "4", "4", "3" };
	/*
	 * private final String
	 * pdp_600[]={"LC1_6_1.txt","LC1_6_2.txt","LC1_6_3.txt",
	 * "LC1_6_4.txt","LC1_6_5.txt"
	 * ,"LR1_6_1.txt","LR1_6_2.txt","LR1_6_3.txt","LR1_6_4.txt"
	 * ,"LR1_6_5.txt","LRC1_6_1.txt"
	 * ,"LRC1_6_2.txt","LRC1_6_3.txt","LRC1_6_4.txt","LRC1_6_5.txt"}; private
	 * final String
	 * best_pdp_600[]={"60","58","50","47","60","59","45","37","28",
	 * "38","53","44","36","25","47"}; private final String
	 * pdp_600_2[]={"LC2_6_1.txt"
	 * ,"LC2_6_2.txt","LC2_6_3.txt","LC2_6_4.txt","LC2_6_5.txt"
	 * ,"LR2_6_1.txt","LR2_6_2.txt"
	 * ,"LR2_6_3.txt","LR2_6_4.txt","LR2_6_5.txt","LRC2_6_1.txt"
	 * ,"LRC2_6_2.txt","LRC2_6_3.txt","LRC2_6_4.txt","LRC2_6_5.txt"}; private
	 * final String
	 * best_pdp_600_2[]={"19","18","17","17","19","11","10","8","6",
	 * "9","16","14","10","7","14"};
	 */
	private final Map<String, String[]> tests;
	private final Map<String, String> paths;
	private final Map<String, String[]> bests;

	private ConfigureSTAlgorithmTests test;

	public Tests() {
		tests = new TreeMap<String, String[]>();
		paths = new TreeMap<String, String>();
		bests = new TreeMap<String, String[]>();

		tests.put("pdp_100", pdp_100);
		paths.put("pdp_100", "pdp_100");
		bests.put("pdp_100", best_pdp_100);

		tests.put("pdp_100_2", pdp_100_2);
		paths.put("pdp_100_2", "pdp_100");
		bests.put("pdp_100_2", best_pdp_100_2);

		tests.put("pdp_200", pdp_200);
		paths.put("pdp_200", "pdp_200");
		bests.put("pdp_200", best_pdp_200);

		tests.put("pdp_200_2", pdp_200_2);
		paths.put("pdp_200_2", "pdp_200");
		bests.put("pdp_200_2", best_pdp_200_2);

		/*
		 * tests.put("pdp_600", pdp_600); paths.put("pdp_600", "pdp_600");
		 * bests.put("pdp_600", best_pdp_600);
		 * 
		 * tests.put("pdp_600_2", pdp_600_2); paths.put("pdp_600_2", "pdp_600");
		 * bests.put("pdp_600_2", best_pdp_600_2);
		 */
	}

	@SuppressWarnings("unused")
	private void doPartTests(BufferedWriter wr, BufferedWriter wr_dist,
			BufferedWriter wr_time, String title, String chooseWorstCommission,
			int maxSTDepth) throws IOException {
		String fileName;

		String key = "pdp_200_2";
		wr.write(key);
		wr.newLine();
		wr.write("\tbest\ttime\tdist\tmix\tbrut1\tbrut2\t\ttime_dist\tdist_dist\tmix_dist\tbrut1_dist\tbrut2_dist");
		wr.newLine();

		wr_dist.write(key);
		wr_dist.newLine();
		wr_dist.write("\tbest\ttime\tdist\tmix\tbrut1\tbrut2\t\ttime_dist\tdist_dist\tmix_dist\tbrut1_dist\tbrut2_dist");
		wr_dist.newLine();

		wr_time.write(key);
		wr_time.newLine();
		wr_time.write("\tbest\ttime\tdist\tmix\tbrut1\tbrut2\t\ttime_dist\tdist_dist\tmix_dist\tbrut1_dist\tbrut2_dist");
		wr_time.newLine();

		int index = 0;
		String names[] = { "LC2_2_1.txt", "LC2_2_2.txt", "LC2_2_3.txt",
				"LC2_2_4.txt", "LC2_2_5.txt", "LR2_2_1.txt", "LR2_2_2.txt",
				"LR2_2_3.txt", "LR2_2_4.txt", "LR2_2_5.txt", "LRC2_2_1.txt",
				"LRC2_2_2.txt", "LRC2_2_3.txt", "LRC2_2_4.txt", "LRC2_2_5.txt" };
		String best[] = { "6", "6", "6", "6", "6", "5", "4", "4", "3", "4",
				"6", "5", "4", "3", "5" };
		double res[];
		for (String file : names) {
			fileName = benchmarksPath + File.separatorChar + paths.get(key);
			fileName = fileName + File.separatorChar + file;
			test = new ConfigureSTAlgorithmTests(fileName, maxSTDepth,
					chooseWorstCommission);
			wr.write(file + "\t" + best[index++] + "\t");
			wr.flush();
			wr_dist.write(file + "\t\t");
			wr_dist.flush();
			wr_time.write(file + "\t\t");
			wr_time.flush();
			res = test.newComplexST(new ConstrctionAlgorithm(
					ComparatorType.time), true);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST(new ConstrctionAlgorithm(
					ComparatorType.distance), true);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST(
					new ConstrctionAlgorithm(ComparatorType.mix), true);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST(new BruteForceAlgorithm(), false);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST(new BruteForceAlgorithm2(), false);
			wr.write(new Integer((int) res[0]).toString() + "\t\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t\t");
			wr_time.flush();

			res = test.newComplexST_dist(new ConstrctionAlgorithm(
					ComparatorType.time), true);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST_dist(new ConstrctionAlgorithm(
					ComparatorType.distance), true);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST_dist(new ConstrctionAlgorithm(
					ComparatorType.mix), true);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST_dist(new BruteForceAlgorithm(), false);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();
			res = test.newComplexST_dist(new BruteForceAlgorithm2(), false);
			wr.write(new Integer((int) res[0]).toString() + "\t");
			wr.flush();
			wr_dist.write(new Double(res[1]).toString() + "\t");
			wr_dist.flush();
			wr_time.write(new Double(res[2]).toString() + "\t");
			wr_time.flush();

			wr.newLine();
			wr.flush();
			wr_dist.newLine();
			wr_dist.flush();
			wr_time.newLine();
			wr_time.flush();
		}

		wr.newLine();
		wr.flush();
		wr_dist.newLine();
		wr_dist.flush();
		wr_time.newLine();
		wr_time.flush();
	}

	@SuppressWarnings("unused")
	private void doTests(BufferedWriter wr, String title,
			String chooseWorstCommission, int maxSTDepth) throws IOException {
		String fileName;
		int index = 0;
		wr.write(title);
		wr.newLine();
		for (String key : tests.keySet()) {
			wr.write(key);
			wr.newLine();
			wr.write("\tbest\ttime\tdist\tmix\tbrut1\tbrut2\t\ttime_dist\tdist_dist\tmix_dist\tbrut1_dist\tbrut2_dist");
			wr.newLine();
			index = 0;
			for (String file : tests.get(key)) {
				fileName = benchmarksPath + File.separatorChar + paths.get(key);
				fileName = fileName + File.separatorChar + file;
				test = new ConfigureSTAlgorithmTests(fileName, maxSTDepth,
						chooseWorstCommission);
				wr.write(file + "\t" + bests.get(key)[index++] + "\t");
				wr.write(new Integer(test.newST(new ConstrctionAlgorithm(
						ComparatorType.time))).toString() + "\t");
				wr.flush();
				wr.write(new Integer(test.newST(new ConstrctionAlgorithm(
						ComparatorType.distance))).toString() + "\t");
				wr.flush();
				wr.write(new Integer(test.newST(new ConstrctionAlgorithm(
						ComparatorType.mix))).toString() + "\t");
				wr.flush();
				wr.write(new Integer(test.newST(new BruteForceAlgorithm()))
						.toString() + "\t");
				wr.flush();
				wr.write(new Integer(test.newST(new BruteForceAlgorithm2()))
						.toString() + "\t\t");
				wr.flush();

				wr.write(new Integer(test.newST_dist(new ConstrctionAlgorithm(
						ComparatorType.time))).toString() + "\t");
				wr.flush();
				wr.write(new Integer(test.newST_dist(new ConstrctionAlgorithm(
						ComparatorType.distance))).toString() + "\t");
				wr.flush();
				wr.write(new Integer(test.newST_dist(new ConstrctionAlgorithm(
						ComparatorType.mix))).toString() + "\t");
				wr.flush();
				wr.write(new Integer(test.newST_dist(new BruteForceAlgorithm()))
						.toString() + "\t");
				wr.flush();
				wr.write(new Integer(test
						.newST_dist(new BruteForceAlgorithm2())).toString()
						+ "\t\t");

				wr.newLine();
				wr.flush();
			}
		}
		wr.newLine();
	}

	private void doTestsWithComplexST(BufferedWriter wr,
			BufferedWriter wr_dist, BufferedWriter wr_time, String title,
			String chooseWorstCommission, int maxSTDepth) throws IOException {
		String fileName;
		int index = 0;
		wr.write(title);
		wr.newLine();
		wr_dist.write(title);
		wr_dist.newLine();
		wr_time.write(title);
		wr_time.newLine();
		for (String key : tests.keySet()) {
			wr.write(key);
			wr.newLine();
			wr.write("\tbest\ttime\tdist\tmix\tbrut1\tbrut2\t\ttime_dist\tdist_dist\tmix_dist\tbrut1_dist\tbrut2_dist");
			wr.newLine();

			wr_dist.write(key);
			wr_dist.newLine();
			wr_dist.write("\tbest\ttime\tdist\tmix\tbrut1\tbrut2\t\ttime_dist\tdist_dist\tmix_dist\tbrut1_dist\tbrut2_dist");
			wr_dist.newLine();

			wr_time.write(key);
			wr_time.newLine();
			wr_time.write("\tbest\ttime\tdist\tmix\tbrut1\tbrut2\t\ttime_dist\tdist_dist\tmix_dist\tbrut1_dist\tbrut2_dist");
			wr_time.newLine();

			index = 0;
			double[] res;
			for (String file : tests.get(key)) {
				fileName = benchmarksPath + File.separatorChar + paths.get(key);
				fileName = fileName + File.separatorChar + file;
				test = new ConfigureSTAlgorithmTests(fileName, maxSTDepth,
						chooseWorstCommission);
				wr.write(file + "\t" + bests.get(key)[index++] + "\t");
				wr.flush();
				wr_dist.write(file + "\t\t");
				wr_dist.flush();
				wr_time.write(file + "\t\t");
				wr_time.flush();
				res = test.newComplexST(new ConstrctionAlgorithm(
						ComparatorType.time), true);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST(new ConstrctionAlgorithm(
						ComparatorType.distance), true);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST(new ConstrctionAlgorithm(
						ComparatorType.mix), true);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST(new BruteForceAlgorithm(), false);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST(new BruteForceAlgorithm2(), false);
				wr.write(new Integer((int) res[0]).toString() + "\t\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t\t");
				wr_time.flush();

				res = test.newComplexST_dist(new ConstrctionAlgorithm(
						ComparatorType.time), true);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST_dist(new ConstrctionAlgorithm(
						ComparatorType.distance), true);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST_dist(new ConstrctionAlgorithm(
						ComparatorType.mix), true);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST_dist(new BruteForceAlgorithm(), false);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();
				res = test.newComplexST_dist(new BruteForceAlgorithm2(), false);
				wr.write(new Integer((int) res[0]).toString() + "\t");
				wr.flush();
				wr_dist.write(new Double(res[1]).toString() + "\t");
				wr_dist.flush();
				wr_time.write(new Double(res[2]).toString() + "\t");
				wr_time.flush();

				wr.newLine();
				wr.flush();
				wr_dist.newLine();
				wr_dist.flush();
				wr_time.newLine();
				wr_time.flush();
			}
		}
		wr.newLine();
		wr.newLine();
		wr_dist.newLine();
		wr_dist.newLine();
		wr_time.newLine();
		wr_time.newLine();
	}

	public void run(String resultFileName) throws IOException {
		File result = new File(resultFileName);
		BufferedWriter wr = new BufferedWriter(new FileWriter(result, true));
		BufferedWriter wr_dist = new BufferedWriter(new FileWriter(new File(
				resultFileName.substring(0, resultFileName.length() - 4)
						+ "_dist.xls"), true));
		BufferedWriter wr_time = new BufferedWriter(new FileWriter(new File(
				resultFileName.substring(0, resultFileName.length() - 4)
						+ "_time.xls"), true));
		// doTests(wr, "Simmulated Trading - old, global time", true, 1);
		// doPartTests(wr, null, false, 1);
		// doTests(wr, "Simmulated Trading - old, wait time", false, 1);
		// wr.newLine();
		// wr.newLine();
		// System.out.println(Calendar.getInstance().getTime());
		// System.exit(0);
		for (int i = 8; i < 9; i++) {
			// doPartTests(wr, wr_dist, wr_time, "", true, i);
			doTestsWithComplexST(wr, wr_dist, wr_time,
					"Simmulated Trading - complex, global time, depth=" + i,
					"time", i);
			System.out.println(Calendar.getInstance().getTime());
			System.exit(0);
			doTestsWithComplexST(wr, wr_dist, wr_time,
					"Simmulated Trading - complex, wait time, depth=" + i,
					"wTime", i);
		}
	}

	public static void main(String args[]) {
		try {
			new Tests().run("res.xls");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
