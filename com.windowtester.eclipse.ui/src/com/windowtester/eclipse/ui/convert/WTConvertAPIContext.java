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

import static com.windowtester.eclipse.ui.convert.util.WTAPIUtil.simpleTypeName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;

/**
 * The source, AST parse tree, and type information for the compilation unit being
 * modified. Use {@link WTConvertAPIContextBuilder} to build this context.
 * <ul>
 * <li>When calling discrete methods such as {@link ImportDeclaration#setName(Name)}, call
 * {@link #replacing(ASTNode, ASTNode)} before calling the discrete method.</li>
 * <li>When inserting one or more nodes into a list such as the list returned by
 * {@link CompilationUnit#imports()}, call {@link #insert(List, int, ASTNode)} or
 * {@link #insert(List, int, Collection)} rather than modifying the list directly</li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 * Because imports affect the types visible to the rest of the compilation unit, they have
 * their own set of special methods that should be called rather than calling the methods
 * listed above.
 * <ul>
 * <li>When adding a new import, call {@link #addImport(String, boolean)} rather than
 * calling {@link #insert(List, int, ASTNode)}</li>
 * <li>When replacing an existing import with a new import, call
 * {@link #replaceImport(ImportDeclaration, String)} rather than calling
 * {@link #replacing(ASTNode, ASTNode)} and {@link ImportDeclaration#setName(Name)}</li>
 * <li>When removing an import, call {@link #removeImport(ImportDeclaration)} rather than
 * calling {@link #remove(List, ASTNode)}</li>
 * <li></li>
 * <li></li>
 * </ul>
 */
public class WTConvertAPIContext
{
	private String source;
	private final String originalSource;
	private final CompilationUnit compUnit;

	/**
	 * A collection of fully qualified names of WindowTester types imported in this
	 * compilation unit
	 */
	private final Set<String> wtTypeNames;

	/**
	 * A map of simple name to fully qualified name of WindowTester types imported in this
	 * compilation unit
	 */
	private final Map<String, String> wtSimpleTypeNameMap;

	/**
	 * A collection of fully qualified names of WindowTester type members statically
	 * imported in this compilation unit
	 */
	private final Set<String> wtStaticTypeNames;

	/**
	 * A map of simple name to fully qualified name of WindowTester types members
	 * statically imported in this compilation unit
	 */
	private final HashMap<String, String> wtSimpleStaticTypeNameMap;

	/**
	 * A collection of names of types to be removed at the end of the
	 * {@link #accept(ASTVisitor)} processing.
	 */
	private final Collection<String> wtTypeNamesToRemove;

	/**
	 * A collection of fully qualified names of common types (e.g. types in the java.lang
	 * package)
	 */
	private static final Set<String> COMMON_TYPE_NAMES = new HashSet<String>();

	/**
	 * A mapping of simple type name to fully qualified type name for common types that
	 * are not WindowTester types (e.g. types in the java.lang package). This mapping is
	 * statically initialized and also fed by types referenced in various rules.
	 * 
	 * @see #addCommonType(Class)
	 */
	private static final Map<String, String> COMMON_SIMPLE_TYPE_NAME_MAP = new HashMap<String, String>();

	/**
	 * Initialize the common type static fields
	 */
	static {
		// TODO add additional common types
		addCommonType(Integer.class);
		addCommonType(Integer.TYPE);
		addCommonType(Object.class);
		addCommonType(String.class);
	}

	/**
	 * Add a common type that is not a WindowTester type (e.g. java.lang.Object,
	 * org.eclipse.swt.widgets.Shell) to the list of known types for use during WT API
	 * migration.
	 * 
	 * @param type the type (not <code>null</code>)
	 */
	public static void addCommonType(Class<?> type) {
		COMMON_TYPE_NAMES.add(type.getName());
		COMMON_SIMPLE_TYPE_NAME_MAP.put(type.getSimpleName(), type.getName());
	}

