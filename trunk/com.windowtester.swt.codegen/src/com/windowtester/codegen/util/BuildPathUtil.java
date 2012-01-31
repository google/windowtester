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
package com.windowtester.codegen.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.windowtester.codegen.CodeGenPlugin;
import com.windowtester.internal.debug.Logger;


/**
 * Build path utilities
 */
@SuppressWarnings("restriction")
public class BuildPathUtil {

	/** The name of the test case superclass, needed to discover commonRuntime.jar */
	private static final String COMMON_UI_TEST_CASE_NAME = "com.windowtester.runtime.common.UITestCaseCommon"; //$NON-NLS-1$
	/** The name of the test case superclass, needed to discover swingRuntime.jar */
	private static final String SWING_UI_TEST_CASE_NAME = "com.windowtester.runtime.swing.UITestCaseSwing"; //$NON-NLS-1$
	/** The name of the test case superclass, needed to discover wt-runtime.jar */
	private static final String SWT_UI_TEST_CASE_NAME = "com.windowtester.runtime.sw.UITestCaseSWT"; //$NON-NLS-1$
	/** The name of the class to test JFace dependency */
	private static final String JFACE_CLASS = "org.eclipse.jface.util.IPropertyChangeListener"; //$NON-NLS-1$
	/** The name of the class to discover debug plugin */
	private static final String LOGGER_CLASS = "com.windowtester.internal.debug.Logger"; //$NON-NLS-1$
	/** The name of the class to discover junit entry in classpath */
	private static final String JUNIT_CLASS = "junit.framework.TestCase";
	/** The name of the class from common core */
	private static final String ECLIPSE_PLUGIN_UTILS_CLASS = "com.windowtester.runtime.util.PluginUtilities";
	/** The name of the class from common util package */
	private static final String COMMON_CORE_PRODUCTS_CLASS = "com.windowtester.internal.product.Products";
	/** The name of the class from core runtime */
	private static final String OSGI_RUNTIME_PATH_CLASS = "org.eclipse.core.runtime.Path";
	/** The name of the class from core runtime */
	private static final String CORE_RUNTIME_PLATFORM_CLASS = "org.eclipse.core.runtime.Platform";
	/** The name of the class from osgi */
	private static final String OSGI_NLS_CLASS = "org.eclipse.osgi.util.NLS";
	
	private static int MAXLINE = 511;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	private static final String LIST_SEPARATOR = "," + LINE_SEPARATOR+" "; //$NON-NLS-1$
	
	private static final String RUNTIME_DEP_RUNTIME_COMMON = "com.windowtester.runtime";
	private static final String RUNTIME_DEP_RUNTIME_SWT = "com.windowtester.swt.runtime";
	private static final String RUNTIME_DEP_RUNTIME_SWING = "com.windowtester.swing.runtime";
	private static final String JFACE_BUNDLE = "org.eclipse.jface";
	private static final String OSGI_RUNTIME_BUNDLE = "org.eclipse.equinox.common";
	private static final String CORE_RUNTIME_BUNDLE = "org.eclipse.core.runtime";
	private static final String OSGI_BUNDLE = "org.eclipse.osgi";

	private static final String WINTEST_JAVADOC_PLUGIN_ID = "com.windowtester.eclipse.help";
	private static final String WINTEST_JAVADOC_RELATIVE_PATH = "html/reference/javadoc";
	
	private static final String JUNIT_BUNDLE = "org.junit";
	private static final String JUNIT_VAR_NAME = "JUNIT_HOME";
	
	public static final String CLASSPATH_CONTAINER_ID = "com.windowtester.eclipse.ui.runtimeClasspathContainer";
	public static final String JUNIT_CONTAINER_ID= "org.eclipse.jdt.junit.JUNIT_CONTAINER"; //$NON-NLS-1$
	
	public final static String JUNIT3= "3.8.1"; //$NON-NLS-1$
	public final static String JUNIT4= "4"; //$NON-NLS-1$
	
