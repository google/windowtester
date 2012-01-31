package abbot.finder;

import java.awt.Component;
import java.awt.Container;

/** Interface to support looking up existing components based on a number of
    different criteria.
    @see Matcher
*/
public interface ComponentFinder {
    /** Find a Component, using the given Matcher to determine whether a given
        component in the hierarchy used by this ComponentFinder is the desired
        one.
        <p>
        Note that {@link MultipleComponentsFoundException} can only be
        thrown if the {@link Matcher} argument is an instance of
        {@link MultiMatcher}. 
    */
    Component find(Matcher m)
        throws ComponentNotFoundException, MultipleComponentsFoundException;

    /** Find a Component, using the given Matcher to determine whether a given
        component in the hierarchy under the given root is the desired
        one.
        <p>
        Note that {@link MultipleComponentsFoundException} can only be
        thrown if the {@link Matcher} argument is an instance of
        {@link MultiMatcher}. 
    */
    Component find(Container root, Matcher m)
        throws ComponentNotFoundException, MultipleComponentsFoundException;
}
