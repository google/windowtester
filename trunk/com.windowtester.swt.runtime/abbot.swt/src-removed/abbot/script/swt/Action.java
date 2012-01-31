
package abbot.script.swt;

import java.lang.reflect.Method;
import java.util.HashMap;

import abbot.script.InvalidScriptException;
import abbot.swt.Resolver;

/** Encapsulate an action. Usage:<br>
 * <blockquote><code>
 * &lt;action method="..." args="..."&gt;<br>
 * &lt;action method="..." args="widget_id[,...]" class="..."&gt;<br>
 * </code></blockquote>
 * An Action reproduces a user semantic action (such as a mouse click, menu
 * selection, or drag/drop action) on a particular Widget.  The id of the
 * Widget being operated on must be the first argument, and the class of
 * that Widget must be identified by the class tag if the action is not
 * provided by the base
 * <a href="../tester/ComponentTester.html">ComponentTester</a> class.<p>
 * Note that the method name is the name of the actionXXX method,
 * e.g. to click a button (actionClick on
 * AbstractButtonTester), the XML would appear thus:<p> 
 * <blockquote><code>
 * &lt;action method="actionClick" args="My Button" class=javax.swing.AbstractButton&gt;<br>
 * </code></blockquote>
 * Note that if the first argument is a Widget, the class tag is required.
 * Note also that the specified class is the <i>tested</i> class, not the
 * target class for the method invocation.
 */
// don't put tested class into target class

public class Action extends Call {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	private static final String USAGE = 
		"<action method=\"...\" args=\"...\" [class=\"...\"]/>";

	/** Provide a default value for the target class name, so that the Call
	 * parent class won't choke.
	 */
	private static HashMap patchAttributes(HashMap map) {
		if (map.get(TAG_CLASS) == null) {
			map.put(TAG_CLASS, "org.eclipse.swt.widgets.Widget");
		}
		return map;
	}

	public Action(Resolver resolver, HashMap attributes) {
		super(resolver, patchAttributes(attributes));
		init();
	}

	/** Action for a method in the ComponentTester base class. */
	public Action(Resolver resolver, String description,
				  String methodName, String[] args) {
		super(resolver, description, "org.eclipse.swt.widgets.Widget", methodName, args);
		init();
	}

	public Action(Resolver resolver, String description,
				  String methodName, String[] args, Class targetClass) {
		super(resolver, description, targetClass.getName(), methodName, args);
		init();
	}

	private void init() {
		// account for deprecated usage
		String mn = getMethodName();
		if (!mn.startsWith("action"))
			setMethodName("action" + mn);
	}

	/** Ensure the default class name is "org.eclipse.swt.widgets.Widget".
	 * The target class <i>must</i> be a subclass of org.eclipse.swt.widgets.Widget.
	 */
	public void setTargetClassName(String cn) {
		if (cn == null || "".equals(cn))
			cn = "org.eclipse.swt.widgets.Widget";
		super.setTargetClassName(cn);
	}

	/** Return the XML tag for this step. */
	public String getXMLTag() { return TAG_ACTION; }

	/** Return custom attributes for an Action. */
	public HashMap getAttributes() {
		HashMap map = super.getAttributes();
		// Only save the class attribute if it's not the default
		map.remove(TAG_CLASS);
		if (!getTargetClassName().equals("org.eclipse.swt.widgets.Widget"))
			map.put(TAG_CLASS, getTargetClassName());
		return map;
	}

	/** Return the proper XML usage for this step. */
	public String getUsage() { return USAGE; }

	/** Return a default description for this action. */
	protected String getDefaultDescription() {
		// strip off "action"
		String name = getMethodName().substring(6);
		return name + "(" + getEncodedArguments() + ")";
	}

	public Class getTargetClass() throws InvalidScriptException {
		return getTarget().getClass();
	}

	/** Return the target of the invocation. */
	protected Object getTarget() throws InvalidScriptException {
		return resolveTester(getTargetClassName());
	}

	/** Resolve the method name into its final form. */
	protected Method getMethod() throws InvalidScriptException {
		return resolveMethod(getMethodName(), getTargetClass(), void.class);
	}

}
