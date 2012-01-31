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
package com.windowtester.codegen.assembly.unit;

public class ImportUnit extends MemberUnit {

	public static ImportUnit forStatic(String name) {
		ImportUnit imprt = new ImportUnit(name);
		imprt.addModifier(Modifier.STATIC);
		return imprt;
	}
	

	public static ImportUnit forName(String name) {
		return new ImportUnit(name);
	}

	
    /**
     * Create an instance.
     * @param name
     */
    public ImportUnit(String name) {
        super(name);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {    
    	if (!(obj instanceof ImportUnit))
    		return false;
    	ImportUnit other = (ImportUnit)obj;
    	return getName().equals(other.getName());
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
    	return 13 + getName().hashCode();
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return "ImportUnit(" + getName() +")";
    }

    
}
