package abbot.tester.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Keep track of all known root windows, and all known showing/hidden/closed
 * windows.  This class has not been completely refactored for use with SWT.
 */
public class DecorationsTracker {
   
	/*
	 * [author=Dan] Occasionally, WindowTester will hang because
	 * the UI thread and the test thread clash when obtaining
	 * a lock on these synchronous methods and the UI asyncExec lock.
	 * The solution is to narrow the synchronization of this class
	 * to protect the fields but not include any asyncExec calls.
	 */
	
    /** Keep track of all app contexts we can.  Maps contexts to sets of root
     * windows.
     */
    private Map contexts;
    /** Keep track of the event queue for each top-level component seen. */
    private Map queues;
//    private ContextTracker contextTracker;
    
	/** Display object for the application **/
	private Display display;
	
	/**  One DecorationsTracker per Display */
	private static Map trackers = new WeakHashMap();
	
    /** Decorations which for which isShowing is true but are not yet ready for
        input. */
    //private Map pendingDecorations = new WeakHashMap();
    /** Decorations which we deem are ready to use.  */
    private Map openDecorations = new WeakHashMap();
    /** Decorations which are not visible. */
    private Map hiddenDecorations = new WeakHashMap();
    /** Decorations which have sent a WINDOW_CLOSE event. */
    //private Map closedDecorations = new WeakHashMap();
//    private static int windowReadyDelay = 
//        Properties.getProperty("abbot.window_ready_delay",
//                               0, 5000, 100);
//    private static boolean timerCanceled = false;
/* Currently, the queue is not being used by anyone. */
//    private Timer queue = new Timer(true) {
//            public void cancel() {
//                Log.debug("Decorations tracker timer canceled");
//                synchronized(openDecorations) {
//                    queue = null;
//                }
//                super.cancel();
//            }
//        };
//    private static DecorationsTracker tracker = null;

	/** Only ever want one of these for a given Display */
	public static synchronized DecorationsTracker getTracker(Display display) {
		DecorationsTracker tracker = (DecorationsTracker)trackers.get(display);
		if (tracker == null) {
			tracker = new DecorationsTracker(display);
			trackers.put(display,tracker);
		}
		return tracker;
	}

    /** Create an instance of DecorationsTracker which will track all windows
     * coming and going on the current and subsequent app contexts. 
     * WARNING: if an applet loads this class, it will only ever see stuff in
     * its own app context.
     */
    protected DecorationsTracker(Display display) {
        //contextTracker = new ContextTracker();
        //long mask = WindowEvent.WINDOW_EVENT_MASK
        //    | ComponentEvent.COMPONENT_EVENT_MASK;
        //Toolkit toolkit = Toolkit.getDefaultToolkit();
        //toolkit.addAWTEventListener(contextTracker, mask);
        //contexts = new WeakHashMap();
        //contexts.put(toolkit.getSystemEventQueue(), new WeakHashMap());
        //queues = new WeakHashMap();
        // Populate stuff that may already have shown/been hidden
        this.display = display;
//    	Shell[] shells = display.getShells();
//        synchronized(openDecorations) {
//            for (int i=0;i < shells.length;i++) {
//                scanExistingDecorations(shells[i]);
//            }
//        }
    }
    
    // don't need this.. use Decorations.getMinimized() instead
//    private static boolean isShowing(Decorations dec) {
//    	int windowStyle = dec.getShell().getStyle();
//    	if ((windowStyle & SWT.MIN) != 0 ) return false;
//    	return true;
//    }
     
//    private void scanExistingDecorations(Decorations dec) {
////        // Make sure we catch subsequent show/hide events for this window
////        new WindowWatcher(dec);
////        Shell[] shells = dec.getShell().getShells();
////        for (int i=0;i < shells.length;i++) {
////            scanExistingDecorations(shells[i]);
////        }
////        openDecorations.put(dec, dec);
////        // TODO: figure out how to make this distinction -- minimized/maximized or visible?
////        if (!dec.getVisible()) {
////            hiddenDecorations.put(dec, dec);
////        }
////        noteContext(dec);
//    }

