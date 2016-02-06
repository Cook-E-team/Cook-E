package org.cook_e.data;

import org.cook_e.data.Duration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the Duration class
 */
public class DurationTest {

	@Test
	public void testMillisZero() {
		testMillis(0);
	}
	@Test
	public void testMillisMax() {
		testMillis(Long.MAX_VALUE);
	}
	@Test
	public void testMillisOne() {
		testMillis(1);
	}
	@Test
	public void testOneSecond() {
		final long millis = 1000l;
		final Duration duration = Duration.milliseconds(millis);
		assertNotNull(duration);
		assertEquals(millis, duration.getMilliseconds());

		assertEquals("Not 1 second", 1.0, duration.getSeconds(), 1E-6);
		assertEquals("Not 1/60 minute", 1.0 / 60.0, duration.getMinutes(), 1E-6);
		assertEquals("Not 1/(60 * 60) hour", 1.0 / 60.0 / 60.0, duration.getHours(), 1E-6);
	}

	@Test
	public void testOneMinute() {
		final long millis = 60000l;
		final Duration duration = Duration.milliseconds(millis);
		assertNotNull(duration);
		assertEquals(millis, duration.getMilliseconds());

		assertEquals("Not 60 seconds", 60.0, duration.getSeconds(), 1E-6);
		assertEquals("Not 1 minute", 1.0, duration.getMinutes(), 1E-6);
		assertEquals("Not 1/60 hour", 1.0 / 60.0, duration.getHours(), 1E-6);
	}


	@Test
	public void testOneHour() {
		final long millis = 1000l * 60l * 60l;
		final Duration duration = Duration.milliseconds(millis);
		assertNotNull(duration);
		assertEquals(millis, duration.getMilliseconds());

		assertEquals("Not 60 * 60 seconds", 60.0 * 60.0, duration.getSeconds(), 1E-6);
		assertEquals("Not 60 minutes", 60.0, duration.getMinutes(), 1E-6);
		assertEquals("Not 1 hour", 1.0, duration.getHours(), 1E-6);
	}


	private void testMillis(long millis) {
		final Duration duration = Duration.milliseconds(millis);
		assertNotNull(duration);
		assertEquals("Constructor and getMilliseconds() not consistent", millis, duration.getMilliseconds());
	}
}
