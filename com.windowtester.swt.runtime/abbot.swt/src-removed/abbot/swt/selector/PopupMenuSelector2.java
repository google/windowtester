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
package com.windowtester.runtime.swt.internal.selector;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector.ClickCenter;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector.ClickLocation;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector.ClickOffset;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector.ClickTopLeft;

public class PopupMenuSelector2 extends BasicWidgetSelector {

//	private static final int CLICK_INTERVAL = 250;
//	private static final int MAX_CLICK_RETRIES = 5;
	//just guesses...
	static final int MAX_FIND_RETRIES = 7;
	static final int FIND_RETRY_INTERVAL = 1000;
	
    public static final String DEFAULT_CASCADING_MENUITEM_SEPARATOR = "/";
//    private final Display _display;
    
    public PopupMenuSelector2()
    {
       this(Display.getDefault()); 
    }
    
    public PopupMenuSelector2(Display display)
    {
//        _display = display;
    }
    
//    private void click(MenuItem item)
//    {
//    	if (OS.isOSX())
//    		new MenuItemSelector().mouseMove(item);   //selection occurs on last item with a key press		
//    	else
//    		new MenuItemSelector().click(item);
//    }

    /**
     * Provide some logic to determine a click location, if one was
     * not provided by the caller of runPopup.
     * 
     * @param widget the widget used to determine the ClickLocation
     * @return The ClickLocation for the widget
     */
    private ClickLocation getDefaultClickLocation(Widget widget)
    {
        ClickLocation location = null;
        if (widget instanceof TreeItem) 
        {
            location = new ClickTopLeft(widget); //TreeItem are clicked top left, b/c they might have very long text, that is scrolled.
        } 
        else
        {
            location = new ClickCenter(widget); //The center seems ok for most widgets.
        }
        return location;
    }

//    private void invokePopup(ClickLocation clickLocation)
//    {
//        //assert clickLocation != null : "clickLocation was null.";
////        log("Invoking at ClickLocation: " + clickLocation);
//        Point p = clickLocation.getClickLocation();
//        
////        /*
////         * We tried using abbot's mousePress functionality, but it failed to work
////         * consistently with on linux. The Event approach was taken from
////         * eclipse'st swt tests.
////         */
////        
////        Event mouseMoveEvent = new Event();
////        // Provisioning for possibly remapped keys here:
////        mouseMoveEvent.button = MouseConfig.SECONDARY_BUTTON;
////        mouseMoveEvent.type = SWT.MouseMove;
////        mouseMoveEvent.x = p.x;
////        mouseMoveEvent.y = p.y;
////        
////        Event mouseDownEvent = new Event();
////        mouseDownEvent.button = MouseConfig.SECONDARY_BUTTON;
////        mouseDownEvent.type = SWT.MouseDown;
////        mouseDownEvent.x = p.x;
////        mouseDownEvent.y = p.y;
////
////        Event mouseUpEvent = new Event();
////        mouseUpEvent.button = MouseConfig.SECONDARY_BUTTON;
////        mouseUpEvent.type = SWT.MouseUp;
////        mouseUpEvent.x = p.x;
////        mouseUpEvent.y = p.y;
////		new SWTPushEventOperation(mouseMoveEvent).execute();
////        
////        /*
////         *  Post events
////         */
////        
////		waitForIdle(_display);
////        pauseCurrentThread(200);
////		new SWTPushEventOperation(mouseDownEvent).execute();
////
////        // [author=Dan] If there is too much time between the mouse down and mouse up
////        // then Linux closes the menu on the mouse up
////		if (!Platform.isLinux())
////        	pauseCurrentThread(200);
////		new SWTPushEventOperation(mouseUpEvent).execute();
////		waitForIdle(_display);
////        pauseCurrentThread(200);
////        
////        if (!menuIsOpen()) {
////        	new SWTPushEventOperation(mouseDownEvent).execute();
////			pauseCurrentThread(200);
////			new SWTPushEventOperation(mouseUpEvent).execute();
////			waitForIdle(_display);
////            pauseCurrentThread(200);
////
////            //now we give up
////        	if (!menuIsOpen())
////        		throw new RuntimeException("Failed to open context menu.");        	
////        }
//		new SWTMouseOperation(WT.BUTTON3).at(new SWTDisplayLocation().offset(p)).execute();
////		pauseCurrentThread(200);
//    }

//	private boolean menuIsOpen() {
//		return MenuWatcher.getInstance(_display).isMenuOpen();
//	}
    
//    private void log(String msg)
//    {
//        //System.out.println(msg);
//    }
    
