package abbot.swt.eclipse.tests;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TestsStrings {
	private static final String BUNDLE_NAME = "abbot.swt.eclipse.tests.tests"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = 
		ResourceBundle.getBundle(BUNDLE_NAME);

	private TestsStrings() {
	}

	public static String getString(String key) {
		// TODO_Tom Auto-generated method stub
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getString(String key, String insert) {
		return getFormattedString(key, new Object[] {insert});
	}

	/**
	 * Returns the formatted resource string associated with the given key in the resource bundle. 
	 * <code>MessageFormat</code> is used to format the message. If there isn't  any value 
	 * under the given key, the key is returned.
	 *
	 * @param key the resource key
	 * @param arg the message argument
	 * @return the string
	 */	
	public static String getFormattedString(String key, Object arg) {
		return getFormattedString(key, new Object[] { arg });
	}

}
