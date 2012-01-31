package experimental;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.UITestCaseSWT;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class LiterateLocatorSpike extends UITestCaseSWT {

	
	public void testWontRun() throws Exception {
		
		IUIContext ui = getUI();
		ui.click(figure().hasText("Foo").part().hasClass("com.acme.MyEditPart").object().hasProperty("id", 13));
		ui.click(figure().object().hasProperty("id", 13).and().hasClass("MyModelClass"));
		ui.click(figure().part().hasClass("MyEditPart"));
		
		ui.click(figure().part().hasClass("MyEditPart").in().editor("Foo.model"));
		
		
	}
	
	
	static interface ILocatorBuilder extends ILocator {
	
	}

	static interface IFigureWithTextLocatorBuilder extends ILocatorBuilder {
		IObjectLocatorBuilder object();
		IPartLocatorBuilder part();
	}
		
		
	static interface IFigureLocatorBuilder extends IFigureWithTextLocatorBuilder {
		IFigureWithTextLocatorBuilder hasText(String textOrPattern);
	}
	
	static interface IPropertyMatcherBuilder extends ILocatorBuilder {
		
		IObjectLocatorBuilder and();
	}
	
	static interface IPropertyMatcherBuilder2 {	
		IPropertyMatcherBuilder hasProperty(String propertyName, Object value);
	}
	
	static interface IObjectLocatorBuilder  {
		IPropertyMatcherBuilder hasProperty(String propertyName, Object value);
		IPropertyMatcherBuilder hasClass(String className);
		IPropertyMatcherBuilder hasClass(Class<?> cls);
		
	}

	static interface IScopeLocatorBuilder extends ILocatorBuilder {
		ILocatorBuilder view(String name);
		ILocatorBuilder editor(String name);
	}
	
	static interface IPartWithClassLocatorBuilder extends ILocatorBuilder {
		IObjectLocatorBuilder object();
		IScopeLocatorBuilder in();
	}
	
	static interface IPartLocatorBuilder extends IPartWithClassLocatorBuilder {
		IPartWithClassLocatorBuilder hasClass(String className);
		IPartWithClassLocatorBuilder hasClass(Class<?> cls);
	}
	
	
	static IFigureLocatorBuilder figure() {
		return null;
	}
	
	
}
