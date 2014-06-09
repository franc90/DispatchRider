package dtp.jade.distributor;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AuctionTest {
	Auction a;
	
	@Before
	public void setUp() throws Exception {
		a = new Auction();
	}
	
	@Test
	public void testGetSentOffersNo()
	{
		assertEquals(0, a.getSentOffersNo());
	}
}
