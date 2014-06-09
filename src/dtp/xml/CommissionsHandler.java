package dtp.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;

/**
 * @author Grzegorz A SAX content handler that creates Drivers, Trucks and
 *         Trailers objects
 */
public class CommissionsHandler extends DefaultHandler {

	private boolean idTag, pickupXTag, pickupYTag, pickupTime1Tag,
			pickupTime2Tag, deliveryXTag, deliveryYTag, deliveryTime1Tag,
			deliveryTime2Tag, loadTag, serviceTimeTag, incomeTimeTag;

	private int id, pickupTime1, pickupTime2, pickupX, pickupY, deliveryX,
			deliveryY, deliveryTime1, deliveryTime2, load, serviceTime,
			incomeTime;

	private final ArrayList<CommissionHandler> commissions;

	public CommissionsHandler() {

		idTag = false;
		pickupXTag = false;
		pickupYTag = false;
		pickupTime1Tag = false;
		pickupTime2Tag = false;
		deliveryXTag = false;
		deliveryYTag = false;
		deliveryTime1Tag = false;
		deliveryTime2Tag = false;
		loadTag = false;
		serviceTimeTag = false;
		incomeTimeTag = false;
		commissions = new ArrayList<CommissionHandler>();
	}

	public ArrayList<CommissionHandler> getCommissions() {
		return this.commissions;
	}

	/**
	 * Sets boolean variables depending on which tag is considered at the
	 * momement
	 */
	@Override
	public void startElement(String namespaceUri, String localName,
			String qualifiedName, Attributes attributes) throws SAXException {
		if (qualifiedName.equals("id"))
			idTag = true;
		else if (qualifiedName.equals("pickupX"))
			pickupXTag = true;
		else if (qualifiedName.equals("pickupY"))
			pickupYTag = true;
		else if (qualifiedName.equals("pickupTime1"))
			pickupTime1Tag = true;
		else if (qualifiedName.equals("pickupTime2"))
			pickupTime2Tag = true;
		else if (qualifiedName.equals("deliveryX"))
			deliveryXTag = true;
		else if (qualifiedName.equals("deliveryY"))
			deliveryYTag = true;
		else if (qualifiedName.equals("deliveryTime1"))
			deliveryTime1Tag = true;
		else if (qualifiedName.equals("deliveryTime2"))
			deliveryTime2Tag = true;
		else if (qualifiedName.equals("load"))
			loadTag = true;
		else if (qualifiedName.equals("serviceTime"))
			serviceTimeTag = true;
		else if (qualifiedName.equals("incomeTime"))
			incomeTimeTag = true;
	}

	/**
	 * Unsets boolean variables depending on which closing tag is considered at
	 * the momement
	 */
	@Override
	public void endElement(String namespaceUri, String localName,
			String qualifiedName) throws SAXException {

		if (qualifiedName.equals("commission")) {
			Commission comm = new Commission(id, pickupX, pickupY, pickupTime1,
					pickupTime2, deliveryX, deliveryY, deliveryTime1,
					deliveryTime2, load, serviceTime, serviceTime);
			commissions.add(new CommissionHandler(comm, incomeTime));
		} else if (qualifiedName.equals("id"))
			idTag = false;
		else if (qualifiedName.equals("pickupX"))
			pickupXTag = false;
		else if (qualifiedName.equals("pickupY"))
			pickupYTag = false;
		else if (qualifiedName.equals("pickupTime1"))
			pickupTime1Tag = false;
		else if (qualifiedName.equals("pickupTime2"))
			pickupTime2Tag = false;
		else if (qualifiedName.equals("deliveryX"))
			deliveryXTag = false;
		else if (qualifiedName.equals("deliveryY"))
			deliveryYTag = false;
		else if (qualifiedName.equals("deliveryTime1"))
			deliveryTime1Tag = false;
		else if (qualifiedName.equals("deliveryTime2"))
			deliveryTime2Tag = false;
		else if (qualifiedName.equals("load"))
			loadTag = false;
		else if (qualifiedName.equals("serviceTime"))
			serviceTimeTag = false;
		else if (qualifiedName.equals("incomeTime"))
			incomeTimeTag = false;
	}

	/**
	 * Reads values of concrete tags depending on location where the parser is
	 * at the moment
	 */
	@Override
	public void characters(char[] chars, int startIndex, int endIndex) {

		if (idTag) {
			id = Integer.parseInt(new String(chars, startIndex, endIndex));
		} else if (pickupTime1Tag) {
			pickupTime1 = Integer.parseInt(new String(chars, startIndex,
					endIndex));
		} else if (pickupTime2Tag) {
			pickupTime2 = Integer.parseInt(new String(chars, startIndex,
					endIndex));
		} else if (pickupXTag) {
			pickupX = Integer.parseInt(new String(chars, startIndex, endIndex));
		} else if (pickupYTag) {
			pickupY = Integer.parseInt(new String(chars, startIndex, endIndex));
		} else if (deliveryTime1Tag) {
			deliveryTime1 = Integer.parseInt(new String(chars, startIndex,
					endIndex));
		} else if (deliveryTime2Tag) {
			deliveryTime2 = Integer.parseInt(new String(chars, startIndex,
					endIndex));
		} else if (deliveryXTag) {
			deliveryX = Integer
					.parseInt(new String(chars, startIndex, endIndex));
		} else if (deliveryYTag) {
			deliveryY = Integer
					.parseInt(new String(chars, startIndex, endIndex));
		} else if (loadTag) {
			load = Integer.parseInt(new String(chars, startIndex, endIndex));
		} else if (serviceTimeTag) {
			serviceTime = Integer.parseInt(new String(chars, startIndex,
					endIndex));
		} else if (incomeTimeTag) {
			incomeTime = Integer.parseInt(new String(chars, startIndex,
					endIndex));
		}
	}
}
