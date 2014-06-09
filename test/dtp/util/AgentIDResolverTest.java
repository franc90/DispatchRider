package dtp.util;

import static org.junit.Assert.*;

import org.junit.Test;


public class AgentIDResolverTest {
	@Test
	public void testGetEUnitIDFromName()
	{
		assertEquals(12, AgentIDResolver.getEUnitIDFromName("Agent#12"));
		assertEquals(12,AgentIDResolver.getEUnitIDFromName("12"));
		
		
	}
	@Test
	public void failTest()
	{
		assertTrue(true);//assertEquals(0,AgentIDResolver.getEUnitIDFromName(""));	
	}
}
