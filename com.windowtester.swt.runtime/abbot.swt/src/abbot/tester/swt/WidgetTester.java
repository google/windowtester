package abbot.tester.swt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import abbot.BugReport;
import abbot.Log;
import abbot.WaitTimedOutError;
import abbot.finder.swt.SWTHierarchy;
import abbot.finder.swt.TestHierarchy;
import abbot.script.Condition;

import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;
import com.windowtester.runtime.swt.internal.operation.SWTKeyOperation;
import com.windowtester.runtime.swt.internal.operation.SWTPushEventOperation;

/**
 * This is the base class for tester objects.  WidgetTester primarily contains
 * three types of methods:
 * <ol>
 * <li>action* methods, for executing a particular action on a widget</li>
 * <li>get* methods, for obtaining info about the current state of a widget from any thread</li>
 * <li>assert* methods, for making assertions about a widget's state from any thread</li>
 * </ol>
 * 
 * @author Kevin Dale
 * @version $Id: WidgetTester.java,v 1.5 2007-11-13 23:57:14 pq Exp $
 */
/* TODO:
 * 
 * 1) Where should swt extensions go (as is, in abbot.tester.extensions)?
 * 	[for now, we'll leave it in abbot.tester.extensions]
 * 2) Are we to put waitForIdle() calls here, or not?
 * 	[for now, we'll include them only where they were included before]
 * 3) What params should keyPress,keyRelease,etc take?
 * 	[for now, we'll NOT use strings as with ComponentTester] 
 */
public class WidgetTester extends Robot {	
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    //!pq: flag to indicate whether to emit verbose trace info to the console
	private static final boolean TRACE = false;
	
	private TestHierarchy hierarchy = null; /* for use in deriveTag across recursive calls */
	
	/**
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */
    
