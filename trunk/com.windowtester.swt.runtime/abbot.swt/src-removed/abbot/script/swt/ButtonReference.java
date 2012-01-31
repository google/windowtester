package abbot.script.swt;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.SimpleResolver;
import abbot.script.swt.WidgetReference;

/**
 * @author tlroche
 * @version $Id :$
 * A bit more convenient than <code>WidgetReference</code>.
 */
public class ButtonReference extends WidgetReference {
	/**
	 * @param id
	 */
	public ButtonReference(String id) {
		super(id, Button.class);
	}

	/**
	 * @param id
	 * @param name
	 */
	public ButtonReference(String id, String name) {
		super(id, Button.class, name);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 */
	public ButtonReference(
		String id,
		String name,
		String tag) {
		super(id, Button.class, name, tag);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 */
	public ButtonReference(
		String id,
		String name,
		String tag,
		String title) {
		super(id, Button.class, name, tag, title);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param text
	 */
	public ButtonReference(
		String id,
		String name,
		String tag,
		String title,
		String text) {
		super(id, Button.class, name, tag, title, text);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 */
	public ButtonReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index) {
		super(id, Button.class, name, tag, title, parent, index);
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
	public ButtonReference(
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
			Button.class,
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
	public ButtonReference(SimpleResolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
