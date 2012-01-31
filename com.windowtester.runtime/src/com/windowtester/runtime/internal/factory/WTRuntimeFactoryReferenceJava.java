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
package com.windowtester.runtime.internal.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.swt.SWT;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.windowtester.internal.runtime.ProductInfo;

/**
 * A facade for an instance of {@link WTRuntimeFactory} when running outside the Eclipse
 * infrastructure such as when testing a Swing application or an SWT application without
 * the OSGi bundle framework.
 */
class WTRuntimeFactoryReferenceJava extends WTRuntimeFactoryReference
{
	private static final String FACTORIES_FILE_NAME = "javaRuntimeFactories.xml";
	private final String className;

	/**
	 * Find the known widget factories as defined in the swingFactories.xml file.
	 * 
	 * @return an array of references (not <code>null</code>, contains no
	 *         <code>null</code>s)
	 */
	static WTRuntimeFactoryReference[] createFactoryReferences() {
		Collection<WTRuntimeFactoryReference> result = new ArrayList<WTRuntimeFactoryReference>();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		DefaultHandler handler = new FactoryDeclarationHandler(result);
		InputStream stream = WTRuntimeFactoryReferenceJava.class.getResourceAsStream(FACTORIES_FILE_NAME);
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(stream, handler);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to read " + FACTORIES_FILE_NAME, e);
		}
		finally {
			try {
				stream.close();
			}
			catch (IOException e) {
				// Ignored
			}
		}
		return result.toArray(new WTRuntimeFactoryReference[result.size()]);
	}

	/**
	 * A handler for reading the runtime factory declarations from
	 * javaRuntimeFactories.xml
	 */
	private static final class FactoryDeclarationHandler extends DefaultHandler
	{
		private final Collection<WTRuntimeFactoryReference> result;
		private final String arch;
		private final String os;
		private final String ws;

		public FactoryDeclarationHandler(Collection<WTRuntimeFactoryReference> result) {
			this.result = result;
			String osName = System.getProperty("os.name");
			String osVersion = System.getProperty("os.version");
			os = getOS(osName, osVersion);
			ws = getWS(osName, osVersion);
			arch = getArch();
			String debugInfo = "WindowTester: " + ProductInfo.build + " - " + os + "," + ws + "," + arch;
//			System.out.println(debugInfo);
			WTRuntimeManager.setPlatformDebugInfo(debugInfo);
		}

		private String getArch() {
			String arch = System.getProperty("os.arch");
			//OSX returns "i386" which we want to normalize to x86
			if (arch.equals("i386"))
				arch = "x86";
			return arch;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (qName.equals(FACTORY_TAG)) {
				String className = attributes.getValue(CLASS_TAG);
				if (className == null)
					throw new RuntimeException("Missing class attribute in " + FACTORIES_FILE_NAME);
				if (className.length() == 0)
					throw new RuntimeException("Invalid class attribute in " + FACTORIES_FILE_NAME);
				if (!isFactoryFor(os, attributes.getValue(OS_TAG)))
					return;
				if (!isFactoryFor(ws, attributes.getValue(WS_TAG)))
					return;
				if (!isFactoryFor(arch, attributes.getValue(ARCH_TAG)))
					return;
				result.add(new WTRuntimeFactoryReferenceJava(className));
			}
		}

		/**
		 * Translate the OS name and version into something that is Eclipse-like
		 * 
		 * @param osName the OS name (e.g. "Windows Vista")
		 * @param osVersion the OS version
		 * @return the Eclipse friendly OS (e.g. "win32", "linux") 
		 */
		private String getOS(String osName, String osVersion) {
			try {
				String ws = SWT.getPlatform();
				if (ws.equals("win32"))
					return "win32";
				if (ws.equals("cocoa") || ws.equals("carbon"))
					return "macosx";
				if (ws.equals("gtk") || ws.equals("motif"))
					return "linux";
				// fall through to determine the WS another way
			}
			catch (NoClassDefFoundError e) {
				// SWT must not be on the classpath, 
				// so fall through to determine the WS another way
			}
			if (osName.startsWith("Windows"))
				return "win32";

			// TODO Is this correct for Mac ?
			if (osName.startsWith("Mac OS X"))
				return "macosx";
			if (osName.startsWith("Mac"))
				return "mac";
			
			return "linux";
		}

		/**
		 * Translate the OS name and version into something that is Eclipse-like
		 * 
		 * @param osName the OS name (e.g. "Windows Vista")
		 * @param osVersion the OS version
		 * @return the Eclipse friendly windowing system name (e.g. "win32", "gtk") 
		 */
		private String getWS(String osName, String osVersion) {
			try {
				return SWT.getPlatform();
			}
			catch (NoClassDefFoundError e) {
				// SWT must not be on the classpath, 
				// so fall through to determine the WS another way
			}
			if (osName.startsWith("Windows"))
				return "win32";

			// TODO Is this correct for Mac ?
			if (osName.startsWith("Mac OS X"))
				return "cocoa";
			if (osName.startsWith("Mac"))
				return "carbon";
			
			// Otherwise assume GTK
			return "gtk";
		}
	}

	/**
	 * Instantiate a reference to a factory with the specified class
	 * 
	 * @param className the fully qualified name of the factory class
	 */
	public WTRuntimeFactoryReferenceJava(String className) {
		this.className = className;
	}

	/**
	 * Instantiate the factory
	 * 
	 * @return the factory (not <code>null</code>)
	 * @throws Exception if the factory could not be instantiated
	 */
	WTRuntimeFactory createFactory() throws Exception {
		return (WTRuntimeFactory) getClass().getClassLoader().loadClass(className).newInstance();
	}
}
