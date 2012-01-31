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
package com.windowtester.swt.event.recorder;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.debug.Tracer;
import com.windowtester.net.ICommunicationProtocolConstants;
import com.windowtester.net.SocketStreamingListener;
import com.windowtester.recorder.IEventRecorder;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.swt.internal.display.DisplayIntrospection;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettingsFactory;
import com.windowtester.swing.event.recorder.SwingGuiTestRecorder;
import com.windowtester.swt.event.server.SessionEventController;
import com.windowtester.swt.event.server.WorkbenchEventController;

/**
 * The main access point to SWT event recording.
 */
public class EventRecorderPlugin extends Plugin {

	/** The unique identifier for this plugin */
	public static final String PLUGIN_ID = "com.windowtester.swt.recorder";

	/** For automatically install bundles provided by this system property */ 
	public static final String INSTALL_BUNDLES_SYS_PROPERTY = "install.bundles";
	
	/** Default amount of time for thread introspection */ 
	private static final long RETRY_TIME = 10000; // TODO [Alex] move the whole Display introspection deal to utils
	
	/** The name of the launch class */
	public static final String LAUNCH_CLASS_NAME_PROP = "com.windowtester.swt.launch.class.name";
	
	/** the property that will always indicate the Swing recording session */ 
	public static final String SWING_LAUNCH_PROP = "com.windowtester.swt.launch.swing.type";	
	
	/** The shared instance. */
	private static EventRecorderPlugin _plugin;

	public static boolean inRecording = false;

	/** Resource bundle. */
	private ResourceBundle _resourceBundle;

	/** The recorder instance */
	private IEventRecorder _eventRecorder;

	/** Socket streaming listener that sends events to main Workbench instance */ 
	private SocketStreamingListener _socketListener;
	
	/** Meta events controller thread reference */ 
	private SessionEventController _metaController;
	
	/** Workbench event controller */
	private WorkbenchEventController workbenchServer;

	/** The display reference */
	private Display _display;

	/**
	 * @return the current display
	 */
	public Display getDisplay() {
		if(_display==null){
			_display = findDisplay();
		}
		return _display;
	}

