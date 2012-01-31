//
//

//#include <stdio.h>
#import "wtjni.h"

JNIEXPORT jint JNICALL OS_NATIVE(_1getMenuBarHeight)
(JNIEnv * env, jobject this)
{	
	NSMenu *mainMenu = [NSApp mainMenu];
	return (jint)[mainMenu menuBarHeight];
}

JNIEXPORT JHANDLE JNICALL OS_NATIVE(getCarbonMenuHandle)
(JNIEnv *env, jclass that, JHANDLE menu)
{
	JHANDLE rc = 0;
	CFBundleRef cocoaBundle = CFBundleGetBundleWithIdentifier(CFSTR("com.apple.Cocoa"));
	if (cocoaBundle) {
		NSGetCarbonMenu_ = CFBundleGetFunctionPointerForName(cocoaBundle, CFSTR("_NSGetCarbonMenu"));
	}
	rc = (JHANDLE) NSGetCarbonMenu_((NSMenu*)menu);
	return rc;
}