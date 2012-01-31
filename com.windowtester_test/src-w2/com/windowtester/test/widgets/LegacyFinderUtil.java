package com.windowtester.test.widgets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

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
public class LegacyFinderUtil {

	

	private class WidgetGetter extends VoidCallable {
		//to ensure no duplicates...
    	private LinkedHashSet<Widget> list = new LinkedHashSet<Widget>();
    	private Widget rootWidget;
    	public Collection<Widget> getList() { return list; }
    	WidgetGetter (Widget w) {
    		this.rootWidget = w;
    	}
    	
    	public void call() {
    		try {
    			if (rootWidget != null && !rootWidget.isDisposed())
    				list.addAll(getWidgets(rootWidget));
    		} catch(Exception e) {
 //   			LogHandler.log(e);
    			e.printStackTrace();
    		}
    	}
    		
 
		private Collection<Widget> getWidgets(Widget w) {
			//System.out.println("getWidget: " + w);
			
    		ArrayList<Widget> localList = new ArrayList<Widget>();
    		
    		
//    		localList.addAll(Collections.singletonList(w));
			if (w instanceof Decorations) {
				Decorations d = (Decorations)w;
				//localList.addAll(getAllMenuItems(d.getMenuBar()));
				addCheck(localList,d.getMenuBar());
				
				//!pq: added to make toolitemt pulldowns surface in hierarchy
				if (ignoreOrphans)
					addOrphanedMenus(d, localList);
			}
			if (w instanceof Control) {
				Control c = (Control)w;
				//localList.addAll(getAllMenuItems(c.getMenu()));    		
				addCheck(localList,c.getMenu());
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
	};
	
	class OrphanFinder {

//		private SWTHierarchy _hierarchy;
//
//		public OrphanFinder(Display display) {
//			_hierarchy = new SWTHierarchy(display);
//		}

		/**
		 * Is this menu orphaned by this shell?
		 * @param menu - the menu in question
		 * @param shell - the shell in question
		 * @return true if the menu is an orphan
		 */
		public boolean isOrphanedBy(Menu menu, Shell shell) {
			Control[] children = shell.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (isContainedBy(menu, child))
					return false;
			}
			return !isSubMenu(menu);
		}


		private boolean isContainedBy(Menu menu, Control control) {
			Collection<Widget> widgets = getImmediateChildren(control);
			return widgets == null ? false : widgets.contains(menu);
		}

		public boolean isSubMenu(Menu menu) {
			return menu.getParentMenu() != null;
		}

	}

	private boolean ignoreOrphans;
	
	/*
	 * Turn off orphan menu handling.  This legacy behavior needs to be reviewed.
	 */
	public LegacyFinderUtil ignoreOrphanedMenus(){
		ignoreOrphans = true;
		return this;
	}
	
	
    public Collection<Widget> getImmediateChildren(final Widget w) {
    	/**
    	 * The lists may be a bit confusing, but what is happening is that the UI 
    	 * thread looks for widgets that are descendants of the widget passed to 
    	 * this function.  It keeps a list of these that is local to that thread 
    	 * instance, and this list may include widgets found in recursive calls 
    	 * to this function.  The inner thread can only communicate through 
    	 * shared class variables, so the class variable outerList is used to 
    	 * store the results of localList.  The function then reads this and returns 
    	 * its values.
    	 */

    	/* hierarchy issues to resolve: do we want this function to recurse? should 
    	 * we have a helper function that gets all direct descendants and another 
    	 * that does the recursion? */
//    	outerList = new ArrayList();
//    	ArrayList innerList = new ArrayList();
        //Runnable widgetGetter =  new Runnable() { 
    	WidgetGetter widgetGetter = new WidgetGetter(w);
    	/*
    	 * !pq: Using the widget's display here is unsafe since it may be disposed!
    	 */
        //w.getDisplay().syncExec(widgetGetter); 
        //display.syncExec(widgetGetter);
    	
        new DisplayReference(w.getDisplay()).execute(widgetGetter);
        
        
        return widgetGetter.getList();
        //innerList.addAll(outerList);
//        System.out.println("List contents:");
//        System.out.println("List: " + list);
        //return innerList;
    }
	
	

