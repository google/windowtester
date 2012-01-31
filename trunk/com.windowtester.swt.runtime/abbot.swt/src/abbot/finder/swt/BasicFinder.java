package abbot.finder.swt;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.swt.internal.abbot.IExceptionListener;
import com.windowtester.runtime.swt.internal.abbot.ModalShellClosingExceptionListener;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.finder.legacy.SearchScopeHelper;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;
import com.windowtester.runtime.util.TestMonitor;

/** Provides basic widget lookup, examining each widget in turn.
    Searches all widgets of interest in a given hierarchy.
 */

public class BasicFinder implements WidgetFinder {
    private Hierarchy hierarchy;

    public static final int DFS = 1; // depth-first search
    public static final int BFS = 2; // breadth-first search

    private static WidgetFinder DEFAULT = null;
    private int searchType;

    
    private int dbComparisons; /* debug variable to test how many comparisons are made */
    
    private List _listeners = new ArrayList();
    
    
    private SearchScopeHelper _searchScopeHelper;
    
  
    //used to bound retries
    private int _attempts;
    
	public static WidgetFinder getDefault() { 
    	if (DEFAULT==null) {
    		DEFAULT = new BasicFinder(new SWTHierarchy(Display.getDefault())); 
    		// TODO: check whether this is the right default
    	}
    	return DEFAULT; 
	}

    private class SingleWidgetHierarchy implements Hierarchy {
        //private Widget root;
        private ArrayList list = new ArrayList();
        public SingleWidgetHierarchy(Widget root) {
            //this.root = root;
            list.add(root);
        }
        public SingleWidgetHierarchy(Composite root) {
            //this.root = root;
            list.add(root);
        }
        public Collection getRoots() {
            return list;
        }
        public Collection getWidgets(Widget c) { 
            return getHierarchy().getWidgets(c);
        }
        public Widget getParent(Widget c) {
            return getHierarchy().getParent(c);
        }
        public boolean contains(Widget c) {
            return getHierarchy().contains(c);
            // TODO: find a way to find widgets at all levels in SWT
            //    && SwingUtilities.isDescendingFrom(c, root);
        }
        public void dispose(Decorations w) { getHierarchy().dispose(w); }
    }

    public BasicFinder(Hierarchy h) {
    	this (h, DFS);
    }

    /**Create a new finder specifying the hierarchy that should be
     * used and the search type to be used (breadth-first or depth-first).
     * Note that choosing the search type does not make sense if the matcher  
     * is an instance MultiMatcher, since in this case the entire hierarchy 
     * has to be searched anyway. 
     * 
     * @param h 			Widget hierarchy to use
     * @param searchAll		If true, return only unique matches and throw WidgetNotFound
     * 						or MultipleWidgetFound exceptions; otherwise, return the 
     * 						first match found or a WidgetNotFoundException
     * @param searchType	Type of search (BFS or DFS)
     */    
    public BasicFinder (Hierarchy h, int searchType) {
    	hierarchy = h;
    	this.searchType = searchType;   	
    	if (hierarchy instanceof SWTHierarchy) {
    		SWTHierarchy swtHierarchy = (SWTHierarchy)hierarchy;
        	//if we're running in a testcase, add a special exception handler 
        	if (TestMonitor.getInstance().isTestRunning())
        		addExceptionListener(new ModalShellClosingExceptionListener(swtHierarchy.getDisplay()));
        	_searchScopeHelper = new SearchScopeHelper(swtHierarchy);
    	}
    }
               
    private void addExceptionListener(IExceptionListener listener) {
		_listeners.add(listener);
	}
    
    private void informExceptionListeners(String description) {
    	for (Iterator iter = _listeners.iterator(); iter.hasNext(); ) {
    		((IExceptionListener)iter.next()).preException(description);
    	}
    }

    protected Hierarchy getHierarchy() {
        return hierarchy;
    }

