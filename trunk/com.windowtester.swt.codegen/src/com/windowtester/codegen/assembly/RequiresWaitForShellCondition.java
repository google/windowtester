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
package com.windowtester.codegen.assembly;

import com.windowtester.codegen.ISourceTypeBuilder;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.CodeUnit;
import com.windowtester.codegen.assembly.unit.ImportUnit;


/**
 * This requirement is used in situations where a new shell needs time to appear
 * before an action can be performed (an OK dialog for instance).
 */
public class RequiresWaitForShellCondition extends CodeRequirement {

    /** The title of the shell on which to wait */
    private final String _shellTitle;

    public RequiresWaitForShellCondition(String shellTitle) {
        _shellTitle = shellTitle;
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.CodeRequirement#doSatisfy(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public void doSatisfy(ISourceTypeBuilder builder) {
        CodeBlock current = builder.getCurrentBlock();
        CodeUnit parent = current.getParent();
        if (parent instanceof CodeBlock) {
            ((CodeBlock)parent).addChildBefore(getWaitForBlock(), current);
        }
        //else: should error here
    }

    /**
     * @return a block that waits for the required condition
     */
    private CodeBlock getWaitForBlock() {
        CodeBlock block = new CodeBlock("WidgetTester.waitForShellShowing(\"" + _shellTitle +"\")");
        block.addRequirement(new RequiresImport(new ImportUnit("abbot.tester.swt.WidgetTester")));
        return block;
    }

    /* (non-Javadoc)
     * @see com.windowtester.codegen.assembly.ICodeRequirement#isSatisfied(com.windowtester.codegen.ISourceTypeBuilder)
     */
    public boolean isSatisfied(ISourceTypeBuilder builder) {
        //TODO: should check to ensure there is a check...  
        //for now, assuming there isn't and adding (a possibly redundant check)
        return false;
    }

}
