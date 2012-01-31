package abbot.swt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.util.StringComparator;

import abbot.Log;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.WidgetNotFoundException;
import abbot.script.swt.ButtonReference;
import abbot.script.swt.WidgetReference;
import abbot.tester.swt.DecorationsTracker;
import abbot.tester.swt.Robot;
import abbot.tester.swt.WidgetTester;

/**
 * 
 * @deprecated Use the new matcher API in abbot.finder.swt.  The class
 * abbot.finder.swt.BasicFinder most closely duplicates the functionality
 * of this class.
 * 
 */
public class DefaultWidgetFinder implements WidgetFinder {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/** Match weight for a strong match. */
	public static final int MW_STRONG_MATCH = 50;
	/** Match weight for a weak match. */
	public static final int MW_WEAK_MATCH = 1;

	public static final String ROOT_MENU_FLAG ="ROOT_MENU";

	// crude, very Eclipse-dependent, and un-i14ed
	// attempt to identify main workbench window
	public static final String WORKBENCH_TITLE = "Resource - ";

	// Matching weights for various attributes
	private static final int MW_TEXT = 10;
	private static final int MW_NAME = 100;
	private static final int MW_TAG = 50;
	private static final int MW_PARENT = 25;
	private static final int MW_INVOKER = 25;
	private static final int MW_INDEX = 25;
	private static final int MW_WINDOW = 10;
	private static final int MW_TITLE = 10;
	private static final int MW_CLASS = 1;
	/** Match weight corresponding to no possible match. */
	public static final int MW_FAILURE = 0;
	/** Timeout for waiting on a popup menu. */
	public static int POPUP_TIMEOUT = 5000;

	private WeakHashMap filteredWidgets = new WeakHashMap();
	/** Allow chaining to existing filter sets. */
	private WidgetFinder parent = null;
	private boolean filter = true;
	//private WindowTracker tracker = WindowTracker.getInstance();

	static {
		String to = System.getProperty("abbot.finder.popup_timeout");
		if (to != null) {
			try {
				POPUP_TIMEOUT = Integer.parseInt(to);
			}
			catch(Exception e) {
			}
		}
	}

	private static DefaultWidgetFinder defaultFinder = null;
	
	// static vars used for inner classes (ie, runnables).
	// Only use these in synchronized methods
	private boolean boolT;
	private Widget widgetT;
	private String stringT;
	private int intT;
	private Object objT;

	/** This is the factory method to use unless you already know otherwise :)
	 */ 
	public static WidgetFinder getFinder() {
		return getFinder(null);
	}

	/** This factory method allows the finder to be used where a finder
	 * already exists and is filtering components.  This should only be
	 * required in very rare cases like when the script editor needs to test
	 * itself. 
	 */
	public static synchronized WidgetFinder getFinder(WidgetFinder context) {
		if (defaultFinder == null) {
			defaultFinder = new DefaultWidgetFinder(context);
		}
		return defaultFinder;
	}

	/** Only factory creation is allowed. */
	private DefaultWidgetFinder(WidgetFinder parent) {
		this.parent = parent;
	}

