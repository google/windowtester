package abbot.editor;

import java.awt.Toolkit;
import java.awt.event.InputEvent;

/** Provide Editor action key names and menu keys.
    Action names are looked up via
    {@link abbot.i18n.Strings#get(String)} by
    prepending the string <code>"actions."</code>.
    NOTE: to add a new editor action, define a key for it here and an action
    for it in ScriptEditor.  Add to the action map in
    ScriptEditor.initActions, then (optionally) add it to the menu layout in
    ScriptEditor.initMenus. 
*/
public interface EditorConstants {

    String MENU_FILE = "menus.file";
    String MENU_EDIT = "menus.edit";
    String MENU_TEST = "menus.test";
    String MENU_INSERT = "menus.insert";
    String MENU_CAPTURE = "menus.capture";
    String MENU_HELP = "menus.help";

    String ACTION_PREFIX = "actions.";
    String ACTION_EDITOR_ABOUT = "editor-about";
    String ACTION_EDITOR_EMAIL = "editor-email";
    String ACTION_EDITOR_BUGREPORT = "editor-submit-bug";
    String ACTION_EDITOR_WEBSITE = "editor-website";
    String ACTION_EDITOR_USERGUIDE = "editor-userguide";
    String ACTION_EDITOR_QUIT = "editor-quit";
    String ACTION_SCRIPT_OPEN = "script-open";
    String ACTION_SCRIPT_NEW = "script-new";
    String ACTION_SCRIPT_DUPLICATE = "script-duplicate";
    String ACTION_SCRIPT_SAVE = "script-save";
    String ACTION_SCRIPT_SAVE_AS = "script-save-as";
    String ACTION_SCRIPT_RENAME = "script-rename";
    String ACTION_SCRIPT_CLOSE = "script-close";
    String ACTION_SCRIPT_DELETE = "script-delete";
    String ACTION_SCRIPT_CLEAR = "script-clear";
    String ACTION_STEP_CUT = "step-cut";
    String ACTION_STEP_MOVE_UP = "step-move-up";
    String ACTION_STEP_MOVE_DOWN = "step-move-down";
    String ACTION_STEP_GROUP = "step-group";
    String ACTION_SELECT_TESTSUITE = "select-testsuite";
    String ACTION_EXPORT_HIERARCHY = "export-hierarchy";
    String ACTION_RUN = "run";
    String ACTION_RUN_TO = "run-to";
    String ACTION_RUN_SELECTED = "run-selected";
    String ACTION_RUN_LAUNCH = "run-launch";
    String ACTION_RUN_TERMINATE = "run-terminate";
    String ACTION_GET_VMARGS = "run-get-vmargs";
    String ACTION_TOGGLE_FORKED = "toggle-forked";
    String ACTION_TOGGLE_SLOW_PLAYBACK = "toggle-slow-playback";
    String ACTION_TOGGLE_AWT_MODE = "toggle-awt-mode";
    String ACTION_TOGGLE_STOP_ON_FAILURE = "toggle-stop-on-failure";
    String ACTION_TOGGLE_STOP_ON_ERROR = "toggle-stop-on-error";
    String ACTION_INSERT_LAUNCH = "insert-launch";
    String ACTION_INSERT_APPLET = "insert-applet";
    String ACTION_INSERT_TERMINATE = "insert-terminate";
    String ACTION_INSERT_CALL = "insert-call";
    String ACTION_INSERT_SAMPLE = "insert-sample";
    String ACTION_INSERT_SEQUENCE = "insert-sequence";
    String ACTION_INSERT_SCRIPT = "insert-script";
    String ACTION_INSERT_FIXTURE = "insert-fixture";
    String ACTION_INSERT_COMMENT = "insert-comment";
    String ACTION_INSERT_EXPRESSION = "insert-expression";
    String ACTION_INSERT_ANNOTATION = "insert-annotation";
    String ACTION_DYNAMIC = "dynamic-actions";
    String ACTION_CAPTURE_IMAGE = "capture-image";
    String ACTION_CAPTURE_COMPONENT = "capture-component";
    String ACTION_SELECT_COMPONENT = "select-component";
    String ACTION_CAPTURE = "capture";
    String ACTION_CAPTURE_ALL = "capture-all";
}
