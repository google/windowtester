/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Log;
import abbot.finder.swt.SWTHierarchy;
import abbot.tester.swt.ControlTester;
import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.SWTUIException;

public class SWTHierarchyHelper {

	private final WeakHashMap _ownerToChildMap = new WeakHashMap();

	/**
	 * To properly connect menus with their owners we need to do an exhaustive search or widgets looking at their
	 * children.  To disable this, set this flag to false.
	 * TODO: a future approach might exploit caching...
	 */
	private static final boolean FIND_OWNER_ENABLED = true;
	
	static ControlTester _controlTester = new ControlTester();
	
	
	/** Cached display used for sync execs and for the creation of an SWTHierachy in the null
	 *  parent elaboration case.
	 */
	private Display _display;
	

	public SWTHierarchyHelper(Display display) {
		_display = display;
	}

	
	public SWTHierarchyHelper() {
		this(Display.getDefault());
	}

	/**
	 * Get this widget's index relative to its parent widget.
	 * <p>Note that indexes only matter in the case where there is at least one sibling
	 * that matches the target widget exactly (by class and name/label).  Other cases
	 * return -1. 
	 * @param w - the widget
	 * @param parent - the parent widget
	 * @return an index, or -1 if is the only child
	 * FIXME: return 0 in only-child case
	 */
	public int getIndex(Widget w, Widget parent) {
		
		List children = getChildren(parent, w.getClass());
		int count =  0;   //the match counter
		int index = -1;   //the index of our target widget
		//only child case...
		if (children.size() == 1)
			return index;
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Widget child = (Widget)iter.next();
			
			//using exact matches...
			if (child.getClass().isAssignableFrom(w.getClass()) && w.getClass().isAssignableFrom(child.getClass())) {
				//also check for nameOrLabelMatch
				if (nameAndOrLabelDataMatch(w, child))
					++count;	
			}
			if (child == w)
				index = count-1; //indexes are zero-indexed
		}
		return (count > 1) ? index : -1;
		//throw new IllegalStateException("unfound child");
	}

	

	

	
   	
   	
    private Collection getAllMenuItems(Menu m) {
    	/* get all menus and menu items rooted at this menu */
    	ArrayList list = new ArrayList();
    	if (m != null) {
    		MenuItem [] items = m.getItems();
    		list.add(m);
    		for (int i=0;i<items.length;i++) {
    			list.addAll(getAllMenuItems(items[i].getMenu()));
    			list.add(items[i]);
    		}
    	}
    	return list;
    }
   	

	
	
	/**
	 * Checks to see that widget names/labels match.
	 * @param w1 - the first widget
	 * @param w2 - the second widget
	 * @return true if they match
	 */
	private boolean nameAndOrLabelDataMatch(Widget w1, Widget w2) {
		String text1 = getWidgetText(w1);
		String text2 = getWidgetText(w2);
		if (text1 == null)
			return text2 == null;
		return text1.equals(text2);
	}
	
	/**
	 * Extract the text from the given widget.
	 * @param w - the widget in question
	 * @return the widget's text
	 */
	public String getWidgetText(final Widget w) {

		return (String) Robot.syncExec(w.getDisplay(),
				new RunnableWithResult() {
					public Object runWithResult() {
						if (w instanceof Control) {
							//controlShowing = ((Control)w).getVisible() && ((Control)w).getShell().getVisible();
							//System.out.println("Widget " + w + " showing: " + controlShowing);
						}
						if (w instanceof Button) {
							return (((Button) w).getText());
						}
						//!pq: Combo text data is too volatile for matching...
						if (w instanceof Combo) {
							//return (((Combo)w).getText());     
							return null;
						}
						if (w instanceof Decorations) {
							return (((Decorations) w).getText());
						}
						if (w instanceof Group) {
							return (((Group) w).getText());
						}
						if (w instanceof Item) {
							if (w instanceof TableItem
									&& ((TableItem) w).getParent()
											.getColumnCount() > 0) {
								//int columns = ((TableItem)w).getParent().getColumnCount();
								return (((TableItem) w).getText(0));
								//TODO: this isn't quite right... 

								//		    			String[] lWtexts = new String[columns];
								//		    			for (int i=0;i<lWtexts.length;i++) {
								//		    				lWtexts[i] = ((TableItem)w).getText(i);
								//		    			}
								//		    			setWtexts(lWtexts);
							} else {
								return (((Item) w).getText());
							}
						}
						if (w instanceof Label) {
							return (((Label) w).getText());
						}
						//		    	!pq: Text data is too volatile for matching...
						//		    	if (w instanceof Text) {
						//		    		return (((Text)w).getText());       	    		
						//		    	}    	 
						if (w instanceof Menu) {
							//TODO: still unclear what to do here...
							//		    		Menu m = (Menu)w;
							//		    		String text = m.getParent().getText();
							//		    		Menu parentMenu = m.getParentMenu();
							//		    		String s = m.toString();
						}
						//fall through ....
						return null;
					}
				});
	}
	
	/**
	 * Get the children (of a particular class) of a given parent widget.
	 * @param parent - the parent widget
	 * @param cls - the class of child widgets of interest
	 * @return a list of children
	 */
	public List getChildren(Widget parent, Class cls) {
		List children = getChildren(parent);
		//prune non-exact class matches
		List pruned = new ArrayList();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Object child = iter.next();
			Class childClass = child.getClass();
			if (cls.isAssignableFrom(childClass) && childClass.isAssignableFrom(cls))
				pruned.add(child);
		}
		return pruned;
	}
	
	
	
	public List getChildren(final Widget w) {
		//in case this is called for a null parent, we need to resort to a cached display
		Display display = w == null ? _display : w.getDisplay();
		return (List)UIProxy.syncExec(display, new RunnableWithResult(){
			public Object runWithResult() {
				return getChildren0(w);
			}
		});
	}
	
	
  	/**
   	 * Get the children of the given widget.
   	 * <p>
   	 * (Taken from SWTHieracrhy and modified.)
   	 * @param w - the widget in question.
   	 * @return the list of children
   	 * @see abbot.finder.swt.SWTHierarchy
   	 */
   	private List getChildren0(Widget w) {
		List localList = new ArrayList();

		//null parent case
		if (w == null) {
			addCheck(localList, new SWTHierarchy(_display).getRoots());
		}
		if (w instanceof Decorations) {
			Decorations d = (Decorations)w;
			localList.addAll(getAllMenuItems(d.getMenuBar()));
			addCheck(localList,d.getMenuBar());
		}
		if (w instanceof Control) {
			Control c = (Control)w;
			addCheck(localList,getAllMenuItems(c.getMenu()));
			//menu added in getAllMenuItems
			//addCheck(localList,c.getMenu());
		}    	
		if(w instanceof Scrollable){
			addCheck(localList,((Scrollable)w).getVerticalBar());
			addCheck(localList,((Scrollable)w).getHorizontalBar());					
		}
		if(w instanceof TreeItem){
			Widget[] widgets = ((TreeItem)w).getItems();
			addCheck(localList,(Arrays.asList(widgets)));
		}
		if(w instanceof Menu){
			Widget[] widgets = ((Menu)w).getItems();
			addCheck(localList,Arrays.asList(widgets));							
		}
		if(w instanceof MenuItem){
			Widget childMenu = ((MenuItem)w).getMenu();
			addCheck(localList,childMenu);
		}
	    if (w instanceof Composite) {
	        if (w instanceof ToolBar) {
	        	addCheck(localList,Arrays.asList(((ToolBar)w).getItems()));
	        }
	        if (w instanceof Table) {
	        	addCheck(localList,Arrays.asList(((Table)w).getItems()));
	        	addCheck(localList,Arrays.asList(((Table)w).getColumns()));
	        }
	        if (w instanceof Tree) {
	        	addCheck(localList,Arrays.asList(((Tree)w).getItems()));
	        }
	        if (w instanceof CoolBar) {
	        	addCheck(localList,Arrays.asList(((CoolBar)w).getItems()));
	        }
			if(w instanceof TabFolder){
				Widget[] widgets = ((TabFolder)w).getItems();
				addCheck(localList,Arrays.asList(widgets));
			}		
			if(w instanceof CTabFolder){
				Widget[] widgets = ((CTabFolder)w).getItems();
				addCheck(localList,Arrays.asList(widgets));
			}		
	    	Composite cont = (Composite)w;
	        Control [] children = cont.getChildren();
	//        for (int i=0;i<children.length;i++) {
	//        	if (children[i] instanceof Composite
	//        			&& ((Composite)children[i]).getChildren().length > 0)
	//        	{
	//        		addCheck(localList,getWidgets(children[i]));
	//        	} 
	//        }
	        addCheck(localList,Arrays.asList(children));
	    }
	    return localList;
	    //outerList = localList;
	}

   	
   	
   	
    /**
     * Retrieve the parent of the given widget.
     * @param widget - the widget in question
     * @return the widget's parent, or null if it has none
     */
    public Widget getParent(final Widget widget) {
//		if (widget == null)
//			throw new AssertionError("null widget");
		
    	//TODO: handle null widget argument case here!
    	
    	
    	/**
    	 * The issue is that while some shell's answer other shells as their parents, they do not show
    	 * up that way in the hierarchy.  To work around this, we short circuit the call to getParent() in the
    	 * case where the widget in question is a Shell.
    	 */
    	if (widget instanceof Shell)
    		return null;
    	
    	
		return (Widget)Robot.syncExec(widget.getDisplay(), new RunnableWithResult(){
			public Object runWithResult(){
				if(widget instanceof Control)
					return ((Control)widget).getParent();
				if(widget instanceof Caret)
					return ((Caret)widget).getParent();		
				if(widget instanceof Menu) {
					/**
					 * Some menus (context) have shells as there parents but a more specific parent exists.
					 * To find these, we need to look into the hierarchy to find whether this menu is a child of 
					 * another widget.  Since this is a potentially expensive operation, it can be disabled
					 * by setting a flag (see: findOwner).
					 */
					Menu menu = (Menu)widget;
					Widget parentOfMenu = findOwner(widget);
					if (parentOfMenu != null) {
						//System.out.println("parent != null:: " + parentOfMenu);
						return parentOfMenu;
					}
					return menu.getParent();
					
					//return (parentOfMenu != null) ? parentOfMenu : menu.getParent(); 
				}
				if(widget instanceof ScrollBar)
					return ((ScrollBar)widget).getParent();					
				if(widget instanceof CoolItem)
					return ((CoolItem)widget).getParent();		
				if(widget instanceof MenuItem)
					return ((MenuItem)widget).getParent();		
				if(widget instanceof TabItem)
					return ((TabItem)widget).getParent();		
				if(widget instanceof TableColumn)
					return ((TableColumn)widget).getParent();		
				if(widget instanceof TableItem)
					return ((TableItem)widget).getParent();		
				if(widget instanceof ToolItem)
					return ((ToolItem)widget).getParent();											
				if(widget instanceof TreeItem)
					return ((TreeItem)widget).getParent();							
				if(widget instanceof DragSource)
					return ((DragSource)widget).getControl().getParent();
				if(widget instanceof DropTarget)
					return ((DropTarget)widget).getControl().getParent();
				if(widget instanceof Tracker)
					Log.debug("requested the parent of a Tracker- UNFINDABLE");	
				//fall through
				return null;
			}
		});
    }

	/**
	 * Look at all of the widgets in the hierarchy seeking one who claims this menu as its own.
	 * @param menu - the menu in question
	 * @return the menu's owner or null if there is none
	 */
    private Widget findOwner(Widget menu) {
    	//short circuit if disabled
    	if (!FIND_OWNER_ENABLED)
    		return null;
    	//check cache first (notice null values might exist)
    	if (_ownerToChildMap.containsKey(menu))
    		return (Widget)_ownerToChildMap.get(menu);
    	
		SWTHierarchy hierarchy = new SWTHierarchy(menu.getDisplay());
		Widget[] widgets = hierarchy.getWidgets();
		Widget owner = null;
		for (int i = 0; i < widgets.length && owner == null; i++) {
			Widget w = widgets[i];
			//TODO: see if there are other types that support menus
			if (w instanceof Control) {
				Control c = (Control)w;
				Menu child = c.getMenu();
				//System.out.println(c + " -> child: " + UIProxy.getToString(child));
				if (child == menu) {
					owner = c;
				}
			}
		}
		_ownerToChildMap.put(menu, owner);
		return owner;
	}
   	
    
  	/////////////////////////////////////////////////////////////////////////////////
   	//
   	// Debugging Helpers
   	//
   	/////////////////////////////////////////////////////////////////////////////////

    /**
     * Get a String that represents the current widget hierarchy starting at the 
     * active shell as root.  ThTE_Eui.wis output is meant to be suitable for debugging 
     * purposes.
     * @param root the root from which to derive the hierarchy description
     * @return a String representation of the hierarchy
     * @return
     */
    public String dumpWidgets() {
    	return dumpWidgets(ShellFinder.getActiveShell(_display));
    }
    
    /**
     * Get a String that represents the current widget hierarchy starting at the 
     * given root.  This output is meant to be suitable for debugging purposes.
     * @param root the root from which to derive the hierarchy description
     * @return a String representation of the hierarchy
     */
    public String dumpWidgets(Widget root) {
    	StringBuffer sb = new StringBuffer();
    	accumulateWidgetInfo(new SWTHierarchy(_display),sb, root, 0);
    	return sb.toString();
    }

    
    private void accumulateWidgetInfo(SWTHierarchy hierarchy, StringBuffer buffer, Widget w, int level) {
    	
    	buffer.append((indent(level)));
    	boolean visible = SWTHierarchyHelper.isVisible(w);
    	
    	if (!visible)
    		buffer.append("[");
    	buffer.append(UIProxy.getToString(w) + "<HC|"+w.hashCode()+">");
    	if (!visible)
    		buffer.append("]->(invisible)");
    	buffer.append(StringUtils.NEW_LINE);
    	
    	Collection childWidgets = hierarchy.getWidgets(w);
        Iterator iter = childWidgets.iterator();        
    	while (iter.hasNext()) {
    		accumulateWidgetInfo(hierarchy, buffer, (Widget)iter.next(), level+1);
        }    	
    }
    
    private StringBuffer indent(int level) {
    	StringBuffer indentation = new StringBuffer("");
    	for (int i=0;i<level;i++) 
    		indentation.append(" ");
    	return indentation;
    }

   	/////////////////////////////////////////////////////////////////////////////////
   	//
   	// Internal
   	//
   	/////////////////////////////////////////////////////////////////////////////////


	/**
	 * Add the contents of this collection to this other collection only if 
	 * it is non-empty.
	 * @param dest - the destination collection
	 * @param src - the source collection
	 */
	private void addCheck(Collection dest, Collection src) {
		/* add object to collection if non-null */
		if (src.size() > 0) {
			// Iterator iter = src.iterator();
			// while (iter.hasNext()) {
			// dest.addAll(getWidgets((Widget)iter.next()));
			// }
			dest.addAll(src);
		}
	}
   	
	/**
	 * Add this object to this collection only if the object is non-null.
	 * @param c - the collection
	 * @param o - the object to add
	 */
	private void addCheck(Collection c, Object o) {
		/* add object to collection if non-null */
		if (o != null) {
			c.add(o);
			// c.addAll(getWidgets((Widget)o));
		}
	}

	//TODO: this should really take an interface (like IScopeLocator) and should be overriden for the specific cases
	public int getIndex(Widget w, IWidgetIdentifier scopeLocator) {
		//TODO: decide whether we want view/shell relative indexes
		return WidgetLocator.UNASSIGNED;
	}


	/**	
	 * Check whether this widget is visible.
	 */
	public static boolean isVisible(Widget w) {
		if (w == null)
			return false;
		if (w.isDisposed())
			return false;
		return guardedVisibilityCheck(w);
	}

	private static boolean guardedVisibilityCheck(Widget w) {
		
//		try {
//			// ask the menu watcher if the given items menu is open
//			if (w instanceof MenuItem)
//				// TODO: use widget references rather than these static methods
////				return MenuWatcher.getInstance(w.getDisplay()).isVisible((MenuItem) w);
////				return new MenuItemReference((MenuItem) w).isVisible();
//				return ((MenuItemReference) WTRuntimeManager.asReference(w)).isVisible();
//			Control control = FinderUtil.getControl(w);
//			if (control.isDisposed())
//				return false;
//			//for some reason Links return false when asked if "isVisible"...
//			if (control instanceof Link) {
//				return _controlTester.getVisible(control);
//			}
//			return _controlTester.isVisible(control);
//		} catch (SWTException e) {
//			// ignore
//		}
		
		IWidgetReference ref = WTRuntimeManager.asReference(w);
		if (ref instanceof ISWTWidgetReference<?>) {
			//there's a chance that a widget may get disposed during our check so we have 
			//to guard against exceptions
			try {
				return ((ISWTWidgetReference<?>) ((ISWTWidgetReference<?>) ref)).isVisible();
			}
			catch (SWTUIException e) {
				return false;
			}
		}

		return false;
	}

}
