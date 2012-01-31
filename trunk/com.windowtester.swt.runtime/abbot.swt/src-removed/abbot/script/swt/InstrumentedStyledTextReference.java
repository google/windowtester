package abbot.script.swt;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.SimpleResolver;
import abbot.script.swt.WidgetReference;

/**
 * A bit more convenient than <code>StyledTextReference</code>
 * for the common case where you have instrumented a <code>StyledText</code>
 * and made its name and ID the same.
 * 
 * @author nntp_ds@fastmail.fm
 * @version $Id: InstrumentedStyledTextReference.java,v 1.1 2005-12-19 20:28:32 pq Exp $
 */
public class InstrumentedStyledTextReference extends StyledTextReference {
	/**
	 * @param idIsName

	 */
	public InstrumentedStyledTextReference(String idIsName) {
		super(idIsName, idIsName);
	}

	/**
	 * @param idIsName

	 * @param tag
	 */
	public InstrumentedStyledTextReference(
			String idIsName,
			String tag) {
		super(idIsName, idIsName, tag);
	}

	/**
	 * @param idIsName

	 * @param tag
	 * @param title
	 */
	public InstrumentedStyledTextReference(
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
	public InstrumentedStyledTextReference(
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
	public InstrumentedStyledTextReference(
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
	public InstrumentedStyledTextReference(
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
	public InstrumentedStyledTextReference(SimpleResolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
