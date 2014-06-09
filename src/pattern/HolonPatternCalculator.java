package pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;

public class HolonPatternCalculator {

	private List<TransportElementInitialData> drivers=new LinkedList<TransportElementInitialData>();
	private List<TransportElementInitialDataTruck> trucks=new LinkedList<TransportElementInitialDataTruck>();
	private List<TransportElementInitialDataTrailer> trailers=new LinkedList<TransportElementInitialDataTrailer>();
	private String fileName;
	
	public HolonPatternCalculator(String fileName) {
		this.fileName=fileName;
		try {
			loadDriversProperties(fileName+File.separator+"drivers.properties");
			loadTrailersProperties(fileName+File.separator+"trailers.properties");
			loadTrucksProperties(fileName+File.separator+"trucks.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected void loadDriversProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);
        
        String line=br.readLine();
        String[] lineParts=line.split("\t");
        int driversCount = Integer.parseInt(lineParts[0]);
        String costFunction="0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
        if(lineParts.length>1) costFunction=lineParts[1];
        
        for (int i = 0; i < driversCount; i++) {
            drivers.add(new TransportElementInitialData(costFunction,100000, 100000, 0));
        }
    }
    
    protected void loadTrucksProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);
        
        String firstLine=br.readLine();
        String[] parts=firstLine.split("\t");
        int trucksCount = Integer.parseInt(parts[0]);
        String defaultCostFunction=null;
        if(parts.length>1) defaultCostFunction=parts[1];
        
        for (int i = 0; i < trucksCount; i++) {
        	        	
        	String lineParts[] = br.readLine().split("\t");

            int power           = Integer.parseInt(lineParts[0]);
            int reliability     = Integer.parseInt(lineParts[1]);
            int comfort         = Integer.parseInt(lineParts[2]);
            int fuelConsumption = Integer.parseInt(lineParts[3]);
            int connectorType   = Integer.parseInt(lineParts[4]);
            String costFunction;
            if(lineParts.length==6) {
            		costFunction = lineParts[5];
            } else {
            	if(defaultCostFunction!=null) {
            		costFunction=defaultCostFunction;
            	} else {
            		costFunction="0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
            	}
            }
            
            trucks.add(new TransportElementInitialDataTruck(costFunction,power, 0, 0, power, reliability, comfort, fuelConsumption, connectorType));
        }
    }
    
    
    protected void loadTrailersProperties(String filePath) throws IOException {
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);
        
        String firstLine=br.readLine();
        String[] parts=firstLine.split("\t");
        int trailersCount = Integer.parseInt(parts[0]);
        String defaultCostFunction=null;
        if(parts.length>1) defaultCostFunction=parts[1];
        
        for (int i = 0; i < trailersCount; i++) {
        	
        	String lineParts[] = br.readLine().split("\t");

            int mass          = Integer.parseInt(lineParts[0]);
            int capacity      = Integer.parseInt(lineParts[1]);
            int cargoType     = Integer.parseInt(lineParts[2]);
            int universality  = Integer.parseInt(lineParts[3]);
            int connectorType = Integer.parseInt(lineParts[4]);
            String costFunction;
            if(lineParts.length==6){
            	costFunction = lineParts[5];
            } else {
            	if(defaultCostFunction!=null) {
            		costFunction=defaultCostFunction;
            	} else {
            		costFunction="0.01*dist*(4-comfort)+(dist/100)*fuel*((mass+load)/power)";
            	}
            }
            
            trailers.add(new TransportElementInitialDataTrailer(costFunction,capacity, 0, 0, mass, capacity, cargoType, universality, connectorType));
        }
        br.close();
        fr.close();
    }
	
	public String getFileName() {
		return fileName;
	}
	
	private double average(List<Double> values) {
		double result=0.0;
		for(Double v:values) result+=v;
		result/=values.size();
		return result;
	}
	
	private double standardDeviation(List<Double> values) {
		double result=0.0;
		double avg=average(values);
		for(Double v:values) result+=Math.pow(v-avg, 2);
		result=Math.sqrt(result);
		return result;
	}
	
	
	/* Srednia z ladownosci przyczep */
	public Double pattern1() {
		List<Double> values=new LinkedList<Double>();
		for(TransportElementInitialDataTrailer trailer:trailers) {
			values.add((double)trailer.getCapacity());
		}
		return average(values);
	}
	
	/* Odchylenie standardowe z ladownosci przyczep */
	public Double pattern2() {
		List<Double> values=new LinkedList<Double>();
		for(TransportElementInitialDataTrailer trailer:trailers) {
			values.add((double)trailer.getCapacity());
		}
		return standardDeviation(values);
	}
	
	public static void main(String args[]) {
		//HolonPatternCalculator calc=new HolonPatternCalculator("holonPatterns//smallHolonTests//50_50");
		HolonPatternCalculator calc=new HolonPatternCalculator("holonPatterns//holonConfig//25_200");
		System.out.println(calc.pattern1());
		System.out.println(calc.pattern2());
	}
	
}
