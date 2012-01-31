package abbot.swt;

import java.util.Iterator;

import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.script.swt.WidgetReference;

/** Interface to support looking up existing Widgets based on a number of
	different criteria.
	
	@deprecated Use the new matcher API in abbot.finder.swt and 
	abbot.finder.matchers.swt.
	@see abbot.finder.swt.WidgetFinder
*/
public interface WidgetFinder {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	static final int MATCH_EXACT = 0;
	static final int MATCH_STRONG = 1;
	static final int MATCH_WEAK = 2;

	/** Attempt to find the given Widget.  */
	Widget findWidget(WidgetReference ref)
		throws WidgetNotFoundException, MultipleWidgetsFoundException;

	/**
	 * Attempt to find the given Widget in the given Shell.
	 */
	Widget findWidget(WidgetReference ref, Shell s)
		throws WidgetNotFoundException, MultipleWidgetsFoundException;

	/**
	 * Return the first shell matching the passed title.
	 */
	public Shell findShellByTitle(String title);

	/** Find the first showing Decorations with the given name. */
	Decorations findDecorationsByName(String name);

	/** Find the first showing Decorations with the given title pattern. */
	Decorations findDecorationsByTitle(String pattern);

	/** Find the first showing Decorations with a name or title that matches the
	 * given string. */
	Decorations findDecorations(String nameOrTitle);

	/** Find the active popup menu under the given Widget root. */
	Widget findActivePopupMenu(Widget root);

	/** Look up the first menu item below root with the given title. */
	Widget findMenuItemByText(Widget root, String name);
    
	/** Find the best match for the given Widget among the given
	 * WidgetReferences.
	 */
	WidgetReference matchWidget(Widget widget, Iterator refs, int type);

	/** Determine the best we can whether the Widget is the one referred to
	 * by the reference.
	 */
	boolean widgetsMatch(Widget widget, WidgetReference ref, int type);

	/** Returns all available root Shells (i.e. those that have no parent). */
	Shell[] getRootShells();

	/** Returns all immediate child Decorationss of the given parent Decorations. */
	Decorations[] getDecorations(Decorations parent);

	/** Returns the set of all available Decorationss. */
	Decorations[] getDecorations();

	/** Return all children of the given container, including Decorationss, 
	 * MenuElements, and popup menus.
	 */ 
	Widget[] getWidgets(Widget widget);
	
	/**
	 * Get all children of the given widget, including non-controls.
	 */
	Widget[] getWidgetChildren(Widget widget, boolean recurse);

	/** Look up the apparent parent of a Widget.  */
	Widget getWidgetParent(Widget widget);

	/** Returns the parent Decorations for the given Widget.  In this context,
	 * the parent of a popup menu is its invoker. */
	Decorations getWidgetDecorations(Widget widget);

	/** Returns the title of the owning Decorations for the given Widget. */
	String getWidgetDecorationsTitle(Widget widget);

	/** Return the Widget's owning SHELL.   There will
	 * <b>always</b> one of these; even a frameless Decorations will have a
	 * temporary frame generated for it.
	 */
	Shell getWidgetShell(Widget widget);

	/** Return the Widget's name. */
	String getWidgetName(Widget widget);

	/** Return the Widget's text */
	String getWidgetText(Widget widget);

	/** Stringify the Widget */
	String widgetToString(Widget widget);

	/** Returns true if the Widget or its Window ancestor is filtered. */
	boolean isFiltered(Widget widget);

	/** Don't return the given Widget in any queries.  Makes the given
		Widget unavailable unless filtering is turned off. */
	void filterWidget(Widget widget);

	/** Indicate whether to filter Widget lists. */
	void setFilterEnabled(boolean enable);

	/** Discard the given Widget from any future queries. */
	void discardWidget(Widget widget);

	/** Omit all currently available Widgets from any future queries. */
	void discardAllWidgets();

	/** Discard all currently available windows. */
	void disposeWindows();

	/** Send close events to all available showing windows. */
	void closeWindows();

	/** Return whether the given window has been opened. */
	boolean isDecorationsShowing(Decorations w);

	/**
	 * @param title
	 * @param parent
	 * @return
	 */
	Shell findShellByTitleInHierarchy(String title, Shell parent);
}
