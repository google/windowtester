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
package com.windowtester.runtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.bundle.IBundleReference;
import com.windowtester.internal.runtime.finder.FinderFactory;

/**
 * Base class for Widget locators.  Widget locators capture hierarchy (containment) relationships between widgets for use
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
 */ 
public class WidgetLocator implements Serializable, IWidgetIdentifier, IAdaptable, com.windowtester.runtime.locator.IWidgetLocator {

	/*
	 * NOTE: this class is serializable and uses the default serialization scheme.
	 * This should _not_ be a problem (hierarchies are not too deep); still, we
	 * could consider a custom serialization scheme.
	 */
	
	private static final long serialVersionUID = 7772528976750829834L;

	/** A sentinel value, indicating an unassigned index */
	public static final int UNASSIGNED = -1;
	
	
	/** the reference to the target class */
	private ClassReference classRef;
	/** The target widget's name or label */
	private final String nameOrLabel;
	/** The target's index relative to its parent */
	private int index;
	/** The target's parent info */
	private WidgetLocator parentInfo;
	/** The target's ancestor info */
	private WidgetLocator ancestorInfo;
	/** A map for associated data key-value pairs */
	private HashMap<String, String> map;
	
	//required for subclass convenience
	protected WidgetLocator() {
		this(null);
	}