    /**
     * Right click a Widget to invoke a popup/context menu, and then
     * click the menu item. 
     * 
     * @param menuOwner
     * @param widgetToRightClick
     * @param menuItemPath - this may be the text for a single menu item, or a path (delimited by "/") 
     *                       of menu items for cascading menus.
     */
    public Widget runPopup(Control menuOwner, Widget widgetToRightClick, String menuItemPath)  throws WidgetNotFoundException, MultipleWidgetsFoundException {
        return runPopup(menuOwner, widgetToRightClick, menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR);
    }
    
    public Widget runPopup(Control menuOwner, Widget widgetToRightClick, int x, int y, String menuItemPath)  throws WidgetNotFoundException, MultipleWidgetsFoundException {
        return runPopup(menuOwner, widgetToRightClick, new ClickOffset(widgetToRightClick, x, y), menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR, null);
	}
    
    public Widget runPopup(Control menuOwner, Widget widgetToRightClick, String menuItemPath, String pathDelimiter)  throws WidgetNotFoundException, MultipleWidgetsFoundException {
        return runPopup(menuOwner, widgetToRightClick, null, menuItemPath, pathDelimiter, null);
    }
    
    public Widget runPopup(Control menuOwner, ClickLocation clickLocation, String menuItemPath) throws WidgetNotFoundException, MultipleWidgetsFoundException
    {
        return runPopup(menuOwner, clickLocation, menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR, null);
    }

    private Widget runPopup(Control menuOwner, Widget widgetToRightClick, ClickLocation clickLocation, String menuItemPath, String pathDelimiter, List verificationList) throws WidgetNotFoundException, MultipleWidgetsFoundException
    {
        if (clickLocation == null) 
        {
            clickLocation = getDefaultClickLocation(widgetToRightClick);
        }
        return runPopup(menuOwner, clickLocation, menuItemPath, pathDelimiter, verificationList);
    }
    