	/**
	 * Proxy for {@link Widget#getData()}.
	 * <p/>
	 * @param w the widget to obtain the data from.
	 * @return the data stored with the widget.
	 */
	public Object getData(final Widget w){
		Object result = Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return w.getData();
			}
		});
		return result; 
	}	
    
	/**
     * Proxy for {@link Widget#getData(java.lang.String)}.
     * <p/>
     * @param w the widget to obtain the data from.
     * @param key the key under which the data is stored.
     * @return the data associated with the key given.
     */
    public Object getData(final Widget w, final String key){
    	Object result = Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
    		public Object runWithResult() {
    			return w.getData(key);
    		}
    	});
    	return result;
    }
    
    /**
     * Proxy for {@link Widget#getDisplay()}.
     * <p/>
     * @param w the widget to obtain the display from.
     * @return the display associated with the widget given.
     */
    public Display getDisplay(final Widget w){
        Display result = (Display)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.getDisplay();
            }
        });
        return result;
    }
    
    /**
	 * Proxy for {@link Widget#getStyle()}.
	 * <p/>
	 * @param w the widget to obtain the style for.
	 * @return the style.
	 */
	public int getStyle(final Widget w){
		Integer result = (Integer) Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(w.getStyle());
			}
		});
		return result.intValue();
	}
       
    /**
     * Proxy for {@link Widget#isDisposed()}.
     * <p/>
     * @param w the widget to ask.
     * @return true if the widget has been disposed.
     */
    public boolean isDisposed(final Widget w){
        Boolean result = (Boolean) Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return Boolean.valueOf(w.isDisposed());
            }
        });
        return result.booleanValue();
    }
    
	/**
	 * Proxy for {@link Widget#isListening(int eventType)}.
	 * <p/>
	 * @param w the widget to ask.
	 * @return true if the widget is listening for the given event.
	 */
	public boolean isListening(final Widget w, final int eventType){
		Boolean result = (Boolean) Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(SWTWorkarounds.isListening(w, eventType));
			}
		});
		return result.booleanValue();
	}
    
    /**
     * Proxy for {@link Widget#toString()}.
     * <p/>
     * @param w the widget to obtain the toString from.
     * @return 
     */
    public static String toString(final Widget w){
        //@todo: this is static b/c super class Robot overrides toString to be static
        String result = (String)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.toString();
            }
        });
        return result;
    }
	/* End getters */	
    
	/* Begin setters/adders */
	/**
	 * Proxy for {@link Widget#addDisposeListener(org.eclipse.swt.events.DisposeListener)}.
	 * <p/>
	 * @param w the widget to add the listener to.
	 * @param disposeListener the listener to add.
	 */
	public void addDisposeListener(final Widget w, final DisposeListener disposeListener) {
		Robot.syncExec(w.getDisplay(), null, new Runnable() {
			public void run() {
				w.addDisposeListener(disposeListener);
			}
		});
	}
	/**
	 * Proxy for {@link Widget#addListener(int, org.eclipse.swt.widgets.Listener)}.
	 * <p/>
	 * @param w the Widget to add the listener to.
	 * @param eventType the eventType for which a listener to add. 
	 * @param listener the listener to add.
	 */
	public void addListener(final Widget w, final int eventType, final Listener listener) {
		Robot.syncExec(w.getDisplay(), null, new Runnable() {
			public void run() {
				w.addListener(eventType, listener);
			}
		});
	}
	/**
	 * Proxy for {@link Widget#notifyListeners(int, org.eclipse.swt.widgets.Event)}.
	 * <p/>
	 * @param w the widgets which listeners should be notified.
	 * @param eventType the eventType to notify.
	 * @param event the event to issue.
	 */
	public void notifyListeners(final Widget w, final int eventType, final Event event) {
		Robot.syncExec(w.getDisplay(), null, new Runnable() {
			public void run() {
				w.notifyListeners(eventType, event);
			}
		});
	}
	/**
	 * Proxy for {@link Widget#removeDisposeListener(org.eclipse.swt.events.DisposeListener)}.
	 * <p/>
	 * @param w the Widget from which to remove the DisposeListener.
	 * @param disposeListener the listener to remove.
	 */
	public void removeDisposeListener(final Widget w, final DisposeListener disposeListener) {
		Robot.syncExec(w.getDisplay(), null, new Runnable() {
			public void run() {
				w.removeDisposeListener(disposeListener);
			}
		});
	}
	/**
	 * Proxy for {@link Widget#removeListener(int, org.eclipse.swt.widgets.Listener)}.
	 * <p/>
	 * @param w the Widget from which the listener to remove.
	 * @param eventType the eventType being removed.
	 * @param listener the listener to remove.
	 */
	public void removeListener(final Widget w, final int eventType, final Listener listener ) {
		Robot.syncExec(w.getDisplay(), null, new Runnable() {
			public void run() {
				w.removeListener(eventType, listener);
			}
		});
	}
	/**
	 * Proxy for {@link Widget#setData(java.lang.Object)}.
	 * <p/>
	 * @param w the Widget whichs data should be set.
	 * @param data the data to set.
	 */
	public void setData(final Widget w, final Object data) {
		Robot.syncExec(w.getDisplay(), null, new Runnable() {
			public void run() {
				w.setData(data);
			}
		});
	}
	/**
	 * Proxy for {@link Widget#setData(java.lang.String, java.lang.Object)}.
	 * <p/>
	 * @param w the widget whichs data to set.
	 * @param key the key under shich the data should be stored.
	 * @param data the data to store.
	 */
	public void setData(final Widget w, final String key, final Object data) {
		Robot.syncExec(w.getDisplay(), null, new Runnable() {
			public void run() {
				w.setData(key, data);
			}
		});
	}
	
	/* End setters/adders */

	/*
	 * TODO: Copied from old DefaultWidgetFinder.  Is this the right place for this?
	 */
	public String getWidgetText(final Widget widget){
		String res = null;
		Method getText=null;
		WidgetTester tester=null;
		Class[] paramTypes = {widget.getClass()};
		Object[] params = {widget};
		boolean foundMethod = false;
		Class widgetClass = widget.getClass();
		while(!foundMethod && (tester==null||tester.getClass()!=WidgetTester.class)){
			try{ 
				//getText = widget.getClass().getMethod("getText",null);	
				tester = WidgetTester.getTester(widgetClass);
				getText =tester.getClass().getMethod("getText",paramTypes);
				foundMethod = true;
			}
			catch(NoSuchMethodException nsme){
				widgetClass = widgetClass.getSuperclass();
				paramTypes[0]=widgetClass;
			}	
		}
		if(getText==null)
			return null;
		try{
			res = (String)getText.invoke(tester,params);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return res;
	}

	/** 
	 * Get the location of the widget in global screen coordinates 
	 **/
	public Point getGlobalLocation(Widget w){
		return getGlobalLocation(w,true);
	}
	
	/** 
	 * Get the location of the widget in global screen coordinates,
	 * optionally ignoring the 'trimmings'.
	 */
	public Point getGlobalLocation(final Widget w, final boolean ignoreBorder){
        Point result = (Point) Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return WidgetLocator.getLocation(w,ignoreBorder);
            }
        });
        return result;
	}
	
	/** 
	 * Get the bounding rectangle for the given Widget in global
	 * screen coordinates.
	 */
	public Rectangle getGlobalBounds(Widget w){
		return getGlobalBounds(w,true);
	}
	
	/** 
	 * Get the bounding rectangle for the given Widget in global
	 * screen coordinates, optionally ignoring the 'trimmings'.
	 */
	public Rectangle getGlobalBounds(final Widget w, final boolean ignoreBorder){
	    Rectangle result = (Rectangle) Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
	        public Object runWithResult() {
	            return WidgetLocator.getBounds(w,ignoreBorder);
	        }
	    });
	    return result;
	}
	
	/** Maps class names to their corresponding Tester object. */
   private static HashMap testers = new HashMap();

   /** Establish the given WidgetTester as the one to use for the given
	* class.  This may be used to override the default tester for a given
	* core class.  Note that this will only work with widgets loaded by		////
	* the framework class loader, not those loaded by the class loader for	////
	* the code under test.													////
	*/
   public static void setTester(Class forClass, WidgetTester tester) {
	   testers.put(forClass.getName(), tester);
   }

   /** Return the appropriate Tester for the given object. */
   public static WidgetTester getTester(Widget widget) {
	   return widget != null ? getTester(widget.getClass())
		   : getTester(Widget.class);
   }

 	/**
 	 * Factory method.
 	 */
 	public static WidgetTester getWidgetTester() {
 		return (WidgetTester)(getTester(Widget.class));
 	}

   /** Find the corresponding Tester object for the given widget class,
	   chaining up the inheritance tree if no specific tester is found for
	   that class.<p>
	   The abbot tester package is searched first, followed by the tester
	   extensions package.
   */
   public static WidgetTester getTester(Class widgetClass) {
	   Log.debug("Looking up tester for " + widgetClass);
	   if (!Widget.class.isAssignableFrom(widgetClass)) {
		   String msg = "Class " + widgetClass.getName()
			   + " is not derived from org.eclipse.swt.widgets.Widget";
		   throw new IllegalArgumentException(msg);
	   }
	   WidgetTester tester = (WidgetTester)
		   testers.get(widgetClass.getName());
	   if (tester == null) {
		   String cname = simpleClassName(widgetClass) + "Tester";
		   String pkg = WidgetTester.class.getPackage().getName();
		   tester = findTester(pkg + "." + cname, widgetClass);
		   if (tester == null) {
			   tester = findTester(pkg + ".extensions." + cname,
			widgetClass);
			   if (tester == null) {
				   tester = getTester(widgetClass.getSuperclass());
			   }
		   }
		   if (tester != null && !tester.isExtension()) {
			   // Only cache it if it's part of the standard framework,
			   // but cache it for every level that we looked up, so we
			   // don't repeat the effort.
			   testers.put(widgetClass.getName(), tester);
		   }
	   }

	   return tester;
   }

   //\/(extensions to swt stuff may go elsewhere)
   /** Return whether this tester is an extension. */
   public final boolean isExtension() {
	   return getClass().getName().startsWith("abbot.tester.extensions");
   }   
     
   /** Look up the given class, using special class loading rules to maintain
	  framework consistency. */
 	private static Class resolveClass(String testerName, Class widgetClass)
	  	throws ClassNotFoundException {
	  	// Extension testers must be loaded in the context of the code under
	  	// test.
	  	Class cls;
	  	if (testerName.startsWith("abbot.tester.extensions")) {
		  	cls = Class.forName(testerName, true,
								widgetClass.getClassLoader());
	  	}
	  	else {
		  	cls = Class.forName(testerName);
	  	}
	  	Log.debug("Loaded class " + testerName + " with "
				  + cls.getClassLoader());
	  	return cls;
  	}
	///\
	
	/** Look up the given class with a specific class loader. */
	private static WidgetTester findTester(String testerName,
											  Class widgetClass) {
		WidgetTester tester = null;
		Class testerClass = null;
		try {
			testerClass = resolveClass(testerName, widgetClass);
			tester = (WidgetTester)testerClass.newInstance();
		}
		catch(InstantiationException ie) {
			Log.warn(ie);
		}
		catch(IllegalAccessException iae) {
			Log.warn(iae);
		}
		catch(ClassNotFoundException cnf) {
			//Log.debug("Class " + testerName + " not found");
		}
		catch(ClassCastException cce) {
			throw new BugReport("Class loader conflict: environment "
								+ WidgetTester.class.getClassLoader() 
								+ " vs. " + testerClass.getClassLoader());
		}

		return tester;
	}
	
