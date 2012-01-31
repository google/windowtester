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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.WaitTimedOutError;
import abbot.finder.swt.TestHierarchy;
import abbot.finder.swt.WidgetSearchException;
import abbot.script.Condition;
import abbot.tester.swt.ControlTester;
import abbot.tester.swt.MenuItemTester;
import abbot.tester.swt.MenuTester;
import abbot.tester.swt.Robot;
import abbot.tester.swt.WidgetTester;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.operation.SWTDisplayLocation;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.StringComparator;
import com.windowtester.runtime.util.TestMonitor;
import com.windowtester.runtime.swt.internal.settings.TestSettings;
import com.windowtester.swt.util.PathStringTokenizer;

/**
 * Selector helper for context menus.
 * 
 * @author Phil Quitslund
 */
public class PopupMenuSelector extends BasicWidgetSelector {
	
    public static final String DEFAULT_CASCADING_MENUITEM_SEPARATOR = "/";
    /*
     * Now in TestSettings
     */
    //private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 10; //wait for single Menu to be visible, or MenuItem to be enabled.
    private static final int DEFAULT_WAIT_INTERVAL_MILLISECONDS = 500;
    private static final int DEFAULT_USER_DELAY = 500;
    private static final int LINUX_DELAY = 1000;
    private static final int SECOND = 1000;

    private static final String MESSAGE_PREFIX = "PopupMenuSelector2: ";

//    private final Display _display;
    
    public PopupMenuSelector()
    {
       this(Display.getDefault()); 
    }
    
    public PopupMenuSelector(Display display)
    {
//        _display = display;
    }
    
    private void click(MenuItem item)
    {
        new MenuItemTester().actionClick(item);
    }

    private void dismissMenus(int numberOfPoppedMenus)
    {
        for(int i = 0; i < numberOfPoppedMenus; i++)
        {
            log(MESSAGE_PREFIX + "Dismissing menu...");
            keyClick(SWT.ESC);
        }
    }
    
    
	private void takeScreenShot() {
		String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
		ScreenCapture.createScreenCapture(testcaseID);
	}
    
    
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
    
    private void invokePopup(ClickLocation clickLocation)
    {
        //assert clickLocation != null : "clickLocation was null.";
        log(MESSAGE_PREFIX + "Invoking at ClickLocation: " + clickLocation);
        Point p = clickLocation.getClickLocation();
        
//        /*
//         * We tried using abbot's mousePress functionality, but it failed to work
//         * consistently with on linux. The Event approach was taken from
//         * eclipse'st swt tests.
//         */
//        
//        Event event = new Event();
//        
//        /*
//         * Provisioning for possibly remapped keys here:
//         */
//        event.button = MouseConfig.SECONDARY_BUTTON;
//        
//        event.type = SWT.MouseMove;
//        event.x = p.x;
//        event.y = p.y;
//		new SWTPushEventOperation(event).execute();
//		pauseCurrentThread(200);
//        
//        event = new Event();
//        event.button = MouseConfig.SECONDARY_BUTTON;
//        event.type = SWT.MouseDown;
//        event.x = p.x;
//        event.y = p.y;
//		new SWTPushEventOperation(event).execute();
//		pauseCurrentThread(200);
//
//        event = new Event();
//        event.button = MouseConfig.SECONDARY_BUTTON;
//        event.type = SWT.MouseUp;
//        event.x = p.x;
//        event.y = p.y;
//		new SWTPushEventOperation(event).execute();
		
		new SWTMouseOperation(WT.BUTTON3).at(new SWTDisplayLocation().offset(p)).execute();
		pauseCurrentThread(200);
    }
    
    private void log(String msg)
    {
        //System.out.println(msg);
    }
    
    /**
     * Right click a Widget to invoke a popup/context menu, and then
     * click the menu item. 
     * 
     * @param menuOwner
     * @param widgetToRightClick
     * @param menuItemPath - this may be the text for a single menu item, or a path (delimited by "/") 
     *                       of menu items for cascading menus.
     */
    public Widget runPopup(Control menuOwner, Widget widgetToRightClick, String menuItemPath) {
        return runPopup(menuOwner, widgetToRightClick, menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR);
    }
    
	public Widget runPopup(Control menuOwner, Widget widgetToRightClick, int x, int y, String menuItemPath) {
		return runPopup(menuOwner, widgetToRightClick, new ClickOffset(widgetToRightClick, x, y), menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR, null);

	}
    
