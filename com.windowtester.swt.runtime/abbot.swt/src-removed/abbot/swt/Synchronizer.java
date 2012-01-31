/* this is still buggy */
package abbot.swt;

import java.util.*;

import org.eclipse.swt.widgets.Display;

/** 
 * Since SWT does not use Java's synchronization support, this class was created
 * in order to coordinate calls to Display.syncExec(Runnable) among multiple 
 * displays.  This is accomplished by making all calls to Display.syncExec(Runnable)
 * from a dedicated Synchronizer Thread while preventing Displays associated with the
 * original calling thread from blocking.
 **/
public class Synchronizer{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	boolean disposed;
	LinkedList displays = new LinkedList();
	LinkedList actions = new LinkedList();
	LinkedList pendingRequests = new LinkedList();
	
	static final int SLEEP_TIME_NS = 10;
	static Synchronizer singleton;
	
	/* Init the synchronizer thread */
	private Synchronizer(){
		disposed = false;
		// the thread that monitors requests
		Thread syncThread = new Thread(){
			public void run(){
				while(!isDisposed()){
					if(waitingRequest())
						serviceRequest();		
				}	
			}	
		};
		syncThread.setName("Abbot Synchronizer Thread");
		syncThread.start();	
	}
	
	/* Returns the singleton instance of this class, if it exists; otherwise it
	 * returns a new Synchronizer instance.
	 */
	public static Synchronizer getSynchronizer(){
		if(singleton==null){
			singleton = new Synchronizer();
		}
		return singleton;
	}
	
	/* Execute a Runnable on the given Display's thread, making sure to 
	 * prevent the Display associated with the calling thread, if it exists, from
	 * blocking.
	 */
	public void syncExec(Display display, Runnable action){
		Display currentThreadsDisplay = Display.findDisplay(Thread.currentThread());
		if(currentThreadsDisplay==display){
			display.syncExec(action);
		}
		if(display==null)
			System.out.println("FOUND NULL DISPLAY");
		else{
			makeRequest(display, action);	
			while(!requestServiced()){
				if(currentThreadsDisplay!=null){
					currentThreadsDisplay.readAndDispatch();
				}
				else{
					try{
					Thread.sleep(0,SLEEP_TIME_NS);
					}catch(InterruptedException ie){
						ie.printStackTrace();
					}
				}
			}
		}
	}
	
	/* Make a request for a syncExec call */
	private synchronized void makeRequest(Display display, Runnable action){
		if(display.isDisposed())
			System.out.println("FOUND DISPOSED DISPLAY");
		displays.add(display);
		actions.add(action);	
		pendingRequests.add(Thread.currentThread());
	}
	
	/* Indicates if the request made by the current thread has been serviced yet */
	private synchronized boolean requestServiced(){
		return !pendingRequests.contains(Thread.currentThread());
	}
	
	/* Called by the synchronizer thread to actually make the next syncExec call */
	private void serviceRequest(){
		Display display;
		Runnable action;
		synchronized(this){
			// get the display and runnable for the next request
			display = (Display)displays.getFirst();
			displays.removeFirst();
			action = (Runnable)actions.getFirst();
			actions.removeFirst();			
		}

		// make the requested syncExec call
		if(display.isDisposed())
			System.out.println("FOUND DISPOSED DISPLAY");
		display.wake();
		display.syncExec(action);
		
		synchronized(this){
			// note that the request was serviced
			pendingRequests.removeFirst();
		}
	}

	/* Indicates if there is a request that is awaiting servicing */
	private synchronized boolean waitingRequest(){
		return pendingRequests.size()>0;
	}
	
	/* Dispose of the synchronizer */
	public void dispose(){
		disposed = true;
	}
	
