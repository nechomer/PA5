package IC.SemanticChecks2;

import IC.AST.ASTNode;
import IC.AST.ArrayLocation;
import IC.AST.Assignment;
import IC.AST.Break;
import IC.AST.CallStatement;
import IC.AST.Continue;
import IC.AST.ExpressionBlock;
import IC.AST.Field;
import IC.AST.Formal;
import IC.AST.ICClass;
import IC.AST.If;
import IC.AST.Length;
import IC.AST.LibraryMethod;
import IC.AST.Literal;
import IC.AST.LocalVariable;
import IC.AST.LogicalBinaryOp;
import IC.AST.LogicalUnaryOp;
import IC.AST.MathBinaryOp;
import IC.AST.MathUnaryOp;
import IC.AST.Method;
import IC.AST.NewArray;
import IC.AST.NewClass;
import IC.AST.PrimitiveType;
import IC.AST.Program;
import IC.AST.Return;
import IC.AST.Statement;
import IC.AST.StatementsBlock;
import IC.AST.StaticCall;
import IC.AST.StaticMethod;
import IC.AST.This;
import IC.AST.UserType;
import IC.AST.VariableLocation;
import IC.AST.VirtualCall;
import IC.AST.VirtualMethod;
import IC.AST.Visitor;
import IC.AST.While;
import IC.SemanticalError;
import IC.SymbolTable.BlockSymbolTable;
import IC.SymbolTable.ClassSymbolTable;
import IC.SymbolTable.GlobalSymbolTable;
import IC.SymbolTable.Kind;
import IC.SymbolTable.MethodSymbolTable;
import IC.SymbolTable.Symbol;
import IC.Type.Type;
import IC.Type.TypeTable;
import java.util.HashMap;

/**
 * This class builds the symbol table tree for the program. In addition, it does
 * just a bit of type checking.
 *
 * Before traversing the AST tree, we do the following inside the program node:
 *
 * - For each class in the program
 *   - Define a type matching it
 *   - For each method in the class, define a type for it and add the method to
 *     the class symbol table. Make sure that if we override a method, the
 *     override will be legal.
 *   - For each field in the class, add it to the class symbol table
 *
 *  After these steps are done, we start visiting recursivly all the AST nodes.
 *
 * Notes:
 * As it was pointed out (in the forum), we may choose what to do with virtual
 * identifiers that conflict with static identifiers (when both are not in the
 * class - if they are in the same class, We were told that it's illegal). We
 * chose to allow these to co-exist as long as they are not in the same class.
 * Note however the if A has foo as a static function,
 *
 * @author Barak Itkin & Nimrod Rapopport
 */
public class TableBuilderVisitor implements Visitor {

    public static void setScope(ASTNode from, ASTNode... to) {
        for (ASTNode aSTNode : to) {
            if (aSTNode != null)
                aSTNode.setEnclosingScope(from.enclosingScope());
        }
    }