    public Widget runPopup(Control menuOwner, Widget widgetToRightClick, String menuItemPath, String pathDelimiter) {
        return runPopup(menuOwner, widgetToRightClick, null, menuItemPath, pathDelimiter, null);
    }
    
    public Widget runPopup(Control menuOwner, ClickLocation clickLocation, String menuItemPath)
    {
        return runPopup(menuOwner, clickLocation, menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR, null);
    }
    
    private Widget runPopup(Control menuOwner, Widget widgetToRightClick, ClickLocation clickLocation, String menuItemPath, String pathDelimiter, List/*<PopupItemInfo>*/ verificationList)
    {
        if (clickLocation == null) 
        {
            clickLocation = getDefaultClickLocation(widgetToRightClick);
        }
        return runPopup(menuOwner, clickLocation, menuItemPath, pathDelimiter, verificationList);
    }
    
    private Widget runPopup(final Control menuOwner, final ClickLocation clickLocation, final String menuItemPath, String pathDelimiter, final List/*<PopupItemInfo>*/ verificationList)
    {
        //assert menuItemPath != null : "menuItemPath was null.";
        //assert !menuItemPath.equals("") : "menuItemPath was empty.";
        //assert clickLocation != null : "clickLocation was null.";

    	int popUpPause = TestSettings.getInstance().getPreContextClickDelay();
    	
    	pauseCurrentThread(popUpPause); //FIXME: CR264634 - we need a condition to tell us when we are ready.
        
       
        final StringTokenizer menuItemTexts = new PathStringTokenizer(menuItemPath /*, pathDelimiter */);
        //final StringTokenizer menuItemTexts = new StringTokenizer(menuItemPath, pathDelimiter );
        
        
        final int numberOfMenus = menuItemTexts.countTokens();
              
        final MenuHandler initialMenuHandler = new MenuHandler(menuOwner);
        
        final String userThreadId = MESSAGE_PREFIX + menuItemPath;
        final UserThread userThread = new UserThread(userThreadId, initialMenuHandler, menuItemPath, menuItemTexts, clickLocation, verificationList);
        
        try
        {
            userThread.start();
            long waitPerMenu = TestSettings.getInstance().getWaitForContextMenuTimeOut();
            long maxWait = numberOfMenus * (2 * waitPerMenu/*DEFAULT_WAIT_TIMEOUT_SECONDS * SECOND*/ + DEFAULT_USER_DELAY) + SECOND  + LINUX_DELAY;
            if (verificationList != null) 
            {
                long verificationWait = verificationList.size() * waitPerMenu /*DEFAULT_WAIT_TIMEOUT_SECONDS * SECOND*/;
                maxWait = maxWait + verificationWait;
            }
            log("Waiting for a maximum number of seconds: " + maxWait / SECOND);
            Robot.wait(new Condition()
            {
                public boolean test()
                {
                    return userThread.isFinished();
                }

                //@Override
                public String toString()
                {
                    return MESSAGE_PREFIX + "User Thread to finish for " + userThreadId ;
                }
            }, maxWait, DEFAULT_WAIT_INTERVAL_MILLISECONDS);
            //This wait must ensure that all handlers have enough time to finish;
        }
        finally
        {
            Throwable throwable = userThread.getThrowable();
            if(throwable != null)
            {
                throw new PopupFailedException(throwable);
            }
        }
        return userThread.getClicked();
    }
    
//    public void verifyPopupContents(Composite menuAndRectangleOwner, org.eclipse.draw2d.geometry.Rectangle rectangleToRightClick, String menuItemPath, String pathDelimiter, List<PopupItemInfo> verificationList) {
//        runPopup(menuAndRectangleOwner, rectangleToRightClick, null, menuItemPath, pathDelimiter, verificationList);
//    }
    
    public void verfiyPopupContents(Control menuOwner, ClickLocation clickLocation, String menuItemPath, List/*<PopupItemInfo>*/ verificationList)
    {
        runPopup(menuOwner, clickLocation, menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR, verificationList);
    }
        
    public void verifyPopupContents(Control menuOwner, Widget widgetToRightClick, List/*<PopupItemInfo>*/ verificationList) {
        runPopup(menuOwner, widgetToRightClick, null, "", DEFAULT_CASCADING_MENUITEM_SEPARATOR, verificationList);
    }
    
    public void verifyPopupContents(Control menuOwner, Widget widgetToRightClick, String menuItemPath, List/*<PopupItemInfo>*/ verificationList) {
        runPopup(menuOwner, widgetToRightClick, null, menuItemPath, DEFAULT_CASCADING_MENUITEM_SEPARATOR, verificationList);
    }
    
