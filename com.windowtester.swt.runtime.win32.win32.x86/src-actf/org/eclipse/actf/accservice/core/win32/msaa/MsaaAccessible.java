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

package org.eclipse.actf.accservice.core.win32.msaa;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import com.windowtester.runtime.swt.internal.os.IAccessibleComponent;
import com.windowtester.runtime.swt.internal.os.InvalidComponentException;



/**
 * implementation of <code>IAccessibleElement</code> for GUI controls that implement Microsoft Active Accessibility (MSAA) interfaces.
 * 
 * <p>This class is a wrapper for an IAccessible pointer, a pointer that Provides
 * access to a native Windows object that provides assistive technologies (ATs) with properties of GUI components 
 * that allow the AT to offer an alternative interface to the control. This class relies upon actf-msaa.dll
 * for most of its implementation. The documentation for the Microsoft COM
 * library and, in particular, for MSAA will be helpful.
 * 
 * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/msaa/msaastart_9w2t.asp">The MSDN MSAA Documentation</a>
 * @author Mike Squillace, Barry Feigenbaum
 */
public class MsaaAccessible implements IAccessibleComponent
{

	protected static final String SWT_CTRL_TYPENAME = "org.eclipse.swt.widgets.Control";
	protected static final String SWT_WIDGET_TYPENAME = "org.eclipse.swt.widgets.Widget";
	protected static final String SWT_ACC_TYPENAME = "org.eclipse.swt.accessibility.ACC";
	public static int childId_self = -1; // ACC.CHILDID_SELF
	
	protected static boolean highLightEnabled =false;
	
	protected int accRef; // pointer to helper class that wraps an MSAA IAccessible 
	protected int hwnd = -1; // native window handle
	protected int childId;
	protected Object element;
	protected int indexInParent = -1;
	
		/**
	 * wrap the given object as an ACTF <code>IAccessibleElement</code>. The 
	 * ACTF engine will invoke this constructor using a registered adaptor factory. Clients do not 
	 * typically call this constructor.
	 * 
	 * <p>Note: To create a MsaaAccessible from a handle for an SWT control, use a child id of
	 * <code>ACC.CHILDID_SELF</code>.
	 * 
	 * @param hwnd -
	 *            window handle for an SWT control
	 * @param childID -
	 *            child ID (if any)
	 */
	public MsaaAccessible (int hwnd, int childID) {
		initFromHwnd(hwnd, childID);
	}


	/**
	 * create an MsaaAccessible element by utilizing the MSAA function <code>AccessibleObjectFromPoint</code>.
	 *  
	 * @param location - any location on the current display device
	 */
	public MsaaAccessible (Point location) {
		initFromPoint(location.x, location.y);
	}

	/**
	 * create a MsaaAccessible from an IAccessible pointer.
	 * 
	 * @param ref - pointer value
	 */
	public MsaaAccessible (int ref) {
		accRef = ref;
		try {
			MsaaAccessibilityService.internalCoInitialize();
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
		}catch (Throwable e) {
			hwnd = -1;
			accRef = 0;
		}
	}

	/** {@inheritDoc} */
	public Object element () {
		return element;
	}

	/**
	 * used by native code only. Clients should not call directly.
	 * @return ptr address for native object
	 */
	protected int internalRef () {
		return accRef;
	}

	public int getAccessibleAddress () {
		return internalGetAddress();
	}
	protected native int internalGetAddress();
	
	public int getWindowHandle () {
		return hwnd ;
	}
	
	/**
	 * get the handle value as a hex string
	 * 
	 * @return handle value as a hex string
	 */
	public String  getWindowHandleAsHex () {
		String id = Integer.toHexString(hwnd).toUpperCase();
		return "00000000".substring(0, 8 - id.length()) + id;
	}

	/**
	 * get the child ID for this MsaaAccessible, if any. Child IDs are associated
	 * with "simple" (i.e. non-IAccessible) children starting at 0. The child ID
	 * for a control (rather than any of its children) is
	 * <code>ACC.CHILDID_SELF</code>
	 * 
	 * @return child ID
	 */
	public int getChildId () {
		return childId;
	}

	/**
	 * tests whether or not this MsaaAccessible is in a valid state. Validity
	 * consists of:
	 * <p><ul>
	 * <li>the handle for the SWT control associated with this MsaaAccessible (if
	 * any) is a valid window handle
	 * <li>the native code, upon initialization, returned a valid pointer to
	 * the underlying MsaaAccessible object
	 * <li>the underlying element returned by <code>getElement()</code> (if any) is not disposed
	 * </ul>
	 *
	 * @throws InvalidComponentException
	 */
	public void checkIsValid () throws InvalidComponentException {
		boolean disposed = element() != null && isDisposed(element());
		if (accRef == 0 || disposed) {
				throw new InvalidComponentException("Invalid accessible element: hwnd=" + hwnd + ",ref=" + accRef + ",isDisposed:" + disposed); 
		}
	}

