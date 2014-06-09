package dtp.graph;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class GraphPointTest {
	GraphPoint g, gp1, gp2;
	
	@Before
	public void setUp()
	{
		g = new GraphPoint(1.5, 4.0);
		gp1 = new GraphPoint(2.5, 4.0);
		gp2 = new GraphPoint(1.5, 4.0);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetX() {
		assertEquals(1.5, g.getX());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetY() {
		assertEquals(4.0, g.getY());
	}

	@Test
	public void testSetName() {
		g.setName("graph");
		assertEquals("graph", g.getName());
	}

	@Test
	public void testHasSameCoordinates() {
		assertTrue(g.hasSameCoordinates(gp2));
		assertFalse(g.hasSameCoordinates(gp1));
	}

}
