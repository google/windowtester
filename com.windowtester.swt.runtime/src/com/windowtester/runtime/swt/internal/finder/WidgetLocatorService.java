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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
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
import abbot.tester.swt.Robot;
import abbot.tester.swt.RunnableWithResult;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.swt.internal.locator.WidgetIdentifier;
import com.windowtester.runtime.swt.internal.selector.UIProxy;

/**
 * A service/factory class that performs various widget querying services and
 * performs identifying widget info inference.
 * <br>
 * Note: instances cache results of calculations.  If the hierarchy changes between uses, results may be 
 * invalid.  In cases where the hierarchy is changing, a new instance must be created.
 *
 */
public class WidgetLocatorService {

	/**
	 * Set this to <code>true</code> to enable new API generation. (this is a temporary hack.)
	 */
//	public static boolean NEW_API = false;
	
	/**
	 * To properly connect menus with their owners we need to do an exhaustive search or widgets looking at their
	 * children.  To disable this, set this flag to false.
	 * TODO: a future approach might exploit caching...
	 */
	private static final boolean FIND_OWNER_ENABLED = true;

	//a list of keys which we want to propagate to locators
//	private static final String[] INTERESTING_KEYS = {"name"};
	
	private final WeakHashMap<Widget, Widget> _ownerToChildMap = new WeakHashMap<Widget, Widget>();


	/** Cached display used for sync execs and for the creation of an SWTHierachy in the null
	 *  parent elaboration case.
	 */
	private Display _display;

	/** A flag to indicate we reached the root of the hierarchy in elaboration*/
	private boolean _nullParentCase;
	
	/**
	 * Identify widget locator strategy.  (Note: cast is due to faulty type inference in the IDE.)
	 */
	//private IWidgetIdentifierStrategy _widgetIdentifier = NEW_API ? (IWidgetIdentifierStrategy)WidgetIdentifier.getInstance() : new ScopedWidgetIdentifierBuilder();
	private IWidgetIdentifierStrategy _widgetIdentifier = WidgetIdentifier.getInstance();
	
	
	/**
	 * Given a widget and an associated event, infers the (minimal) WidgetLocator that uniquely
	 * identifies the widget.
	 * @param w - the target widget
	 * @return the identifying WidgetLocator or null if there was an error in identification
	 */
	public IWidgetIdentifier inferIdentifyingInfo(Widget w, Event event) {
		return _widgetIdentifier.identify(w, event);
		
	}
	
