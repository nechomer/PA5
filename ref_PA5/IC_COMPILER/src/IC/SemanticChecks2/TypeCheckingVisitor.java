package IC.SemanticChecks2;

import IC.AST.*;
import IC.BinaryOps;
import IC.SemanticalError;
import IC.SymbolTable.ClassSymbolTable;
import IC.SymbolTable.Symbol;
import IC.Type.ArrayType;
import IC.Type.ClassType;
import IC.Type.MethodType;
import IC.Type.Type;
import IC.Type.TypeTable;
import IC.UnaryOps;
import java.util.HashMap;

/**
 * Do the type checking
 * @author Barak Itkin
 */
public class TypeCheckingVisitor implements Visitor {

    public HashMap<String, ClassSymbolTable> classSyms;

    public TypeCheckingVisitor(HashMap<String, ClassSymbolTable> classSyms) {
        this.classSyms = classSyms;
    }

    public final void doAccept(ASTNode... visits) {
        for (ASTNode aSTNode : visits) {
            if (aSTNode != null)
                aSTNode.accept(this);
        }
    }

    public final void doAccept(Iterable<? extends ASTNode> visits) {
        for (ASTNode aSTNode : visits) {
            if (aSTNode != null)
                aSTNode.accept(this);
        }
    }

    /*
     * EⱵtrue:bool
     * EⱵfalse:bool
     * EⱵinteger-lieteral:int
     * EⱵstring-literal:string
     * EⱵnull:null
     */
    public Type visit (Literal expr) {
        switch (expr.getType()) {
            // EⱵfalse:bool
            case FALSE:
                return TypeTable.boolType;
            // EⱵtrue:bool
            case TRUE:
                return TypeTable.boolType;
            // EⱵinteger-lieteral:int
            case INTEGER:
                return TypeTable.intType;
            // EⱵstring-literal:string
            case STRING:
                return TypeTable.stringType;
            // EⱵnull:null
            case NULL:
                return TypeTable.nullType;
            // Should not be reached
            default:
                System.err.println("Literal Type visitor - code Should not be reached...");
                return null;
        }
    }

    /*
     * EⱵe0:int, EⱵe1:int, opє{+,-,*,\,%}  ==>  EⱵe0 op e1:int
     * EⱵe0:string, EⱵe1:string            ==>  EⱵe0 + e1:string
     *
     * Also note that:
     * MathBinaryOp ==> e0 op e1, while opє{+,-,*,\,%}
     */
    public Object visit(MathBinaryOp binaryOp) {
        Type e0 = (Type) binaryOp.getFirstOperand().accept(this);
        Type e1 = (Type) binaryOp.getSecondOperand().accept(this);
        BinaryOps op = binaryOp.getOperator();

//        assert op == BinaryOps.PLUS || op == BinaryOps.MINUS
//                || op == BinaryOps.MULTIPLY || op == BinaryOps.DIVIDE
//                || op == BinaryOps.MOD;

        // EⱵe0:int, EⱵe1:int, opє{+,-,*,\,%}  ==> EⱵe0 op e1:int
        if (e0 == TypeTable.intType && e1 == TypeTable.intType)
            return TypeTable.intType;
        // EⱵe0:string, EⱵe1:string  ==> EⱵe0 + e1:string
        else if(e0 == TypeTable.stringType && e1 == TypeTable.stringType && op == BinaryOps.PLUS)
            return TypeTable.stringType;
        else
            throw new SemanticalError("Can't apply binary op " + op.getOperatorString() , binaryOp.getLine());
    }

