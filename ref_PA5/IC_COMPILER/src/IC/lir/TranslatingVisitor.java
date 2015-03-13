/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IC.lir;

import IC.AST.*;
import IC.SemanticChecks2.TypeCheckingVisitor;
import IC.SymbolTable.ClassSymbolTable;
import IC.Type.MethodType;
import IC.Type.TypeTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Nimrod Rappoport
 */
public class TranslatingVisitor implements Visitor {

    public TypeCheckingVisitor typeC;

    public TranslatingVisitor(TypeCheckingVisitor typeC) {
        this.typeC = typeC;
    }
    /**
     * Use this to pass to the visit function the locations in which you want to
     * save the result.
     * BE WARNED - MEMORY TO MEMORY MOVE IS ILLEGAL. IT'S BEST TO STORE RESULTS
     * AT SOME TEMPORARY REGISTER AND THEN MOVE THEM TO THE RIGHT PLACE, TO
     * AVOID PROBLEMS!
     */
    int target;
    /**
     * Use this to keep the numberof the current loop (for break/continue). Simply
     * push the name of the end label when starting to translate a while loop
     * and pop when finished. Break only peeks at this stack, it does not modify
     * it at all.
     *
     * We can't use a simple counter! This is because then the following program
     * will be screwed:
     *
     * while (...) {
     *     while (...) { }
     *         break;
     * }
     *
     * while label names will be:
     * _while_test_labelX - Before the condition
     * _while_end_labelX - After the body
     */
    LinkedList<Integer> whileStack = new LinkedList<Integer>();
    /**
     * Keep a track on the amount of while loops, so that a number won't be used
     * twice...
     */
    int whileNumber;
    /**
     * Keep a track on the amount of if-else statements, so that a number won't
     * be used twice for labeling...
     */
    int _temp_if_NUMBER;
    int temp_string_NUMBER;
    /******************************************/
    /******************************************/
    // maps a class name to its classLayout
    private Map<String, ClassLayout> classLayout = new HashMap<String, ClassLayout>();
    private List<LirInstruction> stringLiterals = new ArrayList<LirInstruction>(); //program's string literals
    private List<LirInstruction> dispatch = new ArrayList<LirInstruction>(); //program's dispatch labels
    private List<LirInstruction> lirList = new ArrayList<LirInstruction>(); //list of all final instructions
    private String currentClassName = null;
    int _temp_general_NUMBER = 1; // for &&, || statements

    /******************************************/
    /******************************************/
    public Object visit(Program program) {

        // next code creates the class layouts
        for (ICClass icClass : program.getClasses()) {
            ClassLayout currentClassLayout;
            if (icClass.hasSuperClass()) {
                currentClassLayout = new ClassLayout(classLayout.get(icClass.getSuperClassName()), icClass.getName());
            } else {
                currentClassLayout = new ClassLayout(icClass.getName());
            }
            for (Method method : icClass.getMethods()) {
                if (method instanceof VirtualMethod) {
                    currentClassLayout.addMethod(method);
                }
            }
            for (Field field : icClass.getFields()) {
                currentClassLayout.addField(field);
            }

            dispatch.add(currentClassLayout.getLabel());
            classLayout.put(icClass.getName(), currentClassLayout);
        }

        // now have all class layouts, and their dispatch tables
        // we can visit the classes

        for (ICClass icClass : program.getClasses()) {
            icClass.accept(this);
        }


        stringLiterals.add(0, new LirComment("String literals:"));
        dispatch.add(0, new LirComment("Distpach tables:"));
        // append all literals, dispatch tables and code together
        stringLiterals.addAll(dispatch);
        stringLiterals.addAll(lirList);
        return stringLiterals;
    }

    // visit each method
    public Object visit(ICClass icClass) {
        currentClassName = icClass.getName();
        target = 1;
        lirList.add(new LirComment("translating "+currentClassName));
        for (Method method : icClass.getMethods()) {
            method.accept(this);
        }

        return null;
    }

    public Object visit(Field field) {
        //never called?
        return null;
    }

    /*
     * creates a label and translates each of the statements
     * adds a return statement at the end
     */
    public Object visit(VirtualMethod method) {
        // create a label
        lirList.add(new LirLabel(
                Naming.functionLabelName(((ClassSymbolTable)method.enclosingScope()).getICClass().getName(), method.getName()), false, true));
        for (Statement stmt : method.getStatements()) {
            target = 1; // data is saved after each statement, so we won't override registers
            stmt.accept(this);
        }
        // adding a return at the end for void functions. For non -void, will be unreachable
        lirList.add(new LirReturn(-1));
        return null;
    }

