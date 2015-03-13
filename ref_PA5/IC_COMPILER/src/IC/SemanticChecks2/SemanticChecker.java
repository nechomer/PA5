package IC.SemanticChecks2;

import IC.AST.*;
import IC.SemanticalError;

/**
 * Does checks 3-5 in the PA guide
 * @author Nimrod Rappoport
 */
public class SemanticChecker implements Visitor {

    private boolean inVirtual = false;
    private int loopCount = 0;

    public Object visit(Program program) {
        boolean sawMain = false;
        for (ICClass ICclass : program.getClasses()) {
            for (Method method : ICclass.getMethods()) { // check that that's what they mean
                if (method.getName().equals("main")
                    && method instanceof StaticMethod
                    && method.getType().getName().equals("void")
                    && method.getFormals().size() == 1
                    && method.getFormals().get(0).getType().getDimension() == 1
                    && method.getFormals().get(0).getType().getName().equals("string")) {
                    if (sawMain) {
                        throw new SemanticalError("Only one main method is allowed", method.getLine());
                    }
                    sawMain = true;
                }
            }

        }
        if (!sawMain) {
            throw new SemanticalError("There is no main method!", program.getLine());
        }

        for (ICClass ICclass : program.getClasses()) {
            ICclass.accept(this);
        }

        return null;
    }

    public Object visit(ICClass icClass) {
        if (icClass.hasSuperClass())
            if (icClass.getSuperClassName().equals(icClass.getName()))
                throw new SemanticalError("The class " + icClass.getName()
                        + " may not extend itself...", icClass.getLine());
        
        for (Method method : icClass.getMethods()) {
            method.accept(this);
        }
        return null;
    }

    public Object visit(Field field) {
        throw new UnsupportedOperationException("Not supported yet.");// never called
    }

    public Object visit(VirtualMethod method) {
        inVirtual = true;
        for (Statement statement : method.getStatements()) {
            statement.accept(this);
        }
        inVirtual = false;
        return null;
    }

    public Object visit(StaticMethod method) {
        for (Statement statement : method.getStatements()) {
            statement.accept(this);
        }
        return null;
    }

    public Object visit(LibraryMethod method) {
        return null;
    }

    public Object visit(Formal formal) {
        throw new UnsupportedOperationException("Not supported yet.");// never called
    }

    public Object visit(PrimitiveType type) {
        throw new UnsupportedOperationException("Not supported yet.");// never called
    }

    public Object visit(UserType type) {
        throw new UnsupportedOperationException("Not supported yet."); // never called
    }

    public Object visit(Assignment assignment) {
        assignment.getAssignment().accept(this);
        assignment.getVariable().accept(this);
        return null;
    }

    public Object visit(CallStatement callStatement) {
        callStatement.getCall().accept(this);
        return null;
    }

    public Object visit(Return returnStatement) {
        if (returnStatement.hasValue()) {
            returnStatement.getValue().accept(this);
        }
        return null;
    }

    public Object visit(If ifStatement) {
        ifStatement.getCondition().accept(this);
        ifStatement.getOperation().accept(this);
        if (ifStatement.hasElse()) {
            ifStatement.getElseOperation().accept(this);
        }
        return null;
    }

    public Object visit(While whileStatement) {
        whileStatement.getCondition().accept(this); // is it OK to have break/continue in condition?
        loopCount++;
        whileStatement.getOperation().accept(this);
        loopCount--;
        return null;
    }

    public Object visit(Break breakStatement) {
        if (loopCount == 0) {
            throw new SemanticalError("break statements can only appear inside loops ", breakStatement.getLine());
        }
        return null;
    }

    public Object visit(Continue continueStatement) {
        if (loopCount == 0) {
            throw new SemanticalError("continue statements can only appear inside loops ", continueStatement.getLine());
        }
        return null;
    }

    public Object visit(StatementsBlock statementsBlock) {
        for (Statement statement : statementsBlock.getStatements()) {
            statement.accept(this);
        }
        return null;
    }

    public Object visit(LocalVariable localVariable) {
        if (localVariable.hasInitValue()) {
            localVariable.getInitValue().accept(this);
        }
        return null;
    }

    // UNTIL HERE
    public Object visit(VariableLocation location) {
        if (location.isExternal())
        location.getLocation().accept(this);
        return null;
    }

    public Object visit(ArrayLocation location) {
        location.getArray().accept(this);
        location.getIndex().accept(this);
        return null;
    }

    public Object visit(StaticCall call) {
        for (Expression exp : call.getArguments()) {
            exp.accept(this);
        }
        return null;
    }

    public Object visit(VirtualCall call) {
        if (call.isExternal())
        call.getLocation().accept(this);
        for (Expression exp : call.getArguments()) {
            exp.accept(this);
        }
        return null;
    }

    public Object visit(This thisExpression) {
        if (inVirtual) {
            return null;
        }
        throw new SemanticalError("\"this\" expression can only be used inside virtual methods", thisExpression.getLine());
    }

    public Object visit(NewClass newClass) {
        return null;
    }

    public Object visit(NewArray newArray) {
        newArray.getSize().accept(this);
        return null;
    }

    public Object visit(Length length) {
        length.getArray().accept(this);
        return null;
    }

    public Object visit(MathBinaryOp binaryOp) {
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this);
        return null;
    }

    public Object visit(LogicalBinaryOp binaryOp) {
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this);
        return null;
    }

    public Object visit(MathUnaryOp unaryOp) {
        unaryOp.getOperand().accept(this);
        return null;
    }

    public Object visit(LogicalUnaryOp unaryOp) {
        unaryOp.getOperand().accept(this);
        return null;
    }

    public Object visit(Literal literal) {
        return null;
    }

    public Object visit(ExpressionBlock expressionBlock) {
        expressionBlock.getExpression().accept(this);
        return null;
    }
}
