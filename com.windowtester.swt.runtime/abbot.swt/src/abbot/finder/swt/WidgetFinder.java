package abbot.finder.swt;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Composite;

/** Interface to support looking up existing widgets based on a number of
    different criteria.
*/

public interface WidgetFinder {
    /** Find a Component, using the given Matcher to determine whether a given
        component in the hierarchy used by this ComponentFinder is the desired
        one.
    */
    Widget find(Matcher m)
        throws WidgetNotFoundException, MultipleWidgetsFoundException;

    /** Find a Widget, using the given Matcher to determine whether a given
        component in the hierarchy under the given root is the desired
        one.
    */
	Widget find(Composite root, Matcher m)
        throws WidgetNotFoundException, MultipleWidgetsFoundException;
}
