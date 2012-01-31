package abbot.swt.eclipse.utils;

import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;

import abbot.tester.swt.WidgetTester;

/**
 * Stuff I like. TODO: find standard equivalents
 * @author tlroche
 * @version $Id: Utils.java,v 1.1 2005-12-19 20:28:33 pq Exp $
 */
public class Utils {
	public static final char SLASH_CHAR = '/'; //$NON-NLS-1$
	public static final String SLASH_STRING = "/"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$
	public static final char DOT_CHAR = '.'; //$NON-NLS-1$

	/**
	 * Is the argument empty or <code>null</code>?
	 * @param sa some array (<code>Object[]</code>)
	 * @return boolean
	 */
	public static boolean isEmpty(Object[] sa) {
		if ((sa == null) || (sa.length < 1)) {
			return true;
		} else {
			return false;
		}
	}

	// TODO: replace with generic
	public static boolean isEmpty(IProject[] pa) {
		return isEmpty((Object[])pa);
	}

	/**
	 * Is the <code>ISelection</code>empty or <code>null</code>?
	 * @param sa some array (<code>Object[]</code>)
	 * @return boolean
	 */
	public static boolean isEmpty(ISelection is) {
		if ((is == null) || (is.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tests if 
	 * @param s <code>String</code>
	 * is empty or <code>null</code>
	 * @return boolean
	 */
	public static boolean isEmpty(String s) {
		if ((s == null) || (s.length() < 1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param buffer
	 * @return
	 */
	public static boolean isEmpty(StringBuffer sb) {
		if (sb == null) return true;
		return isEmpty(sb.toString());
	}

	/**
	 * Tests if the <code>Map</code> (which is not a <code>Collection</code>) 
	 * is empty or <code>null</code>
	 * @return boolean
	 */
	public static boolean isEmpty(Map m) {
		if ((m == null) || m.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tests if 
	 * @param l <code>Collection</code>
	 * is empty or <code>null</code>
	 * @return boolean
	 */
	public static boolean isEmpty(Collection l) {
		if ((l == null) || (l.size() < 1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Just sticks all the strings together with '/'
	 * CONTRACT: user must ensure that param is not empty,
	 * and each member of param is not empty.
	 */
	public static String slashAppend(String[] strings) {
		StringBuffer sb = new StringBuffer(strings[0]);
		int len = strings.length;
		if (len > 1) {
			for (int i = 1; i < len; i++) {
				sb.append(SLASH_STRING).append(strings[i]);
			}
		}
		return sb.toString(); // fully qualified name
	}

	/**
	 * Just sticks all the strings together with '/'
	 * CONTRACT: user must ensure that each param is not empty.
	 */
	public static String slashAppend(String s0, String s1) {
		return new StringBuffer(s0).append(SLASH_STRING).append(s1).toString();
	}

	/**
	 * Just sticks all the strings together with '.'
	 * CONTRACT: user must ensure that param is not empty,
	 * and each member of param is not empty.
	 */
	public static String dotAppend(String[] strings) {
		StringBuffer sb = new StringBuffer(strings[0]);
		int len = strings.length;
		if (len > 1) {
			for (int i = 1; i < len; i++) {
				sb.append(DOT).append(strings[i]);
			}
		}
		return sb.toString(); // fully qualified name
	}

	/**
	 * Just sticks all the strings together with '.'
	 * CONTRACT: user must ensure that each param is not empty.
	 */
	public static String dotAppend(String s0, String s1) {
		return new StringBuffer(s0).append(DOT).append(s1).toString();
	}

	public static void assertNotEmpty(String s) {
		Assert.assertNotNull(s);
		Assert.assertFalse(s.length() < 1);
	}
	
	public static void safeJoin (Thread t)  {
		/* You can't wait for UI threads to finish using join because this keeps the 
		 * display loop from running.  Instead we use actionDelay and poll the status 
		 * of the running thread, returning when it has exited.
		 * */
		WidgetTester wt = WidgetTester.getWidgetTester();
		while (t.isAlive()) {
			wt.actionDelay(100);
		}
	}

}