	/**
	 * @since 3.8.1
	 */
	protected WidgetLocator(ClassReference classRef, String nameOrLabel, int index, WidgetLocator parentInfo) {
		this.classRef    = classRef;
		this.nameOrLabel = nameOrLabel;
		this.index       = index;
		this.parentInfo  = parentInfo;
	}
	
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param index - the target's index relative to its parent
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class<?> cls, String nameOrLabel, int index, WidgetLocator parentInfo) {
		this(ClassReference.forBundleClass(cls), nameOrLabel, index, parentInfo);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param index - the target's index relative to its parent
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class<?> cls, int index, WidgetLocator parentInfo) {
		this(cls, null, index, parentInfo);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class<?> cls, String nameOrLabel, WidgetLocator parentInfo) {
		this(cls, nameOrLabel, UNASSIGNED, parentInfo);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param parentInfo - the target's parent info
	 */
	public WidgetLocator(Class<?> cls, WidgetLocator parentInfo) {
		this(cls, null, UNASSIGNED, parentInfo);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 */
	public WidgetLocator(Class<?> cls) {
		this(cls, (WidgetLocator)null);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 */
	public WidgetLocator(Class<?> cls, String nameOrLabel) {
		this(cls, nameOrLabel, null);
	}
	
	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param nameOrLabel - the target's name or label
	 * @param index - the target's index relative to its parent
	 */
	public WidgetLocator(Class<?> cls, String nameOrLabel, int index) {
		this(cls, nameOrLabel, index, null);
	}

	/**
	 * Create an instance.
	 * @param cls - the target class
	 * @param index - the target's index relative to its parent
	 */
	public WidgetLocator(Class<?> cls, int index) {
		this(cls, null, index, null);
	}
	
	/**
	 * Accept this visitor.
	 * @param visitor
	 */
	public void accept(IWidgetLocatorVisitor visitor) {
		visitor.visit(this);
		if (parentInfo != null)
			parentInfo.accept(visitor);
	}

	/**
	 * Get the <code>WidgetLocator</code> that describes this widget's parent.
	 * @return the parent's <code>WidgetLocator</code> object.
	 */
	public WidgetLocator getParentInfo() {
		return parentInfo;
	}
	
	/**
	 * Get the <code>WidgetLocator</code> that describes one of the widget's ancestors.
	 * @return the ancestor's <code>WidgetLocator</code> object.
	 */
	public WidgetLocator getAncestorInfo() {
		return ancestorInfo;
	}
	
	/**
	 * Get the name or label String that helps identify this widget.
	 * @return the subject's name or label
	 */
	public String getNameOrLabel() {
		return nameOrLabel;
	}
	
	/**
	 * Get the subject widget's class.
	 * @return the subject's class
	 */
	public Class<?> getTargetClass() {
		return classRef.getClassForName();
		
	}
	
	/**
	 * Get the subject widget's class, as a class reference. 
	 * @return the subject's class reference
	 */
	protected ClassReference getTargetClassRef() {
		return classRef;
	}
	
	/**
	 * Get the subject widget's class name
	 * @return the subject's class name
	 */
	public String getTargetClassName(){
		if (classRef != null)
			return classRef.getName();
		else 
			return null;
		
	}
	
	/**
	 * Set the parent <code>WidgetLocator</code>.
	 * @param parentInfo - the new parent <code>WidgetLocator</code>
	 */
	public void setParentInfo(WidgetLocator parentInfo) {
		this.parentInfo = parentInfo;
	}

	/**
	 * Set the ancestor <code>WidgetLocator</code>.
	 * @param ancestorInfo - the new ancestor <code>WidgetLocator</code>
	 */
	public void setAncestorInfo(WidgetLocator ancestorInfo) {
		this.ancestorInfo = ancestorInfo;
	}

	/**
	 * Get this widget's index relative to its parent widget.
	 * @return a 0-based relative index or UNASSIGNED if it is not indexed.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Set this widget's index relative to it's parent.
	 * @param index - the index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	
	/**
	 * Get a String representation of this <code>WidgetLocator</code>.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String name = getWidgetLocatorStringName();
		if (name != null) {
			sb.append(name).append("(");
		} else {
			sb.append("WidgetLocator(").append(classRef.getName());
			if (getNameOrLabel() != null)
				sb.append(", ");
		} 
		
		if (getNameOrLabel() != null) {
			sb.append('\"').append(getNameOrLabel()).append('\"');
			if (getIndex() != UNASSIGNED || getParentInfo() != null)
				sb.append(", ");
		}	
		if (index != UNASSIGNED) {
			sb.append(index).append(", ");
		}
		if (parentInfo != null)
			sb.append(parentInfo);
		
		sb.append(")");
 
		return sb.toString();
	}
	
	//Override to customize Locator name used in toString().
	protected String getWidgetLocatorStringName() {
		//default is null
		return null;
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
		//if (!_classRef.getClassForName().equals(other.getTargetClass()))
		ClassReference targetClassRef = other.getTargetClassRef();
		if (classRef == null && targetClassRef != null)
			return false;
		if (targetClassRef == null && classRef != null)
			return false;
		if (classRef != null && targetClassRef != null && !classRef.equals(targetClassRef))
			return false;
		
		//check name
		if (nameOrLabel == null) {
			if (other.nameOrLabel != null)
				return false;
		} else {
			if (!nameOrLabel.equals(other.nameOrLabel))
				return false;
		}
		
		//check index
		if (index != other.index)
			return false;
		
		//check parent
		if (parentInfo == null) {
			if (other.parentInfo != null)
				return false;
		} else {
			if (!parentInfo.equals(other.parentInfo))
				return false;
		}
		
		//check data map
		if (map == null) {
			if (other.map != null && !other.map.isEmpty())
				return false;
		} else {
			if (!map.equals(other.map))
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
		result = 37*result + index;
		//result = 37*result + ((_cls == null) ? 0 : + _cls.hashCode());
		result = 37*result + ((classRef == null) ? 0 : + classRef.hashCode());
		result = 37*result + ((nameOrLabel == null) ? 0 : + nameOrLabel.hashCode());
		result = 37*result + ((parentInfo == null) ? 0 : + parentInfo.hashCode());
		result += (map == null) ? 0 : map.hashCode();
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
	 * @return the value of the property or null if it has not been set
	 *
	 * @exception IllegalArgumentException if the key is null
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
	 * Returns the programmer defined property of the receiver
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
		return getDataMap().get(key);
	}
	
	/**
	 * Copies all programmer defined properties of the receiver
	 * into the specified locator.
	 * 
	 * @param locator the locator
	 */
	public void copyDataTo(WidgetLocator locator) {
		if (map != null && locator != null)
			map.putAll(locator.getDataMap());
	}
	
	private Map<String, String> getDataMap() {
		if (map == null)
			map = new HashMap<String, String>();
		return map;
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == IBundleReference.class)
			if (classRef instanceof IBundleReference)
				return (IBundleReference)classRef;
		return null;
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// Widget Finding
	//
	/////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public com.windowtester.runtime.locator.IWidgetLocator[] findAll(IUIContext ui) {
		//get the appropriate finder for this ui context
		return FinderFactory.getFinder(ui).findAll(this);
	}

	public boolean matches(Object widget) {
		// TODO Auto-generated method stub
		return false;
	}

}
