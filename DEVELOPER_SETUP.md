#Setting up a development environment for WindowTester

#####WindowTester currently supports Eclipse versions 3.4 - 3.8.x on the following platforms:  

Windows 32 bit  
Windows 64 bit  
Linux GTK 32 bit  
Linux Motif 32 bit  
Mac OS Cocoa 32 bit  
Mac OS Cocoa 64 bit  
Unfortunately Eclipse 4.x is not yet supported, as well as Linux GTK 64 bit.

####1. Eclipse

Download Eclipse from http://www.eclipse.org
Eclipse 3.8.x is recommended

####2. Eclipse delta pack

Download the Eclipse delta pack corresponding to the Eclipse version you want to use
Extract the delta pack to `ECLIPSE_HOME/delta-pack/eclipse/plugins` (ECLIPSE_HOME is the root dir of your Eclipse installation)

####3. SVN support

Install a SVN client plugin
[Subclipse](http://subclipse.tigris.org/) or
[Subversive](http://eclipse.org/subversive/)

####4. WindowTester

`git clone https://github.com/google/windowtester.git` 

Not all plugins are needed for developing WindowTester, eg. the following plugins are either unfinished, deprecated or abandoned projects:  
com.windowtester.pro_buildjavadoc  
com.windowtester.runtime.junit4  
com.windowtester.runtime.junit4_test  
com.windowtester.runtime.legacy  
com.windowtester.server.core  
com.windowtester.server.core_feature  
com.windowtester.server.ui  
com.windowtester.server.ui_feature  
com.windowtester.swt.runtime.legacy

The following plugins contain only example projects:  
com.windowtester.example.contactmanager.rcp  
com.windowtester.example.contactmanager.rcp_test_feature  
com.windowtester.example.contactmanager.rcp_test  
com.windowtester.example.contactmanager.swing  
com.windowtester.example.contactmanager.swing_test  

Depending on your platform you can remove or close the other platform specific plugins
eg. if you want to develop on a Linux 32 bit platform, you can close (or remove) the following plugins:  
com.windowtester.swt.macosx  
com.windowtester.swt.macosx.cocoa  
com.windowtester.swt.runtime.linux.motif.x86  
com.windowtester.swt.runtime.macosx.carbon.x86  
com.windowtester.swt.runtime.macosx.cocoa  
com.windowtester.swt.runtime.macosx.cocoa.x86  
com.windowtester.swt.runtime.macosx.cocoa.x86_64  
com.windowtester.swt.runtime.win32.win32.x86  
com.windowtester.swt.runtime.win32.win32.x86_64  

####5. Compile errors

_Missing API baseline_
set to Warning instead of Error in preferences:
Window -> Preferences -> Plug-in Development -> API Baselines -> Missing API baseline

Apply the patch for compile errors
`FixedCompileErrors_v1.0.patch` from the downloads directory
