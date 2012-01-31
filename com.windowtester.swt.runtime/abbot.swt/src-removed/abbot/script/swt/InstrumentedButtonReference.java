package abbot.script.swt;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.SimpleResolver;
import abbot.script.swt.WidgetReference;

/**
 * @author tlroche
 * @version $Id :$
 * A bit more convenient than <code>ButtonReference</code>
 * for the common case where you have instrumented a <code>Button</code>
 * and made its name and ID the same.
 */
public class InstrumentedButtonReference extends ButtonReference {
	/**
	 * @param idIsName
	 */
	public InstrumentedButtonReference(String idIsName) {
		super(idIsName, idIsName);
	}

	/**
	 * @param idIsName
	 * @param tag
	 */
	public InstrumentedButtonReference(
		String idIsName,
		String tag) {
		super(idIsName, idIsName, tag);
	}

	/**
	 * @param idIsName
	 * @param tag
	 * @param title
	 */
	public InstrumentedButtonReference(
		String idIsName,
		String tag,
		String title) {
		super(idIsName, idIsName, tag, title);
	}

	/**
	 * @param idIsName
	 * @param tag
	 * @param title
	 * @param text
	 */
	public InstrumentedButtonReference(
		String idIsName,
		String tag,
		String title,
		String text) {
		super(idIsName, idIsName, tag, title, text);
	}

	/**
	 * @param idIsName
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 */
	public InstrumentedButtonReference(
		String idIsName,
		String tag,
		String title,
		WidgetReference parent,
		int index) {
		super(idIsName, idIsName, tag, title, parent, index);
	}

	/**
	 * @param idIsName
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 * @param invokerOrWindow
	 * @param text
	 */
	public InstrumentedButtonReference(
		String idIsName,
		String tag,
		String title,
		WidgetReference parent,
		int index,
		WidgetReference invokerOrWindow,
		String text) {
		super(
			idIsName,
			idIsName,
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
	public InstrumentedButtonReference(SimpleResolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
