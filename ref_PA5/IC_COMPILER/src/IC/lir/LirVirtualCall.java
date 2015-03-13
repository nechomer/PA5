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
public class LirVirtualCall extends LirAbstractCall {

    /*
     * Class for the lir virtual call instruction.
     * methodOffset is the offset of the method in the object's dispatch vector
     * objectRegister is the register in which the object is stored
     */
    private int methodOffset;
    private int objectRegister;

    public LirVirtualCall(int methodOffset, int objectRegister, int targetRegister) {
        super(targetRegister);
        this.methodOffset = methodOffset;
        this.objectRegister = objectRegister;
    }

    @Override
    public String toString() {
        return "VirtualCall R" + objectRegister + "." +methodOffset + super.toString();
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public int getMethodOffset() {
        return methodOffset;
    }

    public int getObjectRegister() {
        return objectRegister;
    }


}
