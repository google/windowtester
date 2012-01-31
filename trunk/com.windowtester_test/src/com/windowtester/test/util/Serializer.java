package com.windowtester.test.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.windowtester.swt.event.server.SemanticEventStream;

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
public class Serializer {

	//TODO: close streams...
	
	public static byte[] serializeOut(Object object) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream stream = new ObjectOutputStream(out);
		stream.writeObject(object);
		stream.flush();
		return out.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T serializeOutAndIn(T object) throws IOException, ClassNotFoundException {
		return (T) new SemanticEventStream(new ByteArrayInputStream(serializeOut(object))).readObject();
	}
		
}
