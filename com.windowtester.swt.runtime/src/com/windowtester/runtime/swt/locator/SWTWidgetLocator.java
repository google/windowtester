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
package com.windowtester.runtime.swt.locator;

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.visible;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.withText;

import java.awt.Point;
import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.InaccessableWidgetException;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsVisible;
import com.windowtester.runtime.condition.IsVisibleCondition;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.condition.HasFocusConditionHandler;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.matchers.SWTMatcherBuilder;
import com.windowtester.runtime.swt.internal.matchers.WidgetMatchers;
import com.windowtester.runtime.swt.internal.operation.BasicSWTWidgetClickOperation;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.LinkReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;
import com.windowtester.runtime.util.StringComparator;

/**
 * A base class for SWT widget locators.   Instances capture hierarchy (containment) 
 * relationships between widgets for use in widget identification.
 * <p>
 * For example, a widget identified by class and position relative to its
 * parent composite could be described so:
 * <pre>
 * 		new SWTWidgetLocator(Text.class, 2,
 * 			   new SWTWidgetLocator(Group.class, "addressGroup"));
 * </pre>
 * 
 * Effectively, this SWTWidgetLocator instance describes the third Text widget in
 * the Group labeled "addressGroup". 
 * <p>
 * To make text matching more robust, WindowTester supports wild cards based on the regular expression support 
 * provided by Java.  Regular expressions can be used wherever text matches are performed.
 * For more on String comparison, see the {@link StringComparator} utility.
 * 
 */
public class SWTWidgetLocator extends WidgetLocator implements IUISelector, IsVisible {
	
	private static final long serialVersionUID = 7538392706559282881L;

	/*
	 * Having these fields transient is essential to having UIEvents safely transmitted across the wire.
	 */
	private transient ISWTWidgetMatcher matcher;
//	private transient BasicWidgetSelector _selector = new BasicWidgetSelector();
	
	protected transient SWTMatcherBuilder matcherBuilder = new SWTMatcherBuilder();

	/**
	 * Create an instance that identifies an SWT widget by its class name.
	 * @param className the target widget's fully qualified class name
	 * @since 3.8.1
	 */
	public SWTWidgetLocator(String className) {
		this(className, null);
	}
		
	/**
	 * Create an instance that identifies an SWT widget by its class name.
	 * @param className the target widget's fully qualified class name
	 * @param parent the target's parent
	 * @since 3.8.1
	 */
	public SWTWidgetLocator(String className, SWTWidgetLocator parent) {
		this(className, UNASSIGNED, parent);
	}
	
	/**
	 * Create an instance that identifies an SWT widget by its class name.
	 * @param className the target widget's fully qualified class name
	 * @param index the target's index relative to its parent
	 * @param parent the target's parent
	 * @since 3.8.1
	 */
	public SWTWidgetLocator(String className, int index, SWTWidgetLocator parent) {
		super(ClassReference.forName(className), null, index, parent);
	}
	
	/**
	 * Create an instance.
	 * @param cls the target class
	 */
	public SWTWidgetLocator(Class<?> cls) {
		super(cls);
	}

	/**
	 * Create an instance.
	 * @param cls the target class
	 * @param text the target's label text
	 */
	public SWTWidgetLocator(Class<?> cls, String text) {
		super(cls, text);
	}
	
	/**
	 * Create an instance.
	 * @param cls the target class
	 * @param text the target's text label
	 * @param parent the target's parent
	 */
	public SWTWidgetLocator(Class<?> cls, String text, SWTWidgetLocator parent) {
		super(cls, text, parent);
	}
	
	/**
	 * Create an instance.
	 * @param cls the target class
	 * @param text the target's text label
	 * @param index the target's index relative to its parent
	 * @param parent the target's parent
	 */
	public SWTWidgetLocator(Class<?> cls, String text, int index, SWTWidgetLocator parent) {
		super(cls, text, index, parent);
	}

	/**
	 * Create an instance.
	 * @param cls the target class
	 * @param parent the target's parent
	 */
	public SWTWidgetLocator(Class<?> cls, SWTWidgetLocator parent) {
		super(cls, parent);
	}