    /*
     * EⱵe0:int, EⱵe1:int, opє{<=,<,>=,>}              ==> EⱵe0 op e1:bool
     * EⱵe0:T0, EⱵe1:T1,  T0≤T1 or T1≤T0,  opє{==,!=}  ==> EⱵe0 op e1:bool
     * EⱵe0:bool, EⱵe1:bool, opє{&&,||}                ==> EⱵe0 op e1:bool
     *
     * Also note that:
     * LogicalBinaryOp ==> e0 op e1, while opє{<=,<,>,>=,&&,||}
     */
    public Object visit(LogicalBinaryOp binaryOp) {
        Type e0 = (Type) binaryOp.getFirstOperand().accept(this);
        Type e1 = (Type) binaryOp.getSecondOperand().accept(this);
        BinaryOps op = binaryOp.getOperator();

//        assert op == BinaryOps.LTE || op == BinaryOps.LT
//                || op == BinaryOps.GTE || op == BinaryOps.GT
//                || op == BinaryOps.LAND ||  op == BinaryOps.LOR;

        // EⱵe0:int, EⱵe1:int, opє{<=,<,>=,>}  ==> EⱵe0 op e1:bool
        if (e0 == TypeTable.intType && e1 == TypeTable.intType
                && (op == BinaryOps.LTE || op == BinaryOps.LT
                    || op == BinaryOps.GT ||  op == BinaryOps.GTE))
            return TypeTable.boolType;
        // EⱵe0:T0, EⱵe1:T1,  T0≤T1 or T1≤T0,  opє{==,!=}  ==> EⱵe0 op e1:bool
        else if ((e0.subtypeof(e1) || e1.subtypeof(e0))
                && (op == BinaryOps.EQUAL || op == BinaryOps.NEQUAL))
            return TypeTable.boolType;
        // EⱵe0:bool, EⱵe1:bool, opє{&&,||}  ==> EⱵe0 op e1:bool
        else if (e0 == TypeTable.boolType && e1 == TypeTable.boolType
                && (op == BinaryOps.LAND || op == BinaryOps.LOR))
            return TypeTable.boolType;
        else
            throw new SemanticalError("Can't apply binary op " + op.getOperatorString() , binaryOp.getLine());
    }

    /*
     * EⱵe:int  ==>  EⱵ-e:int
     */
    public Object visit(MathUnaryOp unaryOp) {
        Type e = (Type) unaryOp.getOperand().accept(this);
        UnaryOps op = unaryOp.getOperator();

//        assert op == UnaryOps.UMINUS;

        // EⱵe:int  ==>  EⱵ-e:int
        if (e == TypeTable.intType)
            return TypeTable.intType;
        else
            throw new SemanticalError("Can't apply unary op " + op.getOperatorString() , unaryOp.getLine());
    }

    /*
     * EⱵe:bool  ==>  EⱵ!e:bool
     */
    public Object visit(LogicalUnaryOp unaryOp) {
        Type e = (Type) unaryOp.getOperand().accept(this);
        UnaryOps op = unaryOp.getOperator();

//        assert op == UnaryOps.LNEG;

        // EⱵe:bool  ==>  EⱵ!e:bool
        if (e == TypeTable.boolType)
            return TypeTable.boolType;
        else
            throw new SemanticalError("Can't apply unary op " + op.getOperatorString() , unaryOp.getLine());
    }

    public Object visit(Program program) {
        doAccept(program.getClasses());
        return null;
    }

    // The symbol builder already cheched legal overrides and stuff like that
    public Object visit(ICClass icClass) {
        doAccept(icClass.getFields());
        doAccept(icClass.getMethods());
        return null;
    }

    // The symbol builder already checked the type exists
    // Fields have no initializer - one less check to do
    public Object visit(Field field) {
        return null;
    }

    // The symbol builder already checked that the argument types are ok, and
    // that if there is an override, it's OK
    public Object visit(VirtualMethod method) {
        // No need to visit the type - it's legal if the table builder aprooved
        // For the same reason, no need to visit the formals
        doAccept(method.getStatements());
        return null;
    }

    public Object visit(StaticMethod method) {
        // No need to visit the type - it's legal if the table builder aprooved
        // For the same reason, no need to visit the formals
        doAccept(method.getStatements());
        return null;
    }

    public Object visit(LibraryMethod method) {
        // No need to visit the type - it's legal if the table builder aprooved
        // For the same reason, no need to visit the formals
        doAccept(method.getStatements());
        return null;
    }

    public Object visit(Formal formal) {
        // Should never be reached, and the type is legal if we passed the table
        // builder succesfully
        return null;
    }

    public Object visit(PrimitiveType type) {
        return TypeTable.getType(type);
    }

    public Object visit(UserType type) {
        return TypeTable.getType(type);
    }

    public Object visit(Assignment assignment) {
        Type varType = (Type) assignment.getVariable().accept(this);
        Type valType = (Type) assignment.getAssignment().accept(this);
        if (!valType.subtypeof(varType))
            throw new SemanticalError("Can't assign " + valType + " to " + varType, assignment.getLine());
        else
            return null;
    }

    public Object visit(CallStatement callStatement) {
        // A statement has no type
        callStatement.getCall().accept(this);
        return null;
    }

