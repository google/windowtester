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
//
//  wtjni.h
//  wt-cocoa
//
//

#include <JavaVM/jni.h>

#import <Carbon/Carbon.h>
#import <Cocoa/Cocoa.h>
#import <objc/objc-runtime.h>

#ifndef __WTJNI_H_
#define __WTJNI_H_

#ifdef __LP64__
#define JHANDLE jlong
#define OS_NATIVE(func) Java_com_windowtester_swt_macosx_cocoa_MacCocoa64_##func
#else
#define JHANDLE jint
#define OS_NATIVE(func) Java_com_windowtester_swt_macosx_cocoa_MacCocoa32_##func
#endif

// dynamic functions
OSStatus (*ChangeMenuItemAttributes_)(MenuRef, MenuItemIndex, MenuItemAttributes, MenuItemAttributes);
EventTargetRef (*GetMenuEventTarget_)(MenuRef);
MenuRef (*NSGetCarbonMenu_)(NSMenu*);
OSStatus (*CancelMenuTracking_)(MenuRef, Boolean, UInt32);
#ifdef InstallMenuEventHandler
#undef InstallMenuEventHandler
#endif
#define InstallMenuEventHandler( target, handler, numTypes, list, userData, outHandlerRef ) \
InstallEventHandler( GetMenuEventTarget_( target ), (handler), (numTypes), (list), (userData), (outHandlerRef) )

#endif