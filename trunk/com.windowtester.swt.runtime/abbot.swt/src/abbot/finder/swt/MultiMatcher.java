package abbot.finder.swt;

import org.eclipse.swt.widgets.Widget;

/** Provides methods for determining the best match among a group of matching
    widgets. 
*/ 
public interface MultiMatcher extends Matcher {
    /** Returns the best match among all the given candidates, or throws an
        exception if there is no best match.
    */
    Widget bestMatch(Widget[] candidates)
        throws MultipleWidgetsFoundException;
}
