package abbot.script;

import abbot.*;
import abbot.util.EventDispatchExceptionHandler;

public class EventExceptionHandler extends EventDispatchExceptionHandler {
    protected void exceptionCaught(Throwable thr) {
        if (thr.getClass().getName().
            equals(ExitException.class.getName())) {
            Log.debug("Application attempted exit from the event "
                      + "dispatch thread, ignoring it");
            Log.debug(thr);
        }
        else if (thr instanceof NullPointerException
                 && Log.getStack(Log.FULL_STACK, thr).
                 indexOf("createHierarchyEvents") != -1) {
            // java 1.3 hierarchy listener bug, most likely
            Log.debug("Apparent hierarchy NPE bug:\n"
                      + Log.getStack(Log.FULL_STACK, thr));
        }
        else {
            Log.warn("Unexpected exception while dispatching events:");
            Log.warn(thr);
        }
    }
}