    /** 
     * Convenience method that makes getting a widget's parent possible through 
     * the finder
     * @param widget The widget whose parent should be found
     * @return The parent of the given widget
     */
    public Widget getWidgetParent(Widget widget) {
    	return hierarchy.getParent(widget);
    }
    
    public Widget find(Composite root, Matcher m) 
    throws WidgetNotFoundException, MultipleWidgetsFoundException {
    	return find((Widget)root,m);
    }    

    /** Find a Widget, using the given Matcher to determine whether a given
        widget in the hierarchy under the given root is the desired
        one.
    */
    public Widget find(Widget root, Matcher m) 
        throws WidgetNotFoundException, MultipleWidgetsFoundException {
    	dbComparisons = 0;
    	Hierarchy h = root != null
            ? new SingleWidgetHierarchy(root) : getHierarchy();
        return find(h, m);
    }

    
    /** Find a Widget, using the given Matcher to determine whether a given
        widget in the hierarchy used by this WidgetFinder is the desired
        one.
    */
    public Widget find(Matcher m)
        throws WidgetNotFoundException, MultipleWidgetsFoundException {
    	dbComparisons = 0;
    	_attempts = 0;
    	return findInShellScope(m);
    }

    private Widget findInShellScope(Matcher m) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
    	/*
    	 * SPIKE: root search at active shell or in shell identified by 
    	 * a ShellComponentMatcher
    	 */
    	Shell shell = _searchScopeHelper.getShellSearchScope(m);
    
