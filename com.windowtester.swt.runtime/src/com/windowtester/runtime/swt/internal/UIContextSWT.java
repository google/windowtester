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

import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;



import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.Diagnostic;
import com.windowtester.internal.runtime.ISelectionTarget;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.internal.runtime.UIContextCommon;
import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.internal.runtime.locator.IDefaultUISelectorFactory;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.locator.IUISelector2;
import com.windowtester.internal.runtime.selector.ClickHelper;
import com.windowtester.internal.runtime.selector.IClickDriver;
import com.windowtester.internal.runtime.system.WidgetSystem;
import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.internal.swing.UIContextSwingFactory;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.monitor.IUIThreadMonitor;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.internal.application.ApplicationContext;
import com.windowtester.runtime.swt.internal.condition.shell.ShellMonitor;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.dnd.DragAndDropHelper;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.hover.HoverInfo;
import com.windowtester.runtime.swt.internal.hover.IHoverInfo;
import com.windowtester.runtime.swt.internal.locator.ICloseableLocator;
import com.windowtester.runtime.swt.internal.monitor.UIThreadMonitorSWT;
import com.windowtester.runtime.swt.internal.operation.effects.PlaybackAdvisor;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.swt.internal.selection.SelectionTarget;
import com.windowtester.runtime.swt.internal.selector.DefaultSWTWidgetSelector;
import com.windowtester.runtime.swt.internal.selector.ListHelper;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.swt.internal.text.TextEntryStrategy;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;

/**
 * Abstract implementation of {@link com.windowtester.runtime.IUIContext}.
 * 
 */
public class UIContextSWT extends UIContextCommon
{


	//a default value for cases where no button mask is specified
	private static final int DEFAULT_BUTTON_MASK = WT.BUTTON1;
	
	private final UIDriver _driver = new UIDriver();
//	private final HighlightingDriver _highlightingDriver = new HighlightingDriver();
	private IUIThreadMonitor _threadMonitor;
	private Display _display;
	private ShellMonitor _shellMonitor;

	private ActionDirector _director;
	private DragAndDropHelper _dndHelper;
		
	//private TextDriver textDriver = new TextDriver(this);
	
	/* package */ final ApplicationContext applicationContext = new ApplicationContext();
	
	final ClickManager clickManager = new ClickManager();
	
	
	//number of find retry attempts
	private int _findAttempts;

	
	////////////////////////////////////////////////////////////////////////////
	//
	// System set-up
	//
	////////////////////////////////////////////////////////////////////////////

	{
		WidgetSystem.addDefaultSelector(new IDefaultUISelectorFactory() {
			public IUISelector create(Object widget) {
				if (widget instanceof IWidgetReference)
					widget = ((IWidgetReference)widget).getWidget();
				if (widget instanceof Widget)
					return new DefaultSWTWidgetSelector();
				return null;
			}
		});
		
		getClickDriver().addClickListener(clickManager);
		
	}
	
	//used to manage clicks for DND operations
	class ClickManager implements IClickDriver.Listener {

		//TODO: overriding click makes this go away?
		
		public void clicked(IClickDescription click, IWidgetLocator clicked) {
			cacheTargetInfo(click, clicked);
				
		}

		public void contextClicked(IClickDescription click, IWidgetLocator clicked) {
			cacheTargetInfo(click, clicked);
		}
		
		private void cacheTargetInfo(IClickDescription click, IWidgetLocator clicked) {

			//this greedy try-catch is a safety against failures here that should just go ignored
			try {
				Widget widget = null;
				if (clicked instanceof IWidgetReference){
					Object refPayload = ((IWidgetReference)clicked).getWidget();
					if (refPayload instanceof Widget)
						widget = (Widget) refPayload;
				}
				//only re-find if the reference did not carry a widget
				if (widget == null)
					widget = findWidget(clicked);
				//in case there is no widget found, we default to the current location
				if (widget == null)
					cacheInfoFromCurrentCursorLocation();
				else
					cacheInfoFromClick(click, widget);
			} catch (Exception e) {
				LogHandler.log(e);
			}
		}

		void cacheInfoFromCurrentCursorLocation() {
			getDriver().setMouseHoverInfo(HoverInfo.getAbsolute(UIDriver.getCurrentCursorLocation()));
			return;
		}

