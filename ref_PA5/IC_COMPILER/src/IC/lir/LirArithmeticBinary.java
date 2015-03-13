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
public class LirArithmeticBinary implements LirInstruction{

    /*
     * Class for lir arithmetic binary insturctions
     * op is opertor's name (Add, Sub, Mul, Div, Mod). param1 and 2 are registers numbers.
     * Also checks for runtime division or mod by 0
     */

    private String op;
    private int param1;
    private int param2;

    public LirArithmeticBinary(String op, int param1, int param2) {
        this.op = op;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public String toString() {
        return op + " " +"R"+ param1 + ", " +"R"+param2;
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

    	public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}



}
