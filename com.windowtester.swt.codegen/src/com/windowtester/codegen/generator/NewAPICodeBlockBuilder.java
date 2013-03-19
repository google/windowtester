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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.windowtester.codegen.ITestCaseBuilder;
import com.windowtester.codegen.TestCaseGenerator;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.swing.SwingCodeBlockBuilder;
import com.windowtester.codegen.swing.SwingTestCaseBuilder;
import com.windowtester.codegen.swt.SWTCodeBlockBuilder;
import com.windowtester.codegen.util.CodeGenSnippetBuilder;
import com.windowtester.codegen.util.LocatorUtil;
import com.windowtester.internal.runtime.Adapter;
import com.windowtester.internal.runtime.DefaultCodeGenerator;
import com.windowtester.internal.runtime.ICodeGenerator;
import com.windowtester.internal.runtime.ICodegenParticipant;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.MouseConfig;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.internal.runtime.finder.IIdentifierHintProvider;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.internal.runtime.reflect.Reflector;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.ICheckable;
import com.windowtester.recorder.event.user.IMaskable;
import com.windowtester.recorder.event.user.SemanticComboSelectionEvent;
import com.windowtester.recorder.event.user.SemanticFocusEvent;
import com.windowtester.recorder.event.user.SemanticListSelectionEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticMoveEvent;
import com.windowtester.recorder.event.user.SemanticResizeEvent;
import com.windowtester.recorder.event.user.SemanticShellClosingEvent;
import com.windowtester.recorder.event.user.SemanticShellDisposedEvent;
import com.windowtester.recorder.event.user.SemanticShellShowingEvent;
import com.windowtester.recorder.event.user.SemanticTableSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.condition.WindowDisposedCondition;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JTextComponentLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.locator.IModifiable;
import com.windowtester.runtime.swt.internal.locator.VirtualItemLocator;
import com.windowtester.runtime.swt.internal.util.TextUtils;
import com.windowtester.runtime.swt.locator.ColumnLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeLocator;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;



public class NewAPICodeBlockBuilder implements ICodeBlockBuilder {

	private static final String NEW_LINE = TestCaseGenerator.NEW_LINE;
	
	private final ITestCaseBuilder _builder;
	
	public NewAPICodeBlockBuilder(ITestCaseBuilder builder) {
		_builder = builder;
	}
	
	public CodeBlock build(SemanticListSelectionEvent listSelection) {
		return selectBlock(listSelection);
	}

	public CodeBlock build(SemanticComboSelectionEvent comboSelection) {
		return selectBlock(comboSelection);
	}

	public CodeBlock buildButtonSelect(SemanticWidgetSelectionEvent event) {
		return buildSelect(event);
	}

	public CodeBlock buildFocus(SemanticFocusEvent event) {
		return notHandled("focus");
	}

	public CodeBlock buildFocusChange(IWidgetIdentifier newTarget) {
		return notHandled("focusChange");
	}


	
	public CodeBlock buildKeyEntry(String ctrl, String key) {
		addImport(WT.class);
		//if it's a legacy key, convert it:
		ctrl = fixLegacyKey(ctrl);
		StringBuffer sb = new StringBuffer();
		sb.append(getInstanceName()).append(".keyClick(").append(ctrl).append(", \'").
		  	append(key).append("\');").append(NEW_LINE);
		return new CodeBlock(sb.toString());
	}

	public CodeBlock buildKeyEntry(String key) {
		StringBuffer sb = new StringBuffer();
		addImport(WT.class);
		key = fixLegacyKey(key);
		sb.append(getInstanceName()).append('.').append("keyClick(").append(key).append(");").append(NEW_LINE);
		return new CodeBlock(sb.toString());
	}

	public CodeBlock buildMenuSelect(SemanticMenuSelectionEvent event) {
		
		addImports(event);
		
		if (isContextClick(event)) 
			return contextMenuSelectBlock(event);
		
		//fix tabs that have been interpreted into spaces
		String path = event.getPathString();
		if (path != null)
			event.setPath(escapeTabs(TextUtils.fixTabs(path)));
		return selectBlock(event);
	}