		private void cacheInfoFromClick(IClickDescription click, Widget widget) {
			/*
			 * TODO: this involves an extra bounds calculation -- we should remove this...
			 * The issue is that since actions that were once in the UIDriver have become decentralized, this is a bit
			 * tricky.  Anyway, definitely "todo".
			 */
			Point location = UIDriver.getLocation(widget);
			if (location == null)
				location = UIDriver.getCurrentCursorLocation();
			
			if (!click.isDefaultCenterClick()) {
				location.x += click.x();
				location.y += click.y();
			}
			IHoverInfo hover = HoverInfo.getAbsolute(location);
			getDriver().setMouseHoverInfo(hover);
		}


	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Answer the display associated with the receiver.
	 * 
	 * @return the display or <code>null</code> if it has not been set
	 */
	public Display getDisplay() {
		return _display;
	}

	/**
	 * Set the display associated with the receiver.
	 * 
	 * @param display the display (not <code>null</code>)
	 */
	public void setDisplay(Display display) {
		if (display == null)
			throw new IllegalArgumentException("display cannot be null");
		_display = display;
		// pre-configure platform specific information
		ListHelper.calculateItemListSpacing(display);
		//start menu watcher 
//		MenuWatcher.getInstance(display).startWatching();
	}
	
	ActionDirector getDirector() {
		if (_director == null)
			_director = new ActionDirector(this);
		return _director;
	}
	
	protected DragAndDropHelper getDNDHelper() {
		if (_dndHelper == null)
			_dndHelper = new DragAndDropHelper(this);
		return _dndHelper;
	}
	
	
	// //////////////////////////////////////////////////////////////////////////
	//
	// Adaptation
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Override the superclass implementation to return an SWT appropriate thread monitor
	 * and check the SWT adapter factor before calling super.
	 * 
	 * @see IUIContext#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == IShellMonitor.class)
			return getShellMonitor();
		// UIContextSwing
		if (adapter.equals(UIContextSwing.class)){
			return UIContextSwingFactory.createContext();
		}
		//add compatibility to NEW generic interface
		if (adapter.equals(IUIThreadMonitor.class) || adapter.equals(com.windowtester.runtime.monitor.IUIThreadMonitor.class)
				|| adapter.getName().equals("com.windowtester.swt.monitor.IUIThreadMonitor")) {
			//NOTE: access by name is required because a legacy interface has been moved OUT of this plugin
			//into the compatibility layer
			if (_threadMonitor == null)
				_threadMonitor = new UIThreadMonitorSWT(this, _display);
			return _threadMonitor;
		}
		if (adapter.equals(PlaybackSettings.class))
			return getPlaybackSettings();
		if (adapter.equals(UIDriver.class))
			return getDriver();
				
