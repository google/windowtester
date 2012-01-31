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
package com.windowtester.codegen.debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import com.windowtester.codegen.CodeGenPlugin;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.swt.internal.preferences.WindowTesterSupport;

/**
 * A data structure holding recording information used for debugging purposes. Immediately
 * before launching a recording, call {@link #newRecording()} to instantiate a new
 * instance of this class, then use {@link #getInfo()} to cache the recording information
 * during the launch operation. If the recording goes astray, then a debug recording
 * dialog can be shown to the user with the information cached in this structure.
 */
public class DebugRecordingInfo
{
	private static DebugRecordingInfo mostRecentInfo;
	private IPath workspaceLocation;
	private String[] recorderClasspath;
	private String[] bundleClasspath;
	private ISemanticEvent[] semanticEvents;
	private String recLogContent;
	private String devLogContent;
	static final String RECORDER_CLASSPATH = "Recorder Classpath";
	static final String PROJECT_CLASSPATH = "Project Classpath";

	////////////////////////////////////////////////////////////////////////////
	//
	// Construction
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Create and cache a new recording debug info structure
	 */
	public static void newRecording() {
		mostRecentInfo = new DebugRecordingInfo();
	}

	/**
	 * Answer the information for the most recent recording
	 * 
	 * @return the most recent information or <code>null</code> if none
	 */
	public static DebugRecordingInfo getInfo() {
		return mostRecentInfo;
	}

	private DebugRecordingInfo() {
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////

	public IPath getWorkspaceLocation() {
		return workspaceLocation;
	}

	public void setWorkspaceLocation(IPath location) {
		this.workspaceLocation = location;
	}

	public String[] getRecorderClasspath() {
		return recorderClasspath;
	}

	public void setRecorderClasspath(String[] classpath) {
		this.recorderClasspath = classpath;
	}

	public String[] getBundleClasspath() {
		return bundleClasspath;
	}

	public void setBundleClasspath(String[] classpath) {
		this.bundleClasspath = classpath;
	}

	public Object[] getClasspathNames() {
		Collection result = new ArrayList();
		if (recorderClasspath != null)
			result.add(RECORDER_CLASSPATH);
		if (bundleClasspath != null)
			result.add(PROJECT_CLASSPATH);
		return (String[]) result.toArray(new String[result.size()]);
	}

	public Object[] getClasspath(Object parentElement) {
		if (parentElement == DebugRecordingInfo.RECORDER_CLASSPATH)
			return getRecorderClasspath();
		if (parentElement == DebugRecordingInfo.PROJECT_CLASSPATH)
			return getBundleClasspath();
		return null;
	}

	public ISemanticEvent[] getSemanticEvents() {
		return semanticEvents;
	}

	public void setSemanticEvents(ISemanticEvent[] semanticEvents) {
		this.semanticEvents = semanticEvents;
	}

	public void setSemanticEvents(Collection events) {
		setSemanticEvents((ISemanticEvent[]) (events != null ? events.toArray(new ISemanticEvent[events.size()]) : null));
	}

	/**
	 * Determine if the debug information should be shown to the user
	 * 
	 * @return <code>true</code> if it is recommended to display this information to the
	 *         user, else <code>false</code>
	 */
	public boolean shouldShow() {
		return semanticEvents == null || semanticEvents.length == 0;
	}
	
	public String getRecorderLogContent() {
		if (recLogContent == null)
			recLogContent = readLogContent(workspaceLocation);
		return recLogContent;
	}

	public String getDevelopmentLogContent() {
		if (devLogContent == null)
			devLogContent = readLogContent(ResourcesPlugin.getWorkspace().getRoot().getLocation());
		return devLogContent;
	}

	private static String readLogContent(IPath workspaceLocation) {
		if (workspaceLocation == null)
			return "unspecified workspace location";
		File logFile = workspaceLocation.append(".metadata").append(".log").toFile();
		if (!logFile.exists())
			return "no log file found:\n" + logFile.getPath();
		StringBuffer buf = new StringBuffer(2000);
		Reader logReader;
		try {
			logReader = new BufferedReader(new FileReader(logFile));
		}
		catch (FileNotFoundException e) {
			return "failed to open log file found in recorder workspace:\n" + logFile.getPath() + "\n" + e;
		}
		char[] cbuf = new char[1024];
		try {
			logReader.skip(Math.max(0, logFile.length() - 50000));
			while (true) {
				int count = logReader.read(cbuf);
				if (count == -1)
					break;
				buf.append(cbuf, 0, count);
			}
		}
		catch (IOException e) {
			buf.append("\nException while reading recorder log file\n");
			buf.append(e);
		}
		finally {
			try {
				logReader.close();
			}
			catch (IOException e) {
				buf.append("\nfailed to close recorder log reader\n");
				buf.append(e);
			}
		}
		String content = buf.toString();
		int index = content.lastIndexOf(System.getProperty("line.separator") + "!SESSION ");
		if (index > 0)
			content = content.substring(index);
		return content;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Operations
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Copy the receiver's content to the clipboard
	 */
	public void copyToClipboard() {
		CodeGenPlugin.getDefault().getClipboard().setContents(new Object[]{
			convertToText()
		}, new Transfer[]{
			TextTransfer.getInstance()
		});
	}

	/**
	 * Convert the receiver's content into a single blob of text
	 * 
	 * @return the text (not <code>null</code>)
	 */
	private String convertToText() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		printInfo(writer);
		return stringWriter.toString();
	}

	/**
	 * Append support information to the specified buffer. Subclasses should
	 * override {@link #printSupportInfo(PrintWriter)} rather than this method to provide
	 * additional information
	 * 
	 * @param writer the print writer to which information is appended
	 */
	private void printInfo(PrintWriter writer) {
		WindowTesterSupport.getInstance().printInfo(writer);
		writer.println();
		writer.println("****************** Recording Information");
		printClasspath(writer, RECORDER_CLASSPATH, recorderClasspath);
		printClasspath(writer, PROJECT_CLASSPATH, bundleClasspath);
		writer.println();
		writer.println("Recorder log:");
		writer.println(getRecorderLogContent());
		writer.println();
		writer.println("****************** Development Information");
		writer.println();
		writer.println("Development log:");
		writer.println(getDevelopmentLogContent());
	}

	private static void printClasspath(PrintWriter writer, String name, String[] classpath) {
		writer.println();
		writer.print(name);
		writer.println(":");
		if (classpath != null) {
			for (int i = 0; i < classpath.length; i++) {
				writer.print("  ");
				writer.println(classpath[i]);
			}
		}
	}
}
