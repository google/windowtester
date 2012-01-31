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
package com.windowtester.eclipse.ui.convert.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

import com.windowtester.eclipse.ui.convert.WTAPIUsage;

/**
 * Editor input for displaying WindowTester API usagage in a simple text editor
 */
public final class WTAPIUsageEditorInput
	implements IStorageEditorInput
{
	private IStorage storage;

	public WTAPIUsageEditorInput(final WTAPIUsage usage) {
		storage = new IStorage() {
			public Object getAdapter(Class adapter) {
				return null;
			}
			public boolean isReadOnly() {
				return true;
			}
			public String getName() {
				return null;
			}
			public IPath getFullPath() {
				return null;
			}
			public InputStream getContents() throws CoreException {
				return new ByteArrayInputStream(usage.getAPIUsageText().getBytes());
			}
		};
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getToolTipText() {
		return "WindowTester API";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getName() {
		return "WindowTester API";
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public boolean exists() {
		return false;
	}

	public IStorage getStorage() throws CoreException {
		return storage;
	}
}