    /* 
     * same as above, only adds a special label and exit library call for the main method and
     */
    public Object visit(StaticMethod method) {
        String clsName = method.enclosingScope().getId();   // See the TableBuilderVisitor
        if (isMainMethod(method)) {
            lirList.add(new LirLabel("_ic_main", false));
        }
        // Main has both labels, so the program will start at main but no special code will
        // be needed to call the main function (it for some reason it is called)
        lirList.add(new LirLabel(Naming.functionLabelName(((ClassSymbolTable)method.enclosingScope()).getICClass().getName(), method.getName()), false, true));
        for (Statement stmt : method.getStatements()) {
            stmt.accept(this);
        }
        if (isMainMethod(method)) {
            lirList.add(new LirMove(1, 0)); // R1 =0 ;
            lirList.add(new LirLibCall(LibCall.EXIT, 1, -1, -1)); //Library _exit(0)
        } else {
            lirList.add(new LirReturn(-1));
        }
        // adding a return at the end for void functions. For non -void, will be unreachable
        return null;
    }

    public Object visit(LibraryMethod method) {
        // empty translation
        return null;
    }

    public Object visit(Formal formal) {
        // never called
        return null;
    }

    public Object visit(PrimitiveType type) {
        return null; //shouldn't do anything
    }

    public Object visit(UserType type) {
        return null; //shouldn't do anything
    }


    /*
     * translates assignments
     * there are different cases:
     *    assignment into an array:
     *    * Tr[e1[e2] = e3]
     *      R1 := TR[e3]
     *      R2 := TR[e1]
     *      R3 := TR[e2]
     *      # Library __checkNullRef(R2), Rdummy
     *      # Library __checkArrayAccess(R2, R3), Rdummy
     *      MoveArray R2[R3], R1
     *
     * TR[e1.varName = e2]    (where e1 is not "this')
     * R1:= TR[e2]
     * R2:= TR[e1]
     * # Library __checkNullRef(R2), Rdummy
     * MoveField R1 R2.offsetOfvarName
     *
     * TR[this.varName = e2] or TR[varName = e2] where varName is a field of this object
     * Same like previous, only puts "this" in R2
     *
     * else: TR[varName = e]     (varName must be local)
     *                 R1:=TR[e]
     *                 Move R1 varName
     */
    public Object visit(Assignment assignment) {
        lirList.add(new LirComment("translating assignment"));
        assignment.getAssignment().accept(this);
        if (assignment.getVariable() instanceof ArrayLocation) {
            ArrayLocation arrloc = (ArrayLocation) assignment.getVariable();
            target++;
            arrloc.getArray().accept(this);
            target++;
            arrloc.getIndex().accept(this);
            target -= 2;
            //lirList.add(new LirLibCall(LibCall.NULL_REF, target + 1, -1, -1));
            //lirList.add(new LirLibCall(LibCall.ARRAYACCESS, target + 1, target + 2, -1));
            lirList.add(new LirMoveArray(target + 1, target + 2, target, true));
        } else { //variable location
            VariableLocation varloc = (VariableLocation) assignment.getVariable();
            if (varloc.isExternal() && !(varloc.getLocation() instanceof This)) { // TR[e1.varName = e2]    (where e1 is not "this')
                target++;
                varloc.getLocation().accept(this);
                target--;
                // using the type checker to get the type of the name of the class of e1
                String varClassName = ((IC.Type.ClassType) varloc.getLocation().accept(typeC)).getClassName();
                int offset = classLayout.get(varClassName).getFieldOffset(varloc.getName());
                //lirList.add(new LirLibCall(LibCall.NULL_REF, target+ 1, -1, -1));
                lirList.add(new LirMoveField(target + 1, target, offset, true));
            } else if ((!varloc.isExternal() && varloc.enclosingScope() instanceof ClassSymbolTable)
                    || (varloc.isExternal() && varloc.getLocation() instanceof This)) {
                // Field of this class
                String varClassName = currentClassName;
                int offset = classLayout.get(varClassName).getFieldOffset(varloc.getName());
                lirList.add(new LirMove(target + 1, "this", true));
                lirList.add(new LirMoveField(target + 1, target, offset, true));
            } else {
                lirList.add(new LirMove(target, Naming.varName(varloc), false));
            }
        }
        return null;
    }

