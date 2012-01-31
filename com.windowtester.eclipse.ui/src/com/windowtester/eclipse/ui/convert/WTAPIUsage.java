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
package com.windowtester.eclipse.ui.convert;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.windowtester.eclipse.ui.convert.preprocessor.BundleManifestReader;

/**
 * Build a collection of all WindowTester API used by the selected project/package/class.
 * First {@link #scan(List, IProgressMonitor)} then {@link #getAPIUsageText()}.
 */
public class WTAPIUsage
{
	private Collection<IJavaElement> visited = new HashSet<IJavaElement>(1000);
	private Map<String, Integer> apiUsed = new HashMap<String, Integer>(1000);
	private Collection<IJavaProject> projects = new HashSet<IJavaProject>(10);
	private int compUnitCount;
	private int wtCompUnitCount;
	private int exceptionCount;
	private Exception firstException;
	private String sourceWithException;

	//==============================================================================
	// Scanning

	/**
	 * Recursively iterate over the specified java elements and their children to convert
	 * each compilation to use the new WindowTester API.
	 * 
	 * @param elements a collection of {@link IJavaElement}s to be converted (not
	 *            <code>null</code> and contains no <code>null</code>s)
	 * @param monitor the progress monitor (not <code>null</code>)
	 * @return a collection of API signatures
	 */
	public void scan(List<IJavaElement> elements, IProgressMonitor monitor) throws JavaModelException {
		compUnitCount = 0;
		wtCompUnitCount = 0;
		exceptionCount = 0;
		firstException = null;
		sourceWithException = null;
		monitor.beginTask("Scanning WindowTester tests", elements.size());
		for (Iterator<IJavaElement> iter = elements.iterator(); iter.hasNext();) {
			scanElement(iter.next(), new SubProgressMonitor(monitor, 1));
			monitor.worked(1);
		}
		monitor.done();
	}

	/**
	 * Recursively iterate over the specified java element and their children to convert
	 * each compilation to use the new WindowTester API.
	 * 
	 * @param elem the java element (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void scanElement(IJavaElement elem, IProgressMonitor monitor) throws JavaModelException {
		projects.add(elem.getJavaProject());
		switch (elem.getElementType()) {
			case IJavaElement.JAVA_PROJECT :
				scanProject((IJavaProject) elem, monitor);
				break;
			case IJavaElement.PACKAGE_FRAGMENT_ROOT :
				scanPackageRoot((IPackageFragmentRoot) elem, monitor);
				break;
			case IJavaElement.PACKAGE_FRAGMENT :
				scanPackage((IPackageFragment) elem, monitor);
				break;
			case IJavaElement.COMPILATION_UNIT :
				scanCompilationUnit((ICompilationUnit) elem);
				break;
			default :
				break;
		}
	}

	/**
	 * Recursively iterate over the elements in the specified java element to convert each
	 * compilation to use the new WindowTester API.
	 * 
	 * @param proj the java project (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void scanProject(IJavaProject proj, IProgressMonitor monitor) throws JavaModelException {
		if (visited.contains(proj))
			return;
		visited.add(proj);
		IPackageFragmentRoot[] roots = proj.getPackageFragmentRoots();
		monitor.beginTask("Scanning " + proj.getPath(), roots.length);
		for (int i = 0; i < roots.length; i++) {
			scanPackageRoot(roots[i], new SubProgressMonitor(monitor, 1));
			monitor.worked(1);
		}
		monitor.done();
	}

	/**
	 * Recursively iterate over the elements in the specified java element to convert each
	 * compilation to use the new WindowTester API.
	 * 
	 * @param root the package fragment root (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void scanPackageRoot(IPackageFragmentRoot root, IProgressMonitor monitor) throws JavaModelException {
		if (root.getKind() != IPackageFragmentRoot.K_SOURCE || visited.contains(root))
			return;
		visited.add(root);
		IJavaElement[] children = root.getChildren();
		monitor.beginTask("Scanning " + root.getPath(), children.length);
		for (int i = 0; i < children.length; i++) {
			scanElement(children[i], new SubProgressMonitor(monitor, 1));
			monitor.worked(1);
		}
		monitor.done();
	}

	/**
	 * Recursively iterate over the elements in the specified java element to convert each
	 * compilation to use the new WindowTester API.
	 * 
	 * @param pkg the package fragment (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void scanPackage(IPackageFragment pkg, IProgressMonitor monitor) throws JavaModelException {
		if (visited.contains(pkg))
			return;
		visited.add(pkg);
		IJavaElement[] children = pkg.getChildren();
		monitor.beginTask("Scanning " + pkg.getPath(), children.length);
		for (int i = 0; i < children.length; i++) {
			if (monitor.isCanceled())
				throw new OperationCanceledException();
			scanElement(children[i], new SubProgressMonitor(monitor, 1));
			monitor.worked(1);
		}
		monitor.done();
	}

	/**
	 * Convert the specified compilation unit to use the new WindowTester API.
	 * 
	 * @param compUnit the compilation unit (not <code>null</code>)
	 */
	private void scanCompilationUnit(ICompilationUnit compUnit) throws JavaModelException {
		if (visited.contains(compUnit))
			return;
		visited.add(compUnit);
		compUnitCount++;
		try {
			parseScanResult(new WTConvertAPIContextBuilder().buildContext(compUnit));
		}
		catch (Exception e) {
			exceptionCount++;
			if (firstException == null) {
				firstException = e;
				sourceWithException = compUnit.getSource();
			}
		}
	}

