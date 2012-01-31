package abbot.finder.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
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

import abbot.ExitException;
import abbot.Log;
import abbot.tester.swt.DecorationsTracker;
import abbot.tester.swt.Robot;

import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;

/** Provides access to the current SWT hierarchy. 
 * 
 * 	Methods beginning with db are for development debugging and should not 
 *  be considered part of the final API.
 * 
 * */


public class SWTHierarchy implements Hierarchy {
	protected static Display display; // = new Display();
    protected static DecorationsTracker tracker; //= DecorationsTracker.getTracker(display);
//    private ArrayList outerList;// = new ArrayList();
//    private Decorations rootDecorations;

    // static vars used for inner classes (ie, runnables).
	// Only use these in synchronized methods
//	private boolean boolT;
	private Widget widgetT;
//	private String stringT;
//	private int intT;
//	private Object objT;
	
    public SWTHierarchy(Display d) {
    	display = d;
    	tracker = DecorationsTracker.getTracker(display);
    }
    
    /* Default hierarchy removed: no longer necessary */
    
    /** Returns whether the given widget is reachable from any of the root
     * windows.  The default is to consider all widgets contained in the
     * hierarchy, whether they are reachable or not (NOTE: isReachable is a
     * distinctly different operation).
     */
    public boolean contains(Widget c) {
    	/* not implemented in abbot either */
        return true;
    }

    public void dispose(final Decorations w) {
//        Decorations[] owned = w.getOwnedDecorationss();
//        for (int i=0;i < owned.length;i++) {
//            // Decorations.dispose is recursive; make Hierarchy.dispose recursive
//            // as well.
//            dispose(owned[i]);
//        }
//
//        if (SWT.isSharedInvisibleFrame(w)) {
//            // Don't dispose, or any child windows will be disposed
//            // automatically. 
//            return;
//        }

        // Ensure the dispose is done on the UI thread so we can catch any
        // exceptions. 
        Runnable action = new Runnable() {
            public void run() {
                try {
                    // Distinguish between the abbot framework disposing a
                    // window and anyone else doing so.
                    System.setProperty("abbot.finder.disposal", "true");
                    w.dispose();
                    System.setProperty("abbot.finder.disposal", "false");
                }
                catch(NullPointerException npe) {
                    // Catch bug in SWT 1.3.1 when generating hierarchy
                    // events 
                    Log.log(npe);
                }
                catch(ExitException e) {
                    // Some apps might call System.exit on WINDOW_CLOSED
                    Log.log("Ignoring SUT exit: " + e);
                }
                catch(Throwable e) {
                    // Don't allow other exceptions to interfere with
                    // disposal.
                    Log.warn(e);
                    Log.warn("An exception was thrown when disposing "
                             + " the window " + Robot.toString(w)
                             + ".  The exception is ignored");
                }
            }
        };
        Display disposeDisplay = w.getDisplay();
        disposeDisplay.syncExec(action);
    }

    /** Return all root widgets in the current SWT hierarchy. */
    // TODO: implement in SWT DecorationsTracker
    public Collection getRoots() {
        return tracker.getRootDecorations();
    }

    public Display getDisplay() {
    	return display;
    }
    
    private void addCheck(Collection c, Object o) {
    	/* add object to collection if non-null */
    	if (o!=null) {
    		c.add(o);
    		//c.addAll(getWidgets((Widget)o));
    	}
    }

    private void addCheck(Collection dest, Collection src) {
    	/* add object to collection if non-null */
    	if (src.size()>0) {
//    		Iterator iter = src.iterator();
//    		while (iter.hasNext()) {
//    			dest.addAll(getWidgets((Widget)iter.next()));
//    		}
    		dest.addAll(src);
    	}
    }

    
    protected static final Collection EMPTY = new ArrayList();
    /** Return all descendents of interest of the given Widget.
        This includes owned windows for Decorations, children for Composites.
     */

	private class WidgetGetter implements Runnable {
		//to ensure no duplicates...
    	private LinkedHashSet list = new LinkedHashSet();
    	private Widget rootWidget;
    	public Collection getList() { return list; }
    	WidgetGetter (Widget w) {
    		this.rootWidget = w;
    	}
    	