    /*
     * simply translates the call statement
     */
    public Object visit(CallStatement callStatement) {
        callStatement.getCall().accept(this);
        return null;
    }


    /* Tr[return e]
     * R1 := Tr[e]
     * Return R1     (Rdummy if has no return value)
     */
    public Object visit(Return returnStatement) {
        if (returnStatement.hasValue()) {
            returnStatement.getValue().accept(this);
            lirList.add(new LirReturn(target));
        } else {
            lirList.add(new LirReturn(-1)); // -1 is dummy register
        }
        return null;
    }

    /* without else:
     * R1 := TR[e]
     *   Compare 0,R1
     *   JumpTrue _end_label
     *  TR[s]
     *   _end_label:
     *
     * R1 := TR[e]
     *  Compare 0,R1
     *  JumpTrue _false_label
     *  TR[s1]
     *  Jump _end_label
     *  _false_label:
     *  TR[s2]
     *  _end_label:
     */
    public Object visit(If ifStatement) {
        lirList.add(new LirComment("translating if statement"));
        int currentIfLabel = this._temp_if_NUMBER++;
        String endLabel = Naming.ifEndLabel(currentIfLabel);
        String falseLabel = Naming.ifFalseLabel(currentIfLabel);

        ifStatement.getCondition().accept(this);
        lirList.add(new LirCompare(0, target, true));
        if (ifStatement.hasElse()) {
            lirList.add(new LirJump(JumpTypes.TRUE, falseLabel));
            ifStatement.getOperation().accept(this);
            lirList.add(new LirJump(null, endLabel));
            lirList.add(new LirLabel(falseLabel, false));
            ifStatement.getElseOperation().accept(this);
        } else {
            lirList.add(new LirJump(JumpTypes.TRUE, endLabel));
            ifStatement.getOperation().accept(this);
        }
        lirList.add(new LirLabel(endLabel, false));
        lirList.add(new LirComment("end of if statement"));
        return null;


    }


    /*
     * _test_label:
     *  R1 := TR[e]
     *  Compare 0,R1
     *  JumpTrue _end_label
     *  TR[s]
     *  Jump _test_label
     *  _end_label
     */
    public Object visit(While whileStatement) {
        lirList.add(new LirComment("translating while statement"));
        int currentWhileLabel = this.whileNumber++;
        String endLabel = Naming.whileEndLabel(currentWhileLabel);
        String testLabel = Naming.whileTestLabel(currentWhileLabel);

        whileStack.addFirst(currentWhileLabel);
        lirList.add(new LirLabel(testLabel, false));
        whileStatement.getCondition().accept(this);
        lirList.add(new LirCompare(0, target, true));
        lirList.add(new LirJump(JumpTypes.TRUE, endLabel));
        whileStatement.getOperation().accept(this);
        lirList.add(new LirJump(null, testLabel));
        lirList.add(new LirLabel(endLabel, false));
        whileStack.removeFirst();
        lirList.add(new LirComment("end of while statement"));
        return null;
    }

    /*
     * jumps to end label of current while loop
     */
    public Object visit(Break breakStatement) {
        lirList.add(new LirJump(null,
                Naming.whileEndLabel(whileStack.getFirst())));
        return null;
    }

    /*
     * jump to test label of current while loop
     */
    public Object visit(Continue continueStatement) {
        lirList.add(new LirJump(null,
                Naming.whileTestLabel(whileStack.getFirst())));
        return null;
    }

    /*
     * translates each statement seperately
     */
    public Object visit(StatementsBlock statementsBlock) {
        for (Statement stmt : statementsBlock.getStatements()) {
            target = 1;
            /* since we save data after each statement, we can always start from 1
             * without losing any data
             */
            stmt.accept(this);
        }
        return null;
    }


    /*
     * if uninitialized, does nothing
     * if has init value:
     * Tr[TYPE varName = init;] :
     * R1 :=Tr[init]
     * Move R1 varName
     */
    public Object visit(LocalVariable localVariable) {
        if (localVariable.hasInitValue()) {
            localVariable.getInitValue().accept(this); // value is now in target
            String varName = Naming.varName(localVariable);
            lirList.add(new LirMove(target, varName, false));// move target to memory
        }
        // if has no init value, then no code should be created
        // was only relevant during semantic analysis
        return null;
    }