	protected boolean isDisposed (Object control) {
		boolean isDisposed = true;
		Class widgetClass;
		try {
			widgetClass = Class.forName(SWT_WIDGET_TYPENAME);
			if (control != null && widgetClass != null) {
				Method meth = widgetClass.getMethod("isDisposed", (Class[]) null);
				isDisposed = widgetClass.isAssignableFrom(control.getClass())
						&& ((Boolean) meth.invoke(control, (Object[]) null)).booleanValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDisposed;
	}

	public boolean equals (Object other) {
		boolean result = other != null && other instanceof MsaaAccessible;
		if (result) {
			MsaaAccessible accOther = (MsaaAccessible) other;
			try {
				String accName = this.getAccessibleName();
				String accRole = this.getAccessibleRole();
				String oAccName = accOther.getAccessibleName();
				String oAccRole = accOther.getAccessibleRole();
				result = !((accName == null && oAccName != null)
					|| (accRole == null && oAccRole != null));
				if (result) {
					result = 
						this.getWindowHandle() == accOther.getWindowHandle()
						  && this.getChildId() == accOther.getChildId()
						  && ((accName == null && oAccName == null) || accName.equals(oAccName))
						  && ((accRole == null && oAccRole == null) || accRole.equals(oAccRole))
						  && (this.getAccessibleLocation().equals(accOther.getAccessibleLocation()))
						  && (this.internalGetAccessibleState() == accOther.internalGetAccessibleState());
				}
			} catch (InvalidComponentException e) {
				result = false;
			}
		}
		return result;
	}

	public int hashCode () {
		return accRef % 10000;
	}

	protected void finalize () throws Throwable {
		dispose();
		accRef = 0;
		hwnd = -1;
		MsaaAccessibilityService.internalCoUnInitialize();
	}

	public String toString () {
		String str = null;
		try {
			str = getAccessibleRole() + ":" + getAccessibleName() + "[" + getAccessibleIndexInParent() + "]";
		} catch (InvalidComponentException e) {
			str = "<Exception>";
		}
		return str;
	}

	protected void initFromHwnd (int hwnd, int childID) {
		accRef = internalInitFromHwnd(hwnd, childID);
		if (accRef != 0) {
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
		}
	}

	protected void initFromPoint (int x, int y) {
		accRef = internalInitFromPoint(x, y);
		if (accRef > 0) {
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
		}
	}

	protected void initFromHtmlElement (int htmlElemRef) {
		accRef = internalInitFromHtmlElement(htmlElemRef);
		if (accRef > 0) {
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
		}
	}

	protected native int internalGetWindowHandle ();
	protected native int internalGetChildId ();

	protected native static int internalInitFromHwnd (int hwnd, int childID);
	protected native static int internalInitFromHtmlElement (int htmlElemRef);
	protected native static int internalInitFromPoint (int x, int y);

	/**
	 * dispose the native resources
	 * 
	 */
	protected void dispose () throws InvalidComponentException {
		checkIsValid();
		internalDispose();
	}
	protected native void internalDispose ();

	/**
	 * gets the class name for the given handle
	 * 
	 * @param hwnd -
	 *            window handle
	 * @return name of class
	 */
	public static String getClassNameFromHwnd (int hwnd) {
		return internalGetClassNameFromHwnd(hwnd);
	}
	protected native static String internalGetClassNameFromHwnd (int hwnd);

	/** {@inheritDoc} */
	public MsaaAccessible getAccessibleParent () throws InvalidComponentException {
		checkIsValid();
		MsaaAccessible parent = null;
		int accRef = internalGetAccessibleParent();
		if (accRef != 0) {
			parent = new MsaaAccessible(accRef);
		}
		if (parent != null) {
			//see if the parent is Ia2 and, if so, convert it
			try {
				parent = testAndConvertToIA2((MsaaAccessible) parent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		return parent;
	}
	protected native int internalGetAccessibleParent ();

	/**
	 * return the number of children. Note that children include either
	 * MsaaAccessible objects that represent MsaaAccessible wrappers of IAccessible
	 * pointers or MsaaAccessible objects that wrap "simple" children, children
	 * that share their properties with their IAccessible parent.
	 * 
	 * @return number of child MsaaAccessible objects
	 * @throws InvalidComponentException
	 */
	public int getAccessibleChildCount () throws InvalidComponentException {
		int res = 0;
		checkIsValid();
		res = internalGetAccessibleChildCount();
		return res;
	}
	protected native int internalGetAccessibleChildCount ();

	/**
	 * return the accessible that has the given index in its parent. Note that this is not the 
	 * same value as its childID, though children are typically numbered consecutively 
	 * starting with 0.
	 * 
	 * @param index index of desired child in parent
	 * @return child with the given index or <code>null</code> if 
	 * the index is invalid
	 */
	public MsaaAccessible getAccessibleChild (int index) throws InvalidComponentException {
		checkIsValid();
		int accRef = internalGetAccessibleChild(index);
		MsaaAccessible acc = null;
		if (accRef != 0) {
			acc = new MsaaAccessible(accRef);
		}
		
		if (acc != null) {
			try {
				acc = testAndConvertToIA2(acc);
			} catch (Exception e) {
			}
			acc.indexInParent = index;
		}

		return acc;
	}
	protected native int internalGetAccessibleChild (int childID);

	/** {@inheritDoc} */
	public int getAccessibleIndexInParent () throws InvalidComponentException {
		if (indexInParent == -1) {
			MsaaAccessible parent = getAccessibleParent();
			String accName = parent != null ? parent.getAccessibleName() : "";
			IAccessibleComponent[] elems = accName == null || !accName.equalsIgnoreCase("desktop")
				? parent.getAccessibleChildren() : new MsaaAccessible[0];
			for (int c = 0; indexInParent == -1 && c < elems.length; ++c) {
				if (elems[c].equals(this)) {
					indexInParent = c;
				}
			}
		}
		return indexInParent;
	}
	
	/**
	 * return all of the children of this MsaaAccessible. Note that children
	 * include either MsaaAccessible objects that represent MsaaAccessible wrappers of
	 * IAccessible pointers or MsaaAccessible objects that wrap "simple" children,
	 * children that share their properties with their IAccessible parent.
	 * 
	 * @return children of this MsaaAccessible
	 * @throws InvalidComponentException
	 */
	public IAccessibleComponent[]  getAccessibleChildren () throws InvalidComponentException {
		checkIsValid();
		ArrayList children = new ArrayList();
		int childCount = getAccessibleChildCount();
		
		if (childCount > 0) {
			int[] childRefs = internalGetAccessibleChildren();
			if (childRefs != null && childRefs.length > 0) {
				for (int i = 0; i < childRefs.length; i++) {
					if (childRefs[i] != 0) {
						MsaaAccessible acc = new MsaaAccessible(childRefs[i]);
						if (acc != null) {
							try {
								acc = testAndConvertToIA2(acc);
							} catch (Exception e) {
							}
							acc.indexInParent = i;
						}
						children.add(acc);
					}
				}
			}
		}
		
		return (MsaaAccessible[]) children.toArray(new MsaaAccessible[children.size()]);
	}
	protected native int[] internalGetAccessibleChildren ();
	
	/**
	 * returns whether or not this MsaaAccessible is a simple child. Simple
	 * children obtain their properties from their parent IAccessible object.
	 * The parent IAccessible object has a child ID of
	 * <code>ACC.CHILDID_SELF</code>.
	 * 
	 * @return <code>true</code> if this is a simple child, <code>false</code> otherwise
	 */
	protected boolean isSimpleChild () {
		return childId != childId_self;
	}

	/**
	 * returns whether the accessible object has the keyboard focus
	 * 
	 * @return whether or not this object has keyboard focus
	 * @throws InvalidComponentException
	 */
	public boolean hasFocus () throws InvalidComponentException {
		boolean res = false;
		checkIsValid();
		if (!isDisposed(element)) {
			res = internalHasFocus();
		}
		return res;
	}
	protected native boolean internalHasFocus ();

	/** {@inheritDoc} */
	public String getAccessibleName () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleName();
		return res;
	}
	protected native String internalGetAccessibleName ();

	/** {@inheritDoc} */
	public Object getAccessibleValue () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleValue();
		return res;
	}
	protected native String internalGetAccessibleValue ();

	/**
	 * return help info (usually a tool tip)
	 * 
	 * @return help or "" if no help is provided
	 * @throws InvalidComponentException
	 */
	public String getAccessibleHelp () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleHelp();
		return res;
	}
	protected native String internalGetAccessibleHelp ();

	/** {@inheritDoc} */
	public String getAccessibleKeyboardShortcut ()
		throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleKeyboardShortcut();
		return res;
	}
	protected native String internalGetAccessibleKeyboardShortcut ();

	/** {@inheritDoc} */
	public Object getAccessibleAction () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleAction();
		return res;
	}
	protected native String internalGetAccessibleAction ();

