/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IC.lir;

import IC.asm.LIRVisitor;

/**
 *
 * @author Nimrod Rappoport
 */
public class LirStaticCall extends LirAbstractCall {

    /*
     * A class for the lir Static Call
     * Adds the method name, in addition to everything in abstract call
     */

    private String methodName;

    public LirStaticCall(String methodName, int targetRegister) {
        super(targetRegister);
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "StaticCall " + methodName + super.toString();
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public String getMethodName() {
        return methodName;
    }


    
}