    /*
     * different cases:
     * R1:=TR[e.varName]
     * R2:=TR[e]
     * # Library __checkNullRef(R2), Rdummy (if e is not "this")
     * MoveField e.OffsetOfvarName R1
     *
     * R1:=TR[varName]    in the case where varName is a field of this object
     * same translation like this.varName
     *
     * R1:=TR[varName]    in the case where varName is local
     * Move R1 varName
     */
    public Object visit(VariableLocation location) {
        if (location.isExternal()) {    // Field of some object
            target++;
            location.getLocation().accept(this);
            target--;
            String varClass = ((IC.Type.ClassType) location.getLocation().accept(typeC)).getClassName();
            int offset = classLayout.get(varClass).getFieldOffset(location.getName());
            //if (!(location.getLocation() instanceof This)) {
            //    lirList.add(new LirLibCall(LibCall.NULL_REF, target + 1, -1, -1));
            //}
            lirList.add(new LirMoveField(target + 1, target, offset, false));
        } else if (location.enclosingScope() instanceof ClassSymbolTable) { // Field of this
            String varClass = currentClassName;
            int offset = classLayout.get(varClass).getFieldOffset(location.getName());
            lirList.add(new LirMove(target + 1, "this", true));
            lirList.add(new LirMoveField(target + 1, target, offset, false));
        } else { // Simply a local variable (or a formal)
            lirList.add(new LirMove(target, Naming.varName(location), true));
        }
        return null;
    }

    /*
     * R1 : =TR[e1[e2]]
     * R2 := TR[e1]
     * R3: = TR[e2]
     * # __checkNullRef(R2)
     * # __checkArrayAccess(R3)
     * MoveArray R2[R3], R1
     */
    public Object visit(ArrayLocation location) {
        target++;
        location.getArray().accept(this);
        target++;
        location.getIndex().accept(this);
        target -= 2;
        //lirList.add(new LirLibCall(LibCall.NULL_REF, target + 1, -1, -1));
        //lirList.add(new LirLibCall(LibCall.ARRAYACCESS, target + 1, target + 2, -1));
        lirList.add(new LirMoveArray(target + 1, target + 2, target, false));
        return null;
    }

    // See how the type checking visitor visits this - it basically set's the
    // scope of the call (not it's arguments) to be the class in which the call
    // was defined
    public Object visit(StaticCall call) {
        String realClassName = ((ClassSymbolTable)call.enclosingScope()).getICClass().getName();

        lirList.add(new LirComment("begin traslation of static function call " + call.getClassName() + " " + call.getName()));
        LirStaticCall lsc = null;
        if (call.getClassName().equals("Library")) {
            libraryCall(call);
        } else {
            int origTarget = target;
            
            lsc = new LirStaticCall(Naming.functionLabelName(realClassName, call.getName()), target);
            // Now, in order to get the names of the formals, we will lookup the
            // method and find it's type. Since it's a static call, the exact
            // function was already resolved, and since a new type was defined
            // to this method (even it shares a signature with some others) it
            // will contain the right info
            String[] formalNames = ((MethodType) call.enclosingScope().staticLookup(call.getName()).getType()).getParamNames();
            int i = 0;
            for (Expression expr : call.getArguments()) {
                target++;
                // Arguments of main have a fixed name
                // see also varname in naming
                lsc.addFormal(Naming.formalName(formalNames[i++]), target);
                expr.accept(this);
            }
            target = origTarget;
            lirList.add(lsc);
        }
        lirList.add(new LirComment("end of traslation of static function call " + realClassName + " " + call.getName()));
        return null;
    }