    public void verifyPopupContents(Control menuOwner, Widget widgetToRightClick, String menuItemPath, String pathDelimiter, List/*<PopupItemInfo>*/ verificationList) {
        runPopup(menuOwner, widgetToRightClick, null, menuItemPath, pathDelimiter, verificationList);
    }
    
    private void verifyPopupContents(Menu popup, List /*<PopupItemInfo>*/ verificationList)
    {
        log(MESSAGE_PREFIX + "Verifying PopupItemInfo list...");
        
//        for(PopupItemInfo itemInfo : verificationList)
//        {
        for (Iterator iter = verificationList.iterator(); iter.hasNext(); )
        {
        	PopupItemInfo itemInfo = (PopupItemInfo)iter.next();
            MenuItemHandler itemHandler = new MenuItemHandler(popup);
            itemHandler.waitForMenuItem(itemInfo.text, itemInfo.enabled);
            log(MESSAGE_PREFIX + itemInfo);
        }
    }
    

    public static class ClickCenter extends WidgetClickLocation
    {
        public ClickCenter(Widget widget)
        {
            super(widget);
        }

        //@Override
        public Point getClickLocation()
        {
            WidgetTester widgetTester = new WidgetTester();
            Rectangle bounds = widgetTester.getGlobalBounds(_widget);
            Point point = new Point(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
            return point;
        }

		public SWTLocation asSWTLocation() {
//			return new SWTWidgetLocation(_widget, WTInternal.CENTER);
			throw new RuntimeException("Reimplement caller to use widget reference");
		}
    }

    //!pq:
    public static class ClickOffset extends WidgetClickLocation
    {
        private final int _x;
		private final int _y;

		public ClickOffset(Widget widget, int x, int y)
        {
            super(widget);
			_x = x;
			_y = y;
        }

        //@Override
        public Point getClickLocation()
        {
            WidgetTester widgetTester = new WidgetTester();
            Rectangle bounds = widgetTester.getGlobalBounds(_widget);
            Point point = new Point(bounds.x + _x, bounds.y +_y);
            return point;
        }

		public SWTLocation asSWTLocation() {
//			return new SWTWidgetLocation(_widget, WTInternal.TOPLEFT).offset(_x, _y);
			throw new RuntimeException("Reimplement caller to use widget reference");
		}
    }
    
    
    
    public static abstract class ClickLocation
    {
        abstract public Point getClickLocation();
        
        abstract public SWTLocation asSWTLocation();
        
        //@Override
        public String toString()
        {
            String msg = getClass().getName() + ": ";
            msg = msg + (getClickLocation() != null ? getClickLocation().toString() : "null click location");
            return msg;
        }
    }
    
//    public static class ClickRectangle extends ClickLocation
//    {
//        protected org.eclipse.draw2d.geometry.Rectangle _bounds;
//        protected Composite _parent;
//        private CompositeTester _compositeTester;
//        
//        public ClickRectangle(Composite parent, org.eclipse.draw2d.geometry.Rectangle bounds)
//        {
//            _bounds = bounds;
//            _parent = parent;
//            _compositeTester = TesterFactory.newCompositeTester();
//        }
//
//        //@Override
//        public Point getClickLocation()
//        {
//            final org.eclipse.draw2d.geometry.Point center = _bounds.getCenter();
//            Display display = _compositeTester.getDisplay(_parent);
//            Point p = (Point) Robot.syncExec(display, new RunnableWithResult() {
//                public Object runWithResult() {
//                    return _parent.toDisplay(center.x, center.y);
//                }
//            });
//            return p;
//        }
//    }

    public static class ClickTopLeft extends WidgetClickLocation
    {
        public ClickTopLeft(Widget widget)
        {
            super(widget);
        }

        //@Override
        public Point getClickLocation()
        {
            WidgetTester widgetTester = new WidgetTester();
            Rectangle bounds = widgetTester.getGlobalBounds(_widget);
            Point point = new Point(bounds.x + 1, bounds.y + 1);
            return point;
        }

