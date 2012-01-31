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
package com.windowtester.swt.util;


import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Manages the images and image descriptors implementing an efficient cache.  This
 * implementations caches these objects in a {@link org.eclipse.jface.resource.ImageRegistry}
 * instance.
 * 
 * <p>Before using an instance of this class it is necessary to invoke the 
 * {@link #initialize()} method.
 * 
 * <p>This implementation is based on the 
 * {@link org.eclipse.jdt.internal.ui.JavaPluginImages} class.
 */
abstract public class ImageManager
{
	/*
	 * Image directory prefixes.
	 */
	public static final String T_LCL		= "lcl16";
	public static final String T_TOOL		= "tool16";
	public static final String T_VIEW		= "view16";
	public static final String T_OBJ		= "obj16";
	public static final String T_OVR		= "ovr16";
	public static final String T_WIZBAN		= "wizban";
	
	/**
	 * URL to the plugin's icon base directory. 
	 */
	private URL iconBaseURL;
	
	/**
	 * Image registry used to cache the images and image descriptors.
	 */
	private ImageRegistry imageRegistry;
	
	/**
	 * Indicates if the images were loaded to this image manager.
	 */
	private boolean imagesWereLoaded = false;
	
	/**
	 * Initializes this image manager.  Nothing happens if this method is 
	 * invoked after a previous initialization.
	 * 
	 * <p>This method is not supposed to be invoked more than once.
	 * 
	 * @param iconBaseURL The url to the plugin's icon base directory.
	 * @param imageRegistry The image registry to cache the images and image
	 * descriptors.
	 */
	public void initialize(URL iconBaseURL, ImageRegistry imageRegistry)
	{
		this.iconBaseURL = iconBaseURL;
		this.imageRegistry = imageRegistry;
	}
	
	/**
	 * Checks if the images are loaded in the cache.  The variable 
	 * <code>imagesWereLoaded</code> is used to ensure that they won't
	 * be loaded more then once.
	 */
	protected void checkImages()
	{
		if(imagesWereLoaded)
			return;
			
		addImages();
		imagesWereLoaded = true;
	}

	/**
	 * Subclasses are supposed to add <b>all</b> the images to this manager in this 
	 * method implementation.
	 * 
	 * <p>The images should be added by invoking one the <code>add</code> 
	 * methods.
	 */
	abstract protected void addImages();
	
	/**
	 * Creates an image descriptor for a given image directory prefix and 
	 * image name adding it to the image registry.
	 * @param prefix
	 * @param name
	 * @return ImageDescriptor
	 */
	protected ImageDescriptor add(String prefix, String name)
	{
		ImageDescriptor imageDescriptor = createImageDescriptor(prefix, name);
		if(imageDescriptor != null && imageRegistry != null)
			imageRegistry.put(name, imageDescriptor);
		return imageDescriptor;
	}
	
	/**
	 * Creates an image descriptor for a given image directory prefix, directory
	 * type and image name adding it to the image registry.
	 * 
	 * <p>This method is typically used to register action images.  For example, to 
	 * register <i>clcl16/open.gif</i>, <i>dlcl16/open.gif</i> and <i>elcl16/open.gif</i>
	 * client should execute <pre>addManaged("c", T_LCL, "open.gif")</pre>, 
	 * <pre>add("d", T_LCL, "open.gif")</pre> and 
	 * <pre>add("e", T_LCL, "open.gif")</pre>.
	 * 
	 * @param prefixType
	 * @param prefix
	 * @param name
	 * @return ImageDescriptor
	 */
	protected ImageDescriptor add(String prefixType, String prefix, String name)
	{
		ImageDescriptor imageDescriptor = createImageDescriptor(prefixType + prefix, name);
		if(imageDescriptor != null)
			imageRegistry.put(prefixType + name, imageDescriptor);
		return imageDescriptor;
	}

	/**
	 * Adds an image descriptor to this image manager.
	 * @param key
	 * @param imageDescriptor
	 * @return <code>true</code> if the image descriptor was added of <code>false</code> 
	 * if <code>key</code> or <code>imageDescriptor</code> is <code>null</code> or if the 
	 * default image registry has already the specified <code>key</code>.
	 * @throws IllegalArgumentException if the key already exists
	 */
	protected boolean add(String key, ImageDescriptor imageDescriptor)
	throws IllegalArgumentException
	{
		if((key == null) || (imageDescriptor == null))
			return false;
			
		imageRegistry.put(key, imageDescriptor);
		return true;
	}

	/**
	 * Creates an image descriptor for a given image directory prefix and 
	 * image name.  The image prefix is appended to the 
	 * {@link #iconBaseURL base icon url}
	 * @param prefix
	 * @param name
	 * @return ImageDescriptor
	 */
	protected ImageDescriptor createImageDescriptor(String prefix, String name)
	{
		try
		{
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		}
		catch (MalformedURLException e)
		{
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	/**
	 * Creates a URL for a given image directory prefix and 
	 * image name.
	 * @param prefix
	 * @param name
	 * @return URL
	 * @throws MalformedURLException
	 */
	protected URL makeIconFileURL(String prefix, String name)
	throws MalformedURLException
	{
		if (iconBaseURL == null)
			throw new MalformedURLException();
			
		StringBuffer buffer= new StringBuffer(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(iconBaseURL, buffer.toString());
	}
	
	/**
	 * Returns the image descriptor associated to a key.
	 * @param key
	 * @return ImageDescriptor
	 */
	public ImageDescriptor getImageDescriptor(String key)
	{
		checkImages();
		return imageRegistry.getDescriptor(key);
	}

	/**
	 * Returns the image associated to a key. 
	 * image registry.
	 * @param key
	 * @return Image
	 */
	public Image getImage(String key)
	{
		checkImages();
		return imageRegistry == null ? null : imageRegistry.get(key);
	}

	/**
	 * Returns the image descriptor associated to a prefix type and key.  The
	 * prefix type is the prefix of the directory such as "c", "d" and "e" in
	 * "cview16", "dview16" and "eview16".
	 * @param prefixType
	 * @param key
	 * @return ImageDescriptor
	 */
	public ImageDescriptor getImageDescriptor(String prefixType, String key)
	{
		return getImageDescriptor(prefixType + key);
	}
	
	/**
	 * Returns the image associated to a prefix type and key.  The
	 * prefix type is the prefix of the directory such as "c", "d" and "e" in
	 * "cview16", "dview16" and "eview16".
	 * @param prefixType
	 * @param key
	 * @return ImageDescriptor
	 */
	public Image getImage(String prefixType, String key)
	{
		return getImage(prefixType + key);
	}
	
	/**
	 * Sets all the image descriptors of a given action.  This method assumes
	 * the following convention:<OL>
	 * <LI>The prefix type "e" is used for the main image</LI>
	 * <LI>The prefix type "c" is used for the hover image</LI>
	 * <LI>The prefix type "d" is used for the disabled image</LI>
	 * </OL>
	 * 
	 * @param action
	 * @param name
	 */
	public void setImageDescriptors(IAction action, String name)
	{
		ImageDescriptor imageDescriptor = getImageDescriptor("d", name);
		if(imageDescriptor != null)
			action.setDisabledImageDescriptor(imageDescriptor);

		imageDescriptor = getImageDescriptor("c", name);
		if(imageDescriptor != null)
			action.setHoverImageDescriptor(imageDescriptor);

		imageDescriptor = getImageDescriptor("e", name);
		if(imageDescriptor != null)
			action.setImageDescriptor(imageDescriptor);
	}
}