    public Object visit(VirtualCall call) {
        int offset;
        int origTarget = target;
        target++;
        lirList.add(new LirComment("begin traslation of virtual function call " + call.getName()));
        if (call.isExternal() && !(call.getLocation() instanceof This)) {
            call.getLocation().accept(this);
            //lirList.add(new LirLibCall(LibCall.NULL_REF, target, -1, -1));
            String funcClass = ((IC.Type.ClassType) call.getLocation().accept(typeC)).getClassName();
            offset = classLayout.get(funcClass).getMethodOffset(call.getName());
        } else {
            lirList.add(new LirMove(target, "this", true));
            offset = classLayout.get(currentClassName).getMethodOffset(call.getName());
        }
        // object is now in target
        LirVirtualCall lsc =
                new LirVirtualCall(offset, target, origTarget);

        // See what we do in static call. Basically it's the same, but it
        // resolves to the lowest ancesstor which has that method name, and that
        // is the best naming we can do for the parameters
        String[] formalNames = ((MethodType) call.enclosingScope().virtualLookup(call.getName()).getType()).getParamNames();
        // works for "this"?
        int i = 0;
        for (Expression expr : call.getArguments()) {
            target++;
            //TODO: Check me!
            lsc.addFormal(Naming.formalName(formalNames[i++]), target);
            expr.accept(this);
        }
        target = origTarget;
        lirList.add(lsc);
        lirList.add(new LirComment("end of traslation of virtual function call " + call.getName()));
        return null;
    }

    /* R1:=TR[this]
     * Move this R1
     */
    public Object visit(This thisExpression) {
        lirList.add(new LirMove(target, "this", true));
        return null;
    }

    /*
     * R1:= TR[new A()]
     * size needed to allocate A
     * Library __allocateObject(size needed to allocate A), R1
     * MoveField _DV_A, R1.0
     */
    public Object visit(NewClass newClass) {
        lirList.add(new LirLibCall(LibCall.ALLOCATE_OBJECT,
                classLayout.get(newClass.getName()).size(), -1, target));
        lirList.add(new LirMoveField(target, Naming.classDV(newClass.getName())));
        return null;
    }

    /* R1 := Tr[new TYPE[e1]]
     *      R2 := TR[e1]
     *      # Library __checkSize(R2), Rdummy
     *      Library __allocateArray(R2), R1
     */
    public Object visit(NewArray newArray) {
        target++;
        newArray.getSize().accept(this);
        target--;
        /*
         * remove the comment from the next two commands for exercise  4 to work alone
         * changed this int exercise 5 because in assembly, array allocation recieves
         the number of elements, not bytes
         */
        //lirList.add(new LirMove(target+2, 4));
        //lirList.add(new LirArithmeticBinary("Mul", target+2, target+1));
        //lirList.add(new LirLibCall(LibCall.CHECKSIZE, target + 1, -1, -1)); // first -1 doesn't matter, second means R dummy
        lirList.add(new LirLibCall(LibCall.ALLOCATE_ARRAY, target + 1, -1, target)); // -1 means doesn't matter
        return null;
    }

    /* R1 := Tr[e1.length]
     *      R2 := TR[e1]
     *      # Library __checkNullRef(R2), Rdummy
     *      ArrayLength R2,R1
     *
     * Where e1 is an array
     * 
     */
    public Object visit(Length length) {
        target++;
        length.getArray().accept(this);
        target--;
        //lirList.add(new LirLibCall(LibCall.NULL_REF, target + 1, -1, -1));
        lirList.add(new LirArrayLength(target, target + 1));
        return null;
    }

    /* R1 := Tr[e1 OP e2]
     *      R1 := TR[e1]
     *      R2 := T[e2]
     *      OP R2,R1
     * 
     * Where OP can be + (Add), - (Sub), * (Mul), / (Div), % (Mod)
     * In case of string concatenation, calls a library function
     */
    public Object visit(MathBinaryOp binaryOp) {
        int R1 = target;
        binaryOp.getFirstOperand().accept(this);
        int R2 = ++target;
        binaryOp.getSecondOperand().accept(this);
        target--;
        String op = null;
        switch (binaryOp.getOperator()) {
            case PLUS:
                if (((IC.Type.Type) binaryOp.getFirstOperand().accept(typeC)) == TypeTable.stringType) {
                    lirList.add(new LirLibCall(LibCall.STRCAT, R1, R2, R1));
                    return null;
                }
                op = "Add";
                break;
            case MINUS:
                op = "Sub";
                break;
            case MULTIPLY:
                op = "Mul";
                break;
            case DIVIDE:
                op = "Div";
                //lirList.add(new LirLibCall(LibCall.CHECKZERO, R2, -1, -1));
                break;
            case MOD:
                //lirList.add(new LirLibCall(LibCall.CHECKZERO, R2, -1, -1));
                op = "Mod";
                break;
        }
        lirList.add(new LirArithmeticBinary(op, R2, R1));
        return null;
    }

