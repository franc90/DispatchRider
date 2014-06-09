package machineLearning.clustering;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Vector;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class KMeansTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (!Rengine.versionCheck()) {
			System.err
					.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");
		// 1) we pass the arguments from the command line
		// 2) we won't use the main loop at first, we'll start it later
		// (that's the "false" as second argument)
		// 3) the callbacks are implemented by the TextConsole class above
		Rengine re = new Rengine(args, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}

		// REXP rp = re.eval("options()");

		// System.out.println("OPTS:\n" + rp);

		int[] cells = { 1, 1, 2, 1, 4, 3, 5, 4 };
		String[] rnames = { "A", "B", "C", "D" };
		String[] cnames = { "X", "Y" };

		re.assign("cells", cells);
		re.assign("rnames", rnames);
		re.assign("cnames", cnames);

		re.eval("print(cells)");

		REXP res = re
				.eval("x <- matrix(cells, nrow=4, ncol=2, byrow=TRUE, dimnames=list(rnames, cnames))");
		REXP km = re.eval("km <- kmeans(x, 2, 15)");

		re.eval("print(km$centers)");

		System.out.println(((REXP) km.asVector().get(0)).asIntArray()[0]);

		double[] centres = ((REXP) km.asVector().get(1)).asDoubleArray();
		re.assign("ctrs", centres);
		System.out.println(((REXP) km.asVector().get(1)));
		System.out.println(Arrays.toString(centres));
		
		
		
		
//		re.assign("oldcent", centres);
//		
		double[] newCell={4.5,3.5};
		String[] newRNames={"E"};
		
		re.assign("newrn", newRNames);
		re.assign("newc",newCell);
//		REXP ww=re.eval("z <- matrix(newc, nrow=1, ncol=2, byrow=TRUE, dimnames=list(newrn, cnames))");
		REXP ww=re.eval("z <- matrix(newc, nrow=1, ncol=2, byrow=TRUE)");
		
		
		
//		re.eval("newkm <- kmeans(z,2,15)");
		
	
//		re.eval("print(z)");
		
		System.out.println(km.getClass().getName());
		
		REXP rp=new REXP(centres);
		re.assign("rp", rp);
		
		
//		
		
//		REXP find=re.eval("ww <- kmeans(z,km$centers,10)");
//
//		re.eval("print(km$centers)");
//		System.out.println("NEW\n"+find);
//		re.eval("print(ww$clusters)");
		
		re.eval("library(clue)");
		re.eval("x <- list(centers = km$centers)");
		re.eval("class(x) <- \"kmeans\"");
		
		
		REXP predict=re.eval("pr <- cl_predict(x,z)");
		System.out.println(predict.asInt());
//		REXP predict=re.eval("pr <- predict(km$centers,z)"); //err
		
//		System.out.println("PREDICT:\n"+predict);

//		System.out.println(km);
		
//		System.out.println(find);

		// re.assign("km",km);
		// re.eval("print(km)");
		// re.eval("plot.new()");
		// re.eval("plot(x, col = km$cluster");
		// re.eval("points(km$centers, col = 1:2, pch = 8)");
		// points(km$centers, col = 1:2, pch = 8)
		// re.eval("plot(x)");

		System.exit(0);
	}

}

class TextConsole implements RMainLoopCallbacks {
	public void rWriteConsole(Rengine re, String text, int oType) {
		System.out.print(text);
	}

	public void rBusy(Rengine re, int which) {
		System.out.println("rBusy(" + which + ")");
	}

	public String rReadConsole(Rengine re, String prompt, int addToHistory) {
		System.out.print(prompt);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String s = br.readLine();
			return (s == null || s.length() == 0) ? s : s + "\n";
		} catch (Exception e) {
			System.out.println("jriReadConsole exception: " + e.getMessage());
		}
		return null;
	}

	public void rShowMessage(Rengine re, String message) {
		System.out.println("rShowMessage \"" + message + "\"");
	}

	public String rChooseFile(Rengine re, int newFile) {
		FileDialog fd = new FileDialog(new Frame(),
				(newFile == 0) ? "Select a file" : "Select a new file",
				(newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
		fd.show();
		String res = null;
		if (fd.getDirectory() != null)
			res = fd.getDirectory();
		if (fd.getFile() != null)
			res = (res == null) ? fd.getFile() : (res + fd.getFile());
		return res;
	}

	public void rFlushConsole(Rengine re) {
	}

	public void rLoadHistory(Rengine re, String filename) {
	}

	public void rSaveHistory(Rengine re, String filename) {
	}
}