	public final static IPath JUNIT3_PATH= new Path(JUNIT_CONTAINER_ID).append(JUNIT3);
	
	
	public static IBuildPathUpdater getUpdater(final IJavaProject targetProject) {
		return new IBuildPathUpdater() {
			public void addPluginDependency(String pluginId) throws CoreException {
				BuildPathUtil.addPluginDependency(targetProject, pluginId);
			}
		};
	}
	
	
	/**
	 * Appends target project build path with source project build path.
	 * 
	 * @param targetProject the target project
	 * @param sourceProject the source project
	 * @throws CoreException
	 */
	public static void appendProjectBuildPath(IJavaProject targetProject, IJavaProject sourceProject) throws CoreException {
		try {
			// copy required entries to target
			IClasspathEntry[] srcEntries= sourceProject.getRawClasspath();
			for (int i = 0; i < srcEntries.length; i++) {
				IClasspathEntry entry = srcEntries[i];
				if(entry.isExported() || entry.getEntryKind()==IClasspathEntry.CPE_SOURCE)
					continue;
				addToClasspath(targetProject, entry);
			}
			// add the source project as a project entry
			IClasspathEntry srcPrjEntry = JavaCore.newProjectEntry(sourceProject.getPath());
			addToClasspath(targetProject, srcPrjEntry);
		} catch (JavaModelException e) {
			// we interested only in core exceptions
			if (e.getCause() instanceof CoreException) {	
				throw (CoreException)e.getCause();  
			}
		}
	}

	/**
	 * Adds JAR archive defined in Classpath variable to the buildpath of Java 
	 * project. In the case when the workspace exists with provided name, this 
	 * project will added to the project build path. Othervise, it will use the
	 * variable name to set the concrete specified JAR.
	 * 
	 * @param project the java project that the entry will be append to.
	 * @param workspaceProject name of the project to look in the workspace
	 * @param variable classpath variable to use
	 * @param jarName the name of the archive, if null it wont be used
	 * @throws CoreException
	 */
	public static void addRuntimeJarToBuildPath(IJavaProject project, String workspaceProject, String variable, String jarName) throws CoreException {
		IClasspathEntry entry = getEntry(workspaceProject, variable, jarName);
		addToClasspath(project, entry);
	}
	
	public static IClasspathEntry getEntry(String workspaceProject, String variable, String jarName){
		IProject wsJavaProject= ResourcesPlugin.getWorkspace().getRoot().getProject(workspaceProject);
		IClasspathEntry entry;
		if (wsJavaProject.exists()) {
			entry= JavaCore.newProjectEntry(wsJavaProject.getFullPath());
		} else {
			IPath variablePath= new Path(variable);
			if(jarName!=null)
				variablePath = variablePath.append(jarName);
			entry= JavaCore.newVariableEntry(variablePath,	null, null);
		}
		return entry;
	}
	
