package abbot.finder.swt;

import java.util.Collection;

import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Widget;

/** Provides access to all widgets in a hierarchy. */
public interface Hierarchy {
    Collection getRoots();
    Collection getWidgets(Widget w);
    Widget getParent(Widget w);
    boolean contains(Widget w);
    void dispose(Decorations d);
}
