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
package com.windowtester.codegen.generator;

import org.eclipse.core.runtime.Platform;

import com.windowtester.codegen.util.LocatorUtil;
import com.windowtester.internal.runtime.DefaultCodeGenerator;
import com.windowtester.internal.runtime.ICodeGenerator;
import com.windowtester.internal.runtime.ICodegenParticipant;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.finder.ISearchScope;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.locator.IItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IPathLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.locator.AbstractPathLocator;
import com.windowtester.runtime.swing.locator.JTableItemLocator;
import com.windowtester.runtime.swing.locator.JTextComponentLocator;
import com.windowtester.runtime.swt.internal.locator.IControlRelativeLocator;
import com.windowtester.runtime.swt.internal.locator.IModifiable;
import com.windowtester.runtime.swt.internal.util.TextUtils;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ActiveEditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;

/**
 * Factory for generating Java Strings from locator instances.
 */
public class LocatorJavaStringFactory {

	
	//warning for use in testing ONLY!
	public static class TestOverride {
		private static ICodegenAdvisor testAdvisor;
		public static void setToStringDelegate(ICodegenAdvisor testAdvisor) {
			TestOverride.testAdvisor = testAdvisor;
		}
		public static boolean isSet() {
			return testAdvisor != null;
		}
	}
	
	
	public static String toJavaString(IUISemanticEvent event) {
		IWidgetIdentifier locator = event.getHierarchyInfo();
		if (locator instanceof IdentifierAdapter)
			return toJavaString(((IdentifierAdapter)locator).getLocator());
//		if (locator instanceof com.windowtester.swt.WidgetLocator)
//			return SWTLocatorJavaStringFactory.locatorToJavaString(event);
		return toJavaString(locator);
	}
	

	public static String toJavaString(ILocator locator) {
		ICodegenParticipant cp = adaptToCodegenParticipant(locator);		
		if (cp != null ) {
			ICodeGenerator cg = new CodeGenDelegate();
			cp.describeTo(cg);
			return cg.toString();
		}
		if (locator instanceof SwingWidgetLocator)
			return wlToString((SwingWidgetLocator)locator);
		if (locator instanceof SWTWidgetLocator)
			return wlToString((SWTWidgetLocator)locator); 
		return basicToString(locator);
	}


	//not accessible to subclasses to avoid possibility of circularity
	private static String basicToString(ILocator locator) {
		
		String contributed = delegateToString(locator);
		if (contributed != null)
			return contributed;
		

		
		StringBuffer sb = new StringBuffer();
		appendCons(sb, locator);		
		optionallyAppendPath(sb, locator);
		optionallyAppendScope(sb, locator);
		sb.append(")");
		return sb.toString();
	}


	protected static String delegateToString(ILocator locator) {
		if (TestOverride.isSet())
			return TestOverride.testAdvisor.toJavaString(locator);
		if (!Platform.isRunning())
			return null;
		return CodegenContributionManager.toJavaString(locator);
	}


	protected static void optionallyAppendScope(StringBuffer sb, ILocator locator) {
		IWidgetLocator scopeLocator = adaptToScopeLocator(locator);
		if (scopeLocator == null)
			return;
		if (scopeLocator instanceof ActiveEditorLocator)
			return; //this is the default find behavior
		
		sb.append(", ");
		sb.append(toJavaString(scopeLocator));
	}


	protected static IWidgetLocator adaptToScopeLocator(ILocator locator) {
		if (!(locator instanceof IAdaptable))
			return null;
		ISearchScope scope = (ISearchScope) ((IAdaptable)locator).getAdapter(ISearchScope.class);
		IWidgetLocator scopeLocator = adaptToLocator(scope);
		return scopeLocator;
	}


	protected static IWidgetLocator adaptToLocator(ISearchScope scope) {
		if (scope instanceof IWidgetLocator)
			return (IWidgetLocator)scope;
		if (!(scope instanceof IAdaptable))
			return null;
		return (IWidgetLocator) ((IAdaptable)scope).getAdapter(IWidgetLocator.class);
	}


	protected static void optionallyAppendPath(StringBuffer sb, ILocator locator) {
		/*
		 * perspective special case:
		 */
		if (locator instanceof PerspectiveLocator) {
			sb.append("\"").append(((PerspectiveLocator)locator).getPerspectiveId()).append("\"");
			return;
		}
			
		IItemLocator itemLocator = adaptToItemLocator(locator);
		if (itemLocator == null)
			return;
		String path = itemLocator.getPath();
		sb.append("\"").append(path).append("\"");
	}


	protected static IItemLocator adaptToItemLocator(ILocator locator) {
		if (locator instanceof IItemLocator) {
			return (IItemLocator)locator;
		}
		if (locator instanceof IAdaptable)
			return (IItemLocator) ((IAdaptable)locator).getAdapter(IItemLocator.class);
		return null;
	}

	private static class CodeGenDelegate extends DefaultCodeGenerator {
		private StringBuffer sb = new StringBuffer();
		public ICodeGenerator addImport(String importString) {
			//ignored here
			return this;
		}
		public ICodeGenerator append(String body) {
			sb.append(body);
			return this;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return sb.toString();
		}
	}
	

