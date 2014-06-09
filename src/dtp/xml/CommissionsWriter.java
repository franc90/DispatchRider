package dtp.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import dtp.commission.CommissionHandler;
import dtp.graph.Graph;

//
/**
 * @author Grzegorz
 */
public class CommissionsWriter {

	/** Logger. */
	private static Logger logger = Logger.getLogger(CommissionsWriter.class);

	private final HashSet<CommissionHandler> commissionList;

	private Graph world;

	public void clearSet() {
		commissionList.clear();
	}

	public void addCommission(CommissionHandler ch) {
		commissionList.add(ch);
	}

	public CommissionHandler[] getCommissions() {
		return commissionList.toArray(new CommissionHandler[commissionList
				.size()]);
	}

	public CommissionsWriter() {
		commissionList = new HashSet<CommissionHandler>();
	}

	public CommissionsWriter(CommissionHandler[] comms) {
		commissionList = new HashSet<CommissionHandler>();
		if (comms != null)
			for (int i = 0, ln = comms.length; i < ln; i++)
				addCommission(comms[i]);
	}

	public void saveAsXmlFile(File file) {

		Iterator<CommissionHandler> commIter = commissionList.iterator();
		CommissionHandler currentComm;
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("<?xml version=\"1.0\"?>\r\n");
			writer.write("<commissionlist xmlns=\"https://opensvn.csie.org/AgentDTP\"\r\n");
			writer.write("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n");
			writer.write("\txsi:schemaLocation=\"https://opensvn.csie.org/AgentDTP commissions.xsd\">\r\n");

			while (commIter.hasNext()) {
				currentComm = commIter.next();

				writer.write("\t<commission>\r\n");
				writer.write("\t\t<id> " + currentComm.getCommission().getID()
						+ " </id>\r\n");

				writer.write("\t\t<pickupTime1> "
						+ currentComm.getCommission().getPickupTime1()
						+ " </pickupTime1>\r\n");
				writer.write("\t\t<pickupTime2> "
						+ currentComm.getCommission().getPickupTime2()
						+ " </pickupTime2>\r\n");
				writer.write("\t\t<pickupX> "
						+ currentComm.getCommission().getPickupX()
						+ " </pickupX>\r\n");
				writer.write("\t\t<pickupY> "
						+ currentComm.getCommission().getPickupY()
						+ " </pickupY>\r\n");
				writer.write("\t\t<deliveryX> "
						+ currentComm.getCommission().getDeliveryX()
						+ " </deliveryX>\r\n");
				writer.write("\t\t<deliveryY> "
						+ currentComm.getCommission().getDeliveryY()
						+ " </deliveryY>\r\n");
				writer.write("\t\t<deliveryTime1> "
						+ currentComm.getCommission().getDeliveryTime1()
						+ " </deliveryTime1>\r\n");
				writer.write("\t\t<deliveryTime2> "
						+ currentComm.getCommission().getDeliveryTime2()
						+ " </deliveryTime2>\r\n");
				writer.write("\t\t<load> "
						+ currentComm.getCommission().getLoad()
						+ " </load>\r\n");
				writer.write("\t\t<pickUpServiceTime> "
						+ currentComm.getCommission().getPickUpServiceTime()
						+ " </pickUpServiceTime>\r\n");
				writer.write("\t\t<DeliveryServiceTime> "
						+ currentComm.getCommission().getDeliveryServiceTime()
						+ " </DeliveryServiceTime>\r\n");
				writer.write("\t\t<incomeTime> " + currentComm.getIncomeTime()
						+ " </incomeTime>\r\n");
				writer.write("\t</commission>\r\n");
			}

			writer.write("</commissionlist>");

			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error("IOException: " + e.getMessage());
		}

	}

	public Graph getWorld() {
		return world;
	}

	public void setWorld(Graph gm) {
		this.world = gm;
	}
}
