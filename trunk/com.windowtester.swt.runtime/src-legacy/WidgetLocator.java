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
package com.windowtester.swt;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.windowtester.internal.runtime.finder.FinderFactory;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.selector.WidgetSelectorAdapterFactory;

/**
 * A class that captures hierarchy (containment) relationships between widgets for use
 * in widget identification.
 * <p>
 * For example, a widget identified by class and position relative to its
 * parent composite could be described so:
 * <pre>
 * 		new WidgetLocator(Text.class, 2,
 * 			   new WidgetLocator(Group.class, "addressGroup"));
 * </pre>
 * 
 * Effectively, this WidgetLocator instance describes the third Text widget in
 * the Group labeled "addressGroup".
 *
 * @deprecated Replaced by the {@link IWidgetLocator} class hierarchy
 */ 
public class WidgetLocator implements Serializable, com.windowtester.swt.IWidgetLocator, com.windowtester.runtime.locator.IWidgetLocator {

	/*
	 * NOTE: this class is serializable and uses the default serialization scheme.
	 * This should _not_ be a problem (hierarchies are not too deep); still, we
	 * could consider a custom serialization scheme.
	 */
	
	private static final long serialVersionUID = 7772528976750829834L;

	/** A sentinel value, indicating an unassigned index */
	public static final int UNASSIGNED = -1;
	
	/** The target class */
	private final Class _cls;
	/** The target widget's name or label */
	private final String _nameOrLabel;
	/** The target's index relative to its parent */
	private int _index;
	/** The target's parent info */
	private WidgetLocator _parentInfo;
	/** A map for associated data key-value pairs */
	private HashMap _map;
	
