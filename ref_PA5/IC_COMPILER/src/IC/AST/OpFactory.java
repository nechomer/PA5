/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.AST;

/**
 *
 * @author user
 */
public class OpFactory {

    public static UnaryOp make (IC.UnaryOps op, Expression e)
    {
        if (op == IC.UnaryOps.LNEG)
            return new LogicalUnaryOp(op, e);
        else if (op == IC.UnaryOps.UMINUS)
            return new MathUnaryOp(op, e);
        else
            return null;
    }

    public static BinaryOp make (Expression e1, IC.BinaryOps op, Expression e2)
    {
        if (op == IC.BinaryOps.DIVIDE
                || op == IC.BinaryOps.MINUS
                || op == IC.BinaryOps.MOD
                || op == IC.BinaryOps.MULTIPLY
                || op == IC.BinaryOps.PLUS)
            return new MathBinaryOp(e1, op, e2);
        else if(op == IC.BinaryOps.EQUAL
                || op == IC.BinaryOps.GT
                || op == IC.BinaryOps.GTE
                || op == IC.BinaryOps.LAND
                || op == IC.BinaryOps.LOR
                || op == IC.BinaryOps.LT
                || op == IC.BinaryOps.LTE
                || op == IC.BinaryOps.NEQUAL)
            return new LogicalBinaryOp(e1, op, e2);
        else
            return null;
    }

}