		public SWTLocation asSWTLocation() {
//			return new SWTWidgetLocation(_widget, WTInternal.TOPLEFT).offset(1, 1);
			throw new RuntimeException("Reimplement caller to use widget reference");
		}
    }
    
//    public static class ClickTopRight extends WidgetClickLocation
//    {
//        final private int _offset;
//        
//        public ClickTopRight(Widget widget) 
//        {
//            this(widget, 1);
//        }
//        
//        public ClickTopRight(Widget widget, int offset) 
//        {
//            super(widget);
//            _offset = offset;
//        }
//        
//        //@Override
//        public Point getClickLocation()
//        {
//            WidgetTester widgetTester = new WidgetTester();
//            Rectangle bounds = widgetTester.getGlobalBounds(_widget);
//            int x =  bounds.width - _offset;
//            //if there's a vertical scrollbar, click right next to it.
//            if (_widget instanceof Scrollable) 
//            {
//                Scrollable scrollable = (Scrollable)_widget;
//                ScrollBarTester scrollBarTester = new ScrollBarTester();
//                ScrollableTester scrollableTester = new ScrollableTester();
//                if (scrollableTester.getVerticalBar(scrollable) != null && 
//                    scrollBarTester.getVisible(scrollableTester.getVerticalBar(scrollable)))
//               {
//                    int scrollBarWidth = scrollBarTester.getSize(scrollableTester.getVerticalBar((Scrollable)_widget)).x;
//                    x = x - scrollBarWidth;
//               }
//            } 
//            Point point = new Point(bounds.x + x, bounds.y + 0);
//            return point;
//        }
//    }
    
    private class MenuHandler  implements Condition
    {
        private String LISTENER_MESSAGE_PREFIX = MESSAGE_PREFIX;
        private Widget _menuOwner;
        private Menu _menu;

        private Throwable _throwable;

        private final ControlTester _controlTester;
        private final MenuTester _menuTester;
        private final MenuItemTester _menuItemTester;
        
        public MenuHandler(Widget menuOwner) 
        {
            _menuOwner = menuOwner;
            if (menuOwner instanceof MenuItem) 
            {
                LISTENER_MESSAGE_PREFIX = LISTENER_MESSAGE_PREFIX + "CascadedMenuHandler: ";
            } 
            else 
            {
                LISTENER_MESSAGE_PREFIX = LISTENER_MESSAGE_PREFIX + "MenuHandler: ";
            }
            _controlTester = new ControlTester();
            _menuTester = new MenuTester();
            _menuItemTester = new MenuItemTester();
        }
        
        /**
         * Return true, if menu not null, and is visible.
         */
        public boolean test()
        {
            boolean visible = false;
            try
            {
                if(_menu != null)
                {
                    visible = _menuTester.isVisible(_menu);
                    if(!visible)
                    {
                        _throwable = new WidgetSearchException("Menu NOT VISIBLE.[" + _menuTester.toString(_menu) + "]");
                    }
                }  
                else
                {
                    _menu = findMenu(_menuOwner);
                }
            }
            catch(SWTException e)
            {
                _throwable = e; //A widget *could* be disposed. 
                return false;
            }
            return visible;
        }

        /**
         * Provide debug output suitable for a Robot.wait(Condition) debug
         * message.
         */
        //@Override
        public String toString()
        {
            StringBuffer message = new StringBuffer();
            message.append("Menu to be visible. ");
            if(_throwable != null)
            {
                message.append(_throwable.getLocalizedMessage());
            }
            else
            {
                message.append("Menu NOT FOUND.");
            }
            return message.toString();
        }

        public Menu waitForVisibleMenu()
        {
            _throwable = null;
            //DEFAULT_WAIT_TIMEOUT_SECONDS * SECOND
            int maxWait = TestSettings.getInstance().getWaitForContextMenuTimeOut();
            Robot.wait(this, maxWait, DEFAULT_WAIT_INTERVAL_MILLISECONDS);
            log(LISTENER_MESSAGE_PREFIX + "Found visible menu.");
            return this._menu;
        }

        /**
         * Return the menu with owner.getMenu, if available,
         * otherwise, search the hiearchy for descendants of the owner, which 
         * are Controls, and return the first Menu found.
         * 
         * Returns null if menu not found.
         */
        private Menu findMenu(Widget owner)
        {
            log(LISTENER_MESSAGE_PREFIX + "Looking for menu for: " + WidgetTester.toString(owner));
            Menu menu = null;
            if(owner instanceof Control)
            {
                menu = _controlTester.getMenu((Control)owner);
            }
            else if(owner instanceof MenuItem)
            {
                menu = _menuItemTester.getMenu((MenuItem)owner);
            }
            if(menu == null)
            {
                menu = findMenuRecursively(owner);
            }

            if(menu != null)
            {
                log(LISTENER_MESSAGE_PREFIX + "Found menu: " + WidgetTester.toString(menu));
            }
           
            return menu;
        }
        