	/** 
	 * Attempt to find the component corresponding to the given reference
	 * among existing, visible components.
	 *
	 *  @param ref A Widget reference to use to identify a component
	 *  @throws WidgetNotFoundException if a Widget cannot be located via
	 *  the passed in WidgetReference
	 *  @throws MultipleWidgetsFoundException if more then one component is 
	 *  found via the passed in reference       
	 *  @return The component which matches the passed in WidgetReference
	 */
	public Widget findWidget(WidgetReference ref)
		throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return findWidget(ref, null);
	}

	/** 
	 * Attempt to find the component corresponding to the given reference
	 * among existing, visible components.
	 *
	 * @param ref A Widget reference to use to identify a component
	 * @param s a Shell in which to look
	 * @throws WidgetNotFoundException if a Widget cannot be located via
	 * the passed in WidgetReference
	 * @throws MultipleWidgetsFoundException if more then one component is 
	 * found via the passed in reference       
	 * @return The component which matches the passed in WidgetReference
	 */
	public Widget findWidget(WidgetReference ref, Shell s)
		throws WidgetNotFoundException, MultipleWidgetsFoundException {
		HashSet comps = findMatches(s, ref, MATCH_WEAK);
		Iterator iter = null;
		Widget comp = null;
		int max = -99999;
		ArrayList multiples = new ArrayList();

		if (comps.isEmpty()) {
			throw new WidgetNotFoundException("Widget '" + ref
												 + "' not found");
		}

		if (comps.size() == 1) {
			iter = comps.iterator();
			return (Widget)iter.next();
		}

		iter = comps.iterator();
		comp = (Widget)iter.next();
		max = getWidgetMatchWeight(comp, ref);
		while(iter.hasNext()) {
			Widget next = (Widget)iter.next();
			int val = getWidgetMatchWeight(next, ref);
			if (val > max) {
				max = val;
				comp = next;
				multiples.clear();
			}
			else if (val == max) {
				if (multiples.size() == 0) {
					multiples.add(comp);
				}
				multiples.add(next);
			}
		}

		if (multiples.size() > 0) {
			String msg = "More than one match for '" + ref + "' found";
			final Widget[] list = (Widget[])
				multiples.toArray(new Widget[multiples.size()]);
			System.err.println("MULTIPLE WIDGETS FOUND:");
			for(int j=0; j<list.length;j++){
				final int index = j;
				Robot.syncExec(((Widget)list[j]).getDisplay(),null,new Runnable(){
					public void run(){
						System.err.println("\t"+list[index]);				
					}					
				});

			}
			System.err.println("------------------------\n");
			throw new MultipleWidgetsFoundException(msg, list);
		}
		return comp;
	}

	/**
	 * Find a <code>MenuItem</code> given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static MenuItem findMenuItem(WidgetReference ref) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {	
		return (MenuItem)(getFinder().findWidget(ref));
	}

	/**
	 * Find a <code>MenuItem</code> in the indicated <code>Shell</code> 
	 * given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static MenuItem findMenuItemInShell(WidgetReference ref, Shell sh) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return (MenuItem)(getFinder().findWidget(ref, sh));
	}

	/**
	 * Find a <code>Text</code> given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static Text findText(WidgetReference ref) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {	
		return (Text)(getFinder().findWidget(ref));
	}

	/**
	 * Find a <code>StyledText</code> given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static StyledText findStyledText(WidgetReference ref) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {	
		return (StyledText)(getFinder().findWidget(ref));
	}

	/**
	 * Find a <code>Text</code> in the indicated <code>Shell</code> 
	 * given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static Text findTextInShell(WidgetReference ref, Shell sh) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return (Text)(getFinder().findWidget(ref, sh));
	}

	/**
	 * Find a <code>StyledText</code> in the indicated <code>Shell</code> 
	 * given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static StyledText findStyledTextInShell(WidgetReference ref, Shell sh) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return (StyledText)(getFinder().findWidget(ref, sh));
	}

	/**
	 * Find a <code>Label</code> given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static Label findLabel(WidgetReference ref) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {	
		return (Label)(getFinder().findWidget(ref));
	}

	/**
	 * Find a <code>Label</code> in the indicated <code>Shell</code> 
	 * given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static Label findLabelInShell(WidgetReference ref, Shell sh) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return (Label)(getFinder().findWidget(ref, sh));
	}

	/**
	 * Find a <code>Button</code> given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static Button findButton(WidgetReference ref) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {	
		return (Button)(getFinder().findWidget(ref));
	}

	/**
	 * Find a <code>Button</code> in the indicated <code>Shell</code> 
	 * given a suitable <code>Reference</code>.
	 * CONTRACT: suitability is not checked!
	 */
	public static Button findButtonInShell(WidgetReference ref, Shell sh) 
	throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return (Button)(getFinder().findWidget(ref, sh));
	}

	
	/** Return the window with the given name or title.  Attempts to find a
	 * named window first, since that search is more restrictive. */
	public Decorations findDecorations(String nameOrTitle) {
		Decorations w = findDecorationsByName(nameOrTitle);
		if (w == null)
			w = findDecorationsByTitle(nameOrTitle);
		return w;
	}
	
	/**
	 * Locates a window using the name as the match criterion.
	 * @param match The string to match with a window name
	 * @return A window with a name equal to the passed in string, null
	 * otherwise 
	 */
	public synchronized Decorations findDecorationsByName(String match) {
		Decorations[] windows = getDecorations();
		for (int i=0;i < windows.length;i++) {
			widgetT = windows[i];
			Decorations w = windows[i];
			Robot.syncExec(w.getDisplay(),this,new Runnable(){
				public void run(){
					boolT = ((Decorations)widgetT).isVisible();		
				}
			});
			
			if (boolT) {
				String name = getWidgetName(w);
				if ((match != null && name!=null && match.equals(name))
					|| name == match)
					return w;
			}
		}
		return null;
	}

	/** Return the first visible window whose title matches the given
	 * pattern.
	 */
	public synchronized Decorations findDecorationsByTitle(String title) {
		Decorations[] windows = getDecorations();
		for (int i=0;i < windows.length;i++) {
			Decorations w = windows[i];
			widgetT = w;
			Robot.syncExec(w.getDisplay(),this,new Runnable(){
				public void run(){
					boolT = ((Decorations)widgetT).isVisible();
					intT = ((Decorations)widgetT).getStyle();
					stringT = ((Decorations)widgetT).getText();
				}
			});
			if (boolT && (intT&SWT.TITLE)==SWT.TITLE) {
				if(titlesMatch(title,stringT))
					return w;
			}
		}
		return null;
	}

	public synchronized Widget findActivePopupMenu(final Widget root) {
		objT = null;
		if (root == null) {
			Shell[] shells = getRootShells();
			for (int i=0;i < shells.length;i++) {
				Widget widget = (Widget)shells[i];
				Widget menu = findActivePopupMenu(widget);
				if (menu != null)
					return menu;
			}
		}
		else{			
			Robot.syncExec(root.getDisplay(),this,new Runnable(){
				public void run(){
					if (root instanceof Menu && ( ((Menu)root).getStyle() & SWT.POP_UP )==SWT.POP_UP
						&& ((Menu)root).isVisible() )
						objT =  root;
			
					else if(root instanceof Control){
						Menu menu = ((Control)root).getMenu();
						if(menu != null && ((Menu)root).isVisible() )
							objT = menu;
					}
				
					else if (root instanceof Composite) {
						Widget[] subs = ((Composite)root).getChildren();
						for (int i=0;i < subs.length;i++) {
							Widget menu = findActivePopupMenu(subs[i]);
							if (menu != null)
								objT = menu;
						}
					}					
				}
			});
		}
		return (Widget)objT;
	}

	public synchronized Widget findMenuItemByText(final Widget root, final String name){
		widgetT = null;
		Robot.syncExec(root.getDisplay(),this,new Runnable(){
			public void run(){
				if(root instanceof Menu){
					MenuItem[] items = ((Menu)root).getItems();
					for(int i=0; i<items.length;i++){
						Widget widget = findMenuItemByText(items[i],name);
						if(widget!=null)
							widgetT = widget;				
					}
				}
				else if(root instanceof MenuItem){
					if(((MenuItem)root).getText().equals(name))
						widgetT = root;
					if(((MenuItem)root).getMenu()!=null)
						widgetT = findMenuItemByText(((MenuItem)root).getMenu(),name);
				}
				else if (root instanceof Decorations){
					Widget widget=null;
					if(((Decorations)root).getMenu()!=null)
						widget = findMenuItemByText(((Decorations)root).getMenu(),name);
					if(widget!=null)
						widgetT =widget;
					else if(((Decorations)root).getMenuBar()!=null)
						widget = findMenuItemByText(((Decorations)root).getMenuBar(),name);
					if(widget!=null)
						widgetT = widget;
				}
				else if (root instanceof Control){
					if(((Control)root).getMenu()!=null)
						widgetT = findMenuItemByText(((Control)root).getMenu(),name);				
				}				
			}
		});
		return widgetT;
	}

	/**
	 * Return the set of all components under the given component's
	 * hierarchy (inclusive) which match the given reference.
	 */	
	/*
	 * Note that <code>ancestor</code> can also be the widget itself:
	 * recursive implementation.
	 */
	private HashSet findMatches(Widget ancestor, WidgetReference ref, int type){
		Log.debug("Scanning " + /*Robot.toString*/(ancestor));
//		// DEBUG
////		if (ancestor instanceof Text) {
//		if ((ancestor instanceof Text) && (ancestor.getData() != null)) {
//			String tag = (String)(ancestor.getData("name"));
////			if ((tag != null) && (tag.length() > 0)) {
//				Log.debug("got Text");
////			}						
//		}
//		// END DEBUG
		
		HashSet set = new HashSet();
		if (isWidgetFiltered(ancestor))
			return set;

		if (ancestor == null) {
			// Examine all top-level components and their owned windows.
//			Shell[] shells = getRootShells();
			List allShells = getAllShells();
			Shell[] shells = (Shell[])(allShells.toArray(new Shell[allShells.size()]));				
			for (int f=0;f < shells.length;f++) {
				set.addAll(findMatches(shells[f], ref, type));
			}
			return set;			
		}
		if (widgetsMatch(ancestor, ref, type)) {
			Log.debug("Found match");
			set.add(ancestor);
		}		

		Widget[] children = getWidgetChildren(ancestor,true);
		for(int i=0; i<children.length;i++){
			Widget child = children[i];
			// COULD skip this hierarchy if the child is a Decorations
			// and 1) frame titles don't match; and 2) the child is not showing
			set.addAll(findMatches(child, ref, type));				
			// TODO this makes many unnecesary recursive checks, so FIX
		}
		return set;
	}

	/**
	 * Return the first shell matching the passed title.
	 */
	public Shell findShellByTitle(String title) {
		List allshells = getAllShells();
		// DEBUG	
		return findShellByTitle(title,
			(Shell[])(allshells.toArray(new Shell[allshells.size()])));
	}

	/**
	 * Return the first shell in the shell array matching the passed title.
	 */
	public Shell findShellByTitle(String title, Shell[] sa){
		return findShellByTitle(title, sa, false);
	}

	/**
	 * Return the first shell in the shell array matching the passed title.
	 */
	public Shell findShellByTitle(
		final String title, final Shell[] sa, final boolean filterWorkbench) {
		Log.debug("start findShellByTitle");
		if (title == null) {
			throw new IllegalArgumentException("must pass title of shell sought");
		}
		if (sa == null) {
			throw new IllegalArgumentException("must pass array of shells");
		}

		// first, breadth-first ...
		List forDepth = new ArrayList(sa.length);
		for (int j = 0; j < sa.length; j++) {
			final Shell s = sa[j];
			if (s != null) {
//				String t = s.getText();
//			SWTException!
//			if (title.equals(t)) return s;
//			if (!filterWorkbench || !t.equals(WORKBENCH_TITLE)) forDepth.add(s);

//				// use boolT as flag, since we can't return inside the runnable
//				boolT = false;
				stringT = null;
				s.getDisplay().syncExec(new Runnable() {
					public void run() {
						stringT = s.getText();
//						if (title.equals(stringT)) boolT = true;
					}
				});
				// TODO: reverse tests, or test stringT. For now, I wanna know if it's crap.
				if (stringT.equals(title)) return s;
				if (!filterWorkbench || !stringT.equals(WORKBENCH_TITLE)) forDepth.add(s);
			}
		}
		// then go for depth. inefficient in sa.length ...
		for (Iterator it = forDepth.iterator(); it.hasNext();) {
			Shell s = findShellByTitleInHierarchy(title, (Shell)(it.next()));
			if (s != null) return s;
		}
		return null;
	}

	// WARNING: THERE MAY BE SOME UNRESOLVED SYNCHRONIZATION ISSUES HERE...
	/**
	 * Return the first Shell with the passed title
	 * among parent Shell or its children.
	 */
	public Shell findShellByTitleInHierarchy(
		final String title, final Shell parent){
		Log.debug("start findShellByTitleInHierarchy");
		if (title == null) {
			throw new IllegalArgumentException("must pass title of shell sought");
		}
		if (parent == null) {
			throw new IllegalArgumentException("must pass parent shell");
		}

//		if (title.equals(parent.getText())) return parent; 
// SWTException!
		// get the title using our private stringT
		stringT = null;
		parent.getDisplay().syncExec(new Runnable() {
			public void run() {
				stringT = parent.getText();
			}
		});
		// TODO: reverse test, or test stringT. For now, I wanna know if it's crap.
		if (stringT.equals(title)) {
			return parent;
		} else {
			return findShellByTitleInList(title, getChildren(parent));
		}
	}

	// WARNING: THERE MAY BE SOME UNRESOLVED SYNCHRONIZATION ISSUES HERE...
	private Control[] kids; // children of parent Shell
	/**
	 * Return a List of Control's that are children of the parent Shell.
	 */
	public List getChildren(final Shell parent){
		kids = null;
		parent.getDisplay().syncExec(new Runnable() {
			public void run() {
				kids = parent.getChildren();
			}
		});
		if ((kids == null) || (kids.length <= 0)) return null; // nothing to do
		ArrayList seek = new ArrayList(kids.length);
		seek.addAll(Arrays.asList(kids));
		return seek;
	}

	/**
	 * Return the first Shell with the passed title
	 * among a List of Control's.
	 */
	public Shell findShellByTitleInList(final String title, List seek){
		if ((seek == null) || (seek.size() <= 0)) return null; // nothing to do
		for (Iterator it = seek.iterator(); it.hasNext();) {
			Control topC = (Control)(it.next());
			if ((topC == null) || (!(topC instanceof Shell))) continue;
			final Shell topS = (Shell)topC;
			if (topS == null) continue;
//		if (title.equals(topS.getText())) {
			// else get its title using our private stringT
			stringT = null;
			topS.getDisplay().syncExec(new Runnable() {
				public void run() {
					stringT = topS.getText();
				}
			});
			// TODO: reverse test, or test stringT. For now, I wanna know if it's crap.
			if (stringT.equals(title)) {
				return topS;
			} else {
				seek.addAll(Arrays.asList(topS.getChildren()));
			}
		} // found nothing?
		return null;
	}

	/** Return the total weight required for an exact match. */
	private int getExactMatchWeight(WidgetReference ref) {
		int weight = MW_CLASS;
		if (ref.getName() != null)
			weight += MW_NAME;
		if (ref.getTag() != null)
			weight += MW_TAG;
		if (ref.getInvokerID() != null)
			weight += MW_INVOKER;
		if (ref.getParentID() != null)
			weight += MW_PARENT;
		if (ref.getWindowID() != null)
			weight += MW_WINDOW;
		if (ref.getTitle() != null)
			weight += MW_TITLE;
		if (ref.getIndex() != -1)
			weight += MW_INDEX;
		return weight;
	}


	// FIXME should probably throw if more than one match is found. 
	public WidgetReference matchWidget(Widget comp,
											 Iterator iter, int matchType) {
		//Log.debug("Looking for " + Robot.toString(comp) + " in a group");
		WidgetReference match = null;
		int matchWeight = -1000;
		while (iter.hasNext()) {
			WidgetReference ref = (WidgetReference)iter.next();
			if (widgetsMatch(comp, ref, matchType)) {
				int wt = getWidgetMatchWeight(comp, ref);
				if (wt > matchWeight) {
					match = ref;
					matchWeight = wt;
				}
				else if (wt == matchWeight) {
					// FIXME work out how to throw and handle this properly
					Log.warn("Multiple references match");
					throw new RuntimeException("Multiple references match");
				}
			}
		}
		//Log.debug(match != null ? "Found" : "Not found");
		return match;
	}

	/** Return a measure of how well the given component matches the given
	 * component reference.
	 */
	public int getWidgetMatchWeight(Widget comp, WidgetReference ref) {
		int weight = MW_FAILURE;

		if (null == comp || null == ref){
			return MW_FAILURE;
		}

		if (!ref.isAssignableFrom(comp.getClass())) {
			return MW_FAILURE;        
		} 
		else {
			weight += MW_CLASS;
			// Exact class matches are better than non-exact matches
			if (ref.getRefClassName().equals(comp.getClass().getName()))
				weight += MW_CLASS;
		}
   
		String compTag = WidgetTester.getTag(comp);
		String refTag = ref.getTag();
		if (null != compTag && null != refTag) {
			if (compTag.equals(refTag)) {
				weight += MW_TAG;
			}
			else {
				return MW_FAILURE;
			}
		}

		// NEW STUFF
		String compText = getWidgetText(comp);
		String refText = ref.getText();
		if(null != compText && null != refText) {
			if(compText.equals(refText))
				weight+= MW_TEXT;
			else
				return MW_FAILURE;	
		}	
		// END NEW STUFF
		
		String compName = getWidgetName(comp);
		String refName = ref.getName();
		if (null != compName && null != refName) {
			if (compName.equals(refName)) {
				weight += MW_NAME;
			}
			else {
				weight -= MW_NAME;
			}
		}

		if (null != ref.getParentID()) {
			if (widgetsMatch(getWidgetParent(comp),
								ref.getParentReference(), MATCH_EXACT)) {
				weight += MW_PARENT;
			}
			// Don't detract on parent mismatch, since changing a parent is
			// not that big a change (e.g. adding a scroll pane)
		}

		if (null != ref.getWindowID()) {
			if (widgetsMatch(getWidgetDecorations(comp),
								ref.getWindowReference(), MATCH_EXACT)) {
				weight += MW_WINDOW;
			} 
			else {
				// Changing windows is a big change and not very likely
				weight -= MW_WINDOW;
			}
		}

		if (ref.getTitle() != null) {
			if (titlesMatch(ref.getTitle(), getWidgetDecorationsTitle(comp))) {
				weight += MW_TITLE;
			}
			// Don't subtract on mismatch, since title changes are common
		}

		

		if (ref.getIndex() != -1) {
			Widget parent = getWidgetParent(comp);
			if (null != parent) {
				Widget[] children = getWidgetChildren(parent,true);
				for (int i = 0; i < children.length; ++i) {
					if (children[i] == comp && i == ref.getIndex()) {
						weight += MW_INDEX;
						break;
					}
				}
			}
		}
		/*
		Log.debug("Comparing " + Robot.toString(comp)
				  + " to '" + ref + "', weight is " + weight);
		*/
		return weight;
	}

	/** Determine the best we can whether the component is the one referred to
	 * by the reference.  The match may be a strong or a weak one.
	 */
	public boolean widgetsMatch(Widget comp, WidgetReference ref,
								   int type){
		int weight = getWidgetMatchWeight(comp, ref);
		int min = 0;
		if (ref==null) return false; // can't be a match if the passed reference is null
		if (type == MATCH_EXACT) {
			min = getExactMatchWeight(ref);
			if (weight >= min && min < MW_STRONG_MATCH) {
				Log.warn("Exact match weight comparing "
						 + ref + " to " + /*Robot.toString*/(comp)
						 + " is less than a strong match");
			}
		}
		else if (type == MATCH_STRONG) {
			min = Math.min(MW_STRONG_MATCH, getExactMatchWeight(ref));
		}
		else if (type == MATCH_WEAK) {
			min = Math.min(MW_WEAK_MATCH, getExactMatchWeight(ref));
		}
		else
			throw new IllegalArgumentException("Type must be one of MATCH_EXACT, MATCH_STRONG, or MATCH_WEAK");
		return weight >= min;
	}
	

	private boolean isWidgetFiltered(Widget comp) {
		if (comp == null)
			return false;
		return filter 
			&& (filteredWidgets.containsKey(comp)
				|| (parent != null && parent.isFiltered(comp)));
	}

	/** Returns if any ancestor composite is filtered */
	private boolean isAncestorFiltered(Widget widget) {
		Widget parent = getWidgetParent(widget);
		
		if (parent == null) {
			return false;
		}
		return isFiltered(parent);
	}

	/** Returns true if the component or its Window ancestor is filtered. */
	public boolean isFiltered(Widget comp) {
		return (comp != null)
			&& (isWidgetFiltered(comp) || isAncestorFiltered(comp));
	}

	public void discardAllWidgets() {
		Iterator iter = getDecorationsList().iterator();
		while(iter.hasNext()) {
			discardWidget((Decorations)iter.next());
		}
	}

	public void setFilterEnabled(boolean enable) {
		this.filter = enable;
	}