	public static String toJavaString(IWidgetIdentifier locator) {
		StringBuffer sb = new StringBuffer();
		if (locator instanceof ISelfDescribingLocator)
			return ((ISelfDescribingLocator)locator).toJavaString();
		ICodegenParticipant cp = adaptToCodegenParticipant(locator);		
		if (cp != null ) {
			ICodeGenerator cg = new CodeGenDelegate();
			cp.describeTo(cg);
			return cg.toString();
		}
		if (locator instanceof SwingWidgetLocator)
			return wlToString((SwingWidgetLocator)locator);
		if (locator instanceof SWTWidgetLocator)
			return wlToString((SWTWidgetLocator)locator); 
		return sb.toString();
	}


	private static ICodegenParticipant adaptToCodegenParticipant(Object locator) {
		if (locator instanceof ICodegenParticipant)
			return (ICodegenParticipant) locator;
		if (locator instanceof IAdaptable) {
			return (ICodegenParticipant) ((IAdaptable)locator).getAdapter(ICodegenParticipant.class);
		}
		return null;
	}


	protected static String wlToString(SWTWidgetLocator locator) {
		StringBuffer sb = new StringBuffer();
		appendCons(sb, locator);
		
		optionallyAppendModifiers(sb, locator);
		
		//"raw" widgetlocator case
		if (locator.getClass() == SWTWidgetLocator.class) {
			appendClass(sb, locator);
		}
		//label special case: labeled widget class PRECEDES label
		if (locator instanceof LabeledLocator && !(locator instanceof LabeledTextLocator)) {
			appendClass(sb, locator);
		}
		
		
		//path based locator case
		//TODO: make this an interface!
		if (locator instanceof IPathLocator) {
			appendPath(sb, (IPathLocator)locator);	
		//default	
		} else {
			appendLabel(sb, locator);
		}
		
		
		
		appendParentInfo(sb, locator);
		
		sb.append(")");
		return sb.toString();
	}


	protected static void appendCons(StringBuffer sb, ILocator locator) {
		String className = getClassName(locator.getClass());
		sb.append("new ");
		sb.append(className);
		sb.append("(");
	}


	//check for modifiers like WT.CHECK
	protected static void optionallyAppendModifiers(StringBuffer sb, SWTWidgetLocator locator) {
		//checked case
		if (locator instanceof IModifiable) {
			int mods = ((IModifiable)locator).getSelectionModifiers();
			if (isCheck(mods)) {
				appendCheck(sb);
				appendDelimeter(sb);
			}
		}
	}


	protected static void appendCheck(StringBuffer sb) {
		sb.append("WT.CHECK");
	}


	protected static boolean isCheck(int mods) {
		return (mods & WT.CHECK) == WT.CHECK;
	}


	protected static void appendClass( StringBuffer sb, SWTWidgetLocator locator) {
		
		String targetClassName = locator.getTargetClassName();
		if (targetClassName != null){
			sb.append(getClassName(targetClassName)).append(".class");
		}
	}


	protected static String wlToString(com.windowtester.runtime.WidgetLocator locator) {
		if (locator instanceof SWTWidgetLocator)
			return wlToString((SWTWidgetLocator)locator);
		if (locator instanceof SwingWidgetLocator)
			return wlToString((SwingWidgetLocator)locator);
		throw new IllegalArgumentException("unexpected type: " + locator.getClass());
	}


	protected static String wlToString(SwingWidgetLocator locator) {
		StringBuffer sb = new StringBuffer();
		String className = getClassName(locator.getClass());
		sb.append("new ");
		sb.append(className);
		sb.append("(");

		//"raw" widgetlocator case
		if (locator.getClass() == SwingWidgetLocator.class) {
			
			String targetClassName = locator.getTargetClassName();
			Boolean isInternal = LocatorUtil.isInternalSwingClass(targetClassName);
			if (targetClassName != null && !isInternal){
				sb.append(getClassName(targetClassName)).append(".class");
			}
			else if (isInternal){
				sb.append("\"");
				sb.append(getClassName(targetClassName)).append("\"");
			}
			
			
		}
		
		//path based locator case
		//TODO: make this an interface!
		if (locator instanceof AbstractPathLocator) {
			appendPath(sb, (AbstractPathLocator)locator);	
		//tables
		} else if (locator instanceof JTableItemLocator) {
			appendTableInfo(sb, (JTableItemLocator)locator);
		//text	
		} else if (locator instanceof JTextComponentLocator){  
			appendTextComponentInfo(sb,locator);
		} else {
			appendLabel(sb, locator);
		}
		
		appendParentInfo(sb, locator);
		
		sb.append(")");
		return sb.toString();
	}
	
	protected static void appendTableInfo(StringBuffer sb, JTableItemLocator locator) {
	//	appendLabel(sb, locator);
		sb.append("new Point(").append(locator.getRow()).append(',').append(locator.getColumn()).append(')');		
	}
	
