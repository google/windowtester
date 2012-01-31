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
package com.windowtester.swt.macosx.external;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Platform;
import org.eclipse.swt.internal.carbon.CGPoint;
import org.eclipse.swt.internal.carbon.OS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.windowtester.swt.platform.ext.macosx.MacExtensions;

/**
 * More stuff that ought to be in SWT's OS class
 * 
 * @author Eric Herrmann, Adobe Systems
 * @author Steve Messick
 */
@SuppressWarnings("restriction")
public class OSExt extends Platform implements MacExtensions {

	static {
		try {
			//some background on this lib can be found here:
			
			System.loadLibrary("swtext-carbon-3346");
		} catch ( Throwable t ) {
			t.printStackTrace();
		}
	}

	/**
	 * Returns the value of an accessibility object's attribute.
	 * 
	 * @param inUIElement The AXUIElementRef representing the accessibility object.
	 * @param attribute The attribute name.
	 * @param value On return, the value associated with the specified attribute. 
	 * @return 0 if no error
	 */
	static final native int AXUIElementCopyAttributeValue(int inUIElement, int attribute, int [] value);

	/**
	 * Convert an accessibility attribute value to a CGPoint.
	 * 
	 * @param valueRef the AXValueRef
	 * @param point the CGPoint to hold the value
	 * @return true for success, false if the valueRef is not of the proper type
	 */
	/* $codepro.preprocessor.if version > 3.0 $ */
	static final native boolean AXValueGetValueCGPoint(int valueRef, CGPoint point);
	/* $codepro.preprocessor.endif $ */

	/**
	 * Convert an accessibility attribute value to a CGSize.
	 * 
	 * @param valueRef the AXValueRef
	 * @param point the CGSize to hold the value
	 * @return true for success, false if the valueRef is not of the proper type
	 */
	static final native boolean AXValueGetValueCGSize(int valueRef, CGSize size);

	/**
	 * Return true if the accessibility API is enabled.
	 * 
	 * To enable it: open System Preferences, select Universal Access, then
	 * select "Enable access for assistive devices".
	 * @return true if the accessibility API is enabled
	 */
	static final native boolean AXAPIEnabled();

