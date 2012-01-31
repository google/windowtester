package abbot.finder.matchers.swt;

import com.windowtester.runtime.util.StringComparator;

import abbot.finder.swt.Matcher;

/** Convenience abstract class to provide regexp-based matching of strings. */
public abstract class AbstractMatcher implements Matcher {
    /** Provides direct or regexp matching.  To match a regular expression,
        bound the expected string with slashes, e.g. /regular expression/.  
    */
    protected boolean stringsMatch(String expected, String actual) {
    	if (expected == null || actual == null) {
    		return false;
    	}
    	else {
        return StringComparator.matches(actual, expected);
    	}
    }
    public String toString() {
        return getClass().getName();
    }
}
