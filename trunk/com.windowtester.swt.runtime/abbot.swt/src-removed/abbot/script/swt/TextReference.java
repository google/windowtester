package abbot.script.swt;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.SimpleResolver;
import abbot.script.swt.WidgetReference;

/**
 * @author tlroche
 * @version $Id :$
 * A bit more convenient than <code>WidgetReference</code>.
 */
public class TextReference extends WidgetReference {
	/**
	 * @param id
	 */
	public TextReference(String id) {
		super(id, Text.class);
	}

	/**
	 * @param id
	 * @param name
	 */
	public TextReference(String id, String name) {
		super(id, Text.class, name);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 */
	public TextReference(
		String id,
		String name,
		String tag) {
		super(id, Text.class, name, tag);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 */
	public TextReference(
		String id,
		String name,
		String tag,
		String title) {
		super(id, Text.class, name, tag, title);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param text
	 */
	public TextReference(
		String id,
		String name,
		String tag,
		String title,
		String text) {
		super(id, Text.class, name, tag, title, text);
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
	public TextReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index) {
		super(id, Text.class, name, tag, title, parent, index);
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
	public TextReference(
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
			Text.class,
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
	public TextReference(SimpleResolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
