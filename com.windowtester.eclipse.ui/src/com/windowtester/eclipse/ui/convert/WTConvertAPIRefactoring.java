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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.ReplaceEdit;

import com.windowtester.eclipse.ui.convert.rule.WTReplaceIUIContextMethodCallWithEnsureThatRule;
import com.windowtester.eclipse.ui.convert.rule.WTReplacePauseCallsRule;
import com.windowtester.eclipse.ui.convert.rule.WTReplaceSWTWidgetLocatorRule;
import com.windowtester.eclipse.ui.convert.rule.WTReplaceTypeRule;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.UnableToFindActiveShellException;
import com.windowtester.runtime.swt.locator.ShellLocator;
import com.windowtester.runtime.swt.locator.eclipse.ActiveEditorLocator;
import com.windowtester.runtime.swt.util.DebugHelper;

/**
 * Refactoring for converting/migrating all java code in the selection
 * project/package/class to use the new WindowTester API.
 */
public class WTConvertAPIRefactoring extends Refactoring
{
	private final List<IJavaElement> selection;
	private WTConvertAPIRule[] rules;
	private Collection<IJavaElement> visited = new HashSet<IJavaElement>();
	private CompositeChange compositeChange;
	private RefactoringStatus status;

	public WTConvertAPIRefactoring(List<IJavaElement> selected) {
		this.selection = selected;
	}

	public String getName() {
		return "WindowTester API Conversion";
	}

	/**
	 * Set the rules used to convert WindowTester API. This is used for testing purposes,
	 * and need not be called during normal operation.
	 * 
	 * @param rules an array of conversion rules (not <code>null</code>, contains no
	 *            <code>null</code>s)
	 */
	public void setRules(WTConvertAPIRule[] rules) {
		this.rules = rules;
	}

	/**
	 * Answer the rules used to convert WindowTester API. This is used for testing
	 * purposes, and need not be called during normal operation.
	 * 
	 * @re rules an array of conversion rules (not <code>null</code>, contains no
	 *     <code>null</code>s)
	 */
	public WTConvertAPIRule[] getRules() {
		if (rules == null) {
			rules = new WTConvertAPIRule[]{

				// TODO Include all old API WT classes that need to be replaced one-for-one with new API WT classes
				new WTReplaceTypeRule("com.windowtester.runtime.swt.experimental.locator.ActiveEditorLocator",
					ActiveEditorLocator.class),
				new WTReplaceTypeRule("com.windowtester.swt.util.DebugHelper", DebugHelper.class),
				new WTReplaceTypeRule("com.windowtester.runtime.locator.WidgetReference", IWidgetReference.class),
				new WTReplaceTypeRule("junit.extensions.UITestCase", UITestCaseSWT.class),
				new WTReplaceTypeRule("junit.extensions.UITestCaseSWT", UITestCaseSWT.class),
				new WTReplaceTypeRule("junit.extensions.UITestCaseSwing", UITestCaseSwing.class),
				new WTReplaceTypeRule("com.windowtester.runtime.swt.finder.UnableToFindActiveShellException",
					UnableToFindActiveShellException.class),

				// ??? Should we be converting references to internal classes ??? 
				new WTReplaceTypeRule("com.windowtester.swt.util.ExceptionHandlingHelper",
					"com.windowtester.runtime.swt.internal.ExceptionHandlingHelper"),
				new WTReplaceTypeRule("com.windowtester.event.swt.text.InsertTextEntryStrategy",
					"com.windowtester.runtime.swt.internal.text.InsertTextEntryStrategy"),
				new WTReplaceTypeRule("com.windowtester.event.swt.text.ITextEntryStrategy",
					"com.windowtester.runtime.swt.internal.text.ITextEntryStrategy"),
				new WTReplaceTypeRule("com.windowtester.event.swt.text.TextEntryStrategy",
					"com.windowtester.runtime.swt.internal.text.TextEntryStrategy"),
				new WTReplaceTypeRule("com.windowtester.swt.util.PathStringTokenizerUtil",
					"com.windowtester.runtime.swt.internal.util.PathStringTokenizerUtil"),
				new WTReplaceTypeRule("com.windowtester.swt.util.TextUtils",
					"com.windowtester.runtime.swt.internal.util.TextUtils"),
				new WTReplaceTypeRule("com.windowtester.swt.WidgetLocatorService",
					"com.windowtester.runtime.swt.internal.finder.WidgetLocatorService"),
				new WTReplaceTypeRule("com.windowtester.finder.swt.ShellFinder",
					"com.windowtester.runtime.swt.internal.finder.ShellFinder"),

				// TODO Include all constructor replacement rules
				new WTReplaceSWTWidgetLocatorRule(Shell.class, ShellLocator.class),

				// TODO Include all method call replacement rules
				new WTReplaceIUIContextMethodCallWithEnsureThatRule("setFocus", "hasFocus"),
				new WTReplaceIUIContextMethodCallWithEnsureThatRule("close", "isClosed"), new WTReplacePauseCallsRule()
			};
		}
		return rules;
	}

