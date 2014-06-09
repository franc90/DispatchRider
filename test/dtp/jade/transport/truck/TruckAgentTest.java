package dtp.jade.transport.truck;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TruckAgentTest {
	TruckAgent ta;
	
	@Before
	public void setUp() throws Exception {
		ta = new TruckAgent();
		ta.setCapacity(500);
		ta.setBooked(false);
		ta.setDefaultCapacity(200);
	}

	@Test
	public void testGetCapacity() {
		assertEquals(500, ta.getCapacity());
		
	}

	@Test
	public void testGetDefaultCapacity() {
		assertEquals(200, ta.getDefaultCapacity());
	}

	@Test
	public void testSetDefaultCapacity() {
		ta.setDefaultCapacity(20);
		assertEquals(20, ta.getDefaultCapacity());
	}

	@Test
	public void testIsBooked() {
		assertFalse(ta.isBooked());
	}

}