        /**
         * Search direct children for menu, if not found, look recursively through children. 
         * Only return the menu from a Control.
         */
        private Menu findMenuRecursively(Widget parent) 
        {
            log(LISTENER_MESSAGE_PREFIX + "Looking for menu recursively for: " + WidgetTester.toString(parent));
            Display display = _menuTester.getDisplay(parent);
            TestHierarchy hierarchy = new TestHierarchy(display);
            //@SuppressWarnings("unchecked")
            Collection/*<Widget>*/ children = hierarchy.getWidgets(parent); //This returns other widgets, in addition, to getChildren()
            
//            for(Widget control : children)
//            {
            for(Iterator iter = children.iterator(); iter.hasNext(); )
            {
            	Widget control = (Widget)iter.next();
                if(control instanceof Control)
                {
                    Menu menu = _controlTester.getMenu((Control)control);
                    if (menu != null) 
                    {
                        return menu;
                    }
                }
            }
            
//            for(Widget control : children)
//            {
            for(Iterator iter = children.iterator(); iter.hasNext(); )
            {
            	Widget control = (Widget)iter.next();
                if(control instanceof Composite)
                {
                    Menu menu = findMenuRecursively((Composite)control);
                    if(menu != null)
                    {
                        return menu;
                    }
                }
            }
            
            return null;
        }
    }
    
    private class MenuItemHandler implements Condition
    {
        private final Menu _parent;
        private String _itemText;
        private MenuItem _item;
        private boolean _expectedState = true;
        
        private Throwable _throwable;

        private final MenuTester _menuTester;
        private final MenuItemTester _menuItemTester;

        public MenuItemHandler(Menu menu)
        {
            //assert menu != null : "menu was null.";
            _parent = menu;
            _menuTester = new MenuTester();
            _menuItemTester = new MenuItemTester();
        }

        /**
         * Return true if MenuItem found and is in correct enabled state.
         */
        public boolean test()
        {
            boolean enabled = !_expectedState;
            try
            {
                if(_itemText == null)
                {
                    _throwable = new WidgetSearchException("itemText was null for MenuItemHandler.");
                    return false;
                }
                _item = getNextMenuItem(_parent, _itemText);
                enabled = _menuItemTester.isEnabled(_item);
                if(!(enabled == _expectedState))
                {
                    _throwable = new WidgetSearchException("MenuItem not in correct enabled state. ");
                    return false;
                }
            }
            catch(WidgetSearchException e)
            {
                _throwable = e;
                return false;
            }
            return enabled == _expectedState;
        }

        //@Override
        public String toString()
        {
            StringBuffer message = new StringBuffer();
            message.append("MenuItem [" + _itemText + "], with enabled == [" + _expectedState + "] ");
            if(_throwable != null)
            {
                message.append(_throwable.getLocalizedMessage());
            }
            return message.toString();
        }

        public MenuItem waitForMenuItem(String itemText, boolean expectedState)
        {
            _throwable = null;
            _itemText = itemText; // @todo: we might just want to restrict a
            _expectedState = expectedState; 
            int maxWait = TestSettings.getInstance().getWaitForContextMenuTimeOut();
            Robot.wait(this, maxWait /*DEFAULT_WAIT_TIMEOUT_SECONDS * SECOND*/, DEFAULT_WAIT_INTERVAL_MILLISECONDS);
            return _item;
        }

        private MenuItem getNextMenuItem(Menu parent, String itemText)
                throws WidgetSearchException
        {
            //assert parent != null : "parent Menu was null.";
            //assert itemText != null : "itemText was null.";

            MenuItem[] items = _menuTester.getItems(parent);
            StringBuffer buffer = new StringBuffer();
            buffer.append("Menu Item [");
            buffer.append(itemText);
            buffer.append("] NOT FOUND in item list:\n");
            for(int i = 0; i < items.length; i++)
            {
                String text = _menuItemTester.getText(items[i]);
               
                if(stringsMatch(itemText, text))
                {
                    return items[i];
                }
                buffer.append("\n");
                buffer.append(text);
            }
            throw new WidgetSearchException(buffer.toString());
        }
        
        private boolean stringsMatch(String expected, String actual)
        {
            if(expected == null || actual == null)
            {
                return false;
            }
            else
            {
                return StringComparator.matches(actual, expected);
            }
        }
    }
    
    public static class PopupFailedException extends RuntimeException
    {
        private static final long serialVersionUID = 2699160308329690072L;
        
