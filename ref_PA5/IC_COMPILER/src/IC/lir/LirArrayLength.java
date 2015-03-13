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
public class LirArrayLength implements LirInstruction{

    private int destReg;
    private int arrayReg;

    public LirArrayLength(int destReg, int arrayReg) {
        this.destReg = destReg;
        this.arrayReg = arrayReg;
    }

    @Override
    public String toString() {
        return "ArrayLength R"+arrayReg+", R" + destReg;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public int getArrayReg() {
        return arrayReg;
    }

    public int getDestReg() {
        return destReg;
    }

}
