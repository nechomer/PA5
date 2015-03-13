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
public class LirReturn implements LirInstruction {

/*
 * A class for the lir return instruction
 * param is the number of the register we store the result in, -1 for Rdummy
 */
    private int param;

    public LirReturn(int param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "Return "+(param == -1 ? "Rdummy" : "R"+param);
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public int getParam() {
        return param;
    }


}