	/*
	 * Used to implement new API IWidgetLocator.
	 * Notice that it's transient since Matchers are not necessarly serializable.
	 */
	private transient IWidgetMatcher _matcher;

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param index - the target's index relative to its parent
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class cls, String nameOrLabel, int index, WidgetLocator parentInfo) {
		_cls         = cls;
		_nameOrLabel = nameOrLabel;
		_index       = index;
		_parentInfo  = parentInfo;
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param index - the target's index relative to its parent
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class cls, int index, WidgetLocator parentInfo) {
		this(cls, null, index, parentInfo);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class cls, String nameOrLabel, WidgetLocator parentInfo) {
		this(cls, nameOrLabel, UNASSIGNED, parentInfo);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class cls, WidgetLocator parentInfo) {
		this(cls, null, UNASSIGNED, parentInfo);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 */
	public WidgetLocator(Class cls) {
		this(cls, (WidgetLocator)null);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 */
	public WidgetLocator(Class cls, String nameOrLabel) {
		this(cls, nameOrLabel, null);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param index - the target's index relative to its parent
	 */
	public WidgetLocator(Class cls, String nameOrLabel, int index) {
		this(cls, nameOrLabel, index, null);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param index - the target's index relative to its parent
	 */
	public WidgetLocator(Class cls, int index) {
		this(cls, null, index, null);
	}
	
//	/**
//	 * Accept this visitor.
//	 * @param visitor
//	 */
//	public void accept(IWidgetLocatorVisitor visitor) {
//		visitor.visit(this);
//		if (_parentInfo != null)
//			_parentInfo.accept(visitor);
//	}

	/**
	 * Get the <code>WidgetLocator</code> that describes this widget's parent.
	 * @return the parent's <code>WidgetLocator</code> object.
	 */
	public WidgetLocator getParentInfo() {
		return _parentInfo;
	}
	
	/**
	 * Get the name or label String that helps identify this widget.
	 * @return the subject's name or label
	 */
	public String getNameOrLabel() {
		return _nameOrLabel;
	}
	
	/**
	 * Get the subject widget's class.
	 * @return the subject's class
	 */
	public Class getTargetClass() {
		return _cls;
	}
	
	/**
	 * Set the parent <code>WidgetLocator</code>.
	 * @param parentInfo - the new parent <code>WidgetLocator</code>
	 */
	public void setParentInfo(WidgetLocator parentInfo) {
		_parentInfo = parentInfo;
	}

	/**
	 * Get this widget's index relative to its parent widget.
	 * @return a 0-based relative index or UNASSIGNED if it is not indexed.
	 */
	public int getIndex() {
		return _index;
	}
	
	/**
	 * Set this widget's index relative to it's parent.
	 * @param index - the index.
	 */
	public void setIndex(int index) {
		_index = index;
	}

	/**
	 * Get a String representation of this <code>WidgetLocator</code>.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("WidgetLocator(").append(_cls.getName());

		if (_nameOrLabel != null)
			sb.append(", ").append(_nameOrLabel);
		if (_index != UNASSIGNED)
			sb.append(", ").append(_index);
		if (_parentInfo != null)
			sb.append(", ").append(_parentInfo);
		
		sb.append(")");
 
		return sb.toString();
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		
		//null check
		if (o == null)
			return false;
		
		//type check
		if (!(o instanceof WidgetLocator))
			return false;
		
		//self check
		if (o == this)
			return true;
		
		WidgetLocator other = (WidgetLocator)o;
		
		//check class
		if (!_cls.equals(other._cls))
			return false;
		
		//check name
		if (_nameOrLabel == null) {
			if (other._nameOrLabel != null)
				return false;
		} else {
			if (!_nameOrLabel.equals(other._nameOrLabel))
				return false;
		}
		
		//check index
		if (_index != other._index)
			return false;
		
		//check parent
		if (_parentInfo == null) {
			if (other._parentInfo != null)
				return false;
		} else {
			if (!_parentInfo.equals(other._parentInfo))
				return false;
		}
		
		//check data map
		if (_map == null) {
			if (other._map != null && !other._map.isEmpty())
				return false;
		} else {
			if (!_map.equals(other._map))
				return false;
		}
		
		//fall through
		return true;
	}
	
	/**
	 * Returns a hash code value for this <code>WidgetLocator</code> object. 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = 13;
		result = 37*result + _index;
		result = 37*result + ((_cls == null) ? 0 : + _cls.hashCode());
		result = 37*result + ((_nameOrLabel == null) ? 0 : + _nameOrLabel.hashCode());
		result = 37*result + ((_parentInfo == null) ? 0 : + _parentInfo.hashCode());
		result += (_map == null) ? 0 : _map.hashCode();
		return result;
	}

	
	
	
	/**
	 * Returns the programmer defined property of the receiver
	 * with the specified name, or null if it has not been set.
	 * <p>
	 * Data mappings allow programmers to associate arbitrary key-value 
	 * pairs with locator instances.
	 * </p>
	 *
	 * @param	key the name of the property
	 *
	 * @exception IllegalArgumentException if the key is <code>null</code>
	 * 
	 *
	 */
	public void setData(String key, String value) {
		/*
		 * NOTE: keys and values are NOT objects (as in widgets)
		 * this is because locators need to be serializable.
		 * An alternative is to have the be ISerializables...
		 * we could widen the interface if need be.
		 */
		if (key == null)
			throw new IllegalArgumentException("key must not be null");
		getDataMap().put(key, value);
	}
	
	/**
	 * Returns the pogrammer defined property of the receiver
	 * with the specified name, or null if it has not been set.
	 * <p>
	 * Data mappings allow programmers to associate arbitrary key-value 
	 * pairs with locator instances.
	 * </p>
	 * 
	 *
	 * @param	key the name of the property
	 * @return the value of the property or null if it has not been set
	 *
	 * @exception IllegalArgumentException if the key is null
	 */
	public String getData (String key) {
		if (key == null)
			throw new IllegalArgumentException("key must not be null");
		return (String) getDataMap().get(key);
	}
	
	
	
	private Map getDataMap() {
		if (_map == null)
			_map = new HashMap();
		return _map;
	}

	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	//
	// New API Compatability
	//
	///////////////////////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		IWidgetLocator[] locators = FinderFactory.getFinder(ui).findAll(this);
		//add UISelector behavior
		for (int i = 0; i < locators.length; i++) {
			locators[i] = WidgetSelectorAdapterFactory.create(locators[i]);
		}
		return locators;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
//in the process of refactoring out -- will fail if called at runtime		
//		if (_matcher == null)
//			_matcher = new AdapterFactory().adapt(WidgetLocatorService.getMatcher(this));
		return _matcher.matches(widget);
	}

	public String getTargetClassName() {
		
		return _cls.getName();
	}

}
