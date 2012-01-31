
package abbot.script.swt;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Widget;

import abbot.Log;
import abbot.finder.swt.BasicFinder;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.WidgetFinder;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.script.XMLConstants;
import abbot.swt.SimpleResolver;
import abbot.tester.swt.WidgetTester;

/**
 * This class contains a subset of the functionality of 
 * <code>abbot.script.ComponentReference</code>, omitting code that
 * is related to scripting/XML.  
 */
public class WidgetReference implements XMLConstants /*, XMLifiable */{

//	private static final String CR_USAGE = 
//		"<widget id=\"<id>\" class=\"...\" .../>";
//
//	private SimpleResolver resolver;
	private WidgetFinder finder;
	
	// unique id for this reference
	private String id = null;
	// widget text, if any
	private String text = null;
	// widget name, if any
	private String name = null;
	// widget class name
	private String refClassName = null;
	// parent's ref id
	private String parentID = null;
	private WidgetReference parentReference = null;
	// index among parent's children
	private int index = -1;
	// parent window ref id
	private String windowID = null;
	private WidgetReference windowReference = null;
	// parent window title 
	private String title = null;
	// tag specific to this widget
	private String tag = null;
	// for popup menus only, the invoking widget
	private String invokerID = null;
	private WidgetReference invokerReference = null;
	// for popup menus only, the location of invocation
	private Point point = new Point(-1,-1);

	/** For general widget lookup by class name.
	 * @param id Desired ID for the reference.  Only used if this reference is
	 * to be passed as the parent, window, or invoker of another.
	 * @param widgetClass Class of the widget (required)
	 */
	public WidgetReference(String id, Class widgetClass) {
		this(id, widgetClass, null, null, null, null, -1, null, null);
	}

	/** For general widget lookup.
	 * @param id Desired ID for the reference.  Only used if this reference is
	 * to be passed as the parent, window, or invoker of another.
	 * @param widgetClass Class of the widget (required)
	 * @param name Name of the widget, or null	
	 */	
	public WidgetReference(String id, Class widgetClass, String name){
		this(id, widgetClass, name, null, null, null, -1, null, null);
	}

	/** For general widget lookup.
	 * @param id Desired ID for the reference.  Only used if this reference is
	 * to be passed as the parent, window, or invoker of another.
	 * @param widgetClass Class of the widget (required)
	 * @param name Name of the widget, or null
	 * @param tag Tag as returned by WidgetTester.getTag(Widget)
	 */
	public WidgetReference(String id, Class widgetClass,
							  String name, String tag) {
		this(id, widgetClass, name, tag, null, null, -1, null, null);
	}

	/** For general widget lookup.
	 * @param id Desired ID for the reference.  Only used if this reference is
	 * to be passed as the parent, window, or invoker of another.
	 * @param widgetClass Class of the widget (required)
	 * @param name Name of the widget, or null
	 * @param tag Tag as returned by WidgetTester.getTag(Widget)
	 * @param title Owning Frame/Dialog title, or null
	 */
	public WidgetReference(String id, Class widgetClass,
							  String name, String tag, String title) {
		this(id, widgetClass, name, tag, title, null, -1, null, null);
	}

	/** For general widget lookup.
	 * @param id Desired ID for the reference.  Only used if this reference is
	 * to be passed as the parent, window, or invoker of another.
	 * @param widgetClass Class of the widget (required)
	 * @param name Name of the widget, or null
	 * @param tag Tag as returned by WidgetTester.getTag(Widget)
	 * @param title Owning Frame/Dialog title, or null
	 * @param text The text of the widget as returned by widget.getText(), or null
	 */	
	public WidgetReference(String id, Class widgetClass,
								String name, String tag, String title, String text){
		this(id, widgetClass, name, tag, title, null, -1, null, text);
	}

	/** For general widget lookup.
	 * @param id Desired ID for the reference.  Only used if this reference is
	 * to be passed as the parent, window, or invoker of another.
	 * @param widgetClass Class of the widget (required)
	 * @param name Name of the widget, or null
	 * @param tag Tag as returned by WidgetTester.getTag(Widget)
	 * @param title Owning Frame/Dialog title, or null
	 * @param parent Reference to parent, or null
	 * @param index Index within parent, or -1
	 */
	public WidgetReference(String id, Class widgetClass,
							  String name, String tag, String title,
							  WidgetReference parent, int index) {
		this(id, widgetClass, name, tag, title, parent, index, null, null);
	}

	/** For general widget lookup.
	 * @param id Desired ID for the reference.  Only used if this reference is
	 * to be passed as the parent, window, or invoker of another.
	 * @param widgetClass Class of the widget (required)
	 * @param name Name of the widget, or null
	 * @param tag Tag as returned by WidgetTester.getTag(Widget)
	 * @param title Owning Frame/Dialog title, or null
	 * @param parent Reference to parent, or null
	 * @param index Index within parent, or -1
	 * @param invokerOrWindow Window reference, invoker, or null
	 * @param text widget's text
	 */
	public WidgetReference(String id, Class widgetClass,
							  String name, String tag, String title,
							  WidgetReference parent, int index,
							  WidgetReference invokerOrWindow, String text) {
		// hack, kinda
		//resolver = parent != null ? parent.resolver : new Script();
		this.id = id;
		this.refClassName = widgetClass.getName();
		this.name = name;
		this.tag = tag;
		this.parentID = parent != null ? parent.getID() : null;
		this.index = index;
		this.text = text;
		//if (JPopupMenu.class.isAssignableFrom(widgetClass)) {
		//	invokerID = invokerOrWindow != null
		//		? invokerOrWindow.getID() : null;
		//}
		//else {
			windowID = invokerOrWindow != null
				? invokerOrWindow.getID() : null;
		//}
		this.title = title;
	}
	
