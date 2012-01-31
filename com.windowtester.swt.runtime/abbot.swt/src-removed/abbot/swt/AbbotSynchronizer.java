/* THIS CODE IS CURRENTLY NOT IN USE ANYWHERE IN ABBOTFORSWT
 * An attempt to subclass org.eclipse.swt.widgets.Synchronizer in order
 * to provide a public API for coordination with calls to Display.syncExec(Runnable). 
 * 
 * NOTE: Most of the code here is copied from org.eclipse.swt.widgets.Synchronizer.
 */
package abbot.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.internal.Compatibility;
import org.eclipse.swt.SWT;

public class AbbotSynchronizer extends org.eclipse.swt.widgets.Synchronizer{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	/**
	 * Instances of this class are used to ensure that an
	 * application cannot interfere with the locking mechanism
	 * used to implement asynchonous and synchronous communication
	 * between widgets and background threads.
	 */
	class RunnableLock {
		Runnable runnable;
		Thread thread;
		Throwable throwable;
	
		RunnableLock (Runnable runnable) {
			this.runnable = runnable;
		}
	
		boolean done () {
			return runnable == null || throwable != null;
		}
	
		void run () {
			if (runnable != null) runnable.run ();
			runnable = null;
		}
	}

	
	Display display;
	int messageCount;
	RunnableLock [] messages;
	Object messageLock = new Object ();
	Thread syncThread;

	public AbbotSynchronizer(Display display){
		super(display);
		this.display = display;
	}
	
		
	void addLast (RunnableLock lock) {
		synchronized (messageLock) {
			if (messages == null) messages = new RunnableLock [4];
			if (messageCount == messages.length) {
				RunnableLock[] newMessages = new RunnableLock [messageCount + 4];
				System.arraycopy (messages, 0, newMessages, 0, messageCount);
				messages = newMessages;
			}
			messages [messageCount++] = lock;
		}
	}
	
	/**
	 * Causes the <code>run()</code> method of the runnable to
	 * be invoked by the user-interface thread at the next 
	 * reasonable opportunity. The caller of this method continues 
	 * to run in parallel, and is not notified when the
	 * runnable has completed.
	 *
	 * @param runnable code to run on the user-interface thread.
	 *
	 * @see #syncExec
	 */
	protected void asyncExec (Runnable runnable) {
		if (runnable != null) addLast (new RunnableLock (runnable));
		display.wake ();
	}
	
	int getMessageCount () {
		return messageCount;
	}
	
	void releaseSynchronizer () {
		display = null;
		messages = null;
		messageLock = null;
		syncThread = null;
	}
	
	RunnableLock removeFirst () {
		synchronized (messageLock) {
			if (messageCount == 0) return null;
			RunnableLock lock = messages [0];
			System.arraycopy (messages, 1, messages, 0, --messageCount);
			messages [messageCount] = null;
			if (messageCount == 0) messages = null;
			return lock;
		}
	}
	
	boolean runAsyncMessages () {
		if (messageCount == 0) return false;
		RunnableLock lock = removeFirst ();
		if (lock == null) return true;
		synchronized (lock) {
			syncThread = lock.thread;
			try {
				lock.run ();
			} catch (Throwable t) {
				lock.throwable = t;
				SWT.error (SWT.ERROR_FAILED_EXEC, t);
			} finally {
				syncThread = null;
				lock.notifyAll ();
			}
		}
		return true;
	}
	
	
	/**
	 * Causes the <code>run()</code> method of the runnable to
	 * be invoked by the user-interface thread at the next 
	 * reasonable opportunity. The thread which calls this method
	 * is suspended until the runnable completes.
	 *
	 * @param runnable code to run on the user-interface thread.
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_FAILED_EXEC - if an exception occured when executing the runnable</li>
	 * </ul>
	 *
	 * @see #asyncExec
	 */
	protected void syncExec (Runnable runnable) {
		if (Thread.currentThread()== display.getThread()) {
			if (runnable != null) runnable.run ();
			return;
		}
		if (runnable == null) {
			display.wake ();
			return;
		}
		RunnableLock lock = new RunnableLock (runnable);
		/*
		 * Only remember the syncThread for syncExec.
		 */
		 		 
		lock.thread = Thread.currentThread();
		
		/* NEW CODE */
		Display callingThreadsDisplay = Display.findDisplay(lock.thread);
		final int WAIT_TIME_NS = 10;
		/* END NEW CODE */
		
		synchronized (lock) {
			addLast (lock);
			display.wake ();
			boolean interrupted = false;
			while (!lock.done ()) {
				try {
					/* CHANGED CODE */
					lock.wait (0, WAIT_TIME_NS);  // was 'lock.wait();'
					/* END CHANGED CODE */
					
					/* NEW CODE */
					if(callingThreadsDisplay!=null && !callingThreadsDisplay.isDisposed()){
						callingThreadsDisplay.readAndDispatch();
					}
					/* END NEW CODE */				
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
			if (interrupted) {
				Compatibility.interrupt();
			}
			if (lock.throwable != null) {
				SWT.error (SWT.ERROR_FAILED_EXEC, lock.throwable);
			}
		}
	}

}