	/**
	 * Construct a new instance for traversing and manipulating the compilation unit
	 * 
	 * @param source the compilation unit source (not <code>null</code>)
	 * @param compUnit the compilation unit (not <code>null</code>)
	 * @param wtTypeNames the WindowTester types imported by this compilation unit (not
	 *            <code>null</code>)
	 * @param wtStaticTypeNames the WindowTester static members imported by this
	 *            compilation unit (not <code>null</code>)
	 */
	public WTConvertAPIContext(String source, CompilationUnit compUnit, Set<String> wtTypeNames,
		Set<String> wtStaticTypeNames) {
		this.source = source;
		this.originalSource = source;
		this.compUnit = compUnit;

		this.wtTypeNames = wtTypeNames;
		wtSimpleTypeNameMap = new HashMap<String, String>();
		for (String typeName : wtTypeNames)
			wtSimpleTypeNameMap.put(simpleTypeName(typeName), typeName);

		this.wtStaticTypeNames = wtStaticTypeNames;
		wtSimpleStaticTypeNameMap = new HashMap<String, String>();
		for (String typeName : wtStaticTypeNames)
			wtSimpleStaticTypeNameMap.put(simpleTypeName(typeName), typeName);

		wtTypeNamesToRemove = new ArrayList<String>();
	}

	//===========================================================
	// Accessors

	/**
	 * Answer <code>true</code> if the source has been modified
	 */
	public boolean isSourceModified() {
		return !source.equals(originalSource);
	}

	/**
	 * Answer the modified source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Answer the modified source for the specified node
	 * 
	 * @param node the node (must be part of the receiver)
	 * @return the source
	 */
	public String getSourceFor(ASTNode node) {
		int start = node.getStartPosition();
		return source.substring(start, start + node.getLength());
	}

	/**
	 * Answer the AST parse tree
	 */
	public CompilationUnit getCompilationUnit() {
		return compUnit;
	}

	/**
	 * Answer true if the receiver's compilation unit references WindowTester types
	 */
	public boolean hasWTReferences() {
		return wtTypeNames.size() > 0 || wtStaticTypeNames.size() > 0;
	}

	/**
	 * Answer the names of the imported types
	 */
	public Set<String> getWTTypeNames() {
		return wtTypeNames;
	}

	/**
	 * Answer the fully qualified names of the WindowTester types in the specified package
	 * or <code>null</code> if the specified package is not part of the WindowTester
	 * product
	 */
	public Collection<String> getWTTypesInPackage(String packageName) {
		return WTConvertAPIContextBuilder.getWTTypesInPackage(packageName);
	}

	/**
	 * Answer the fully qualified type name if it can be resolved or <code>null</code> if
	 * it cannot be resolved
	 * 
	 * @param typeName the simple or fully qualified name of the type
	 * @return the fully qualified name of the type or <code>null</code> if cannot be
	 *         resolved
	 */
	public String resolve(String typeName) {
		if (typeName == null)
			return null;
		if (typeName.indexOf('.') != -1)
			return typeName;
		int index = typeName.indexOf('[');
		String suffix = "";
		if (index != -1) {
			suffix = typeName.substring(index);
			typeName = typeName.substring(0, index);
		}
		String resolvedTypeName = wtSimpleTypeNameMap.get(typeName);
		if (resolvedTypeName == null)
			resolvedTypeName = COMMON_SIMPLE_TYPE_NAME_MAP.get(typeName);
		return resolvedTypeName + suffix;
	}

	/**
	 * Determine if the specified name is the name of a WindowTester type
	 */
	public boolean isWTType(String typeName) {
		if (typeName == null)
			return false;
		if (typeName.endsWith(".class"))
			typeName = typeName.substring(0, typeName.length() - 6);
		if (typeName.indexOf('.') == -1)
			return wtSimpleTypeNameMap.containsKey(typeName);
		return WTConvertAPIContextBuilder.isWTType(typeName);
	}

	/**
	 * Traverse the compilation unit's parse tree
	 * 
	 * @param visitor the visitor (not <code>null</code>)
	 */
	public void accept(ASTVisitor visitor) {
		compUnit.accept(visitor);

		// Remove imports that were replaced using the replaceImport(...) method

		wtTypeNames.removeAll(wtTypeNamesToRemove);
		for (String typeName : wtTypeNamesToRemove)
			wtSimpleTypeNameMap.remove(simpleTypeName(typeName));
	}

	//===========================================================
	// Imports

