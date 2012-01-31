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
package com.windowtester.codegen.assembly.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.windowtester.codegen.assembly.unit.CodeUnit;
import com.windowtester.codegen.assembly.unit.VarUnit;


/**
 * A base class for basic code units.
 */
public class CodeBlock extends CodeUnit {

    /** List of child blocks */
    private List _blocks;
    
    /** The parent code unit (might be null) */
    private CodeUnit _parent;
    
    /** Set of locally defined variables */
    private Set _locals;
    
    /** A list of exceptions caught in this block, if non-null, the block
     * wraps a try-catch
     */
    private List _catches;

    /** The body of this block */
    private String _body;
    
    /**
     * Create an instance.
     */
    public CodeBlock() { }
    
    /**
     * Create an instance.
     * @param body
     */
    public CodeBlock(String body) {
        _body = body;
    }
    
    /**
     * Create an instance.
     * @param body
     */
    public CodeBlock(StringBuffer body) {
        this(body.toString());
    }
    
    public void setBody(String body) {
    	_body = body;
    }
    
    
    /**
     * @return the list of child blocks
     */
    private List getBlocks() {
        if (_blocks == null)
            _blocks = new LinkedList();
        return _blocks;
    }
    
    /**
     * @return the set of locally defined variables
     */
    public Set getLocals() {
        if (_locals == null)
            _locals = new HashSet();
        return _locals;
    }
    
    /**
     * @return the list of catch clauses that close this block (null if there are none)
     * TODO: maybe this should be pushed into a TryCatchBlock?
     */
    public List getCatches() {
        if (_catches == null)
            _catches = new ArrayList();
        return _catches;
    }
    
    
    /**
     * Get the children of this block.  Note that this list is immutable,
     * to add entries, see the addChild*(..) and removeChild(..) methods.
     * @return the list of children
     */
    public List getChildren() {
        return Collections.unmodifiableList(getBlocks());
    }
    
    /**
     * Remove this block from the list of children.
     * @param child
     */
    public void removeChild(CodeBlock child) {
        getBlocks().remove(child);
    }
    
    /**
     * Adds a block to the end of this block's list of children
     * @param block
     */
    public void addChild(CodeBlock block){
        block._parent = this;
        getBlocks().add(block);
    }
    
    /**
     * Add this block before the given block in the child list.  If the block
     * to precede is not in the list, the block is inserted at the start of the list.
     * @param toAdd
     * @param toPrecede
     */
    public void addChildBefore(CodeBlock toAdd, CodeBlock toPrecede) {
        toAdd._parent = this;
        int index = getBlocks().indexOf(toPrecede);
        if (index == -1)
            index = 0;  
        getBlocks().add(index, toAdd);
    }
   
    /**
     * Add this block before the given block in the child list.If the block
     * to sucede is not in the list, the block is inserted at the end of the list.
     * @param toAdd
     * @param toAdd
     * @param toSucede
     */
    public void addChildAfter(CodeBlock toAdd, CodeBlock toSucede) {
        toAdd._parent = this;
        int index = 0;
        //if it's not at the beginning, look for the right index
        if (toSucede != null)
            index = getBlocks().indexOf(toSucede);
        if (index == -1)
            index = getBlocks().size()-1;  
        getBlocks().add(index, toAdd);
    }    
   
    /**
     * @return this block's immediate parent
     */
    public CodeUnit getParent() {
        return _parent;
    }

    /**
     * Checks to see if a given id is in scope.
     * @param id - the id to check
     * @return true if the given name is in scope
     * TODO: needs to walk up the chain of blocks...
     */
    public boolean isIdentifierInScope(String id) {
        Set localVars = getLocals();
        for (Iterator iter = localVars.iterator(); iter.hasNext();) {
            VarUnit local = (VarUnit) iter.next();
            if (local.getName().equals(id))
                return true;
        }
        //for now, very naive: doesn't check fields...
        //TODO: might consider an "owner" field to identify the type that owns the block to make
        //field lookup easier...
        return false;
        
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer sb = new StringBuffer();
        sb.append(_body == null ? "" : _body);
        for (Iterator iter = getBlocks().iterator(); iter.hasNext();) {
            CodeBlock element = (CodeBlock) iter.next();
            sb.append(element);
        }
        return sb.toString();
    }

    /**
     * Ad a local variable definition to this block.
     * @param name
     */
    public void addLocal(VarUnit var) {
        getLocals().add(var);
    }
    
}