	/**
	 * Given a widget, infers the (minimal) WidgetLocator that uniquely
	 * identifies the widget.
	 * @param w - the target widget
	 * @return the identifying WidgetLocator or null if there was an error in identification
	 */
	public IWidgetIdentifier inferIdentifyingInfo(Widget w) {
		
		/*
		 * pulling inference into separate strategy
		 */
		return _widgetIdentifier.identify(w);
				
		//return inferIdentifyingInfo_v10(w);
	}

//	/*
//	 * This is the inference algorithm as implemented in public release v1.0.
//	 * 
//	 * This will ultimately be made to go away.  In the meantime, we keep it
//	 * here for easy rollback.
//	 */
//	private WidgetLocator inferIdentifyingInfo_v10(Widget w) {
//		//cache display instance
//		_display = w.getDisplay();
//		
//		boolean found      = false;
//		WidgetLocator info = getInfo(w);
//		do {
//			try {
//				
//				
//				if (rootReached()) {
//					WidgetIdentificationException e = new WidgetIdentificationException("root reached and explored; aborting search");
//					LogHandler.log(e);
//					return null; //indicate a failure
//				}
//					
//				Matcher m = getMatcher(info);
//				TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, "using matcher: " + m);
//				TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, WidgetLocatorService.getJavaString(info));
//				//BasicFinder2.getDefault().dbPrintWidgets();
//				Widget widget = BasicFinder2.getDefault().find(m);
//				//getting here means we have identifying criteria
//				//or it should! there's a bug in the finder which is not catching all multiple widget instances:
//				//so: sanity check to be sure
//				sanityCheckFoundWidget(w, widget);
//				found = true;
//			} catch (WidgetNotFoundException e) {
//				// this is an ERROR!
//				//BasicFinder2.getDefault().dbPrintWidgets();
//				LogHandler.log(e);
//				return null; //indicate a failure
//			} catch (MultipleWidgetsFoundException e) {
//				//ignore, and keep looking...
//				TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, "...multiple widgets found(" + UIProxy.getToString(w) + "): elaborating");
//				info = elaborate(info, w);
//			}
//		} while (!found);
//		
//		return info;
//	}

//	/**
//	 * Generate a Matcher that can be used to identify the widget described
//	 * by this WidgetLocator object.
//	 * @return a Matcher that matches this object.
//	 * @see Matcher
//	 */
//	public static Matcher getMatcher(WidgetLocator wl) {
//		
//		return MatcherFactory.getMatcher(wl);
//		
//		
////		int index = wl.getIndex();
////		String nameOrLabel = wl.getNameOrLabel();
////		WidgetLocator parentInfo = wl.getParentInfo();
////		Class cls = wl.getTargetClass();
////		
////		if (index != WidgetLocator.UNASSIGNED) {
////			if (nameOrLabel != null) {
////				return (parentInfo == null) ? getTargetMatcher(wl)
////						: new HierarchyMatcher(cls, nameOrLabel, index, getMatcher(parentInfo));
////			} else {
////				return (parentInfo == null) ? getTargetMatcher(wl)
////						: new HierarchyMatcher(cls, index, getMatcher(parentInfo));
////			}
////		} else {
////			if (nameOrLabel != null) {
////				return (parentInfo == null) ? getTargetMatcher(wl)
////					: new HierarchyMatcher(cls, nameOrLabel, getMatcher(parentInfo));
////			} else {
////				return (parentInfo == null) ? getTargetMatcher(wl)
////						: new HierarchyMatcher(cls, getMatcher(parentInfo));
////			}
////		}
//	}
	
	
//	/**
//	 * Get the matcher for the target widget.
//	 * @return the target matcher
//	 */
//	private static Matcher getTargetMatcher(WidgetLocator wl) {
//		int index = wl.getIndex();
//		String nameOrLabel = wl.getNameOrLabel();
//		Class cls = wl.getTargetClass();
//		
//		
//		//FIXME: refactor and centralize (duplicated in HierarchyMatcher constructor); also notice uses of IndexMatcher -- should be removed...		
//		if (index == WidgetLocator.UNASSIGNED) {
//			if (nameOrLabel == null) {
//				return new ExactClassMatcher(cls);
//			} else
//				return new CompositeMatcher(new Matcher[] {
//								new ExactClassMatcher(cls),
//								new NameOrLabelMatcher(nameOrLabel) });
//		}
//		if (nameOrLabel == null) {
//			return new IndexMatcher(
//							new ExactClassMatcher(cls), index);
//		} else
//			return new CompositeMatcher(new Matcher[] {
//				new ExactClassMatcher(cls),
//				new IndexMatcher(
//						new NameOrLabelMatcher(nameOrLabel), index) });
//	}
//	
	
	
	
	
//	private void sanityCheckFoundWidget(Widget original, Widget found) throws MultipleWidgetsFoundException {
//		/**
//		 * CCombos are a special case: their button's get the selection event but we want to identify
//		 * them by the button's parent CCombo.
//		 */
//		if (found instanceof CCombo) {
//			if (original instanceof Button)
//				original = new CComboTester().getParent((Button)original);
//		}
//		
//		if (found != original)
//			throw new MultipleWidgetsFoundException("internally generated, sanity check", new Widget[]{original, found});
//	}

//	/**
//	 * Takes an Info object and elaborates on its parentInfo. (Used to 
//	 * disambiguate info with multiple matches.
//	 * @param info - the info to elaborate.
//	 * @param w - the widget being identified.
//	 * @return elaborated WidgetLocator
//	 * FIXME: handle null parent case...
//	 */
//	private WidgetLocator elaborate(WidgetLocator info, Widget w) {
//		
//		TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, "elaborating on: " + info + " widget=" + w);
//		
//		WidgetLocator root = info;
//		
//		boolean elaborated = false;
//		
//		WidgetLocator parentInfo = null;
//		while(!elaborated) {
//			parentInfo = info.getParentInfo();
//			
//			Widget parent = getParent(w);
//			if (parent == null) {
//				TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, UIProxy.getToString(w) + " has null parent");
//				//throw new AssertionError("null parent in elaboration");
//				//!pq: enable this to help debug the null parent case
//				//new SWTHierarchy(w.getDisplay()).dbPrintWidgets();
//				_nullParentCase = true;
//			}
//				
//			
//			if (parentInfo == null) {
//				info.setParentInfo(getInfo(parent));
//				int index = getIndex(w,parent);
//				if (index != WidgetLocator.UNASSIGNED)
//					info.setIndex(index);
//				elaborated = true;
//			}
//			w = parent; //setup for next iteration
//			info = parentInfo;
//		} 
//		return root;
//	}

	
	/**
	 * Check whether the root of the hierarchy was reached in the last info elaboration.
	 */
	public boolean rootReached() {
		return _nullParentCase;
	}
	
//	/**
//	 * Generates a Java string that when interpreted creates an object identical
//	 * to this one.  By default class names are unqualified.
//	 * @return a Java string describing this object
//	 */
//	public static String getJavaString(WidgetLocator locator) {
//		return getJavaString(locator, false);
//	}
//	
//	/**
//	 * Generates a Java string that when interpreted creates an object identical
//	 * to this one.
//	 * @param qualify - falg indicating whether to qualify class names.
//	 * @return a Java string describing this object
//	 */
//	public static String getJavaString(WidgetLocator wl, boolean qualify) {
//		StringBuffer sb = new StringBuffer();
//		
//		
//		if (wl instanceof ViewLocator) {
//			
//			sb.append("new ViewLocator(");
//			sb.append("\"").append(wl.getNameOrLabel()).append("\"");	
//				
//		} else if (wl instanceof LabeledLocator) {
//			
//			sb.append("new LabeledLocator(");
//			sb.append(getClassName(wl.getTargetClass(), qualify)).append(", \"");	
//			sb.append(wl.getNameOrLabel()).append("\"");
//			
//		} else if (wl instanceof ShellLocator) {
//		
//			sb.append("new ShellLocator(");
//			sb.append("\"").append(wl.getNameOrLabel()).append("\", ");	
//			sb.append(((ShellLocator)wl).isModal());
//		
//		} else { 
//			
//			/*
//			 * View Locators don't use the class field
//			 */
//			
//			sb.append("new WidgetLocator(");
//			sb.append(getClassName(wl.getTargetClass(), qualify));
//			
//			//name
//			String nameOrLabel = wl.getNameOrLabel();
//			if (nameOrLabel != null)
//				sb.append(", \"").append(nameOrLabel).append("\"");	
//			
//
//			
//		}
//		
//
//		
//		//index
//		int index = wl.getIndex();
//		if (index != WidgetLocator.UNASSIGNED)
//			sb.append(", ").append(index);
//		
//		//parent
//		WidgetLocator parentInfo = wl.getParentInfo();
//		if (parentInfo != null)
//			sb.append(", ").append(getJavaString(parentInfo, qualify));
//		
//		//close
//		sb.append(")");
//		return sb.toString();
//	}

//	private static String getClassName(Class cls, boolean qualify) {
//		// get the simple name of the class
//		int lastPeriod = cls.getName().lastIndexOf('.');
//		String simpleName = (lastPeriod >= 0) ? cls.getName().substring(
//				lastPeriod + 1) : cls.getName();
//
//		// cls
//		String clsName = (qualify) ? cls.getName() : simpleName;
//		clsName +=".class";
//		return clsName;
//	}

	
	
		
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
				//also check for nameOrLabelMatch && visibility
				if (nameAndOrLabelDataMatch(w, child) && SWTHierarchyHelper.isVisible(child))
					++count;	
			}
			if (child == w)
				index = count-1; //indexes are zero-indexed
		}
		return (count > 1) ? index : -1;
		//throw new IllegalStateException("unfound child");
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
   	 * (Taken from SWTHierarchy and modified.)
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
	