	/**
	 * Create an instance.
	 * @param cls the target class
	 * @param index the target's index relative to its parent
	 * @param parent the target's parent
	 */
	public SWTWidgetLocator(Class<?> cls, int index, SWTWidgetLocator parent) {
		super(cls, index, parent);
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Scoping
	//
	///////////////////////////////////////////////////////////////////////////
	
	public SWTWidgetLocator /* THIS_TYPE */ in(int index, SWTWidgetLocator parent) {
	//	SWTWidgetLocator locator = this;
			
		SWTWidgetLocator locator = getTopScope();
		
		locator.setParentInfo(parent);
		locator.setIndex(index);
		//add collected specs (e.g., name)
//		locator.matcherBuilder.specify(matcherBuilder.criteria());
//		locator.matcherBuilder.setParent(index, parent.buildMatcher());
		return this;
	}

	private SWTWidgetLocator getTopScope() {
		WidgetLocator parent = this;
		while (parent.getParentInfo() != null)
			parent = parent.getParentInfo();
		return (SWTWidgetLocator) parent;
	}

	public SWTWidgetLocator /* THIS_TYPE */ in(SWTWidgetLocator parent) {
		return in(UNASSIGNED, parent);
	}
	
	public SWTWidgetLocator /* THIS_TYPE */ named(String name) {
		matcherBuilder.specify(WidgetMatchers.named(name));
		return this;
	}

	/**
	 * Similar to containedIn(int, SWTWidgetLocator), but expects a single matching Widgets.
	 * Not always the same as containedIn(0, ancestor)!
	 *
	 * @param ancestor
	 * @return This object
	 *
	 * @author Max Hohenegger
	 * @see SWTWidgetLocator#containedIn(int, SWTWidgetLocator)
	 */
	public <T> T containedIn(SWTWidgetLocator ancestor) {
		return containedIn(UNASSIGNED, ancestor);
	}

	/**
	 * Similar to in(int, SWTWidgetLocator), but works with arbitrary ancestors.
	 *
	 *
	 * @param index The index of the widget, since multiple widgets are expected to be found.
	 * @param ancestor Ancestor of the Widget in question.
	 * @return This object
	 *
	 * @author Max Hohenegger
	 * @see SWTWidgetLocator#in(int, SWTWidgetLocator)
	 */
	public <T> T containedIn(int index, SWTWidgetLocator ancestor) {
		setIndex(index);
		setAncestorInfo(ancestor);
		return (T) this;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Access
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#setParentInfo(com.windowtester.runtime.WidgetLocator)
	 */
	public void setParentInfo(WidgetLocator parentInfo) {
		/*
		 * this is a bit weird but since we're caching matchers, we need to invalidate them when parents
		 * are added
		 */
		matcher = null;
		super.setParentInfo(parentInfo);
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#setParentInfo(com.windowtester.runtime.WidgetLocator)
	 */
	public void setAncestorInfo(WidgetLocator ancestorInfo) {
		/*
		 * this is a bit weird but since we're caching matchers, we need to invalidate them when parents
		 * are added
		 */
		matcher = null;
		super.setAncestorInfo(ancestorInfo);
	}


	///////////////////////////////////////////////////////////////////////////
	//
	// Matching criteria
	//
	///////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		/*
		 * EXPERIMENTAL: create a reference on demand
		 */
		if (widget instanceof Widget)
			widget = WTRuntimeManager.asReference(widget);
		
		if (!(widget instanceof ISWTWidgetReference<?>))
			return false;
		// TODO[pq]: should we build a ref here for matching?
		ISWTWidgetReference<?> ref = (ISWTWidgetReference<?>)widget;
		return getMatcher().matches(ref);
	}

	/**
	 * Get the associated matcher.
	 * NOTE: since this class is serialized (and matchers may or may not be)
	 * the matcher is lazily initialized.
	 */
	protected ISWTWidgetMatcher getMatcher() {
		if (matcher == null)
			matcher = buildMatcher();
		return matcher;
	}
	
	/**
	 * Build the associated matcher
	 */
	protected ISWTWidgetMatcher buildMatcher() {
				
		/*
		 * First, query locator for identifying details.
		 */
		Class<?> cls                 = getTargetClass();
		String nameOrLabel           = getNameOrLabel();
		int index                    = getIndex();
		IWidgetMatcher<?> parentInfo = getParentInfo();
		IWidgetMatcher<?> ancestorInfo = getAncestorInfo();
		
		matcherBuilder.specify(ofClass(cls), visible());
		
		if (nameOrLabel != null) {
			matcherBuilder.specify(withText(nameOrLabel));
		}

		if (ancestorInfo != null) {
			matcherBuilder.specify(new ISWTWidgetMatcher() {

			public boolean matches(ISWTWidgetReference<?> widgetReference) {
				if (getAncestorInfo().matches(widgetReference)) {
					return true;
				}

				while (widgetReference != null) {
					widgetReference = widgetReference.getParent();
					if (widgetReference == null)
						return false;
					if (getAncestorInfo().matches(widgetReference))
						return true;
					}
					return false;
				}

			});
		}

		if (parentInfo != null) {
			matcherBuilder.scope(index, parentInfo);
		}
		return matcherBuilder.build();
//		return InternalMatcherBuilder.build2(this);
	}


	///////////////////////////////////////////////////////////////////////////
	//
	// Finding
	//
	///////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	@Override
	public IWidgetLocator[] findAll(IUIContext ui) {
		ISWTWidgetMatcher matcher = buildMatcher();
		ISWTWidgetReference<?>[] allWidgetsFound = SWTWidgetFinder.forActiveShell().findAll(matcher);
		//only for ".containedIn()" locators
		if (getAncestorInfo() != null){
			if (getIndex() == UNASSIGNED) {
				return allWidgetsFound;
			}
			if (getIndex() > allWidgetsFound.length - 1) {
				return new ISWTWidgetReference<?>[0];
			}
			ISWTWidgetReference<?>[] singleWidgetFound = new ISWTWidgetReference<?>[1];
			singleWidgetFound[0] = allWidgetsFound[getIndex()];
			return singleWidgetFound;
		}else{
			return allWidgetsFound;
		}
//		return Finder.findWidgets(matcher).in(Finder.activeShell());
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selector Click Actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		IWidgetReference ref = widget;
		if (widget == null)
			ref = (IWidgetReference)ui.find(this); //only do this lookup if necessary 
		Widget w = (Widget)ref.getWidget();
		Point offset = getXYOffset(ref, click);
		preClick(ref, offset, ui);
		//gtkPreClickHack(w);
		Widget clicked;
//		try {
//			clicked = doClick(click.clicks(), w, offset, click.modifierMask());
			clicked = w;
			new BasicSWTWidgetClickOperation((com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference<Widget>)ref).forClick(click).atOffset(offset.x, offset.y).execute();
			
			
//		} finally {
//			gtkPostClickHack(w);
//		}
		
		//note: this creates a legacy ref
		IWidgetReference clickedRef = WidgetReference.create(clicked, this);
		postClick(clickedRef, ui);
		
		return clickedRef;
	}
	
//	/**
//	 * @nooverride This method is not intended to be re-implemented or extended by clients.
//	 * @noreference This method is not intended to be referenced by clients.
//	 * @since 3.7.1
//	 */
//	protected void gtkPreClickHack(Widget w) {
//		// display thread will be blocked after the click so add a filter to catch ESC before the click
//		if (OS.isLinux()) {
///*			final Display d = w.getDisplay();
//			d.syncExec(new Runnable() {
//				public void run() {
//						escInterceptor.setCharged(true);
//						escInterceptor.setDisplay(d);
//						closeInterceptor.setCharged(true);
//						closeInterceptor.setDisplay(d);
//						traverseListener.setCharged(true);
//						traverseListener.setDisplay(d);
//	 				
//					//	d.addFilter(SWT.KeyUp, escInterceptor);
//					//	d.addFilter(SWT.Close, closeInterceptor);
//						d.addFilter(SWT.Traverse, traverseListener);
//
//				}
//			});*/
//		}
//	}
		
//	/**
//	 * @nooverride This method is not intended to be re-implemented or extended by clients.
//	 * @noreference This method is not intended to be referenced by clients.
//	 * @since 3.7.1
//	 */
//	protected void gtkPostClickHack(Widget w) {
//		if (OS.isLinux()) {
///*			Display  d1;			
//			if(!w.isDisposed()){			
//				d1 = w.getDisplay();
//			}else{
//				d1 = Display.getDefault();
//			}	
//					
//			final Display d = d1; 
//
//		new abbot.swt.Robot().keyRelease(SWT.ESC);
//			
//		if( OS.isLinux() ){
//				try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {				
//					e.printStackTrace();
//				}
//			}
//			d.syncExec(new Runnable() {
//				public void run() {
//					d.removeFilter(SWT.KeyUp, escInterceptor);
//					d.removeFilter(SWT.Close, closeInterceptor);
//					d.removeFilter(SWT.Traverse, traverseListener);
//				}
//				});				
//*/		}
//	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, final IWidgetReference widget, final IClickDescription click, String menuItemPath) throws WidgetSearchException {		
//		IWidgetReference ref = widget;
//		if (widget == null)
//			ref = (IWidgetReference)ui.find(this); //only do this lookup if necessary 
//		Widget w = (Widget)ref.getWidget();
//		Point offset = getXYOffset(w, click);
//		preClick(w, offset, ui);
//		Widget clicked = doContextClick(w, offset, menuItemPath);	
//		postClick(clicked, ui);
//		//note: this creates a legacy ref
//		return WidgetReference.create(clicked, this);

		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return showContextMenu(widget, click);
			}
		}, menuItemPath);
	}

	// TODO move this method into a reference class... but which one? 
	private MenuReference showContextMenu(IWidgetReference widget, IClickDescription click) {
		// Default context click location is NOT in the center of the widget
		SWTLocation location = SWTWidgetLocation.withDefaultTopLeft33(widget, click);
			
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, false);
		op.execute();
		return op.getMenu();
	}

