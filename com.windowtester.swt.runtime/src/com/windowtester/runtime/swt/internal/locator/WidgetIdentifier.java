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
package com.windowtester.runtime.swt.internal.locator;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Hyperlink;

import abbot.finder.swt.Matcher;
import abbot.finder.swt.SWTHierarchy;
import abbot.tester.swt.ButtonTester;
import abbot.tester.swt.CComboTester;
import abbot.tester.swt.ComboTester;
import abbot.tester.swt.MenuItemTester;
import abbot.tester.swt.WidgetTester;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.swt.internal.abbot.matcher.TreeItemByPathMatcher;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.finder.FilteredTreeHelper;
import com.windowtester.runtime.swt.internal.finder.IWidgetIdentifierStrategy;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.finder.WidgetLocatorService;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewHandle;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.internal.finder.legacy.SearchScopeHelper;
import com.windowtester.runtime.swt.internal.finder.legacy.WidgetFinder;
import com.windowtester.runtime.swt.internal.finder.matchers.AdapterFactory;
import com.windowtester.runtime.swt.internal.locator.forms.FormTextReference;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkControlReference;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkLocatorScopeFactory;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkReference;
import com.windowtester.runtime.swt.internal.locator.jface.DialogFinder;
import com.windowtester.runtime.swt.internal.matchers.MenuItemByPathMatcher;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CComboItemLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.ListItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.ShellLocator;
import com.windowtester.runtime.swt.locator.StyledTextLocator;
import com.windowtester.runtime.swt.locator.TabItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;
import com.windowtester.runtime.swt.locator.jface.DialogMessageLocator;

/**
 * A V2 API WidgetLocator Factory.
 */
public class WidgetIdentifier implements IWidgetIdentifierStrategy {
	
	
	/**
	 * A service that maps widgets to corresponding API V2 locators.
	 * For example, Button -> ButtonLocator.
	 */
	class LocatorMapper {
		
		//helpers for extracting path info
		private  final MenuItemTester _menuItemTester = new MenuItemTester();
		
		/**
		 * Map this widget to a corresponding locator (note: this is a factory method
		 * --- one will be created).
		 */
		WidgetLocator map(Widget w) {
			String text = getText(w);
			if (isA(w, Button.class)) 
				return new ButtonLocator(text);
			if (isA(w, MenuItem.class))
				return new MenuItemLocator(_menuItemTester.getPathString((MenuItem)w));
			if (isA(w, TreeItem.class)) {
				TreeItem item = (TreeItem)w;
				String path = TreeItemByPathMatcher.extractPathString(item);
				if (isInFilteredTree(item))
					return new FilteredTreeItemLocator(path);
				return new TreeItemLocator(path);
			}
			if (isA(w, CTabItem.class))
				return new CTabItemLocator(text);
			if (isA(w, TabItem.class))
				return new TabItemLocator(text);
			if (isA(w, Combo.class))
				return new ComboItemLocator(text);
			if (isA(w, CCombo.class))
				return new CComboItemLocator(text);
			if (isA(w, TableItem.class))
				return new TableItemLocator(text);
			if (isA(w, List.class))
				return new ListItemLocator(text);
			if (isA(w, Hyperlink.class))
				return new HyperlinkLocator(text);
			if (isA(w, Shell.class))
				return new ShellLocator(text);
			if (isA(w, StyledText.class))
				return new StyledTextLocator();
			
			//fall through
			return new SWTWidgetLocator(w.getClass(), text);
		}

		private boolean isInFilteredTree(TreeItem item) {
			return FilteredTreeHelper.containedInFilteredTree(item);
		}

		private boolean isA(Widget w, Class targetClass) {
			return w.getClass().equals(targetClass);
		}
		
		private String getText(Widget w) {
			if (w instanceof CCombo)
				return new CComboTester().getText((CCombo)w);
			if (w instanceof Combo)
				return new ComboTester().getText((Combo)w);
			if (w instanceof Hyperlink)
				return HyperlinkControlReference.forControl((Hyperlink)w).getText();
			//default case:
			return WidgetLocatorService.getWidgetText(w);
		}
		
	}

	//vet and break out into new class
	static class ContributedWidgetLocatorRegistry {

		static ContributedWidgetLocatorRegistry _instance;
		
		public static ContributedWidgetLocatorRegistry getInstance() {
			if (_instance == null)
				_instance = new ContributedWidgetLocatorRegistry();
			return _instance;
		}

