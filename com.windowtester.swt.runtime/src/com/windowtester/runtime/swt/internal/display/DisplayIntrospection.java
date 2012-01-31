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

package com.windowtester.runtime.swt.internal.display;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * Display introspection works in two modes: synchronous and asynchronous. 
 * 
 * In the case of asynchronous mode (method asynchIntrospect) DisplayIntrospection will start 
 * a thread and will try to find Display from all existing threads in current process and it 
 * will notify all registered listeners about success or timeout. 
 * 
 * In the case of synchronous mode (method syncIntrospect) display introspection will return 
 * Display object or null after some predefined timeout.
 * 
 */
public class DisplayIntrospection implements Runnable {

	
	private ArrayList<IDisplayIntrospectionListener> listeners = new ArrayList<IDisplayIntrospectionListener>();
	private ThreadGroup root;
	private long retryTime;
	
	/** Shell that we want to introspect in the case of synchronous introspection */ 
	//private Shell shell;

	
	
	public DisplayIntrospection(long retryTime){
		this.retryTime = retryTime;
		// Find the root thread group
	    root = Thread.currentThread().getThreadGroup();
	    while (root.getParent() != null) {
	        root = root.getParent();
	    }
	}
	
	public void addIntrospectionListener(IDisplayIntrospectionListener listener){
		listeners.add(listener);
	}
	
	public void removeIntrospectionListener(IDisplayIntrospectionListener listener){
		listeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Display display = doRun();
		if(display!=null){
			for (Iterator<IDisplayIntrospectionListener> iter = listeners.iterator(); iter.hasNext();) {
				IDisplayIntrospectionListener listener = iter.next();
				listener.provideDisplay(display);
			}
		}else{
			for (Iterator<IDisplayIntrospectionListener> iter = listeners.iterator(); iter.hasNext();) {
				IDisplayIntrospectionListener listener = iter.next();
				listener.timeout();
			}
		}
	}
	
	protected Display doRun(){
		long start = System.currentTimeMillis();
		Display display = null;
		while ((System.currentTimeMillis() - start) < retryTime) {
			display = findDisplay();
			if(display!=null)
				break;
			try {
				Thread.sleep(50); // be a good guy ...
			} catch (InterruptedException e) {
			}
		}
		if(display!=null)
			ensureInitialized(display);
		
		return display;
	}
	/**
	 * This method verifies and ensures display initialization. Verification
	 * is based on synchronous execution of empty Runnable.  
	 * 
	 * @param display the display to verify
	 */
	protected void ensureInitialized(Display display) {
		// execute until display is initialized
		while(!display.isDisposed()){
			try {
				// this empty execution is only required 
				// to verify that display is initialized
				new DisplayReference(display).execute(new Callable<Boolean>() {
					public Boolean call() throws Exception {
						return true;
					}
				}, 30000);
				break;
			} catch (Throwable e) {
				continue;
			}
		}
	}

	/**
	 * Finds Display in all threads of this process
	 * 
	 * @return Display instance from UI thread 
	 */
	protected Display findDisplay() {
	    return visit(root);
	}
	
	/**
	 * This method recursively visits all thread groups and tries to get 
	 * Display in all thread it finds.
	 * 
	 * @return The introspected Display from UI thread
	 */
    protected Display visit(ThreadGroup group) {
    	// Display to return
    	Display display = null;
        // Get threads in 'group'
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads*2];
        numThreads = group.enumerate(threads, false);
        // Enumerate each thread in 'group'
        for (int i=0; i<numThreads; i++) {
            // Get thread
            Thread thread = threads[i];
            display = Display.findDisplay(thread);
            if(display!=null)
            	return display;
        }
        // Get thread subgroups of 'group'
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups*2];
        numGroups = group.enumerate(groups, false);
        // Recursively visit each subgroup
        for (int i=0; i<numGroups; i++) {
        	display = visit(groups[i]);
        	if(display!=null)
        		return display;
        }
        return null;
    }
    
    public void asyncIntrospect(){
    	Thread t = new Thread(this, "Display Introspection Thread");
    	t.setDaemon(true);
    	t.start();
    }
    
    public Display syncIntrospect(){
    	return doRun();
    }
}
