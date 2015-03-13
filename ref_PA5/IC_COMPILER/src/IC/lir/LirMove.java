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
public class LirMove implements LirInstruction {

    /*
     * This is a class for the lir move instruction
     * May still change
     * toReg is true <=> we move into a register
     * RegNum is the number of the involved register (there is always exactly one,
     * as we never move two registers in our translation)
     *
     */
    private boolean toReg;
    private int regNum;
    private String memory;
    private int constant;

    public LirMove(int regNum, int constant) {
        this.toReg = true;
        this.regNum = regNum;
        this.constant = constant;
    }

    public LirMove(int regNum, String memory, boolean toReg) {
        this.regNum = regNum;
        this.memory = memory;
        this.toReg = toReg;
    }

    @Override
    public String toString() {
        String ret = "Move ";
        if (memory == null) { // move constant into register
            return ret + constant + ", R" + regNum;
        } else if (toReg) {
            return ret + memory + ", R" + regNum;
        } else {
            return ret + "R" + regNum + ", " + memory;
        }
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public int getConstant() {
        return constant;
    }

    public String getMemory() {
        return memory;
    }

    public int getRegNum() {
        return regNum;
    }

    public boolean isToReg() {
        return toReg;
    }


}
