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
#include <Carbon/Carbon.h>

#include "os_ext.h"

#ifndef NO_CGSize
void cacheCGSizeFields(JNIEnv *env, jobject lpObject);
CGSize *getCGSizeFields(JNIEnv *env, jobject lpObject, CGSize *lpStruct);
void setCGSizeFields(JNIEnv *env, jobject lpObject, CGSize *lpStruct);
#define CGSize_sizeof() sizeof(CGSize)
#else
#define cacheCGSizeFields(a,b)
#define getCGSizeFields(a,b,c) NULL
#define setCGSizeFields(a,b,c)
#define CGSize_sizeof() 0
#endif


// Copied from swt, not needed if linked to SWT lib somehow
#ifndef NO_CGPoint
void cacheCGPointFields(JNIEnv *env, jobject lpObject);
CGPoint *getCGPointFields(JNIEnv *env, jobject lpObject, CGPoint *lpStruct);
void setCGPointFields(JNIEnv *env, jobject lpObject, CGPoint *lpStruct);
#define CGPoint_sizeof() sizeof(CGPoint)
#else
#define cacheCGPointFields(a,b)
#define getCGPointFields(a,b,c) NULL
#define setCGPointFields(a,b,c)
#define CGPoint_sizeof() 0
#endif
