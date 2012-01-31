package abbot.finder.swt;

import org.eclipse.swt.widgets.Widget;

/** Provides an indication whether a Widget matches some desired
    criteria.
*/ 
public interface Matcher {
    /** Return whether the given Widget matches some lookup criteria. */
    boolean matches(Widget w);
}
