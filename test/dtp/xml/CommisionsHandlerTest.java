package dtp.xml;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class CommisionsHandlerTest extends TestCase {
	CommissionsHandler ch;
	@Before
	public void setUp()
	{
		ch = new CommissionsHandler();
	}
	@Test
	public void testHandler()
	{
		assertEquals(ch.getCommissions().size(), 0);
	}
}