// COMPLETED:::::	
   /** Send an explicit window close event to all showing windows.  Note
	   that this is not guaranteed to actually make the window go away.  */
   public synchronized void closeWindows() {
	   Iterator iter = getDecorationsList().iterator();
	   while(iter.hasNext()){
		   Decorations dec = (Decorations)iter.next();
		   widgetT = dec;
		   Robot.syncExec(dec.getDisplay(),this,new Runnable(){
				public void run(){
					if(((Decorations)widgetT).isVisible()){
						((Decorations)widgetT).setVisible(false);
						if(((Decorations)widgetT) instanceof Shell)
							((Shell)widgetT).close();
					}		   			
				}
		   });			
	   }
   }
	
   /** 
	* Return whether the the given title matches the given pattern. 
	*/   
   private boolean titlesMatch(String pattern, String actual) {
	   boolean match = StringComparator.matches(actual, pattern);
	   return match;
   }

   /** Look up the apparent parent of a component.  A
	* popup menu's parent is the menu or component that spawned it.
	*/
   public synchronized Widget getWidgetParent(final Widget widget) {
	   widgetT = null;
	   if(widget==null)
	   	System.err.println("NULL WIDGET");
	   if(this==null)
	   	System.err.println("THIS IS NULL");
		Robot.syncExec(widget.getDisplay(),this,new Runnable(){
			public void run(){
				if(widget instanceof Control)
					widgetT=((Control)widget).getParent();
				if(widget instanceof Caret)
					widgetT=((Caret)widget).getParent();		
				if(widget instanceof Menu)
					widgetT=((Menu)widget).getParent();		
				if(widget instanceof ScrollBar)
					widgetT=((ScrollBar)widget).getParent();					
				if(widget instanceof CoolItem)
					widgetT=((CoolItem)widget).getParent();		
				if(widget instanceof MenuItem)
					widgetT=((MenuItem)widget).getParent();		
				if(widget instanceof TabItem)
					widgetT=((TabItem)widget).getParent();		
				if(widget instanceof TableColumn)
					widgetT=((TableColumn)widget).getParent();		
				if(widget instanceof TableItem)
					widgetT=((TableItem)widget).getParent();		
				if(widget instanceof ToolItem)
					widgetT=((ToolItem)widget).getParent();											
				if(widget instanceof TreeItem)
					widgetT=((TreeItem)widget).getParent();							
				if(widget instanceof DragSource)
					widgetT=((DragSource)widget).getControl().getParent();
				if(widget instanceof DropTarget)
					widgetT=((DropTarget)widget).getControl().getParent();
				if(widget instanceof Tracker)
					Log.debug("requested the parent of a Tracker- UNFINDABLE");	
			}
	   });
	   return widgetT;	
   }