//	/**
//	 * Create an (unelaborated) info object for this widget. 
//	 * @param w - the widget to describe.
//	 * @return an info object that describes the widget.
//	 */
//	private WidgetLocator getInfo(Widget w) {
//		if (w == null) {
//			//return new WidgetLocator(NullParent.class);//TODO: handle NullParent case...
//			return null;
//		}
//		/**
//		 * CCombos require special treatment as the chevron is a button and receives the click event.
//		 * Instead of that button, we want to be identifying the combo itself (the button's parent).
//		 */
//		if (w instanceof Button) {
//			Widget parent = new ButtonTester().getParent((Button)w);
//			if (parent instanceof CCombo)
//				w = parent;
//		}
//		
//		Class cls   = w.getClass();
//		/**
//		 * We don't want the combo text to be part of the identifying information since it
//		 * is only set to the value AFTER it is selected...
//		 * Text values are also too volatile to use as identifiers.
//		 * 
//		 */
//		String text = (w instanceof Combo || w instanceof CCombo || w instanceof Text || w instanceof StyledText)? null : getWidgetText(w);
//		WidgetLocator locator = (text != null) ? new WidgetLocator(cls, text)
//				: new WidgetLocator(cls);
//		
//		setDataValues(locator, w);
//		
//		return locator;
//	}

	
//	//propagate values of interest from the widget to the locator
//	private void setDataValues(WidgetLocator locator, Widget w) {
//		String key;
//		Object value;
//		WidgetTester tester = new WidgetTester();
//		for (int i= 0; i < INTERESTING_KEYS.length; ++i) {
//			key = INTERESTING_KEYS[i];
//			value = tester.getData(w, key);
//			if (value != null)
//				locator.setData(key, value.toString());
//		}
//	}

	/**
	 * Extract the text from the given widget.
	 * @param w - the widget in question
	 * @return the widget's text
	 */
	public static String getWidgetText(final Widget w) {

        return (String)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
    		public Object runWithResult() {
		    	if (w instanceof Control) {
		    		//controlShowing = ((Control)w).getVisible() && ((Control)w).getShell().getVisible();
		    		//System.out.println("Widget " + w + " showing: " + controlShowing);
		    	}
		    	if (w instanceof Button) {
		    		return (((Button)w).getText());       	    		
		    	}
		    	//!pq: Combo text data is too volatile for matching...
		    	if (w instanceof Combo) {
		    		//return (((Combo)w).getText());     
		    		return null;
		    	}
		    	if (w instanceof Decorations) {
		    		return (((Decorations)w).getText());       	    		
		    	}
		    	if (w instanceof Group) {
		    		return (((Group)w).getText());       	    		
		    	}
		    	if (w instanceof Item) {
		    		if (w instanceof TableItem && ((TableItem)w).getParent().getColumnCount() > 0 ) {
		    			//int columns = ((TableItem)w).getParent().getColumnCount();
		    			return (((TableItem)w).getText(0));
		    			//TODO: this isn't quite right... 
		    			
//		    			String[] lWtexts = new String[columns];
//		    			for (int i=0;i<lWtexts.length;i++) {
//		    				lWtexts[i] = ((TableItem)w).getText(i);
//		    			}
//		    			setWtexts(lWtexts);
		    		} else {
		    			return (((Item)w).getText());
		    		}
		    	}
		    	if (w instanceof Label) {
		    		return (((Label)w).getText());       	    		
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
		    	if (w instanceof Link) {
		    		return ((Link)w).getText();
		    	}
		    	//fall through ....
		    	return null;
    		}});
	}
	
	
    /**
     * Retrieve the parent of the given widget.
     * @param widget - the widget in question
     * @return the widget's parent, or null if it has none
     */
    public synchronized Widget getParent(final Widget widget) {
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
    	
    	if (widget == null || widget.isDisposed())
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
	
    public static final class WidgetIdentificationException extends RuntimeException {
		private static final long serialVersionUID = -2487576955669523193L;

    	public WidgetIdentificationException(String msg) {
			super(msg);
		}
    }
    
    
}
