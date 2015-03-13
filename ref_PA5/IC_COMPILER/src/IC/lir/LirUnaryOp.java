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
public class LirUnaryOp implements LirInstruction{

    /*
     * Class for lir unary operation instruction - negation or "not"
     * isNeg is true <=> this is negation. param is register number.
     */

    private boolean isNeg; //otherwise it's a not
    private int param;

    public LirUnaryOp(boolean isNeg, int param) {
        this.isNeg = isNeg;
        this.param = param;
    }

    @Override
    public String toString() {
        if (isNeg)
            return "Neg " + "R"+param;
        else return "Not " +"R"+ param;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public boolean isIsNeg() {
        return isNeg;
    }

    public int getParam() {
        return param;
    }



}
