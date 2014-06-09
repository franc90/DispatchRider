package dtp.graph;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GraphLinkTest {

	GraphLink gl;
	
	@Before
	public void setUp() throws Exception {
		gl = new GraphLink();
	}
	@SuppressWarnings("deprecation")
	@Test
	public void testCost() {
		assertEquals(0, gl.getCost());
		gl.setCost(20);
		assertEquals(20, gl.getCost());
	}

}
