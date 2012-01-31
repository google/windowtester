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
package com.windowtester.runtime.swt.internal;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.SWTUIException;

/**
 * SWT utility methods.
 */
@SuppressWarnings("deprecation")
public class SWTUtils {

	public static <T> T safeExec(Callable<T> callable, T defaultValue) {
		try {
			return callable.call();
		} catch (SWTError error){
			if (!SWTUtils.isDisposed(error)){
				throw error;
			}
		} catch (SWTException ex){
			if (!SWTUtils.isDisposed(ex)){
				throw ex;
			}
		} catch (SWTUIException uie){
			if (!SWTUtils.isDisposedException(uie.getCause()))
				throw uie;
		} catch (Exception e) {
			throw new SWTUIException(e);
		}
		return defaultValue;
	}
	
	public static void safeExec(VoidCallable callable) {
		try {
			callable.call();
		} catch (SWTError error){
			if (!SWTUtils.isDisposed(error)){
				throw error;
			}
		} catch (SWTException ex){
			if (!SWTUtils.isDisposed(ex)){
				throw ex;
			}
		} catch (SWTUIException uie){
			if (!SWTUtils.isDisposedException(uie.getCause()))
				throw uie;
		} catch (Exception e) {
			throw new SWTUIException(e);
		}
	}
	
	
	
	public static boolean isUIThread(Display display) {
		return display.getThread() == Thread.currentThread();
	}
	
	public static boolean invalidDisplay(Display display) {
		return (display == null) || display.isDisposed();
	}

	public static boolean isDisposed(SWTError e) {
		return e.code == SWT.ERROR_WIDGET_DISPOSED;
	}
	
	public static boolean isDisposed(SWTException e) {
		return e.code == SWT.ERROR_WIDGET_DISPOSED;
	}
	
	/**
	 * Given this widget, find its control.
	 * 
	 * @param w
	 * @return
	 */
	public static Control getControl(final Widget c) {

		if (c instanceof Control)
			return (Control)c;
		
		return DisplayReference.getDefault().execute(new Callable<Control>() {
			public Control call() {
				if (c instanceof Caret)
					return ((Caret)c).getParent();
				if (c instanceof Menu)
					return ((Menu) c).getParent();
				if (c instanceof ScrollBar)
					return ((ScrollBar) c).getParent();
				if (c instanceof CoolItem)
					return ((CoolItem) c).getParent();
				if (c instanceof CTabItem)
					return ((CTabItem) c).getParent();
				if (c instanceof TabItem)
					return ((TabItem) c).getParent();
				if (c instanceof TableColumn)
					return ((TableColumn) c).getParent();
				if (c instanceof TableTreeItem)
					return ((TableTreeItem) c).getParent();
				if (c instanceof MenuItem) {
					return ((MenuItem) c).getParent().getParent();
				}
//				if (c instanceof TrayItem)
//					return ((TrayItem) c) ???
				if (c instanceof TabItem)
					return ((TabItem) c).getParent();
				if (c instanceof TableColumn)
					return ((TableColumn) c).getParent();
				if (c instanceof TableItem)
					return ((TableItem) c).getParent();
				if (c instanceof ToolItem)
					return ((ToolItem) c).getParent();
				if (c instanceof TreeItem)
					return ((TreeItem) c).getParent();
				if (c instanceof DragSource)
					return ((DragSource) c).getControl().getParent();
				if (c instanceof DropTarget)
					return ((DropTarget) c).getControl().getParent();
				return null;
			}
		});
	}

	public static boolean isDisposedException(Throwable e) {
		if (e instanceof SWTError)
			return isDisposed((SWTError) e);
		if (e instanceof SWTException)
			return isDisposed((SWTException) e);
		if (e instanceof SWTUIException)
			return isDisposedException(e.getCause());
		return false;
	}


	
	
}