//	/**
//	 * @deprecated 
//	 */
//	protected abbot.swt.WidgetFinder getFinder() {
//		return abbot.swt.DefaultWidgetFinder.getFinder();
//	}

	/** Derive a tag from the given accessible context if possible, or return
	* null.
	*/
   protected String deriveAccessibleTag(AccessibleContext context) {
	   String tag = null;
	   if (context != null) {
		   if (context.getAccessibleName() != null) {
			   tag = context.getAccessibleName();
		   }
		   if ((tag == null || "".equals(tag))
			   && context.getAccessibleIcon() != null
			   && context.getAccessibleIcon().length > 0) {
			   AccessibleIcon[] icons = context.getAccessibleIcon();    
			   tag = icons[0].getAccessibleIconDescription();
			   if (tag != null) {
				   tag = tag.substring(tag.lastIndexOf("/") + 1);
				   tag = tag.substring(tag.lastIndexOf("\\") + 1);
			   }
		   }
	   }
	   return tag;
   }

  	private static final String[] tagMethods = {
	  	"getText",
	  	"getData" 
  	};

	private static final Class[][] tagParamTypes = {
		null,
		{String.class}
	};
	
	private static final Object[][] tagArgs = {
		null,
		{"name"}  	
	};
	
  	/** Return a reasonable identifier for the given widget. */
  	public static String getTag(Widget widget) {
		return getTester(widget.getClass()).deriveTag(widget);
  	}

	/** Provide a String that is fairly distinct for the given widget.  For
	 * a generic widget, attempt to look up some common patterns such as a
	 * title or label.  Derived classes should absolutely override this method
	 * if such a String exists.<p>
	 * Don't use widget names as tags.<p>
	 */
	public String deriveTag(Widget widget) {
		Method m = null;
		String tag = null;
		// Try a few default methods
		for (int i=0;i < tagMethods.length;i++) {
			// Don't use getText on text components
			if ((widget instanceof Text)
				&& "getText".equals(tagMethods[i])) {
				continue;
			}
			try {
				m = widget.getClass().getMethod(tagMethods[i], tagParamTypes[i]);
				String tmp = (String)m.invoke(widget, tagArgs[i]);
				// Don't ever use empty strings for tags
				if (tmp != null && !"".equals(tmp)) {
					tag = tmp;
					break;
				}
			}
			catch(Exception e) {
				// System.err.println("tagMethods["+i+"] = "+m);
				// System.err.println(tagMethods[i]);
				// System.err.println(tagParamTypes[i]);
				// System.err.println(tagArgs[i]);
			}
		}
		// In the absence of any other tag, try to derive one from something
		// recognizable on one of its ancestors.
		if (tag == null || "".equals(tag)) {
			/* To fix this it will be necessary to go back and find the 
			 * place in hierarchy where getparent was implemented and 
			 * perhaps finish the implementation.
			 * */
//			Widget parent = DefaultWidgetFinder.getFinder().getWidgetParent(widget);
			/* need to keep hierarchy across recursive calls */
			//TestHierarchy hierarchy = new TestHierarchy(widget.getDisplay());
			if (hierarchy==null) {
				hierarchy = new TestHierarchy(widget.getDisplay());
			}
			Widget parent = hierarchy.getParent(widget);
			if (parent != null) {
				String ptag = getTag(parent);
				if (ptag != null && !"".equals(tag)) {
					// Don't use the tag if it's simply the window title; that
					// doesn't provide any extra information.
					if (!ptag.endsWith(" Root Pane")) {
						StringBuffer buf = new StringBuffer(ptag);
						int under = ptag.indexOf(" under ");
						if (under != -1)
							buf = buf.delete(0, under + 7);
						buf.insert(0, " under ");
						buf.insert(0, simpleClassName(widget.getClass()));
						tag = buf.toString();
					}
				}
			}
		}

		return tag;
	}
	

	/**
	 * Wait for an idle AWT event queue.  Will return when there are no more
	 * events on the event queue.  
	 */
	public void actionWaitForIdle() {
		waitForIdle();
	}

	/**
	 * Wait for an idle AWT event queue.  Will return when there are no more
	 * events on the event queue for the given display. 
	 */
	public void actionWaitForIdle(Display d){
		waitForIdle(d);	
	}

	/** Delay the given number of ms. */
	public void actionDelay(int ms) {
		delay(ms);
	}

	public void actionSelectMenuItemByText(Menu menu, String text) {
		selectMenuItemByText(menu, text);
	}
	
	// FIXME do actionSelectMenuItemByName in addition to by component
	// FIXME do actionSelectMenuItemByPath (for dynamics) which follows labels
	/** Select the given menu item. */
	public void actionSelectMenuItem(MenuItem item) {
		Log.debug("Attempting to select menu item " + toString(item));
		selectMenuItem(item);
	}

	/** Open the item's popup menu at the given coordinates of its parent control, and
	 *  select the given item.
	 */
	public void actionSelectPopupMenuItem(MenuItem item, int x, int y){
		selectPopupMenuItem(item,x,y);
	}

	/** Click on the center of the widget. */
	public void actionClick(Widget widget) {
		click(widget);
//		waitForIdle(widget.getDisplay());
		// What if the widget is a cancel button?
		// It will be promptly disposed.
		if ((widget != null) && (!widget.isDisposed())) { 
			waitForIdle(widget.getDisplay());
		}
	}

	/** Click on the widget at the given location. */
	public void actionClick(Widget widget, int x, int y) {
		click(widget, x, y, SWT.BUTTON1);
//		waitForIdle(widget.getDisplay());
		// What if the widget is a cancel button?
		// It will be promptly disposed.
		if ((widget != null) && (!widget.isDisposed())) { 
			waitForIdle(widget.getDisplay());
		}
	}

	/** Click on the widget at the given location.  The buttons string
	 * should be the org.eclipse.swt.SWT field name for the mask.
	 */
	public void actionClick(Widget widget, int x, int y, String buttons) {
		click(widget,x,y,getModifiers(buttons));
//		waitForIdle(widget.getDisplay());
		// What if the widget is a cancel button?
		// It will be promptly disposed.
		if ((widget != null) && (!widget.isDisposed())) { 
			waitForIdle(widget.getDisplay());
		}
	}

	/** 
	 * Click on the widget at the given location.  The buttons string
	 * should be the org.eclipse.swt.SWT field name for the mask.  This 
	 * variation provides for multiple clicks. 
	 */
	public void actionClick(Widget comp, int x, int y,
							String buttons, int count) {
		click(comp, x, y, getModifiers(buttons), count);
		waitForIdle(comp.getDisplay());
	}
	
	/*TODO implement Robot.getKeyCode(String) and change these keyMethods to take
	 * Strings as parameters
	 */
	
	/**
	 * Press the keys contained in the given accelerator
	 */
	public void actionKeyPress(int accelerator,Display d){
		keyPress(accelerator);
		
		waitForIdle(d);	
	}

	/**
	 * Release the keys contained in the given accelerator
	 */
	public void actionKeyRelease(int accelerator,Display d) {
		keyRelease(accelerator);
		waitForIdle(d);
	}

	/**
	 * Press, and release, the keys contained in the given accelerator
	 */
	public void actionKey(int accelerator,Display d){
		key(accelerator);
		waitForIdle(d);
	}

	/**
	 * Type the given character.  Note that this sends the key to whatever
	 * component currently has the focus.
	 */	
	public void actionKeyChar(char c,Display d){
		key((int)c);
		waitForIdle(d);	
	}
	
	/** 
	 * Type the given string. 
	 */
	public void actionKeyString(String string,Display d){
		keyString(string);
		waitForIdle(d);
	}
	

	/** Set the focus on to the given component. */
	/* TODO MAY NEED TO CHECK THAT THE CONTROL DOES INDEED HAVE FOCUS */
	public void actionFocus(Widget widget) {
		TestHierarchy hierarchy = new TestHierarchy(Display.getDefault());
		while(!(widget instanceof Control))
			widget = hierarchy.getParent(widget);
		focus((Control)widget);
		waitForIdle(widget.getDisplay());
	}

	/** Perform a drag action.  Derived classes should provide more specific
	 * identifiers for what is being dragged, e.g. actionDragTableCell or
	 * actionDragListElement.
	 */	
	public void actionDrag(Widget source, int x, int y){
		drag(source,x,y,SWT.BUTTON1);
		waitForIdle(source.getDisplay());
	}

	/** Perform a drag action.  Derived classes should provide more specific
	 * identifiers for what is being dragged, e.g. actionDragTableCell or
	 * actionDragListElement.   The modifiers represents the set of active
	 * modifiers when the drop is made.
	 */
	public void actionDrag(Widget source, int x, int y, int modifiers){
		drag(source,x,y,modifiers);
		waitForIdle(source.getDisplay());
	}

	/** Perform a basic drop action (implicitly causing a preceding mouse
	 * drag).
	 */ 	
	public void actionDrop(Widget target, int x, int y){
		drop(target,x,y,SWT.BUTTON1);
		waitForIdle(target.getDisplay());
	}
	
	/** Perform a basic drop action (implicitly causing a preceding mouse
	 * drag).  The modifiers represents the set of active modifiers when the
	 * drop is made.
	 */ 
	public void actionDrop(Widget target, int x, int y, int modifiers){
		drop(target,x,y,modifiers);
		waitForIdle(target.getDisplay());
	}
	
	/** Return whether the widget's contents matches the given image. */
	public boolean assertImage(Widget widget, Image image,
									  boolean ignoreBorder) {
		Image widgetImage = capture(widget, ignoreBorder);
		SWTImageComparator ic = new SWTImageComparator();
		boolean result = (ic.compare(image, widgetImage) == 0);
		widgetImage.dispose();
		return result;
	}

	
	/** Wait for the given condition, throwing an ActionFailedException if it
	 * times out.
	 */
	protected void waitAction(String desc, Condition cond)
		throws ActionFailedException {
		try { wait(cond); }
		catch(WaitTimedOutError wto) {
			throw new ActionFailedException(desc);
		}
	}	
	
	/** Returns whether Decorations corresponding to the given String is
	 * showing.  The string may be a plain String or regular expression and
	 * may match either the Decoration's title or name
	 */ 
	public synchronized boolean assertDecorationsShowing(String title) {
		return assertDecorationsShowing(title, true);
	}
	// FIXME provide more options for identifying the window (name, title,
	// class, nth window)	
	/* Support added to allow a search of either all decorations or just those that 
	 * are top-level decorations (root windows) */
	public synchronized boolean assertDecorationsShowing(final String title, boolean topOnly) {
		boolT = false;
		//System.out.println ("ADS: " + title + " / " + topOnly);
		if (topOnly) {
			/* only search top-level decorations */
			Display[] displays = DecorationsTracker.getDisplays();
			//ArrayList decorationsList = new ArrayList();
			for (int i=0;i<displays.length;i++) {
				Collection decorationsList = DecorationsTracker.getTracker(displays[i]).getRootDecorations();
				final Iterator iter = decorationsList.iterator();
				Robot.syncExec(displays[i],this,new Runnable(){
					public void run(){
						while (iter.hasNext()) {
							Decorations d = (Decorations)iter.next();
							if (!d.isDisposed() && d.getText().equals(title)) {
								boolT = d.isVisible();
							}
						}
					}
				});

			}					
		} else {
			// TODO: clean up and document this code
			// This code basically traverses the widget hierarchy looking for Decorations
			// We also have to make sure we run the methods that get text and other things 
			// from the correct thread
			/* search all decorations */
			Display[] displays = DecorationsTracker.getDisplays();
			ArrayList decorationsList = new ArrayList();
			for (int i=0;i<displays.length;i++) {
				//System.out.println("Display " + i + ": " + displays[i]);
				SWTHierarchy hierarchy = new TestHierarchy(displays[i]);
				final Iterator rootIter = hierarchy.getRoots().iterator();
				while (rootIter.hasNext()) {
					decorationsList.addAll(hierarchy.getWidgets((Widget)rootIter.next()));
				}
				decorationsList.addAll(hierarchy.getRoots());
				//hierarchy.dbPrintWidgets();
				final Iterator decorationsIter = decorationsList.iterator();
				ArrayList shellList = new ArrayList();
				while (decorationsIter.hasNext()){
					Object d = decorationsIter.next();
					//System.out.println ("Widget ("+d.hashCode()+") " + i + ": " + d + " belongs to: " + ((Widget)d).getDisplay());
					/* remove non-Decorations and those that don't belong to this display */
					if (
							( (d instanceof Decorations) 
							)
							
							&& ((Widget)d).getDisplay().equals(displays[i])) {
						shellList.add(d);
					}
				}
				final Iterator iter = shellList.iterator();

				Runnable runThread = new Runnable() {
					public void run(){
						while (iter.hasNext()) {
							Decorations d = (Decorations)iter.next();
							if (!d.isDisposed() && d.getText().equals(title)) {
								boolT = d.isVisible();
							}
						}
					}
				};		
				if (displays[i].getThread().equals(Thread.currentThread())) {
					/* run in this thread */
					while (iter.hasNext()) {
						Decorations d = (Decorations)iter.next();
						if (!d.isDisposed() && d.getText().equals(title)) {
							boolT = d.isVisible();
						}
					}

				} else {
					Robot.syncExec(displays[i],this,runThread);
					
				}
										
				}
			
		}
		//System.out.println ("ADS returning: " + boolT);
		return boolT;
	}

	/** Convenience wait for a window to be displayed.  The given string may
	  * be a plain String or regular expression and may match either the window
	  * title or its Widget name.  This method is provided as a convenience
	  * for hand-coded tests, since scripts will use a wait step instead.  The
	  * property abbot.robot.component_delay affects the default timeout
	  * (default is 30s).  
	  */
	 public static void waitForFrameShowing(final String title) {
		 wait(new Condition() {
			 public boolean test() {
				 return getTester(Widget.class).assertDecorationsShowing(title);
			 }
			 public String toString() { return title + " to show"; }
		 }, componentDelay);
	 }	

	/** Convenience wait for a shell to be displayed.  This method is like 
	 * waitForFrameShowing, with the exception that this method searches all 
	 * shells in all displays; the former only searches for top-level shells
	 */
	public static void waitForShellShowing(final String title) {
		waitForShellShowing(title, componentDelay);
	}	
	 	 
	/**
	 * Convenience wait for a shell to be displayed.  This method is like 
	 * waitForFrameShowing, with the exception that this method searches all 
	 * shells in all displays; the former only searches for top-level shells
	 * 
	 * @param delay in millis
	 */
	public static void waitForShellShowing(final String title, final int delay) {
		wait(new Condition() {
			public boolean test() {
				return getTester(Widget.class).assertDecorationsShowing(title, false);
			}
			public String toString() { return title + " to show"; }
		}, delay);
	}	
	 	 