	/** {@inheritDoc} */
	public String getAccessibleDescription () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleDescription();
		return res;
	}
	protected native String internalGetAccessibleDescription ();

	/** {@inheritDoc} */
	public String getAccessibleRole () throws InvalidComponentException {
		checkIsValid();
		String res = null;
		int role = internalGetAccessibleRoleAsInt();
		
		res = Msaa.getMsaaActfRoleName(role);
		if (res == null || res.length() < 1) {
			try {
				res = (String) Class.forName("org.eclipse.actf.accservice.core.win32.ia2.IA2")
					.getMethod("getIA2ActfRoleName", new Class[] {long.class})
						.invoke(null, new Object[] {new Integer(role)});
			}catch (Exception e) {
			}
		}
		if(res==null || res.length() <1){
			res = internalGetAccessibleRole();
		}
		return res;
	}
	protected native int internalGetAccessibleRoleAsInt ();
	protected native String internalGetAccessibleRole();

	/** {@inheritDoc} */
	public Set getAccessibleState () throws InvalidComponentException {
		checkIsValid();
		int state = internalGetAccessibleState();
		return Msaa.getState(state);
	}
	protected native int internalGetAccessibleState ();

	/**
	 * get the list of selected accessibles. Currently this method will only
	 * return a single selected item, although MSAA supports multiple
	 * selection in some controls. If a control supports multiple selection and
	 * multiple selections are present, this method will return an empty array.
	 * 
	 * @return selections or empty array if no selection
	 * @throws InvalidComponentException
	 */
	public MsaaAccessible[] getAccessibleSelection () throws InvalidComponentException {
		// TODO support multiple selections in dll
		checkIsValid();
		int[] accRefs = internalGetAccessibleSelection();
		MsaaAccessible[] selections = new MsaaAccessible[accRefs != null ? accRefs.length : 0];
		if ((accRefs != null) && (accRefs.length > 0)) {
			for (int i = 0; i < selections.length; i++) {
				selections[i] = new MsaaAccessible(accRefs[i]);
			}
		}
		return selections;
	}
	protected native int[] internalGetAccessibleSelection ();

	/** {@inheritDoc} */
	public Rectangle getAccessibleLocation() throws InvalidComponentException {
		Rectangle pt = new Rectangle();
		checkIsValid();
		pt = internalGetAccessibleLocation();
		return pt;
	}
	protected native Rectangle internalGetAccessibleLocation ();

	public boolean drawRectangle() {
		return highLightEnabled  && internalDrawRectangle();
	}
	protected native boolean internalDrawRectangle();

	public boolean eraseRectangle(Rectangle drawRef) {
		if (drawRef == null) {
			return eraseDesktop();
		}
		int left = drawRef.x;
		int top = drawRef.y;
		int right = drawRef.x + drawRef.width;
		int bottom = drawRef.y + drawRef.height;
		return internalEraseRectangle(left, top, right, bottom);
	}

	public static boolean eraseDesktop() {
		return internalEraseDesktop();
	}
	
	protected native static boolean internalEraseDesktop();
	protected native boolean internalEraseRectangle(int left, int top, int right, int bottom);
	
	public static boolean isHighlightEnabled(){
		return highLightEnabled;
	}
	
	public static void setHighlightEnabled(boolean val){
		highLightEnabled = val;
	}
	
	public MsaaAccessible testAndConvertToIA2 (MsaaAccessible acc) throws Exception {
//		boolean isIA2 = false;
		//TODO: [pq]: this is disabled to reduce dependencies...
		MsaaAccessible result = acc;
//		isIA2 = ((Boolean) Class.forName("org.eclipse.actf.accservice.core.win32.ia2.IA2Accessible")
//			.getMethod("isIA2Accessible", new Class[] {MsaaAccessible.class})
//				.invoke(null, new Object[] {acc})).booleanValue();
//		if (isIA2){
//			if(acc.hwnd!=0) {
//			result = (MsaaAccessible) Class.forName("org.eclipse.actf.accservice.core.win32.ia2.IA2Accessible")
//				.getConstructor(new Class[] {int.class, int.class})
//					.newInstance(new Object[] {new Integer(acc.hwnd), new Integer(acc.childId)});
//			}
//		else{
//				int ia2Acc =0;
//				ia2Acc = ((Integer) Class.forName("org.eclipse.actf.accservice.core.win32.ia2.IA2Accessible")
//						.getMethod("getIA2FromIAcc", new Class[] {MsaaAccessible.class})
//						.invoke(null, new Object[] {acc})).intValue();
//				result = (MsaaAccessible) Class.forName("org.eclipse.actf.accservice.core.win32.ia2.IA2Accessible")
//				.getConstructor(new Class[] {int.class})
//					.newInstance(new Object[]{new Integer(ia2Acc)});
//			}
//		}
		return result;
	}
	
	public boolean doDefaultAction() throws InvalidComponentException{
		checkIsValid();
		return internalDoDefaultAction();
	}
	protected native boolean internalDoDefaultAction();
	
	public String getAccessibleHelpTopic () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleHelpTopic();
		return res;
	}
	protected native String internalGetAccessibleHelpTopic ();

	public boolean select(int flag) throws InvalidComponentException{
		checkIsValid();
		return internalSelect(flag);
	}
	protected native boolean internalSelect(int flag);
	
}