	/**
	 * Checks some initial conditions based on the element to be refactored. The method is
	 * typically called by the UI to perform an initial checks after an action has been
	 * executed.
	 * <p>
	 * The refactoring has to be considered as not being executable if the returned status
	 * has the severity of <code>RefactoringStatus#FATAL</code>.
	 */
	public RefactoringStatus checkInitialConditions(IProgressMonitor monitor) throws CoreException,
		OperationCanceledException
	{
		if (monitor != null) {
			monitor.beginTask("", 1); //$NON-NLS-1$
			monitor.worked(1);
			monitor.done();
		}
		return new RefactoringStatus();
	}

	/**
	 * After <code>checkInitialConditions</code> has been performed and the user has
	 * provided all input necessary to perform the refactoring this method is called to
	 * check the remaining preconditions.
	 * <p>
	 * The refactoring has to be considered as not being executable if the returned status
	 * has the severity of <code>RefactoringStatus#FATAL</code>.
	 */
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor) throws CoreException,
		OperationCanceledException
	{
		if (monitor == null)
			monitor = new NullProgressMonitor();
		status = new RefactoringStatus();
		compositeChange = new CompositeChange("Convert to new WindowTester API");
		monitor.beginTask("Converting to new WindowTester API", selection.size());
		for (Iterator<IJavaElement> iter = selection.iterator(); iter.hasNext();)
			convertElement(iter.next(), new SubProgressMonitor(monitor, 1));
		monitor.done();

		//		if (monitor != null) {
		//			monitor.beginTask("", 1); //$NON-NLS-1$
		//			monitor.worked(1);
		//			monitor.done();
		//		}
		//
		//
		//		monitor.beginTask("Checking preconditions", selection.size());
		//		for (IJavaElement elem : selection) {
		//			checkForProblems(elem, status, monitor);
		//		}
		//		monitor.done();

		return status;
	}

	//	private void checkForProblems(IJavaElement elem, RefactoringStatus status, IProgressMonitor monitor)
	//		throws JavaModelException
	//	{
	//		switch (elem.getElementType()) {
	//			case IJavaElement.JAVA_PROJECT :
	//				IJavaProject project = (IJavaProject) elem;
	//				IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
	//				monitor.beginTask("Checking " + project.getPath(), roots.length);
	//				for (int i = 0; i < roots.length; i++)
	//					checkForProblems(roots[i], status, new SubProgressMonitor(monitor, 1));
	//				monitor.done();
	//				break;
	//			case IJavaElement.PACKAGE_FRAGMENT_ROOT :
	//				IPackageFragmentRoot root = (IPackageFragmentRoot) elem;
	//				IJavaElement[] children = root.getChildren();
	//				monitor.beginTask("Checking " + root.getPath(), children.length);
	//				for (int i = 0; i < children.length; i++)
	//					checkForProblems(children[i], status, new SubProgressMonitor(monitor, 1));
	//				monitor.done();
	//				break;
	//			case IJavaElement.PACKAGE_FRAGMENT :
	//				IPackageFragment pkg = (IPackageFragment) elem;
	//				monitor.beginTask("Converting " + pkg.getPath(), pkg.getChildren().length);
	//				for (IJavaElement child : pkg.getChildren())
	//					checkForProblems(child, status, new SubProgressMonitor(monitor, 1));
	//				monitor.done();
	//				break;
	//			case IJavaElement.COMPILATION_UNIT :
	//				ICompilationUnit cu = (ICompilationUnit) elem;
	//				if (hasProblem(cu)) {
	//					//JDT status contexts are all internal so this is the best we can do
	//					status.addWarning(cu.getElementName() + " has syntax errors and will be skipped");
	//				}
	//				break;
	//			default :
	//				break;
	//		}
	//	}

	//	private boolean hasProblem(ICompilationUnit cu) throws JavaModelException {
	//		String source = cu.getSource();
	//		ASTParser parser = ASTParser.newParser(AST.JLS3);
	//		parser.setSource(source.toCharArray());
	//		CompilationUnit compUnit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
	//		IProblem[] problems = compUnit.getProblems();
	//		for (IProblem problem : problems) {
	//			if (problem.isError())
	//				return true;
	//		}
	//		return false;
	//	}

	/**
	 * Recursively iterate over the selected java elements and their children to convert
	 * each compilation to use the new WindowTester API. Creates a {@link Change} object
	 * that performs the actual workspace transformation.
	 */
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		return compositeChange;
	}

	/**
	 * Recursively iterate over the specified java element and their children to convert
	 * each compilation to use the new WindowTester API.
	 * 
	 * @param elem the java element (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void convertElement(IJavaElement elem, IProgressMonitor monitor) throws JavaModelException {
		switch (elem.getElementType()) {
			case IJavaElement.JAVA_PROJECT :
				convertProject((IJavaProject) elem, monitor);
				break;
			case IJavaElement.PACKAGE_FRAGMENT_ROOT :
				convertPackageRoot((IPackageFragmentRoot) elem, monitor);
				break;
			case IJavaElement.PACKAGE_FRAGMENT :
				convertPackage((IPackageFragment) elem, monitor);
				break;
			case IJavaElement.COMPILATION_UNIT :
				convertCompilationUnit((ICompilationUnit) elem, monitor);
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
	private void convertProject(IJavaProject proj, IProgressMonitor monitor) throws JavaModelException {
		if (visited.contains(proj))
			return;
		visited.add(proj);
		IPackageFragmentRoot[] roots = proj.getPackageFragmentRoots();
		monitor.beginTask("Converting " + proj.getPath(), roots.length);
		for (int i = 0; i < roots.length; i++)
			convertPackageRoot(roots[i], new SubProgressMonitor(monitor, 1));
		monitor.done();
	}

	/**
	 * Recursively iterate over the elements in the specified java element to convert each
	 * compilation to use the new WindowTester API.
	 * 
	 * @param root the package fragment root (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void convertPackageRoot(IPackageFragmentRoot root, IProgressMonitor monitor) throws JavaModelException {
		if (root.getKind() != IPackageFragmentRoot.K_SOURCE || visited.contains(root))
			return;
		visited.add(root);
		IJavaElement[] children = root.getChildren();
		monitor.beginTask("Converting " + root.getPath(), children.length);
		for (int i = 0; i < children.length; i++)
			convertElement(children[i], new SubProgressMonitor(monitor, 1));
		monitor.done();
	}

	/**
	 * Recursively iterate over the elements in the specified java element to convert each
	 * compilation to use the new WindowTester API.
	 * 
	 * @param pkg the package fragment (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void convertPackage(IPackageFragment pkg, IProgressMonitor monitor) throws JavaModelException {
		if (visited.contains(pkg))
			return;
		visited.add(pkg);
		IJavaElement[] children = pkg.getChildren();
		monitor.beginTask("Converting " + pkg.getPath(), children.length);
		for (int i = 0; i < children.length; i++)
			convertElement(children[i], new SubProgressMonitor(monitor, 1));
		monitor.done();
	}

	/**
	 * Convert the specified compilation unit to use the new WindowTester API.
	 * 
	 * @param compUnit the compilation unit (not <code>null</code>)
	 * @param monitor the progress monitor (not <code>null</code>)
	 */
	private void convertCompilationUnit(ICompilationUnit compUnit, IProgressMonitor monitor) throws JavaModelException {
		if (visited.contains(compUnit))
			return;
		visited.add(compUnit);
		monitor.beginTask("Converting " + compUnit.getPath(), 1);
		String oldSource = compUnit.getSource();
		WTConvertAPIContext context;
		try {
			context = new WTConvertAPIContextBuilder().buildContext(compUnit);
			convertCompilationUnitSource(context);
		}
		catch (WTConvertAPIParseException e) {
			IProblem problem = e.getProblem();
			status.addWarning(compUnit.getElementName() + " has a syntax error on line "
				+ problem.getSourceLineNumber() + " and will be skipped: " + problem);
			return;
		}
		if (context.isSourceModified()) {
			String newSource = context.getSource();
			TextFileChange change = new TextFileChange(compUnit.getElementName(), (IFile) compUnit.getResource());
			change.setEdit(new ReplaceEdit(0, oldSource.length(), newSource));
			compositeChange.add(change);
		}
		monitor.done();
	}

	/**
	 * Convert the specified compilation unit's source. This is used for testing purposes
	 * and typically not called outside this class during the normal course of events
	 * 
	 * @param source the compilation unit source
	 * @return the context containing the converted source (not <code>null</code>)
	 */
	public WTConvertAPIContext convertCompilationUnitSource(String source) {
		WTConvertAPIContext context = new WTConvertAPIContextBuilder().buildContext(source);
		convertCompilationUnitSource(context);
		return context;
	}

	private void convertCompilationUnitSource(WTConvertAPIContext context) {
		if (context.hasWTReferences())
			for (WTConvertAPIRule rule : getRules())
				rule.convert(context);
	}
}
