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
package com.windowtester.runtime.swt.internal.finder.legacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.finder.swt.Hierarchy;
import abbot.finder.swt.Matcher;
import abbot.finder.swt.MultiMatcher;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.SWTHierarchy;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.settings.TestSettings;

/**
 * Provides basic widget lookup, examining each widget in turn.
 * Searches all widgets of interest in a given hierarchy.  Unlike Abbot <code>abbot.finder.swt.BasicFinder</code>,
 * it does not throw exceptions on match failures.  Instead, <code>WidgetFinder</code>
 * returns a <code>MatchResult</code>.
 * <p>
 * ANOTHER! copy of BasicFinder.  This one differs in the following ways:
 * <br>
 * (1) It does not throw exceptions in case widgets are not found -- returning a 
 *     <code>MatchResult</code> object instead. 
 * 
 * 
 * @deprecated
 *
 */
public class WidgetFinder {
	
	//match type constants
	public static final int MATCH                  = 0;
	public static final int WIDGET_NOT_FOUND       = 1;
	public static final int MULTIPLE_WIDGETS_FOUND = 3;
	
	//search constants
	public static final int DFS = 1; // depth-first search
	public static final int BFS = 2; // breadth-first search
	
	//number of times to retry a widget find
	//public static final int MAX_FIND_RETRIES = 10;
	//how long to wait between retries (in ms)
	//public static final int RETRY_INTERVAL = 500;
	
	
	//the search type (DFS|BFS)
	private int _searchType = DFS;   //default

	//debug variable to test how many comparisons are made
	private int _dbComparisons; 

	/**
	 * A hierarchy rooted by a given widget.
	 */
	private class SingleWidgetHierarchy implements Hierarchy {

		private final ArrayList _list = new ArrayList();

		private final Hierarchy _hierarchy; 
		
		public SingleWidgetHierarchy(Widget root) {
			_hierarchy = new SWTHierarchy(root.getDisplay());
			_list.add(root);
		}

		public Collection getRoots() {
			return _list;
		}

		public Collection getWidgets(Widget c) {
			return _hierarchy.getWidgets(c);
		}

		public Widget getParent(Widget c) {
			return _hierarchy.getParent(c);
		}

		public boolean contains(Widget c) {
			return _hierarchy.contains(c);
		}

		public void dispose(Decorations w) {
			_hierarchy.dispose(w);
		}
	}

	/**
	 * Encapsulates match test results.
	 */
	public static class MatchResult {
	
		/** the matched widget */
		private Widget _widget;
		
		/** matched widgets (multiple match case) */
		private Collection _widgets;
		
		/** the type of result (match by default) */
		private int _type = MATCH;
		
		/**
		 * Create a match result of the given type.
		 * @param type
		 */
		public MatchResult(int type) {
			_type = type;
		}

		/**
		 * Create a match result for the given widget.
		 * @param w
		 */
		public MatchResult(Widget w) {
			_widget = w;
		}
		
		
		/**
		 * Create a widget not found match result.
		 */
		static MatchResult notFound() {
			return new MatchResult(WIDGET_NOT_FOUND);
		}
		
		/**
		 * Create a multiple widgets found match result.
		 * @param found 
		 */
		static MatchResult multipleFound(Collection found) {
			MatchResult result =  new MatchResult(MULTIPLE_WIDGETS_FOUND);
			result.setFound(found);
			return result;
		}

		/**
		 * Create a found match result.
		 */
		static MatchResult match(Widget w) {
			return new MatchResult(w);
		}
		
		/**
		 * Get the found widget.
		 * @return the found widget or <code>null</code> if none was found.
		 */
		public Widget getWidget() {
			return _widget;
		}
		
		/**
		 * Get the found widgets.  (Note this will only be set in the case
		 * where multiple widgets are found.)  
		 * TODO: this is to be refactored post 2.0
		 * @return the found widgets in the multiple match case
		 */
		public Collection getWidgets() {
			return _widgets;
		}
		
		public void setFound(Collection found) {
			_widgets = new ArrayList();
			_widgets.addAll(found);    //note: defensive copy
		}
		
		
		public int getType() {
			return _type;
		}

		
	}
	
	
	

	/** Find a Widget, using the given Matcher to determine whether a given
	 widget in the hierarchy under the given root is the desired
	 one.
	 */
	public MatchResult find(Widget root, Matcher m) {
		
		assertNotNull(root, "root must not be null");
		assertNotNull(m, "matcher must not be null");
		
		_dbComparisons = 0;
		
		return find(new SingleWidgetHierarchy(root), m);
	}

	
	public MatchResult find(Widget root, Matcher m, int tries) {
		assertNotNull(root, new RootWidgetIsNullError());
		assertNotNull(m, "matcher must not be null");
		
		_dbComparisons = 0;
		
		return find(new SingleWidgetHierarchy(root), m, tries);
	}
	