	/**
	 * Adds an import for the given type to the beginning of the list of imports.
	 * 
	 * @param newTypeName the fully qualified type name to be imported (not
	 *            <code>null</code>, not empty)
	 * @param isStatic <code>true</code> if the import should be a static import
	 */
	@SuppressWarnings("unchecked")
	public void addImport(String newTypeName, boolean isStatic) {
		//NOTE: this assumes that there is at least one import...  TODO: fix this!

		List<ASTNode> imports = getCompilationUnit().imports();
		for (ASTNode node : imports) {
			ImportDeclaration importNode = (ImportDeclaration) node;
			String typeName = importNode.getName().getFullyQualifiedName();
			if (typeName.equals(newTypeName) && importNode.isStatic() == isStatic)
				return;
		}
		insert(compUnit, imports, 0, newImport(newTypeName, isStatic));
		addKnownType(newTypeName, isStatic);
	}

	/**
	 * Expand the specified demand import to be zero or more explicit imports
	 * 
	 * @param importNode the demand import to be expanded
	 */
	@SuppressWarnings("unchecked")
	public void expandImport(ImportDeclaration importNode) {
		assertInCompUnit(importNode);
		if (!importNode.isOnDemand())
			throw new IllegalArgumentException("import is not on demand: " + importNode);

		String packageName = importNode.getName().getFullyQualifiedName();
		Collection<String> wtTypeNames = getWTTypesInPackage(packageName);
		if (wtTypeNames == null)
			return;

		Collection<ASTNode> newNodes = new ArrayList<ASTNode>();
		for (String typeName : new TreeSet<String>(wtTypeNames))
			newNodes.add(newImport(typeName, false));

		CompilationUnit compUnit = (CompilationUnit) importNode.getParent();
		List<ASTNode> imports = compUnit.imports();
		int index = imports.indexOf(importNode);

		// Intentionally do not use the add/remove import methods
		// because we are not changing the list of visible types
		insert(compUnit, imports, index, newNodes);
		remove(imports, importNode);
	}

	/**
	 * Modify the specified import declaration to import the specified type
	 * 
	 * @param importNode the import declaration to be modified
	 * @param newTypeName the fully qualified type name to be imported (not
	 *            <code>null</code>, not empty)
	 */
	public void replaceImport(ImportDeclaration importNode, String newTypeName) {
		assertInCompUnit(importNode);

		Name oldNameNode = importNode.getName();
		String oldTypeName = oldNameNode.getFullyQualifiedName();
		Name newNameNode = newName(newTypeName, 0);

		replacing(oldNameNode, newNameNode);
		importNode.setName(newNameNode);

		removeKnownType(oldTypeName, importNode.isStatic());
		addKnownType(newTypeName, importNode.isStatic());
	}

	/**
	 * Remove the import from the current list of imports
	 * 
	 * @param importNode the import to be removed
	 */
	@SuppressWarnings("unchecked")
	public void removeImport(ImportDeclaration importNode) {
		assertInCompUnit(importNode);

		CompilationUnit cu = getCompilationUnit();
		List<ASTNode> imports = cu.imports();
		Name oldNameNode = importNode.getName();
		String oldTypeName = oldNameNode.getFullyQualifiedName();

		remove(imports, importNode);

		removeKnownType(oldTypeName, importNode.isStatic());
	}

	/**
	 * Construct a new import declaration
	 * 
	 * @param typeName the fully qualified type name (not <code>null</code>, not empty)
	 * @param isStatic <code>true</code> if the import should be a static import
	 * @return the import declaration (not <code>null</code>)
	 */
	private ImportDeclaration newImport(String typeName, boolean isStatic) {
		ImportDeclaration newImport = compUnit.getAST().newImportDeclaration();
		newImport.setStatic(isStatic);
		int start = 7; // 7 = position after the "import" keyword and one space
		if (isStatic)
			start += 7; // add length of "static" keyword and one space
		newImport.setName(newName(typeName, start));
		newImport.setSourceRange(0, newImport.toString().trim().length());
		return newImport;
	}

	/**
	 * Add the specified type to the list of known types
	 * 
	 * @param newTypeName the fully qualified type name to be added (not <code>null</code>
	 *            , not empty)
	 * @param isStatic <code>true</code> if the import should be a static import
	 */
	private void addKnownType(String newTypeName, boolean isStatic) {
		if (isStatic) {
			// TODO How do we cache statically imported types?
		}
		else {
			wtTypeNames.add(newTypeName);
			wtSimpleTypeNameMap.put(simpleTypeName(newTypeName), newTypeName);
		}
	}