		public WidgetLocator findProposal(Widget w, Event event) {
			
			/*
			 * This will be broken into separate classes (or pushed into
			 * the locators themselves)
			 * for now the logic is local
			 */

			String name = UIProxy.getData(w, "name");
			// added class to NamedWidgetLocator : 5/2/07 :kp
			
			if (name != null) {
			
				NamedWidgetLocator namedLocator = new NamedWidgetLocator(w.getClass(),name);
				
				WidgetLocator locator = WidgetIdentifier.getInstance().getMapper().map(w);
				if (locator instanceof VirtualItemLocator) {
					locator.setParentInfo(namedLocator);
					return locator;
				}
				return namedLocator;
			}
			
			if (w instanceof ToolItem) {
				ToolItem item = (ToolItem)w;
				String id = ContributedToolItemLocator.getAssociatedContributionID(item);
				if (id != null)
					return new ContributedToolItemLocator(id);
			}
			
			if (w instanceof Control) {
				Control messageControl = DialogFinder.findActiveDialogMessageControl();
				if (messageControl == w)
					return new DialogMessageLocator();
			}
			
			if (w instanceof FormText && event != null) {
				FormText text = (FormText)w;
				IHyperlinkReference link = FormTextReference.forText(text).findHyperlinkAt(event.x, event.y);
				if (link != null) {	
					HyperlinkLocator locator = HyperlinkReference.toLocator(link);
					return HyperlinkLocatorScopeFactory.addScope(locator, text);
				}
				
			}
			
			
			return null;
		}
		
		
	}
	
	
	LocatorMapper _mapper = new LocatorMapper();

	
	private static WidgetIdentifier _instance = new WidgetIdentifier();
	
	public static WidgetIdentifier getInstance() {
		return _instance;
	}

	/** A list of keys which we want to propagate to locators */
	private static final String[] INTERESTING_KEYS = { "name" };

	/** Limits elaborations to protect against pathologically nested widgets hanging the recorder */
	private static final int MAX_ELABORATIONS = 10;

	/** For use in checking for unique matches */
	private final WidgetFinder _finder = new WidgetFinder();

	/** For use in elaboration (created once per call it identify) */
	private SWTHierarchyHelper _hierarchyHelper;
	private SearchScopeHelper _searchScopeHelper;

	/** for use in adapting locators to matchers */
	private AdapterFactory _adapterFactory;

	/* (non-Javadoc)
	 * @see com.windowtester.swt.locator.IWidgetIdentifierStrategy#identify(org.eclipse.swt.widgets.Widget, org.eclipse.swt.widgets.Event)
	 */
	public IWidgetIdentifier identify(Widget w, Event event) {
		if (w == null)
			return null;
		
		Display display = w.getDisplay();
		//cache the helpers for use in elaboration
		_hierarchyHelper   = new SWTHierarchyHelper(display);
       	_searchScopeHelper = new SearchScopeHelper(new SWTHierarchy(display));
		
		//get top-level scope
		WidgetLocator scope = findTopLevelScope(w);
		//get locator describing the target widget itself
		WidgetLocator locator = getLocator(w, event);
		//post-process special case
		//locator = optionallyHandleVirtualNamedCase(w, locator);
		
		//attach scope
		attachScope(locator, scope);//note: it can be null

		
		/*
		 * And here we make a BIG assumption.
		 * 
		 * If the locator is a ViewLocator we assume that the match is definite.  (In any case,
		 * we don't know how to elaborate so this is actually a BEST GUESS.
		 */
		if (scope instanceof ViewLocator)
			return locator;			

		
		Matcher matcher = adaptToMatcher(locator);
		Shell shellSearchScope = _searchScopeHelper.getShellSearchScope(matcher); 

		
		if (locator instanceof VirtualItemLocator) {
			//look ahead and see if we have a match
			//the idea here is to create the parents (combos, lists) that
			//qualify the virtual items (but only do this if we need to elaborate)
			if (!isUniquelyIdentifying(matcher, shellSearchScope)) {
				//rename me!
				locator.setParentInfo(optionallySynthesizeVirtualParent(w));
			}
		}
		

		
		
		int count = 0;
		
		//elaborate until done (notice: null locator indicates a failure)
		while(!isUniquelyIdentifying(matcher, shellSearchScope) && locator != null) {
//			System.out.println("locator: " + locator + " not uniquely matching, elaborating...");
			locator = elaborate(locator, w);
			if (locator != null)
				matcher = adaptToMatcher(locator);
			if (++count >= MAX_ELABORATIONS) {
				TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, "maximum identifier elaborations (" + MAX_ELABORATIONS + ") exceeded - identification cancelled");
				return null;
			}
//				new DebugHelper.printWidgets();
		}
		