	private MatchResult find(Hierarchy h, Matcher m, int maxTries) {
		
		MatchResult result = find0(h, m);
	
		//if it wasn't a match retry
		int tries = 0;
		while (result.getType() != MATCH && tries++ < maxTries) {
			TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "WidgetFinder failed to find widget (" + m + ") retrying [" + tries + "/" + maxTries +"]");
//			try {
//				throw new RuntimeException();
//			} catch(Throwable t) {
//				t.printStackTrace();
//			}
			pause(getFinderRetryInterval());
			result = find0(h, m);
		}
		return result;
	}


	/** Find a Widget, using the given Matcher to determine whether a given
	 widget in the hierarchy in the given display is the desired
	 one.
	 */
	public MatchResult find(Display display, Matcher m) {
		return find(display, m, getMaxFinderRetries());
	}
	
	public MatchResult find(Display display, Matcher m, int maxRetries) {

		assertNotNull(display, "display must not be null");
		assertNotNull(m, "matcher must not be null");
		
		_dbComparisons = 0;
		
		return find(new SWTHierarchy(display), m, maxRetries);
		
	}
	
	public MatchResult find(Hierarchy h, Matcher m) {	
		return find(h, m, getMaxFinderRetries());
	}

	
	
	protected MatchResult find0(final Hierarchy h, final Matcher m) {
		
		final Set found = new HashSet();
		/* The list is used in parallel with the set for multimatchers who need 
		 * to know in what order elements were added to the list */
		final java.util.List foundList = new ArrayList();
		final Collection roots = h.getRoots();
		if (Platform.isOSX()) { // Mac testing -- this is a hack
			Display display = null;
			if (roots.size() > 0) {
				Object x = roots.iterator().next();
				if (x instanceof Widget) {
					Widget w = (Widget) x;
					if (!w.isDisposed())
						display = w.getDisplay();
				}
			}
			// this will work even if display is null as long as MenuWatcher is a singleton
			//roots.addAll(MenuWatcher.getInstance(display).getOpenMenus());
		}
		if (_searchType == DFS) {
			DisplayExec.sync(new Runnable(){
				public void run() {
					Iterator iter = roots.iterator();
					while (iter.hasNext()) {
						findMatches(h, m, (Widget) iter.next(), found, foundList);
					}					
				}
			});
		} else if (_searchType == BFS) {
			LinkedList searchQ = new LinkedList(roots);
			while (searchQ.size() > 0) {
				Widget current = (Widget) searchQ.removeFirst();
				_dbComparisons++;
				if (m.matches(current)) {
					if (!(m instanceof MultiMatcher)) {
						return MatchResult.match(current);
					}
					found.add(current);
					if (m instanceof MultiMatcher) {
						foundList.add(current);
					}
				}
				searchQ.addAll(h.getWidgets(current));
			}
		}

		if (found.size() == 0) {
			return MatchResult.notFound();
		} else if (found.size() > 1) {
			Widget[] list = (Widget[]) foundList.toArray(new Widget[foundList
					.size()]);
			if (!(m instanceof MultiMatcher)) {
				return MatchResult.multipleFound(found);
			}
			try {
				MatchResult.match(((MultiMatcher) m).bestMatch(list));
			} catch (MultipleWidgetsFoundException e) {
				return MatchResult.multipleFound(found);
			}
		}
		return MatchResult.match((Widget) found.iterator().next());
	}

	
	

	protected void findMatches(Hierarchy h, Matcher m, Widget w, Set found,
			java.util.List foundList) {
		// Matcher should never return multiple or throw MultipleWidgetsFoundException
		//	!pq: actually DO want this behavior!
		//	        if (found.size() == 1 && !(m instanceof MultiMatcher))
		//	            return;
		if (_searchType == DFS) {
			Iterator iter = h.getWidgets(w).iterator();
			while (iter.hasNext()) {
				findMatches(h, m, (Widget) iter.next(), found, foundList);
				//			        if (!searchAll && found.size() > 0) return;
			}
			_dbComparisons++; // for debug purposes: keep track of the number of comparisons done
			//		        if (!searchAll && found.size() > 0) return;
			if (m.matches(w)) {
				found.add(w);
				if (m instanceof MultiMatcher) {
					foundList.add(w);
				}
			}
		} 
	}
//MOVING
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	/////////////////////////////////////////////////////////////////////////////////

	//number of times to retry a widget find
	private static int getMaxFinderRetries() {
		return TestSettings.getInstance().getFinderRetries();
	}
	
	private static int getFinderRetryInterval() {
		return TestSettings.getInstance().getFinderRetryInterval();
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Contract helper.
	//
	/////////////////////////////////////////////////////////////////////////////////
	
	private void assertNotNull(Object o, String msg) {
		if (o == null)
			throw new AssertionError(msg);
	}
	
	private void assertNotNull(Object o, Error ex) {
		if (o == null)
			throw ex;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	///////////////////////////////////////////////////////////////////////////
	
	private static void pause(int ms) {
		try { Thread.sleep(ms); } catch(InterruptedException ie) { }
	}



	
}
