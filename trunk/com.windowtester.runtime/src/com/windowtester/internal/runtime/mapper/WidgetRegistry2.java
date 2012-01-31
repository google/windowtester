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
package com.windowtester.internal.runtime.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.WidgetLocator;



/**
 * A type-safe map of String key values to IWidgetLocator objects.
 */
public class WidgetRegistry2 {

	/** The proxied Map store, mapping Strings to Info objects */
	private Map _register = new HashMap();
	
	/** An auxilliary map for reverse lookups */
	private Map/*<WidgetLocator,String>*/ _values = new HashMap();
	
	/**
	 * Associates the specified widget info with the specified key in this 
	 * registry.
	 * @param key - key with which the specified value is to be associated.
	 * @param info - info value to be associated with the specified key.
	 * @return previous value associated with specified key, or null  
	 * if there was no mapping for key.
	 */
	public WidgetLocator add(String key, IWidgetIdentifier info) {
		//first store the value for reverse lookups:
		_values.put(info, key);
		//next register the key
		return (WidgetLocator)_register.put(key, info);
	}
	
	/**
	 * Returns the info value to which this registry maps the specified key. 
	 * @param key - key whose associated info object is to be returned.
	 * @return the value to which this map maps the specified key, or null if the 
	 * registry contains no mapping for this key.
	 */
	public IWidgetIdentifier get(String key) {
		return (IWidgetIdentifier)_register.get(key);
	}
	
	/**
	 * Returns the String key which maps to the specified value. 
	 * @param info - info whose associated key is to be returned.
	 * @return the associated key, or null if the registry contains no mapping 
	 * for this value.
	 */
	public String getKey(IWidgetIdentifier info) {
		return (String)_values.get(info);
	}
	
	/**
	 * Removes the mapping for this key from this map if it is present.
	 * @param key - key whose mapping is to be removed from the registry.
	 * @return previous value associated with specified key, or null  
	 * if there was no mapping for key.
	 */
	public IWidgetIdentifier remove(String key) {
		//first remove the value from the reverse-lookup map
		IWidgetIdentifier toRemove = get(key);
		_values.remove(toRemove);
		//next: remove from the register
		return (IWidgetIdentifier)_register.remove(key);
	}
	
	/**
	 * Returns true if this registry maps one or more keys to the specified value.
	 * @param info - info whose presence in this registry is to be tested.
	 * @return true if this registry maps one or more keys to the specified value.
	 */
	public boolean containsValue(IWidgetIdentifier info) {
		return _register.containsValue(info);
	}
	
	/**
	 * Returns true if this registry contains a mapping for the specified key.
	 * @param key - key whose presence in this registry is to be tested.
	 * @return true if this registry contains a mapping for the specified key.
	 */
	public boolean containsKey(String key) {
		return _register.containsKey(key);
	}

	
	/**
	 * Returns a set view of the mappings contained in this registry. Each element 
	 * in the returned set is a Map.Entry. The set is backed by the registry, so changes 
	 * to the map are reflected in the set, and vice-versa. If the registry is modified 
	 * while an iteration over the set is in progress, the results of the iteration 
	 * are undefined. 
	 * @see Map#entrySet()
	 * @return a set view of the mappings contained in this map.
	 */
	public Set getEntries() {
		return _register.entrySet();
	}

}
