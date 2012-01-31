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
#include "os_structs.h"

#ifndef NO_CGSize
typedef struct CGSize_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID width, height;
} CGSize_FID_CACHE;

CGSize_FID_CACHE CGSizeFc;

void cacheCGSizeFields(JNIEnv *env, jobject lpObject)
{
	if (CGSizeFc.cached) return;
	CGSizeFc.clazz = (*env)->GetObjectClass(env, lpObject);
	CGSizeFc.width = (*env)->GetFieldID(env, CGSizeFc.clazz, "width", "F");
	CGSizeFc.height = (*env)->GetFieldID(env, CGSizeFc.clazz, "height", "F");
	CGSizeFc.cached = 1;
}

CGSize *getCGSizeFields(JNIEnv *env, jobject lpObject, CGSize *lpStruct)
{
	if (!CGSizeFc.cached) cacheCGSizeFields(env, lpObject);
	lpStruct->width = (float)(*env)->GetFloatField(env, lpObject, CGSizeFc.width);
	lpStruct->height = (float)(*env)->GetFloatField(env, lpObject, CGSizeFc.height);
	return lpStruct;
}

void setCGSizeFields(JNIEnv *env, jobject lpObject, CGSize *lpStruct)
{
	if (!CGSizeFc.cached) cacheCGSizeFields(env, lpObject);
	(*env)->SetFloatField(env, lpObject, CGSizeFc.width, (jfloat)lpStruct->width);
	(*env)->SetFloatField(env, lpObject, CGSizeFc.height, (jfloat)lpStruct->height);
}
#endif

#ifndef NO_CGPoint
typedef struct CGPoint_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID x, y;
} CGPoint_FID_CACHE;

CGPoint_FID_CACHE CGPointFc;

void cacheCGPointFields(JNIEnv *env, jobject lpObject)
{
	if (CGPointFc.cached) return;
	CGPointFc.clazz = (*env)->GetObjectClass(env, lpObject);
	CGPointFc.x = (*env)->GetFieldID(env, CGPointFc.clazz, "x", "F");
	CGPointFc.y = (*env)->GetFieldID(env, CGPointFc.clazz, "y", "F");
	CGPointFc.cached = 1;
}

CGPoint *getCGPointFields(JNIEnv *env, jobject lpObject, CGPoint *lpStruct)
{
	if (!CGPointFc.cached) cacheCGPointFields(env, lpObject);
	lpStruct->x = (float)(*env)->GetFloatField(env, lpObject, CGPointFc.x);
	lpStruct->y = (float)(*env)->GetFloatField(env, lpObject, CGPointFc.y);
	return lpStruct;
}

void setCGPointFields(JNIEnv *env, jobject lpObject, CGPoint *lpStruct)
{
	if (!CGPointFc.cached) cacheCGPointFields(env, lpObject);
	(*env)->SetFloatField(env, lpObject, CGPointFc.x, (jfloat)lpStruct->x);
	(*env)->SetFloatField(env, lpObject, CGPointFc.y, (jfloat)lpStruct->y);
}
#endif