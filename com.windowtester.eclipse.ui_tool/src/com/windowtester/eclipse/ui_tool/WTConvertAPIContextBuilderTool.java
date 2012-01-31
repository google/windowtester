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
package com.windowtester.eclipse.ui_tool;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

/**
 * Traverse the known world, building a list of WindowTester types and members for
 * inclusion in WTConvertAPIContextBuilder. This information is written to the files
 * "wt-types-yyyymmddhhmmss.txt" and "wt-static-members-yyyymmddhhmmss.txt" in the
 * com.windowtester.eclipse.ui/src/com/windowtester/eclipse/ui/convert folder. This file
 * should be merged into the wt-types.txt and wt-static-members.txt files respectively
 * residing in the same directory.
 */
public class WTConvertAPIContextBuilderTool extends AbstractHandler
{
	private Shell wbShell;
	private Set<String> wtTypes;
	private Set<String> wtStaticMembers;

	/**
	 * Scan the workspace and create the files.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		wbShell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

		// Instantiate the files to be written

		String ymdhms = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		IFile wtTypesFile = getFile("wt-types", ymdhms);
		if (wtTypesFile == null)
			return null;
		IFile wtStaticMembersFile = getFile("wt-static-members", ymdhms);
		if (wtStaticMembersFile == null)
			return null;

		// Scan the workspace for WindowTester types and members

		try {
			new ProgressMonitorDialog(wbShell).run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						scanWorkspace(monitor);
					}
					catch (Exception e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		}
		catch (Exception e) {
			if (e instanceof InvocationTargetException)
				e = (InvocationTargetException) e;
			new ExceptionDetailsDialog(wbShell, "Exception",
				"Failed to generate content for " + wtTypesFile.getFullPath(), e).open();
			return null;
		}

		// Write the results to the files

		writeFile(wtTypesFile, wtTypes);
		writeFile(wtStaticMembersFile, wtStaticMembers);

		// Open the files in an editor

		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(activePage, wtTypesFile);
			IDE.openEditor(activePage, wtStaticMembersFile);
		}
		catch (PartInitException e) {
			new ExceptionDetailsDialog(wbShell, "Exception",
				"Failed to open editors for generated files.", e);
		}

		return null;
	}

	/**
	 * Answer the file in the
	 * com.windowtester.eclipse.ui/src/com/windowtester/eclipse/ui/convert folder
	 * 
	 * @param filePrefix the file prefix (e.g. "wt-types")
	 * @param ymdhms the file suffix (e.g. the year, month, day, ...)
	 * @return the file or <code>null</code> if there was a problem getting the file not
	 *         exist)
	 */
	private IFile getFile(String filePrefix, String ymdhms) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("com.windowtester.eclipse.ui");
		if (!project.exists()) {
			MessageDialog.openError(wbShell, "Missing Project", "Failed to find project " + project.getName());
			return null;
		}
		IFolder folder = project.getFolder(new Path("src/com/windowtester/eclipse/ui/convert"));
		if (!folder.exists()) {
			MessageDialog.openError(wbShell, "Missing Folder", "Failed to folder " + folder.getFullPath());
			return null;
		}
		return folder.getFile(filePrefix + "-" + ymdhms + ".txt");
	}

	/**
	 * Write the element to the specified file
	 * 
	 * @param file the file (not <code>null</code>)
	 * @param elements the elements (not <code>null</code> and contains no
	 *            <code>null</code> s)
	 */
	private void writeFile(IFile file, Set<String> elements) {
		StringWriter stringWriter = new StringWriter(1000);
		PrintWriter writer = new PrintWriter(stringWriter);
		for (Iterator<String> iter = new TreeSet<String>(elements).iterator(); iter.hasNext();)
			writer.println(iter.next());
		writer.flush();
		try {
			file.create(new ByteArrayInputStream(stringWriter.toString().getBytes()), false, null);
		}
		catch (CoreException e) {
			new ExceptionDetailsDialog(wbShell, "Exception", "Failed to create file "
				+ file.getFullPath(), e);
		}
	}

	/**
	 * Recursively iterate over all projects in the workspace looking for WindowTester
	 * classes and members.
	 * 
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void scanWorkspace(IProgressMonitor monitor) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		wtTypes = new HashSet<String>();
		wtStaticMembers = new HashSet<String>();
		IProject[] allProjects = root.getProjects();
		monitor.beginTask("Scanning Projects", allProjects.length + 1);
		for (int i = 0; i < allProjects.length; i++) {
			monitor.worked(1);
			IProject project = allProjects[i];
			scanProject(project);
		}
		monitor.done();
	}


	/**
	 * Recursively iterate over all elements in the project looking for WindowTester
	 * classes and members.
	 * 
	 * @param proj the java project (not <code>null</code>)
	 */
	private void scanProject(IProject proj) throws JavaModelException {
		if (!proj.exists())
			return;
		String projName = proj.getName();
		if (!projName.startsWith("com.windowtester.") || projName.indexOf('_') != -1)
			return;
		if (projName.endsWith(".recorder") || projName.endsWith(".help") || projName.endsWith(".codegen"))
			return;
		if (projName.equals("com.windowtester.eclipse.ui"))
			return;
		IJavaProject javaProject = JavaCore.create(proj);
		if (!javaProject.exists())
			return;
		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		for (int i = 0; i < roots.length; i++)
			scanPackageRoot(roots[i]);
	}

	/**
	 * Recursively iterate over the elements in the specified java element looking for
	 * WindowTester classes and members.
	 * 
	 * @param root the package fragment root (not <code>null</code>)
	 */
	private void scanPackageRoot(IPackageFragmentRoot root) throws JavaModelException {
		if (root.getKind() != IPackageFragmentRoot.K_SOURCE)
			return;
		IJavaElement[] children = root.getChildren();
		for (int i = 0; i < children.length; i++)
			scanElement(children[i]);
	}

	/**
	 * Recursively iterate over the specified java element and their children looking for
	 * WindowTester classes and members.
	 * 
	 * @param elem the java element (not <code>null</code>)
	 */
	private void scanElement(IJavaElement elem) throws JavaModelException {
		switch (elem.getElementType()) {
			case IJavaElement.PACKAGE_FRAGMENT :
				scanPackage((IPackageFragment) elem);
				break;
			case IJavaElement.COMPILATION_UNIT :
				scanCompilationUnit((ICompilationUnit) elem);
				break;
			default :
				break;
		}
	}

	/**
	 * Recursively iterate over the elements in the specified java element looking for
	 * WindowTester classes and members.
	 * 
	 * @param pkg the package fragment (not <code>null</code>)
	 */
	private void scanPackage(IPackageFragment pkg) throws JavaModelException {
		System.out.println(pkg.getElementName());
		IJavaElement[] children = pkg.getChildren();
		for (int i = 0; i < children.length; i++)
			scanElement(children[i]);
	}

	/**
	 * Scan the specified compilation unit looking for WindowTester classes and members.
	 * 
	 * @param compUnit the compilation unit (not <code>null</code>)
	 */
	private void scanCompilationUnit(ICompilationUnit compUnit) throws JavaModelException {

		IPackageDeclaration[] allPackageDeclarations = compUnit.getPackageDeclarations();
		if (allPackageDeclarations == null || allPackageDeclarations.length == 0)
			return;
		String packageName = allPackageDeclarations[0].getElementName();

		IType[] allTypes = compUnit.getTypes();
		for (int i = 0; i < allTypes.length; i++)
			scanType(packageName, allTypes[i]);
	}

	/**
	 * Scan the specified top level type looking for WindowTester classes and members.
	 * 
	 * @param packageName the name of the package containing the type (not
	 *            <code>null</code>, not empty)
	 * @param type the type (not <code>null</code>)
	 */
	private void scanType(String packageName, IType type) throws JavaModelException {
		if (!Flags.isPublic(type.getFlags()))
			return;
		boolean isInterface = type.isInterface();
		String fullyQualifiedClassName = packageName + "." + type.getElementName();
		wtTypes.add(fullyQualifiedClassName);

		IField[] allFields = type.getFields();
		for (int i = 0; i < allFields.length; i++) {
			IField field = allFields[i];
			if (isInterface || (Flags.isPublic(field.getFlags()) && Flags.isStatic(field.getFlags())))
				wtStaticMembers.add(fullyQualifiedClassName + "." + field.getElementName());
		}

		IMethod[] allMethods = type.getMethods();
		for (int i = 0; i < allMethods.length; i++) {
			IMethod method = allMethods[i];
			if (isInterface || (Flags.isPublic(method.getFlags()) && Flags.isStatic(method.getFlags())))
				wtStaticMembers.add(fullyQualifiedClassName + "." + method.getElementName() + "()");
		}

		IType[] allTypes = type.getTypes();
		for (int i = 0; i < allTypes.length; i++) {
			IType memberType = allTypes[i];
			if (isInterface || (Flags.isPublic(memberType.getFlags()) && Flags.isStatic(memberType.getFlags())))
				scanType(fullyQualifiedClassName, memberType);
		}
	}
}
