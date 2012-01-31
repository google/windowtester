package abbot.script.swt;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.SimpleResolver;

/**
 * @author tlroche
 * @version $Id :$
 * A bit more convenient than <code>WidgetReference</code>.
 */
public class LabelReference extends WidgetReference {
	/**
	 * @param id
	 */
	public LabelReference(String id) {
		super(id, Label.class);
	}

	/**
	 * @param id
	 * @param name
	 */
	public LabelReference(String id, String name) {
		super(id, Label.class, name);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 */
	public LabelReference(
		String id,
		String name,
		String tag) {
		super(id, Label.class, name, tag);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 */
	public LabelReference(
		String id,
		String name,
		String tag,
		String title) {
		super(id, Label.class, name, tag, title);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param text
	 */
	public LabelReference(
		String id,
		String name,
		String tag,
		String title,
		String text) {
		super(id, Label.class, name, tag, title, text);
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
	public LabelReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index) {
		super(id, Label.class, name, tag, title, parent, index);
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
	public LabelReference(
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
			Label.class,
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
	public LabelReference(SimpleResolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