    	return findInShellScope0(shell, m);
	}

	private Widget findInShellScope0(Shell shell, Matcher m) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
		Hierarchy h = new SingleWidgetHierarchy(shell);
        
		Set found = new HashSet();
        /* The list is used in parallel with the set for multimatchers who need 
         * to know in what order elements were added to the list */
        java.util.List foundList = new ArrayList();
        Collection roots = h.getRoots();
        if (searchType==DFS) {
	        Iterator iter = roots.iterator();
	        while (iter.hasNext()) {
	            findMatches(h, m, (Widget)iter.next(), found, foundList);
	        }
        } else if (searchType==BFS) {
    		LinkedList searchQ = new LinkedList(roots);
    		while (searchQ.size() > 0) {
    			Widget current = (Widget)searchQ.removeFirst();
    			dbComparisons++;
    			if (m.matches(current)) {
					// Matcher should never return multiple or throw MultipleWidgetsFoundException
    				if (!(m instanceof MultiMatcher)) {
    					return current;
    				}
    	            found.add(current);
    	        	if (m instanceof MultiMatcher) {
    	        		foundList.add(current);
    	        	}
    			}
    			searchQ.addAll(h.getWidgets(current));
    		}        	
        }
        
        //update attempt number and possibly try again
        if (found.size()!= 1 && _attempts++ < SWTWidgetFinder.getMaxFinderRetries()) {
        	TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "BasicFinder failed to find widget (" + m + ") relative to shell (" + UIProxy.getToString(shell) +") retrying [" + _attempts + "/" + SWTWidgetFinder.getMaxFinderRetries() +"]");
			pause(SWTWidgetFinder.getFinderRetryInterval());
        	return findInShellScope(m);
        }	
             
        if (found.size() == 0) {
	        String msg = "Widget NOT Found:\n" + m.toString() + " in shell: " + UIProxy.getToString(shell);
	        informExceptionListeners("WidgetNotFound");
			throw new WidgetNotFoundException(msg);
        }
        else if (found.size() > 1) {
            Widget[] list = (Widget[])
                foundList.toArray(new Widget[foundList.size()]);
            if (!(m instanceof MultiMatcher)) {
	          String msg = "MULTIPLE Widgets Found:\n" + m.toString();
	          informExceptionListeners("MultipleWidgetsFound");
              throw new MultipleWidgetsFoundException(msg, list);
            }
            return ((MultiMatcher)m).bestMatch(list);
        }
        return (Widget)found.iterator().next();
	}

	protected Widget find(Hierarchy h, Matcher m)
    	throws WidgetNotFoundException, MultipleWidgetsFoundException {
    	//reset attempts
    	_attempts = 0;
    	return find0(h, m);
    }
    
    protected Widget find0(final Hierarchy h, final Matcher m)
        throws WidgetNotFoundException, MultipleWidgetsFoundException {
        final Set found = new HashSet();
        /* The list is used in parallel with the set for multimatchers who need 
         * to know in what order elements were added to the list */
        final java.util.List foundList = new ArrayList();
        final Collection roots = h.getRoots();
        if (searchType==DFS) {
	        Iterator iter = roots.iterator();
	        while (iter.hasNext()) {
	            findMatches(h, m, (Widget)iter.next(), found, foundList);
	        }
		
	        DisplayExec.sync(new Runnable(){
				public void run() {
					Iterator iter = roots.iterator();
					while (iter.hasNext()) {
						findMatches(h, m, (Widget) iter.next(), found, foundList);
					}					
				}
			});
			
        } else if (searchType==BFS) {
    		LinkedList searchQ = new LinkedList(roots);
    		while (searchQ.size() > 0) {
    			Widget current = (Widget)searchQ.removeFirst();
    			dbComparisons++;
    			if (m.matches(current)) {
					// Matcher should never return multiple or throw MultipleWidgetsFoundException
    				if (!(m instanceof MultiMatcher)) {
    					return current;
    				}
    	            found.add(current);
    	        	if (m instanceof MultiMatcher) {
    	        		foundList.add(current);
    	        	}
    			}
    			searchQ.addAll(h.getWidgets(current));
    		}        	
        }
        
        
        
        //update attempt number and possibly try again
        if (found.size()!= 1 && _attempts++ < SWTWidgetFinder.getMaxFinderRetries()) {
        	TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "BasicFinder failed to find widget (" + m + ") retrying [" + _attempts + "/" + SWTWidgetFinder.getMaxFinderRetries() +"]");
			pause(SWTWidgetFinder.getFinderRetryInterval());
        	return find0(h, m);
        }	
        
        
        if (found.size() == 0) {
	        String msg = "Widget NOT Found:\n" + m.toString();
	        informExceptionListeners("WidgetNotFound");
			throw new WidgetNotFoundException(msg);
        }
        else if (found.size() > 1) {
            Widget[] list = (Widget[])
                foundList.toArray(new Widget[foundList.size()]);
            if (!(m instanceof MultiMatcher)) {
	          String msg = "MULTIPLE Widgets Found:\n" + m.toString();
	          informExceptionListeners("MultipleWidgetsFound");
              throw new MultipleWidgetsFoundException(msg, list);
            }
            return ((MultiMatcher)m).bestMatch(list);
        }
        return (Widget)found.iterator().next();
    }
    
    protected void findMatches(Hierarchy h, Matcher m,
                               Widget w, Set found, java.util.List foundList) {
		// Matcher should never return multiple or throw MultipleWidgetsFoundException
        if (found.size() == 1 && !(m instanceof MultiMatcher))
            return;
    	if (searchType==DFS) {
	        Iterator iter = h.getWidgets(w).iterator();
	        while (iter.hasNext()) {
	            findMatches(h, m, (Widget)iter.next(), found, foundList);
//		        if (!searchAll && found.size() > 0) return;
	        }
	        dbComparisons++;  // for debug purposes: keep track of the number of comparisons done
//	        if (!searchAll && found.size() > 0) return;
	        if (m.matches(w)) {
	        	found.add(w);
	        	if (m instanceof MultiMatcher) {
	        		foundList.add(w);
	        	}
	        }
    	} 

    }
     
    public void dbPrintWidgets() {
    	if (hierarchy instanceof SWTHierarchy) {
    		((SWTHierarchy)hierarchy).dbPrintWidgets();
    	}
    }

    public void printWidgets() {
    	dbPrintWidgets();
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