	/**
	 * Convert the specified compilation unit's source. This is used for testing purposes
	 * and typically not called outside this class during the normal course of events
	 * 
	 * @param source the compilation unit source
	 * @return the context containing the converted source (not <code>null</code>)
	 */
	public void scanCompilationUnitSource(String source) {
		parseScanResult(new WTConvertAPIContextBuilder().buildContext(source));
	}

	/**
	 * Fold the results of the API scan into the report
	 * 
	 * @param context the scan result (not <code>null</code>)
	 */
	private void parseScanResult(WTConvertAPIContext context) {
		if (context.getWTTypeNames().size() > 0) {
			wtCompUnitCount++;
			context.accept(new WTAPIUsageVisitor(context) {
				public void apiUsed(String signature) {
					Integer count = apiUsed.get(signature);
					apiUsed.put(signature, (count != null ? count : 0) + 1);
				}
			});
		}
	}

	//==============================================================================================
	// Reporting

	/**
	 * Return text describing what WindowTester APIs were detected
	 * 
	 * @return a String (not <code>null</code>)
	 */
	public String getAPIUsageText() {
		StringWriter stringWriter = new StringWriter(2000);
		PrintWriter writer = new PrintWriter(stringWriter);
		printHeader(writer);
		printReferencedAPI(writer);
		printReferencedPlugins(writer);
		printExceptionIfAny(writer);
		return stringWriter.toString();
	}

	private void printHeader(PrintWriter writer) {
		writer.println();
		writer.println("WindowTester API Usage Report");
		writer.println("==========================================================");
		printInt(writer, projects.size());
		writer.println("projects scanned");
		printInt(writer, compUnitCount);
		writer.println("compilation units scanned");
		printInt(writer, wtCompUnitCount);
		writer.println("compilation units scanned that referenced WindowTester types");
		if (exceptionCount > 0) {
			printInt(writer, exceptionCount);
			writer.println("exceptions while scanning source");
		}
	}

