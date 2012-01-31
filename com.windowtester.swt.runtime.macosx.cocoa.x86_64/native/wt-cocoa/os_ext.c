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
#include "os_ext.h"
#include "os_structs.h"

#ifdef __LP64__
#define JHANDLE jlong
#define OS_NATIVE(func) Java_com_windowtester_swt_macosx_cocoa_MacCocoa64_##func
#else
#define JHANDLE jint
#define OS_NATIVE(func) Java_com_windowtester_swt_macosx_cocoa_MacCocoa32_##func
#endif

AXUIElementRef AXUIElementCreateWithHIObjectAndIdentifier (HIObjectRef inHIObject, UInt64 inIdentifier);

JNIEXPORT jint JNICALL OS_NATIVE(AXUIElementCopyAttributeValue)
	(JNIEnv *env, jclass that, JHANDLE arg0, JHANDLE arg1, jlongArray arg2)
{
	jlong *lparg2=NULL;
	jint rc = 0;

	if (arg2) if ((lparg2 = (*env)->GetLongArrayElements(env, arg2, NULL)) == NULL) goto fail;

	rc = (jint)AXUIElementCopyAttributeValue((AXUIElementRef)arg0, (CFStringRef)arg1, (CFTypeRef *) lparg2 );

fail:
	if (arg2 && lparg2) (*env)->ReleaseLongArrayElements(env, arg2, lparg2, 0);

	return rc;
}

JNIEXPORT jint JNICALL OS_NATIVE(AXValueGetValueCGPoint)
	(JNIEnv *env, jclass that, JHANDLE arg0, jobject arg1)
{
	CGPoint _arg1, *lparg1=NULL;
	jint rc = 0;

	if (arg1) if ((lparg1 = getCGPointFields(env, arg1, &_arg1)) == NULL) goto fail;
	rc = (jint) AXValueGetValue((AXValueRef)arg0, kAXValueCGPointType, (void *) lparg1);

fail:
	if (arg1 && lparg1) setCGPointFields(env, arg1, lparg1);
	
	return rc;
}

JNIEXPORT jint JNICALL OS_NATIVE(AXValueGetValueCGSize)
	(JNIEnv *env, jclass that, JHANDLE arg0, jobject arg1)
{
	CGSize _arg1, *lparg1=NULL;
	jint rc = 0;

	if (arg1) if ((lparg1 = getCGSizeFields(env, arg1, &_arg1)) == NULL) goto fail;
	rc = (jint) AXValueGetValue((AXValueRef)arg0, kAXValueCGSizeType, (void *) lparg1);

fail:
	if (arg1 && lparg1) setCGSizeFields(env, arg1, lparg1);
	
	return rc;
}

JNIEXPORT jboolean JNICALL OS_NATIVE(AXAPIEnabled)
	(JNIEnv *env, jclass that)
{
	Boolean result = AXAPIEnabled();
	return (jboolean) result;
}


JNIEXPORT JHANDLE JNICALL OS_NATIVE(AXUIElementCreateWithHIObjectAndIdentifier)
(JNIEnv *env, jclass that, JHANDLE arg0, jlong arg1)
{
	JHANDLE rc = 0;
	rc = (JHANDLE)AXUIElementCreateWithHIObjectAndIdentifier((HIObjectRef)arg0, (UInt64)arg1);
	return rc;
}

JNIEXPORT JHANDLE JNICALL OS_NATIVE(CFStringCreateWithCharacters__I_3CJ)
(JNIEnv *env, jclass that, jint arg0, jcharArray arg1, jlong arg2)
{
	jchar *lparg1=NULL;
	JHANDLE rc = 0;
	if (arg1) if ((lparg1 = (*env)->GetCharArrayElements(env, arg1, NULL)) == NULL) goto fail;
	rc = (JHANDLE)CFStringCreateWithCharacters((CFAllocatorRef)NULL, (const UniChar *)lparg1, (CFIndex)arg2);
fail:
	if (arg1 && lparg1) (*env)->ReleaseCharArrayElements(env, arg1, lparg1, 0);
	return rc;
}

JNIEXPORT JHANDLE JNICALL OS_NATIVE(CFArrayGetValueAtIndex)
(JNIEnv *env, jclass that, JHANDLE arg0, jlong arg1)
{
	JHANDLE rc = 0;
	rc = (JHANDLE)CFArrayGetValueAtIndex((CFArrayRef)arg0, arg1);
	return rc;
}
