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
package com.windowtester.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.windowtester.internal.debug.Logger;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderControllerStartEvent;
import com.windowtester.recorder.event.meta.RecorderDisplayNotFoundEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;

/**
 * A listener that redirects event notifications over a socket.  Before streaming, 
 * SWTSemanticEvents are translated (demoted) into simpler objects that have no
 * references to UI objects (e.g., Widgets and Events).
 */
public class SocketStreamingListener implements ISemanticEventListener {

	/** The socket to use for sending event communication */
	private Socket _socket;
	
	/** An object output stream reference for use in sending objects */
	private ObjectOutputStream _out;
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notify(com.windowtester.swt.event.model.IUISemanticEvent)
	 */
	public void notify(IUISemanticEvent event) {
		send(event);
	}

	/**
	 * Send this event over the wire
	 * @param event - the event to send
	 */
	public void send(ISemanticEvent event) {
		if (event == null)
			return; //ignore null events
		// get the port number to connect to
		String portStr = System.getProperty(ICommunicationProtocolConstants.RECORDER_PORT_SYSTEM_PROPERTY);
		int port = Integer.parseInt(portStr);
		try {
			_socket = new Socket(InetAddress.getByName("localhost"), port);
			_out = new ObjectOutputStream(_socket.getOutputStream());
			_out.writeObject(event);
			_out.close();
		} catch (IOException e) {
			//Since events are logged by sending them over the socket, a broken socket event can't be sent! 
			//the best we can hope for is that there is a console attached
			Logger.log("An error occured in sending a semantic event message", e);
		} finally {
			_socket = null;
		}
	}


	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStart()
	 */
	public void notifyStart() {
		send(RecorderMetaEvent.START);
	}

    /**
     * @see com.windowtester.recorder.event.ISemanticEventListener#notifyPause()
     */
    public void notifyPause() {
    	send(RecorderMetaEvent.PAUSE);
    }
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStop()
	 */
	public void notifyStop() {
		send(RecorderMetaEvent.STOP);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyWrite()
	 */
	public void notifyWrite() {
		// TODO this will go away from the interface; ignored for now
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyRestart()
	 */
	public void notifyRestart() {
		send(RecorderMetaEvent.RESTART);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyDispose()
	 */
	public void notifyDispose() {
		send(RecorderMetaEvent.DISPOSE);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyError(com.windowtester.swt.event.model.RecorderErrorEvent)
	 */
	public void notifyError(RecorderErrorEvent event) {
		send(event);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyTrace(com.windowtester.swt.event.model.RecorderTraceEvent)
	 */
	public void notifyTrace(RecorderTraceEvent event) {
		send(event);
	}
	
	/**
	 * @see com.windowtester.recorder.event.ISemanticEventListener#notifyAssertionHookAdded(java.lang.String)
	 */
	public void notifyAssertionHookAdded(String hookName) {
		send(new RecorderAssertionHookAddedEvent(hookName));
	}
	
	/**
	 * This event will be broadcasted as soon as main event controller will start.
	 * @param port
	 */
	public void notifyControllerStart(int port){
		send(new RecorderControllerStartEvent(port));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyDisplayNotFound()
	 */
	public void notifyDisplayNotFound() {
		send(new RecorderDisplayNotFoundEvent());
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventListener#notifySpyModeToggle()
	 */
	public void notifySpyModeToggle() {
		send(RecorderMetaEvent.TOGGLE_SPY);
	}
}