	/**
	 * Creates a WidgetReference object for the given Widget
	 */
	public WidgetReference(SimpleResolver resolver, Widget widget){
//		this.resolver = resolver;
		this.finder = BasicFinder.getDefault(); //DefaultWidgetFinder.getFinder();
		WidgetTester wt = new WidgetTester();
		// TODO: fix this
		Class refClass = widget.getClass();
		Widget parent = ((BasicFinder)finder).getWidgetParent(widget);
//		Widget win = null;
		
		refClass = WidgetTester.getCanonicalClass(refClass);
		refClassName = refClass.getName();
		name = (String)wt.getData(widget, "name");
			//finder.getWidgetName(widget);
		tag = WidgetTester.getTag(widget);
		// FIXME need to add this functionality
//		if (null != parent) {
//			Widget[] children = finder.getWidgetChildren(parent,false);
//			for (int i = 0; i < children.length; ++i) {
//				if (children[i] == widget) {
//					index = i;
//					break;
//				}
//			}
//			//if (tag == null) {
//			WidgetReference ref = resolver.addWidget(parent);
//			parentID = ref.getID();
//			parentReference = ref;
//			//}
//		}
		
//		title = finder.getWidgetDecorationsTitle(widget);
		text = wt.getWidgetText(widget);
		
		// If no title is available, save the parent window id instead
//		if (title == null) {
//			win = finder.getWidgetDecorations(widget);
//			if (win != null && !(widget instanceof Decorations)) {
//				WidgetReference wref = resolver.addWidget(win);
//				this.windowID = wref != null ? wref.id : null;
//				this.windowReference = wref;
//			}
//		}	
		
		try {
			// FIXME if the component has just become not visible, this lookup
			// will fail!  Need a lookup flag for visible components?
			WidgetTester.findWidget(this);
		}
		catch (MultipleWidgetsFoundException e) {
			// More than one match found, so add more information
			if (parent != null && parentID == null) {
				WidgetReference ref = resolver.addWidget(parent);
				parentID = ref.getID();
				parentReference = ref;
				// Try the lookup again to make sure it works this time
				try { 
					WidgetTester.findWidget(this);
				}
				catch(Exception exc) {
					Log.warn(exc);
					exc.printStackTrace();
					throw new Error("Reverse lookup failed for " + toString()
									+ " trying to match "
									+ /*WidgetTester.toString(*/widget);
					
				}
			}
		}
		catch (WidgetNotFoundException cnf) {
			// This indicates a failure in the reference recording mechanism,
			// and requires a fix.
			throw new Error("Reverse lookup failed for " + toString() 
							+ " trying to match "
							+ /*ComponentTester.toString(*/widget);
		}

		// Finally, get a unique ID for this reference
		id = resolver.getUniqueID(this);
		Log.debug("Unique ID is " + id);		
	}
	
	/** Unique identifier for this widget. */
	public String getID() { return id; }

	/** This widget's name, null if no name was set. */
	public String getName() { return name; }

	/** This widget's class name. */
	public String getRefClassName() { return refClassName; }

	/** Return whether this reference has the same class or is a superclass of
	 * the given widget's class.  Simply compare class names to avoid class
	 * loader conflicts.  Note that this does not take into account interfaces
	 * (which is okay, since with GUI widgets we're only concerned with
	 * class inheritance). 
	 */
	public boolean isAssignableFrom(Class cls) {
		return cls != null
			&& (refClassName.equals(cls.getName())
				|| isAssignableFrom(cls.getSuperclass()));
	}

	/** Reference ID of this widget's parent (optional). */
	public String getParentID() { return parentID; }
	public WidgetReference getParentReference() {
		//if (parentID != null && parentReference == null) {
		//	parentReference = resolver.getWidgetReference(parentID);
		//}
		return parentReference;
	}

	/** Index among parent's children (optional). */
	public int getIndex() { return index; }

	/** Reference ID of this widget's parent window (optional). */
	public String getWindowID() { return windowID; }
	public WidgetReference getWindowReference() {
		//if (windowID != null && windowReference == null) {
		//	windowReference = resolver.getWidgetReference(windowID);
		//}
		return windowReference;
	}

	/** Title string of this widget's parent frame (optional). */
	public String getTitle() { return title; }

	/** Text of this widget */
	public String getText() { return text; } 
	
	/** Invoker of a JPopupMenu. */
	public String getInvokerID() { return invokerID; }
	public WidgetReference getInvokerReference() {
		//if (invokerID != null && invokerReference == null) {
		//	invokerReference = resolver.getWidgetReference(invokerID);
		//}
		return invokerReference;
	}

	public Point getInvocationLocation() { return point; }

	/** Returns a widget class-specific tag used to match this
		reference to a real widget. */
	public String getTag() { return tag; }

	public String toString() {
		String str = id != null ? id : (refClassName + " (no id yet)");
		if (str.indexOf("Instance") == -1)
			str += " (" + refClassName + ")";
		return str;
	}
}
