package com.instantiations.test.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Utility methods for testing collections
 * <p>
 * Copyright (c) 2006, 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 * 
 * @author Dan Rubel
 * @author Phil Quitslund
 */
public class TestCollection
{
	/**
	 * Assert that the actual collection contains the expected elements
	 * but may contain more.
	 * 
	 * @param expected the expected elements (but there may be more)
	 * @param actual the collection to be tested
	 */
	public static void assertContains(Object[] expected, Collection<Object> actual) {
		int index = testContains(expected, actual);
		if (index != -1)
			fail("Missing element: " + expected[index], expected, actual);
	}	
	
	/**
	 * Assert that the actual array contains the expected elements
	 * 
	 * @param expected the expected elements
	 * @param actual the actual array to be tested
	 */
	public static void assertContainsOnly(Object[] expected, Object[] actual) {
		assertContainsOnly(expected, Arrays.asList(actual));
	}

	public static void assertContainsOnly(Object[] expected, Object[] actual, Comparator<Object> comparator) {
		assertContainsOnly(expected, Arrays.asList(actual), comparator);
	}
	
	/**
	 * Assert that the actual collection contains the expected elements
	 * 
	 * @param expected the expected elements
	 * @param actual the actual collection to be tested
	 */
	public static void assertContainsOnly(Object[] expected, Collection<Object> actual) {
		if (!testContainsOnly(expected, actual))
			fail("Collections are not equal:", expected, actual);
	}
	
	public static void assertContainsOnly(Object[] expected, Collection<Object> actual, Comparator<Object> comparator) {
		if (!testContainsOnly(expected, actual, comparator))
			fail("Collections are not equal:", expected, actual);
	}
	
	private static boolean testContainsOnly(Object[] expected, Collection<Object> actual, Comparator<Object> comparator) {
		if (expected == null)
			return actual == null;
		if (actual == null || expected.length != actual.size())
			return false;
		return testContains(expected, actual, comparator) == -1;
	}
	
	
	private static boolean testContainsOnly(Object[] expected, Collection<Object> actual) {
		if (expected == null)
			return actual == null;
		if (actual == null || expected.length != actual.size())
			return false;
		return testContains(expected, actual) == -1;
	}

	private static int testContains(Object[] expected, Collection<Object> actual) {
		for (int i = 0; i < expected.length; i++)
			if (!actual.contains(expected[i]))
				return i;
		return -1;
	}

	private static int testContains(Object[] expected, Collection<Object> actual, Comparator<Object> comparator) {
		for (int i = 0; i < expected.length; i++)
			if (!testContains(actual, expected[i], comparator))
				return i;
		return -1;
	}
	
	private static boolean testContains(Collection<Object> collection, Object elem, Comparator<Object> comparator) {
		for (Iterator<Object> iterator = collection.iterator(); iterator.hasNext();) {
			if (comparator.compare(iterator.next(), elem) == 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Fail with the specified message and the content of the specified collections
	 * 
	 * @param message the message
	 * @param expected the expected elements
	 * @param actual the actual collection
	 */
	public static void fail(String message, Object[] expected, Collection<Object> actual) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		writer.println(message);
		if (expected == null) {
			writer.println("Expected:" + " <null>");
		}
		else {
			writer.println("Expected:");
			for (int i = 0; i < expected.length; i++) {
				writer.print(i);
				writer.print(": ");
				writer.println(expected[i]);
			}
		}
		if (actual == null) {
			writer.println("Actual:" + " <null>");
		}
		else {
			writer.println("Actual:");
			int i = 0;
			for (Iterator<Object> iter = actual.iterator(); iter.hasNext();) {
				writer.print(i);
				writer.print(": ");
				writer.println(iter.next());
				i++;
			}
		}
		TestCase.fail(stringWriter.toString());
	}
}
