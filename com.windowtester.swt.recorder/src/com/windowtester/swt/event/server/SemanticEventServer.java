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
package com.windowtester.swt.event.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.ISemanticEventHandler;
import com.windowtester.swt.event.recorder.IEventRecorderPluginTraceOptions;

/**
 * A controller class that receives meta events from developer Workbench and delegates them to GUI Recorder
 */
public class SemanticEventServer extends Thread {

	/** A special handler for meta-events */
	private ISemanticEventHandler handler;

	/** Flag to interrupt endless loop process */
	private boolean disposed = true;
	
	/**	The server socket */ 
	private ServerSocket server = null;
	
	/** The server port. The default is 0 meaning it will take any available port */
	private int port = 0;
	
	public SemanticEventServer(String name){
		super(name);
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			Logger.log("An error occured creating the codegen server socket: "+name, e);
		}
		disposed = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		super.run();
		
		Socket socket = null;
//		ObjectInputStream in = null;
		SemanticEventStream in = null;
		while (!disposed) {
			try {
				socket = server.accept();
//				in = new ObjectInputStream(socket.getInputStream());
				in = SemanticEventStream.forSocket(socket);
//				ISemanticEvent event = (ISemanticEvent)in.readObject();
				ISemanticEvent event = in.readEvent();
								
				handleEvent(event);
				in.close();
			} catch(SocketException e) {
			
				//socket exceptions are "expected" when the app tears down out from under
				//the event server -- a better solution would detect this and close the socket
				//preventatively...  in the meantime we just catch and ignore (or trace)
				
				//if trace is on and the exception does not appear to be of the "close" variety
				//trace it
				if (!isSocketClosed(e))
					TraceHandler.trace(IEventRecorderPluginTraceOptions.BASIC, "Socket exception caught in event server: " + e.getMessage());
				
			} catch (Exception e) {
				// log all other exceptions!
				LogHandler.log(e);
			}
		}
	}
	
	/**
	 * Test if this exception is of the "socket closed" variety
	 */
	private boolean isSocketClosed(SocketException e) {
		String message = e.getMessage();
		if (message == null)
			return false;
		return message.trim().equalsIgnoreCase("socket closed");
	}

	/**
	 * Handle the given event.
	 * @param event the event to handle
	 */
	protected void handleEvent(ISemanticEvent event) {
		if (event == null) {
			Logger.log(new IllegalArgumentException("event null"));
			return;
		}
			
		// accept event event for handling
		if(handler!=null)
			event.accept(handler);
		else
			Logger.log("Event handler was not supported!");
	}
	
	public void stopServer() {
		// this is asynchronous event - first set thread termination  flag to true
		disposed = true;
		// then close socket if the thread was blocked on accept,
		// this will completely terminate this thread
		try {
			server.close();
		} catch (IOException e) {
		}
	}
	
	public int getPort() {
		if(server!=null&&!server.isClosed()&&server.isBound())
			return server.getLocalPort();
		return -1;
	}

	public void setPort(int port){
		this.port = port;
	}
	
	public ISemanticEventHandler getHandler() {
		return handler;
	}

	public void setHandler(ISemanticEventHandler handler) {
		this.handler = handler;
	}

	public boolean isDisposed() {
		return disposed;
	}
}
