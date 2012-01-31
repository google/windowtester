package abbot.finder.swt;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.DecorationsTracker;

/** Provide filtering of components in the current hierarchy to facilitate
    testing.  Widgets may be filtered after disposal so they don't factor
    into future test results.<p>
    In general, only Decorations are filtered, and isFiltered checks for both self
    and Decorations being filtered.  So getWidgets() returns a filtered set only
    when the parent is a Decorations or Shell.<p>
*/
public class TestHierarchy extends SWTHierarchy {

    private Map filtered = new WeakHashMap();
//    private Map transientFiltered = new WeakHashMap();

//    private static boolean trackAppletConsole =
//        Boolean.getBoolean("abbot.applet.track_console");
    // must keep a reference to this, or it will be gc'd
//    private Listener listener;

    public TestHierarchy(Display d) {
    	super(d);
    	display = d;
    	tracker = DecorationsTracker.getTracker(display);
        //ignoreExisting();
        // Watch for introduction of transient dialogs so we can automatically
        // filter them on dispose (WINDOW_CLOSED).  Don't do anything when the
        // component is simply hidden, since we can't tell whether it will be
        // re-used. 
        // TODO: look up the equivalent for SWT
//        listener = new Listener() {
//            public void handleEvent(Event e) {
//                if (e.type == SWT.Activate || e.type == SWT.Deiconify                    
//                        && e.widget instanceof Decorations) {
//                    final Decorations d = (Decorations)e.widget;
//                    if (transientFiltered.containsKey(d)) {
//                        // In case we disposed of it before
//                        setFiltered(d, false);
//                    }
//                }
//                else if (e.type == SWT.Close) {
//                    Decorations d = (Decorations)e.widget;
//                    if (SWT.isTransientDialog(w)) {
//                        setFiltered(w, true);
//                        transientFiltered.put(w, Boolean.TRUE);
//                    }
//                }
//            }
//        };
        // Add a weak listener so we don't leave a listener lingering about.
       // new WeakSWTEventListener(listener, WindowEvent.WINDOW_EVENT_MASK);
    }

    public boolean contains(Widget c) {
        return super.contains(c) && !isFiltered(c);
    }

    public void dispose(Decorations d) {
        if (contains(d)) {
            super.dispose(d);
            setFiltered(d, true);
        }
    }

    public void ignoreExisting() {
        Iterator iter = getRoots().iterator();
        while (iter.hasNext()) {
            setFiltered((Widget)iter.next(), true);
        }
    }

    public Collection getRoots() {
        Collection s = super.getRoots();
        s.removeAll(filtered.keySet()); 
        return s;
    }

    public Collection getWidgets(Widget w) {
    	//return super.getWidgets(w);
        if (!isFiltered(w)) {
            Collection s = super.getWidgets(w);
            // NOTE: this only removes those components which are directly
            // filtered. 
            s.removeAll(filtered.keySet());
            return s;
        }
        return EMPTY;
    }

//    private boolean isDecorationsFiltered(Widget w) {
//    	if (w instanceof Control) {
//	    	Decorations d = ((Control)w).getShell();	
//	        return w != null && isFiltered(w);
//    	} else {
//    		return false;
//    	}
//    }

    public boolean isFiltered(Widget w) {
        return filtered.containsKey(w);
            //|| (!(w instanceof Decorations) && isDecorationsFiltered(w));
    }

    /** Indicates whether the given component is to be included in the
        Hierarchy.  If the component is a Window, recursively applies the
        action to all owned Windows. 
    */
    public void setFiltered(final Widget w, final boolean filter) {
//        if (SWT.isSharedInvisibleFrame(c)) {
//            Iterator iter = getWidgets(c).iterator();
//            while (iter.hasNext()) {
//                setFiltered((Widget)iter.next(), filter);
//            }
//        }
//        else 
        {
            if (filter)
                filtered.put(w, Boolean.TRUE);
            else
                filtered.remove(w);
            if (w instanceof Shell) {
            	display.syncExec( new Runnable() {
            		public void run() {
                Shell[] owned = ((Shell)w).getShells();
                for (int i=0;i < owned.length;i++) {
                    setFiltered(owned[i], filter);
                }
            		}
            	});
            }
        }
    }
}