    /** Returns whether the window is ready to receive input.
        A window's "isShowing" flag may be set true before the WINDOW_OPENED
        event is generated, and even after the WINDOW_OPENED is sent the
        window peer is not guaranteed to be ready.
    */ 
    public boolean isWindowReady(Decorations dec) {
        synchronized(openDecorations) {
            return openDecorations.containsKey(dec) && !hiddenDecorations.containsKey(dec);
        }
    }

    /** Return the event queue corresponding to the given component.  In most
        cases, this is the same as
        Widget.getToolkit().getSystemEventQueue(), but in the case of
        applets will bypass the AppContext and provide the real event queue.
    */
//    public EventQueue getQueue(Widget c) {
//        // Widgets above the applet in the hierarchy may or may not share
//        // the same context with the applet itself.
//        while (!(c instanceof java.applet.Applet) && c.getParent() != null)
//            c = c.getParent();
//        synchronized(contexts) {
//            EventQueue q = (EventQueue)queues.get(c);
//            if (q == null)
//                q = c.getToolkit().getSystemEventQueue();
//            return q;
//        }
//    }

    /** Returns all known event queues. */
    public Collection getEventQueues() {
        HashSet set = new HashSet();
        synchronized(contexts) {
            set.addAll(queues.values());
        }
        return set;
    }

	/**
	 * get all shells for this tracker's display
	 */
	public static /* synchronized */ Shell[] getShells(final Display d){
		// [author=Dan] Do NOT synchronize this entire method because 
		// it causes the test thread and the UI thread to occasionally deadlock
		// Instead, rework method so that it does not use a field 
		// and thus does not need to be synchronized.
		if(d == null || d.isDisposed())
			return new Shell[] {};
		if(d.getThread() == Thread.currentThread())
			return d.getShells();
		try{
			Thread.sleep(0,10);
		}
		catch(Exception e) {
		}
		final Object[] result = new Object[1];
		Robot.syncExec(d,null,new Runnable(){
			public void run(){
				if (d.isDisposed())
					result[0] = new Shell[] {};
				else
					result[0] = d.getShells();
			}
		});
		return (Shell[]) result[0];
	}
    
    /** Return all available root Decorations.  A root Window is one
     * that has a null parent.  Nominally this means a list similar to that
     * returned by Frame.getFrames(), but in the case of an Applet may return
     * a few Dialogs as well.
     */
    public Collection getRootDecorations() {
    	//!pq: added for insertion order invariant
        Set set = new LinkedHashSet();
        // TODO: In this version we don't do any tracking, but we provide this 
        // function for similarity with the Abbot API
        
//        synchronized(contexts) {
//            Iterator iter = contexts.keySet().iterator();
//            while (iter.hasNext()) {
//                EventQueue queue = (EventQueue)iter.next();
//                Toolkit toolkit = Toolkit.getDefaultToolkit();
//                Map map = (Map)contexts.get(queue);
//                set.addAll(map.values());
//            }
//        }
//        Shell activeShell = null;
//        if (display != null) { 
//        	activeShell = display.getActiveShell();
//        } else {
//        	System.out.println("Couldn't find display");
//        }
//        if (activeShell==null) {
//        	System.out.println ("Tracker couldn't find any shells");
//        	return set;
//        }
//        Shell[] shells = activeShell.getShells();
        Shell [] shells = getShells(display);
        for (int i=0;i < shells.length;i++) {
            set.add(shells[i]);
        }
        //Log.debug(String.valueOf(list.size()) + " total Frames");
        return set;
    }

