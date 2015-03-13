package IC.asm;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nimrod Rappoport
 * This class will remember the offset of each LIR register, parameter and local variable
 * of a method, and will be used to get the offset given the variable
 * (unfinished obviously)
 */
public class MethodLayout {
    // offsets has the offset of each variable, parameter or LIR register.

    private Map<String, Integer> offsets = new HashMap<String, Integer>();
    private int nextVarOffset = -4, nextFormalOffset = 8;
    // will remember offset of next var to be inserted (same with formal)
    // -4 is where first local is inserted, 8 is where first formal

    public int getVarOffset(String varName) {
        return offsets.get(varName);
    }

    // introduces a new local variable to the method layout
    public void introduceVariable(String varName) {
        if (offsets.get(varName) == null) {
            offsets.put(varName, nextVarOffset);
            nextVarOffset -= 4;
        }
    }

    void introduceFormals(String[] paramNames) {
        for (String formalName : paramNames) {
                offsets.put(formalName, nextFormalOffset);
                nextFormalOffset += 4;
        }
    }

    /* returns 4* numbers of local variables of the method
     * (the size we need to move the esp in the function's prologue)
     * only called after all variables were introduced.
     */
    public int getStackSize() {
        // TODO: make sure
        return -(nextVarOffset+4);
    }
}