	/**
	 * Cannot remove old type immediately because it may be needed by the resolve(...)
	 * method so queue the old type for removal at the end of the accept(...) method
	 * 
	 * @param oldTypeName the fully qualified type name to be removed
	 * @param isStatic <code>true</code> if the import is a static import
	 */
	private void removeKnownType(String oldTypeName, boolean isStatic) {
		if (isStatic) {
			// TODO How do we cache statically imported types?
		}
		else {
			wtTypeNamesToRemove.add(oldTypeName);
		}
	}

	//=================================================================================
	// Node modification utility methods

	/**
	 * Change the name of the type being referenced
	 * 
	 * @param type the type to be changed (not <code>null</code> and must be in the
	 *            receiver's compilation unit)
	 * @param the new type name (not <code>null</code>, not empty)
	 */
	public void setTypeName(SimpleType type, String newTypeName) {
		Name newNameNode = newName(newTypeName, 0);
		replacing(type.getName(), newNameNode);
		type.setName(newNameNode);
	}

	/**
	 * Chnage the name of the method being invoked
	 * 
	 * @param invocation the method invocation (not <code>null</code> and must be in the
	 *            receiver's compilation unit)
	 * @param newMethodName the new name of the method to be invoked (not
	 *            <code>null</code>, not empty)
	 */
	public void setMethodName(MethodInvocation invocation, String newMethodName) {
		SimpleName newName = newSimpleName(newMethodName, 0);
		replacing(invocation.getName(), newName);
		invocation.setName(newName);
	}

	//=================================================================================
	// Inserting and removing nodes in a list 

	/**
	 * Insert the new node in the specified list of nodes. WARNING! this method ASSUMES
	 * that nodeList contains at least one element
	 * 
	 * @param parent the node containing the node list
	 * @param nodeList the list of nodes (not <code>null</code> and contains no
	 *            <code>null</code>s)
	 * @param index the position at which the node should be inserted (0 <= index <=
	 *            nodeList.length())
	 * @param newNode the new node (not <code>null</code>)
	 */
	public void insert(ASTNode parent, List<ASTNode> nodeList, int index, ASTNode newNode) {
		List<ASTNode> newNodes = new ArrayList<ASTNode>();
		newNodes.add(newNode);
		insert(parent, nodeList, index, newNodes);
	}

	/**
	 * Insert the new nodes in the specified list of nodes. WARNING! this method ASSUMES
	 * that nodeList contains at least one element because otherwise we cannot determine a
	 * starting position.
	 * 
	 * @param parent the node containing the node list
	 * @param nodeList the list of nodes (not <code>null</code> and contains no
	 *            <code>null</code>s)
	 * @param index the position at which the nodes should be inserted (0 <= index <=
	 *            nodeList.length())
	 * @param newNodes the new nodes (not <code>null</code> and contains no
	 *            <code>null</code> s)
	 */
	public void insert(ASTNode parent, List<ASTNode> nodeList, int index, Collection<ASTNode> newNodes) {
		assertInCompUnit(parent);

		// Find the starting position in the source if possible

		int start;
		if (nodeList.size() == 0) {
			if (parent instanceof MethodInvocation) {
				MethodInvocation invocation = (MethodInvocation) parent;
				SimpleName methodName = invocation.getName();
				start = methodName.getStartPosition() + methodName.getLength();
				while (source.charAt(start) != '(')
					start++;
				start++;
			}
			// TODO add section to determine start when inserting imports into a compilation unit without imports
			else
				throw new IllegalStateException("Cannot determine start because nodeList is empty");
		}
		else if (index == nodeList.size()) {
			ASTNode lastNode = nodeList.get(index - 1);
			start = lastNode.getStartPosition() + lastNode.getLength();
		}
		else {
			start = nodeList.get(index).getStartPosition();
		}

		// Insert the nodes and adjust the source

		String newSource = "";
		for (ASTNode node : newNodes) {
			assertRoot(node);
			adjustStartPositions(node, 0, start + newSource.length());
			newSource += node.toString();
		}
		adjustStartPositions(compUnit, start, start + newSource.length());
		for (ASTNode node : newNodes) {
			nodeList.add(index++, node);
		}
		source = source.substring(0, start) + newSource + source.substring(start);
	}

