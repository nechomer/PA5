/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.asm;

import IC.lir.*;

/**
 *
 * @author Nimrod Rappoport
 * This is an interface for a visitor of a lir program
 */
public interface LIRVisitor {

    public Object visit(LirArithmeticBinary lab);

    public Object visit(LirArrayLength lal);

    public Object visit(LirComment lc);

    public Object visit(LirCompare lc);

    public Object visit(LirJump lj);

    public Object visit(LirLabel ll);

    public Object visit(LirLibCall llc);

    public Object visit(LirLogicalBinary llb);

    public Object visit(LirMove lm);

    public Object visit(LirMoveArray lma);

    public Object visit(LirMoveField lmf);

    public Object visit(LirReturn lr);

    public Object visit(LirStaticCall lsc);

    public Object visit(LirUnaryOp luo);

    public Object visit(LirVirtualCall lvc);


}