    /* R1 := Tr[e1 || e2]
     *      R1 := TR[e1]
     *      Compare 1,R1
     *      JumpTrue _end_label
     *      R2 := T[e2]
     *      Or R2,R1
     *      _end_label:
     * 
     * R1 := Tr[e1 && e2]
     *      R1 := TR[e1]
     *      Compare 0,R1
     *      JumpTrue _end_label
     *      R2 := T[e2]
     *      And R2,R1
     *      _end_label:
     *
     * R1 := Tr[e1 == e2]
     *      R2 := TR[e1]
     *      R3 := TR[e2]
     *      Move 1, R1
     *      Compare R2, R3
     *      JumpTrue _l1
     *      Move 0, R1
     *      _l1:
     * 
     * Similar for <= >= != < >, by replacing the jump command
     */
    public Object visit(LogicalBinaryOp binaryOp) {

        lirList.add(new LirComment("begin traslation of binary logical operation" + binaryOp.getOperator().toString()));
        switch (binaryOp.getOperator()) {
            case LAND:
                translateOrAnd(binaryOp, false);
                break;
            case LOR:
                translateOrAnd(binaryOp, true);
                break;
            default: {
                String endLabel = Naming.generalEndLabel(_temp_general_NUMBER++);
                target++;
                binaryOp.getFirstOperand().accept(this);
                target++;
                binaryOp.getSecondOperand().accept(this);
                target -= 2;

                lirList.add(new LirMove(target, 1));
                lirList.add(new LirCompare(target + 1, target + 2, false));
                switch (binaryOp.getOperator()) {
                    case EQUAL:
                        lirList.add(new LirJump(JumpTypes.TRUE, endLabel));
                        break;
                    case NEQUAL:
                        lirList.add(new LirJump(JumpTypes.FALSE, endLabel));
                        break;
                    case GT:
                        lirList.add(new LirJump(JumpTypes.L, endLabel));
                        break;
                    case GTE:
                        lirList.add(new LirJump(JumpTypes.LE, endLabel));
                        break;
                    case LT:
                        lirList.add(new LirJump(JumpTypes.G, endLabel));
                        break;
                    case LTE:
                        lirList.add(new LirJump(JumpTypes.GE, endLabel));
                        break;

                }
                lirList.add(new LirMove(target, 0));
                lirList.add(new LirLabel(endLabel, false));
            }
        }
        lirList.add(new LirComment("end of traslation of binary logical operation" + binaryOp.getOperator().toString()));
        return null;
    }

    /*
     * does the actual translation of "or" and "and"
     */
    private void translateOrAnd(LogicalBinaryOp binaryOp, boolean isOr) {
        String endLabel = Naming.generalEndLabel(_temp_general_NUMBER++);
        binaryOp.getFirstOperand().accept(this);
        lirList.add(new LirCompare((isOr ? 1 : 0), target, true));
        lirList.add(new LirJump(JumpTypes.TRUE, endLabel));
        target++;
        binaryOp.getSecondOperand().accept(this);
        target--;
        if (isOr) {
            lirList.add(new LirLogicalBinary("Or", target+1, target));
        } else {
            lirList.add(new LirLogicalBinary("And", target+1, target));
        }
        lirList.add(new LirLabel(endLabel, false));
    }

    /* R1 := Tr[- e1]
     *      R1 := TR[e1]
     *      Neg R1
     */
    public Object visit(MathUnaryOp unaryOp) {
        unaryOp.getOperand().accept(this);
        lirList.add(new LirUnaryOp(true, target));
        return null;
    }

    /* R1 := Tr[! e1]
     *      R1 := TR[e1]
     *      Not R1
     *
     * NOTE: The Lir Not instruction seems to work sometimes and sometimes, well, not.
     * An alternative translation could be maybe Sub 1,R1 (if true is 1 and 0 is false)
     */
    public Object visit(LogicalUnaryOp unaryOp) {
        unaryOp.getOperand().accept(this);
        lirList.add(new LirUnaryOp(false, target));
        return null;
    }

