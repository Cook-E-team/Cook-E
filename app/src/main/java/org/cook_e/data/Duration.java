/*
 * Copyright 2016 the Cook-E development team
 *
 * This file is part of Cook-E.
 *
 * Cook-E is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cook-E is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.data;

/**
 * Represents a duration, with millisecond precision
 *
 * Objects of this class are immutable.
 */
public final class Duration implements Comparable<Duration> {

	/**
	 * The maximum number of seconds that can be used to create a duration without overflow
	 */
	private static final long MAX_SECONDS = Long.MAX_VALUE / 1000l;
	/**
	 * The maximum number of minutes that can be used to create a duration without overflow
	 */
	private static final long MAX_MINUTES = MAX_SECONDS / 60l;
	/**
	 * The maximum number of hours that can be used to create a duration without overflow
	 */
	private static final long MAX_HOURS = MAX_MINUTES / 60l;

	/**
	 * The number of milliseconds in this duration
	 */
	private final long mMillis;

	/**
	 * Creates a new Duration as a copy of an existing Duration
	 * @param other the duration to copy
	 */
	public Duration(Duration other) {
		this(other.mMillis);
	}

	/**
	 * Creates a new duration with a specified number of milliseconds
	 * @param milliseconds the number of milliseconds
	 * @throws IllegalArgumentException if milliseconds is negative
	 */
	private Duration(long milliseconds) {
		if (milliseconds < 0) {
			throw new IllegalArgumentException("A duration cannot be negative");
		}
		mMillis = milliseconds;
	}
	/**
	 * Creates a new duration with a specified number of milliseconds
	 * @param milliseconds the number of milliseconds
	 * @throws IllegalArgumentException if milliseconds is negative
	 * @return a new Duration
	 */
	public static Duration milliseconds(long milliseconds) {
		return new Duration(milliseconds);
	}

	/**
	 * Creates a new duration to represent a specified number of seconds
	 * @param seconds the number of seconds
	 * @return a new Duration
	 * @throws IllegalArgumentException if seconds is negative or greater than an implementation-
	 * defined limit
	 */
	public static Duration seconds(long seconds) {
		if (seconds > MAX_SECONDS) {
			throw new IndexOutOfBoundsException("Number of seconds exceeds limit");
		}
		if (seconds < 0) {
			throw new IndexOutOfBoundsException("Number of seconds must not be negative");
		}
		return new Duration(seconds * 1000);
	}

	/**
	 * Creates a new duration to represent a specified number of minutes
	 * @param minutes the number of minutes
	 * @return a new Duration
	 * @throws IllegalArgumentException if minutes is negative or greater than an implementation-
	 * defined limit
	 */
	public static Duration minutes(long minutes) {
		if (minutes > MAX_MINUTES) {
			throw new IndexOutOfBoundsException("Number of minutes exceeds limit");
		}
		if (minutes < 0) {
			throw new IndexOutOfBoundsException("Number of minutes must not be negative");
		}
		return new Duration(minutes * 1000 * 60);
	}

	/**
	 * Creates a new duration to represent a specified number of hours
	 * @param hours the number of hours
	 * @return a new Duration
	 * @throws IllegalArgumentException if hours is negative or greater than an implementation-
	 * defined limit
	 */
	public static Duration hours(long hours) {
		if (hours > MAX_HOURS) {
			throw new IndexOutOfBoundsException("Number of hours exceeds limit");
		}
		if (hours < 0) {
			throw new IndexOutOfBoundsException("Number of hours must not be negative");
		}
		return new Duration(hours * 1000 * 60 * 60);
	}

	/**
	 * Returns the number of milliseconds in this duration
	 * @return the number of milliseconds
	 */
	public long getMilliseconds() {
		return mMillis;
	}

	/**
	 * Returns the number of seconds in this duration
	 * @return the number of seconds
	 */
	public double getSeconds() {
		return ((double) mMillis) / 1000.0;
	}

	/**
	 * Returns the number of minutes in this duration
	 * @return the number of minutes
	 */
	public double getMinutes() {
		return ((double) mMillis) / 1000.0 / 60.0;
	}

	/**
	 * Returns the number of hours in this duration
	 * @return the number of hours
	 */
	public double getHours() {
		return ((double) mMillis) / 1000.0 / 60.0 / 60.0;
	}

	/**
	 * Compares this Duration to another object
	 * @param o the object to compare to
	 * @return true if o is a Duration with the same time, otherwise false
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Duration duration = (Duration) o;

		return mMillis == duration.mMillis;

	}

	/**
	 * Returns a hash code of this Duration
	 * @return a hash code
	 */
	@Override
	public int hashCode() {
		return (int) (mMillis ^ (mMillis >>> 32));
	}

	/**
	 * Returns a String representation of this Duration
	 * @return a String
	 */
	@Override
	public String toString() {
		return mMillis + " milliseconds";
	}

	/**
	 * Compares this duration to another
	 * @param another a duration to compare
	 * @return a negative integer if this instance is less than another; a positive integer if this
	 * instance is greater than another; 0 if this instance has the same order as another.
	 */
	@Override
	public int compareTo(Duration another) {
		if (mMillis > another.mMillis) {
			return 1;
		}
		else if (mMillis < another.mMillis) {
			return -1;
		}
		else {
			return 0;
		}
	}
}
