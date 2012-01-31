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
package com.windowtester.runtime.gef.internal;

import com.windowtester.runtime.gef.IFigureMatcher;


/**
 * An (internal) marker interface for GEF matchers.
 * <p>
 * Clients are <b>not intended to implement this interface</b>.  Instead, they 
 * should implement one of it's subtypes: {@link IFigureMatcher}, {@link IGEFEditPartMatcher},
 * or {@link IModelObjectMatcher}.
 */
public interface IGEFMatcher {

}