    	public void run() {
    		try {
    			if (rootWidget != null && !rootWidget.isDisposed())
    				list.addAll(getWidgets(rootWidget));
    		} catch(Exception e) {
    			LogHandler.log(e);
    		}
    		
    		
    		
//    		Iterator iter = list.iterator();
//    		while (iter.hasNext()) {
//    			list.addAll(getDirectWidgets((Widget)iter.next()));
//    		}
    	}

//    	private Collection getWidgets(Widget w) {
//    		ArrayList localList = new ArrayList();
//    		LinkedList searchQ = new LinkedList(Collections.singletonList(rootWidget));
//    		while (searchQ.size() > 0) {
//    			Widget current = (Widget)searchQ.removeFirst();
//    			localList.add(current);
//    			searchQ.addAll(getDirectDescendantWidgets(current));
//    		}
//    		return localList;
//    	}
    	
//    	//!pq:
//    	private Collection addUnattachedMenus() {
//			System.out.println("adding...");
//    		// TODO Auto-generated method stub
//			return null;
//		}
    	
    	
		private Collection getWidgets(Widget w) {
			//System.out.println("getWidget: " + w);
			
    		ArrayList localList = new ArrayList();
    		
    		
//    		localList.addAll(Collections.singletonList(w));
			if (w instanceof Decorations) {
				Decorations d = (Decorations)w;
				//localList.addAll(getAllMenuItems(d.getMenuBar()));
				addCheck(localList,d.getMenuBar());
				
				//!pq: added to make toolitemt pulldowns surface in hierarchy
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

    
//    public Collection getWidgets(final Widget w) {
//    	/* read up on inner anonymous classes */
//    	
//    }

    public Collection getWidgets(final Widget w) {
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
        display.syncExec(widgetGetter);
    	
        return widgetGetter.getList();
        //innerList.addAll(outerList);
//        System.out.println("List contents:");
//        System.out.println("List: " + list);
        //return innerList;
    }

    public synchronized Widget getParent(final Widget c) {
//    	if (c instanceof Control) {
//    		return ((Control)c).getParent();
//    	}
//    	if (c instanceof Menu) {
//    		return ((Menu)c).getParent();
//    	}
//    	if (c instanceof Item) {
//    		// TODO: return parents for each item type if necessary
//    	}
    	widgetT = null;
		Robot.syncExec(c.getDisplay(),this,new Runnable(){
			public void run(){

				if(c instanceof Control)
					widgetT =  ((Control)c).getParent();
				if(c instanceof Caret)
					widgetT =  ((Caret)c).getParent();		
				if(c instanceof Menu)
					widgetT =  ((Menu)c).getParent();		
				if(c instanceof ScrollBar)
					widgetT =  ((ScrollBar)c).getParent();					
				if(c instanceof CoolItem)
					widgetT =  ((CoolItem)c).getParent();		
				if(c instanceof MenuItem)
					widgetT =  ((MenuItem)c).getParent();		
				if(c instanceof TabItem)
					widgetT =  ((TabItem)c).getParent();		
				if(c instanceof TableColumn)
					widgetT =  ((TableColumn)c).getParent();		
				if(c instanceof TableItem)
					widgetT =  ((TableItem)c).getParent();		
				if(c instanceof ToolItem)
					widgetT =  ((ToolItem)c).getParent();											
				if(c instanceof TreeItem)
					widgetT =  ((TreeItem)c).getParent();							
				if(c instanceof DragSource)
					widgetT =  ((DragSource)c).getControl().getParent();
				if(c instanceof DropTarget)
					widgetT =  ((DropTarget)c).getControl().getParent();
				if(c instanceof Tracker)
					Log.debug("requested the parent of a Tracker- UNFINDABLE");	
			}
		});
    	return widgetT;
    }
    
    private String indent(int level) {
    	String indentation = "";
    	for (int i=0;i<level;i++) indentation = indentation + " ";
    	return indentation;
    }
    
    private void dbPrintItems (Item [] items, int level) {
    	for (int i=0;i<items.length;i++) {
    		System.out.println(indent(level) + items[i].toString());
    		if (items[i] instanceof MenuItem) {
    			Menu m = ((MenuItem)items[i]).getMenu();
    	       	dbPrintMenu(m, level+1);   			
    		}
 
    	}
    }
    
    private void dbPrintMenu(Menu m, int level) {
    	if (m != null) {
    		System.out.println (indent(level) + m.toString());
    		dbPrintItems(m.getItems(), level + 1);
    	}
    }
        
//    private Collection getAllMenuItems(Menu m) {
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
    
//    private void dbPrintItems(Composite c, int level) {
//    	/* Prints items in composites that have them */
//    	if (c instanceof ToolBar) {
//    		dbPrintItems(((ToolBar)c).getItems(), level);
//    	}
//    	if (c instanceof CoolBar) {
//    		dbPrintItems(((CoolBar)c).getItems(), level);
//    	}
//    	if (c instanceof Tree) {
//    		dbPrintItems(((Tree)c).getItems(), level);
//    	}
//    	if (c instanceof Table) {
//    		dbPrintItems(((Table)c).getColumns(), level);
//    		dbPrintItems(((Table)c).getItems(), level);
//    	}
//    	if (c instanceof TabFolder) {
//    		dbPrintItems(((TabFolder)c).getItems(), level);
//    	}    	
//    }
    
//    private void dbPrintChildren(Composite c, int level) {
//    	/* recursively prints children of composite */
//		if (c instanceof Shell) {
//			Shell[] shells = ((Shell)c).getShells();
//    		System.out.println(indent(level) + "child shells: " + shells.length);
//			for (int j=0;j<shells.length;j++) {
//    			dbPrintMenu(shells[j].getMenu(), level+1);
//    			dbPrintChildren(shells[j], level+1);    				
//			}
//		}
//    	Control[] children = c.getChildren();
//    	for (int i=0;i<children.length;i++) {
//    		System.out.println(indent(level) + children[i].toString());
//    		if (children[i] instanceof Control) {
//    			dbPrintMenu(children[i].getMenu(), level+1);
//    		}
//    		if (children[i] instanceof Composite) {
//    			Composite child = (Composite)children[i];
//    			dbPrintItems (child, level+1);
//    			dbPrintChildren(child, level+1);
//    		}
//    	}
//    }
    
//    public void dbPrintWidgets() {
//    	/* debugging function that prints all widgets in the hierarchy */
//    	Collection allRoots = getRoots();
//        Iterator iter = allRoots.iterator();        
//        System.out.println("Printing widgets:");
//        //System.out.println("Current display:" + display.toString());
//        System.out.println("Roots:");
//        while (iter.hasNext()) {
//        	final Shell shell = (Shell)iter.next();
//        	display.syncExec( new Runnable() {
//        		public void run() {
//                	String s = shell.toString();
//                	System.out.println("Root Shell: " + s);  
//                	dbPrintMenu(shell.getMenuBar(), 1);
//                	dbPrintChildren(shell, 1);
//        		}
//        	} );
//        }
//    }
    public void dbPrintWidgets() {
    	/* debugging function that prints all widgets in the hierarchy */
    	/* rewritten to use getWidgets */
    	Collection allRoots = getRoots();
        Iterator iter = allRoots.iterator();        
        System.out.println("Printing widgets:");
        //System.out.println("Current display:" + display.toString());
        System.out.println("Roots:");
        while (iter.hasNext()) {
        	final Shell shell = (Shell)iter.next();
        	display.syncExec( new Runnable() {
        		public void run() {
        			dbPrintWidgets(shell,1);
                	//String s = shell.toString();
                	//System.out.println("Root Shell: " + s);  
                	//dbPrintMenu(shell.getMenuBar(), 1);
                	//dbPrintChildren(shell, 1);
        		}
        	} );
        }
    }

    private void dbPrintWidgets (Widget w, int level) {
    	
    	System.out.print(indent(level));
    	boolean visible = SWTHierarchyHelper.isVisible(w);
    	
    	if (!visible)
    		System.out.print("[");
    	System.out.print(w+"<HC|"+w.hashCode()+">");
    	if (!visible)
    		System.out.print("]->(invisible)");
    	System.out.println();
    	
    	//temporary fix:
    	//System.out.println(w.getData("unique.id"));
    	Collection childWidgets = getWidgets(w);
        Iterator iter = childWidgets.iterator();        
    	while (iter.hasNext()) {
    		//Widget j = (Widget)iter.next();
        	//System.out.println(indent(level+1)+j+"<HC|"+j.hashCode()+">");
    		dbPrintWidgets((Widget)iter.next(), level+1);
        }    	
    }

    
    //!pq
	public Widget[] getWidgets() {
		List widgets = new ArrayList();
    	Collection allRoots = getRoots();
        Iterator iter = allRoots.iterator();        
        while (iter.hasNext()) {
        	final Shell shell = (Shell)iter.next();
        	widgets.add(shell);
        	addChildren(shell, widgets);
        }
		return (Widget[]) widgets.toArray(new Widget[]{});
	}
	
	private void addChildren(final Widget w, final List widgets) {
    	display.syncExec( new Runnable() {
    		public void run() {
    	    	Collection childWidgets = getWidgets(w);
    	        Iterator iter = childWidgets.iterator();        
    	    	while (iter.hasNext()) {
    	    		Widget next = (Widget)iter.next();
    	    		widgets.add(next);
    	    		addChildren(next, widgets);
    	        } 
    		}
    	} );
	}

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
	 */
	private void addOrphanedMenus(Decorations d, ArrayList localList) {
		if (d instanceof Shell) {
			Menu[] menus = SWTWorkarounds.getMenus((Decorations)d);
			if (menus != null) {
				OrphanFinder finder = new OrphanFinder(d.getDisplay());
				for (int i = 0; i < menus.length; i++) {
					Menu menu = menus[i];
					if (menu != null && finder.isOrphanedBy(menu, (Shell) d)) {
						addCheck(localList, menu);
					}
				}
			}
		}
	}
	
	
}
