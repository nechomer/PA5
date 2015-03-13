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
public class LirJump implements LirInstruction {

    /*
     * A class for the lir jump instruction.
     * jumpType is the type of jump.
     * jumpTo is the name of the label we jump to
     */
    // Label instead of string?
    private String jumpTo;
    private JumpTypes jumpType;

    public LirJump(JumpTypes jumpType, String jumpTo) {
        this.jumpType = jumpType;
        this.jumpTo = jumpTo;
    }

    @Override
    public String toString() {
        String ret = "Jump";
        if (this.jumpType != null) {
            ret += jumpType.getName();
        }
        ret += " " + jumpTo;
        return ret;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public String getJumpTo() {
        return jumpTo;
    }

    public JumpTypes getJumpType() {
        return jumpType;
    }


}
