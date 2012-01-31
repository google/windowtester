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
package com.windowtester.ui.internal.corel.model;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.ui.core.model.IObservation;
import com.windowtester.ui.core.model.IObservationSequence;

public class ObservationSequence implements IObservationSequence {

	
	List _observations = new ArrayList();
	
	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IObservationSequence#getObservations()
	 */
	public IObservation[] getObservations() {
		return (IObservation[]) _observations.toArray(new IObservation[]{});
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IObservationSequence#add(com.windowtester.util.core.model.IObservation)
	 */
	public void add(IObservation o) {
		_observations.add(o);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IObservationSequence#remove(com.windowtester.util.core.model.IObservation)
	 */
	public void remove(IObservation o) {
		_observations.remove(o);
	}
	
}
