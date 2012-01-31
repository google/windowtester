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

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.recorder.event.IUISemanticEvent;
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
import com.windowtester.runtime.locator.ILocator;

/**
 * A snippet building strategy object.
 *
 */
public interface ICodeBlockBuilder {

	CodeBlock buildShellClosing(SemanticShellClosingEvent event);

	CodeBlock buildMove(SemanticMoveEvent event);

	CodeBlock buildResize(SemanticResizeEvent event);

	CodeBlock buildFocus(SemanticFocusEvent curr);

	CodeBlock buildButtonSelect(SemanticWidgetSelectionEvent event);

	CodeBlock buildMenuSelect(SemanticMenuSelectionEvent event);

	CodeBlock buildTreeSelect(SemanticTreeItemSelectionEvent event);

	CodeBlock buildTableSelect(SemanticTableSelectionEvent tableSelection);
	
	//TODO: merge with button?
	CodeBlock buildSelect(SemanticWidgetSelectionEvent event);

	CodeBlock buildTextEntry(String string);

	CodeBlock buildKeyEntry(String ctrl, String key);
	
	CodeBlock buildKeyEntry(String key);

	CodeBlock buildFocusChange(IWidgetIdentifier newTarget);

	CodeBlock build(SemanticListSelectionEvent listSelection);

	CodeBlock build(SemanticComboSelectionEvent comboSelection);

	CodeBlock buildDragTo(IUISemanticEvent event);

	CodeBlock buildMoveTo(IUISemanticEvent event);

	CodeBlock buildMethodInvocation(String method);

	CodeBlock buildWaitForShellShowing(SemanticShellShowingEvent event);

	CodeBlock buildWaitForShellDisposed(SemanticShellDisposedEvent event);

	ImportUnit getKeyEventImport();

	CodeBlock buildAssertion(ILocator locator, PropertyMapping propertyMapping);

	CodeBlock buildWidgetClosing(SemanticWidgetClosedEvent event);

	

}