	private CodeBlock contextMenuSelectBlock(SemanticMenuSelectionEvent event) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(getInstanceName()).append('.').append("contextClick(");
		String block = toJavaString(event);
		if (block.length() == 0)
			return new CodeBlock("//WARNING: unsupported widget context selection ignored - " + event + NEW_LINE);
		
		sb.append(block);
		sb.append(", \"").append(escapeTabs(event.getPathString())).append("\");").append(NEW_LINE);
		return new CodeBlock(sb.toString());
		
	}

	private boolean isContextClick(SemanticMenuSelectionEvent event) {
		return event.getButton() == MouseConfig.SECONDARY_BUTTON;
	}

	public CodeBlock buildMethodInvocation(String method) {	
		StringBuffer sb = new StringBuffer();
        sb.append(method).append("();").append(NEW_LINE);
		return new CodeBlock(sb.toString());
	}

	public CodeBlock buildMove(SemanticMoveEvent event) {
		return notHandled("move");
	}

	public CodeBlock buildMoveTo(IUISemanticEvent event) {
		return notHandled("moveTo");
	}

	public CodeBlock buildResize(SemanticResizeEvent event) {
		return notHandled("resize");
	}

	public CodeBlock buildSelect(SemanticWidgetSelectionEvent event) {
		addImports(event);
		return selectBlock(event);
	}
	
	public CodeBlock buildDragTo(IUISemanticEvent event) {
		addImports(event);
		String block = toJavaString(event);
		StringBuffer sb = new StringBuffer();
		sb.append(getInstanceName()).append(".dragTo(").append(block).append(");").append(NEW_LINE);
		return new CodeBlock(sb.toString());
	}
	
	public CodeBlock buildAssertion(ILocator locator, PropertyMapping property) {
		addImportsForLocator(locator);
		Reflector reflector = Reflector.forObject(locator);
		String propertyKey = property.getKey();
		Class<?>[] argTypes = (!property.isBoolean() && propertyKey.startsWith("has")) ? new Class<?>[]{String.class} : null;
		if (!reflector.supports(propertyKey, argTypes))
			return notHandled("assertion of property: " + property.toString() + "(unsupported)");
		
		String assertion = toJavaString(locator) + "." + propertyKey + "(";
		
		//boolean case
		if (propertyKey.startsWith("is") || property.isBoolean()) {
			if (property.getValue().equals("false"))
				assertion += "false";
		} else {
			//String case
			assertion += '"' + property.getValue() + '"';
		}
		assertion += ")";
		
		return new CodeBlock(getInstanceName() + ".assertThat(" + assertion + ");" + NEW_LINE);
	}	
	
	private CodeBlock selectBlock(IUISemanticEvent event) {
		addImports(event);
		optionallyAddSWTModifierInfoIfNecessary(event);
		String block = toJavaString(event);
		if (block.length() == 0)
			return new CodeBlock("//WARNING: unsupported widget selection ignored - " + event + NEW_LINE);
		
		/*
		 * Optionally handle XYLocator and ColumnLocator wrappering
		 */
		block = optionallyWrapBlockInColumnLocator(block, event);
		block = optionallyWrapBlockInXYLocator(block, event);
			
		StringBuffer sb = new StringBuffer();
		sb.append(getInstanceName()).append(".click(");
		int clicks = event.getClicks();
		
		if (event instanceof IMaskable){
			IMaskable maskedEvent = (IMaskable)event;
			//masks require click count always, but otherwise only if > 1
			if (maskedEvent.getMask() != null || clicks > 1){
				if (clicks == 0) //unset clicks defaults to 1
					clicks = 1;
				sb.append(clicks).append(", ");
			}
		}
		else if (clicks > 1)
			sb.append(clicks).append(", ");
		
		sb.append(block);
		
		//append shift modifiers (e.g., WT.SHIFT)
		optionallyHandleClickMods(sb, event);

		//close block
		sb.append(");").append(NEW_LINE);
		
		return new CodeBlock(sb);
	}

	protected String toJavaString(IUISemanticEvent event) {
		return LocatorJavaStringFactory.toJavaString(event);
	}
	
	protected String toJavaString(ILocator locator) {
		return LocatorJavaStringFactory.toJavaString(locator);
	}
	
	public CodeBlock buildShellClosing(SemanticShellClosingEvent event) {
		
		if (isSWT(event))
			return buildSWTShellClosing(event);
			
		
		//TODO[!pq}: this is not right... close and dispose are not distinguished...
		//Keerti: confirm for Swing?
		addImport(ShellDisposedCondition.class);
		
		//handled above -- this will add a superfluous generic widgetlocator import
		//addImports(event);
		
		/*
		 * Hijacking and replicating buildShellDisposed(..)
		 */
		StringBuffer sb = new StringBuffer();
		sb
				.append("//TODO: this is really generated by the close (close and dispose are not differentiated)");
		sb.append(NEW_LINE);
		sb.append(getInstanceName()).append(".wait(");
		sb.append(getSystemBuilder(event).shellDisposedCondition(
				event.getName()));
		sb.append(");").append(NEW_LINE);
		return new CodeBlock(sb);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.ICodeBlockBuilder#buildWidgetClosing(com.windowtester.recorder.event.user.SemanticWidgetClosedEvent)
	 */
	public CodeBlock buildWidgetClosing(SemanticWidgetClosedEvent event) {
		addImports(event);
		String locatorString = LocatorJavaStringFactory.toJavaString(event);
		StringBuffer sb = new StringBuffer();
//		sb.append(getInstanceName()).append(".close(").append(locatorString).append(");").append(NEW_LINE);
		sb.append(getInstanceName()).append(".ensureThat(").append(locatorString).append(".isClosed());").append(NEW_LINE);
		return new CodeBlock(sb);
	}
	
	
	private CodeBlock buildSWTShellClosing(SemanticShellClosingEvent event) {
		addImports(event);
		String locatorString = LocatorJavaStringFactory.toJavaString(event);
		StringBuffer sb = new StringBuffer();
//		sb.append(getInstanceName()).append(".close(").append(locatorString).append(");").append(NEW_LINE);
		sb.append(getInstanceName()).append(".ensureThat(").append(locatorString).append(".isClosed());").append(NEW_LINE);		
		return new CodeBlock(sb);
	}

	public CodeBlock buildTextEntry(String string) {
		string = CodeGenSnippetBuilder.handleEscapes(string);
		StringBuffer sb = new StringBuffer();
		sb.append(getInstanceName()).append('.').append("enterText(\"")
			.append(string).append("\");").append(NEW_LINE);
		return new CodeBlock(sb);
	}

	public CodeBlock buildTableSelect(SemanticTableSelectionEvent tableSelection) {
		addImport(java.awt.Point.class);
		addImports(tableSelection);
		//optionallyAddSWTModifierInfoIfNecessary(tableSelection);
		StringBuffer sb = new StringBuffer();
		// check for context click
		String cmd = "click";
		String menuPath = tableSelection.getContextMenuSelectionPath();
		if (tableSelection.getButton() == 3 && menuPath != null)	
			cmd = "contextClick";
	
		sb.append(getInstanceName()).append('.').append(cmd).append("(");
		
		// check if mods, then append click count
		if (tableSelection.getMask() != null) {
			String clickString = (tableSelection.getClicks() == 1) ? "1, " : "2, ";
			sb.append(clickString);	
		}
	
		sb.append(LocatorJavaStringFactory.toJavaString(tableSelection.getHierarchyInfo()));
		if (tableSelection.getButton() == 3) {		
			if (menuPath != null)
				sb.append(", \"").append(escapeTabs(menuPath)).append("\"");
		}
			
		//check for modifiers
		optionallyHandleClickMods(sb, tableSelection);
			
		//close block
		sb.append(");").append(NEW_LINE);
			
		return new CodeBlock(sb.toString());
		
		//return selectBlock(tableSelection);
	}
	
	
	public CodeBlock buildTreeSelect(SemanticTreeItemSelectionEvent event) {
		
		addImports(event);
		optionallyAddSWTModifierInfoIfNecessary(event);
		
		StringBuffer sb = new StringBuffer();
		
		TreeEventType type 	  = event.getType();
		
		if (type == TreeEventType.SINGLE_CLICK || type == TreeEventType.DOUBLE_CLICK) {
			
			String cmd = "click";
			
			boolean isContext = event.getButton() == 3;
			
			//context clicks (override clicks/double-clicks)
			if (isContext)
				cmd = "contextClick";
			
			sb.append(getInstanceName()).append('.').append(cmd).append("(");
			
			optionallyAppendTreeClickCount(sb, event);

			sb.append(LocatorJavaStringFactory.toJavaString(event.getHierarchyInfo()));
			if (event.getButton() == 3) {
				String menuPath = event.getContextMenuSelectionPath();
				if (menuPath != null)
					sb.append(", \"").append(escapeTabs(menuPath)).append("\"");
			}
			
			//check for modifiers
			if (!isContext)
				optionallyHandleClickMods(sb, event);
			
			//close block
			sb.append(");").append(NEW_LINE);
		}	
		return new CodeBlock(sb.toString());
	}

	public CodeBlock buildWaitForShellDisposed(SemanticShellDisposedEvent event) {
		if (isSwing(event)) {
			addImport(WindowDisposedCondition.class);
		} else 
			addImport(ShellDisposedCondition.class);
		StringBuffer sb = new StringBuffer();
		sb.append(getInstanceName()).append(".wait(");
		sb.append(getSystemBuilder(event).shellDisposedCondition(event.getName()));
		sb.append(");").append(NEW_LINE);
		return new CodeBlock(sb);
	}
	
	public CodeBlock buildWaitForShellShowing(SemanticShellShowingEvent event) {
		if (isSwing(event)) {
			addImport(WindowShowingCondition.class);
		} else 
			addImport(ShellShowingCondition.class);
		StringBuffer sb = new StringBuffer();
		sb.append(getInstanceName()).append(".wait(");
		sb.append(getSystemBuilder(event).shellShowCondition(event.getName()));
		sb.append(");").append(NEW_LINE);
		return new CodeBlock(sb);
	}

	
	//////////////////////////////////////////////////////////////////////////
	//
	// Key Events
	//
	//////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.ICodeBlockBuilder#getKeyEventImport()
	 */
	public ImportUnit getKeyEventImport() {
		//kludge to handle Swing case
		if (getBuilder() instanceof SwingTestCaseBuilder) {
			return new ImportUnit(KeyEvent.class.getName());
		}		
		return new ImportUnit(WT.class.getName());
		
		//this returns SWT!
		//return _builder.getKeyEventImport();
	}
	
	//////////////////////////////////////////////////////////////////////////
	//
	// Builder Utility Helpers
	//
	//////////////////////////////////////////////////////////////////////////
	
	private String getInstanceName() {
		return getBuilder().getUIContextInstanceName();
	}

	private ITestCaseBuilder getBuilder() {
		return _builder;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Formatting
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	private static String escapeTabs(String str) {	
		return str.replaceAll("\t", "\\\\t");	
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Imports
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	//add prerequisite imports
	private void addImports(IUISemanticEvent event) {
		Object locator = event.getHierarchyInfo();
		addImportsForLocator(locator);	
	}

	private void addImportsForLocator(Object locator) {
		if (locator instanceof ISelfDescribingLocator) {
			((ISelfDescribingLocator)locator).addImports(getBuilder());
			return;
		}
		if (locator instanceof IAdaptable) {
			ICodegenParticipant cp = (ICodegenParticipant) ((IAdaptable)locator).getAdapter(ICodegenParticipant.class);
			if (cp != null) {
				cp.describeTo(new DefaultCodeGenerator() {
					public ICodeGenerator addImport(String importString) {
						NewAPICodeBlockBuilder.this.addImport(importString);
						return this;
					}
					public ICodeGenerator append(String body) {
						return this;
					}
				});
				return;
			}
			
		}
		
		
		if (locator instanceof IdentifierAdapter)
			locator = ((IdentifierAdapter)locator).getLocator();
		boolean isTreeItemLocatorCase = locator instanceof TreeItemLocator;
		while (locator != null) {
			//tree items ignore parents if they are generic tree locators that don't themselves have parents
			
			if (isTreeItemLocatorCase && locator.getClass().equals(SWTWidgetLocator.class) || locator.getClass().equals(FilteredTreeLocator.class)) {
				SWTWidgetLocator parent = (SWTWidgetLocator)locator;
				if (parent.getParentInfo() != null)
					addImport(parent.getClass());
			} else {	
				addImport(locator.getClass());
			}
			
			//icky cast... 
			if (locator instanceof WidgetLocator) {
				/*
				 * First we need to unpack control locators in the virtual locator case
				 * TODO: might there be more than one?
				 */
				
				// kp: added support for Swing widgetlocators
				if (locator instanceof SwingWidgetLocator)
					addImports((SwingWidgetLocator)locator);
				if (locator instanceof VirtualItemLocator)
					addImports(((VirtualItemLocator)locator).getControlLocator());

				//repeat kludge! -- rub: we need to pull target class out of label-wrappered locators
				if (locator.getClass() == LabeledLocator.class)
				
					addImport(((LabeledLocator)locator).getTargetClassName());
				//SWTWidgetLocators need their target classes handled as well
				if (locator.getClass() == SWTWidgetLocator.class) {
					
					String clsName = ((SWTWidgetLocator)locator).getTargetClassName();
					//(Implicit) trees should not get added in the treeitem case 
					//(unless there's a parent to the tree locator)
					if (!isTreeItemLocatorCase || 
							!clsName.equals("org.eclipse.swt.widgets.Tree") || 
							((SWTWidgetLocator)locator).getParentInfo() != null)
						addImport(((SWTWidgetLocator)locator).getTargetClassName());
				}
				//then move on to the next locator
				//in case of a pulldown, this is the control:
				if (locator instanceof PullDownMenuItemLocator) {
					locator = ((PullDownMenuItemLocator)locator).getControlLocator();
				} else {
					locator = ((WidgetLocator)locator).getParentInfo();
				}
			} else {
				locator = null; //just short-circuit...
			}
		}
	}

	
	//unpack and add preqs
	private void addImports(SWTWidgetLocator locator) {
		if (locator == null)
			return; //no-op
		Class<?> cls = locator.getClass();
		addImport(cls);
		//this is a kludge! -- rub: we need to pull target class out of label-wrappered locators
		if (cls == LabeledLocator.class)
			
			//addImport(((LabeledLocator)locator).getTargetClass());
			addImport(((LabeledLocator)locator).getTargetClassName());
			
	
	}
	
	private void addImports(SwingWidgetLocator locator){
		if (locator == null)
			return; //no-op
		
		// changed getTargetClass() to getTargetClassName() 
		Class<?> cls = locator.getClass();
		addImport(cls);
		if (cls == JTextComponentLocator.class)
			addImport(((JTextComponentLocator)locator).getTargetClassName());
		if (cls == SwingWidgetLocator.class)
			if (!LocatorUtil.isInternalSwingClass(locator.getTargetClassName()))
				addImport(locator.getTargetClassName());
			
		
	}
	
	private void addImport(Class<?> cls) {
		addImport(new ImportUnit(cls.getName()));
	}
	
	private void addImport(ImportUnit imprt) {
		getBuilder().addImport(imprt);
	}
	
	
	private void addImport(String clsName){
		addImport(new ImportUnit(clsName));
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Swing/SWT System Accessors
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean isSwing(IUISemanticEvent event) {
		return !isSWT(event);
	}
	
	private boolean isSWT(IUISemanticEvent event) {
		//first we can quickly test for the simple case
		if (event.getItemClass().startsWith("org.eclipse.swt"))
			return true;
		//else look a little further
		IWidgetIdentifier locator = event.getHierarchyInfo();
//		return locator instanceof com.windowtester.swt.WidgetLocator ||
//			locator instanceof SWTWidgetLocator;
		return locator instanceof SWTWidgetLocator;
	}
	
	IWidgetSystemCodeBlockBuilder getSystemBuilder(IUISemanticEvent event) {
		return isSwing(event) ? SwingCodeBlockBuilder.getInstance() : SWTCodeBlockBuilder.getInstance();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Legacy handling Helpers
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	//convert legacy to new key constant (e.g., SWT.ALT -> WT.ALT)
	private String fixLegacyKey(String keyConstant) {
		//notice it does trim as a side effect but this is safe
		keyConstant = keyConstant.trim();
		if (keyConstant.startsWith("SWT"))
			return keyConstant.substring(1);
		return keyConstant;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Optional appends
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	//test for modifiers and append if necessary
	private void optionallyHandleClickMods(StringBuffer sb, IUISemanticEvent event) {
		if (!(event instanceof IMaskable))
			return; //no-op
			
		IMaskable maskedEvent = (IMaskable)event; 
		if (isSWT(event)){
			if (event.getHierarchyInfo() instanceof TableItemLocator)
				tableSelectionAppendSWTMaskAndAddImports(sb, maskedEvent);
			else
				optionallyAppendSWTMaskAndAddImports(sb, maskedEvent);
		}
		else
			optionallyAppendSwingMask(sb, maskedEvent);
		
	}
	
	private void optionallyAppendSwingMask(StringBuffer sb, IMaskable maskedEvent) {
		String mod = "InputEvent.";
		String mask = maskedEvent.getMask();
		if (mask == null)
			return; //nothing to do
		List<String> convertedMasks = new ArrayList<String>();
		StringTokenizer strtok = new StringTokenizer(mask, "|");
		while (strtok.hasMoreTokens()) {
			String m = strtok.nextToken().trim();
			convertedMasks.add(mod.concat(m));
		}
		
		//bail if there's nothing to do
		if (convertedMasks.isEmpty())
			return;
		
		//add a leading comma ", " unless the last char is a '(' or a ','
		String currentBlockString = sb.toString().trim();
		if (!(currentBlockString.endsWith("(") || currentBlockString.endsWith(",")))
			sb.append(", ");
		
		//now build the tokens back up
		for (Iterator<String> iter = convertedMasks.iterator(); iter.hasNext(); ) {
			sb.append(iter.next());
			if (iter.hasNext())
				sb.append(" | ");
		}
		addImport(InputEvent.class);
		
	}
	
	//some locators do not have their modifier info set at recording time
	//the info is in the vent and needs to get migrated to the locator to simplify codegen
	private void optionallyAddSWTModifierInfoIfNecessary(IUISemanticEvent event) {
		IWidgetIdentifier locator = event.getHierarchyInfo();
		if (!(locator instanceof IModifiable))
			return; //nothing to do
		if (!(event instanceof ICheckable))
			return; //nothing to do
		IModifiable modifiable = (IModifiable)locator;
		ICheckable checkable = (ICheckable)event;
		if (checkable.getChecked()) {
			modifiable.setSelectionModifiers(modifiable.getSelectionModifiers() | WT.CHECK);
			addImport(WT.class);
		}
	}
	
	private void optionallyAppendTreeClickCount(StringBuffer sb, SemanticTreeItemSelectionEvent event) {
		if (event.isContext())
			return;
		
		TreeEventType type = event.getType();
		//TODO: triple+ clicks
		if (type == TreeEventType.DOUBLE_CLICK) {
			sb.append("2, ");
			return;
		}
		//modified clicks require count as well
		if (event.getMask() != null) {
			String clickString = (type == TreeEventType.DOUBLE_CLICK) ? "2, " : "1, ";
			sb.append(clickString);	
		}
	}
	
	private void optionallyAppendSWTMaskAndAddImports(StringBuffer sb, IMaskable maskedEvent) {
		String mask = maskedEvent.getMask();
		if (mask == null)
			return; //nothing to do
		List<String> convertedMasks = new ArrayList<String>();
		StringTokenizer strtok = new StringTokenizer(mask, "|");
		while (strtok.hasMoreTokens()) {
			String m = strtok.nextToken().trim();
			//buttons are ignored (SWT.BUTTON1) -- CHECKS too
			if (m.startsWith("SWT") && !m.startsWith("SWT.BUTTON") && !m.equals("SWT.CHECK")) { 
				addImport(WT.class);
				convertedMasks.add(m.substring(1));
			}
		}
		
		appendSWTMask(sb, convertedMasks);
		
	}

	private void tableSelectionAppendSWTMaskAndAddImports(StringBuffer sb, IMaskable maskedEvent) {
		String mask = maskedEvent.getMask();
		if (mask == null)
			return; //nothing to do
		List<String> convertedMasks = new ArrayList<String>();
		StringTokenizer strtok = new StringTokenizer(mask, "|");
		while (strtok.hasMoreTokens()) {
			String m = strtok.nextToken().trim();
			if (m.startsWith("SWT") && !m.startsWith("SWT.BUTTON")) { 
				addImport(WT.class);
				convertedMasks.add(m.substring(1));
			}
		}
		
		appendSWTMask(sb, convertedMasks);
		
	}
	
	private void appendSWTMask(StringBuffer sb, List<String> convertedMasks) {
		//bail if there's nothing to do
		if (convertedMasks.isEmpty())
			return;
		
		//add a leading comma ", " unless the last char is a '(' or a ','
		String currentBlockString = sb.toString().trim();
		if (!(currentBlockString.endsWith("(") || currentBlockString.endsWith(",")))
			sb.append(", ");
		
		//now build the tokens back up
		for (Iterator<String> iter = convertedMasks.iterator(); iter.hasNext(); ) {
			sb.append(iter.next());
			if (iter.hasNext())
				sb.append(" | ");
		}
	}

	private String optionallyWrapBlockInColumnLocator(String block, IUISemanticEvent event) {
		if (event.getIndex() != -1 && event.getHierarchyInfo() instanceof TableItemLocator) {
			addImport(ColumnLocator.class);
			return wrapInColumnLocator(block, event);
		}
		return block;
	}

	private String optionallyWrapBlockInXYLocator(String block, IUISemanticEvent event) {
		if (requiresXY(event)) {
			addImport(XYLocator.class);
			return wrapInXYLocator(block, event);
		}
		return block;
	}

	private boolean requiresXY(IUISemanticEvent event) {
		//check for overriding hint
		IIdentifierHintProvider hint = (IIdentifierHintProvider) Adapter.adapt(event.getHierarchyInfo(), IIdentifierHintProvider.class);
		if (hint != null)
			return hint.requiresXY();
		return event.requiresLocationInfo();
	}

	private String wrapInXYLocator(String block, IUISemanticEvent event) {
		StringBuffer sb = new StringBuffer();
		sb.append("new XYLocator(").append(block).append(", ").append(event.getX()).append(", ").append(event.getY()).append(")");
		return sb.toString();
	}

	private String wrapInColumnLocator(String block, IUISemanticEvent event) {
		StringBuffer sb = new StringBuffer();
		sb.append("new ColumnLocator(").append(event.getIndex()).append(", ").append(block).append(")");
		return sb.toString();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	////////////////////////////////////////////////////////////////////////////////////////

	private CodeBlock notHandled(String type) {
		return new CodeBlock("//event type: [" + type + "] not handled" + NEW_LINE);
	}

}