//	/**
//	 * Perform the context click.  This is intended to be overridden in subclasses.
//	 */
//	protected Widget doContextClick(Widget w, Point offset, String menuItemPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		return _selector.contextClick(w, offset.x, offset.y, menuItemPath);
//	}
	
//	/**
//	 * Perform the click.  This is intended to be overridden in subclasses
//	 * @param clicks - the number of clicks
//	 * @param w - the widget to click
//	 * @param offset - the x,y offset (from top left corner)
//	 * @param modifierMask - the mouse modifier mask
//	 * @return the clicked widget
//	 */
//	protected Widget doClick(int clicks, Widget w, Point offset, int modifierMask) {
//		return _selector.click(w, offset.x, offset.y, modifierMask, clicks);
//	}

	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selector participation hooks
	//
	///////////////////////////////////////////////////////////////////////////
	
	protected void preClick(IWidgetReference reference, Point offset, IUIContext ui) {
		Widget w = (Widget) reference.getWidget();
		
		UIDriver driver = getLegacyUIDriver(ui);
		//use the driver to move the mouse button (if highlighting is on, this will
		//be delayed appropriately
		if (offset != null)
			driver.mouseMove(w, offset.x, offset.y);
		else
			driver.mouseMove(w);
		//use the driver to (possibly highlight)
		driver.highlight(w);
	}
	
	
	protected void postClick(IWidgetReference reference, IUIContext ui) {
		//use the driver to (possibly) pause
		getLegacyUIDriver(ui).postClickPause();
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// XY helpers
	//
	///////////////////////////////////////////////////////////////////////////
	

	/**
	 * Test this click to see if an offset is specified.
	 */
	protected boolean unspecifiedXY(IClickDescription click) {
		//dummy sentinel for now
		return click.relative() == -1;
	}

	/**
	 * Get the x,y offset for the click.
	 * @param click 
	 */
	public Point getXYOffset(IWidgetReference reference, IClickDescription click) {
		Widget w = (Widget) reference.getWidget();
		// TODO[pq]: we want this method to stay public but want to use an alternative version that uses our new widget refs...
		if (unspecifiedXY(click)) {
			/*
			 * crude hack to handle links where we need to find the offset of the href.
			 * NOTE: this only handles Links with exactly one href.  If the need arises
			 * we will need to revisit.
			 */
			if (w instanceof Link) {
				Rectangle offset = new LinkReference((Link)w).getOffset(0);
				//sanity check in case y is unset (in win32 case)
				if (offset.y == 0)
					offset.y = 4;
				if (offset != null)
					return new Point(offset.x + offset.width/2, offset.y/2);
			}
			// TODO[pq]: this reference should already be calculated and passed in 
			ISWTWidgetReference<?> ref = (ISWTWidgetReference<?>) WTRuntimeManager.asReference(w);
			Rectangle rect = ref.getDisplayBounds();
			return getUnspecifiedXYOffset(rect);
		}
		return new Point(click.x(), click.y());
	}

	protected Point getUnspecifiedXYOffset(Rectangle rect) {
		return new Point(rect.width/2, rect.height/2);
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Adaptation
	//
	///////////////////////////////////////////////////////////////////////////
	
	public static SWTWidgetLocator adapt(IWidgetLocator parent) {
		if (parent instanceof IWidgetReference)
			return new SWTWidgetReference((IWidgetReference)parent);
		if (parent instanceof SWTWidgetLocator)
			return (SWTWidgetLocator) parent;
		// fall-through
		return null;
	}
	
	protected UIDriver getLegacyUIDriver(IUIContext ui) {
		return (UIDriver)ui.getAdapter(UIDriver.class);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class<?> adapter) {
		if (adapter == ISWTWidgetMatcher.class)
			return getMatcher();
		return super.getAdapter(adapter);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Default reference Implementation
	//
	///////////////////////////////////////////////////////////////////////////
	
	// TODO[pq]: how does this untyped proxying widget ref play with our new typed widget ref story?
	static class SWTWidgetReference extends SWTWidgetLocator implements IWidgetReference {

		private static final long serialVersionUID = -4977467342559356305L;
		
		private final IWidgetReference _ref;
		
		public SWTWidgetReference(IWidgetReference ref) {
			super(Widget.class); //ugh?
			_ref = ref;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
		 */
		public IWidgetLocator[] findAll(IUIContext ui) {
			return new IWidgetLocator[] {this};
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
		 */
		public Object getWidget() {
			return _ref.getWidget();
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#matches(java.lang.Object)
		 */
		public boolean matches(Object widget) {
			return getWidget() == widget;
		}
		
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// IsVisibleLocator
	//
	////////////////////////////////////////////////////////////////////////////
	
	public boolean isVisible(IUIContext ui) throws WidgetSearchException {
		return ui.findAll(this).length > 0;
	}
	
	/**
	 * Create a condition that tests if the widget is visible.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isSelected(true)</code>
	 */
	public IUICondition isVisible() {
		return isVisible(true);
	}
	
	/**
	 * Create a condition that tests if the given the widget is visible.
	 * @param selected 
	 * @param expected <code>true</code> if the widget is expected to be selected, else
	 *            <code>false</code>
	 */            
	public IUICondition isVisible(boolean expected) {
		return new IsVisibleCondition(this, expected);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// HasFocusLocator
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Resolve the locator to a single object and determine if that object has focus.
	 * This method is ONLY supported for those subclasses that implement the
	 * {@link HasFocus} interface.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return <code>true</code> if the object has focus, else false
	 * @see IsVisible#isVisible()
	 */
	public boolean hasFocus(IUIContext ui) throws WidgetSearchException {
		return new HasFocusConditionHandler(this).hasFocus(ui);
	}
	
	/**
	 * Resolve the locator to a single object and determine if that object has focus.<p/>
	 * Used in an {@link IUIContext#ensureThat(com.windowtester.runtime.condition.IConditionHandler)} clause, the resulting condition
	 * can be used to ensure that the associated widget has focus.  For example:
	 * <p>
	 * <code>
	 *   ui.ensureThat(new ButtonLocator("OK").hasFocus());
	 * </code>
	 * </p>
	 * tests if the "OK" button has focus and if it does not, gives it focus.
	 * 
	 */
	public IUIConditionHandler hasFocus() {
		return new HasFocusConditionHandler(this);
	}
	
	
	// //////////////////////////////////////////////////////////////////////////
	//
	// IsEnabledLocator
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Resolve the locator to a single object and determine if that object is enabled.
	 * This method is ONLY supported for those subclasses that implement the
	 * {@link IsEnabled} interface. This method finds the widget then calls the
	 * {@link #isWidgetEnabled(Control)} on the UI thread to determine if the widget is
	 * enabled.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return <code>true</code> if the object is enabled, else false
	 * @see IsEnabled#isEnabled()
	 */
	public boolean isEnabled(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator found = ui.find(this);
		if (found instanceof IWidgetReference) {
			final Object widget = ((IWidgetReference) found).getWidget();
			final boolean[] result = new boolean[1];
			final Exception[] exception = new Exception[1];
			Display.getDefault().syncExec(new Runnable() {
				public void run()
				{
					try {
						result[0] = isWidgetEnabled((Widget)widget);
					} catch (Exception e) {
						exception[0] = e;
					}
				}
			});
			if (exception[0] != null)
				throw new WidgetSearchException(exception[0]);
			return result[0];
		}
		return false;
	}

	/**
	 * This is called by {@link #isEnabled(IUIContext)} on the UI thread to determine if
	 * the widget is enabled. Subclasses may override to provide additional or alternate
	 * behavior. This is only intended to be called by the {@link #isEnabled(IUIContext)}
	 * method and not by clients.
	 * 
	 * @param swtWidget the widget to be tested (not <code>null</code>)
	 * @return <code>true</code> if enabled, else <code>false</code>
	 */
	protected boolean isWidgetEnabled(final Widget swtWidget) throws WidgetSearchException {
		if (swtWidget instanceof Control) {
			Control control = (Control)swtWidget;
			return !control.isDisposed() && control.isEnabled();
		}
		
		if (swtWidget instanceof ToolItem){
			ToolItem item = (ToolItem)swtWidget;
			return !item.isDisposed() && item.isEnabled();
		}
		return false;
	}
	
	//TODO [author=Jaime] need for isVisible, or are we done with the existing isWidgetEnabled method just above?

	////////////////////////////////////////////////////////////////////////////
	//
	// HasText
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Resolve the locator to a single object and answer the text associated with it.
	 * This method is ONLY supported for those subclasses that implement the
	 * {@link HasText} interface. This method finds the widget then calls the
	 * {@link #getWidgetText(Control)} on the UI thread to obtain the widget text.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the text associated with that object (may be null)
	 */
	public String getText(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator found = ui.find(this);
		if (found instanceof IWidgetReference) {
			final Object widget = ((IWidgetReference) found).getWidget();
			if (widget instanceof Control) {
				final String[] result = new String[1];
				final Exception[] exception = new Exception[1];
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						try {
							result[0] = getWidgetText((Control) widget);
						}
						catch (Exception e) {
							exception[0] = e;
						}
					}
				});
				if (exception[0] != null)
					throw new WidgetSearchException(exception[0]);
				return result[0];
			}
		}
		return null;
	}

	/**
	 * This is called by {@link #getText(IUIContext)} on the UI thread to obtain the
	 * widget's text. Subclasses that implement {@link HasText} should override
	 * {@link #getText(IUIContext)} or this method to return text for the widget, because
	 * this method always throws a Runtime "not implemented" exception. This is only
	 * intended to be called by the {@link #getText(IUIContext)} method and not by
	 * clients.
	 * 
	 * @param swtWidget the widget from which text is to be obtained (not <code>null</code>)
	 * @return the widget's text or <code>null</code> if none
	 * @throws RuntimeException if this is not supported by this type of locator
	 */
	protected String getWidgetText(Control widget) throws WidgetSearchException {
		throw new InaccessableWidgetException("HasText not implemented by this locator: " + getClass().getName());
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	///////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getWidgetLocatorStringName()
	 */
	protected String getWidgetLocatorStringName() {
		return getClass().getSimpleName();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getWidgetLocatorStringName());
		sb.append("(");
		
		boolean needsDelim = false;
		
		String detail = getToStringDetail();
		if (detail != null) {
			sb.append(detail);
			needsDelim = true;
		}
		
		String label = getNameOrLabel();
		if (label != null) {
			if (needsDelim)
				sb.append(", ");
			//either way, we need to make sure we get a delim next time
			needsDelim = true;
			sb.append(label);
		}
			
		WidgetLocator parentInfo = getParentInfo();
		if (parentInfo != null) {
			if (needsDelim)
				sb.append(", ");
			int index = getIndex();
			if (index != UNASSIGNED) {
				sb.append(index).append(", ");
			}
			sb.append(parentInfo);
		}
		
		sb.append(")");
		
		return sb.toString();
	}

	/**
	 * Details used in <code>toString()</code> to specify things like name, type or
	 * path info.  Default returns <code>null</code>.
	 */
	protected String getToStringDetail() {
		/*
		 * A bit of a kludge.  We want subclasses to have no behavior but
		 * for the base class, use targetClass info.
		 */
		if (!getClass().equals(SWTWidgetLocator.class))
			return null;
		
		/*
		Class targetClass = getTargetClass();
		if (targetClass == null)
			return null;
		return targetClass.getName();
		*/
		String clsName = getTargetClassName();
		if (clsName == null)
			return null;
		return clsName;
	}

}
