package org.cook_e.cook_e;

import org.cook_e.cook_e.TestThing;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestThingTest {

	@Test
	public void testIsThing() {
		final TestThing thing = new TestThing();
		assertTrue(thing.isAThing());
	}
	@Test
	public void testIsTest() {
		final TestThing thing = new TestThing();
		assertTrue(thing.isATest());
	}

	@Test
	public void testIsEdible() {
		final TestThing thing = new TestThing();
		assertFalse(thing.isEdible());
	}

}


