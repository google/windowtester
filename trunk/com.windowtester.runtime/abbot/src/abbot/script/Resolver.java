package abbot.script;

import java.awt.Component;
import java.util.Collection;
import java.io.File;

import abbot.finder.Hierarchy;

// TODO: extract reference management
// o hierarchy
// o refs collection
// o name generation

/** Interface to provide a general context in which tests are run.
 *  Includes ComponentReferences, current gui hierarchy, properties, and a
 *  working directory. 
 */
public interface Resolver {
    /** Return the existing reference for the given component, or null if none
        exists. */
    ComponentReference getComponentReference(Component comp);
    /** Add a new component to the existing collection. */
    ComponentReference addComponent(Component comp);
    /** Add a new component reference to the existing collection. */
    void addComponentReference(ComponentReference ref);
    /** Returns a collection of all the existing references. */
    Collection getComponentReferences();
    /** Return the ComponentReference matching the given id, or null if none
        exists. */
    ComponentReference getComponentReference(String refid);
    /** Get Hierarchy used by this Resolver. */
    Hierarchy getHierarchy();

    /** Return the class loader for use in this context. */
    ClassLoader getContextClassLoader();
    /** Provide a working directory context for relative pathnames. */
    File getDirectory();
    /** Provide temporary storage of String values. */
    void setProperty(String name, Object value);
    /** Provide retrieval of values from temporary storage. */
    Object getProperty(String name);
    /** Provide a human-readable string that describes the given step's
        context.
    */
    // TODO: this belongs in UIContext
    String getContext(Step step);
}
