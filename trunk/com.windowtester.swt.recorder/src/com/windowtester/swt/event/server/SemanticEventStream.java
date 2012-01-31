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
package com.windowtester.swt.event.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.Socket;

import com.windowtester.internal.runtime.resolver.ClassResolverContributionManager;
import com.windowtester.recorder.event.ISemanticEvent;


/**
 * A custom input stream for reading and resolving semantic events.
 */
public class SemanticEventStream extends ObjectInputStream {

	
	public static SemanticEventStream forSocket(Socket socket) throws IOException {
		return new SemanticEventStream(socket.getInputStream());
	}
	
	public SemanticEventStream(InputStream in) throws IOException {
		super(in);
	}

	public ISemanticEvent readEvent() throws IOException, ClassNotFoundException {
		return (ISemanticEvent)readObject();
	}
	
	
	protected Class resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
		try {
			return super.resolveClass(desc);
		} catch (Exception e) {
			return resolveContributedClass(desc, e);
		} 
	}

	private Class resolveContributedClass(ObjectStreamClass desc, Exception e) throws ClassNotFoundException {
		Class resolved = ClassResolverContributionManager.resolveClass(desc.getName());
		if (resolved == null)
			throw new ClassNotFoundException("No contributed resolver for class", e);
		return resolved;
	}
	
	
	
}