    public static void setScope(ASTNode from, Iterable<? extends ASTNode> to) {
        for (ASTNode aSTNode : to) {
            if (aSTNode != null)
                aSTNode.setEnclosingScope(from.enclosingScope());
        }
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

    protected GlobalSymbolTable currentParse;
    protected HashMap<String, ClassSymbolTable> classSyms;

    public HashMap<String, ClassSymbolTable> getClassSyms() {
        return classSyms;
    }

    /**
     * @param program
     * @return The global symbol table
     */
    public Object visit(Program program) {
        currentParse = new GlobalSymbolTable("$GLOB");
        classSyms = new HashMap<String, ClassSymbolTable>();
        // For each class in the program
        for (ICClass cls : program.getClasses()) {
            String spr = cls.getSuperClassName();

            // If it has a super class, which is not yet defined, throw an error
            if (spr != null && TypeTable.getClassType(spr, cls.getLine()) == null)
                throw new SemanticalError("Can't define the class " + cls.getName()
                    + " extending " + spr + ", since the class "
                    + spr + " wasn't defined yet!", cls.getLine());

            // Otherwise, define a type for that class
            // If there is already a definition, the type table will warn
            Type t = TypeTable.defineClassType(cls.getName(), cls.getSuperClassName(), cls);

            // Afterwards, add the class to the global symbol table
            currentParse.define(new Symbol(cls.getName(), t, Kind.CLASS), cls.getLine());
        }

        // Now, before really visiting in the classes, define their fields and
        // methods, while creating the class symbol tables
        for (ICClass cls : program.getClasses()) {
            ClassSymbolTable cst = new ClassSymbolTable(cls.getName(), cls);

            // The parent scope is the global table if we have no super class
            // If we have a super class, it's symbol table is the parent
            if (!cls.hasSuperClass()) {
                cst.setParentSymbolTable(currentParse);
                cls.setEnclosingScope(currentParse);
            } else {
                cst.setParentSymbolTable(classSyms.get(cls.getSuperClassName()));
                cls.setEnclosingScope(classSyms.get(cls.getSuperClassName()));
            }
            
            classSyms.put(cls.getName(), cst);

            for (Method method : cls.getMethods()) {
                method.setEnclosingScope(cst); // Important! This line must be first!
                cst.defineMethod(method, cls.getName()); // Checks for illegal overrides!
                TypeTable.defineMethodType(cls.getName(), method);
            }
            for (Field field : cls.getFields()) {
                field.setEnclosingScope(cst); // Important! This line must be first!
                cst.defineField(field);
            }
        }

        // Now, really visit classes
        for (ICClass cls : program.getClasses()) {
            cls.accept(this);
        }

        return currentParse;
    }

    // TODO: finish me
    // We already set the scope when visiting the program
    public Object visit(ICClass icClass) {
        doAccept(icClass.getFields());
        doAccept(icClass.getMethods());
        return null;
    }

    // the field was added to the symbol table when visiting the class
    public Object visit(Field field) {
        TypeTable.getType(field.getType()); // throws an error if the type is undefined
        return null;
    }

    // the function was added to the symbol table when visiting the class
    public Object visit(VirtualMethod method) {
        String clsName = method.enclosingScope().getId();
        // Create a new symbol table and point it's parent symbol table
        MethodSymbolTable mst = new MethodSymbolTable(clsName + "." + method.getName(), false, method);
        mst.setParentSymbolTable(method.enclosingScope());

        // Add the formals to the symbol table, both for correctness and to prevent
        // shadowing
        for (Formal fml : method.getFormals()) { // TypeTable.getType checks for existance
            mst.define(new Symbol(fml.getName(), TypeTable.getType(fml.getType()), Kind.PARAMETER), method.getLine());
        }

        // Add the "this" parameter
        mst.define(new Symbol("this", TypeTable.getClassType(clsName, method.getLine()), Kind.VAR), method.getLine());

        mst.define(new Symbol("$ret", TypeTable.getType(method.getType()), Kind.VAR), method.getLine());

        // Visit the statements and check their symbolic correctness
        for (Statement stmnt : method.getStatements()) {
            stmnt.setEnclosingScope(mst); // Important! This line must be first!
            stmnt.accept(this);
        }
        return mst;
    }

    // the function was added to the symbol table when visiting the class
    public Object visit(StaticMethod method) {
        String clsName = method.enclosingScope().getId();
        // Create a new symbol table and point it's parent symbol table
        MethodSymbolTable mst = new MethodSymbolTable(clsName + "." + method.getName(), true, method);
        mst.setParentSymbolTable(method.enclosingScope());

        // Add the formals to the symbol table, both for correctness and to prevent
        // shadowing
        for (Formal formal : method.getFormals()) { // TypeTable.getType checks for existance
            mst.define(new Symbol(formal.getName(), TypeTable.getType(formal.getType()), Kind.PARAMETER), method.getLine());
        }

        mst.define(new Symbol("$ret", TypeTable.getType(method.getType()), Kind.VAR), method.getLine());

        // Visit the statements and check their symbolic correctness
        for (Statement stmnt : method.getStatements()) {
            stmnt.setEnclosingScope(mst); // Important! This line must be first!
            stmnt.accept(this);
        }
        return mst;
    }

    // the function was added to the symbol table when visiting the class
    // Almost a copy of static method
    public Object visit(LibraryMethod method) {
        String clsName = method.enclosingScope().getId();
        // Create a new symbol table and point it's parent symbol table
        MethodSymbolTable mst = new MethodSymbolTable(clsName + "." + method.getName(), true, method);
        mst.setParentSymbolTable(method.enclosingScope());

        // Add the formals to the symbol table, both for correctness and to prevent
        // shadowing
        for (Formal formal : method.getFormals()) { // TypeTable.getType checks for existance
            mst.define(new Symbol(formal.getName(), TypeTable.getType(formal.getType()), Kind.PARAMETER), method.getLine());
        }

        mst.define(new Symbol("$ret", TypeTable.getType(method.getType()), Kind.VAR), method.getLine());

        return mst;
    }

    public Object visit(Formal formal) { // NEVER REACHED
        TypeTable.getType(formal.getType()); // throws an error if needed
        return null;
    }

    public Object visit(PrimitiveType type) { // NOTHING TO DO
        return null;
    }

    public Object visit(UserType type) {
        if (currentParse.lookup(type.getName(), type.getLine()) == null)
            throw new SemanticalError("No such type " + type.getName(), type.getLine());
        return null;
    }

    public Object visit(Assignment assignment) {
        setScope (assignment, assignment.getAssignment(), assignment.getVariable());
        doAccept(assignment.getAssignment(), assignment.getVariable());
        return null;
    }

    public Object visit(CallStatement callStatement) {
        setScope(callStatement, callStatement.getCall());
        doAccept(callStatement.getCall());
        return null;
    }

    public Object visit(Return returnStatement) {
        setScope(returnStatement, returnStatement.getValue());
        doAccept(returnStatement.getValue());
        return null;
    }

    public Object visit(If ifStatement) {
        setScope(ifStatement, ifStatement.getCondition(), ifStatement.getOperation(), ifStatement.getElseOperation());
        doAccept(ifStatement.getCondition(), ifStatement.getOperation(), ifStatement.getElseOperation());
        return null;
    }

    public Object visit(While whileStatement) {
        setScope(whileStatement, whileStatement.getCondition(), whileStatement.getOperation());
        doAccept(whileStatement.getCondition(), whileStatement.getOperation());
        return null;
    }

    public Object visit(Break breakStatement) {
        return null;
    }

    public Object visit(Continue continueStatement) {
        return null;
    }

    public Object visit(StatementsBlock statementsBlock) {
        BlockSymbolTable bst = new BlockSymbolTable((MethodSymbolTable)statementsBlock.enclosingScope());
        bst.setParentSymbolTable(statementsBlock.enclosingScope());
        for (Statement stmnt : statementsBlock.getStatements()) {
            stmnt.setEnclosingScope(bst);
        }
//        for (Statement stmnt : statementsBlock.getStatements()) {
//            stmnt.accept(this);
//        }
        doAccept(statementsBlock.getStatements());
        return null;
    }

    public Object visit(LocalVariable localVariable) {
        String name = localVariable.getName();
        setScope (localVariable, localVariable.getInitValue(), localVariable.getType());
        // Order matters - evaluate the value before defining the variable!
        doAccept (localVariable.getInitValue(), localVariable.getType());

        if (localVariable.enclosingScope().depth0Lookup(name) != null)
            throw new SemanticalError(name + " is a variable redefinition in the"
                    + " same scope!", localVariable.getLine());
        else // will throw potential error when the type does not exist
            localVariable.enclosingScope().depth0Define(name, new Symbol(name, TypeTable.getType(localVariable.getType()), Kind.VAR));

        return null;
    }

    /**
     * "getLocation()"."getName()"
     * Note that the type of the location is unknown right now, so we can't
     * check if the name is legal...
     * The only exception is the case of
     * "getName()"
     * in that case we can check immidiatly
     *
     * Now, for the case in which the location is empty, we need to keep some
     * indicator whether this is a variable/parameter from the function scope, or
     * whether it is a field of the class. So in that case, if it's a field, set
     * it's enclosing scope to be the scope in which it's defined.
     *
     * Also, we have the same problem for { int x = 5; { x = 2; int x = 8; } }
     * So eventually, for all variables, set their enclosing scope to be the
     * scope in which they were defined
     * 
     * TODO: Check this in the type checking!
     */
    public Object visit(VariableLocation location) {
        String name = location.getName();
        setScope (location, location.getLocation());
        doAccept (location.getLocation());

        if (!location.isExternal())
        {
            // The lookup function throws an error if not found
            Symbol s = ((MethodSymbolTable)location.enclosingScope()).lookup(name, location.getLine());

            // If it's a field or a local variable, set it's scope to the scope
            // in which it was defined
            while (location.enclosingScope().depth0Lookup(name) == null)
                location.setEnclosingScope(location.enclosingScope().getParentSymbolTable());
        }

        return null;
    }

    /**
     * "location.getArray()"["location.getIndex()"]
     */
    public Object visit(ArrayLocation location) {
        setScope(location, location.getArray(), location.getIndex());
        doAccept(location.getArray(), location.getIndex());
        return null;
    }

    // Change the scope to the scope of the actual function
    public Object visit(StaticCall call) {
        setScope(call, call.getArguments());
        doAccept(call.getArguments());
        
        String funcname = call.getName();

        // The lookup function throws an error if not found
        if (!classSyms.containsKey(call.getClassName()))
                throw new SemanticalError("no such class " + call.getClassName(), call.getLine());
        classSyms.get(call.getClassName()).lookupMethod(funcname, true, call.getLine());

        // Set the scope in which this function is defined
        call.setEnclosingScope(classSyms.get(call.getClassName()));

        Symbol match = call.enclosingScope().depth0Lookup(funcname);
        while (match == null || !match.getKind().isStatic()) {
            call.setEnclosingScope(call.enclosingScope().getParentSymbolTable());
            match = call.enclosingScope().depth0Lookup(funcname);
        }


        return null;
    }

    // TODO: When type checking, set the location to "this" if location == null
    public Object visit(VirtualCall call) {
        setScope(call, call.getArguments());
        doAccept(call.getArguments());

        if (call.isExternal()) {
            setScope(call, call.getLocation());
            doAccept(call.getLocation());
        } else { // No location is like calling the function on "this"
            MethodSymbolTable mst = (MethodSymbolTable) call.enclosingScope();
            // Looking up the name will throw an error if it's not avilable
            if (mst.isStaticScope())
                throw new SemanticalError("Can't call a virtual method directly"
                        + " since we are inside a static function!", call.getLine());
            else
                mst.lookup(call.getName(), call.getLine()); // Will throw an error for undefined functions
        }
        return null;
    }

    // In our semantic checker, we disaproove this in non-virtual functions
    // So actually checking this here is pointless (The code for doing this is
    // provided and commented here in hope that it will make someone happy =])
    public Object visit(This thisExpression) {
//        MethodSymbolTable mst = (MethodSymbolTable) thisExpression.enclosingScope();
//        if (mst.isStaticScope())
//            throw new SemanticalError("\"this\" expression can only be used"
//                    + " inside virtual methods", thisExpression.getLine());
        return null;
    }

    public Object visit(NewClass newClass) {
        if (currentParse.lookup(newClass.getName(), newClass.getLine()) == null)
            throw new SemanticalError("Class " + newClass.getName()
                    + " not found", newClass.getLine());
        else
            return null;
    }

    public Object visit(NewArray newArray) {
        setScope(newArray, newArray.getSize(), newArray.getType());
        doAccept(newArray.getSize(), newArray.getType());
        return null;
    }

    public Object visit(Length length) {
        setScope(length, length.getArray());
        doAccept(length.getArray());
        return null;
    }

    public Object visit(MathBinaryOp binaryOp) {
        setScope(binaryOp, binaryOp.getFirstOperand(), binaryOp.getSecondOperand());
        doAccept(binaryOp.getFirstOperand(), binaryOp.getSecondOperand());
        return null;
    }

    public Object visit(LogicalBinaryOp binaryOp) {
        setScope(binaryOp, binaryOp.getFirstOperand(), binaryOp.getSecondOperand());
        doAccept(binaryOp.getFirstOperand(), binaryOp.getSecondOperand());
        return null;
    }

    public Object visit(MathUnaryOp unaryOp) {
        setScope(unaryOp, unaryOp.getOperand());
        doAccept(unaryOp.getOperand());
        return null;
    }

    public Object visit(LogicalUnaryOp unaryOp) {
        setScope(unaryOp, unaryOp.getOperand());
        doAccept(unaryOp.getOperand());
        return null;
    }

    public Object visit(Literal literal) {
        return null;
    }

    public Object visit(ExpressionBlock expressionBlock) {
        setScope(expressionBlock, expressionBlock.getExpression());
        doAccept(expressionBlock.getExpression());
        return null;
    }

}
