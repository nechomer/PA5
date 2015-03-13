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
public class LirMoveArray implements LirInstruction{

    /*
     * A class for the MoveArray lir instruction.
     * arrayReg is the register number that stores the array address.
     * arrayIndexis the register number that stores the index we will access
     * desReg is the register we will save (or take from!) the result.
     * store is true <=> this a store instruction (store into the array)
     */

    private int arrayReg;
    private int arrayIndex;
    private int destReg;
    private boolean store;

    public LirMoveArray(int arrayReg, int arrayIndex, int destReg, boolean store) {
        this.arrayReg = arrayReg;
        this.arrayIndex = arrayIndex;
        this.destReg = destReg;
        this.store = store;
    }

    @Override
    public String toString() {
        String ret = "MoveArray ";
        String access = "R"+arrayReg+"[R"+arrayIndex+"]";
        if (store)
            return ret + "R" + destReg +", " + access;
        else
            return ret + access + ", "+ "R" + destReg;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public int getArrayIndex() {
        return arrayIndex;
    }

    public int getArrayReg() {
        return arrayReg;
    }

    public int getDestReg() {
        return destReg;
    }

    public boolean isStore() {
        return store;
    }



}
