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
#include "jni.h"
#include "os_ext.h"
#include "os_structs.h"

#define OS_NATIVE(func) Java_com_windowtester_swt_macosx_external_OSExt_##func

#ifndef NO_AXUIElementCopyAttributeValue
JNIEXPORT jint JNICALL OS_NATIVE(AXUIElementCopyAttributeValue)
	(JNIEnv *env, jclass that, jint arg0, jint arg1, jintArray arg2)
{
	jint *lparg2=NULL;
	jint rc = 0;

	if (arg2) if ((lparg2 = (*env)->GetIntArrayElements(env, arg2, NULL)) == NULL) goto fail;

	rc = (jint)AXUIElementCopyAttributeValue((AXUIElementRef)arg0, (CFStringRef)arg1, (CFTypeRef *) lparg2 );

fail:
	if (arg2 && lparg2) (*env)->ReleaseIntArrayElements(env, arg2, lparg2, 0);

	return rc;
}
#endif

#ifndef NO_AXValueGetValueCGPoint
JNIEXPORT jint JNICALL OS_NATIVE(AXValueGetValueCGPoint)
	(JNIEnv *env, jclass that, jint arg0, jobject arg1)
{
	CGPoint _arg1, *lparg1=NULL;
	jint rc = 0;

	if (arg1) if ((lparg1 = getCGPointFields(env, arg1, &_arg1)) == NULL) goto fail;
	rc = (jint) AXValueGetValue((AXValueRef)arg0, kAXValueCGPointType, (void *) lparg1);

fail:
	if (arg1 && lparg1) setCGPointFields(env, arg1, lparg1);
	
	return rc;
}
#endif

#ifndef NO_AXValueGetValueCGSize
JNIEXPORT jint JNICALL OS_NATIVE(AXValueGetValueCGSize)
	(JNIEnv *env, jclass that, jint arg0, jobject arg1)
{
	CGSize _arg1, *lparg1=NULL;
	jint rc = 0;

	if (arg1) if ((lparg1 = getCGSizeFields(env, arg1, &_arg1)) == NULL) goto fail;
	rc = (jint) AXValueGetValue((AXValueRef)arg0, kAXValueCGSizeType, (void *) lparg1);

fail:
	if (arg1 && lparg1) setCGSizeFields(env, arg1, lparg1);
	
	return rc;
}
#endif

JNIEXPORT jboolean JNICALL OS_NATIVE(AXAPIEnabled)
	(JNIEnv *env, jclass that)
{
	Boolean result = AXAPIEnabled();
	return (jboolean) result;
}	