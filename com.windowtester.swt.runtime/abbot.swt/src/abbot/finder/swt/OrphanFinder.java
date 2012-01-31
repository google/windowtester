package abbot.finder.swt;

import java.util.Collection;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * A service that looks ahead to see if a given item is
 * accounted for in the SWT hierarchy.  If it is not, it 
 * is flagged an orphan.
 */
public class OrphanFinder {

	private SWTHierarchy _hierarchy;

	public OrphanFinder(Display display) {
		_hierarchy = new SWTHierarchy(display);
	}

	/**
	 * Is this menu orphaned by this shell?
	 * @param menu - the menu in question
	 * @param shell - the shell in question
	 * @return true if the menu is an orphan
	 */
	public boolean isOrphanedBy(Menu menu, Shell shell) {
		Control[] children = shell.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			if (isContainedBy(menu, child))
				return false;
		}
		return !isSubMenu(menu);
	}

	/**
	 * Is this menu contained by this control?
	 * @param menu
	 * @param control
	 * @return
	 */
	private boolean isContainedBy(Menu menu, Control control) {
		Collection widgets = _hierarchy.getWidgets(control);
		return widgets == null ? false : widgets.contains(menu);
	}

	/**
	 * Is this menu a submenu?
	 * @param menu
	 * @return
	 */
	public boolean isSubMenu(Menu menu) {
		return menu.getParentMenu() != null;
	}

}
