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
package com.windowtester.recorder.ui;

/**
 * Convenience factory.
 */
public class RecorderUI {

	public static IRecorderPanelModel getPanelModel() {
		return getPanelModel(new EventSequenceModel());
	}

	

	public static IRecorderPanelModel getPanelModel(IEventSequenceModel eventProvider) {
		return new RecorderPanelModel(eventProvider);
	}
	
}
