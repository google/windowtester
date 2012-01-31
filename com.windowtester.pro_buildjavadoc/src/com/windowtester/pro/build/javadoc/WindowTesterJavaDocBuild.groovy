/**
 * 
 */
package com.windowtester.pro.build.javadoc

import com.instantiations.pde.build.JavaDocBuild;

/**
 * @author markr
 *
 */
public class WindowTesterJavaDocBuild extends JavaDocBuild {

	public static void main(String[] args) {
		new WindowTesterJavaDocBuild().build();
	}
	
	public List<String> getSubproducts() {
		return['WindowTesterRuntime'];
	}
}
