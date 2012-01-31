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
package com.windowtester.eclipse.ui.viewers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.eclipse.ui.jdt.analysis.util.TypeHierarchyCache;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;

@SuppressWarnings("restriction")
public class EventSequenceDNDSupport {

	
	//private static final String SNIPPET_TEST_NAME = "Snippet";
	public EventSequenceSnippetBuilder builder;
	
		
	private static class DragAdapter implements DragSourceListener {

		private static final String UICONTEXT_ACCESSOR = "getUI";
		private static final String[] UICONTEXT_ACCESSOR_PARAMS = null;
		private final EventSequenceTreeViewer viewer;
		private String generated;
		private StyledText editorWidget;
		private EventSequenceSnippetBuilder builder;
		private TypeHierarchyCache hierarchy = new TypeHierarchyCache();
		
		
		
		public DragAdapter(EventSequenceTreeViewer viewer) {
			this.viewer = viewer;
		}

		public void dragFinished(DragSourceEvent event) {
			
			try {
				doDragFinished(event);
			} catch (Throwable th) {
				th.printStackTrace();
				//ignore and move on
			}
		}

		
		
		private void doDragFinished(DragSourceEvent event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, JavaModelException {
			
			if (!currentEditorSelectionMatchesDragContents())
				return;
			
			addImports();
			updateInsertedContentsIfNecessary();
			formatInsertedContents();

		}

		private void formatInsertedContents() {
			AbstractTextEditor editor = getTextEditor();
			if (editor == null)
				return;
			
			IAction action = editor.getAction("Format"); //NOTE: this action is registered in the Java editor
			if (action == null)
				return;
			action.run();
		}

		private void updateInsertedContentsIfNecessary() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
			IJavaElement insertHost = getJavaElementAtInsertOffset();
			if (insertHost == null)
				return;

			if (insertHost instanceof IMethod) {
				insertMethodSnippet();
				return;
			}
			
			if (!hostProvidesContext())
				insertParameterizedMethod();
			
			
		}

		private void insertParameterizedMethod() {
			editorWidget.insert(builder.getParameterizedMethod());
		}

		private boolean hostProvidesContext() {
			ICompilationUnit cu = getCompilationUnit();
			IType primaryType = cu.findPrimaryType();
			ITypeHierarchy th = hierarchy.getTypeHierarchy(primaryType, primaryType.getJavaProject());
			IType[] types = th.getAllSupertypes(primaryType);
			for (int i = 0; i < types.length; i++) {
				IType type = types[i];
//				try {
//					IMethod[] methods = type.getMethods();
//					for (int j = 0; j < methods.length; j++) {
//						if (methods[j].getElementName().equals(UICONTEXT_ACCESSOR))
//							return true;
//					}
//				} catch (JavaModelException e) {
//					e.printStackTrace();
//				}
				IMethod method = type.getMethod(UICONTEXT_ACCESSOR, UICONTEXT_ACCESSOR_PARAMS);
				//System.out.println("method: " + method + " exists: " + method.exists());
				
				if (method != null && method.exists())
					return true;
			}
			return false;
		}

		private void insertMethodSnippet() {
			editorWidget.insert(builder.getMethodSnippet());
		}



		private void addImports() throws JavaModelException {
			ICompilationUnit cu = getCompilationUnit();
			if (cu == null)
				return;
			addImportsTo(cu);
		}

		private void addImportsTo(ICompilationUnit cu) throws JavaModelException {
			ImportUnit[] imports = builder.getImports();
			for (int i = 0; i < imports.length; i++) {
				if (skippedImport(imports[i]))
					continue;
				cu.createImport(imports[i].getName(), null, null);
			}
		}

		private boolean skippedImport(ImportUnit importUnit) {
			String name = importUnit.getName();
			if (name == null)
				return true;
			if (name.equals(UITestCaseSWT.class.getName()))
				return true;
			if (name.equals(UITestCaseSwing.class.getName()))
				return true;
			return false;
		}

		private ICompilationUnit getCompilationUnit() {
			IJavaElement je = getEditorInputJavaElement();
			return getCompilationUnitFor(je);
		}

