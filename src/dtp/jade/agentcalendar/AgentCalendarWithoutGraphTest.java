package dtp.jade.agentcalendar;

import java.awt.geom.Point2D;

import dtp.commission.Commission;
import dtp.jade.eunit.LoadNotOkException;

public class AgentCalendarWithoutGraphTest {

	public static void main(String[] args) throws LoadNotOkException {

		AgentCalendarWithoutGraph calendar = new AgentCalendarWithoutGraph(200,
				new Point2D.Double(0, 0), 120);
		int timestamp = 0;

		Commission[] coms = new Commission[4];
		coms[0] = new Commission(0, 0, 20, 0, 0, 200, 0, 30, 0, 0, 200, 10, 5,
				5);
		coms[1] = new Commission(1, 0, 30, 0, 0, 200, 0, 40, 0, 0, 200, 15, 5,
				5);
		coms[2] = new Commission(2, 0, 20, 0, 0, 200, 0, 30, 10, 0, 200, 20, 0,
				0);
		// coms[3] = new Commission(3, 25, 0, 0, 100, 5, 0, 0, 100, 90, 2, 2);

		System.out.println("\n*******************************************");
		System.out.println("Calendar before:");
		calendar.print();
		System.out.println("\n*******************************************");

		System.out.println("\n*******************************************");
		System.out.println("addCommission:");
		coms[0].printCommision();
		System.out.println("Extra dist = "
				+ calendar.getExtraDistance(coms[0], timestamp));
		calendar.addCommission(coms[0], timestamp);

		System.out.println("Calendar after:");
		calendar.print();
		System.out.println("\n*******************************************");

		System.out.println("\n*******************************************");
		System.out.println("addCommission:");
		coms[1].printCommision();
		System.out.println("Extra dist = "
				+ calendar.getExtraDistance(coms[1], timestamp));
		calendar.addCommission(coms[1], timestamp);

		System.out.println("Calendar after:");
		calendar.print();
		System.out.println("\n*******************************************");

		System.out.println("\n*******************************************");
		System.out.println("addCommission:");
		coms[2].printCommision();
		System.out.println("Extra dist = "
				+ calendar.getExtraDistance(coms[2], timestamp));
		calendar.addCommission(coms[2], timestamp);

		System.out.println("Calendar after:");
		calendar.print();
		System.out.println("\n*******************************************");

		int currentTimestamp = 0;
		Commission remCom = calendar.getWorstCommission(currentTimestamp, 1);
		System.out.println("Worst commission:\n" + remCom.toString());
	}
}
