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
package com.windowtester.runtime.gef.internal.experimental.factory;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.internal.matchers.CompositeMatcher;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.swt.internal.Context;

public class Factory {
	

	static abstract class AbstractSubject {
		
		protected final IFigureMatcher matcher;
		
		protected AbstractSubject(IFigureMatcher matcher) {
			this.matcher = matcher;
		}

		protected IFigureFact composeWithThis(IFigureMatcher matcher) {
			return new FigureFact(new CompositeMatcher(this.matcher, matcher));
		}
	}
	
	
	static class FigureSubject extends AbstractSubject implements IFigureSubject {
		
		static class YesMatcher implements IFigureMatcher {
			public boolean matches(IFigureReference figureRef) {
				return true;
			}
		}
		
		public FigureSubject(IFigureMatcher matcher) {
			super(matcher);
		}
		
		public FigureSubject() {
			this(new YesMatcher());
		}

		public IFigureFact hasClass(String className) {
			return composeWithThis(new ByClassNameFigureMatcher(className));
		}
		
		public IFigureFact hasProperty(String propertyName, String propertyValue) {
			return composeWithThis(new ByStringPropertyFigureMatcher(propertyName, propertyValue));
		}
		
		public IFigureFact hasName(String name) {
			return composeWithThis(new ByStringPropertyFigureMatcher("name", name));
		}
		
		public IEditPartSubject editPart() {
			return new EditPartSubject(this.matcher);
		}
		public IModelObjectSubject modelObject() {
			// TODO Auto-generated method stub
			return null;
		}
		

		
	}
	
	static class FigureFact extends FigureLocator implements IFigureFact {

		private static final long serialVersionUID = 1L;

		private final IFigureMatcher matcher;

		public FigureFact(IFigureMatcher matcher) {
			super(matcher);
			this.matcher = matcher;
		}

		public IFigureSubject and() {
			return new FigureSubject(matcher);
		}
	}
	
	static class EditPartSubject extends AbstractSubject implements IEditPartSubject {

		public EditPartSubject(IFigureMatcher matcher) {
			super(matcher);
		}
		public IFigureFact hasClass(String className) {
			return composeWithThis(new ByEditPartClassFigureMatcher(className));
		}
		public IFigureFact hasProperty(String propertyName, String propertyValue) {
			return composeWithThis(new ByEditPartStringPropertyFigureMatcher(propertyName, propertyValue));
		}
	}
	
	
	static class ModelObjectSubject extends AbstractSubject implements IEditPartSubject {

		public ModelObjectSubject(IFigureMatcher matcher) {
			super(matcher);
		}
		public IFigureFact hasClass(String className) {
			return composeWithThis(new ByEditPartClassFigureMatcher(className));
		}
		public IFigureFact hasProperty(String propertyName, String propertyValue) {
			return composeWithThis(new ByEditPartStringPropertyFigureMatcher(propertyName, propertyValue));
		}
	}
	
	
	public static IFigureSubject figure() {
		return new FigureSubject();
	}
	
	
	
	public static void main(String[] args) throws WidgetSearchException {
		IUIContext ui = Context.GLOBAL.getUI();
		
		//samples
		
		ui.click(figure().hasClass("Foo").and().editPart().hasClass("Bar"));
		ui.click(figure().hasProperty("id", "qwerty"));
		ui.click(figure().modelObject().hasProperty("id", "qwerty"));
		
		ui.click(figure().hasName("my.named.figure"));
				
	}
}