	/**
	 * Return an array of all Displays in the current vm
	 * (Refactored -- moved from DefaultWidgetFinder)
	 */
	public static Display[] getDisplays(){
		// find the root threadgroup with no parent
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		while(group.getParent()!=null)
			group = group.getParent();
		
		// get all threads in this group
		Thread[] threads = new Thread[group.activeCount()+10];
		group.enumerate(threads,true);
		
		// check each thread for a corresponding display
		ArrayList displays = new ArrayList();	
		Display threadsDisplay;
		for(int i=0; i<threads.length; i++){
			if(threads[i]!=null){
				threadsDisplay = Display.findDisplay(threads[i]);
				if(threadsDisplay!=null && !threadsDisplay.isDisposed())
					displays.add(threadsDisplay);		
			}
		}
		
		//System.out.println("DefaultWidgetFinder.getDisplays() found "+displays.size()+" displays");
		return (Display[])displays.toArray(new Display[displays.size()]);
	}

//  --- STOPPED HERE --------
    /** Provides tracking of window visibility state.  We explicitly add this
     * on WINDOW_OPEN and remove it on WINDOW_CLOSE to avoid having to process
     * extraneous WidgetEvents.
     */
//    private class WindowWatcher
//        extends ShellAdapter implements ShellListener {
//        public WindowWatcher(Decorations w) {
//            //w.addControlListener(this);
//            w.getShell().addShellListener(this);
//        }
//        //public void componentShown(WidgetEvent e) {
//        public void shellActivated(ShellEvent e) {
//            markWindowShowing((Window)e.getSource());
//        }
//        //public void componentHidden(WidgetEvent e) {
//        public void shellDeactivated(ShellEvent e) {
//            synchronized(openDecorations) {
//                hiddenDecorations.put(e.getSource(), e.getSource());
//                cancelPendingReady(e.getSource());
//            }
//        }
//        public void windowClosed(WindowEvent e) {
//            e.getWindow().removeWindowListener(this);
//            e.getWindow().removeWidgetListener(this);
//        }
//        public void componentResized(WidgetEvent e) { }
//        public void componentMoved(WidgetEvent e) { }
//    }
//
//    /** Whenever we get a window that's on a new event dispatch thread, take
//     * note of the thread, since it may correspond to a new event queue and
//     * AppContext. 
//     */
//    // FIXME what if it has the same app context? can we check?
//    private class ContextTracker implements AWTEventListener {
//        public void eventDispatched(AWTEvent ev) {
//
//            WidgetEvent event = (WidgetEvent)ev;
//            Widget comp = event.getWidget();
//            // This is our sole means of accessing other app contexts
//            // (if running within an applet).  We look for window events
//            // beyond OPENED in order to catch windows that have already
//            // opened by the time we start listening but which are not
//            // in the Frame.getFrames list (i.e. they are on a different
//            // context).   Specifically watch for COMPONENT_SHOWN on applets,
//            // since we may not get frame events for them.
//            if (!(comp instanceof java.applet.Applet)
//                && !(comp instanceof Window)) {
//                return;
//            }
//
//            int id = ev.getID();
//            if (id == WindowEvent.WINDOW_OPENED) {
//                noteOpened(comp);
//            }
//            else if (id == WindowEvent.WINDOW_CLOSED) {
//                noteClosed(comp);
//            }
//            else if (id == WindowEvent.WINDOW_CLOSING) {
//                // ignore
//            }
//            // Take note of all other window events
//            else if ((id >= WindowEvent.WINDOW_FIRST
//                      && id <= WindowEvent.WINDOW_LAST)
//                     || id == WidgetEvent.COMPONENT_SHOWN) {
//                synchronized(openDecorations) {
//                    if (!getRootDecorations().contains(comp)
//                        || closedDecorations.containsKey(comp)) {
//                        noteOpened(comp);
//                    }
//                }
//            }
//            // The context for root-level windows may change between
//            // WINDOW_OPENED and subsequent events.
//            synchronized(contexts) {
//                if (!comp.getToolkit().getSystemEventQueue().
//                    equals(queues.get(comp))) {
//                    noteContext(comp);
//                }
//            }
//        }
//    }
//
//    private void noteContext(Widget comp) {
//        EventQueue queue = comp.getToolkit().getSystemEventQueue();
//        synchronized(contexts) {
//            Map whm = (Map)contexts.get(queue);
//            if (whm == null) {
//                contexts.put(queue, whm = new WeakHashMap());
//            }
//            if (comp instanceof Window && comp.getParent() == null)
//                whm.put(comp, comp);
//            queues.put(comp, queue);
//        }
//    }
//
//    private void noteOpened(Widget comp) {
//        //Log.debug("Noting " + Robot.toString(comp));
//        noteContext(comp);
//        // Attempt to ensure the window is ready for input before recognizing
//        // it as "open".  There is no Java API for this, so we institute an
//        // empirically tested delay.
//        if (comp instanceof Window) {
//            new WindowWatcher((Window)comp);
//            markWindowShowing((Window)comp);
//        }
//    }
//
//    private void noteClosed(Widget comp) {
//        if (comp.getParent() == null) {
//            EventQueue queue = comp.getToolkit().getSystemEventQueue();
//            synchronized(contexts) {
//                Map whm = (Map)contexts.get(queue);
//                if (whm != null)
//                    whm.remove(comp);
//                else {
//                    EventQueue foundQueue = null;
//                    Iterator iter = contexts.keySet().iterator();
//                    while (iter.hasNext()) {
//                        EventQueue q = (EventQueue)iter.next();
//                        Map map = (Map)contexts.get(q);
//                        if (map.containsKey(comp)) {
//                            foundQueue = q;
//                            map.remove(comp);
//                        }
//                    }
//                    if (foundQueue == null) {
//                        Log.log("Got WINDOW_CLOSED on "
//                                + Robot.toString(comp)
//                                + " on a previously unseen context: "
//                                + queue + "("
//                                + Thread.currentThread() + ")");
//                    }
//                    else {
//                        Log.log("Window " + Robot.toString(comp)
//                                + " sent WINDOW_CLOSED on "
//                                + queue + " but sent WINDOW_OPENED on "
//                                + foundQueue);
//                    }
//                }
//            }
//        }
//        synchronized(openDecorations) {
//            openDecorations.remove(comp);
//            hiddenDecorations.remove(comp);
//            closedDecorations.put(comp, comp);
//            cancelPendingReady(comp);
//        }
//    }
//
//    private void markWindowReady(Window w) {
//        synchronized(openDecorations) {
//            // If the window was closed after the check timer started running,
//            // the check timer might still try to mark the window ready.
//            // Make sure it's still on the pending list.
//            if (pendingDecorations.containsKey(w)) {
//                closedDecorations.remove(w);
//                hiddenDecorations.remove(w);
//                openDecorations.put(w, w);
//                pendingDecorations.remove(w);
//            }
//        }
//    }
//
//    /** Provide a method for canceling a pending "set window ready" task, in
//        case the window is closed before we think it's ready.
//    */
//    private void cancelPendingReady(Object key) {
//        synchronized(openDecorations) {
//            TimerTask task = (TimerTask)pendingDecorations.get(key);
//            if (task != null) {
//                pendingDecorations.remove(key);
//                task.cancel();
//            }
//        }
//    }
//
//    /** Indicate a window has set isShowing true and needs to be marked ready
//        when it is actually ready.
//    */
//    private void markWindowShowing(final Window w) {
//        if (windowReadyDelay == 0) {
//            markWindowReady(w);
//        }
//        else {
//            // NOTE: this task must be canceled if a CLOSE or HIDDEN is
//            // encountered before the task is run.
//            TimerTask task = new TimerTask() {
//                public void run() {
//                    synchronized(openDecorations) {
//                        markWindowReady(w);
//                    }
//                }
//            };
//            synchronized(openDecorations) {
//                if (queue != null) {
//                    try {
//                        queue.schedule(task, windowReadyDelay);
//                        pendingDecorations.put(w, task);
//                        //Log.debug("Added: " + Robot.toString(w));
//                    }
//                    catch(IllegalStateException e) {
//                        Log.warn("Not a valid timer task: " + e.getMessage());
//                    }
//                }
//            }
//        }
//    }
}
