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
package com.windowtester.swt.macosx.cocoa;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Native methods for Cocoa, 32-bit.
 * @author messick
 */
@SuppressWarnings("restriction")
public class MacCocoa32 extends MacCocoa {
	static {
		loadNativeLibrary();
		fixMenuBarFontSize();
	}

	private static void loadNativeLibrary() {
		System.loadLibrary("wt-cocoa");
	}

	int getMenuHandle(Menu menu) {
		return getID(menu, "nsMenu");
	}

	/**
	 * Return the menu bar height.
	 */
	int getMenuBarHeight() {
		return _getMenuBarHeight();
	}

	/**
	 * @return the Cocoa id field.
	 */
	static int getID(Widget control, String string) {
		Object fieldObject = Util.getFieldObject(control, string);
		return Util.getFieldInt(fieldObject, "id");
	}

	/**
	 * JNI function that returns the menu bar height.
	 */
	private static native int _getMenuBarHeight();

	int getCarbonMenuHandle(Menu menu) {
		int nsMenuId = getID(menu, "nsMenu");
		return getCarbonMenuHandle(nsMenuId);
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

	static final native int getCarbonMenuHandle(int menuId);

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

	static final native int AXUIElementCreateWithHIObjectAndIdentifier(int inHIObject, long inIdentifier);
	static final native int CFStringCreateWithCharacters(int alloc, char[] chars, int numChars);
	static final native int CFArrayGetValueAtIndex(int theArray, int idx); 

	/**
	 * Utility method to convert a java String to a CFString.
	 * NOTE: Caller is responsible for releasing the returned value.
	 * 
	 * @param string the string to convert
	 * @return a CFString reference
	 */
	static int stringToCFStringRef(String string) {
		
		/* $codepro.preprocessor.if version > 3.0 $ */
		char [] buffer = new char [string.length ()];
		string.getChars (0, buffer.length, buffer, 0);
		return CFStringCreateWithCharacters (kCFAllocatorDefault, buffer, buffer.length);
		
		/* $codepro.preprocessor.elseif version <= 3.0 $
		throw new RuntimeException("Internal error: Eclipse 3.0 on Mac not supported");
		$codepro.preprocessor.endif $ */
	}

	/**
	 * Given a MenuItem, return its bounding box.
	 * 
	 * @param item the menu item
	 * @return Rectangle of item (in global coordinates), or null if something didn't work
	 */
	public Rectangle getMenuItemBounds(MenuItem item) {		
		/* $codepro.preprocessor.if version > 3.1 $ */
		if (item == null)
			return null;
		Menu parent = item.getParent();
		int index = parent.indexOf(item);
		if (isMenuBarItem(item)) {
			List bounds = new ArrayList();
			getMenuBarVisualData(parent, bounds);
			return (Rectangle) bounds.get(index);
		}
		int axError;
		boolean axReturnCode;
		
		// Find the OS handle of the menu. It's private in Menu, so we need this workaround.
		int menuRef = getCarbonMenuHandle(parent);

		// Get the AX element for the menu
		int[] children = new int[1];
		int axMenuRef = AXUIElementCreateWithHIObjectAndIdentifier(menuRef, (long) 0);
		int cfChildren = stringToCFStringRef(kAXChildrenAttribute);
		axError = AXUIElementCopyAttributeValue(axMenuRef, cfChildren, children);
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
		int menuItem = CFArrayGetValueAtIndex(children[0], index);

		CGPoint position = new CGPoint();
		CGSize size = new CGSize();

		// Get the position
		int cfPosition = stringToCFStringRef(kAXPositionAttribute);
		int[] positionRef = new int[1];
		axError = AXUIElementCopyAttributeValue(menuItem, cfPosition, positionRef);
		axReturnCode = AXValueGetValueCGPoint(positionRef[0], position);
		OS.CFRelease(positionRef[0]);
		OS.CFRelease(cfPosition);
		if (!axReturnCode) {
			System.out.println("Internal error: type mismatch in native code");
			return null;
		}

		// Get the size
		int cfSize = stringToCFStringRef(kAXSizeAttribute);
		int[] sizeRef = new int[1];
		axError = AXUIElementCopyAttributeValue(menuItem, cfSize, sizeRef);
		axReturnCode = AXValueGetValueCGSize(sizeRef[0], size);
		OS.CFRelease(sizeRef[0]);
		OS.CFRelease(cfSize);
		if (!axReturnCode) {
			System.out.println("Internal error: type mismatch in native code");
			return null;
		}

		Rectangle result = new Rectangle((int)position.x, (int)position.y, (int)size.width, (int)size.height);
		//System.out.println(result);
		return result;
		
		/* $codepro.preprocessor.elseif version <= 3.1 $
		throw new RuntimeException("Internal error: Eclipse 3.0 on Mac not supported");
		$codepro.preprocessor.endif $ */
	}

	private static class CGSize {
		public float width;
		public float height;
	}

	private static class CGPoint {
		public float x;
		public float y;
	}

}
