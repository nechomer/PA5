/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.AST;

import IC.SemanticChecks2.TypeCheckingVisitor;

/**
 *
 * @author user
 */
public class PrettyPrinter2 extends PrettyPrinter {

    TypeCheckingVisitor tcv;

    public PrettyPrinter2(String icFile, TypeCheckingVisitor tcv) {
        super (icFile);
        this.tcv = tcv;
    }

    @Override
    public Object visit(Program program) {
        return super.visit(program);
    }

    @Override
    public Object visit(ICClass icClass) {
        return super.visit(icClass);
    }

    @Override
    public Object visit(PrimitiveType type) {
        return super.visit(type);
    }

    @Override
    public Object visit(UserType type) {
        return super.visit(type);
    }

    @Override
    public Object visit(Field field) {
        return super.visit(field);
    }

    @Override
    public Object visit(LibraryMethod method) {
        return super.visit(method);
    }

    @Override
    public Object visit(Formal formal) {
        return super.visit(formal);
    }

    @Override
    public Object visit(VirtualMethod method) {
        return super.visit(method);
    }

    @Override
    public Object visit(StaticMethod method) {
        return super.visit(method);
    }

    @Override
    public Object visit(Assignment assignment) {
        return super.visit(assignment);
    }

    @Override
    public Object visit(CallStatement callStatement) {
        return super.visit(callStatement);
    }

    @Override
    public Object visit(Return returnStatement) {
        return super.visit(returnStatement);
    }

    @Override
    public Object visit(If ifStatement) {
        return super.visit(ifStatement);
    }

    @Override
    public Object visit(While whileStatement) {
        return super.visit(whileStatement);
    }

    @Override
    public Object visit(Break breakStatement) {
        return super.visit(breakStatement);
    }

    @Override
    public Object visit(Continue continueStatement) {
        return super.visit(continueStatement);
    }

    @Override
    public Object visit(StatementsBlock statementsBlock) {
        return super.visit(statementsBlock);
    }

    @Override
    public Object visit(LocalVariable localVariable) {
        return super.visit(localVariable);
    }

    @Override
    public Object visit(VariableLocation location) {
        StringBuffer output = new StringBuffer();

        indent(output, location);
        output.append("Reference to variable: " + location.getName() + " (" + location.accept(tcv) + ")");
        if (location.isExternal())
                output.append(", in external scope");
        if (location.isExternal()) {
                ++depth;
                output.append(location.getLocation().accept(this));
                --depth;
        } else {
            output.append(" {defined in " + location.enclosingScope().getId() + "}");
        }
        return output.toString();
    }

    @Override
    public Object visit(ArrayLocation location) {
        return (String)super.visit(location);// + " (" + tcv.visit(location) + ")";
    }

    @Override
    public Object visit(StaticCall call) {
        StringBuffer output = new StringBuffer();

        indent(output, call);
        output.append("Call to static method: " + call.getName()
                        + ", in class " + call.getClassName());
        if (!call.getClassName().equals(call.enclosingScope().getId()))
            output.append(" {actually, the method was defined in " + call.enclosingScope().getId() +"}");
        depth += 2;
        for (Expression argument : call.getArguments())
                output.append(argument.accept(this));
        depth -= 2;
        return output.toString();
    }

    @Override
    public Object visit(VirtualCall call) {
		StringBuffer output = new StringBuffer();

		indent(output, call);
		output.append("Call to virtual method: " + call.getName());
		if (call.isExternal())
			output.append(", in external scope ").append("(static definition in " + call.enclosingScope().getId() + ")");
		depth += 2;
		if (call.isExternal())
			output.append(call.getLocation().accept(this));
		for (Expression argument : call.getArguments())
			output.append(argument.accept(this));
		depth -= 2;
		return output.toString();
    }

    @Override
    public Object visit(This thisExpression) {
        return super.visit(thisExpression);
    }

    @Override
    public Object visit(NewClass newClass) {
        return super.visit(newClass);
    }

    @Override
    public Object visit(NewArray newArray) {
        return super.visit(newArray);
    }

    @Override
    public Object visit(Length length) {
        return super.visit(length);
    }

    @Override
    public Object visit(MathBinaryOp binaryOp) {
        return super.visit(binaryOp);
    }

    @Override
    public Object visit(LogicalBinaryOp binaryOp) {
        return super.visit(binaryOp);
    }

    @Override
    public Object visit(MathUnaryOp unaryOp) {
        return super.visit(unaryOp);
    }

    @Override
    public Object visit(LogicalUnaryOp unaryOp) {
        return super.visit(unaryOp);
    }

    @Override
    public Object visit(Literal literal) {
        return super.visit(literal);
    }

    @Override
    public Object visit(ExpressionBlock expressionBlock) {
        return super.visit(expressionBlock);
    }



}
