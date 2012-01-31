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
package com.windowtester.runtime.swt.internal.widgets;

import static com.windowtester.internal.runtime.util.ReflectionUtils.invoke;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.reflect.Reflector;
import com.windowtester.internal.runtime.util.ReflectionUtils;
import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.SafeCallable;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.SWTUtils;
import com.windowtester.runtime.swt.internal.matchers.ByTextMatcher;
import com.windowtester.runtime.swt.internal.widgets.finder.MatchCollector;

/**
 * Widget references are used to <em>test</em> or access widgets safely on the UI thread.
 * <p/>
 * Values returned by reference accessors should themselves be safely accessible. This
 * means that in cases where the accessors return other widgets, these returned widgets
 * are themselves wrapped in proxy widget references. The exception to this rule is
 * {@link #getWidget()} which returns the bare {@link Widget} instance. Access to this
 * instance directly should be avoided if at all possible since care needs to be take to
 * ensure that it is safely accessed from the UI thread.
 * @param <T> the type of the bare widget
 */
public class SWTWidgetReference<T extends Widget> extends AbstractSWTDisplayable
	implements ISWTWidgetReference<T>, IVisitable, ISearchable
{
	public static interface Visitor {
		<W extends Widget> void visit(SWTWidgetReference<W> widget);
		//note: not composites because non-composites such as Menus can have children 
		<T extends SWTWidgetReference<?>> void visitEnter(T composite);
		<T extends SWTWidgetReference<?>> void visitLeave(T composite);
		
	}
	
	/**
	 * A set of references used to build the results of the call to {@link SWTWidgetReference#getChildren()}. 
	 */
	public static class ChildSet {
		//preserves insertion order 
		final LinkedHashSet<SWTWidgetReference<?>> children = new LinkedHashSet<SWTWidgetReference<?>>();
		
		ChildSet add(SWTWidgetReference<?> ... refs){
			if (refs != null) {
				for (SWTWidgetReference<?> ref : refs) {
					if (ref != null)
						children.add(ref);
				}
			}
			return this;
		}
		
		SWTWidgetReference<?>[] toArray(){
			return children.toArray(emptyArray());
		}
	}
	
	
	protected final T widget;
	
	/**
	 * Constructs a new instance with the given widget.
	 * 
	 * @param w the widget.
	 */
	public SWTWidgetReference(T widget) {
		super(validate(widget).getDisplay());
		this.widget  = widget;
	}
	
	protected static <W extends Widget> W validate(W widget){
		//TODO[pq]: consider a better exception
		assertWidgetNotNull(widget); //TODO [pq]: should we assert not disposed?
		return widget;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getParent()
	 */
	public ISWTWidgetReference<?> getParent(){
		//overridden in subclasses
		// TODO make this method abstract
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getChildren()
	 */
	public final SWTWidgetReference<?>[] getChildren() {
		final ChildSet children = new ChildSet();
		SWTUtils.safeExec(new VoidCallable() {
			@Override
			public void call() throws Exception {
				setChildren(children);
			}
		});
		return children.toArray();
	}

	
	/**
	 * Add elements for inclusion in the list of children returned by {@link SWTWidgetReference#getChildren()}.
	 * Note that {@link ChildSet} subscribes to the {@link Set} contract in that it ensures that there are no duplicates.
	 * @param children the collecting child set
	 */
	protected void setChildren(ChildSet children){
		//implemented in subclasses that have children
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IWidgetReference#getWidget()
	 */
	public T getWidget(){
		return widget;
	}

	/**
	 * Proxy for {@link Widget#getDisplay()}.
	 */
	public DisplayReference getDisplayRef(){
		return displayRef;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getDisplayBounds()
	 */
	public Rectangle getDisplayBounds() {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				Control parent   = (Control) invoke(widget, "getParent");
				Rectangle bounds = (Rectangle) invoke(widget, "getBounds");
				return widget.getDisplay().map(parent, null, bounds);
			}
		});
	}
	
//	/**
//	 * Gets if the object's widget is enabled.
//	 * 
//	 * @return <code>true</code> if the widget is enabled.
//	 * @see Control#isEnabled()
//	 */
//	public boolean isEnabled() {
//		//TODO[pq]: compare with our impls.
//		if (widget instanceof Control)
//			return syncExec(new BooleanRunnable() {
//				public Boolean run() {
//					return isEnabledInternal();
//				}
//			});
//		return false;
//	}
//	/**
//	 * Gets if the widget is enabled.
//	 * <p>
//	 * This method is not thread safe, and must be called from the UI thread.
//	 * </p>
//	 * 
//	 * @return <code>true</code> if the widget is enabled.
//	 * @since 1.0
//	 */
//	protected boolean isEnabledInternal() {
//		try {
//			return ((Boolean) SWTUtils.invokeMethod(widget, "isEnabled")).booleanValue(); //$NON-NLS-1$
//		} catch (Exception e) {
//			return true;
//		}
//	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IWidgetReference))
			return false;
		return widget.equals(((IWidgetReference)obj).getWidget());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return widget.hashCode();
	}
	
	@Override
	public String toString() {
		return displayRef.execute(new SafeCallable<String>() {
			public String call() throws Exception {
				return SWTWidgetReference.this.getClass().getSimpleName() + " - " + widget.toString();
			}
			public String handleException(Throwable e) throws Throwable {
				return SWTWidgetReference.this.getClass().getSimpleName() + " - widget (?) " + e;
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	protected <R extends SWTWidgetReference<?>> R[] asReferencesOfType(Widget[] widgets, Class<? extends SWTWidgetReference<?>> k){
		//TODO[pq]: test me!
		R[] array = (R[]) ReflectionUtils.newArray(k, widgets.length);
		if (widgets.length == 0)
			return array;
		for (int i = 0; i < array.length; i++) {
			array[i] = (R) WTRuntimeManager.asReference(widgets[i]);
		}
		return array;
	}
	
	protected ControlReference<?>[] asControlReferences(Control[] controls){
		ControlReference<?>[] references = ReflectionUtils.newArray(ControlReference.class, controls.length);
		for (int i = 0; i < references.length; i++) {
			//references[i] = new ControlReference<Control>(controls[i]);
			references[i] = (ControlReference<?>) forWidget(controls[i]);
		}
		return references;
	}

	protected SWTWidgetReference<?>[] asReferences(Widget[] controls){
		SWTWidgetReference<?>[] references = ReflectionUtils.newArray(SWTWidgetReference.class, controls.length);
		for (int i = 0; i < references.length; i++) {
			//references[i] = new ControlReference<Control>(controls[i]);
			references[i] = (SWTWidgetReference<?>) forWidget(controls[i]);
		}
		return references;
	}
	
	public static SWTWidgetReference<?>[] newReferenceArray(int length){
		return ReflectionUtils.newArray(SWTWidgetReference.class, length);
	}
	
	public static SWTWidgetReference<?>[] emptyArray(){
		return ReflectionUtils.newArray(SWTWidgetReference.class, 0);
	}
	
	//NOTE: does null check
	@SuppressWarnings("unchecked")
	protected <R extends SWTWidgetReference<?>> R asReferenceOfType(Widget widget,
			Class<R> refType) {
		if (widget == null)
			return null;
		return (R) WTRuntimeManager.asReference(widget);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return new IWidgetLocator[]{this};
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		// TODO[pq]: implement this legacy matches APIs (or find workaround)
		return false;
	}
	
	// TODO[pq]: potential API?
	// TODO[pq]: finds implicitly should ignore not visible widgets?  how to search for invisibles?
	public ISWTWidgetReference<?>[] findWidgets(ISWTWidgetMatcher matcher){
		//implemented in terms of a visit:
		MatchCollector collector = new MatchCollector(matcher);
		return (ISWTWidgetReference<?>[]) collector.findMatchesIn(this).toArray(emptyArray());
	}
	

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IVisitable#accept(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor)
	 */
	public final void accept(Visitor visitor) {
		visitor.visit(this);
		visitor.visitEnter(this);
		visitAll(visitor, getChildren());
		visitor.visitLeave(this);
	}

	
	protected void visitIfNotNull(SWTWidgetReference.Visitor visitor, SWTWidgetReference<?> widget) {
		if (widget != null)
			widget.accept(visitor);
	}

	
	protected void visitAll(SWTWidgetReference.Visitor visitor, SWTWidgetReference<?>[] widgets) {
		for (SWTWidgetReference<?> widget : widgets) {
			visitIfNotNull(visitor, widget);
		}
	}
		
	
	public static ISWTWidgetReference<?> forWidget(Widget widget) {
		return (ISWTWidgetReference<?>) WTRuntimeManager.asReference(widget);
	}
	
	public static ControlReference<?> forControl(Control control) {
		return (ControlReference<?>) forWidget(control);
	}
	
	private static void assertWidgetNotNull(Object widget) {
		if (widget == null)
			throw new IllegalArgumentException("widget must not be null");
	}

	
	/**
	 * Gets the text associated with the underlying widget.  The text is retrieved using 
	 * the widget's <code>getText()</code> method if one is defined.  If there is no such method
	 * a <code>null</code> value is returned instead. If a client wants to know if the underlying widget
	 * supports the <code>getText()</code> protocol, they are encouraged to call {@link #hasText()} first.
	 * 
	 * @return the widget's text
	 */
	public String getText() {
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return (String) invoke(widget, "getText");
			}
		});
	}

	/**
	 * Gets the text associated with the underlying widget and prepares it for matching.
	 * <p/>
	 * As a general rule this post-processing strips things like accelerators.  So, for example,
	 * the menu item "Ne&w\tCtrl+N" would get simplified to the value "New".
	 * 
	 * @return the widgets's text suitable for matching
	 */
	public String getTextForMatching(){
		String text = getText();
		if (text == null)
			return text;
		return StringUtils.trimMenuText(text);
	}
	
	/**
	 * Test if this widget has associated text (as returned by a <code>getText()</code> method).
	 * 
	 * @return true if the widget has text, false otherwise
	 */
	public boolean hasText(){
		return Reflector.forObject(widget).supports("getText");
	}

	/**
	 * Get the underlying widget's name as set by calling <code>widget.setData("name", "widget.name");</code>
	 * @see Widget#setData(String, Object)
	 */
	public String getName() {
		Object name = getData("name");
		if (name == null)
			return null;
		return name.toString();
	}
	
	
	/**
	 * Proxy for {@link Widget#getData()}.
	 */
	public Object getData() {
		return displayRef.execute(new Callable<Object>() {
			public Object call() throws Exception {
				return widget.getData();
			}
		});
	}
	
	/**
	 * Proxy for {@link Widget#getData(String))}.
	 */
	public Object getData(final String key) {	
		return displayRef.execute(new Callable<Object>() {
			public Object call() throws Exception {
				return widget.getData(key);
			}
		});
	}
	
    /**
	 * Proxy for {@link Widget#getStyle()}.
	 * <p/>
	 * @return the style.
	 */
	public int getStyle(){
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getStyle();
			}
		});
	}

	/**
	 * Checks if the widget has the given style.
	 * 
	 * @param style the style.
	 * @return <code>true</code> if the widget has the specified style bit set, else
	 *         <code>false</code>.
	 */
	public boolean hasStyle(final int style) {
		if (style == SWT.NONE)
			return false;
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return !widget.isDisposed() && (widget.getStyle() & style) != 0;
			}
		});
	}
	
	/**
	 * Tests if the associated widget is visible.
	 */
	public boolean isVisible(){		
		//Basic legacy impl. to be pushed down
		try {
//			// ask the menu watcher if the given items menu is open
//			if (w instanceof MenuItem)
//				return MenuWatcher.getInstance(w.getDisplay()).isVisible(
//						(MenuItem) w);
			Control control = SWTUtils.getControl(widget);
			if (control.isDisposed())
				return false;
//			//for some reason Links return false when asked if "isVisible"...
//			if (control instanceof Link) {
//				return _controlTester.getVisible(control);
//			}
//			return _controlTester.isVisible(control);
			return SWTWidgetReference.forWidget(control).isVisible();
		} catch (SWTException e) {
			// ignore
		}

		return false;
	}
	
	public boolean isMatchedBy(ISWTWidgetMatcher matcher){
		return matcher.matches(this);
	}

	/**
	 * Proxy for {@link Widget#isDisposed()}.
	 * @deprecated If you call this method from a non-UI thread to guard
	 * against accessing a non-disposed widget, then there is an inherent
	 * race condition. Better to call isDisposed on the UI thread.
	 */
	public boolean isDisposed() {
		return widget.isDisposed();
	}

	public boolean isEnabled() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return (Boolean) invoke(widget, "isEnabled");
			}
		});
	}
	
	public ICondition isEnabled(final boolean enabled){
		return new ICondition(){
			public boolean test() {
				return isEnabled() == enabled;
			}			
			@Override
			public String toString() {
				return SWTWidgetReference.this.toString() + " to be enabled (" + enabled + ")";
			}
		};
	}
	
	
	private class WidgetHasTextCondition implements ICondition {
		private final String txt;
		private ByTextMatcher matcher;

		public WidgetHasTextCondition(String txt) {
			this.txt = txt;
			matcher = new ByTextMatcher(txt);
		}

		public boolean test() {
			return isMatchedBy(matcher);
		}
		
		@Override
		public String toString() {
			return SWTWidgetReference.this.toString() + " to have text :'" + txt +"'";
		}
	}
	
	public ICondition hasText(String txt) {
		return new WidgetHasTextCondition(txt);
	}

	/* (non-javadoc)
	 * @see ISWTWidgetReference#showPulldownMenu(IClickDescription)
	 */
	public MenuReference showPulldownMenu(IClickDescription click) {
		throw new RuntimeException(toString() + " does not have a pulldown menu");
	}
}
