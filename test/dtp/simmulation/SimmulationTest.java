package dtp.simmulation;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SimmulationTest {
	Simmulation s;
	@Before
	public void setUp() throws Exception {
		s = new Simmulation();
	}
	
	@Test
	public void testNulls()
	{
		assertNull(s.getGraph());
		assertNull(s.getSimInfo());
		
	}

}