	/**
	 * Get the OS handle from a menu. This field is not public in SWT
	 * so we use reflection to access it.
	 * 
	 * @param menu the menu
	 * @return the menu handle as an int or 0 if something went wrong
	 */
	static final int SWTGetMenuHandle(Menu menu) {
		try {
			Field field = Menu.class.getDeclaredField("handle");
			field.setAccessible(true);
			return field.getInt(menu);
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * Utility method to convert a java String to a CFString.
	 * NOTE: Caller is responsible for releasing the returned value.
	 * 
	 * @param string the string to convert
	 * @return a CFString reference
	 */
	static int stringToCFStringRef(String string) {
		char [] buffer = new char [string.length ()];
		string.getChars (0, buffer.length, buffer, 0);
		return OS.CFStringCreateWithCharacters (OS.kCFAllocatorDefault, buffer, buffer.length);
	}

	/**
	 * Given a MenuItem, return its bounding box.
	 * 
	 * @param item the menu item
	 * @return Rectangle of item (in global coordinates), or null if something didn't work
	 */
	public Rectangle getMenuItemBounds(MenuItem item) {
		if (item == null)
			return null;
		Menu parent = item.getParent();
		int index = parent.indexOf(item);
		int axError;
		boolean axReturnCode;
		
		// Find the OS handle of the menu. It's private in Menu, so we need this workaround.
		int menuRef = OSExt.SWTGetMenuHandle(parent);

		// Get the AX element for the menu
		int[] children = new int[1];
		int axMenuRef = OS.AXUIElementCreateWithHIObjectAndIdentifier(menuRef, (long) 0);
		int cfChildren = OSExt.stringToCFStringRef(OS.kAXChildrenAttribute);
		axError = OSExt.AXUIElementCopyAttributeValue(axMenuRef, cfChildren, children);
		OS.CFRelease(cfChildren);
		OS.CFRelease(axMenuRef);

		if (axError != 0 || children[0] == 0) {
			System.out.println("Do you have 'System Preferences/Universal Access/Enable access for assistive devices' turned on?");
			return null;
		}
		// The Mac menu bar includes the Apple menu and the application menu, which are
		// not part of the SWT menu bar. If we're looking at the menu bar, increment
		// the index to skip those two menus. (Does menu bar visibility matter?)
		if ((parent.getStyle() & SWT.BAR) != 0)
			index += 2;
		int menuItem = OS.CFArrayGetValueAtIndex(children[0], index);

		CGPoint position = new CGPoint();
		CGSize size = new CGSize();

		// Get the position
		int cfPosition = OSExt.stringToCFStringRef(OS.kAXPositionAttribute);
		int[] positionRef = new int[1];
		axError = OSExt.AXUIElementCopyAttributeValue(menuItem, cfPosition, positionRef);
		axReturnCode = OSExt.AXValueGetValueCGPoint(positionRef[0], position);
		OS.CFRelease(positionRef[0]);
		OS.CFRelease(cfPosition);
		if (!axReturnCode) {
			System.out.println("Internal error: type mismatch in native code");
			return null;
		}

		// Get the size
		int cfSize = OSExt.stringToCFStringRef(OS.kAXSizeAttribute);
		int[] sizeRef = new int[1];
		axError = OSExt.AXUIElementCopyAttributeValue(menuItem, cfSize, sizeRef);
		axReturnCode = OSExt.AXValueGetValueCGSize(sizeRef[0], size);
		OS.CFRelease(sizeRef[0]);
		OS.CFRelease(cfSize);
		if (!axReturnCode) {
			System.out.println("Internal error: type mismatch in native code");
			return null;
		}

		Rectangle result = new Rectangle((int)position.x, (int)position.y, (int)size.width, (int)size.height);
		//System.out.println(result);
		return result;
	}

	/**
	 * Given a TabItem, return its bounding box.
	 * 
	 * @param item the tab item
	 * @return Rectangle of item (in global coordinates), or null if something didn't work
	 */
	public Rectangle getTabItemBounds(TabItem item) {
		if (item == null)
			return null;
		TabFolder parent = item.getParent();

		// Find the index of the tab
		int index;
		boolean found = false;
		for (index = 0; index < parent.getItemCount(); index++) {
			if (parent.getItem(index) == item) {
				found = true;
				break;
			}
		}
		if (!found)
			return null;
		index += 1; // index=0 represents the page; tabs start at 1 (see Apple docs)

		CGPoint position = new CGPoint();
		CGSize size = new CGSize();
		int axTabControlRef = 0;
		try {
			// Get the AX element for the tab item
			axTabControlRef = OS.AXUIElementCreateWithHIObjectAndIdentifier(parent.handle, (long) index);
			@SuppressWarnings("unused")
			int axError;
			boolean axReturnCode;
	
			// Get the position
			int cfPosition = OSExt.stringToCFStringRef(OS.kAXPositionAttribute);
			int[] positionRef = new int[1];
			axError = OSExt.AXUIElementCopyAttributeValue(axTabControlRef, cfPosition, positionRef);
			axReturnCode = OSExt.AXValueGetValueCGPoint(positionRef[0], position);
			OS.CFRelease(positionRef[0]);
			OS.CFRelease(cfPosition);
			if (!axReturnCode) {
				System.out.println("Internal error: type mismatch in native code");
				return null;
			}
	
			// Get the size
			int cfSize = OSExt.stringToCFStringRef(OS.kAXSizeAttribute);
			int[] sizeRef = new int[1];
			axError = OSExt.AXUIElementCopyAttributeValue(axTabControlRef, cfSize, sizeRef);
			axReturnCode = OSExt.AXValueGetValueCGSize(sizeRef[0], size);
			OS.CFRelease(sizeRef[0]);
			OS.CFRelease(cfSize);
			if (!axReturnCode) {
				System.out.println("Internal error: type mismatch in native code");
				return null;
			}
		} finally {
			if (axTabControlRef != 0)
				OS.CFRelease(axTabControlRef);
		}
		Rectangle result = new Rectangle((int)position.x, (int)position.y, (int)size.width, (int)size.height);
		return result;
	}

	/**
	 * Return true if the accessibility API is enabled.
	 * 
	 * To enable it: open System Preferences, select Universal Access, then
	 * select "Enable access for assistive devices".
	 * @return true if the accessibility API is enabled
	 */
	public boolean isAXAPIEnabled() {
		return AXAPIEnabled();
	}
}