	private void printReferencedAPI(PrintWriter writer) {
		TreeSet<Map.Entry<String, Integer>> sorted = new TreeSet<Map.Entry<String, Integer>>(
			new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
					String line1 = entry1.getKey();
					String line2 = entry2.getKey();
					String type1 = line1.substring(0, line1.indexOf('#'));
					String type2 = line2.substring(0, line2.indexOf('#'));
					String package1 = type1.substring(0, type1.lastIndexOf('.'));
					String package2 = type2.substring(0, type2.lastIndexOf('.'));
					int delta = package1.compareToIgnoreCase(package2);
					if (delta == 0) {
						delta = type1.compareToIgnoreCase(type2);
						if (delta == 0)
							delta = line1.compareToIgnoreCase(line2);
					}
					return delta;
				}
			});
		sorted.addAll(apiUsed.entrySet());

		writer.println();
		writer.println("API Usage:");
		for (Map.Entry<String, Integer> entry : sorted) {
			Integer value = entry.getValue();
			printInt(writer, value);
			writer.println(entry.getKey());
		}
	}

	private void printReferencedPlugins(PrintWriter writer) {
		Collection<String> pluginIds = collectReferencedPlugins(writer);
		writer.println();
		writer.println("Plugins:");
		for (String id : new TreeSet<String>(pluginIds)) {
			writer.print("   ");
			writer.println(id);
		}
	}

	private Collection<String> collectReferencedPlugins(PrintWriter writer) {
		Collection<String> pluginIds = new HashSet<String>();
		try {
			for (IJavaProject proj : projects)
				for (IClasspathEntry entry : proj.getRawClasspath())
					collectPluginsReferencedByClasspathEntry(writer, pluginIds, proj, entry);
		}
		catch (JavaModelException e) {
			writer.println();
			e.printStackTrace(writer);
		}
		catch (IOException e) {
			writer.println();
			e.printStackTrace(writer);
		}
		return pluginIds;
	}

	private void collectPluginsReferencedByClasspathEntry(PrintWriter writer, Collection<String> pluginIds, IJavaProject proj, IClasspathEntry entry)
		throws IOException
	{
		IPath path = entry.getPath();
		switch (entry.getEntryKind()) {

			case IClasspathEntry.CPE_LIBRARY :
			case IClasspathEntry.CPE_VARIABLE :
				for (int i = path.segmentCount() - 1; i >= 0; i--) {
					String segment = path.segment(i);
					if (segment.startsWith("com.windowtester.")) {
						String id = segment;
						i++;
						while (i < path.segmentCount())
							id += "/" + path.segment(i++);
						pluginIds.add(id);
						break;
					}
				}
				break;

			case IClasspathEntry.CPE_CONTAINER :
				if (path.segmentCount() >= 1 && path.segment(0).equals("org.eclipse.pde.core.requiredPlugins"))
					collectPluginsReferencedInManifest(pluginIds, proj);
				break;

			case IClasspathEntry.CPE_SOURCE :
			case IClasspathEntry.CPE_PROJECT :
				// ignored
				break;

			default :
				pluginIds.add("unknown " + entry.getEntryKind() + " - " + entry);
				break;
		}
	}

	private void collectPluginsReferencedInManifest(Collection<String> pluginIds, IJavaProject proj) throws IOException
	{
		IPath location = proj.getResource().getLocation();
		if (location != null) {
			File pluginXmlFile = null;
			for (File dir : location.toFile().listFiles()) {
				if (!dir.isDirectory() || !dir.getName().equalsIgnoreCase("META-INF"))
					continue;
				for (File file : dir.listFiles()) {
					String name = file.getName();
					if (!name.equalsIgnoreCase("MANIFEST.MF")) {
						if (name.equalsIgnoreCase("plugin.xml"))
							pluginXmlFile = file;
						continue;
					}
					BundleManifestReader reader = new BundleManifestReader();
					reader.process(file);
					for (String id : reader.getRequiredPlugins()) {
						if ( id.startsWith("com.windowtester."))
							pluginIds.add(id);
					}
					return;
				}
			}
			if (pluginXmlFile != null)
				pluginIds.add("unknown plugins referenced in " + proj.getResource().getName() + "/"
					+ pluginXmlFile.getName());
		}
	}

	private void printExceptionIfAny(PrintWriter writer) {
		if (firstException != null) {
			writer.println();
			writer.println("==========================================================");
			firstException.printStackTrace(writer);
			writer.println("==========================================================");
			writer.println(sourceWithException);
		}
	}

	private void printInt(PrintWriter writer, int value) {
		String count = Integer.toString(value);
		for (int i = 5 - count.length(); i > 0; i--)
			writer.print(" ");
		writer.print(count);
		writer.print(" ");
	}
}