    public Object visit(Return returnStatement) {
        Type retType = returnStatement.enclosingScope().commnLookup("$ret").getType();
        if (returnStatement.hasValue()) {
            Type valType = (Type) returnStatement.getValue().accept(this);
            if (retType == TypeTable.voidType) {
                throw new SemanticalError("Can't return a value from a"
                        + " void-type function!", returnStatement.getLine());
            } else if (!valType.subtypeof(retType)) {
                throw new SemanticalError("Can't return a " + valType + "from "
                        + "a " + retType + " function!", returnStatement.getLine());
            }
        } else if (retType != TypeTable.voidType) {
            throw new SemanticalError("Must return a value!", returnStatement.getLine());
        }
        return null;
    }

    /*
     * EⱵe:bool, EⱵS1        ==>  if (e) S1
     * EⱵe:bool, EⱵS1, EⱵS2  ==>  if (e) S1 else S2
     */
    public Object visit(If ifStatement) {
        if (ifStatement.getCondition().accept(this) != TypeTable.boolType) {
            throw new SemanticalError("If Condition must evaluate to bool!",
                    ifStatement.getCondition().getLine());
        }

        // Do type checking for the operation
        ifStatement.getOperation().accept(this);

        // Do type checking for the else operation
        if (ifStatement.hasElse()) {
            ifStatement.getElseOperation().accept(this);
        }

        // If statements have no type, so don't return any
        return null;
    }

    /*
     * EⱵe:bool, EⱵS  ==>  while (e) S
     */
    public Object visit(While whileStatement) {
        if (whileStatement.getCondition().accept(this) != TypeTable.boolType) {
            throw new SemanticalError("While Condition must evaluate to bool!",
                    whileStatement.getCondition().getLine());
        }

        // Do type checking for the operation
        whileStatement.getOperation().accept(this);

        // While statements have no type, so don't return any
        return null;
    }

    /*
     * EⱵbreak;
     */
    public Object visit(Break breakStatement) {
        // Break statements have no type, so don't return any
        return null;
    }

    /*
     * EⱵcontinue;
     */
    public Object visit(Continue continueStatement) {
        // Continue statements have no type, so don't return any
        return null;
    }

    public Object visit(StatementsBlock statementsBlock) {
        // Do type checking for the enclosed statements
        if (statementsBlock.getStatements() != null) {
            for (Statement statement : statementsBlock.getStatements()) {
                statement.accept(this);
            }
        }

        // Block statements have no type, so don't return any
        return null;
    }

    /*
     * EⱵe:T0, T0≤T, E,x:TⱵS  ==>  EⱵT x = e; T
     * E,x:TⱵS  ==>  EⱵT x;
     *
     * And now in plain English - make sure a variable is defined before it's,
     * used, and make sure that if it has an initiali value, that value would be
     * of a matching type!
     */
    public Object visit(LocalVariable localVariable)
    {
        Type T = (Type) localVariable.getType().accept(this);

        // Validate the type of the initial value
        if (localVariable.hasInitValue()) {
            Type T0 = (Type) localVariable.getInitValue().accept(this);
            if (!T0.subtypeof(T)) {
                throw new SemanticalError("Type of initial value mismatches the "
                        + "declaration!", localVariable.getLine());
            }
        }

        // Variable declarations have no type, so don't return any
        return null;
    }

    /*
     * EⱵe:T0, T0≤T, E,x:TⱵS  ==>  EⱵT x = e; T
     * E,x:TⱵS  ==>  EⱵT x;
     *
     * And now in plain English - make sure a variable is defined before it's,
     * used, and make sure that if it has an initiali value, that value would be
     * of a matching type!
     */
    public Object visit(VariableLocation location) {
        if (location.isExternal()) {
            Type objType = (Type) location.getLocation().accept(this);
            if (! (objType instanceof ClassType))
                throw new SemanticalError(objType + " is not a class type, so "
                        + " you can't access it's fields!", location.getLocation().getLine());
            else {
                ClassSymbolTable cst = classSyms.get(((ClassType)objType).getClassName());
                // Lookup will throw an error if the field will not be found
                return cst.lookupField(location.getName(), false, location.getLine()).getType();
            }
        } else { // We made sure the enclosing scope of a non-external field will
                 // be the scope in which it's defined
            return location.enclosingScope().depth0Lookup(location.getName()).getType();
        }
    }

    public Object visit(ArrayLocation location) {
        Type indexType = (Type) location.getIndex().accept(this);
        Type locationType = (Type) location.getArray().accept(this);

        if (!indexType.subtypeof(TypeTable.intType))
            throw new SemanticalError("Array index must be an integer! Found "
                    + indexType, location.getIndex().getLine());

        else if(!(locationType instanceof ArrayType))
            throw new SemanticalError("Can't reference non-array type "
                    + locationType, location.getArray().getLine());

        else
            return ((ArrayType)locationType).getElemType();
    }

