package com.windowtester.test.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import junit.framework.TestCase;

/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
public class TestCollection
{
	/**
	 * Assert that the actual collection contains the expected elements
	 * but may contain more.
	 * 
	 * @param expected the expected elements (but there may be more)
	 * @param actual the collection to be tested
	 */
	public static void assertContains(Object[] expected, Collection actual) {
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

	public static void assertContainsOnly(Collection expected, Collection actual) {
		assertContainsOnly(expected.toArray(), actual);
	}
	
	
	public static void assertContainsOnly(Object[] expected, Object[] actual, Comparator comparator) {
		assertContainsOnly(expected, Arrays.asList(actual), comparator);
	}
	
	/**
	 * Assert that the actual collection contains the expected elements
	 * 
	 * @param expected the expected elements
	 * @param actual the actual collection to be tested
	 */
	public static void assertContainsOnly(Object[] expected, Collection actual) {
		if (!testContainsOnly(expected, actual))
			fail("Collections are not equal:", expected, actual);
	}
	
	public static void assertContainsOnly(Object[] expected, Collection actual, Comparator comparator) {
		if (!testContainsOnly(expected, actual, comparator))
			fail("Collections are not equal:", expected, actual);
	}
	
	private static boolean testContainsOnly(Object[] expected, Collection actual, Comparator comparator) {
		if (expected == null)
			return actual == null;
		if (actual == null || expected.length != actual.size())
			return false;
		return testContains(expected, actual, comparator) == -1;
	}
	
	
	private static boolean testContainsOnly(Object[] expected, Collection actual) {
		if (expected == null)
			return actual == null;
		if (actual == null || expected.length != actual.size())
			return false;
		return testContains(expected, actual) == -1;
	}

	private static int testContains(Object[] expected, Collection actual) {
		for (int i = 0; i < expected.length; i++)
			if (!actual.contains(expected[i]))
				return i;
		return -1;
	}

	private static int testContains(Object[] expected, Collection actual, Comparator comparator) {
		for (int i = 0; i < expected.length; i++)
			if (!testContains(actual, expected[i], comparator))
				return i;
		return -1;
	}
	
	private static boolean testContains(Collection collection, Object elem, Comparator comparator) {
		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
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
	public static void fail(String message, Object[] expected, Collection actual) {
		fail(message, expected, actual, new Comparator<Object>(){
			public int compare(Object o1, Object o2) {
				return o1 == o2 ? 0 : -1;
			}
		});
	}
	
	public static void fail(String message, Object[] expected, Collection actual, Comparator comparator) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		writer.println(message);
		if (expected == null) {
			writer.println("Expected:" + " <null>");
		}
		else {
			writer.println("Expected:");
			boolean mismatch;
			for (int i = 0; i < expected.length; i++) {
				mismatch = false;
				if (i < actual.size()-1 && comparator.compare(expected[i], actual.toArray()[i]) != 0) {
					mismatch = true;
					writer.print(" > ");
				}
				writer.print(i);
				writer.print(": ");
				writer.print(expected[i]);
				if (mismatch){
					writer.print(" but got: ");
					writer.print(actual.toArray()[i]);
				}
				writer.println();
			}
		}
		if (actual == null) {
			writer.println("Actual:" + " <null>");
		}
		else {
			writer.println("Actual:");
			int i = 0;
			for (Iterator iter = actual.iterator(); iter.hasNext();) {
				writer.print(i);
				writer.print(": ");
				writer.println(iter.next());
				i++;
			}
		}
		TestCase.fail(stringWriter.toString());
	}
}
