package abbot.finder.matchers.swt;

import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

/** This matcher does not have anything to do with 
 *  org.eclipse.swt.widgets.Composite; rather, it allows searches for widgets 
 *  using several matchers.  The specified matchers are ANDed together so that 
 *  a given widget matches in the CompositeMatcher if and only if the widget 
 *  matches in all of the component matchers.  Nulls in the array of matchers are ignored
 * */
public class CompositeMatcher extends AbstractMatcher {
    private Matcher [] matchers;
    public CompositeMatcher(Matcher [] matchers) {
        this.matchers = matchers;
    }
    public boolean matches(final Widget w) {
    	boolean result = true; /* ANDing things together, so start true */
    	boolean atLeastOneMatcherPresent = false; /* If that construction this should be checked! */
    	for (int i=0;i<matchers.length;i++) {
    		if (matchers[i] != null) {
    			result = result && matchers[i].matches(w);
    			atLeastOneMatcherPresent = true;
    		}
    	}
    	result = result && atLeastOneMatcherPresent;
      return result;
    }
    public String toString() {
		StringBuffer buffer = new StringBuffer();
        buffer.append("Composite matcher with " + matchers.length + " component matchers:\n");
		for (int i = 0; i < matchers.length; i++) {
			buffer.append("["+i+"] "+ matchers[i].toString() + "\n");
		}
		return buffer.toString();
    }
}