//	/** Return whether the Widget represented by the given
//		WidgetReference is available.
//	*/
//	public boolean assertWidgetShowing(WidgetReference ref) {
//		try {
//			findWidget(ref);
//			return true;
//		}
//		catch(Exception e) {
//			return false;
//		}
//
//	}

//	/** 
//	 * Return the Widget represented by the given
//	 	WidgetReference.
//	 * @throws MultipleWidgetsFoundException
//	 * @throws WidgetNotFoundException
//	*/	
//	public static Widget findWidget(WidgetReference ref) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		/* should this be nonstatic? */
//		/* try to find the widget using finder / matcher */
//		Matcher [] widgetToFind = {
//				ref.getText() != null && !ref.getText().equals("") ? new TextMatcher (ref.getText()) : null,
//				ref.getName() != null && !ref.getName().equals("") ? new NameMatcher (ref.getName()) : null,
//				/* new ClassMatcher (ref.getClass() ) */ null
//				/* TODO: match on other stuff such as tag, etc. 
//				 * No tagmatcher currently exists -- necessary? 
//				 * */
//		};
//		WidgetFinder finder = BasicFinder.getDefault();
//		Widget result = finder.find(new CompositeMatcher(widgetToFind));
//		//getFinder().findWidget(ref);
//		return result;		
//	}
	
