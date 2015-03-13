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


public class LirLibCall implements LirInstruction{

    /*
     * A class for a lir library call
     * op1 and 2 are numbers of involved registers (at most 2)
     * All library calls are performed on registers, except object allocation
     * In this case, op1 is the size to allocate
     * destRegister is the number of register where we save the output
     * libcall is the type of library call
     */
    private int op1;
    private int op2;
    private int destRegister;
    private LibCall libcall;

    public LirLibCall(LibCall libcall, int op1, int op2, int destRegister) {
        this.op1 = op1;
        this.op2 = op2;
        this.destRegister = destRegister;
        this.libcall = libcall;
    }

    @Override
    public String toString() {
        String ret ="Library " + libcall.getFuncCall()+"(";
        if (libcall!=LibCall.ALLOCATE_OBJECT) {
        if (libcall.getNumArguments()>=1)
            ret+="R"+op1;
        if (libcall.getNumArguments()==2)
            ret+=", R"+op2;
        ret+="), R"+ (destRegister == -1 ? "dummy" : destRegister);
        return ret;
        }
        return ret + op1 + "), R" + destRegister;


    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public int getDestRegister() {
        return destRegister;
    }

    public LibCall getLibcall() {
        return libcall;
    }

    public int getOp1() {
        return op1;
    }

    public int getOp2() {
        return op2;
    }



}