        private PopupFailedException(String msg)
        {
            super(msg);
        }
        
        private PopupFailedException(Throwable t)
        {
            super(t);
        }
    }
    
    /** Data class for verifying existence and state of a MenuItem in a popup.
     */
    public static class PopupItemInfo
    {
        public final String text;

        public final boolean enabled;

        public PopupItemInfo(String text, boolean enabled)
        {
            this.text = text;
            this.enabled = enabled;
        }
        
        //@Override
        public String toString()
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Text=");
            buffer.append(text);
            buffer.append("\n");
            buffer.append("ExpectedState=");
            buffer.append(enabled);
            buffer.append("\n");
            return buffer.toString();
        }
    }
    
    private class UserThread extends Thread
    {
        private Throwable _throwable;
        private boolean _finished;
        private int _numberOfPoppedMenus;
        
        private final MenuHandler _initialMenuHandler;
        private final String _menuItemPath;
        private final StringTokenizer _menuItemTexts;
        private final ClickLocation _clickLocation;
        private final List/*<PopupItemInfo>*/ _verificationList;
        
		//the clicked item
		private MenuItem _clicked;
        
        
        private UserThread(String id, MenuHandler initialMenuHandler, String menuItemPath, StringTokenizer menuItemTexts, ClickLocation clickLocation, List/*<PopupItemInfo>*/ verificationList)
        {
            super(id);
            _initialMenuHandler = initialMenuHandler;
            _menuItemPath = menuItemPath;
            _menuItemTexts = menuItemTexts;
            _clickLocation = clickLocation;
            _verificationList = verificationList;
        }
        
        public Throwable getThrowable()
        {
            return _throwable;
        }
        
        public boolean isFinished()
        {
            return _finished;
        }

		public MenuItem getClicked() {
			return _clicked;
		}
        
        //@Override
        public void run()
        {
            _finished = false;
            _throwable = null;
            _numberOfPoppedMenus = 0;
            try
            {
                /* Invoke the popup Menu, and click MenuItem(s) */
                log(MESSAGE_PREFIX + "Invoking popup Menu with path: " + _menuItemPath);
                invokePopup(_clickLocation);
                // Wait for popup Menu
                Menu menu = _initialMenuHandler.waitForVisibleMenu();
                _numberOfPoppedMenus++;
                MenuHandler cascadedMenuHandler = null;

                // Loop for clicking and verifying menu items.
                while(_menuItemTexts.hasMoreTokens())
                {
                    String itemText = _menuItemTexts.nextToken();

                    // Otherwise continue with either clicking, or cascading
                    MenuItemHandler itemHandler = new MenuItemHandler(menu);
                    MenuItem item = itemHandler.waitForMenuItem(itemText, true);

                    pauseCurrentThread(DEFAULT_USER_DELAY); // This delay more closely simulates user interaction.
                    log(MESSAGE_PREFIX + "Clicking Menu Item: " + itemText);
                    
                    click(item);
                    _clicked = item;
                    
                    cascadedMenuHandler = new MenuHandler(item);
                    if(_menuItemTexts.hasMoreTokens())
                    {
                        log(MESSAGE_PREFIX + "Cascading...");
                        menu = cascadedMenuHandler.waitForVisibleMenu();
                        _numberOfPoppedMenus++;
                    }
                }

                // Verify popup menu items if required
                if(_verificationList != null)
                {
                    if(cascadedMenuHandler != null)
                    {
                        menu = cascadedMenuHandler.waitForVisibleMenu();
                        _numberOfPoppedMenus++;
                    }

                    try
                    {
                        verifyPopupContents(menu, _verificationList);
                    } catch(WaitTimedOutError e) {
                    	//capture screen BEFORE dismissing menus
                    	TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "creating screenshot of pop-up timeout failure");
                    	takeScreenShot();
                    	//rethrow exception
                    	throw e;
                    } finally
                    {
                        dismissMenus(_numberOfPoppedMenus);
                    }
                }
            }
            catch(Throwable e)
            {
            	//capture screen BEFORE dismissing menus
            	TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "creating screenshot of pop-up timeout failure");
            	takeScreenShot();
                _throwable = e;
                dismissMenus(_numberOfPoppedMenus);
            }
            finally
            {
                log(MESSAGE_PREFIX + "Finished.");
                _finished = true;
            }
        }
        
    }
    
    public static abstract class WidgetClickLocation extends ClickLocation
    {
        protected Widget _widget;

        public WidgetClickLocation(Widget widget)
        {
            _widget = widget;
        }
    }



}
