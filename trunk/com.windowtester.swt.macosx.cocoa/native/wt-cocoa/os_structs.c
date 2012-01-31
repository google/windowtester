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

#ifdef __LP64__
#define GetRealField GetDoubleField
#define SetRealField SetDoubleField
#define FIELD_SIG "D"
#define JREAL jdouble
#define REAL double
#else
#define GetRealField GetFloatField
#define SetRealField SetFloatField
#define FIELD_SIG "G"
#define JREAL jfloat
#define REAL float
#endif

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
	CGSizeFc.width = (*env)->GetFieldID(env, CGSizeFc.clazz, "width", FIELD_SIG);
	CGSizeFc.height = (*env)->GetFieldID(env, CGSizeFc.clazz, "height", FIELD_SIG);
	CGSizeFc.cached = 1;
}

CGSize *getCGSizeFields(JNIEnv *env, jobject lpObject, CGSize *lpStruct)
{
	if (!CGSizeFc.cached) cacheCGSizeFields(env, lpObject);
	lpStruct->width = (REAL)(*env)->GetRealField(env, lpObject, CGSizeFc.width);
	lpStruct->height = (REAL)(*env)->GetRealField(env, lpObject, CGSizeFc.height);
	return lpStruct;
}

void setCGSizeFields(JNIEnv *env, jobject lpObject, CGSize *lpStruct)
{
	if (!CGSizeFc.cached) cacheCGSizeFields(env, lpObject);
	(*env)->SetRealField(env, lpObject, CGSizeFc.width, (JREAL)lpStruct->width);
	(*env)->SetRealField(env, lpObject, CGSizeFc.height, (JREAL)lpStruct->height);
}


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
	CGPointFc.x = (*env)->GetFieldID(env, CGPointFc.clazz, "x", FIELD_SIG);
	CGPointFc.y = (*env)->GetFieldID(env, CGPointFc.clazz, "y", FIELD_SIG);
	CGPointFc.cached = 1;
}

CGPoint *getCGPointFields(JNIEnv *env, jobject lpObject, CGPoint *lpStruct)
{
	if (!CGPointFc.cached) cacheCGPointFields(env, lpObject);
	lpStruct->x = (REAL)(*env)->GetRealField(env, lpObject, CGPointFc.x);
	lpStruct->y = (REAL)(*env)->GetRealField(env, lpObject, CGPointFc.y);
	return lpStruct;
}

void setCGPointFields(JNIEnv *env, jobject lpObject, CGPoint *lpStruct)
{
	if (!CGPointFc.cached) cacheCGPointFields(env, lpObject);
	(*env)->SetRealField(env, lpObject, CGPointFc.x, (JREAL)lpStruct->x);
	(*env)->SetRealField(env, lpObject, CGPointFc.y, (JREAL)lpStruct->y);
}