		Object result = AdapterFactory.getInstance().getAdapter(this, adapter);
		if (result != null)
			return result;
		return super.getAdapter(adapter);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// UI actions
	//
	// //////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.UIContextCommon#click(com.windowtester.runtime.locator.ILocator)
	 */
	public IWidgetLocator click(ILocator locator) throws WidgetSearchException {
		/*
		 * An unfortunate feature of the new API is that we have 2 classes named MenuItemLocator that subclass ILocator
		 * The rub is that only the swt version knows how to perform the click.
		 * To guard against users using the runtime version when they MEAN the SWT
		 * version, we do a little adaptation.
		 * 
		 * 
		 */
		if (locator instanceof com.windowtester.runtime.locator.MenuItemLocator) {
			com.windowtester.runtime.locator.MenuItemLocator pathLocator = (com.windowtester.runtime.locator.MenuItemLocator)locator;
			locator = new com.windowtester.runtime.swt.locator.MenuItemLocator(pathLocator.getPath());
		}
		
		handleConditions();
		return super.click(locator);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.UIContextCommon#click(int, com.windowtester.runtime.locator.ILocator, int)
	 */
	public IWidgetLocator click(int clickCount, ILocator locator, int buttonMask) throws WidgetSearchException {
		handleConditions();
		//mouseMove(locator);

		// If this is a Swing locator, then redirect it to the Swing UI context
		// TODO: Move "_swingContext" field and associated code from UIContext into this class
		if (locator instanceof SwingWidgetLocator
			// TODO: hack to handle XYLocators... need a polymorphic approach or better SWT/Swing integration
			|| (locator instanceof XYLocator && ((XYLocator) locator).locator() instanceof SwingWidgetLocator)
		) {
			UIContextSwing uiSwing = (UIContextSwing) getAdapter(UIContextSwing.class);
			// Must translate SWT button mask to Swing button mask
			int swingButtonMask = InputEvent.BUTTON1_MASK;
			if ((buttonMask & WT.BUTTON2) != 0)
				swingButtonMask = InputEvent.BUTTON2_MASK;
			if ((buttonMask & WT.SHIFT) != 0)
				swingButtonMask &= InputEvent.SHIFT_MASK;
			if ((buttonMask & WT.CTRL) != 0)
				swingButtonMask &= InputEvent.CTRL_MASK;
			if ((buttonMask & WT.ALT) != 0)
				swingButtonMask &= InputEvent.ALT_MASK;
			return uiSwing.click(clickCount, locator, swingButtonMask);
		}

		return clicked(super.click(clickCount, locator, buttonMask));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.UIContextCommon#click(int, com.windowtester.runtime.locator.ILocator)
	 */
	public IWidgetLocator click(int clickCount, ILocator locator) throws WidgetSearchException {
		handleConditions();
		return super.click(clickCount, locator);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.UIContextCommon#contextClick(com.windowtester.runtime.locator.ILocator, com.windowtester.runtime.locator.IMenuItemLocator)
	 */
	public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.UIContextCommon#contextClick(com.windowtester.runtime.locator.ILocator, com.windowtester.runtime.locator.IMenuItemLocator, int)
	 */
	public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem, int modifierMask) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem, modifierMask);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.UIContextCommon#contextClick(com.windowtester.runtime.locator.ILocator, java.lang.String)
	 */
	public IWidgetLocator contextClick(ILocator locator, String menuItem) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.UIContextCommon#contextClick(com.windowtester.runtime.locator.ILocator, java.lang.String, int)
	 */
	public IWidgetLocator contextClick(ILocator locator, String menuItem, int modifierMask) throws WidgetSearchException {
		handleConditions();
		return super.contextClick(locator, menuItem, modifierMask);
	}
	
	/**
	 * A hook for post-processing clicked widgets before returning them to the client.
	 */
	private IWidgetLocator clicked(IWidgetLocator click) {
		PlaybackAdvisor.getDefault().postClickPause();
		return click;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Text entry actions
	//
	///////////////////////////////////////////////////////////////////////////
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#enterText(java.lang.String)
	 */
	public void enterText(String txt) {
		handleConditions();
		//temporary call out to pluggable text entry strategy
		//getDriver().enterText(txt);
		TextEntryStrategy.getCurrent().enterText(this, txt);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#keyClick(int)
	 */
	public void keyClick(int key) {
		handleConditions();
		getDriver().keyClick(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#keyClick(char)
	 */
	public void keyClick(char key) {
		handleConditions();
		getDriver().keyClick(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#keyClick(int, char)
	 */
	public void keyClick(int ctrl, char c) {
		handleConditions();
		getDriver().keyClick(ctrl, c);
	}
		
	///////////////////////////////////////////////////////////////////////////
	//
	// Move/selection
	//
	///////////////////////////////////////////////////////////////////////////
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#mouseMove(com.windowtester.runtime.locator.ILocator)
	 */
	public IWidgetLocator mouseMove(ILocator locator) throws WidgetSearchException {
		handleConditions();
		ISelectionTarget target = getTarget(locator);
		return doMouseMove(target);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#dragTo(com.windowtester.runtime.locator.ILocator)
	 */
	public IWidgetLocator dragTo(ILocator locator) throws WidgetSearchException {	
		handleConditions(); 
		ISelectionTarget target = getTarget(locator);

		IUISelector2 selector2 = adaptToSelector2(locator);
		if (selector2 != null) {
			return selector2.dragTo(this, target);
		}
		//we need a pause lest the OS interpret the drag mouseDown as a double click
		UIDriver.pause(1000); //<-- we use the driver to avoid triggering conditions
		return doDragTo(target);
	}
	
	private IUISelector2 adaptToSelector2(ILocator locator) {
		locator = ClickHelper.getWidgetLocator(locator); //parses out XYs
		if (locator instanceof IUISelector2)
			return (IUISelector2)locator;
		if (locator instanceof IAdaptable)
			return (IUISelector2) ((IAdaptable)locator).getAdapter(IUISelector2.class);
		return null;
	}

	private IWidgetLocator doDragTo(ISelectionTarget target) throws WidgetSearchException {
		IClickDescription click = target.getClickDescription();
		IWidgetLocator locator  = target.getWidgetLocator();
		Widget w = findWidget(locator);
		//drag and wrapper the result in a widget reference
		Widget dropTarget = click.isDefaultCenterClick() ? dragTo(w) : dragTo(w, click.x(), click.y());
		return WidgetReference.create(dropTarget);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#dragTo(com.windowtester.runtime.locator.ILocator, int)
	 */
	public IWidgetLocator dragTo(ILocator locator, int mods) throws WidgetSearchException {

		/*
		 * This is a bit tricky since we don't want to handle conditions WHILE a key is 
		 * down and the actual drag is being done in a subclass who is free to handle conditions...
		 * 
		 * The current work-around is to "go native"
		 * 
		 */
		//LogHandler.log("modifiers in dragTo ignored -- not implemented");
		//go native so that conditions are not handled while the key is down
		boolean isStatePreDragNative = applicationContext.isNative(); //cache for restore
		applicationContext.setNative();
		try {
			getDriver().mouseDown(mods);
			return dragTo(locator);
		} finally {
			getDriver().mouseUp(mods);
			//restore state
			if (!isStatePreDragNative)
				applicationContext.setDefault();
		}
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#close(com.windowtester.runtime.locator.IWidgetLocator)
	 */
	public void close(IWidgetLocator locator) throws WidgetSearchException {

		ICloseableLocator closeable = getCloseable(locator);
		
		if (closeable != null) {
			closeable.doClose(this);
			return;
		}
		
			
		//conditions handled in the close
		//handleConditions();		
		Widget widget = findWidget(locator);
		if (widget == null)
			throw new WidgetSearchException("target of a close call must not be null");
		if (!(widget instanceof Shell))
			throw new WidgetSearchException("target of a close call must be a Shell, got a: " + widget.getClass() + " instead");
		
		close((Shell)widget);
	}


	private ICloseableLocator getCloseable(IWidgetLocator locator) {
		if (locator instanceof ICloseableLocator)
			return (ICloseableLocator)locator;
		if (locator instanceof IAdaptable)
			return (ICloseableLocator) ((IAdaptable)locator).getAdapter(ICloseableLocator.class);
		return null;
	}

	//close the given shell
	protected void close(Shell shell) {
		handleConditions();
		getDriver().close(shell);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#setFocus(com.windowtester.runtime.locator.IWidgetLocator)
	 */	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#find(com.windowtester.runtime.locator.IWidgetLocator)
	 */
	public IWidgetLocator find(IWidgetLocator locator) throws WidgetSearchException {
		
		/*
		 * TODO: remove _findAttempts as a field and refactor to a parameter of a second
		 * find helper method
		 */
		
		handleConditions();
		IWidgetLocator[] locators = findAll(locator);
		
		//success condition
		if (locators.length == 1) {
			_findAttempts = 0;
			
			IWidgetReference ref = (IWidgetReference)locators[0];
			//test for raw/generic reference case and upgrade as needed
			Object widget = ref.getWidget();
			if (widget instanceof Widget && ref.getClass().equals(WidgetReference.class)) {
				ref = WTRuntimeManager.asReference(widget);
			}
				
			
//			// TODO[pq]: this special case should be fixed -- rub: hyperlink refs are NOT widgets...			
//			IWidgetReference<?> ref = (IWidgetReference<?>)locators[0];
////			System.out.println("UIContextSWT.find(): " + ref);
//			Object widget = ref.getWidget();
//			if (!(widget instanceof Widget))
//				return ref;
//			System.out.println("+++ adapting: " + ref);
//			HERE: thought : really only want to upgrade if it's a legacy WidgetReference instance...
//			// TODO[pq]: this translation will not be needed when the finder is replaced
//			return SWTWidgets.asReference((Widget)widget);
			
			return ref;
		}
		
        //update attempt number and possibly try again
        if (_findAttempts++ < SWTWidgetFinder.getMaxFinderRetries()) {
        	TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "UIContextSWT failed to find widget (" + locator + ") retrying [" + _findAttempts + "/" + SWTWidgetFinder.getMaxFinderRetries() +"]");
			pause(SWTWidgetFinder.getFinderRetryInterval());
        	return find(locator);
        }	
		
		//in the error case, handle cleanup before throwing the exception
		handleCleanup();
		
		//be sure to reset find attempts for next find
		_findAttempts = 0;
		
		if (locators.length > 1) {
			StringBuffer buf = new StringBuffer(200);
			buf.append("Multiple Widgets Found:\nlooking for\n  ");
			buf.append(locator.toString());
			buf.append("\nand found:");
			for (int i = 0; i < locators.length; i++) {
				buf.append("\n  ");
				try {
					buf.append(getToStringOnUIThread(locators[i]));
				}
				catch (Exception e) {
					buf.append(locators[i].getClass() + " - " + e);
				}				
			}
			throw new MultipleWidgetsFoundException(buf.toString());
		}
		throw new WidgetNotFoundException("Widget NOT Found:\n" + locator.toString());
		
	}

	/**
	 * Handle cleanup.  Consider making this protected so subclasses can override.
	 */
	private void handleCleanup() {
		takeScreenShot();
		closeOpenShells();
	}

	private void closeOpenShells() {
		new ExceptionHandlingHelper(_display, true, this).closeOpenShells();
	}

	private void takeScreenShot() {
		String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "Creating screenshot for testcase: " + testcaseID);
        //TODO: make this filename format user configurable
		ScreenCapture.createScreenCapture(testcaseID /*+ "_" + desc*/);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#findAll(com.windowtester.runtime.locator.IWidgetLocator)
	 */
	public IWidgetLocator[] findAll(IWidgetLocator locator) {
		handleConditions();
		return (IWidgetLocator[])locator.findAll(this);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#getActiveWindow()
	 */
	public Object getActiveWindow() {
		handleConditions();
		Display display = getDisplay();
		if (display == null)
			display = Display.getDefault();
		return ShellFinder.getActiveShell(display);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selection helpers
	//
	///////////////////////////////////////////////////////////////////////////
	
	private IWidgetLocator doMouseMove(ISelectionTarget target) throws WidgetSearchException {
		IWidgetLocator hoverTarget = getDirector().doMouseMove(target);
		//this is a bit of a kludge --- the idea is to update the cached target info for drag and drops
		clickManager.cacheInfoFromCurrentCursorLocation(); 
		return hoverTarget;
	}

	
	IWidgetLocator doWidgetMouseMove(ISelectionTarget target)
			throws WidgetSearchException {
		IClickDescription click = target.getClickDescription();
		IWidgetLocator locator  = target.getWidgetLocator();
		
		Widget targetWidget     = findWidget(locator);
		// move and wrapper target
		if (click.isDefaultCenterClick())
			mouseMove(targetWidget);
		else 
			mouseMove(targetWidget, click.x(), click.y());			
		return WidgetReference.create(targetWidget);
	}


	
	///////////////////////////////////////////////////////////////////////////
	//
	// Widget finding helpers
	//
	///////////////////////////////////////////////////////////////////////////
	
	private ISelectionTarget getTarget(ILocator locator) {
		return SelectionTarget.parse(locator);
	}
	
	private Widget findWidget(IWidgetLocator locator) throws WidgetSearchException {
		IWidgetReference ref = (IWidgetReference) find((IWidgetLocator)locator);
		Object target = ref.getWidget();
		if (target == null)
			throw new IllegalArgumentException("widget reference must not be null");
		if (!(target instanceof Widget))
			return null; //NULL is now a sentinel
			//throw new IllegalArgumentException("widget reference must of class Widget, got: " + target.getClass());
		return (Widget)target;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Drag and drop actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	public Widget dragTo(Widget target) {
		handleConditions();
		return getDNDHelper().dragTo(target);
	}

//	/**
//	 * @see com.windowtester.swt.IUIContext#dragTo(java.lang.String)
//	 */
//	public Widget dragTo(String widgetHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		//condition handling done in find
//		return getDNDHelper().dragTo(find(widgetHandle));
//	}
	
	public Widget dragTo(Widget target, int x, int y) {
		handleConditions();
		return getDNDHelper().dragTo(target, x, y);
	}
	
//	/**
//	 * @see com.windowtester.swt.IUIContext#dragTo(java.lang.String, int, int)
//	 */
//	public Widget dragTo(String widgetHandle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		//condition handling done in find
//		return getDNDHelper().dragTo(find(widgetHandle), x, y);
//	}
	
//	/**
//	 * @see com.windowtester.swt.IUIContext#dragTo(org.eclipse.swt.widgets.Widget, java.lang.String, int, int)
//	 */
//	public Widget dragTo(Widget w, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		handleConditions();
//		try {
//			return getDNDHelper().dragTo(w, path, x, y);
//		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
//			throw new WidgetNotFoundException(e);
//		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
//			throw new MultipleWidgetsFoundException(e);
//		}
//	}
	
//	/**
//	 * @see com.windowtester.swt.IUIContext#dragTo(java.lang.String, java.lang.String, int, int)
//	 */
//	public Widget dragTo(String widgetHandle, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		//condition handling done in find
//		try {
//			return getDNDHelper().dragTo(find(widgetHandle), path, x, y);
//		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
//			throw new WidgetNotFoundException(e);
//		} catch (com.windowtester.runtime.MultipleWidgetsFoundException e) {
//			throw new MultipleWidgetsFoundException(e);
//		}
//	}
	
//	/**
//	 * @see com.windowtester.swt.IUIContext#dragTo(int, int)
//	 */
//	public void dragTo(int x, int y) {
//		handleConditions();
//		getDNDHelper().dragTo(x,y);
//	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	///////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#pause(int)
	 */
	public void pause(int ms) {
		expectDelay(ms);
		handleConditions();
		UIDriver.pause(ms);		
	}

	/*package*/ void expectDelay(long ms) {
		if (_threadMonitor != null)
			_threadMonitor.expectDelay(ms);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#wait(com.windowtester.runtime.condition.ICondition)
	 */
	public void wait(ICondition condition) throws WaitTimedOutException {
		wait(condition, WT.getDefaultWaitTimeOut());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#wait(com.windowtester.runtime.condition.ICondition, long)
	 */
	public void wait(ICondition condition, long timeout) throws WaitTimedOutException {
		wait(condition, timeout, WT.getDefaultWaitInterval());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#wait(com.windowtester.runtime.condition.ICondition, long, int)
	 */
	public void wait(ICondition condition, long timeout, int interval) throws WaitTimedOutException {
		expectDelay(timeout);
		
		// TODO [author=Dan] waitForShellShowing/Disposed had handleConditions
		// 		but other wait(...) methods did not, so this is a slight change  
		handleConditions();
		
		long now = System.currentTimeMillis();
		while (!ConditionMonitor.test(this, condition)) {
			if (System.currentTimeMillis() - now > timeout) {

				// If the display is valid, then capture the screen and close open shells
				if (_display == null)
					LogHandler.log("failed to get current display in wait timeout handling");
				else if (_display.isDisposed())
					LogHandler.log("current display is disposed in wait timeout handling");
				else {
					doScreenCapture("on timeout");
					new ExceptionHandlingHelper(_display, true).closeOpenShells();
				}
				
				// Build diagnostic information
				throw new WaitTimedOutException(Diagnostic.toString("Timed out waiting for condition", condition));
			}
			//note conditions are handled in the pause
			pause(interval);
		}
	}

	protected PlaybackSettings getPlaybackSettings() {
		return Platform.isRunning() ? RuntimePlugin.getDefault().getPlaybackSettings() : PlaybackSettings
			.loadFromFile();
	}

	public UIDriver getDriver() {
		return /*isHighlightingOrDelayOn() ? _highlightingDriver : */ _driver;
	}

	protected boolean isHighlightingOrDelayOn() {
		return getPlaybackSettings().getHighlightingOn() || getPlaybackSettings().getDelayOn();
	}
	
	protected int getDefaultButtonMask() {
		return DEFAULT_BUTTON_MASK;
	}

	// //////////////////////////////////////////////////////////////////////
	//
	// Condition-handling
	//
	// //////////////////////////////////////////////////////////////////////
	
	/**
	 * Answer the local shell monitor associated with this instance.
	 * @return the shell monitor (not <code>null</code>)
	 */
	private IShellMonitor getShellMonitor() {
		if (_shellMonitor == null)
			_shellMonitor = new ShellMonitor((ConditionMonitor) getConditionMonitor());
		return _shellMonitor;
	}

	/**
	 * Check for any active conditions and handle them. If a condition is handled,
	 * original hover context will be restored post condition handling.
	 * 
	 * @return one of the following flags indicating what was processed:
	 *         {@link IConditionMonitor#PROCESS_NONE} if conditions were processed but no conditions were satisfied, 
	 *         {@link IConditionMonitor#PROCESS_ONE_OR_MORE} if conditions were processed and at least on condition was satisfied,
	 *         {@link IConditionMonitor#PROCESS_RECURSIVE} if conditions were already being processed and no additional action was taken.
	 */
	public int handleConditions() {
		
		/*
		 * Since conditions might access the UI thread, we need to skip them
		 * in the Native case.  
		 */
		if (applicationContext.isNative())
			return IConditionMonitor.PROCESS_NATIVE;
		
		// cache current info
		IHoverInfo hoverInfo = getDriver().getCurrentMouseHoverInfo();
		// process conditions
		int result = super.handleConditions();
		// if conditions were handled, restore hover context (if there is one)
		if (hoverInfo != null && result == IConditionMonitor.PROCESS_ONE_OR_MORE) {
			Point location = hoverInfo.getLocation();
			// notice we do this using the driver directly so that we don't retrigger
			// condition-handling...
			if (location != null)
				getDriver().mouseMove(location.x, location.y);
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Utility
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Determine if the current test is for the Eclipse organization by examining the
	 * stack trace for classes in unexpected packages.
	 * 
	 * @return <code>true</code> if running any classes in unexpected packages are found
	 */
	static boolean isEclipseOrgTest() {
		StringWriter stringWriter = new StringWriter(1000);
		PrintWriter writer = new PrintWriter(stringWriter);
		try {
			throw new RuntimeException();
		}
		catch (Exception e) {
			e.printStackTrace(writer);
		}
		return isEclipseOrgTest(stringWriter.toString());
	}

	/**
	 * Determine if the current test is for the Eclipse organization by examining the
	 * stack trace for classes in unexpected packages.
	 * 
	 * @param stackTrace the stack trace (not <code>null</code>)
	 * @return <code>true</code> if running any classes in unexpected packages are found
	 */
	static boolean isEclipseOrgTest(String stackTrace) {
		LineNumberReader reader = new LineNumberReader(new StringReader(stackTrace));
		while (true) {
			String line;
			try {
				line = reader.readLine();
			}
			catch (IOException e) {
				return true;
			}
			if (line == null)
				return true;
			line = line.trim();
			if (line.startsWith("at")) {
				line = line.substring(2).trim();
				if (line.startsWith("com.windowtester."))
					continue;
				if (line.startsWith("java.lang."))
					continue;
				if (line.startsWith("junit.extensions."))
					continue;
				if (line.startsWith("junit.framework."))
					continue;
				if (line.startsWith("org.eclipse."))
					continue;
				if (line.startsWith("org.osgi."))
					continue;
				if (line.startsWith("sun.reflect."))
					continue;
				return false;
			}
		}
	}
	
	private String getToStringOnUIThread(final Object o) {
		Display display = getDisplay();
		if (display == null)
			display = Display.getDefault();
		if (display == null)
			return "<unable to retrieve display -- toString() failed>";
		final String [] str = new String[1];
		display.syncExec(new Runnable(){
			public void run() {
				str[0] = o.toString();
			}
		});
		return str[0];
	}

		
	///////////////////////////////////////////////////////////////////////////
	//
	// Primitive mouse action commands
	//
	///////////////////////////////////////////////////////////////////////////
	
	protected void mouseMove(Widget w) {
		handleConditions();
		getDriver().mouseMove(w);
	}
	
	protected void mouseMove(Widget w, int x, int y) {
		handleConditions();
		getDriver().mouseMove(w, x, y);
	}
	
	
}