//	/**
//	 * Wait for the Widget represented by the given WidgetReference to
//	 * become available.  The timeout is affected by
//	 * abbot.robot.component_delay, which defaults to 30s.
//	 * @deprecated Use the new matcher API in abbot.finder.swt.
//	*/
//	public static void waitForWidgetShowing(final WidgetReference ref) {
//		wait(new Condition() {
//			public boolean test() {
//				return getTester(Widget.class).assertWidgetShowing(ref);
//			}
//			public String toString() { return ref + " to show"; }
//		}, componentDelay);
//	}

	private Method[] cachedMethods = null;

	/** Look up methods with the given prefix. */
	private Method[] getMethods(String prefix, Class returnType, 
								boolean onWidget) {
		if (cachedMethods == null) {
			cachedMethods = getClass().getMethods();
		}
		ArrayList methods = new ArrayList();
		HashSet names = new HashSet();
		Method[] mlist = cachedMethods;
		for (int i=0;i < mlist.length;i++) {
			String name = mlist[i].getName();
			if (!names.contains(name)) {
				Class[] params = mlist[i].getParameterTypes();
				if (name.startsWith(prefix)
					&& (returnType == null 
						|| returnType.equals(mlist[i].getReturnType()))
					&& ((params.length == 0 && !onWidget)
						|| (params.length > 0 
							&& (Widget.class.isAssignableFrom(params[0]) 
								== onWidget)))) {
					methods.add(mlist[i]);
					names.add(name);
				}
			}
		}
		return (Method[])methods.toArray(new Method[methods.size()]);
	}

	private Method[] cachedActions = null;
	/** Return a list of all actions defined by this class that don't depend
	 * on a widget argument. 
	 */
	public Method[] getActions() {
		if (cachedActions == null) {
			cachedActions = getMethods("action", void.class, false);
		}
		return cachedActions;
	}
	

	private Method[] cachedComponentActions = null;
	/** Return a list of all actions defined by this class that require
	 * a widget argument. 
	 */
	public Method[] getWidgetActions() {
		if (cachedComponentActions == null) {
			cachedComponentActions = getMethods("action", void.class, true);
		}
		return cachedComponentActions;
	}

    
	private Method[] cachedPropertyMethods = null;
	/** Return an array of all property check methods defined by this class.
	 * The first argument <b>must</b> be a Widget.
	 */
	public Method[] getPropertyMethods() {
		if (cachedPropertyMethods == null) {
			ArrayList all = new ArrayList();
			all.addAll(Arrays.asList(getMethods("is", boolean.class, true)));
			all.addAll(Arrays.asList(getMethods("get", null, true))); 
			// Remove getXXX or isXXX methods which aren't property checks
			Class[] args = new Class[] { Widget.class };
			try {
				all.remove(getClass().getMethod("getTag", args));
				all.remove(getClass().getMethod("getTester", args));
				all.remove(getClass().getMethod("isOnPopup", args));
			}
			catch(NoSuchMethodException e) {
			}
			cachedPropertyMethods =
				(Method[])all.toArray(new Method[all.size()]);
		}
		return cachedPropertyMethods;
	}

	private Method[] cachedAssertMethods = null;
	/** Return a list of all assertions defined by this class that don't
	 * depend on a widget argument.
	 */
	public Method[] getAssertMethods() {
		if (cachedAssertMethods == null) {
			cachedAssertMethods = getMethods("assert", boolean.class, false);
		}
		return cachedAssertMethods;
	}

	private Method[] cachedComponentAssertMethods = null;
	/** Return a list of all assertions defined by this class that require a
	 * widget argument.
	 */
	public Method[] getComponentAssertMethods() {
		if (cachedComponentAssertMethods == null) {
			cachedComponentAssertMethods =
				getMethods("assert", boolean.class, true);
		}
		return cachedComponentAssertMethods;
	}

	/** Quick and dirty strip raw text from html, for getting the basic text
		from html-formatted labels and buttons.  Behavior is undefined for
		badly formatted html.
	*/
	public static String stripHTML(String str) {
		if (str != null
			&& (str.startsWith("<html>")
				|| str.startsWith("<HTML>"))) {
			while (str.startsWith("<")) {
				int right = str.indexOf(">");
				if (right == -1)
					break;
				str = str.substring(right + 1);
			}
			while (str.endsWith(">")) {
				int right = str.lastIndexOf("<");
				if (right == -1)
					break;
				str = str.substring(0, right);
			}
		}
		return str;
	}
	
	/** Return the Widget class that corresponds to this WidgetTester
		class.  
	*/
	public Class getTestedClass(Class cls) {
		while (getTester(cls.getSuperclass()) == this) {
			cls = cls.getSuperclass();
		}
		return cls;
	}