	private Display findDisplay() {
		DisplayIntrospection displayFinder = new DisplayIntrospection(RETRY_TIME);
		return displayFinder.syncIntrospect();
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;
		//_settings = new RecorderSettings();
		try {
			_resourceBundle = ResourceBundle
					.getBundle("com.windowtester.event.recorder.EventRecorderPluginResources");
		} catch (MissingResourceException x) {
			_resourceBundle = null;
		}
		if(isRecordingSession()&&!isRcpRecordingSession()){
			installSpecialBundles(context);
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		if(workbenchServer!=null&&workbenchServer.isAlive()){
			workbenchServer.stopServer();
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventRecorderPlugin getDefault() {
		return _plugin;
	}

	/**
	 * Get the String identifier for this plugin.
	 * @return the plugin id
	 */
	public static String getPluginId() {
		return PLUGIN_ID;
	}
	
	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = EventRecorderPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return _resourceBundle;
	}

	/**
	 * Start recording events
	 */
	public static void startRecording() {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().start();
			}	
		}).run();
	}

	/**
	 * Stop recording events
	 */
	public static void stopRecording() {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().stop();
			}	
		}).run();
	}

	/**
	 * 
	 * @return the shared event recorder instance
	 */
	public IEventRecorder getRecorder() {
		try {
			if (_eventRecorder == null) {
				_eventRecorder = createRecorder();
				_eventRecorder.addListener(_socketListener);
			}
		} catch (RuntimeException e) {
			/*
			 * This is for debugging purposes.  We have no choice but to dump to
			 * the console since this is occuring in the event recording context. 
			 */
			e.printStackTrace();
			throw e;
		}
		return _eventRecorder;
	}

	/**
	 * Create a new recorder instance.
	 */
	private IEventRecorder createRecorder() {
		if (isSwingRecordingSession())
			return new SwingGuiTestRecorder();
		int apiVersion = PlaybackSettingsFactory.getPlaybackSettings().getRuntimeAPIVersion();
		return new GuiTestRecorder(getDisplay(), apiVersion );		
	}

	/**
	 * Write the recorded events.
	 */
	public static void writeRecording() {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().write();
			}	
		}).run();
	}

	/**
	 * Pause recording.
	 */
	public static void pauseRecording() {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().pause();
			}	
		}).run();
	}
	
	/**
	 * Terminate the recording session.
	 */
	public static void terminateRecording() {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().terminate();
			}	
		}).run();
	}
	
	public static void toggleSpyMode() {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().toggleSpyMode();
			}	
		}).run();
	}
	
	
	/**
	 * Restart the recorder.
	 */
	public static void restartRecording() {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().restart();
			}	
		}).run();
	}

	/**
	 * Add an assertion hook.
	 * @param hookName - the name of the hook to add 
	 */
	public static void addAssertion(final String hookName) {
		(new SyncDisplayRunnable(){
			void doRun() {
				getDefault().getRecorder().addHook(hookName);
			}	
		}).run();
	}
	
	/**
	 * Find out if current application is under recording setup
	 * @return true if it is being set for recording
	 */
	public static boolean isRecordingSession(){
		return System.getProperty(ICommunicationProtocolConstants.RECORDER_PORT_SYSTEM_PROPERTY)!=null;
	}
	
	/**
	 * Find out if current application is under RCP recording setup
	 * @return true if it is being set for recording
	 */
	public static boolean isRcpRecordingSession(){
		return isRecordingSession()&&(System.getProperty(EventRecorderPlugin.LAUNCH_CLASS_NAME_PROP)==null);
	}
	
	public static boolean isSwingRecordingSession(){
		return System.getProperty(SWING_LAUNCH_PROP)!=null;
	}
	
	/**
	 * Small wrapper to run synchronous operation within Display's syncExec method if session is
	 * SWT session and not if recording session is Swing session.
	 */
	static abstract class SyncDisplayRunnable {
		void run(){
			if(isSwingRecordingSession()){
				doRun();
				return;
			}else{
				Display d = getDefault().getDisplay();
				if(d!=null){
					d.syncExec(new Runnable(){
						public void run() {
							doRun();
						}
					});
				}else{
					getDefault()._socketListener.notifyDisplayNotFound();
				}
			}
		}
		abstract void doRun();
	}
	
	public void startSession(){
		// deal with application recording initialization
		if(isRecordingSession()){
			
			// create listener that broadcasts UI events to main Workbench
			if(_socketListener==null)
				_socketListener = new SocketStreamingListener();
			
			// start listening to meta events broadcasted from main Workbench
			if(_metaController==null)
				_metaController = new SessionEventController("Meta Event Controller Thread");
			_metaController.start();
			
			// asynchronously notify main Workbench on which port it listens
			new Thread(){
				public void run() {
					// sleep for one second
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					// then notify the main Workbench
					_socketListener.notifyControllerStart(_metaController.getPort());
				}
			}.start();
		}else{
			// not a recording session and is a debug mode
			if(isInDebugMode()){
				// start the workbench server to be reused many times in launch configuration
				workbenchServer = new WorkbenchEventController();
				workbenchServer.start();
			}
		}
	}
	
	/**
	 * Install special bundles provided in system property
	 * @param context
	 * @throws BundleException
	 */
	private void installSpecialBundles(BundleContext context) {
		String bundles = System.getProperty(INSTALL_BUNDLES_SYS_PROPERTY);
		if(bundles==null)
			return;
		String[] array = bundles.split("[,]");
		for (int i = 0; i < array.length; i++) {
			String bundleReference = array[i];
			try {
				Bundle bundle = context.installBundle(bundleReference);
				if(bundle!=null){
					// workaround to force bundle to be resolved 
					try {
						bundle.loadClass("does.not.matter.what.class.to.load");
					} catch (ClassNotFoundException e1) {
					}
				}else{
					throw new BundleException("Bundle "+bundleReference+" was not installed");
				}
				Tracer.trace(PLUGIN_ID+"/trace", "Dynamically installed and resolved bundle: "+bundle.getSymbolicName()+".");
			} catch (BundleException e) {
				Logger.log("Cannot install the bundle "+bundleReference, e);
			}
		}
	}
	
	public static boolean isInDebugMode(){
		String dmode = System.getProperty("windowtester.debug.mode");
		return dmode!=null && dmode.equals("true");
	}
	
	public WorkbenchEventController getWorkbechController(){
		return workbenchServer;
	}

	/**
	 * Send this event over the wire
	 * @param event - the event to send
	 */
	public static void send(ISemanticEvent event, int port) {
		if (event == null || !inRecording)
			return; //ignore null events
		
		//handleMetaEvents(event);
		
		Socket _socket = null;
		try {
			_socket = new Socket(InetAddress.getByName("localhost"), port);
			ObjectOutputStream _out = new ObjectOutputStream(_socket.getOutputStream());
			_out.writeObject(event);
			_out.close();
		} catch (IOException e) {
			Logger.log("An error occured in sending a semantic event message", e);
		}
	}
}