	public static IClasspathEntry getEntry(String bundleName, String jarName, boolean hasJavadoc) {
		
		// check if bundle is a workspace project and if it is then use the project entry 
		// from workspace or otherwise resolve path from the bundle location
		IProject wsJavaProject = ResourcesPlugin.getWorkspace().getRoot().getProject(bundleName);
		if (wsJavaProject.exists())
			return JavaCore.newProjectEntry(wsJavaProject.getFullPath());
		
		// get the bundle
		Bundle bundle = Platform.getBundle(bundleName);
		if (bundle == null)
			return null;
		String path;
		try {
			// get the bundle location
			URL location = bundle.getEntry(jarName);
			// there is no jar in bundle location
			URL jarLocation = null;
			if (location == null) {
				// assume bundle location is jared archive
				location = bundle.getEntry("/");
				jarLocation = Platform.resolve(location);
				// test if bundle is jared
				if (!jarLocation.getFile().endsWith(".jar!/")) {
					// return null if it is not jar
					return null;
				}
				else {
					// remove jar: protocol from URL at this step
					jarLocation = new URL(jarLocation.getPath());
				}
			}
			else {
				jarLocation = Platform.resolve(location);
			}
			// normalize path
			path = jarLocation.getFile();
			if (path.endsWith(".jar!/")) {
				path = path.substring(0, path.length() - 2);
			}
		}
		catch (IOException e) {
			Logger.log(e);
			return null;
		}

		/* $codepro.preprocessor.if version < 3.1 $ 
		return JavaCore.newLibraryEntry(new Path(path), null, null);
		
		$codepro.preprocessor.elseif version >= 3.1 $ */
		org.eclipse.jdt.core.IClasspathAttribute[] attributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
		if (hasJavadoc) {
			Bundle docBundle = Platform.getBundle(WINTEST_JAVADOC_PLUGIN_ID);
			if (docBundle != null) {
				URL location = bundle.getEntry(WINTEST_JAVADOC_RELATIVE_PATH);
				if (location != null) {
					attributes = new org.eclipse.jdt.core.IClasspathAttribute[]{
						JavaCore.newClasspathAttribute(
							org.eclipse.jdt.core.IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, location
								.toExternalForm())
					};
				}
			}
		}
		return JavaCore.newLibraryEntry(new Path(path), null, null, ClasspathEntry.NO_ACCESS_RULES, attributes, false);
		
		/* $codepro.preprocessor.endif $ */
	}
	
	public static void addPluginJarToBuildPath(IJavaProject project, String pluginId) throws CoreException{
		Bundle bundle = Platform.getBundle(pluginId);
		if(bundle==null){
			Logger.log("Cannot find Bundle "+pluginId);
			return;
		}
		IPath path = new Path(getFullPath(bundle.getEntry("/")));		
		IClasspathEntry entry = JavaCore.newLibraryEntry(path, null, null);
		addToClasspath(project, entry);	
	}
	
