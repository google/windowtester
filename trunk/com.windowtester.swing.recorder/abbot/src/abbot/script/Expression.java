package abbot.script;

import java.util.*;

import org.jdom.*;
import org.jdom.Element;
//import bsh.*;
import abbot.AssertionFailedError;

/** Provides evaluation of arbitrary Java expressions.  Any Java expression is
    supported, with a more loose syntax if desired.  See the
    <a href=http://www.beanshell.org/docs.html>beanshell documentation</a> for
    complete details of the extended features available in this evaluator.
    <p>
    Note that any variables declared or assigned will be available to any
    subsequent steps in the same Script.
*/

public class Expression extends Step {

    public static final String TAG_EXPRESSION = "expression";

    private static final String USAGE = "<expression>{java/beanshell expression}</expression>";
    private String expression = "";

    public Expression(Resolver resolver, Element el, Map attributes) {
        super(resolver, attributes);

        String expr = null;
        Iterator iter = el.getContent().iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof CDATA) {
                expr = ((CDATA)o).getText();
                break;
            }
        }
        if (expr == null)
            expr = el.getText();
        setExpression(expr);
    }

    public Expression(Resolver resolver, String description) {
        super(resolver, description);
    }

    public String getDefaultDescription() { 
        return getExpression();
    }
    public String getUsage() { return USAGE; }
    public String getXMLTag() { return TAG_EXPRESSION; }

    protected Element addContent(Element el) {
        return el.addContent(new CDATA(getExpression()));
    }

    public void setExpression(String text) {
        expression = text;
    }

    public String getExpression() {
        return expression;
    }

    /** Evaluates the expression. */
    protected void runStep() throws Throwable {
 /*       Interpreter sh = (Interpreter)
            getResolver().getProperty(Script.INTERPRETER);
        try {
            sh.eval(getExpression());
        }
        catch(TargetError e) {
            throw e.getTarget();
        }
  */  }
}