	/**
	 * Remove the specified node from the specified list of nodes
	 * 
	 * @param nodeList the list of nodes (not <code>null</code> and contains no
	 *            <code>null</code>s)
	 * @param oldNode the node to be removed (not <code>null</code>)
	 * @return
	 */
	public ASTNode remove(List<ASTNode> nodeList, ASTNode oldNode) {
		int index = nodeList.indexOf(oldNode);
		if (index == -1)
			throw new IllegalArgumentException("The nodeList does not contain the node to be removed");
		return remove(nodeList, index);
	}

	/**
	 * Remove the specified node from the specified list of nodes.
	 * 
	 * @param nodeList the list of nodes (not <code>null</code> and contains no
	 *            <code>null</code>s)
	 * @param index the index of the node to be removed (0 <= index < nodeList.size())
	 */
	public ASTNode remove(List<ASTNode> nodeList, int index) {
		ASTNode oldNode = nodeList.get(index);
		assertInCompUnit(oldNode);

		// Find the original node's source start and end.

		int start = oldNode.getStartPosition();
		int oldEnd = start + oldNode.getLength();

		// If this is a statement (ends with a semi-colon) 
		// then consume the whitespace to the end of the line including the line end characters

		if (source.charAt(oldEnd - 1) == ';') {
			oldEnd = skipWhitespaceToNextLineStart(oldEnd);
		}

		// otherwise consume trailing comma, or leading comma if part of a list of method arguments

		else {
			oldEnd = skipWhitespace(oldEnd);
			if (source.charAt(oldEnd) == ',')
				oldEnd = skipWhitespace(oldEnd + 1);
			// TODO consume leading comma if trailing comma is not found
		}

		// Remove the node and adjust the source

		adjustStartPositions(oldNode, start, 0);
		adjustStartPositions(compUnit, oldEnd, start);

		source = source.substring(0, start) + source.substring(oldEnd);

		nodeList.remove(index);
		return oldNode;
	}

	//================================================================
	// AST node construction

	/**
	 * Answer a copy of the specified node or <code>null</code> if the specified node is null
	 */
	public ASTNode deepCopy(ASTNode node) {
		if (node == null)
			return null;
		ASTNode result = ASTNode.copySubtree(compUnit.getAST(), node);
		adjustStartPositions(result, result.getStartPosition(), 0);
		return result;
	}

	/**
	 * Construct a new simple name
	 * 
	 * @param text the simple name as text (not <code>null</code>, not empty, not a
	 *            keyword, not boolean literal ("true", "false"), not null literal
	 *            ("null")
	 * @param startPosition the starting position relative to the new AST nodes being
	 *            created. For example, when instantiating a new name node to replace an
	 *            existing name node, pass zero. When instantiation a new name node to be
	 *            used as part of a new import declaration, pass the offset of the name
	 *            node within the new import declaration.
	 * @return a new name (not <code>null</code>)
	 */
	public SimpleName newSimpleName(String text, int startPosition) {
		SimpleName newName = compUnit.getAST().newSimpleName(text);
		newName.setSourceRange(startPosition, text.length());
		return newName;
	}

	/**
	 * Construct a new simple or qualified name
	 * 
	 * @param text the simple or qualified name as text (not <code>null</code>, not empty)
	 * @param startPosition the starting position relative to the new AST nodes being
	 *            created. For example, when instantiating a new name node to replace an
	 *            existing name node, pass zero. When instantiation a new name node to be
	 *            used as part of a new import declaration, pass the offset of the name
	 *            node within the new import declaration.
	 * @return a new name (not <code>null</code>)
	 */
	public Name newName(String text, final int startPosition) {
		Name newName = compUnit.getAST().newName(text);
		newName.accept(new ASTVisitor() {
			int index = startPosition;

			public boolean visit(QualifiedName node) {
				node.setSourceRange(startPosition, node.toString().length());
				return true;
			}

			public boolean visit(SimpleName node) {
				int length = node.getIdentifier().length();
				node.setSourceRange(index, length);
				index += length + 1;
				return true;
			}
		});
		return newName;
	}