    /* For a literal string l1 (kept in the label strXXX)
     * R1 := Tr[l1]
     *      Move strXXX, R1
     *
     * For a literal number n1
     * R1 := Tr[n1]
     *      Move n1, R1
     *
     * For a the literal boolean b1 with true:
     * R1 := Tr[b1]
     *      Move 1, R1
     *
     * For a the literal boolean b1 with false:
     * R1 := Tr[b1]
     *      Move 0, R1
     */
    public Object visit(Literal literal) {
        String strAddress;
        switch (literal.getType()) {
            case FALSE:
                lirList.add(new LirMove(target, 0));
                break;
            case TRUE:
                lirList.add(new LirMove(target, 1));
                break;
            case INTEGER:
                lirList.add(new LirMove(target, (Integer) literal.getValue()));
                break;
            case STRING:
                if ((strAddress = get((String) literal.getValue())) == null) {
                    this.stringLiterals.add(new LirLabel(Naming.stringLabel(stringLiterals.size() + 1),
                            (String) literal.getValue()));
                    lirList.add(new LirMove(target, Naming.stringLabel(stringLiterals.size()), true));
                } else {
                    lirList.add(new LirMove(target, strAddress, true));
                }
                break;
            case NULL:
                lirList.add(new LirMove(target, 0)); // null is 0
                break;
        }
        return null;
    }

    /*
     * R1:= TR[(e)]
     * R1:=TR[e]
     */
    public Object visit(ExpressionBlock expressionBlock) {
        expressionBlock.getExpression().accept(this);
        return null;
    }

    /*
     * returns the name of the label that fits to the string literal that is the parameter, null if none exists so far
     */
    private String get(String string) {
        int i = 0;
        for (LirInstruction ins : stringLiterals) {
            i++;
            if (((LirLabel) ins).getStringLiteral().equals(string)) {
                return Naming.stringLabel (i);
            }
        }
        return null;
    }

    /*
     * Calls a library function, by translating the parameters and creating the right library call
     */
    private void libraryCall(StaticCall call) {
        int originalTarget = target;
        for (Expression expr : call.getArguments()) {
            target++;
            expr.accept(this);
        }
        target = originalTarget;
        if (call.getName().equals("println")) {
            lirList.add(new LirLibCall(LibCall.PRINTLN, target + 1, -1, -1));
        } else if (call.getName().equals("print")) {
            lirList.add(new LirLibCall(LibCall.PRINT, target + 1, -1, -1));
        } else if (call.getName().equals("printi")) {
            lirList.add(new LirLibCall(LibCall.PRINTI, target + 1, -1, -1));
        } else if (call.getName().equals("printb")) {
            lirList.add(new LirLibCall(LibCall.PRINTB, target + 1, -1, -1));
        } else if (call.getName().equals("readi")) {
            lirList.add(new LirLibCall(LibCall.READI, -1, -1, target));
        } else if (call.getName().equals("readln")) {
            lirList.add(new LirLibCall(LibCall.READLN, -1, -1, target));
        } else if (call.getName().equals("eof")) {
            lirList.add(new LirLibCall(LibCall.EOF, -1, -1, target));
        } else if (call.getName().equals("stoi")) {
            lirList.add(new LirLibCall(LibCall.STOI, target + 1, target + 2, target));
        } else if (call.getName().equals("itos")) {
            lirList.add(new LirLibCall(LibCall.ITOS, target + 1, -1, target));
        } else if (call.getName().equals("stoa")) {
            lirList.add(new LirLibCall(LibCall.STOA, target + 1, -1, target));
        } else if (call.getName().equals("atos")) {
            lirList.add(new LirLibCall(LibCall.ATOS, target + 1, -1, target));
        } else if (call.getName().equals("random")) {
            lirList.add(new LirLibCall(LibCall.RANDOM, target + 1, -1, target));
        } else if (call.getName().equals("time")) {
            lirList.add(new LirLibCall(LibCall.TIME, -1, -1, target));
        } else if (call.getName().equals("exit")) {
            lirList.add(new LirLibCall(LibCall.EXIT, target + 1, -1, -1));
        } else {
            System.err.println("unsupported library call");
        }
    }

    /*
     * Checks whether a given method is the main method for the program
     */
    private boolean isMainMethod(StaticMethod method) {
        // name is main, one argument, return type void and parameter is an array of strings
        if (method.getName().equals("main") && method.getFormals().size() == 1
                && (TypeTable.getType(method.getType()) == TypeTable.voidType)
                && (TypeTable.getType(method.getFormals().get(0).getType())
                == TypeTable.getArrayType(TypeTable.stringType))) {
            return true;
        }
        return false;
    }
}
