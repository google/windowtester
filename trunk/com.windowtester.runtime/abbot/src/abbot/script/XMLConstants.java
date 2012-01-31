package abbot.script;

/** Attributes used by script steps and component references.  Any entries
    here should have a corresponding entry to the
    <a href="doc-files/abbot.xsd">XML schema</a> file.  See that
    file for appropriate XML usage.
*/
public interface XMLConstants {
    /** Primary document tag for a test script. */
    String TAG_AWTTESTSCRIPT = "AWTTestScript";
    String TAG_LAUNCH    = "launch";
    /** @deprecated */
    String TAG_DELEGATE  =   "delegate"; 
    String TAG_CLASSPATH =   "classpath";
    String TAG_THREADED  =   "threaded";
    String TAG_APPLETVIEWER = "appletviewer";
    String TAG_CODE      =   "code";
    String TAG_CODEBASE  =   "codebase";
    String TAG_ARCHIVE   =   "archive";
    String TAG_TERMINATE = "terminate";
    String TAG_COMPONENT = "component";
    String TAG_ID        =   "id";
    String TAG_NAME      =   "name";
    String TAG_WEIGHTED  =   "weighted";
    String TAG_WINDOW    =   "window";
    /** Title of a Frame, Dialog or JInternalFrame.  Any other use is
        deprecated (i.e. Title of window ancestor).
    */
    String TAG_TITLE     =   "title"; 
    String TAG_BORDER_TITLE = "borderTitle";
    String TAG_ROOT      =   "root";
    String TAG_PARENT    =   "parent"; 
    String TAG_INDEX     =   "index";
    String TAG_CLASS     =   "class";
    String TAG_TAG       =   "tag";
    String TAG_LABEL     =   "label";
    String TAG_TEXT      =   "text";
    String TAG_ICON      =   "icon";
    String TAG_INVOKER   =   "invoker";
    String TAG_HORDER    =   "hOrder";
    String TAG_VORDER    =   "vOrder";
    String TAG_PARAMS    =   "params";
    String TAG_DOCBASE   =   "docBase";
    String TAG_EVENT     = "event";
    String TAG_SEQUENCE  = "sequence";
    String TAG_TYPE      =   "type";
    String TAG_KIND      =   "kind";
    String TAG_X         =   "x"; 
    String TAG_Y         =   "y"; 
    String TAG_WIDTH     =   "width";
    String TAG_HEIGHT    =   "height";
    String TAG_MODIFIERS =   "modifiers";
    String TAG_COUNT     =   "count";
    String TAG_TRIGGER   =   "trigger";
    String TAG_KEYCODE   =   "keyCode";
    String TAG_KEYCHAR   =   "keyChar";
    String TAG_ACTION    = "action";
    String TAG_ASSERT    = "assert";
    String TAG_CALL      = "call";
    String TAG_SAMPLE    = "sample";
    String TAG_PROPERTY  =   "property";
    String TAG_FIXTURE   = "fixture";
    String TAG_SCRIPT    = "script";
    String TAG_FILENAME  =   "filename";
    String TAG_FORKED    =   "forked";
    String TAG_SLOW      =   "slow";
    String TAG_AWT       =   "awt";
    String TAG_VMARGS   =   "vmargs";
    String TAG_WAIT      = "wait";
    String TAG_EXPR      =   "expr";
    String TAG_METHOD    =   "method";
    String TAG_ARGS      =   "args";
    String TAG_VALUE     =   "value";
    String TAG_DESC      =   "desc";
    String TAG_INVERT    =   "invert";
    String TAG_TIMEOUT   =   "timeout";
    String TAG_POLL_INTERVAL = "pollInterval";
    String TAG_STOP_ON_FAILURE = "stopOnFailure";
    String TAG_STOP_ON_ERROR = "stopOnError";
    // this is not actually used as a tag per se
    String TAG_COMMENT   = "comment"; 
}