//	/**
//	 * Clicks on the control given asynchronously and wait for the shell with the 
//	 * title given to open.
//	 * <p/>
//	 * The method is intended to be used with blocking dialogs.
//	 * @param widget the widget to click on.
//	 * @param shellTitle the title of the shell to wait for or <code>null</code> if none.
//	 * @return the shell which has been opened by this action, if findable by the title given.
//	 */
//	/*
// 	 * TODO The click and keyboard actions should be expanded to an asynchronous call, too.
//	 */
//	public Shell actionClickAsync(final Widget widget, final String shellTitle) {
//		Runnable clickAsync = new Runnable() {
//			public void run() {
//				WidgetTester.this.actionClick(widget);
//			}
//		};
//		widget.getDisplay().asyncExec(clickAsync);
//		if (shellTitle != null) {
//			WidgetTester.waitForShellShowing(shellTitle);
//			Shell openedShell = null; 
//			try {
//				openedShell = (Shell) BasicFinder.getDefault().find(new TextMatcher(shellTitle));
//			} 
//			catch (Exception ex) {
//				ex.printStackTrace();
//			} 
//			return openedShell;
//		}
//		return null;
//	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Alternative event posting actions 
	//
	////////////////////////////////////////////////////////////////////////////

	protected void keyClick(final int keyCode) {
		new SWTKeyOperation().keyCode(keyCode).execute();
//		keyUp(keyCode);
//		keyDown(keyCode);
	}
