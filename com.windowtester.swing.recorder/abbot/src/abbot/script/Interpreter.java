package abbot.script;

import java.io.*;
import abbot.Log;
import abbot.finder.BasicFinder;

/** Provides a BeanShell interpreter customized for the Costello scripting
 * environment.
 */
public class Interpreter{ // extends bsh.Interpreter {
    public Interpreter(Resolver r) {
//        setClassLoader(r.getContextClassLoader());
//        try {
//            set("finder", new BasicFinder(r.getHierarchy()));
//            set("resolver", r);
//            set("script", r);
//            InputStream is = getClass().getResourceAsStream("init.bsh");
//            eval(new BufferedReader(new InputStreamReader(is)));
//        }
//        catch(bsh.EvalError e) {
//            Log.warn("Error initializing interpreter: " + e);
//        }
    }
}
