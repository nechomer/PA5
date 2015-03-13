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
public class LirLogicalBinary implements LirInstruction {
/*
 * Class for the "or" and "and" lir instructions
 * op is operation name (Or, And). param1 and 2 are register numbers
 */
    
    private String op;
    private int param1;
    private int param2;

    public LirLogicalBinary(String op, int param1, int param2) {
        this.op = op;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public String toString() {
        return op + " " + "R" + param1 + ", " + "R" + param2;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public String getOp() {
        return op;
    }

    public int getParam1() {
        return param1;
    }

    public int getParam2() {
        return param2;
    }


}