    private Widget runPopup(final Control menuOwner, final ClickLocation clickLocation, final String menuItemPath, String pathDelimiter, final List verificationList) throws WidgetNotFoundException, MultipleWidgetsFoundException
	{
    	return new SWTMenuSelector().contextClick(clickLocation.asSWTLocation(), false, menuItemPath).getWidget();

		//    	int attempts = 0;
//    	while (true) {
//	    	new SystemEventMonitor(menuOwner, SWT.MenuDetect){
//				public void syncExecEvents() {
//					invokePopup(clickLocation);
//				}
//			}.run();
    	
//			Widget item = clickItem(menuOwner, menuItemPath);
//			if (item != null)
//				return item;

			// [author=Dan] On Linux, when the system is busy, the menu select can fail

//			attempts++;
//			boolean isActive = isMenuShellActive(menuOwner);
//			String errMsg = isActive ? " arm context menu failed" : " menu shell no longer active";
//			ScreenCapture.createScreenCapture(TestMonitor.getInstance().getCurrentTestCaseID() + errMsg);
//			errMsg = "Failed to select context menu: " + menuItemPath + "\n" + errMsg;
//			Logger.logStackTrace(errMsg);
			
			// [author=Dan] Unknown if this works on any platform other than Linux
			
//			if (!Platform.isLinux())
//				throw new WidgetNotFoundException(errMsg);
			
			// [author=Dan] Ensure that the correct shell is active, let the system settle, and try again
			
//			if (attempts < 3) {
//				pauseCurrentThread(attempts * 5000);
//				// Attempt to close any open menu
//				if (menuIsOpen()) {
//					Logger.log("Attempting to close open menu with Esc key.");
//					keyClick(WT.ESC);
//				}
//				pauseCurrentThread(attempts * 5000);
//				if (!isActive) {
//					Logger.log("Forcing menu owner's shell to be active.");
//					forceMenuShellActive(menuOwner);
//				}
//				pauseCurrentThread(attempts * 5000);
//			}
//			
//			if (attempts > 3 || menuOwner.isDisposed())
//				throw new WidgetNotFoundException(errMsg);
//    	}
    }

//	private Widget contextClick(SWTLocation location, final String menuItemPath) throws WidgetNotFoundException,
//		MultipleWidgetsFoundException
//	{
//		MenuSelector menuSelector = new MenuSelector();
//		
//		try {
//			String[] elements = PathStringTokenizerUtil.tokenize(menuItemPath);
//			SWTMenuOperation op = new SWTMenuOperation();
//			MenuItem item = null;
//			op.popup(location).execute();
//			for (int i = 0; i < elements.length; i++) {
//				item = getNextMenuItem(op.getMenuItems(), elements[i]);
//				op.click(item).execute();
//			}
//			return item;
//		}
//		catch (Exception e) {
//			ScreenCapture.createScreenCapture();
//			// Close any open menus
//			for (int i= 0; i <= 5; ++i)
//				new SWTKeyOperation().keyCode(WT.ESC).execute();
//			// Rethrow any exceptions
//			if (e instanceof WidgetNotFoundException)
//				throw (WidgetNotFoundException) e;
//			if (e instanceof MultipleWidgetsFoundException)
//				throw (MultipleWidgetsFoundException) e;
//			if (e instanceof RuntimeException)
//				throw (RuntimeException) e;
//			throw new RuntimeException(e);
//		}
//	}

//	private MenuItem getNextMenuItem(MenuItem[] items, String itemText) throws MultipleWidgetsFoundException,
//		WidgetNotFoundException
//	{
//		StringBuffer buffer = new StringBuffer();
//    	int firstAppearance = -1;
//    	buffer.append("Menu Item [");
//    	buffer.append(itemText);
//    	buffer.append("] NOT FOUND in item list:\n");
//    	for(int i = 0; i < items.length; i++)
//    	{
//    		String text = new MenuItemTester().getText(items[i]);
//
//    		if(stringsMatch(itemText, text))
//    		{
//    			if (firstAppearance < 0)
//    				firstAppearance = i;
//        		if(firstAppearance != i) {
////        	    	ScreenCapture.createScreenCapture();
////        			handleMenuClose();
//        			throw new MultipleWidgetsFoundException("Multiple items found: " + itemText + "\n");
//        		}
//    		}
//
//    		buffer.append("\n");
//    		buffer.append(text);
//    	}
//    	if (firstAppearance >= 0)
//    		return items[firstAppearance];
////    	ScreenCapture.createScreenCapture();
////    	handleMenuClose();
//    	throw new WidgetNotFoundException(buffer.toString());
//	}

    /**
	 * @since 3.8.1
	 */
//	private MenuItem[] getItems(final Menu parent) {
//		/*
//		 * In OSX, items can take a bit to appear...
//		 */
//		if (OS.isOSX()) {
//			return (MenuItem[]) RetrySupport
//					.retryUntilArrayResultIsNonEmpty(new RunnableWithResult() {
//						public Object runWithResult() {
//							return new MenuTester().getItems(parent);
//						}
//					});
//		}
//		return new MenuTester().getItems(parent);
//	}

//	private boolean stringsMatch(String expected, String actual)
//    {
//        if(expected == null || actual == null)
//            return false;
//        String trimmed = NameOrLabelMatcher.trimMenuText(actual);
// 		if (expected.equals(trimmed))
//			return true;
//        return StringComparator.matches(actual, expected);
//    }
    
}