/*
   public Widget[] getWidgetChildren(Widget widget, boolean recurse){
	   LinkedList children = new LinkedList();	
	   if(widget instanceof Control){
		   if(((Control)widget).getMenu()!=null)
			   children.add(((Control)widget).getMenu());
	   }
	   if(widget instanceof Decorations){
		   if(((Decorations)widget).getMenuBar()!=null)
			   children.add(((Decorations)widget).getMenu());
	   }
	   if(widget instanceof Composite){
		   Widget[] widgets = ((Composite)widget).getChildren();
		   if(widgets.length!=0)
			   children.addAll(Arrays.asList(widgets));
	   }
	   if(widget instanceof CoolBar){
		   Widget[] widgets = ((CoolBar)widget).getItems();
		   if(widgets.length!=0)
			   children.addAll(Arrays.asList(widgets));
	   }
	   if(widget instanceof TabFolder){
		   Widget[] widgets = ((TabFolder)widget).getItems();
		   if(widgets.length!=0)
			   children.addAll(Arrays.asList(widgets));
	   }		
	   if(widget instanceof Table){
		   Widget[] widgets = ((Table)widget).getItems();
		   if(widgets.length!=0)
			   children.addAll(Arrays.asList(widgets));
		   widgets = ((Table)widget).getColumns();
		   if(widgets.length!=0)
			   children.addAll(Arrays.asList(widgets));			
	   }
	   if(widget instanceof ToolBar){
		   Widget[] widgets = ((ToolBar)widget).getItems();
		   if(widgets.length!=0)
			   children.addAll(Arrays.asList(widgets));
	   }
	   if(widget instanceof Tree){
		   Widget[] widgets = ((Tree)widget).getItems();
		   if(widgets.length!=0)
			   children.addAll(Arrays.asList(widgets));
	   }
		
	   if(recurse){
		   LinkedList extendedFamily = new LinkedList();
		   Iterator iter = children.iterator();
		   Widget w;
		   while(iter.hasNext()){
			   w = (Widget)iter.next();	
			   extendedFamily.addAll(Arrays.asList(getWidgetChildren(w,recurse)));
		   }
		   children.addAll(extendedFamily);		
	   }

		return (Widget[])(children.toArray(new Widget[children.size()]));
   }
*/
   boolean disposed;
   public synchronized boolean isDisposed(final Widget w){
   		w.getDisplay().syncExec(new Runnable(){
   			public void run(){
   				disposed = w.isDisposed();
   			}
   		});
   		return disposed;   	
   }
   
   public synchronized Widget[] getWidgetChildren(final Widget widget, final boolean recurse){
	   //LinkedList children = new LinkedList();	
	   objT = new LinkedList();	
	   if(widget==null || isDisposed(widget) )
	   		return new Widget[0];
		Robot.syncExec(widget.getDisplay(),this,new Runnable(){
			public void run(){
				if(widget instanceof Control){
					if(((Control)widget).getMenu()!=null)
						((LinkedList)objT).add(((Control)widget).getMenu());
				}
				if(widget instanceof Scrollable){
					if(((Scrollable)widget).getVerticalBar()!=null)
						((LinkedList)objT).add(((Scrollable)widget).getVerticalBar());
					if(((Scrollable)widget).getHorizontalBar()!=null)
						((LinkedList)objT).add(((Scrollable)widget).getHorizontalBar());					
				}
				if(widget instanceof Decorations){
					if(((Decorations)widget).getMenuBar()!=null)
						((LinkedList)objT).add(((Decorations)widget).getMenuBar());
				}
				if(widget instanceof Composite){
					Widget[] widgets = ((Composite)widget).getChildren();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));
				}
				if(widget instanceof CoolBar){
					Widget[] widgets = ((CoolBar)widget).getItems();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));
				}
				if(widget instanceof TabFolder){
					Widget[] widgets = ((TabFolder)widget).getItems();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));
				}		
				if(widget instanceof Table){
					Widget[] widgets = ((Table)widget).getItems();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));
					widgets = ((Table)widget).getColumns();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));			
				}
				if(widget instanceof ToolBar){
					Widget[] widgets = ((ToolBar)widget).getItems();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));
				}
				if(widget instanceof Tree){
					Widget[] widgets = ((Tree)widget).getItems();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));
				}
				// BEGIN added hmceuen 20040526
				if(widget instanceof TreeItem){
					Widget[] widgets = ((TreeItem)widget).getItems();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));
				}
				// END added hmceuen 20040526
				if(widget instanceof Menu){
					Widget[] widgets = ((Menu)widget).getItems();
					if(widgets.length!=0)
						((LinkedList)objT).addAll(Arrays.asList(widgets));							
				}
				if(widget instanceof MenuItem){
					Widget childMenu = ((MenuItem)widget).getMenu();
					if(childMenu!=null)
						((LinkedList)objT).add(childMenu);
				}
				
			}
	   });
	   LinkedList children = (LinkedList)objT;
		if(recurse&&children.size()>0){
			LinkedList extendedFamily = new LinkedList();
			Widget w;
			for(int i=0; i<children.size();i++){
				w = (Widget)children.get(i);
				extendedFamily.addAll(Arrays.asList(getWidgetChildren(w,recurse)));
			}
			
			children.addAll(extendedFamily);		
		}		   
		return (Widget[])(children.toArray(new Widget[children.size()]));
   }

   /** 
	* Return the title of the nearest ancestor Decorations with a title.  
	* If no title is found, return null.
	*/
   public synchronized String getWidgetDecorationsTitle(Widget widget) {
	   Decorations dec = getWidgetDecorations(widget);
		objT = dec;
		stringT = null;		
	   if(dec==null)
		   return null;
			Robot.syncExec(dec.getDisplay(),this,new Runnable(){
			public void run(){
				if(	(((Decorations)objT).getStyle()&SWT.TITLE)==SWT.TITLE
					&& !((Decorations)objT).getText().equals(""))
					stringT =  ((Decorations)objT).getText();				
			}
		});		
		if(stringT!=null)
			return stringT;
	   Widget parent = getWidgetParent(dec);
	   if(parent!=null)
		   return getWidgetDecorationsTitle(parent);
			
	   return null;
   }
	

   /** Return the component's owning shell.   There will
	* <b>always</b> one of these.
	*/
   public Shell getWidgetShell(Widget widget) {
	   Widget parent = widget;
	   while (!(parent instanceof Shell) 
			  && (widget = getWidgetParent(parent)) != null) {
		   parent = widget;
	   }
	   return (Shell)widget;
   }

   /** Return the nearest Decorations ancestor of the given Widget.
	*/
   public Decorations getWidgetDecorations(Widget widget) {
	   Widget parent = widget;
	   while (!(parent instanceof Decorations) 
			  && (widget = getWidgetParent(parent)) != null) {
		   parent = widget;
	   }
	   return (Decorations)widget;
   }

   /** Dispose of all available windows, and does not return until they have
	   been disposed of.  */
   public void disposeWindows() {
	   Log.debug("Disposing all windows");
	   Decorations[] windows = getDecorations();
	   for (int i=0;i < windows.length;i++) {
		   final Decorations win = windows[i];
		   discardWidget(win);
		   System.setProperty("abbot.finder.disposal", "true");
		   try {
			   Log.debug("Disposing of " + win);
			   win.dispose();
		   }
		   catch(NullPointerException npe) {
			   Log.log(npe);
		   }
		   System.setProperty("abbot.finder.disposal", "false");
	   }
   }
	
   /** Discard and no longer reference the given component. */
   public void discardWidget(Widget comp) {
	   Log.debug("Discarding " + comp);
	   filterWidget(comp);
	   if (parent != null) {
		   parent.discardWidget(comp);
	   }
   }

   public void filterWidget(Widget comp) {
	   Log.debug("Now filtering " + comp);
	   filteredWidgets.put(comp, comp);
		
   }	
	
	public synchronized boolean isDecorationsShowing(final Decorations w) {
		Robot.syncExec(w.getDisplay(),this,new Runnable(){
			public void run(){
				boolT = w.isVisible();
			}
		});
		return boolT;
	}

	/** Return the component's name, ensuring that null is returned if the
	 * name appears to be auto-generated.
	 */
	public synchronized String getWidgetName(final Widget widget) {
		Robot.syncExec(widget.getDisplay(),this,new Runnable(){
			public void run(){
				stringT = (String)widget.getData("name");		
			}
		});
		return stringT;
//		synchronized(widget.getDisplay()){
//			widget.getDisplay().syncExec(new Runnable(){
//				public void run(){
//					stringT = (String)widget.getData("name");		
//				}
//			});
//			return stringT;					
//		}	
	}
	
	/** 
	 * Stringify the widget.
	 */
	public synchronized String widgetToString(final Widget widget) {
		Robot.syncExec(widget.getDisplay(),this,new Runnable(){
			public void run(){
				stringT = (String)(widget.toString());		
			}
		});
		return stringT;
	}
	
	/**
	 * Return an array of all available root Shells
	 */ 
	// WARNING: THERE MAY BE SOME UNRESOLVED SYNCHRONIZATION ISSUES HERE...
	static Shell[] shells0;
	public static Display[] displays;
	static int i;
	static boolean flag;
	static Shell[] shells;
	public synchronized Shell[] getRootShells(){
		List allShells = getAllShells();
		final Shell[] shells = (Shell[])(allShells.toArray(new Shell[allShells.size()]));
		ArrayList rootShells = new ArrayList();
		//System.out.println("Length(start)="+shells.length);
		for(i=0; i<shells.length;i++){
			//System.out.println("\tShell("+i+")"+shells[i].getText());		
			
			flag = false;
			if(shells[i].getDisplay().getThread()==Thread.currentThread())
				flag = (shells[i].getParent()==null);
			else{
				Robot.syncExec(shells[i].getDisplay(),null,new Runnable(){
					public void run(){
						if(shells[i].getParent()==null)
							flag = true;
					}
				});
			}
			if(flag)
				rootShells.add(shells[i]);
		}
		//System.out.println("end");
		return (Shell[])(rootShells.toArray(new Shell[rootShells.size()]));
	}

	/**
	 * get all shells for all displays
	 */
	public static synchronized List getAllShells(){
		ArrayList allShells = new ArrayList();
		displays = DecorationsTracker.getDisplays();
		for(i=0; i<displays.length;i++){
			if(!displays[i].isDisposed() &&displays[i]!=null){
				if(displays[i].getThread()==Thread.currentThread())
					shells0 = displays[i].getShells();
				else{
					try{
						Thread.sleep(0,10);
					}catch(Exception e){
					}
					
					Robot.syncExec(displays[i],null,new Runnable(){
						public void run(){
							shells0 = displays[i].getShells();
							//System.out.println("Executed inside 1st sync block");
						}
					});
				}			
			}
			if(shells0!=null)
				allShells.addAll(Arrays.asList(shells0));
		}		
		return allShells;
	}
	