		return locator;
	}
	


	/* (non-Javadoc)
	 * @see com.windowtester.swt.locator.IWidgetIdentifierStrategy#identify(org.eclipse.swt.widgets.Widget)
	 */
	public IWidgetIdentifier identify(Widget w) {
		return identify(w, null);
	}

	/**
	 * Advance until we find an empty parent slot.
	 */
	private void attachScope(WidgetLocator locator, WidgetLocator scope) {
		
		//specially marked locators do not get scoped
		if (locator instanceof IUnscopedLocator)
			return;
		
		//moreover: namedLocators do not get scoped either (for now...)
		if (isNamed(locator))
			return;
		
		//yet ANOTHER TreeItem hack -- tree item implicit scope
		//gets clobbered by  a view scope
		//NOTE: this should be for Tables and Lists too?
		if (locator instanceof TreeItemLocator) {
			WidgetLocator parent = locator.getParentInfo();
			if (parent.getClass() == SWTWidgetLocator.class && scope instanceof ViewLocator) {
				locator.setParentInfo(scope);
				return;
			}
		}
		
		
		/*
		 * the regular case climbs the stack and attaches scope to the top
		 */
		while (locator.getParentInfo() != null) {
			locator = locator.getParentInfo();
		}

		locator.setParentInfo(scope); 
	}

	/**
	 * Test whether this or any ancestor is named.
	 */
	private boolean isNamed(WidgetLocator locator) {
		do {
			if (locator instanceof NamedWidgetLocator)
				return true;
			locator = locator.getParentInfo();
		} while (locator != null);
		return false;
	}



	private Matcher adaptToMatcher(WidgetLocator locator) {
		//menu item special case...
		if (locator instanceof MenuItemLocator) {
			final MenuItemByPathMatcher pathMatcher = new MenuItemByPathMatcher(((MenuItemLocator)locator).getPath());
			return new Matcher(){
				public boolean matches(Widget w) {
					return pathMatcher.matches(SWTWidgetReference.forWidget(w));
				}
			};
		}
		/**
		 * A little fudging here to handle tree items.
		 * 
		 * The rub: tree items have a matcher that matches based on path BUT
		 * we don't want to use this in identification...  In identification we
		 * are looking to match the parent Tree.
		 * 
		 * So... to provision, we pop up to fetch the parent locator (UNLESS its a ViewLocator!)
		 */
		if (locator instanceof TreeItemLocator) {
			if (!(locator.getParentInfo() instanceof ViewLocator)) //tree items scoped with view locators are sufficient
				locator = locator.getParentInfo();
		}
		
		return getAdapterFactory().adapt(locator);
	}


	private AdapterFactory getAdapterFactory() {
		if (_adapterFactory == null)
			_adapterFactory = new AdapterFactory();
		return _adapterFactory;
	}


	/**
	 * Find top-level scope (Shell | View) -- might be <code>null</code>.
	 */
	private WidgetLocator findTopLevelScope(Widget w) {
		
		
		if (!PlatformUI.isWorkbenchRunning())
			return null; //bail out if the platform is not running
		
		//as a final sanity check, wrap this, in case we get a failure:
		try {
			// 1 check for view scope
			IViewHandle handle = ViewFinder.find(w);
			if (handle != null)
				return new ViewLocator(handle.getId());
		} catch (IllegalStateException e) {
			LogHandler.log(e);
		} // TODO: remove this wrapper post 2.0
			
		
		//2 check for shell scope

		//TODO: when to use Shell Scope?
		
//		IShellHandle shellHandle = ShellFinder.find(w);
//		if (shellHandle != null && shellHandle.isModal())
//			return new ShellLocator(shellHandle.getTitle(), shellHandle.isModal());
		
		//handle other cases here...
		return null;
	}
	
	/**
	 * Takes a WidgetLocator object and elaborates on it until is uniquely identifying.
	 * If no uniquely identifying locator can be inferred, a <code>null</code> value 
	 * is returned.
	 * 
	 */
	private WidgetLocator elaborate(WidgetLocator info, Widget w) {
		
		//a pointer to the original locator for returning (in the success case)
		WidgetLocator root = info;
		
		//a bit of a hack: virtual item locators get located by their parent info
		if (info instanceof VirtualItemLocator)
			info = info.getParentInfo();
		
		TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, "elaborating on: " + info + " widget=" + w);

		boolean elaborated = false;
		WidgetLocator parentInfo = null;
		
		while(!elaborated) {

			//get parent info of the current (top-most) locator 
			parentInfo = info.getParentInfo();
			//get the parent of the current (top-most) widget in the target's hierarchy
			Widget parent = _hierarchyHelper.getParent(w);
			/*
			 * if the parent is null at this point, we've failed to elaborate and we
			 * need to just return
			 */
			if (parent == null) {
				TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, UIProxy.getToString(w) + " has null parent, aborting elaboration");
				return null;
			}
			
			//if the parent is a scope locator, connect to it
			if (isScopeLocator(parentInfo)) {
				handleScopeLocatorCase(info, parentInfo, w, parent);
				elaborated = true;
			//if the parent is null, create a new parent and attach it	
			} else if (parentInfo == null) {
				info.setParentInfo(getLocator(parent));
				setIndex(info, w, parent);
				elaborated = true;
			}
			
			/*
			 * setup for next iteration
			 */
			w    = parent;
			info = parentInfo;
		} 
		
		return root;
	}

	private WidgetLocator optionallySynthesizeVirtualParent(Widget w) {
		if (w instanceof Combo || w instanceof CCombo || w instanceof List)
			return new SWTWidgetLocator(w.getClass());
		return null;
		
	}


	LocatorMapper getMapper() {
		return _mapper;
	}
	

	/**
	 * Check to see if the given locator is a scope locator.
	 */
	private boolean isScopeLocator(WidgetLocator locator) {
		//TODO: ideally this will be an interface: IScopeLocator?
		return locator instanceof ViewLocator || locator instanceof ShellLocator;
	}

	/**
	 * Handle case where parent locator is a scoping locator.
	 */
	private void handleScopeLocatorCase(WidgetLocator currentTopLocator, WidgetLocator scopeLocator, Widget currentWidget, Widget widgetParent) {

			//1. create a new parent
			WidgetLocator newParent = getLocator(widgetParent);
			//attach it to our old top locator
			currentTopLocator.setParentInfo(newParent);
			setIndex(currentTopLocator, currentWidget, widgetParent);
			
			int scopeRelativeIndex = _hierarchyHelper.getIndex(currentWidget, scopeLocator);
			if (scopeRelativeIndex != WidgetLocator.UNASSIGNED)
				newParent.setIndex(scopeRelativeIndex);

			newParent.setParentInfo(scopeLocator);	
	}

	/**
	 * Set the index for this locator that describes the given widget relative to the given parent.
	 */
	private void setIndex(WidgetLocator locator, Widget currentWidget, Widget widgetParent) {
		int index = _hierarchyHelper.getIndex(currentWidget,widgetParent);
		if (index != WidgetLocator.UNASSIGNED)
			locator.setIndex(index);
	}
	

	/**
	 * Does this matcher uniquely identify a widget in this Shell?
	 */
	private boolean isUniquelyIdentifying(Matcher matcher, Shell shellSearchScope) {
		return _finder.find(shellSearchScope, matcher, 0 /* no retries */).getType() == WidgetFinder.MATCH;
	}

	
	/**
	 * Create an (unelaborated) info object for this widget. 
	 * @param w - the widget to describe.
	 * @return an info object that describes the widget.
	 */
	private WidgetLocator getLocator(Widget w, Event event) {
		if (w == null) {
			return null;
		}
		
		/**
		 * CCombos require special treatment as the chevron is a button and receives the click event.
		 * Instead of that button, we want to be identifying the combo itself (the button's parent).
		 * 
		 * Actually: we want to ignore the button selections altogether since they are followed by a selection
		 * which is the "real" event.  To do this, we simply return null.
		 */
		if (w instanceof Button) {
			Widget parent = new ButtonTester().getParent((Button) w);
			if (parent instanceof CCombo)
				return new NoOpLocator();
		}

		//first check for a contributed locator
		WidgetLocator locator = checkForContributedLocator(w, event);
		
		//failing that, check for a labeled case
		if (locator == null)
			locator = checkForLabeledLocatorCase(w);
		
		/**
		 * Another tree item special case.  If the tree item is labeled, the labeled locator needs to be properly
		 * connected
		 */
		if (locator != null && w instanceof TreeItem) {
			//create item locator
			WidgetLocator itemLocator = _mapper.map(w);
			//connect parent label
			itemLocator.setParentInfo(locator);
			locator = itemLocator;
		}
		
		//check for item in named Tree parent case
		if (locator == null)
			locator = checkForNamedTreeCase(w);
		
		//if not labeled case or item in named tree case, use the mapper
		if (locator == null)
			locator = _mapper.map(w);
		
		setDataValues(locator, w);
		return locator;
	}

	
	/**
	 * Create an (unelaborated) info object for this widget. 
	 * @param w - the widget to describe.
	 * @return an info object that describes the widget.
	 */
	private WidgetLocator getLocator(Widget w) {
		return getLocator(w, null);

	}

	private WidgetLocator checkForNamedTreeCase(Widget w) {
		if (!(w instanceof TreeItem))
			return null;

		Widget parent = _hierarchyHelper.getParent(w);
		if (!(parent instanceof Tree))
			return null; // sanity
		Tree tree = (Tree) parent;
		WidgetLocator parentLocator = getLocator(tree);
		if (parentLocator instanceof NamedWidgetLocator) {
			//get the tree item locator
			WidgetLocator childLocator = _mapper.map(w);
			if (!(childLocator instanceof TreeItemLocator))
				return null;
			TreeItemLocator treeItemLocator = (TreeItemLocator)childLocator;
			treeItemLocator.setParentInfo(parentLocator);
			return treeItemLocator;
		}
		return null;
	}



	//see if a contributed locator matches this widget
	private WidgetLocator checkForContributedLocator(Widget w, Event event) {
		return ContributedWidgetLocatorRegistry.getInstance().findProposal(w, event);
	}



	private WidgetLocator checkForLabeledLocatorCase(Widget w) {
		
		//treeitems are actually matched on their trees, so update the widget accordingly
		if (w instanceof TreeItem)
			w = _hierarchyHelper.getParent(w);
		
		Widget parent = _hierarchyHelper.getParent(w);
		if (!(parent instanceof Composite))
			return null;
		
		//labels are not themselves considered labelable; 
		if (w instanceof Label)
			return null;
		
		//for NOW, lists are not considered labeled -- but they should be in the future!
		if (w instanceof List)
			return null;
		
		//Labeled buttons don't generally make sense; 
		if (w instanceof Button)
			return null;
		
		//labeled hyperlinks do not make sense
		if (w instanceof Hyperlink)
			return null;
		
		final Composite comp = (Composite)parent;
		final Class cls = w.getClass();//our target class
		
		final String labelText[] = new String[1];
		final boolean found[] = new boolean[1];
		final Widget[] widget = new Widget[]{w};
		
		/*
		 * Iterate over children looking for a Label widget.
		 * If we find one, if the next widget of the class of our target widget
		 * is the target widget, we have a labeled locator case.
		 */
		w.getDisplay().syncExec(new Runnable() {
			public void run() {

				Control[] children = comp.getChildren();
				Control child;
				for (int i = 0; i < children.length; i++) {
					child = children[i];
					//look for next widget of target class
					if (labelText[0] != null) {
						if (child.getClass().equals(cls)) {
							found[0] = child == widget[0];
							/*
							 * only kick out if we've found it 
							 * (there may be multiple label text pairs in a composite) 
							 */
							if (found[0])
								return;
						}
					}
					//set up for next iteration
					if (child instanceof Label)
						labelText[0] = ((Label)child).getText();
				}
			}
		});
		
		if (found[0]) {
			//here we guard against the case where the label is empty -- this is not a legitimate case for a labeled locator			
			if (labelText[0] == null || labelText[0].trim().length() == 0)
				return null;
			LabeledLocator locator = new LabeledLocator(cls, labelText[0]);
			//combos and ccombos are more elaborate cases (embed label in item locator)
			if (cls == Combo.class)
				return new ComboItemLocator(new ComboTester().getText((Combo)w), locator);
			if (cls == CCombo.class)
				return new CComboItemLocator(new CComboTester().getText((CCombo)w), locator);
			//Labeled Text gets special treatment since it's so common
			if (cls == Text.class)
				return new LabeledTextLocator(labelText[0]);
			
			return locator;
		
		}
		return null;		
	}

	
	
	/**
	 * Propagate values of interest from the widget to the locator
	 */
	private void setDataValues(WidgetLocator locator, Widget w) {
		String key;
		Object value;
		WidgetTester tester = new WidgetTester();
		for (int i = 0; i < INTERESTING_KEYS.length; ++i) {
			key = INTERESTING_KEYS[i];
			value = tester.getData(w, key);
			if (value != null)
				locator.setData(key, value.toString());
		}
	}

}