    // The symbol builder gaurantees the method exists, otherwise an error would
    // have been thrown there
    public Object visit(StaticCall call) {
        //MethodType funcType = TypeTable.getMethodType(call.getClassName(), call.getName(), call.getLine());
        MethodType funcType = (MethodType) call.enclosingScope().staticLookup(call.getName()).getType();
        int neededArgs = funcType.getParamCount(), hasArgs = call.getArguments().size();

        if (neededArgs != hasArgs)
            throw new SemanticalError(funcType + " Expected " + neededArgs
                    + " args, got " + hasArgs, call.getLine());

        Type[] needParamTypes = funcType.getParamTypes();
        Type[] realArgs = new Type[neededArgs];

        for (int i = 0; i < realArgs.length; i++) {
            realArgs[i] = (Type) call.getArguments().get(i).accept(this);
        }

        for (int i = 0; i < neededArgs; i++) {
            if (!realArgs[i].subtypeof(needParamTypes[i]))
                throw new SemanticalError(call.getName() + " expects "
                        + TypeTable.makeTypeListString(needParamTypes)
                        + ", however it recieved "
                        + TypeTable.makeTypeListString(realArgs),
                        + call.getLine());
        }

        return funcType.getReturnType();
    }

    // The symbol builder gaurantees that if the location is empty (i.e. calling
    // a virtual function of the current class) it will be defined
    public Object visit(VirtualCall call) {
        Type locationType;

        if (call.isExternal()) {
            locationType = (Type) call.getLocation().accept(this);
            if (!(locationType instanceof ClassType))
                throw new SemanticalError("Can't reference non-object type "
                        + locationType, call.getLocation().getLine());
        }
        else
            locationType = call.enclosingScope().virtualLookup("this").getType();

        call.setEnclosingScope(classSyms.get(((ClassType)locationType).getClassName()));
        // Now, find the class in which the function was defined
        if (call.enclosingScope().virtualLookup(call.getName()) == null
                || !call.enclosingScope().virtualLookup(call.getName()).getKind().isMethod())
            throw new SemanticalError("No such virtual method " + call.getName() + " for type " + locationType, call.getLine());

        Symbol s = call.enclosingScope().depth0Lookup(call.getName());
        while (s == null || ! s.getKind().isVirtual() || ! s.getKind().isMethod()) {
            call.setEnclosingScope(call.enclosingScope().getParentSymbolTable());
            s = call.enclosingScope().depth0Lookup(call.getName());
        }

        String className = ((ClassSymbolTable)call.enclosingScope()).getICClass().getName();
        MethodType funcType = TypeTable.getMethodType(className, call.getName(), call.getLine());
        int neededArgs = funcType.getParamCount(), hasArgs = call.getArguments().size();

        if (neededArgs != hasArgs)
            throw new SemanticalError(funcType + " Expected " + neededArgs
                    + " args, got " + hasArgs, call.getLine());

        Type[] needParamTypes = funcType.getParamTypes();
        Type[] realArgs = new Type[neededArgs];

        for (int i = 0; i < realArgs.length; i++) {
            realArgs[i] = (Type) call.getArguments().get(i).accept(this);
        }

        for (int i = 0; i < neededArgs; i++) {
            if (!realArgs[i].subtypeof(needParamTypes[i]))
                throw new SemanticalError(call.getName() + " expects "
                        + TypeTable.makeTypeListString(needParamTypes)
                        + ", however it recieved "
                        + TypeTable.makeTypeListString(realArgs),
                        + call.getLine());
        }

        return funcType.getReturnType();
    }

    public Object visit(This thisExpression) {
        return thisExpression.enclosingScope().virtualLookup("this").getType();
    }

    public Object visit(NewClass newClass) {
        return TypeTable.getClassType(newClass.getName(), newClass.getLine());
    }

    public Object visit(NewArray newArray) {
        if (newArray.getSize().accept(this) == TypeTable.intType)
            return TypeTable.getArrayType((Type) newArray.getType().accept(this));
        else
            throw new SemanticalError("Array length must be an integer!", newArray.getLine());
    }

    public Object visit(Length length) {
        if ((Type)(length.getArray().accept(this)) instanceof ArrayType)
            return TypeTable.intType;
        else
            throw new SemanticalError("Can't retrieve the length of non array type!", length.getLine());
    }


    public Object visit(ExpressionBlock expressionBlock) {
        return expressionBlock.getExpression().accept(this);
    }

}