	/**
	 * Construct a new method invocation
	 * 
	 * @param target the target expression or null if none
	 * @param methodName the method name (not <code>null</code>, not empty)
	 * @param arguments the argument expressions
	 * @return the method invocation
	 */
	@SuppressWarnings("unchecked")
	public MethodInvocation newMethodInvocation(Expression target, String methodName, Expression... arguments) {
		MethodInvocation invocation = getCompilationUnit().getAST().newMethodInvocation();
		int start = 0;
		if (target != null) {
			assertRoot(target);
			invocation.setExpression(target);
			start += target.toString().length() + 1; // target plus dot
		}
		invocation.setName(newSimpleName(methodName, start));
		start += methodName.length();
		for (int i = 0; i < arguments.length; i++) {
			Expression arg = arguments[i];
			assertRoot(arg);
			start += i == 0 ? 1 : 2; // opening parenthesis or comma and space
			adjustStartPositions(arg, 0, start);
			invocation.arguments().add(arg);
			start += arg.toString().length();
		}
		invocation.setSourceRange(0, invocation.toString().length());
		return invocation;
	}

	//================================================================
	// Internal utility methods

	/**
	 * Find the start of the next line
	 */
	private int skipWhitespace(int index) {
		while (index < source.length()) {
			if (!Character.isWhitespace(source.charAt(index)))
				break;
			index++;
		}
		return index;
	}

	/**
	 * Find next non-whitespace character or the start of the next line whichever comes
	 * first
	 */
	private int skipWhitespaceToNextLineStart(int index) {
		while (index < source.length()) {
			char ch = source.charAt(index++);
			if (!Character.isWhitespace(ch))
				break;
			if (ch == '\r') {
				if (index < source.length() && source.charAt(index) == '\n')
					index++;
				break;
			}
			if (ch == '\n') {
				break;
			}
		}
		return index;
	}

	/**
	 * Call this method to update the source BEFORE one node replaces another node
	 * 
	 * @param oldNode the original node (not <code>null</code>)
	 * @param newNode the replacement node (not <code>null</code>)
	 */
	private void replacing(ASTNode oldNode, ASTNode newNode) {
		assertNotRoot(oldNode);
		assertRoot(newNode);

		// Find the original node's source

		int start = oldNode.getStartPosition();
		int oldEnd = start + oldNode.getLength();

		// Adjust the source

		adjustStartPositions(oldNode, start, 0);
		adjustStartPositions(newNode, 0, start);
		adjustStartPositions(compUnit, oldEnd, start + newNode.getLength());

		source = source.substring(0, start) + newNode.toString() + source.substring(oldEnd);
	}

	/**
	 * Adjust the start position of nodes with start position greater than oldStart in the
	 * specified root node.
	 * 
	 * @param startNode the top most node containing nodes to be adjusted
	 * @param oldStart the old start position
	 * @param newStart the new start position
	 */
	private void adjustStartPositions(ASTNode startNode, final int oldStart, int newStart) {
		final int offset = newStart - oldStart;
		startNode.accept(new ASTVisitor() {
			public void preVisit(ASTNode node) {
				if (node.getStartPosition() >= oldStart)
					node.setSourceRange(node.getStartPosition() + offset, node.getLength());
				else if (node.getStartPosition() + node.getLength() >= oldStart)
					node.setSourceRange(node.getStartPosition(), node.getLength() + offset);
			}
		});
	}

	/**
	 * Answer the root node containing the specified node or return the specified node if
	 * the specified node is a root node
	 * 
	 * @param node the node in question
	 * @return the root node (not <code>null</code>)
	 */
	private ASTNode getRoot(ASTNode node) {
		ASTNode current = node;
		while (true) {
			ASTNode parent = current.getParent();
			if (parent == null)
				return current;
			current = parent;
		}
	}

	/**
	 * Assert that the specified node is a root node and not a child of any other node
	 * 
	 * @param node the node to be checked (not <code>null</code>)
	 */
	private void assertRoot(ASTNode node) {
		if (node.getParent() != null)
			throw new IllegalArgumentException("Expected root node: " + node);
	}

	/**
	 * Assert that the specified node is not a root node
	 * 
	 * @param node the node to be checked (not <code>null</code>)
	 */
	private void assertNotRoot(ASTNode node) {
		if (node.getParent() == null)
			throw new IllegalArgumentException("Expected non-root node: " + node);
	}

	/**
	 * Assert that the specified node is part of the receiver's compilation unit
	 * 
	 * @param node the node to check (not <code>null</code>)
	 */
	private void assertInCompUnit(ASTNode node) {
		if (getRoot(node) != compUnit)
			throw new IllegalArgumentException("Expected node to be part of compilation unit: " + node);
	}
}