		private ICompilationUnit getCompilationUnitFor(IJavaElement je) {
			if (je == null)
				return null;
			if (je instanceof ICompilationUnit)
				return (ICompilationUnit)je;
			if (je instanceof IMember)
				return ((IMember)je).getCompilationUnit();
			return getCompilationUnitFor(je.getParent());
		}

		private IJavaElement getJavaElementAtInsertOffset()
				throws NoSuchMethodException, IllegalAccessException,
				InvocationTargetException {
			int offset = editorWidget.getCaretOffset();

			//System.out.println("widget offset: " + offset);
	
			ISourceViewer editorSourceViewer = getEditorSourceViewer();
			
			if (editorSourceViewer instanceof ITextViewerExtension5) {
				offset = ((ITextViewerExtension5)editorSourceViewer).widgetOffset2ModelOffset(offset);
				//System.out.println("model offset: " + offset);
			}
			
			//IJavaElement je = getEditorInputJavaElement();
			//System.out.println("editor element handle: " + je.getHandleIdentifier());
			
			Method m = JavaEditor.class.getDeclaredMethod("getElementAt", new Class[]{int.class});
			if (m == null)
				return null;
			m.setAccessible(true);
			AbstractTextEditor editor = getTextEditor();
			if (editor == null)
				return null;
			IJavaElement insertHost = (IJavaElement) m.invoke(editor, new Object[]{new Integer(offset)});
			return insertHost;
		}


		public ISourceViewer getEditorSourceViewer() {
			AbstractTextEditor editor = getTextEditor();
			if (editor == null)
				return null;
			Method m;
			try {
				m = JavaEditor.class.getDeclaredMethod("getViewer", (Class[])null);
				if (m == null)
					return null;
				m.setAccessible(true);
				return (ISourceViewer) m.invoke(editor, (Object[])null);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			return null;

		}
		
		private IJavaElement getEditorInputJavaElement() {
			AbstractTextEditor editor = getTextEditor();
			if (editor == null)
				return null;
			return JavaUI.getEditorInputJavaElement(editor.getEditorInput());
		}

		private boolean currentEditorSelectionMatchesDragContents() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
			editorWidget = getCurrentEditorTextWidget();
			if (editorWidget == null)
				return false;
			String selectionText = editorWidget.getSelectionText();
			return matches(generated, selectionText);
		}

		private boolean matches(String expected, String actual) {
			if (expected == null || actual == null)
				return false;
			return expected.compareToIgnoreCase(actual) == 0;
		}

		private StyledText getCurrentEditorTextWidget() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			AbstractTextEditor textEditor = getTextEditor();
			if (textEditor == null)
				return null;
			Method method = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer", (Class[])null);
			method.setAccessible(true);
			ISourceViewer viewer = (ISourceViewer) method.invoke(textEditor, (Object[])null);
			if (viewer == null)
				return null;
			return viewer.getTextWidget();
		}

		private AbstractTextEditor getTextEditor() {
			IEditorPart editor = EditorFinder.getActiveEditorPartNoRetries();
			if (!(editor instanceof AbstractTextEditor))
				return null;
			AbstractTextEditor textEditor = (AbstractTextEditor)editor;
			if (editor == null)
				return null;
			return textEditor;
		}

		public void dragSetData(DragSourceEvent event) {
			//System.out.println("set: " + event);
			try {
				builder = new EventSequenceSnippetBuilder(viewer.getSelection());
				generated = builder.getMainMethodString();
				event.data = generated;
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}

		public void dragStart(DragSourceEvent event) {
			event.doit = viewer.getSelection().length != 0;
			
		}
		
		
	}
	
	
	EventSequenceSnippetBuilder snippetBuilder;


	public static void addTo(EventSequenceTreeViewer viewer) {
		// int ops = DND.DROP_MOVE | DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
		// viewer.getTreeViewer().addDragSupport(ops, transfers, new DragAdapter
		// (viewer));
		//
		TreeViewer treeViewer = viewer.getTreeViewer();
		DragSource source = new DragSource(treeViewer.getControl(),
				DND.DROP_COPY);
		source.setTransfer(transfers);
		source.addDragListener(new DragAdapter(viewer));

	}


	
}
