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

import java.util.Set;

import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

import com.windowtester.finder.swt.BasicFinder2;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.swt.mapper.IKeyGenerator;
import com.windowtester.swt.mapper.SmartKeyGenerator;
import com.windowtester.swt.mapper.WidgetRegistry;

/**
 * A top-level service that maps widgets to String handles.
 * 
 * @author Phil Quitslund
 */
public class WidgetMapper {

	/** The backing widget registry */
	private final WidgetRegistry _registry = new WidgetRegistry();

	/** The finder used to retrieve widgets */
	private abbot.finder.swt.WidgetFinder _finder = BasicFinder2.getDefault();
	
	/** The KeyGenerator for generating new widget keys */
	private final IKeyGenerator _keyGen;
	
	/**
	 * Create an instance.
	 * @param keyGen - the key generator to use for generating new keys.
	 */
	public WidgetMapper(IKeyGenerator keyGen) {
		_keyGen = keyGen;
	}

	/**
	 * Create an instance using the default key generator.
	 */
	public WidgetMapper() {
		this(new SmartKeyGenerator());/** A default key generator, in case none is specified */
	}

	/**
	 * Finds the Widget associated with the given key.
	 * @param key - the key in question.
	 * @return the associated widget.
	 * @throws WidgetNotFoundException
	 * @throws MultipleWidgetsFoundException
	 */
	public Widget find(String key) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		WidgetLocator info = _registry.get(key);
		if (info == null)
			throw new UnregisteredKeyException();
		Matcher m = WidgetLocatorService.getMatcher(info);
		try {
			return _finder.find(m);
		} catch (abbot.finder.swt.WidgetNotFoundException wnfe) {
			//replace/rethrow with our own exception
			throw new WidgetNotFoundException(wnfe.getMessage());
		} catch (abbot.finder.swt.MultipleWidgetsFoundException mwfe) {
			//replace/rethrow with our own exception
			throw new MultipleWidgetsFoundException(mwfe.getMessage());
		}
	}

	/**
	 * Registers this info object, returning its associated key.
	 * @param info - the info object to register.
	 * @return the key associated with this info object
	 */
	public String register(WidgetLocator info) {
		if (_registry.containsValue(info))
			return _registry.getKey(info);
		
		//if not already registered, generate a key and register
		String key = null;
		//if it's named, register its name as a key unless there's a conflict
		String name = info.getData("name");
		if (name != null && !_registry.containsKey(name))
			key = name;
		else
			key = _keyGen.generate(info);
		
		register(key, info);
		return key;	
	}
		
		
	/**
	 * Registers this key, info pair.
	 * @param key - the key to register.
	 * @param info - the associated info object.
	 */
	public void register(String key, WidgetLocator info) {
		WidgetLocator old = _registry.add(key, info);
		if (old != info) {
			/*
			 * this case is no longer being treated as an exception.
			 * We'll log it nonetheless though.
			 * 
			 */
			//log(new IllegalArgumentException("key registered twice with different values"));
			log("key: " + key + " registered twice to different values (clobbering old value)");
		}	
	}
	
	/**
	 * Returns true if this map contains a mapping for the specified key.
	 * @param key - key whose presence in this map is to be tested.
	 * @return true if this mapper's registry contains a mapping for the specified key.
	 */
	public boolean containsKey(String key) {
		return _registry.containsKey(key);
	}
	
	/**
	 * Get the locator associated with this key, or null if there is none.
	 * @param key - the key of interest
	 * @return the associated locator (or null)
	 */
	public WidgetLocator getLocator(String key) {
		return _registry.get(key);
	}
	
	/**
	 * Returns a set view of the mappings contained in this mapper. Each element 
	 * in the returned set is a Map.Entry. The set is backed by the mapper, so changes 
	 * to the mapper are reflected in the set, and vice-versa. If the mapper is modified 
	 * while an iteration over the set is in progress, the results of the iteration 
	 * are undefined. 
	 * @see WidgetRegistry#getEntries()
	 * @return a set view of the mappings contained in this map.
	 */
	public Set getMappings() {
		return _registry.getEntries();
	}
	
	

	
	////////////////////////////////////////////////////////////////////////////
	//
	// Internal
	//
	////////////////////////////////////////////////////////////////////////////
	
	
	private void log(String msg) {
		LogHandler.log(msg);
	}


	/**
	 * Exception signaling that a widget was sought for a key that was not registered.
	 * @author Phil Quitslund
	 */
	public static final class UnregisteredKeyException extends RuntimeException {

		private static final long serialVersionUID = -5996967689887211859L;
		
	}



}
