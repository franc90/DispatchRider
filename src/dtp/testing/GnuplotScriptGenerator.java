package dtp.testing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class GnuplotScriptGenerator {

    private static Logger logger = Logger.getLogger(GnuplotScriptGenerator.class);
    private String titleOfDiagram, titleX, titleY;
    private double values[][];
    private String plotAddition = null;
    /**
     * Gnuplot usage not implemented.
     * 
     * @deprecated
     */
    private int typeX;
    /**
     * Gnuplot usage not implemented.
     * 
     * @deprecated
     */
    private int typeY;

    public static final int LINNEAR = 1;
    public static final int LOG = 2;

    public GnuplotScriptGenerator() {
        super();
    }

    /**
     * Writes data to gnuplot script
     * 
     * @param root
     *        root of script, data and image filename, .gp, .dat .png will be generated defines root of filename
     *        (without extension) of target diagram image Hardcoded image type is png, and '.png' is added to imageroot.
     */
    public void writeToFiles(String root) {

        try {
            if (values == null || values.length == 0)
                return;

            double minX = values[0][1];
            double maxX = values[0][1];
            double minY = values[0][2];
            double maxY = values[0][2];

            for (int i = 1, ln = values.length; i < ln; i++) {
                if (values[i][1] < minX)
                    minX = values[i][2];
                if (values[i][1] > maxX)
                    maxX = values[i][2];
                if (values[i][2] < minY)
                    minX = values[i][2];
                if (values[i][2] > maxY)
                    maxX = values[i][2];
            }

            FileWriter writer = new FileWriter(new File(root + ".gp"));
            writer.write("# -- Gnuplot script automagically generated with AgentDTP program. --\n");
            writer.write("set terminal png medium transparent\n");
            writer.write("set output './" + root + ".png'\n");
            writer.write("set title \"" + titleOfDiagram + "\"\t\n");
            writer.write("set xlabel \"" + titleX + "\"\t\n");
            writer.write("set ylabel \"" + titleY + "\"\t\n");
            // ["+(minX-1)+":"+(maxX+1)+"] ["+(minY-1)+":"+(maxY+1)+"]
            writer.write("plot '" + root + ".dat' title\"" + root + "\"");
            if (plotAddition != null)
                writer.write(", " + plotAddition);
            writer.write("\n");
            writer.flush();
            writer.close();

            writer = new FileWriter(new File(root + ".dat"));
            writer.write(" # -- Gnuplot data file automagically generated with with ContextualCorrection program.\n");
            for (int i = 0, ln = values.length; i < ln; i++)
                writer.write(values[i][1] + " " + values[i][2] + "\n");
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error("GnuplotScriptGenerator.writeToFile(): " + e.toString());
        }
    }

    public String getTitleOfDiagram() {
        return titleOfDiagram;
    }

    public void setTitleOfDiagram(String titleOfDiagram) {
        this.titleOfDiagram = titleOfDiagram;
    }

    public String getTitleX() {
        return titleX;
    }

    public void setTitleX(String titleX) {
        this.titleX = titleX;
    }

    public String getTitleY() {
        return titleY;
    }

    public void setTitleY(String titleY) {
        this.titleY = titleY;
    }

    public int getTypeX() {
        return typeX;
    }

    public void setTypeX(int typeX) {
        this.typeX = typeX;
    }

    public int getTypeY() {
        return typeY;
    }

    public void setTypeY(int typeY) {
        this.typeY = typeY;
    }

    public double[][] getValues() {
        return values;
    }

    public void setValues(double[][] values) {
        this.values = values;
    }

    public GnuplotScriptGenerator(String titleOfDiagram, String titleX, String titleY, double minX, double maxX,
            double minY, double maxY, double[][] values) {
        super();
        this.titleOfDiagram = titleOfDiagram;
        this.titleX = titleX;
        this.titleY = titleY;
        this.values = values;
    }

    public String getPlotAddition() {
        return plotAddition;
    }

    public void setPlotAddition(String plotAddition) {
        this.plotAddition = plotAddition;
    }
}
