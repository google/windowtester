package abbot.swt.i18n;

import java.text.MessageFormat;
import java.util.*;

import abbot.Log;

/**
 * Provides i18n support a là Abbot.
 * @version $Id: Strings.java,v 1.1 2005-12-19 20:28:32 pq Exp $ 
 */
// TODO: use & in strings to indicate mnemonics
// TODO: auto-format tooltips (".tt.") and dialog messages (".dlg.")
public class Strings {
    private static final String BUNDLE = "abbot.i18n.StringsBundle";
    private static Set bundles = new HashSet();
    private static Map formats = new HashMap();

    static {
        addBundle(BUNDLE);
    }

    protected Strings() { }

    public static void addBundle(String bundle) {
        Locale locale = Locale.getDefault();
        try {
            bundles.add(ResourceBundle.getBundle(bundle, locale));
        }
        catch(MissingResourceException mre) {
            throw new Error("No resource bundle found in " + bundle);
        }
    }

    /** Returns the localized string for the given key, or the key surrounded
        by '#' if no corresponding localized string is found.
    */
    public static String get(String key) {
        return get(key, false);
    }

    /** Returns the localized string for the given key.  If optional is true,
        return null, otherwise returns the key surrounded by '#' if no
        corresponding localized string is found. 
    */
    public static String get(String key, boolean optional) {
        String defaultValue = "#" + key + "#";
        String value = null;
        Iterator iter = bundles.iterator();
        while (iter.hasNext()) {
            ResourceBundle local = (ResourceBundle)iter.next();
            try {
                value = local.getString(key);
            }
            catch(MissingResourceException mre) {
            }
        }
        if (value == null) {
            if (!optional) {
                Log.log("Missing resource '" + key + "'");
                value = defaultValue;
            }
        }
        return value;
    }

    /** Returns a formatted localized string for the given key and arguments,
        or the key if no corresponding localized string is found.  Use
        java.text.MessageFormat syntax for the format string and arguments.
    */
    public static String get(String key, Object[] args) {
        MessageFormat fmt = (MessageFormat)formats.get(key);
        if (fmt == null) {
            fmt = new MessageFormat(get(key));
            formats.put(key, fmt);
        }
        return fmt.format(args);
    }

    /** Returns a formatted localized string for the given key and insert,
    or the key if no corresponding localized string is found.  Use
    java.text.MessageFormat syntax for the format string and arguments.
	*/
	public static String get(String key, String insert) {
	    MessageFormat fmt = (MessageFormat)formats.get(key);
	    if (fmt == null) {
	        fmt = new MessageFormat(get(key));
	        formats.put(key, fmt);
	    }
	    return fmt.format(insert);
	}

}
