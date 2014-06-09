package benchmark;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFileChooser;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.gui.ExtensionFilter;
import dtp.optimization.Dijkstra;
import dtp.optimization.TrackFinder;
import dtp.xml.GraphParser;

public class SimpleBenhmarkGenerator {

	private final List<CommissionDesc> commissions = new LinkedList<CommissionDesc>();
	private CommissionDesc base;
	private TrackFinder trackFinder;

	private final List<GraphPoint> points = new LinkedList<GraphPoint>();

	private final Random rand;
	private Graph graph;
	private int numberOfCommissions;
	private int maxTimeWindowSize;
	private int minTimeWindowSize;
	private int numberOfHolons;
	private int loadOfCommission;
	private int simTime = 0;
	private int maxDepartureShift;
	private int serviceTime;

	public SimpleBenhmarkGenerator() {
		this.rand = new Random();
		this.rand.setSeed(Calendar.getInstance().getTimeInMillis());
	}

	private String getFile(String title, String ext) {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle(title);
		chooser.setFileFilter(new ExtensionFilter(new String[] { ext }));
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	public void generate() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.println("Podaj parametry generatora:");
			System.out.print("1. Wybierz plik z opisem grafu: ");
			String graphFile = getFile("Choose graph file", "xml");
			if (graphFile == null) {
				System.out.println("\n");
				System.out.println("Generacja przerwana przez uzytkownika");
				return;
			}
			System.out.println(graphFile);
			this.graph = new GraphParser().parse(graphFile);
			int pointsSize = graph.getPointsSize();
			System.out.println("Iloœæ punktów w grafie: " + pointsSize);
			System.out
					.print("2. Podaj iloœæ zleceñ (liczba musi byæ mniejsza od "
							+ (Math.floor((pointsSize - 1) / 2)) + "): ");
			this.numberOfCommissions = Integer.parseInt(reader.readLine());
			if (this.numberOfCommissions > Math.floor(pointsSize / 2) - 1) {
				System.out.println("Podana liczba zleceñ jest niew³aœciwa");
				return;
			}
			System.out.print("3. Podaj iloœæ holonów (musi byæ <= "
					+ this.numberOfCommissions + "): ");
			this.numberOfHolons = Integer.parseInt(reader.readLine());
			if (this.numberOfHolons > this.numberOfCommissions) {
				System.out.println("Za du¿o holonów");
				return;
			}
			System.out.print("4. Min d³ugoœæ okna czasowego: ");
			this.minTimeWindowSize = Integer.parseInt(reader.readLine());
			System.out.print("5. Max d³ugoœæ okna czasowego: ");
			this.maxTimeWindowSize = Integer.parseInt(reader.readLine());
			if (this.minTimeWindowSize > this.maxTimeWindowSize) {
				System.out
						.println("Min d³ugoœæ okna czaowego nie mo¿e byæ wiêksza od max d³ugoœci");
				return;
			}
			System.out
					.print("6. Ile (masa) ma przewoziæ ka¿dy holon w ramach zlecenia: ");
			this.loadOfCommission = Integer.parseInt(reader.readLine());
			System.out
					.print("7. Podaj max opóŸnienie w realizacji zlecenia - max czas po którym mo¿na wyjechaæ z bazy i zd¹¿yæ zrealizowaæ zlecenie: ");
			this.maxDepartureShift = Integer.parseInt(reader.readLine());
			System.out.print("8. Podaj service time: ");
			this.serviceTime = Integer.parseInt(reader.readLine());
			this.trackFinder = new Dijkstra(graph);
			for (int i = 0; i < graph.getPointsSize(); i++) {
				points.add(graph.getPoint(i));
			}

			chooseBase();
			int numOfCommsPerHolon = (int) Math.floor(this.numberOfCommissions
					/ this.numberOfHolons);
			int rest = this.numberOfCommissions - numOfCommsPerHolon
					* this.numberOfHolons;
			for (int i = 0; i < this.numberOfHolons - 1; i++) {
				createCommissionsForNextHolon(numOfCommsPerHolon);
			}
			createCommissionsForNextHolon(numOfCommsPerHolon + rest);

			this.base.setEndOfTW(this.simTime);

			String fileName;
			System.out.print("Gdzie zapisaæ wynik:");
			JFileChooser chooser = new JFileChooser(".");
			chooser.setDialogTitle("Zapisz wynik");
			chooser.setFileFilter(new ExtensionFilter(new String[] { "txt" }));
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				fileName = chooser.getSelectedFile().getAbsolutePath();
				if (!fileName.endsWith(".txt"))
					fileName = fileName + ".txt";
			} else {
				System.out.println("\n");
				System.out.println("Generacja przerwana przez uzytkownika");
				return;
			}

			BenchmarkSaver.save(fileName, this.base, this.commissions,
					this.loadOfCommission, this.serviceTime);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void chooseBase() {
		int index = rand.nextInt(points.size());
		GraphPoint point = points.get(index);
		this.points.remove(index);
		CommissionDesc com = new CommissionDesc();
		com.setPoint(point);
		base = com;
	}

	private void createCommissionsForNextHolon(int numOfCommissions) {
		GraphPoint currentPoint = base.getPoint();
		GraphPoint nextPoint;
		CommissionDesc com;
		int index;
		double driveTime;
		double summaryHolonTime = rand.nextInt(this.maxDepartureShift);
		double[] timeWins;
		List<CommissionDesc> coms = new LinkedList<CommissionDesc>();
		for (int i = 0; i < 2 * numOfCommissions; i++) {
			com = new CommissionDesc();
			index = rand.nextInt(points.size());
			nextPoint = points.remove(index);
			com.setPoint(nextPoint);
			driveTime = trackFinder.findTrack(currentPoint, nextPoint)
					.getCost();
			summaryHolonTime += driveTime;
			timeWins = getTimeWindow(summaryHolonTime);
			com.setBeginOfTW((int) timeWins[0]);
			com.setEndOfTW((int) timeWins[1]);
			summaryHolonTime += this.serviceTime;
			coms.add(com);
			currentPoint = nextPoint;
		}

		summaryHolonTime += trackFinder
				.findTrack(currentPoint, base.getPoint()).getCost();

		if (summaryHolonTime > this.simTime)
			this.simTime = (int) summaryHolonTime + 10;
		matchParts(coms);
		for (CommissionDesc c : coms) {
			c.setId(this.commissions.size() + 1);
			this.commissions.add(c);
		}
	}

	private double[] getTimeWindow(double time) {
		int size = 0;
		while (size == 0)
			size = rand
					.nextInt(this.maxTimeWindowSize - this.minTimeWindowSize);
		int shift = rand.nextInt(size);
		return new double[] { time - shift, time + (size - shift) };
	}

	private void matchParts(List<CommissionDesc> coms) {
		CommissionDesc pickup;
		CommissionDesc delivery;
		int index;
		int size;
		for (int i = 0; i < coms.size(); i++) {
			pickup = coms.get(i);
			if (pickup.getSecondPart() != null)
				continue;
			pickup.setPickup(true);
			size = coms.size() - (i + 1);
			if (size > 0) {
				do {
					index = i + 1 + rand.nextInt(size);
					delivery = coms.get(index);
				} while (delivery.getSecondPart() != null);
			} else {
				delivery = coms.get(i + 1);
			}
			pickup.setSecondPart(delivery);
			delivery.setPickup(false);
			delivery.setSecondPart(pickup);
		}
	}

	public static void main(String args[]) {
		SimpleBenhmarkGenerator generator = new SimpleBenhmarkGenerator();
		generator.generate();
	}
}
