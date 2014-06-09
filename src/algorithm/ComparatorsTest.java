package algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * It's class which was used to test different comparators for ConstructionAlgorithm 
 *
 */
public class ComparatorsTest {
	private final String benchmarksPath="benchmarks";
	private final String pdp_100[]={"lc101.txt","lc102.txt","lc103.txt","lc104.txt","lc105.txt","lr101.txt","lr102.txt","lr103.txt","lr104.txt","lr105.txt","lrc101.txt","lrc102.txt","lrc103.txt","lrc104.txt","lrc105.txt"};
	private final String best_pdp_100[]={"10","10","9","9","10","19","17","13","9","14","14","12","11","10","13"};
	private final String pdp_100_2[]={"lc201.txt","lc202.txt","lc203.txt","lc204.txt","lc205.txt","lr201.txt","lr202.txt","lr203.txt","lr204.txt","lr205.txt","lrc201.txt","lrc202.txt","lrc203.txt","lrc204.txt","lrc205.txt"};
	private final String best_pdp_100_2[]={"3","3","3","3","3","4","3","3","2","3","4","3","3","3","4"};
	private final String pdp_200[]={"LC1_2_1.txt","LC1_2_2.txt","LC1_2_3.txt","LC1_2_4.txt","LC1_2_5.txt","LR1_2_1.txt","LR1_2_2.txt","LR1_2_3.txt","LR1_2_4.txt","LR1_2_5.txt","LRC1_2_1.txt","LRC1_2_2.txt","LRC1_2_3.txt","LRC1_2_4.txt","LRC1_2_5.txt"};
	private final String best_pdp_200[]={"20","19","17","17","20","20","17","15","10","16","19","15","13","10","16"};
	private final String pdp_200_2[]={"LC2_2_1.txt","LC2_2_2.txt","LC2_2_3.txt","LC2_2_4.txt","LC2_2_5.txt","LR2_2_1.txt","LR2_2_2.txt","LR2_2_3.txt","LR2_2_4.txt","LR2_2_5.txt","LRC2_2_1.txt","LRC2_2_2.txt","LRC2_2_3.txt","LRC2_2_4.txt","LRC2_2_5.txt"};
	private final String best_pdp_200_2[]={"6","6","6","6","6","5","4","4","3","4","6","5","4","3","5"};
	private final String pdp_600[]={"LC1_6_1.txt","LC1_6_2.txt","LC1_6_3.txt","LC1_6_4.txt","LC1_6_5.txt","LR1_6_1.txt","LR1_6_2.txt","LR1_6_3.txt","LR1_6_4.txt","LR1_6_5.txt","LRC1_6_1.txt","LRC1_6_2.txt","LRC1_6_3.txt","LRC1_6_4.txt","LRC1_6_5.txt"};
	private final String best_pdp_600[]={"60","58","50","47","60","59","45","37","28","38","53","44","36","25","47"};
	private final String pdp_600_2[]={"LC2_6_1.txt","LC2_6_2.txt","LC2_6_3.txt","LC2_6_4.txt","LC2_6_5.txt","LR2_6_1.txt","LR2_6_2.txt","LR2_6_3.txt","LR2_6_4.txt","LR2_6_5.txt","LRC2_6_1.txt","LRC2_6_2.txt","LRC2_6_3.txt","LRC2_6_4.txt","LRC2_6_5.txt"};
	private final String best_pdp_600_2[]={"19","18","17","17","19","11","10","8","6","9","16","14","10","7","14"};

	private Map<String,String[]> tests;
	private Map<String,String> paths;
	private Map<String, String[]> bests;
	
	private AlgorithmTest timeComparatorAlg;
	private AlgorithmTest distComparatorAlg;
	private AlgorithmTest mixComparatorAlg;
	
	public ComparatorsTest() {
		tests=new TreeMap<String, String[]>();
		paths=new TreeMap<String, String>();
		bests=new TreeMap<String,String[]>();
		
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
		
		tests.put("pdp_600", pdp_600);
		paths.put("pdp_600", "pdp_600");
		bests.put("pdp_600", best_pdp_600);
		
		tests.put("pdp_600_2", pdp_600_2);
		paths.put("pdp_600_2", "pdp_600");
		bests.put("pdp_600_2", best_pdp_600_2);
	}
	
	public void run(String resultFileName) throws IOException {
		String fileName;
		File result=new File(resultFileName);
		BufferedWriter wr=new BufferedWriter(new FileWriter(result));
		int index=0;
		wr.write("test1");
		wr.newLine();
		for(String key:tests.keySet()) {
			wr.write(key);
			wr.newLine();
			wr.write("\tbest\tcurrent\ttime\tdist\tmix\tbrut1\tbrut2");
			wr.newLine();
			index=0;
			for(String file:tests.get(key)) {
				fileName=benchmarksPath+File.separatorChar+paths.get(key);
				fileName=fileName+File.separatorChar+file;
				timeComparatorAlg = new AlgorithmTest(fileName,ComparatorType.time);
				distComparatorAlg = new AlgorithmTest(fileName, ComparatorType.distance);
				mixComparatorAlg = new AlgorithmTest(fileName, ComparatorType.mix);
				wr.write(file+"\t"+bests.get(key)[index++]+"\t");
				wr.write(new Integer(timeComparatorAlg.test()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.newTest()).toString()+"\t");
				wr.write(new Integer(distComparatorAlg.newTest()).toString()+"\t");
				wr.write(new Integer(mixComparatorAlg.newTest()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.new2Test()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.new3Test()).toString());
				wr.newLine();
				wr.flush();
			}
		}
		wr.newLine();
		wr.newLine();
		wr.newLine();
		wr.write("test2");
		wr.newLine();
		for(String key:tests.keySet()) {
			wr.write(key);
			wr.newLine();
			wr.write("\tbest\tcurrent\ttime\tdist\tmix\tbrut1\tbrut1_dist\tbrut2\tbrut2_dist");
			wr.newLine();
			index=0;
			for(String file:tests.get(key)) {
				fileName=benchmarksPath+File.separatorChar+paths.get(key);
				fileName=fileName+File.separatorChar+file;
				timeComparatorAlg = new AlgorithmTest(fileName,ComparatorType.time);
				distComparatorAlg = new AlgorithmTest(fileName, ComparatorType.distance);
				mixComparatorAlg = new AlgorithmTest(fileName, ComparatorType.mix);
				wr.write(file+"\t"+bests.get(key)[index++]+"\t");
				wr.write(new Integer(timeComparatorAlg.test2()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.newTest2()).toString()+"\t");
				wr.write(new Integer(distComparatorAlg.newTest2()).toString()+"\t");
				wr.write(new Integer(mixComparatorAlg.newTest2()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.new2Test2()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.new2Test2_dist()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.new3Test2()).toString()+"\t");
				wr.write(new Integer(timeComparatorAlg.new3Test2_dist()).toString());
				wr.newLine();
				wr.flush();
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			new ComparatorsTest().run("res.xls");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
