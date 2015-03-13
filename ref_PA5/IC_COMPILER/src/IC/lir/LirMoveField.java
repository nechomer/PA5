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
public class LirMoveField implements LirInstruction {

    /*
     * A class for the MoveField lir instruction.
     * objectReg is the register number that stores the object's address.
     * fieldOffset is the offset of the involved field in the object's class layout
     * desReg is the register we will save (or take from!) the result.
     * store is true <=> this a store instruction. 
     * dispatch is the name of the dispatch vector. needed for the "new" translation.
     */
    private int objectReg;
    private int destReg;
    private int fieldOffset;
    private String dispatch;
    private boolean store;

    public LirMoveField(int objectReg, int destReg, int fieldOffset, boolean store) {
        this.objectReg = objectReg;
        this.destReg = destReg;
        this.fieldOffset = fieldOffset;
        this.store = store;
        this.dispatch = null;
    }

    public LirMoveField(int objectReg, String dispatch) {
        this.objectReg = objectReg;
        this.dispatch = dispatch;
    }

    @Override
    public String toString() {
        String ret = "MoveField ";

        if (dispatch == null) {
            String access = "R" + objectReg + "." + fieldOffset;
            if (store) {
                return ret + "R" + destReg + ", " + access;
            } else {
                return ret + access + ", R" + destReg;
            }
        } else {
            ret += dispatch + ", R" + objectReg + ".0";
            return ret;
        }

    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public int getDestReg() {
        return destReg;
    }

    public String getDispatch() {
        return dispatch;
    }

    public int getFieldOffset() {
        return fieldOffset;
    }

    public int getObjectReg() {
        return objectReg;
    }

    public boolean isStore() {
        return store;
    }


}
