# Setting up a development environment for WindowTester #

WindowTester currently supports Eclipse versions 3.4 - 3.8.x on the following platforms:
  * Windows 32 bit
  * Windows 64 bit
  * Linux GTK 32 bit
  * Linux Motif 32 bit
  * Mac OS Cocoa 32 bit
  * Mac OS Cocoa 64 bit

Unfortunately Eclipse 4.x is not yet supported, as well as Linux GTK 64 bit.

## 1. Eclipse ##
  * Download Eclipse from http://www.eclipse.org
  * Eclipse 3.8.x is recommended
## 2. Eclipse delta pack ##
  * Download the Eclipse delta pack corresponding to the Eclipse version you want to use
  * Extract the delta pack to ECLIPSE\_HOME/delta-pack/eclipse/plugins (ECLIPSE\_HOME is the root dir of your Eclipse installation)
## 3. SVN support ##
  * Install a SVN client plugin
    * Subclipse (http://subclipse.tigris.org/) or
    * Subversive (eg. from the Juno update site http://download.eclipse.org/releases/juno)
## 4. WindowTester ##
  * You can use the provided team project set to import all necessary plugins
    * http://code.google.com/p/windowtester/downloads/detail?name=WindowTester_dev_withoutExamples_v1.0.psf
    * File -> Import... -> Team -> Team Project Set
  * Or checkout the WindowTester plugins manually from http://windowtester.googlecode.com/svn/trunk
  * Not all plugins are needed for developing WindowTester, eg. the following plugins are either unfinished, deprecated or abandoned projects:
    * com.windowtester.pro\_buildjavadoc
    * com.windowtester.runtime.junit4
    * com.windowtester.runtime.junit4\_test
    * com.windowtester.runtime.legacy
    * com.windowtester.server.core
    * com.windowtester.server.core\_feature
    * com.windowtester.server.ui
    * com.windowtester.server.ui\_feature
    * com.windowtester.swt.runtime.legacy
  * The following plugins contain only example projects:
    * com.windowtester.example.contactmanager.rcp
    * com.windowtester.example.contactmanager.rcp\_test\_feature
    * com.windowtester.example.contactmanager.rcp\_test
    * com.windowtester.example.contactmanager.swing
    * com.windowtester.example.contactmanager.swing\_test
  * Depending on your platform you can remove or close the other platform specific plugins
    * eg. if you want to develop on a Linux 32 bit platform, you can close (or remove) the following plugins:
      * com.windowtester.swt.macosx
      * com.windowtester.swt.macosx.cocoa
      * com.windowtester.swt.runtime.linux.motif.x86
      * com.windowtester.swt.runtime.macosx.carbon.x86
      * com.windowtester.swt.runtime.macosx.cocoa
      * com.windowtester.swt.runtime.macosx.cocoa.x86
      * com.windowtester.swt.runtime.macosx.cocoa.x86\_64
      * com.windowtester.swt.runtime.win32.win32.x86
      * com.windowtester.swt.runtime.win32.win32.x86\_64
## 5. Compile errors ##
  * Missing API baseline
    * set to Warning instead of Error in preferences:
      * Window -> Preferences -> Plug-in Development -> API Baselines -> Missing  API baseline
  * Apply patch for compile errors
    * http://code.google.com/p/windowtester/downloads/detail?name=FixedCompileErrors_v1.0.patch