package abbot.util;

import java.lang.reflect.Array;

import com.windowtester.runtime.util.StringComparator;

/** Utility class to perform comparisons. */
public class ExtendedComparator {

	//are we using our regexp support?
	private static final boolean REGEXP_MATCHING = true;
	
	private ExtendedComparator() { }

    /** Perform element-by-element comparisons of arrays in addition to
        regular comparisons.  */
    public static boolean equals(Object obj1, Object obj2) {
        boolean result = false;
        if (obj1 == null && obj2 == null) {
            result = true;
        }
        else if (obj1 == null && obj2 != null
                 || obj2 == null && obj1 != null) {
            result = false;
        }
        else if (obj1.equals(obj2)) {
            result = true;
        }
        // If both are strings, check for a regexp match
        else if (obj1 instanceof String && obj2 instanceof String) {
            result = StringComparator.matches(((String)obj2), ((String)obj1));
        }
        else if (obj1.getClass().isArray() && obj2.getClass().isArray()) {
            if (Array.getLength(obj1) == Array.getLength(obj2)) {
                result = true;
                for (int i=0;i < Array.getLength(obj1);i++) {
                    if (!equals(Array.get(obj1, i), Array.get(obj2, i))) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }
}