//
//	protected void keyUp(final int keyCode) {
//		trace("post key down " + keyCode);
//		Event event = new Event();
//		event.type = SWT.KeyDown;
//		event.keyCode = keyCode;
//		new SWTPushEventOperation(event).execute();
//	}
//	
//	protected void keyDown(final int keyCode) {
//		trace("post key up " + keyCode);
//		Event event = new Event();
//		event.type = SWT.KeyUp;
//		event.keyCode = keyCode;
//		new SWTPushEventOperation(event).execute();
//	}
	
	//!pq: an alternative way to post mouse press events, sidestepping the robot
	protected void mousePress2(int accelerator) {
		accelerator &= BUTTON_MASK;

		Event event = new Event();
		event.type = SWT.MouseDown;
		
		if((accelerator&SWT.BUTTON1)==SWT.BUTTON1)
			event.button = 1;
		if((accelerator&SWT.BUTTON2)==SWT.BUTTON2)
			event.button = 2;
		if((accelerator&SWT.BUTTON3)==SWT.BUTTON3)
			event.button = 3;
		new SWTPushEventOperation(event).execute();
	}
	
	//!pq: an alternative way to post mouse press events, sidestepping the robot
	protected void mouseRelease2(int accelerator) {
		accelerator &= BUTTON_MASK;

		Event event = new Event();
		event.type = SWT.MouseUp;
		
		if((accelerator&SWT.BUTTON1)==SWT.BUTTON1)
			event.button = 1;
		if((accelerator&SWT.BUTTON2)==SWT.BUTTON2)
			event.button = 2;
		if((accelerator&SWT.BUTTON3)==SWT.BUTTON3)
			event.button = 3;
		new SWTPushEventOperation(event).execute();
	}
	
	//!pq: an alternative way to move the mouse, sidestepping the robot
	protected void mouseMove2(int x, int y) {
		Event event = new Event();
		event.type = SWT.MouseMove;
		event.x = x;
		event.y = y;
		new SWTPushEventOperation(event).execute();
	}
	
    protected void trace(String msg) {
    	if (TRACE)
    		System.out.println(msg);
    }
}
