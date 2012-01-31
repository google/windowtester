package abbot.script.swt;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.SimpleResolver;
import abbot.script.swt.WidgetReference;

/**
 * A bit more convenient than <code>WidgetReference</code>.
 * 
 * @author nntp_ds@fastmail.fm
 * @version $Id: StyledTextReference.java,v 1.1 2005-12-19 20:28:32 pq Exp $
 */
public class StyledTextReference extends WidgetReference {
	/**
	 * @param id
	 */
	public StyledTextReference(String id) {
		super(id, StyledText.class);
	}

	/**
	 * @param id
	 * @param name
	 */
	public StyledTextReference(String id, String name) {
		super(id, StyledText.class, name);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag) {
		super(id, StyledText.class, name, tag);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag,
		String title) {
		super(id, StyledText.class, name, tag, title);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param text
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag,
		String title,
		String text) {
		super(id, StyledText.class, name, tag, title, text);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index) {
		super(id, StyledText.class, name, tag, title, parent, index);
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
	public StyledTextReference(
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
			StyledText.class,
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
	public StyledTextReference(SimpleResolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