	public static List getClasspathPaths(String pluginID) throws CoreException {
		List result = new ArrayList();
		try {
			Bundle bundle = Platform.getBundle(pluginID);
			String requires = (String) bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
			if (requires == null) {
				requires = ".";
			}
			ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, requires);
			if (elements != null) {
				for (int i = 0; i < elements.length; ++i) {
					ManifestElement element = elements[i];
					String value = element.getValue();
					if (".".equals(value)) {
						value = "/";
					}
					URL url = bundle.getEntry(value);
					if (url != null) {
						result.add(getFullPath(url));
					}
				}
			}
		} catch (BundleException e) {
			throw new CoreException(Logger.createLogStatus(e.getMessage(), e, null));
		}
		return result;
	}
	
	public static String getFullPath(URL url) throws CoreException{
		try {
			URL resolvedURL = Platform.resolve(url);
			String resolvedURLString = resolvedURL.toString();
			if (resolvedURLString.endsWith("!/")) {
				resolvedURLString = resolvedURL.getFile();
				resolvedURLString = resolvedURLString.substring(0, resolvedURLString.length()- "!/".length());
			}
			if (resolvedURLString.startsWith("file:")) {
				return resolvedURLString.substring("file:".length());
			} else {
				return Platform.asLocalURL(url).getFile();
			}
		} catch (IOException e) {
			throw new CoreException(Logger.createLogStatus(e.getMessage(), e, null));
		}
	}
	
	public static void addToClasspath(final IJavaProject project, IClasspathEntry entry) throws CoreException {
		IClasspathEntry[] oldEntries= project.getRawClasspath();
		for (int i= 0; i < oldEntries.length; i++) {
			if (oldEntries[i].equals(entry)) {
				return;
			}
		}
		int nEntries= oldEntries.length;
		final IClasspathEntry[] newEntries= new IClasspathEntry[nEntries + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, nEntries);
		newEntries[nEntries]= entry;
		try {
			try {
				project.setRawClasspath(newEntries, null);
			} catch (JavaModelException e) {
				throw new InvocationTargetException(e);
			}
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {	
				throw (CoreException)t;  
			}
		} 
	}
	
	public static void addPluginDependency(IJavaProject targetProject, String pluginId) throws CoreException{
		IFile manifest = targetProject.getProject().getFile("META-INF/MANIFEST.MF");
		if(manifest.exists()){
			updateManifestDependency(manifest.getLocation().toFile(), pluginId);
			manifest.refreshLocal(1, null);
		}else{
			IFile plugin = targetProject.getProject().getFile("plugin.xml");
			if(!plugin.exists())
				throw new CoreException(Logger.createLogStatus("No plugin or manifest file found in target project", null, null));
			updatePluginDependnecy(plugin.getLocation().toFile(), pluginId);
			plugin.refreshLocal(1, null);
		}
	}

	public static void updatePluginDependnecy(File plugin, String pluginId) throws CoreException {
		try {
			Document doc = parseXmlFile(plugin, false);
			NodeList list = doc.getElementsByTagName("requires");
			for (int i=0; i<list.getLength(); i++) {
		        Element element = (Element)list.item(i);
		        Element importElement = doc.createElement("import");
		        importElement.setAttribute("plugin", pluginId);
		        element.appendChild(importElement);
		    }
			writeXmlFile(doc, plugin);
		} catch (Exception e) {
			throw new CoreException(Logger.createLogStatus(e.getMessage(), e, null));
		}
	}
	
	public static Document parseXmlFile(File file, boolean validating) throws Exception {
        // Create a builder factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);

        // Create the builder and parse the file
        Document doc = factory.newDocumentBuilder().parse(file);
        return doc;
    }
	
	public static void writeXmlFile(Document doc, File file) throws Exception{
        // Prepare the DOM document for writing
        Source source = new DOMSource(doc);
        // Prepare the output file
        Result result = new StreamResult(file);
        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, result);
    }
	
	public static void updateManifestDependency(File file, String pluginId) throws CoreException {
		InputStream manifestStream = null;
		try {
			manifestStream = new FileInputStream(file);
			Manifest manifest = new Manifest(manifestStream);
			Properties prop = manifestToProperties(manifest.getMainAttributes());
			String require = prop.getProperty(Constants.REQUIRE_BUNDLE);
			if(require == null || require.trim().equals("")){
				prop.put(Constants.REQUIRE_BUNDLE, pluginId);
			}else{
				boolean exists = false;
				ManifestElement[] elements = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, require);
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < elements.length; i++) {
					if(elements[i].getValue().equals(pluginId))
						exists = true;
					appendValue(buffer, elements[i].getValue());
				}
				// add required bundle at the end
				if(!exists)
					appendValue(buffer, pluginId);
				prop.put(Constants.REQUIRE_BUNDLE, buffer.toString());
			}
			writeManifest(file, prop);
		} catch (FileNotFoundException e) {
			throw new CoreException(Logger.createLogStatus(e.getMessage(), e, null));
		} catch (IOException e) {
			throw new CoreException(Logger.createLogStatus(e.getMessage(), e, null));
		} catch (BundleException e) {
			throw new CoreException(Logger.createLogStatus(e.getMessage(), e, null));
		} finally {
			try {
				if (manifestStream != null)
					manifestStream.close();
			} catch (IOException e) {
			}
		}
	}
	public static void writeManifest(File generationLocation, Dictionary manifestToWrite) throws CoreException {
		Writer out = null;
		try {
			File parentFile = new File(generationLocation.getParent());
			parentFile.mkdirs();
			generationLocation.createNewFile();
			if (!generationLocation.isFile()) {
				throw new CoreException(Logger.createLogStatus("Location must be a file.", null, null));
			}
			// replaces any eventual existing file
			manifestToWrite = new Hashtable((Map) manifestToWrite);
			// MANIFEST.MF files must be written using UTF-8
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(generationLocation), "UTF-8"));
			Enumeration keys = manifestToWrite.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				writeEntry(out, key, (String) manifestToWrite.get(key));
			}
			out.flush();
		} catch (IOException e) {
			throw new CoreException(Logger.createLogStatus("Error when building bundle manifets", null, null));
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// only report problems writing to/flushing the file
				}
		}
	}
	
	public static void writeBuildProperties(File generationLocation, Dictionary propertiesToWrite) throws CoreException {
		Writer out = null;
		try {
			File parentFile = new File(generationLocation.getParent());
			parentFile.mkdirs();
			generationLocation.createNewFile();
			if (!generationLocation.isFile()) {
				throw new CoreException(Logger.createLogStatus("Location must be a file.", null, null));
			}
			// replaces any eventual existing file
			propertiesToWrite = new Hashtable((Map) propertiesToWrite);
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(generationLocation), "UTF-8"));
			Enumeration keys = propertiesToWrite.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				writePropertyEntry(out, key, (String) propertiesToWrite.get(key));
			}
			out.flush();
		} catch (IOException e) {
			throw new CoreException(Logger.createLogStatus("Error when writing properties file", null, null));
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// only report problems writing to/flushing the file
				}
		}
	}
	
	private static void writePropertyEntry(Writer out, String key, String value) throws IOException {
		if (value != null && value.length() > 0) {
			out.write(splitOnComma(key + "= " + value)); //$NON-NLS-1$
			out.write(LINE_SEPARATOR);
		}
	}

	
	private static void writeEntry(Writer out, String key, String value) throws IOException {
		if (value != null && value.length() > 0) {
			out.write(splitOnComma(key + ": " + value)); //$NON-NLS-1$
			out.write(LINE_SEPARATOR);
		}
	}

	private static String splitOnComma(String value) {
		if (value.length() < MAXLINE || value.indexOf(LINE_SEPARATOR) >= 0)
			return value; // assume the line is already split
		String[] values = getArrayFromList(value);
		if (values == null || values.length == 0)
			return value;
		StringBuffer sb = new StringBuffer(value.length() + ((values.length - 1) * LIST_SEPARATOR.length()));
		for (int i = 0; i < values.length - 1; i++)
			sb.append(values[i]).append(LIST_SEPARATOR);
		sb.append(values[values.length -1]);
		return sb.toString();
	}
	
	public static String[] getArrayFromList(String stringList) {
		if (stringList == null || stringList.trim().equals("")) //$NON-NLS-1$
			return null;
		Vector list = new Vector();
		StringTokenizer tokens = new StringTokenizer(stringList, ","); //$NON-NLS-1$
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken().trim();
			if (!token.equals("")) //$NON-NLS-1$
				list.addElement(token);
		}
		return list.isEmpty() ? new String[0] : (String[]) list.toArray(new String[list.size()]);
	}
	
	private static void appendValue(StringBuffer buffer, String value){
		if (buffer.length() > 0) {
			buffer.append(LIST_SEPARATOR);
		}
		buffer.append(value);
	}
	
	private static Properties manifestToProperties(Attributes d) {
		Iterator iter = d.keySet().iterator();
		Properties result = new Properties();
		while (iter.hasNext()) {
			Attributes.Name key = (Attributes.Name) iter.next();
			result.put(key.toString(), d.get(key));
		}
		return result;
	}
	
	/**
	 * Validate if the project has required libraries in build path. I the case if not it will add them to it.
	 * 
	 * @return the status of the validation
	 */
	public static IStatus validateDependencies(IJavaProject targetProject, boolean addDefault, boolean isRcpApplication) {
		
		Status status= new Status(IStatus.OK, CodeGenPlugin.getPluginId(), IStatus.OK, "Project has all libraires", null);
		
		boolean addJUnitJar = checkToAddEntry(targetProject, JUNIT_CLASS);
		boolean addCommonRuntimeJar = checkToAddEntry(targetProject, COMMON_UI_TEST_CASE_NAME);
		boolean addSwtRuntimeJar = checkToAddEntry(targetProject, SWT_UI_TEST_CASE_NAME);
		boolean addSwingRuntimeJar = checkToAddEntry(targetProject, SWING_UI_TEST_CASE_NAME);
		boolean addJFaceJar = checkToAddEntry(targetProject, JFACE_CLASS);
//		boolean addCommonDebug = checkToAddEntry(targetProject, LOGGER_CLASS);
//		boolean addCommonCore = checkToAddEntry(targetProject, COMMON_CORE_PRODUCTS_CLASS);
		boolean addCoreRuntime = checkToAddEntry(targetProject, CORE_RUNTIME_PLATFORM_CLASS);
//		boolean addCommonUtil = checkToAddEntry(targetProject, ECLIPSE_PLUGIN_UTILS_CLASS);
		boolean addOsgiRuntime = checkToAddEntry(targetProject, OSGI_RUNTIME_PATH_CLASS);
		boolean addOsgi = checkToAddEntry(targetProject, OSGI_NLS_CLASS);

		// test if there is a need to move forward
		if(!addJUnitJar && 
		   !addCommonRuntimeJar && 
		   !addSwtRuntimeJar &&
		   !addSwingRuntimeJar &&
		   !addJFaceJar &&
		   !addCoreRuntime &&
		   !addOsgiRuntime &&
		   !addOsgi)
		{
			return status;
		}
		
		if (addDefault) { 
			try {
				if(isRcpApplication){
					if(addJUnitJar)
						BuildPathUtil.addPluginDependency(targetProject, JUNIT_BUNDLE);
					if(addCommonRuntimeJar)
						BuildPathUtil.addPluginDependency(targetProject, RUNTIME_DEP_RUNTIME_COMMON);
					if(addSwtRuntimeJar)
						BuildPathUtil.addPluginDependency(targetProject, RUNTIME_DEP_RUNTIME_SWT);
					if(addSwingRuntimeJar)
						BuildPathUtil.addPluginDependency(targetProject, RUNTIME_DEP_RUNTIME_SWING);
					if(addJFaceJar)
						BuildPathUtil.addPluginDependency(targetProject, JFACE_BUNDLE);
					if(addCoreRuntime)
						BuildPathUtil.addPluginDependency(targetProject, CORE_RUNTIME_BUNDLE);
				} else{
					
					// add the WindowTester classpath container
					addToClasspath(targetProject, JavaCore.newContainerEntry(new Path(CLASSPATH_CONTAINER_ID), false));

					// add the appropriate JUnit container/library depending upon platform
					
					/* $codepro.preprocessor.if version >= 3.2 $ */
					addToClasspath(targetProject, JavaCore.newContainerEntry(JUNIT3_PATH, false));
					
					/* $codepro.preprocessor.elseif version < 3.2 $
					addRuntimeJarToBuildPath(targetProject, JUNIT_BUNDLE, JUNIT_VAR_NAME, "junit.jar");
					
					$codepro.preprocessor.endif $ */
				}
				return status;
			} catch(CoreException e) {
				return e.getStatus(); 
			}	
		}
		return new Status(IStatus.WARNING, CodeGenPlugin.getPluginId(), IStatus.WARNING, "Some required libraries are missing.", null);
	}
	
	public static IClasspathEntry getRuntimeContainerEntry(IJavaProject targetProject) throws JavaModelException{
		// add WindowTester Runtime Classpath container and give it to handle all dependencies 
		IClasspathEntry entry = JavaCore.newContainerEntry(
			    new Path(CLASSPATH_CONTAINER_ID),
			    false);
		IClasspathEntry[] entries = BuildPathUtil.getRuntimeClasspathEntries(targetProject);
		JavaCore.setClasspathContainer(
				entry.getPath(), 
				new IJavaProject[]{ targetProject }, 
				new IClasspathContainer[] {new RuntimeClasspathContainer(entries, entry.getPath())}, 
				null);
		return entry;
	}
	
	public static IStatus validateDependencies(IJavaProject targetProject, String mainAppProjectName, boolean addDefault, boolean isRcpApplication) throws CoreException {
		IProject mainAppProject = ResourcesPlugin.getWorkspace().getRoot().getProject(mainAppProjectName);
		if(!targetProject.getProject().getName().equals(mainAppProjectName) && addDefault){
			BuildPathUtil.appendProjectBuildPath(targetProject, JavaCore.create(mainAppProject));
		}
		if(!targetProject.getProject().getName().equals(mainAppProjectName) && !addDefault)
			return new Status(IStatus.WARNING, CodeGenPlugin.getPluginId(), IStatus.WARNING, "Some required libraries are missing.", null);
		return validateDependencies(targetProject, addDefault, isRcpApplication);
	}

	/**
	 * Get required runtime Classpath entries for standalone SWT application. If the 
	 * provided project already have some dependency in the classpath, it will
	 * not appear in the output.  
	 * @param project the project to which the entries has to be computed
	 * @return array of required dependencies
	 */
	public static IClasspathEntry[] getRuntimeClasspathEntries(IJavaProject targetProject) {
		// output array of entries
		ArrayList entries = new ArrayList();
		
		checkAndAddEntry(targetProject, entries, COMMON_UI_TEST_CASE_NAME, RUNTIME_DEP_RUNTIME_COMMON, "commonRuntime.jar", true);
		checkAndAddEntry(targetProject, entries, SWING_UI_TEST_CASE_NAME, RUNTIME_DEP_RUNTIME_SWING, "swingRuntime.jar", true);
		checkAndAddEntry(targetProject, entries, SWT_UI_TEST_CASE_NAME, RUNTIME_DEP_RUNTIME_SWT, "wt-runtime.jar", true);
		checkAndAddEntry(targetProject, entries, JFACE_CLASS, JFACE_BUNDLE, "jface.jar", false);
		checkAndAddEntry(targetProject, entries, CORE_RUNTIME_PLATFORM_CLASS, CORE_RUNTIME_BUNDLE, "runtime.jar", false);
		checkAndAddEntry(targetProject, entries, OSGI_RUNTIME_PATH_CLASS, OSGI_RUNTIME_BUNDLE, "runtime.jar", false);
		checkAndAddEntry(targetProject, entries, OSGI_NLS_CLASS, OSGI_BUNDLE, "osgi.jar", false);
				
		return (IClasspathEntry[])entries.toArray(new IClasspathEntry[] {});
	}

	private static void checkAndAddEntry(IJavaProject targetProject, ArrayList entries, String classToCheck, String bundle, String jarName, boolean hasJavadoc){
		if(checkToAddEntry(targetProject, classToCheck)){
			IClasspathEntry entry = getEntry(bundle, jarName, hasJavadoc);
			if(entry!=null)
				entries.add(entry);
		}
	}
	
	private static boolean checkToAddEntry(IJavaProject targetProject, String classToCheck){
		
		/* 
		 * 
		 * Classpath Container in periodically coming up empty. I suspect that the java project is not initialized
		 * when this method called resulting in invalid response from or exception when calling findType(...).
		 * Added tracing and logging rather than throwing away the exception.
		 * 
		 * I commented out the entire findType(...) functionality because I suspect that Eclipse
		 * cannot handle this type of call from here under certain circumstances causing Eclipse
		 * to lock up without a graceful exit.
		 */

		boolean required = true;
//		try {
//			if (targetProject.findType(classToCheck) != null) {
//				Logger.log("Failed to find " + classToCheck + " in " + targetProject.getProject().getName());
//				required = false;
//			}
//		}
//		catch (JavaModelException e) {
//			Logger.log("Exception when finding " + classToCheck + " in " + targetProject.getProject().getName(), e);
//		}
		return required;
	}
}