	/* Indicates if the synchronizer has been disposed yet */
	public synchronized boolean isDisposed(){
		return disposed;
	}
}
//public class Synchronizer {
//	Hashtable locks = new Hashtable();
//	ArrayList queue = new ArrayList();
//	static Synchronizer synchronizer;
//	Hashtable flags = new Hashtable();
//	static final int SLEEP_TIME_NS = 100;
//	
//	Synchronizer(){// make sure that no one can instantiate their own
//	}
//			
//	public synchronized void initFlag(Thread callingThread){
//		flags.put(callingThread,new Boolean(false));
//	}
//		
//	public synchronized void clearFlag(Thread callingThread){
//		flags.remove(callingThread);
//	}
//			
//	/* To be called inside of an syncExec/asyncExec call */	
//	public synchronized void setFlag(Object obj, Thread callingThread){
//		Display display = Display.findDisplay(Thread.currentThread());
//		flags.put(callingThread, new Boolean(true));
//		try{
//		if(obj!=null)
//			obj.notifyAll();
//		}catch(IllegalMonitorStateException imse){
//			imse.printStackTrace();
//		}
//		notifyAll();
//	}
//		
//	public synchronized boolean getFlag(Thread callingThread){
////		if(display==null)
////			System.err.println("null display in getFlag");
////		 if(flags.get(display)==null)
////			System.err.println("null flag in getFlag");
//		return ((Boolean)flags.get(callingThread)).booleanValue();
//	}
//	
//	// DOESN'T WORK... breaks when multiple threads are waiting...
//	/**
//	 * 
//	 * @param display the Display whose thread the Runnable is to be run on
//	 * @param obj the Object inside whose synchronized method this is called or null	 	
//	 * @param action the action to be executed 
//	 */
//	public void syncExec(Display display, final Object obj,final Runnable action){
//		syncExecEntry(display);
//		display.syncExec(action);
//		syncExecExit(display);
//
////		final Thread callingThread = Thread.currentThread();
////		initFlag(callingThread);
////		
////		
////		display.asyncExec(new Runnable(){
////			public void run(){
////				if(obj!=null){
////					synchronized(obj){
////						action.run();
////						setFlag(obj,callingThread);		
////					}
////				}
////				else{
////					action.run();
////					setFlag(obj,callingThread);		
////				}
////			}
////		});
////		
////		while(!getFlag(callingThread)){
//////			Robot.waitForIdle(display);
////			if(display.getThread()==Thread.currentThread()){
////				display.readAndDispatch();
////			}
////			else{
////				try{
////					if(obj==null)
////						wait();
////						//Thread.sleep(0,SLEEP_TIME_NS);
////					else
////					try{	
////						wait();
////						obj.wait();
////					}catch(IllegalMonitorStateException imse){
////						imse.printStackTrace();
////					}
////				}
////				catch(InterruptedException ie){
////					ie.printStackTrace();
////				}
////			}
////		}
////		
////		clearFlag(callingThread);
//	}
//
//	/** Returns the singleton instance of this class **/
//	public static Synchronizer getSynchronizer(){
//		if(synchronizer==null)
//			synchronizer = new Synchronizer();
//		return synchronizer;		
//	}
//	Hashtable callerDisplays = new Hashtable(); 
//	Hashtable calleeDisplays = new Hashtable();
//	
//	HashSet lockedDisplays = new HashSet();
//	// only lock the calling thread's display, if any
//	// if this doesn't work
//	public void syncExecEntry(Display display){
//		boolean locked;
//		Display thisThreadsDisplay = Display.findDisplay(Thread.currentThread());
//		
//		while(true){
//			synchronized(this){
//				locked = lockedDisplays.contains(display);
//			}
//			if(locked)
//				thisThreadsDisplay.readAndDispatch();
//			else{
//				lockedDisplays.add(display);
//			}
//		}		
//	}
//	
//	public void syncExecExit(Display display){
//		synchronized(this){
//			lockedDisplays.remove(display);
//		}
//	}
//	
//	/* Prevents the Display object associated with the current thread, if it exists,
//	 * from blocking, and locks the given object.  The Object parameter should be
//	 * the object on which a method is synchronized, or null.
//	 */
//	public void syncEntry(Object obj){
//		boolean locked;
//		Display display;
//		synchronized(this){
//			if(!locks.containsKey(obj))
//				locks.put(obj,new Boolean(false));
//		
//			locked =((Boolean)locks.get(obj)).booleanValue();
//			if(!locked){ // lock 
//				locks.put(obj, new Boolean(true));			
//				return;
//			}		
//		}
//		display = Display.findDisplay(Thread.currentThread());
//		if(display==null && obj!=null){
//			try{obj.wait();}  // NOT SURE ABOUT THIS ONE.		
//			catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		
//		while(true){
//			synchronized(this){
//				locked =((Boolean)locks.get(obj)).booleanValue();
//				if(!locked){
//					locks.put(obj, new Boolean(true));	
//					return;
//				}	
//			}
//			if(!display.isDisposed())
//				display.readAndDispatch();			
//		}
//	}
//	
//	public synchronized void syncExit(Object obj){
//		locks.put(obj,new Boolean(false));
//		obj.notifyAll();
//	}
//}

//	/** Execute the given Runnable on the Display's Thread in a synchronized
//	 *  manner.
//	 */
//	public void syncExec(Display display, Runnable action){
//		lock(display);
//		display.syncExec(action);		
//		unlock(display);
//	}
//
//	private synchronized void lock(final Display display){
//		if(!locks.containsKey(display)){
//			locks.put(display,new Boolean(false));
//		}
//		boolean locked = ((Boolean)locks.get(display)).booleanValue();
//		
//		if(locked){
//			try{
//				wait();
//			}
//			catch(InterruptedException ie){
//				ie.printStackTrace();
//			}
//		}
//		display.syncExec(new Runnable(){
//			public void run(){
//				locks.put(display,new Boolean(true));			
//			}
//		});
//	}
//	
//	private synchronized void unlock(final Display display){
//		display.syncExec(new Runnable(){
//			public void run(){
//				locks.put(display,new Boolean(false));
//			}			
//		});
//		notifyAll();
//	}
//}

/* Here's the plan.  We're going to have 4 synchronization methods-
 * 		syncExecEntry(Display)/syncExecExit(Display)
 *  	syncEntry(Object)/syncExit(Object)
 * And they're all going to use the same set of locks.
 * 
 * Inside a now-synchronized method (which will no longer be once this is ready),
 * we'll do this:
 * 		public void myMethod(){
 * 			syncEntry(this);
 * 				...
 * 				Robot.syncExec() // which calls syncExecEntry/...Exit
 * 			
 * 			synchronized(this){
 * 				syncExit(this);
 * 				return res;
 * 			}
 * 		}
 * Note how we overlapped the synchronization so that we can return a result while
 * still protected.  This will require that you do something other that obj.wait
 * inside of syncEntry.  You should wait on some other object that is associated 
 * with obj.
 * 
 * Just to summarize, the purpose of syncExecEntry/Exit is to make sure that the
 * calling thread doesn't block so that calls to syncExec  on the display associated 
 * with this calling thread do not block as well and cause deadlock.
 * 
 * The syncEntry/Exit is to make sure that, inside a syncExec block, we don't block
 * on a synchronized method while someone else blocks on another call to syncExec/
 * 
 * The existing code is not correct, but its getting somewhere.  Just think about
 * these 2 situations and it should work out.
*/