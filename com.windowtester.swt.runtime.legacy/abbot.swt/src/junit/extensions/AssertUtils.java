package junit.extensions;

import junit.framework.ComparisonFailure;

/**
 * Static convenience assertions.
 * @author Tom Roche
 * @version $Id: AssertUtils.java,v 1.1 2008-11-20 23:36:50 pq Exp $
 */
public class AssertUtils extends junit.framework.Assert {
	
	/**
	 * There must be a better way to do this ...
	 */
	protected static final String FQNAME = "junit.extensions.Assert";

	/**
	 * Protect ctor: static-only class
	 */
	protected AssertUtils() {
	}

	/**
	 * Asserts that two Strings are equal, optionally trimming whitespace.
	 * Note that, even with trim=true, null and a whitespace-only String
	 * are still unequal. 
	 * @param trim leading and trailing whitespace
	 */
	static public void assertEquals(String expected, String actual, boolean trim) {
		assertEquals(null, expected, actual, trim);
	}

	/**
	 * Asserts that two Strings are equal, optionally trimming whitespace.
	 * Note that, even with trim=true, null and a whitespace-only String
	 * are still unequal. 
	 * @param trim leading and trailing whitespace
	 */
	static public void assertEquals(
		String message, String expected, String actual, boolean trim) {
		if (!trim) assertEquals(message, expected, actual);
		if (expected == null && actual == null)
			return;
		if (((expected == null) && (actual != null)) ||
		    ((actual == null) && (expected != null)))
			throw new ComparisonFailure(message, expected, actual);
		// both not null
		if (trim) {
			expected = expected.trim();
			actual = actual.trim();
		}
		// and should still be not null
		String error = FQNAME + ".assertEquals(String, String, String, boolean): INTERNAL ERROR";
		assertNotNull(error, expected);
		assertNotNull(error, actual);
		assertEquals(message, expected, actual);
	}

}