	@SuppressWarnings("unchecked")
	private static void addCheck(Collection dest, Collection src) {
		/* add object to collection if non-null */
		if (src.size() > 0) {
			// Iterator iter = src.iterator();
			// while (iter.hasNext()) {
			// dest.addAll(getWidgets((Widget)iter.next()));
			// }
			dest.addAll(src);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void addCheck(Collection c, Object o) {
		/* add object to collection if non-null */
		if (o != null) {
			c.add(o);
			// c.addAll(getWidgets((Widget)o));
		}
	}
	
//    @SuppressWarnings("unchecked")
//	private Collection getAllMenuItems(Menu m) {
//    	/* get all menus and menu items rooted at this menu */
//    	ArrayList list = new ArrayList();
//    	if (m != null) {
//    		MenuItem [] items = m.getItems();
//    		list.add(m);
//    		for (int i=0;i<items.length;i++) {
//    			list.addAll(getAllMenuItems(items[i].getMenu()));
//    			list.add(items[i]);
//    		}
//    	}
//    	return list;
//    }
    
	/**
	 * Add orphaned menus to the list.
	 * <br>
	 * Explanation:  Menus that belong to the shell but not the menubar (e.g.,
	 * toolitme pull-downs) are not in the default hierarchy.  To remedy this
	 * we need to introspect the Shell's "menus" field which contains these references.
	 * Unfortunately, this array also contains references to menus owned by other controls
	 * in the hierarchy.  Our strategy for avoiding duplicates is to look ahead for
	 * each menu and see if it is contained in any of the Shell's children.
	 * 
	 * @see OrphanFinder#isOrphanedBy(Menu, Shell)
	 * 
	 * @author Phil Quitslund
	 */
	private void addOrphanedMenus(Decorations d, ArrayList<Widget> localList) {
		if (d instanceof Shell) {
			Menu[] menus = getMenus((Decorations)d);
			if (menus != null) {
				OrphanFinder finder = new OrphanFinder();
				for (int i = 0; i < menus.length; i++) {
					Menu menu = menus[i];
					if (menu != null && finder.isOrphanedBy(menu, (Shell) d)) {
						addCheck(localList, menu);
					}
				}
			}
		}
	}
    
	
	public static Menu[] getMenus(Decorations shell) {
		Menu[] result = null;
		try {
			if (OS.isOSX()) {
				Menu bar = shell.getMenuBar();
				if (bar == null)
					return new Menu[0]; // TODO Mac testing
				MenuItem[] items = bar.getItems();
				result = new Menu[items.length];
				for (int i = 0; i < items.length; i++)
					result[i] = items[i].getMenu();
			} else {
				Field field = Decorations.class.getDeclaredField("menus");
				field.setAccessible(true);
				result = (Menu[]) field.get(shell);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			// TODO - decide what should happen when the method is unavailable
		}
		return result;
	}
	
    
// 
//    @SuppressWarnings("unchecked")
//	public Collection getRootDecorations(Display display) {
//    	//!pq: added for insertion order invariant
//        Set set = new LinkedHashSet();
//        // TODO: In this version we don't do any tracking, but we provide this 
//        // function for similarity with the Abbot API
//        
////        synchronized(contexts) {
////            Iterator iter = contexts.keySet().iterator();
////            while (iter.hasNext()) {
////                EventQueue queue = (EventQueue)iter.next();
////                Toolkit toolkit = Toolkit.getDefaultToolkit();
////                Map map = (Map)contexts.get(queue);
////                set.addAll(map.values());
////            }
////        }
////        Shell activeShell = null;
////        if (display != null) { 
////        	activeShell = display.getActiveShell();
////        } else {
////        	System.out.println("Couldn't find display");
////        }
////        if (activeShell==null) {
////        	System.out.println ("Tracker couldn't find any shells");
////        	return set;
////        }
////        Shell[] shells = activeShell.getShells();
//        Shell [] shells = getShells(display);
//        for (int i=0;i < shells.length;i++) {
//            set.add(shells[i]);
//        }
//        //Log.debug(String.valueOf(list.size()) + " total Frames");
//        return set;
//    }
//	
//    
//	public static /* synchronized */ Shell[] getShells(final Display d){
//		// [author=Dan] Do NOT synchronize this entire method because 
//		// it causes the test thread and the UI thread to occasionally deadlock
//		// Instead, rework method so that it does not use a field 
//		// and thus does not need to be synchronized.
//		if(d == null || d.isDisposed())
//			return new Shell[] {};
//		if(d.getThread() == Thread.currentThread())
//			return d.getShells();
//		try{
//			Thread.sleep(0,10);
//		}
//		catch(Exception e) {
//		}
////		final Object[] result = new Object[1];
////		Robot.syncExec(d,null,new Runnable(){
////			public void run(){
////				if (d.isDisposed())
////					result[0] = new Shell[] {};
////				else
////					result[0] = d.getShells();
////			}
////		});
////		return (Shell[]) result[0];
//		return new DisplayReference(d).execute(new Callable<Shell[]>(){
//			public Shell[] call() throws Exception {
//				if (d.isDisposed())
//					return new Shell[] {};
//				else
//					return d.getShells();
//			}
//			
//		});
//
//	}
	
	
//	  public void dbPrintWidgets() {
//	    	/* debugging function that prints all widgets in the hierarchy */
//	    	/* rewritten to use getWidgets */
//	    	Collection allRoots = getRoots();
//	        Iterator iter = allRoots.iterator();        
//	        System.out.println("Printing widgets:");
//	        //System.out.println("Current display:" + display.toString());
//	        System.out.println("Roots:");
//	        while (iter.hasNext()) {
//	        	final Shell shell = (Shell)iter.next();
//	        	display.syncExec( new Runnable() {
//	        		public void run() {
//	        			dbPrintWidgets(shell,1);
//	                	//String s = shell.toString();
//	                	//System.out.println("Root Shell: " + s);  
//	                	//dbPrintMenu(shell.getMenuBar(), 1);
//	                	//dbPrintChildren(shell, 1);
//	        		}
//	        	} );
//	        }
//	    }

	public Collection<Widget> getAllWidgets(Widget w) {

		LinkedHashSet<Widget> widgets = new LinkedHashSet<Widget>();
		
		return getAllWidgets(widgets, w);
	}   
	    
	
	private Collection<Widget> getAllWidgets(Collection<Widget> widgets,
			Widget w) {
		widgets.add(w);
		for (Widget child : getImmediateChildren(w))
			getAllWidgets(widgets, child);
		return widgets;
	}



	public static Menu[] getMenus(Display d) {
		MenuItem[] result = null;
		try {			
			Field field = d.getClass().getDeclaredField("items");
			field.setAccessible(true);
			result = (MenuItem[]) field.get(d);
		} catch (Throwable th) {
			th.printStackTrace();
			// TODO - decide what should happen when the method is unavailable
		}
		LinkedHashSet<Menu> set = new LinkedHashSet<Menu>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				if (result[i] != null) {
					Menu menu = (Menu) ((MenuItem) result[i]).getParent();
					// TODO: clean this up!
					if (menu != null && !isSubMenu(menu) && !parentIsControl(menu))
						set.add(menu);
				}
			}
		}
		return (Menu[])set.toArray(new Menu[]{});
	}

	//TODO: fix me... producing duplicates...
	private static boolean isSubMenu(Menu menu) {
		MenuItem parent = menu.getParentItem();
		return parent != null;
	}
	
	private static boolean parentIsControl(Menu menu) {
		Decorations parent = menu.getParent();
		return (parent != null && parent.getClass().equals(Control.class));
	}
    
}
