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
public class LirCompare implements LirInstruction {

    /*
     * Class for lir compare instruction
     * literal is true <=> this indicates a compare between a constant and a register
     * if this is not literal, op1 and op2 are registers numbers (always != -1)
     * it this is a literal, op1 is the number and op2 is the register number
     */
    private int op1;
    private int op2;
    private boolean literal;

    public LirCompare(int op1, int op2, boolean literal) {
        this.literal = literal;
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toString() {
        if (literal)
        return "Compare " + op1 + ", " +"R" + op2;
        else return "Compare " + "R" + op1 + ", " +"R" + op2;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public boolean isLiteral() {
        return literal;
    }

    public int getOp1() {
        return op1;
    }

    public int getOp2() {
        return op2;
    }


}
