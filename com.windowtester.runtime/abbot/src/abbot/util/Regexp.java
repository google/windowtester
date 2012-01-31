package abbot.util;

import gnu.regexp.*;
import abbot.Log;

/** Simple wrapper around the more fully-featured RE class. */

public class Regexp {
    /** Return whether there is a match for the given regular expression
     * within the given string.
     */
    public static boolean stringContainsMatch(String regexp, String actual) {
        try {
            boolean multiline = false;
            if (regexp.startsWith("(?m)")) {
                multiline = true;
                regexp = regexp.substring(4);
            }
            RE e = new RE(regexp, multiline
                          ? RE.REG_MULTILINE|RE.REG_DOT_NEWLINE : 0);
            REMatch m = e.getMatch(actual);
            return m != null;
        }
        catch(REException exc) {
            Log.warn(exc);
            return false;
        }
    }

    /** Return whether the given regular expression matches the given string
     * exactly. 
     */
    public static boolean stringMatch(String regexp, String actual) {
        if (actual == null)
            actual = "";
        try {
            boolean multiline = false;
            if (regexp.startsWith("(?m)")) {
                multiline = true;
                regexp = regexp.substring(4);
            }
            RE e = new RE(regexp, multiline
                          ? RE.REG_MULTILINE|RE.REG_DOT_NEWLINE : 0);
            return e.isMatch(actual);
        }
        catch(REException exc) {
            Log.warn(exc);
            return false;
        }
    }
}
