package abbot.swt.utilities;

import java.util.List;

public class ExceptionHelper {
    
    /**
     * Take a List of Throwables, and chain them together, by
     * starting at the last Throwable in the List, and setting
     * the previous Throwable as its cause.
     *   
     * @param throwables List<Throwable>
     * @return Throwable the first Throwable in the List
     */
    //public static Throwable chainThrowables(List<Throwable> throwables)
    public static Throwable chainThrowables(List throwables)
    {
        for(int i = throwables.size() - 1; i > 0; i--)
        {
            Throwable parent = (Throwable)throwables.get(i);
            Throwable cause  = (Throwable)throwables.get(i - 1);
            if (parent != cause) {
                parent.initCause(cause);
            } 
        }
        return (Throwable)throwables.get(throwables.size() - 1); //First throwable
    }
}
