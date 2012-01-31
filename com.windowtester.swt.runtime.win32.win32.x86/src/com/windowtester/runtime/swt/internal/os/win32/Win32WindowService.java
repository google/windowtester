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
package com.windowtester.runtime.swt.internal.os.win32;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.accservice.core.AccessibleConstants;
import org.eclipse.actf.accservice.core.win32.msaa.InitializationException;
import org.eclipse.actf.accservice.core.win32.msaa.MsaaAccessibilityService;
import org.eclipse.actf.accservice.core.win32.msaa.MsaaAccessible;
import org.eclipse.actf.accservice.core.win32.msaa.MsaaLibraryManager;
import org.eclipse.actf.accservice.core.win32.msaa.MsaaWindowService;
import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.os.IAccessibleComponent;
import com.windowtester.runtime.swt.internal.os.IAccessibleWindow;
import com.windowtester.runtime.swt.internal.os.IWindowService;
import com.windowtester.runtime.swt.internal.os.InvalidComponentException;

/**
 * A window service for Win32.
 */
public class Win32WindowService implements IWindowService {

	private static MsaaWindowService msaaWindowService;
	private static MsaaAccessibilityService accService;
		
	static class AccessibleWindow implements IAccessibleWindow {

		private final String name;
		private boolean inProcess;
		private String role;
		private final MsaaAccessible element;
		
		public AccessibleWindow(MsaaAccessible element) throws InvalidComponentException {
			this.element = element;
			this.name = element.getAccessibleName();
		}
		
		/* (non-Javadoc)
		 * @see acc.spike.IAccessibleWindow#getName()
		 */
		public String getName() {
			return name;
		}

		
		public static AccessibleWindow forHandle(Integer windowHandle) {
			
			int pid = msaaWindowService.getProcessId(windowHandle);
			MsaaAccessible element;
			try {
				element = accService.createAccessibleElement(windowHandle, null);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			if (element == null)
				return null; //TODO: consider null object here...
			
			try {
				return new AccessibleWindow(element).inProcess(pid == getCurrentPID()).withRole(element.getAccessibleRole());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}

		private AccessibleWindow inProcess(boolean inProcess) {
			this.inProcess = inProcess;
			return this;
		}
		
		/* (non-Javadoc)
		 * @see acc.spike.IAccessibleWindow#inProcess()
		 */
		public boolean inProcess() {
			return inProcess;
		}
		
		public String getAccessibleRole() {
			return role;
		}
		
		public String getAccessibleName() throws InvalidComponentException {
			return getName();
		}
		
		public IAccessibleComponent[] getAccessibleChildren() throws InvalidComponentException {
			return element.getAccessibleChildren();
		}
		
		private AccessibleWindow withRole(String role) {
			this.role = role;
			return this;
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.os.IAccessibleWindow#close()
		 */
		public void close() {
			try {
				IAccessibleComponent title = MsaaAccessibleHelper.getTitleBar(element);
				if (title == null)
					return; // throw exception?
				IAccessibleComponent[] children = title.getAccessibleChildren();
				((MsaaAccessible)children[children.length-1]).doDefaultAction();
			} catch (InvalidComponentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public String toString() {
			return "Window: " + getName() + " " + " - in process: " + inProcess + ", with role: " + role;
		}
	}
		
	public Win32WindowService() throws InitializationException {
		MsaaLibraryManager.load();
		createMsaaWindowService();
		createMsaaAccService();
	}


	private static void createMsaaAccService() throws InitializationException {
		if (accService == null)
			accService = new MsaaAccessibilityService();
	}


	private static void createMsaaWindowService() throws InitializationException {
		if (msaaWindowService == null)
			msaaWindowService = new MsaaWindowService();
	}
	
	
	public Integer[] getTopLevelWindowHandles() {
		int[] hwnds = MsaaWindowService.internalGetWindowsList();
		if (hwnds == null) {
			hwnds = new int[0];
		}
		Integer[] objList = new Integer [hwnds.length];
		for (int i=0; i< hwnds.length; i++) {			
			objList[i]=  new Integer( hwnds[i]);
		}
		
		return objList;
	}
	
	public IAccessibleWindow[] getTopLevelWindows() {
		Integer[] handles = getTopLevelWindowHandles();
		List windows = new ArrayList();
		for (int i = 0; i < handles.length; i++) {
			AccessibleWindow window = AccessibleWindow.forHandle(handles[i]);
			if (window != null)
				windows.add(window);
		}
		return (IAccessibleWindow[]) windows.toArray(new IAccessibleWindow[]{});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.os.IWindowService#getDialogs()
	 */
	public IAccessibleWindow[] getNativeDialogs() {
		IAccessibleWindow[] windows = getTopLevelWindows();
		List dialogs = new ArrayList();
		for (int i = 0; i < windows.length; i++) {
			IAccessibleWindow window = windows[i];
			if (isNativeDialog(window))
				dialogs.add(window);
		}
		return (IAccessibleWindow[]) dialogs.toArray(new IAccessibleWindow[]{});	
	}
	
	private boolean isNativeDialog(IAccessibleWindow window) {
		return window != null && window.inProcess() && hasDialogRole(window) && inNativeContext();
	}


	private boolean hasDialogRole(IAccessibleWindow window) {
		try {
			return AccessibleConstants.ROLE_DIALOG.equals(window.getAccessibleRole());
		} catch (InvalidComponentException e) {
			return false;
		}
	}


	private boolean inNativeContext() {
		/*
		 * This may not be the best way to do this...  but it appears that we can detect the native case
		 * when the display returns no active shells...
		 */
		return ShellFinder.getActiveShell(Display.getDefault()) == null;
	}


	public static int getCurrentPID() {
		return msaaWindowService.getCurrentProcessId();
	}
	
	public static int getPID(Object windowHandle) {
		 return msaaWindowService.getProcessId(windowHandle);
	}
	
}