	protected static void appendTextComponentInfo(StringBuffer sb,SwingWidgetLocator locator){
		String targetClassName = locator.getTargetClassName();
		// check for caret position
		int caret = ((JTextComponentLocator)locator).getCaretPosition();
		if (caret != WidgetLocator.UNASSIGNED){
			sb.append(caret);
			sb.append(",");
		}
		if (!(locator instanceof com.windowtester.runtime.swing.locator.LabeledTextLocator))
			sb.append(getClassName(targetClassName)).append(".class");
		appendLabel(sb,locator);
		
	}

	protected static void appendParentInfo(StringBuffer sb, SwingWidgetLocator locator) {
		com.windowtester.runtime.WidgetLocator parentInfo = locator.getParentInfo();
		if (parentInfo != null) {
			int index = locator.getIndex();
			if (index != SwingWidgetLocator.UNASSIGNED){
				appendDelimeter(sb);
				sb.append(index);
			}
			appendDelimeter(sb);
			sb.append(wlToString(parentInfo));
		}
	}
	
	protected static void appendParentInfo(StringBuffer sb, SWTWidgetLocator locator) {
		com.windowtester.runtime.WidgetLocator parentInfo = locator.getParentInfo();
		//note, virtual items have virtual parents and we need to fetch those...
		if (locator instanceof IControlRelativeLocator) 
			parentInfo = ((IControlRelativeLocator)locator).getControlLocator();
		
		if (parentInfo != null && !specialCaseIgnoreParentLocator(locator)) {
			int index = locator.getIndex();
			if (index != SWTWidgetLocator.UNASSIGNED){
				appendDelimeter(sb);
				sb.append(index);
			}
			appendDelimeter(sb);
			sb.append(wlToString(parentInfo));
		}
	}


//	protected static com.windowtester.runtime.WidgetLocator optionallyResynthesizeVirtualParents(SWTWidgetLocator locator, com.windowtester.runtime.WidgetLocator parentInfo) {
//		if (locator instanceof ComboItemLocator) {
//			if (parentInfo == null) {
//				parentInfo = locator.getParentInfo();
//				if (parentInfo != null)
//					parentInfo = new SWTWidgetLocator(Combo.class, WidgetLocator.UNASSIGNED, (SWTWidgetLocator)parentInfo);
//			}
//		}
//		if (locator instanceof CComboItemLocator) {
//			if (parentInfo == null) {
//				parentInfo = locator.getParentInfo();
//				if (parentInfo != null)
//					parentInfo = new SWTWidgetLocator(CCombo.class, WidgetLocator.UNASSIGNED, (SWTWidgetLocator)parentInfo);
//			}
//		}
//		return parentInfo;
//	}
	
	


	/**
	 * This identifies special cases where parent hierarchy can be ignored (it's implicit).
	 */
	protected static boolean specialCaseIgnoreParentLocator(SWTWidgetLocator locator) {
		if (locator instanceof TreeItemLocator) {
			com.windowtester.runtime.WidgetLocator parentInfo = locator.getParentInfo();
			//note: want to provision for named case, which should NOT be special cased
			if (parentInfo instanceof NamedWidgetLocator)
				return false;
			
			//return (parentInfo != null && parentInfo.getTargetClass() == Tree.class && !(parentInfo instanceof LabeledLocator) && parentInfo.getParentInfo() == null);
			//^--- is this scary enough?!? ;) [!pq: let's fix 2.0]
			return (parentInfo != null && 
					parentInfo.getTargetClassName().equals("org.eclipse.swt.widgets.Tree") && 
					!(parentInfo instanceof LabeledLocator) && 
					parentInfo.getParentInfo() == null);
		}
		return false;
	}


	static void appendLabel(StringBuffer sb, IWidgetIdentifier locator) {
		String label = locator.getNameOrLabel();
		if (label != null) {
			appendDelimeter(sb);
			sb.append("\"").append(escapeText(label)).append("\"");
		}
	}

	//append a comma if necessary
	protected static void appendDelimeter(StringBuffer sb) {
		String current = sb.toString().trim();
		if (!current.endsWith("(") && !current.endsWith(","))
			sb.append(", ");
	}

	protected static void appendPath(StringBuffer sb, IPathLocator pathLocator) {
		String path = pathLocator.getPath();
		if (path != null)
			sb.append("\"").append(escapeTabs(escapeText(path))).append("\"");
	}
	
	protected static String escapeText(String text) {
		return TextUtils.escapeText(text);
	}


	protected static String escapeTabs(String str) {	
		return str.replaceAll("\t", "\\\\t");	
	}
	
	static String getClassName(Class cls) {
		return getClassName(cls, false);
	}
	
	protected static String getClassName(Class cls, boolean qualify) {
		// get the simple name of the class
		int lastPeriod = cls.getName().lastIndexOf('.');
		String simpleName = (lastPeriod >= 0) ? cls.getName().substring(
				lastPeriod + 1) : cls.getName();

		// cls
		return (qualify) ? cls.getName() : simpleName;
	}

	
	protected static String getClassName(String str){
		int lastPeriod = str.lastIndexOf('.');
		String simpleName = (lastPeriod >= 0) ? str.substring(
				lastPeriod + 1) : str;
		return simpleName;
	}



	
}
