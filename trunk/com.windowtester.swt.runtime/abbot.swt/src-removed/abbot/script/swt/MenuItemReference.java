package abbot.script.swt;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.SimpleResolver;

/**
 * @author tlroche
 * @version $Id :$
 * A bit more convenient than <code>WidgetReference</code>.
 */
public class MenuItemReference extends WidgetReference {
	/**
	 * @param id
	 */
	public MenuItemReference(String id) {
		super(id, MenuItem.class);
	}

	/**
	 * @param id
	 * @param name
	 */
	public MenuItemReference(String id, String name) {
		super(id, MenuItem.class, name);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 */
	public MenuItemReference(
		String id,
		String name,
		String tag) {
		super(id, MenuItem.class, name, tag);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 */
	public MenuItemReference(
		String id,
		String name,
		String tag,
		String title) {
		super(id, MenuItem.class, name, tag, title);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param text
	 */
	public MenuItemReference(
		String id,
		String name,
		String tag,
		String title,
		String text) {
		super(id, MenuItem.class, name, tag, title, text);
		// TODO_Tom Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 */
	public MenuItemReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index) {
		super(id, MenuItem.class, name, tag, title, parent, index);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 * @param invokerOrWindow
	 * @param text
	 */
	public MenuItemReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index,
		WidgetReference invokerOrWindow,
		String text) {
		super(
			id,
			MenuItem.class,
			name,
			tag,
			title,
			parent,
			index,
			invokerOrWindow,
			text);
	}

	/**
	 * @param resolver
	 * @param widget
	 */
	public MenuItemReference(SimpleResolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