//	/** Return all decorations owned by a given decorations object. */
//	private List getDecorationsList(Decorations parent) {
//		return getDecorationsList(parent, filter, false);
//	}

	/** Return all windows owned by the given window, optionally filtered. */
	private List getDecorationsList(Decorations parent, boolean filter, boolean recurse){
		Widget[] children = getWidgetChildren(parent,recurse);
			
		ArrayList list = new ArrayList();
		for (int i=0;i < children.length;i++) {
			if(children[i] instanceof Decorations){
				if (!filter || !isWidgetFiltered(children[i])) {
					list.add(children[i]);
					if (recurse) {
						list.addAll(getDecorationsList((Decorations)children[i], filter, recurse));
					}
				}
			}
		}
		return list;
	}


	/** Return all windows owned by the given window that have not been
	 * filtered.
	 */ 
	public Decorations[] getDecorations(Decorations parent) {
		List list = getDecorationsList(parent, filter, false);
		
		return (Decorations[])list.toArray(new Decorations[list.size()]);
	}

	private List getDecorationsList() {
		Shell[] rootShells = getRootShells();
		
		ArrayList list = new ArrayList();
		for(int i=0; i<rootShells.length;i++){
			if(!filter || !isWidgetFiltered(rootShells[i])){
				list.add(rootShells[i]);
				list.addAll(getDecorationsList(rootShells[i],filter,true));
			}			
		}
		return list;
	}

	/** Returns the set of all available decorations that have not been
	 * filtered.  This includes shells and decorations.
	 */ 
	public Decorations[] getDecorations() {
		List list = getDecorationsList();
		return (Decorations[])list.toArray(new Decorations[list.size()]);
	}

	/** Returns all components below the GUI hierarchy of the given Control,
	 * including Windows and MenuElements.
	 */ 
	public Widget[] getWidgets(Widget widget){
		return getWidgetChildren(widget,true);
	}
	//String widgetText = null;
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

	/** Copy/mod of Tom MacDougall: get widgets of the given class */
	public List getMatchesForClass(Class widgetClass) {
		List ret = null;
		WidgetReference ref =
			new WidgetReference(null, widgetClass, null, null, null, null); 
		HashSet comps = findMatches(null, ref, MATCH_WEAK);
		if (comps == null) {
			return null;
		} else if (comps.isEmpty()) {
			// TODO_Tom: use tracing
			System.err.println("Widget '" + ref + "' not found ("
				+ widgetClass.getName() +") ");
			return Collections.EMPTY_LIST;
		} else {
			ret = new ArrayList(comps.size());
			for (Iterator iterator = comps.iterator(); iterator.hasNext();) { 
				ret.add((Widget)(iterator.next())); 
			}
		}
		return ret;
	} 

	/** Copy/mod of Tom MacDougall: print widgets of the given class */
	public String printMatchesForClass(Class widgetClass) {
		List matches = getMatchesForClass(widgetClass);
		StringBuffer sb = new StringBuffer();
		if ((matches == null) || (matches.isEmpty())) {
			sb.append("Widget not found for class \"").append(widgetClass.getName()).
				append("\"\n"); 
		} else {
			sb.append("Found:\n");			 
			for (Iterator iterator = matches.iterator(); iterator.hasNext();) { 
				Widget element = (Widget)(iterator.next()); 
				sb.append("\t").append(element.toString()).append("\n");			 
			}
		}
		return sb.toString();
	}

	/**
	 * @param text either on or labelling the <code>Button</code>
	 * @param title of the frame containing the <code>Button</code>
	 * @param shell containing the <code>Button</code>
	 */
	public static Button findButtonByTextTitleShell(
		String text, String title, Shell shell) throws
		WidgetNotFoundException, MultipleWidgetsFoundException {
		Button ret = null;
		ButtonReference ref = new ButtonReference(null, null, null, title, text);
		if (ref != null) return DefaultWidgetFinder.findButtonInShell(ref, shell);
		return ret;
	}

}
