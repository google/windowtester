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
package com.windowtester.eclipse.ui.convert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;

public abstract class WTAPIAbstractVisitor extends ASTVisitor
	implements WTConvertAPIRule
{
	protected WTConvertAPIContext context;

	public WTAPIAbstractVisitor(WTConvertAPIContext context) {
		this.context = context;
	}

	public final void convert(WTConvertAPIContext context) {
		this.context = context;
		context.accept(this);
	}

	//=============================================================================
	// Fields and Local Variables

	/**
	 * A mechanism for caching names of fields and local variables that can be referenced
	 * by the local node being visited.
	 */
	private class VarScope
	{
		private final VarScope parent;
		private Map<String, String> varTypeMap = new HashMap<String, String>();
		private Map<String, Expression> varValueMap = new HashMap<String, Expression>();
		private Map<ASTNode, String> nodeTypeMap = new HashMap<ASTNode, String>();
		private Map<ASTNode, Expression> nodeValueMap = new HashMap<ASTNode, Expression>();

		VarScope(VarScope parent) {
			this.parent = parent;
		}

		VarScope getParent() {
			return parent;
		}

		void setVarType(String name, String typeName) {
			varTypeMap.put(name, typeName);
		}

		String getVarType(String name) {
			String typeName = varTypeMap.get(name);
			if (typeName == null && parent != null)
				typeName = parent.getVarType(name);
			return typeName;
		}

		void setVarValue(String name, Expression value) {
			varValueMap.put(name, value);
		}

		Expression getVarValue(String name) {
			Expression value = varValueMap.get(name);
			if (value == null && parent != null)
				value = parent.getVarValue(name);
			return value;
		}

		void setNodeType(ASTNode node, String typeName) {
			nodeTypeMap.put(node, typeName);
		}

		String getNodeType(ASTNode node) {
			String typeName = nodeTypeMap.get(node);
			if (typeName == null && parent != null)
				typeName = parent.getNodeType(node);
			return typeName;
		}

		void setNodeValue(ASTNode node, Expression value) {
			nodeValueMap.put(node, value);
		}

		Expression getNodeValue(ASTNode node) {
			Expression value = nodeValueMap.get(node);
			if (value == null && parent != null)
				value = parent.getNodeValue(node);
			return value;
		}
	}

	/**
	 * The current variable scope
	 */
	private VarScope currentScope;

	/**
	 * Answer the type name for the specified variable or <code>null</code> if it is
	 * unknown
	 */
	public String getVarType(String name) {
		return currentScope.getVarType(name);
	}

	/**
	 * Answer the current "value" for the specified variable as an expression or <code>null</code> if it is
	 * unknown. This expression is most likely already part of the AST tree, and thus must be copied before
	 * being used elsewhere.
	 */
	public Expression getVarValue(String name) {
		return currentScope.getVarValue(name);
	}

	/**
	 * Answer the expression type name of the AST node last visited or <code>null</code>
	 * if unknown or if the last AST node was not an expression
	 */
	public String getNodeType(ASTNode node) {
		return currentScope.getNodeType(node);
	}

	/**
	 * Answer the "value" of the expression in terms of a snippet of code such as "true",
	 * "0", "new CTabLocator(...)" or <code>null</code> if unknown
	 */
	public Expression getNodeValue(ASTNode node) {
		return currentScope.getNodeValue(node);
	}

	//=============================================================================
	// Visitor
	
	public void endVisit(ArrayType node) {
		String typeName = getNodeType(node.getComponentType());
		if (typeName == null)
			return;
		currentScope.setNodeType(node, typeName + "[]");
	}

	/**
	 * Visits the assignment to determine its "value"
	 */
	public void endVisit(Assignment node) {
		Expression expression = node.getLeftHandSide();
		if (expression == null)
			return;
		String varName;
		switch (expression.getNodeType()) {
			case ASTNode.SIMPLE_NAME :
				varName = ((SimpleName) expression).getFullyQualifiedName();
				break;
			default :
				return;
		}
		expression = node.getRightHandSide();
		if (expression == null)
			return;
		Expression value = getNodeValue(expression);
		if (value == null)
			return;
		currentScope.setVarValue(varName, value);
	}

	/**
	 * Initialize the variable scope before visiting nodes
	 */
	@SuppressWarnings("unchecked")
	public boolean visit(Block node) {
		currentScope = new VarScope(currentScope);
		ASTNode parent = node.getParent();
		if (parent instanceof MethodDeclaration) {
			List<SingleVariableDeclaration> parameters = ((MethodDeclaration) parent).parameters();
			for (SingleVariableDeclaration param : parameters) {
				Type type = param.getType();
				String unresolvedTypeName;
				if (type.isSimpleType())
					unresolvedTypeName = ((SimpleType) type).getName().getFullyQualifiedName();
				else if (type.isQualifiedType())
					unresolvedTypeName = ((QualifiedType) type).getName().getFullyQualifiedName();
				else
					unresolvedTypeName = null;
				String typeName = context.resolve(unresolvedTypeName);
				if (typeName == null)
					typeName = unresolvedTypeName;
				String varName = param.getName().getFullyQualifiedName();
				currentScope.setVarType(varName, typeName);
			}
		}
		return true;
	}

	/**
	 * Clear the variable scope after visiting nodes
	 */
	public void endVisit(Block node) {
		currentScope = currentScope.getParent();
	}

	/**
	 * Cache the expression information for this boolean literal
	 */
	public void endVisit(BooleanLiteral node) {
		currentScope.setNodeType(node, Boolean.TYPE.getName());
		currentScope.setNodeValue(node, node);
	}

	/**
	 * Cache the expression information for this cast expression.
	 */
	public void endVisit(CastExpression node) {
		currentScope.setNodeType(node, getNodeType(node.getType()));
		currentScope.setNodeValue(node, getNodeValue(node.getExpression()));
	}

	/**
	 * Visit the class instance creation and determine its expression type. Call
	 * {@link #visit(ClassInstanceCreation, String, String[])} with the type information
	 * found.
	 */
	public void endVisit(ClassInstanceCreation node) {
		currentScope.setNodeType(node, getNodeType(node.getType()));
		currentScope.setNodeValue(node, node);
	}

	/**
	 * Initialize the variable scope before visiting nodes
	 */
	public boolean visit(CompilationUnit node) {
		currentScope = new VarScope(null);
		currentScope.setNodeType(null, null);
		currentScope.setNodeValue(null, null);
		return true;
	}

	/**
	 * Clear the variable scope after visiting nodes
	 */
	public void endVisit(CompilationUnit node) {
		currentScope = null;
	}

	/**
	 * Visit the field declaration and determine its expression type
	 */
	public void endVisit(FieldDeclaration node) {
		String typeName = getNodeType(node.getType());
		currentScope.setNodeType(node, typeName);
		currentScope.setNodeValue(node, null);
		for (Object each : node.fragments()) {
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) each;
			String name = fragment.getName().getFullyQualifiedName();
			currentScope.setVarType(name, typeName);
		}
	}

	/**
	 * Visit the method invocation and determine its expression type. Call
	 * {@link #visit(MethodInvocation, String, String[])} with the type information found.
	 */
	@SuppressWarnings("unchecked")
	public void endVisit(MethodInvocation node) {
		List<ASTNode> arguments = node.arguments();

		// TODO determine the return type and "value" of known WindowTester methods
		String returnTypeName = null;
		Expression returnValue = null;
		
		// Special case - return type for getUI()
		String methodName = node.getName().getFullyQualifiedName();
		if (methodName.equals("getUI") && arguments.size() == 0) {
			returnTypeName = IUIContext.class.getName();
		}
		
		// Return type determination like this could be generalized
		else if (methodName.equals("findAll") && arguments.size() == 1
			&& IUIContext.class.getName().equals(getNodeType(node.getExpression()))) {
			returnTypeName = IWidgetLocator.class.getName() + "[]";
			returnValue = getNodeValue(arguments.get(0));
		}
		
		currentScope.setNodeType(node, returnTypeName);
		currentScope.setNodeValue(node, returnValue);
	}

	/**
	 * Cache the expression information for this numeric literal
	 */
	public void endVisit(NumberLiteral node) {
		currentScope.setNodeType(node, Number.class.getName());
		currentScope.setNodeValue(node, node);
	}

	/**
	 * Cache the expression information for this parenthesized expression
	 */
	public void endVisit(ParenthesizedExpression node) {
		Expression expression = node.getExpression();
		currentScope.setNodeType(node, getNodeType(expression));
		currentScope.setNodeValue(node, getNodeValue(expression));
	}

	/**
	 * If the node represents a WindowTester type, then cache the expression information
	 * for the parent node.
	 */
	public void endVisit(QualifiedName node) {
		String typeName = context.resolve(node.getFullyQualifiedName());
		if (typeName != null) {
			currentScope.setNodeType(node, typeName);
			currentScope.setNodeValue(node, null);
		}
	}

	/**
	 * If the node represents a WindowTester type, then cache the expression information
	 * for the parent node.
	 */
	public void endVisit(QualifiedType node) {
		String unresolvedName = node.toString();
		String typeName = context.resolve(unresolvedName);
		currentScope.setNodeType(node, (typeName != null ? typeName : unresolvedName));
	}

	/**
	 * If the node represents a WindowTester type, then cache the expression information
	 * for the parent node.
	 */
	public void endVisit(SimpleName node) {
		String name = node.getFullyQualifiedName();
		currentScope.setNodeType(node, currentScope.getVarType(name));
		currentScope.setNodeValue(node, currentScope.getVarValue(name));
	}

	/**
	 * If the node represents a WindowTester type, then cache the expression information
	 * for the parent node.
	 */
	public void endVisit(SimpleType node) {
		String unresolvedName = node.getName().getFullyQualifiedName();
		String typeName = context.resolve(unresolvedName);
		currentScope.setNodeType(node, (typeName != null ? typeName : unresolvedName));
	}

	/**
	 * Cache the expression information for this string literal
	 */
	public void endVisit(StringLiteral node) {
		currentScope.setNodeType(node, String.class.getName());
		currentScope.setNodeValue(node, node);
	}

	/**
	 * Cache the expression information
	 */
	public void endVisit(ThisExpression node) {
		String typeName = null;
		Name qualifier = node.getQualifier();
		if (qualifier != null)
			typeName = context.resolve(qualifier.getFullyQualifiedName());
		currentScope.setNodeType(node, typeName);
		currentScope.setNodeValue(node, node);
	}

	/**
	 * Set the variable scope and then visit field then method nodes. Visit all the fields
	 * before visiting the methods to cache the field type information accessible by the
	 * methods.
	 */
	public boolean visit(TypeDeclaration node) {
		Type superType = node.getSuperclassType();
		if (superType != null)
			superType.accept(this);
		for (Object each : node.superInterfaceTypes())
			((Type) each).accept(this);
		currentScope = new VarScope(currentScope);
		FieldDeclaration[] fields = node.getFields();
		for (int i = 0; i < fields.length; i++)
			fields[i].accept(this);
		TypeDeclaration[] types = node.getTypes();
		for (int i = 0; i < types.length; i++)
			types[i].accept(this);
		MethodDeclaration[] methods = node.getMethods();
		for (int i = 0; i < methods.length; i++)
			methods[i].accept(this);
		currentScope = currentScope.getParent();
		return false;
	}

	/**
	 * Cache the expression information for this type literal.
	 */
	public void endVisit(TypeLiteral node) {
		String typeName = getNodeType(node.getType());
		if (typeName != null)
			typeName += ".class";
		currentScope.setNodeType(node, typeName);
		currentScope.setNodeValue(node, null);
	}

	/**
	 * Visits the variable initializer to determine its "value"
	 */
	public void endVisit(VariableDeclarationFragment node) {
		Expression initializer = node.getInitializer();
		if (initializer != null) {
			String name = node.getName().getFullyQualifiedName();
			currentScope.setVarValue(name, getNodeValue(initializer));
		}
	}

	/**
	 * If this node represents a variable declaration of a WindowTester type then call
	 * {@link #visitWT(VariableDeclarationStatement, String, boolean)}
	 */
	public void endVisit(VariableDeclarationStatement node) {
		Type type = node.getType();
		String typeName = getNodeType(type);
		for (Object each : node.fragments()) {
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) each;
			String name = fragment.getName().getFullyQualifiedName();
			currentScope.setVarType(name, typeName);
		}
		currentScope.setNodeType(node, typeName);
		currentScope.setNodeValue(node, null);
	}
}