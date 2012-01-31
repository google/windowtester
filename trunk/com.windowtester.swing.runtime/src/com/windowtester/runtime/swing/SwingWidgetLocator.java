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
package com.windowtester.runtime.swing;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Point;
import java.awt.Window;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.JLabel;

import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.matcher.CompoundMatcher;
import com.windowtester.internal.runtime.matcher.ExactClassMatcher;
import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.internal.swing.condition.HasFocusConditionHandler;
import com.windowtester.internal.swing.matcher.ClassByNameMatcher;
import com.windowtester.internal.swing.matcher.ClassMatcher;
import com.windowtester.internal.swing.matcher.HierarchyMatcher;
import com.windowtester.internal.swing.matcher.IndexMatcher;
import com.windowtester.internal.swing.matcher.NameOrTextMatcher;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.InaccessableWidgetException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsVisible;
import com.windowtester.runtime.condition.IsVisibleCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.locator.JCheckBoxLocator;
import com.windowtester.runtime.swing.locator.JRadioButtonLocator;
import com.windowtester.runtime.swing.locator.JTableItemLocator;
import com.windowtester.runtime.swing.locator.JToggleButtonLocator;


/**
 * A class that captures Swing hierarchy (containment) relationships between widgets for use
 * in widget identification.
 */
public class SwingWidgetLocator extends com.windowtester.runtime.WidgetLocator 
	implements IUISelector, IDiagnosticParticipant, IsVisible {

	/*
	 * NOTE: this class is serializable and uses the default serialization scheme.
	 * This should _not_ be a problem (hierarchies are not too deep); still, we
	 * could consider a custom serialization scheme.
	 * 
	 * ADDENDUM [11/04/06]: the matcher is transient.  As a consequence, it does not
	 * get serialized.  In general this should be OK, since serialization
	 * is intended for the case where locators are passed over the wire
	 * for use in codegen.  That said, we might remedy this in the future by,
	 * for instance, making the matcher lazily initialized.
	 */
	
	private static final long serialVersionUID = -6896731161658482507L;

	/** Delegate matcher 
     *  NOTICE: the matcher is transient.
	 */
	protected transient IWidgetMatcher _matcher; //TODO: build up in constructor
	
	
	/**
	 * Create an instance that identifies an SWT widget by its class name.
	 * @param className the target widget's fully qualified class name
	 * @since 3.8.1
	 */
	public SwingWidgetLocator(String className) {
		this(className, null);
	}
		
	/**
	 * Create an instance that identifies an SWT widget by its class name.
	 * @param className the target widget's fully qualified class name
	 * @param parent the target's parent
	 * @since 3.8.1
	 */
	public SwingWidgetLocator(String className, SwingWidgetLocator parent) {
		this(className, UNASSIGNED, parent);
	}
	
	/**
	 * Create an instance that identifies an SWT widget by its class name.
	 * @param className the target widget's fully qualified class name
	 * @param index the target's index relative to its parent
	 * @param parent the target's parent
	 * @since 3.8.1
	 */
	public SwingWidgetLocator(String className, int index, SwingWidgetLocator parent) {
		super(ClassReference.forName(className), null, index, parent);
		_matcher = ClassByNameMatcher.create(className);
		if (index != UNASSIGNED)
			_matcher = IndexMatcher.create(_matcher, index);
		
		if (parent != null){
			if (index != UNASSIGNED)
				_matcher = HierarchyMatcher.create(_matcher, parent.getMatcher(), index);
			else
				_matcher = HierarchyMatcher.create(_matcher,parent.getMatcher());
		}
			
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 */
	public SwingWidgetLocator(Class cls) {
		this(cls,(SwingWidgetLocator)null);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param index - the target's index relative to its parent
	 */
	public SwingWidgetLocator(Class cls, int index) {
		this(cls, null, index);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 */
	public SwingWidgetLocator(Class cls, String nameOrLabel) {
		this(cls, nameOrLabel,(SwingWidgetLocator)null);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param parentInfo - the target's parent info
	 */
	public SwingWidgetLocator(Class cls, SwingWidgetLocator parentInfo) {
		this(cls, null,parentInfo);
	}
	
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param index - the target's index relative to its parent
	 * @param parentInfo - the target's parent info
	 */
	public SwingWidgetLocator(Class cls, int index, SwingWidgetLocator parentInfo) {
		this(cls, null,index, parentInfo);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param index - the target's index relative to its parent
	 * 
	 */
	public SwingWidgetLocator(Class cls, String nameOrLabel, int index) {
		this(cls, nameOrLabel, index,null);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param parentInfo - the target's parent info
	 */
	public SwingWidgetLocator(Class cls, String nameOrLabel, SwingWidgetLocator parentInfo) {
		this(cls, nameOrLabel,UNASSIGNED,parentInfo);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param index - the target's index relative to its parent
	 * @param parentInfo - the target's parent info
	 */
	public SwingWidgetLocator(Class cls, String nameOrLabel, int index, SwingWidgetLocator parentInfo) {
		super(cls, nameOrLabel, index, parentInfo);
		// create the matcher
		// components such as buttons, lists and tables are matched by whether they are 
		// an instance of their respective swing classes, and not by exact class matching
	
		if (cls != null){
			if (this instanceof JButtonLocator || this instanceof JRadioButtonLocator )
				_matcher = ClassMatcher.create(cls);
			else if (this instanceof JCheckBoxLocator || this instanceof JToggleButtonLocator
					|| this instanceof JTableItemLocator )
				_matcher = ClassMatcher.create(cls);
			else _matcher = new ExactClassMatcher(cls);
		}
		if ((nameOrLabel != null) && (nameOrLabel.length() > 0))
			_matcher = new CompoundMatcher(_matcher, NameOrTextMatcher.create(nameOrLabel));
		
		if (index != UNASSIGNED)
			_matcher = IndexMatcher.create(_matcher, index);
		
		if (parentInfo != null){
			if (index != UNASSIGNED)
				_matcher = HierarchyMatcher.create(_matcher, parentInfo.getMatcher(), index);
			else
				_matcher = HierarchyMatcher.create(_matcher,parentInfo.getMatcher());
		}
	}

	
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		//delegates matching to component matcher
		//will be overriden in subclasses
		//commonly, subclasses will want to define their match to call super as well:
		// return doSubclassMatching(w) && super.matches(w);
		return _matcher.matches(widget);
	}
	
	public IWidgetMatcher getMatcher(){
		return _matcher;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Widget finding convenience methods 
	//
	///////////////////////////////////////////////////////////////////////////////
	
	protected Component findComponent(IUIContext ui) throws WidgetSearchException {
		//we know this is a widget reference
		WidgetReference widgetReference = (WidgetReference) ui.find(this);
		//get the widget
		return (Component)widgetReference.getWidget();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Basic click functionality  
	//
	///////////////////////////////////////////////////////////////////////////////
	
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		Component component = (Component)widget.getWidget();
		Point offset = getXYOffset(component, click);
		Component clicked = doClick(ui, click.clicks(), component, offset, click.modifierMask());		
		return WidgetReference.create(clicked, this);
	}
	
	/**
	 * Perform the click.  This is intended to be overridden in subclasses
	 * @param clicks - the number of clicks
	 * @param w - the widget to click
	 * @param offset - the x,y offset (from top left corner)
	 * @param modifierMask - the mouse modifier mask
	 * @return the clicked widget
	 */
	protected Component doClick(IUIContext ui, int clicks, Component c, Point offset, int modifierMask) {
		return ((UIContextSwing)ui).getDriver().click(clicks,c, offset.x, offset.y, modifierMask);
	}

	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath) throws WidgetSearchException {
		Component component = (Component)widget.getWidget();
		//TODO: hook up xys
		Component clicked = ((UIContextSwing)ui).getDriver().contextClick(component, menuItemPath);
		return WidgetReference.create(clicked, this);
	}
	
	/**
	 * Get the x,y offset for the click.
	 * @param click 
	 */
	public Point getXYOffset(Component c, IClickDescription click) {
		if (unspecifiedXY(click)) {
			return new Point(c.getWidth()/2, c.getHeight()/2);
		}
		return new Point(click.x(), click.y());
	}
	
	/**
	 * Test this click to see if an offset is specified.
	 */
	protected boolean unspecifiedXY(IClickDescription click) {
		//dummy sentinel for now
		return click.relative() == -1;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////
	// Diagnostics
	//
	/////////////////////////////////////////////////////////////////////////
	/**
	 * Provide additional diagnostic information
	 */
	public void diagnose(IDiagnostic diagnostic) {
		Hierarchy h = AWTHierarchy.getDefault();
		Iterator iter = h.getRoots().iterator();
		while (iter.hasNext()) {
        	Component c = (Component)iter.next();
        	if (((Window)c).isActive()){
        		getAllChildren(c,h,diagnostic);
        	}
        	else {
        			if (c.getClass().getName().equals("javax.swing.SwingUtilities$SharedOwnerFrame")){
        				Window[] windows = ((Window)c).getOwnedWindows();
        				for (int i = 0; i< windows.length; i++){
        					if (windows[i].isActive())
        						getAllChildren(c,h,diagnostic);
        				}
        			}
       		}
        }
	}
		
		
	private void getAllChildren(Component c, Hierarchy h,IDiagnostic diagnostic){
		
		Iterator iter = h.getComponents(c).iterator();
		while (iter.hasNext()) {
			Component component = (Component)iter.next();
			getAllChildren(component,h, diagnostic);
		}
		if (c instanceof Frame || c instanceof Dialog ){
			diagnostic.attribute("class", c.getClass().toString());
			if (c.getName()!= null)
				diagnostic.attribute("name", c.getName());
			diagnostic.attribute("title", getComponentText(c));
		}
		else if (c.getClass().equals(getTargetClass())){
			diagnostic.attribute("class", c.getClass().toString());
			if (c.getName()!= null)
				diagnostic.attribute("name", c.getName());
			diagnostic.attribute("text", getComponentText(c));
		}
		
	}
	
	private String getComponentText(Component w){
		
		if (w instanceof Button)
			return ((Button)w).getLabel();
		if (w instanceof Checkbox)
			return ((Checkbox)w).getLabel();
		if (w instanceof Choice)
			return ((Choice)w).getSelectedItem();
		if (w instanceof Label)
			return ((Label)w).getText();
		if (w instanceof AbstractButton) 
			return ((AbstractButton)w).getText();
		if (w instanceof JLabel)
			return ((JLabel)w).getText();
		if (w instanceof Dialog)
			return ((Dialog)w).getTitle();
		if (w instanceof Frame)
			return ((Frame)w).getTitle();
		
		return "";
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// HasText
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Resolve the locator to a single object and answer the text associated with it.
	 * This method is ONLY supported for those subclasses that implement the
	 * {@link HasText} interface. This method finds the widget then calls the
	 * {@link #getWidgetText(Component)} to obtain the widget text.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the text associated with that object (may be null)
	 */
	public String getText(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator found = ui.find(this);
		if (found instanceof IWidgetReference) {
			Object widget = ((IWidgetReference) found).getWidget();
			String result = getWidgetText((Component)widget);
			return result;	
		}
		return null;
	}
	
	
	/**
	 * This is called by {@link #getText(IUIContext)} to obtain the
	 * widget's text. Subclasses that implement {@link HasText} should override
	 * {@link #getText(IUIContext)} or this method to return text for the widget, because
	 * this method always throws a Runtime "not implemented" exception. This is only
	 * intended to be called by the {@link #getText(IUIContext)} method and not by
	 * clients.
	 * 
	 * @param widget the widget from which text is to be obtained (not <code>null</code>)
	 * @return the widget's text or <code>null</code> if none
	 * @throws RuntimeException if this is not supported by this type of locator
	 */
	protected String getWidgetText(Component widget) throws WidgetSearchException {
		throw new InaccessableWidgetException("HasText not implemented by this locator: " + getClass().getName());
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// IsEnabledLocator
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Resolve the locator to a single object and determine if that object is enabled.
	 * This method is ONLY supported for those subclasses that implement the
	 * {@link IsEnabled} interface. This method finds the widget then calls the
	 * {@link #isWidgetEnabled(Component)}  to determine if the widget is
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
			boolean result = isWidgetEnabled((Component)widget);
			return result;
		}
		return false;
	}

	/**
	 * This is called by {@link #isEnabled(IUIContext)} on the UI thread to determine if
	 * the widget is enabled. Subclasses may override to provide additional or alternate
	 * behavior. This is only intended to be called by the {@link #isEnabled(IUIContext)}
	 * method and not by clients.
	 * 
	 * @param widget the widget to be tested (not <code>null</code>)
	 * @return <code>true</code> if enabled, else <code>false</code>
	 */
	protected boolean isWidgetEnabled(Component widget) throws WidgetSearchException {
		return widget.isEnabled();
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
	
	
	
}
