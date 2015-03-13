/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IC.asm;

import IC.lir.LibCall;
import IC.lir.LirAbstractCall;
import IC.lir.LirArithmeticBinary;
import IC.lir.LirArrayLength;
import IC.lir.LirComment;
import IC.lir.LirCompare;
import IC.lir.LirInstruction;
import IC.lir.LirJump;
import IC.lir.LirLabel;
import IC.lir.LirLibCall;
import IC.lir.LirLogicalBinary;
import IC.lir.LirMove;
import IC.lir.LirMoveArray;
import IC.lir.LirMoveField;
import IC.lir.LirReturn;
import IC.lir.LirStaticCall;
import IC.lir.LirUnaryOp;
import IC.lir.LirVirtualCall;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Nimrod Rappoport
 * This visitor will do the actual translating from Lir to assembly
 */
public class AssemblingVisitor implements LIRVisitor {

    // currentMethod and layouts are like in the builder
    // assemblyProgram is the program we are writing
    private MethodLayout currentMethod;
    private Map<String, MethodLayout> layouts;
    private StringBuffer assemblyProgram = new StringBuffer();
    private boolean isFirstMethod = true;
    private String icFileName;
    private String currentMethodLabelName;
    private boolean translatingMain = false;

    public AssemblingVisitor(Map<String, MethodLayout> layouts, String icFileName) {
        this.layouts = layouts;
        this.icFileName = icFileName;
    }

    private void emit(String str) {
        assemblyProgram.append(str).append("\n");
    }

    public String getProgramString() {
        return assemblyProgram.toString();
    }
    /*
     * this is the method the compiler class will call.
     * It will translate by translating each lir instruction
     */

    public Object translate(List<LirInstruction> program) {
        emit(".title    \"" + icFileName + "\"");
        emit("# global declaratrions");
        emit(".global __ic_main");
        emit("# data section");
        emit(".data");
        emit("\t.align 4");
        createErrorStrings();
        for (LirInstruction instruction : program) {
            instruction.accept(this);
        }
        // an epilogue for the last function
        emit("# epilogue");
        emit(currentMethodLabelName + "_epilogue:");
        emit("mov %ebp, %esp");
        emit("pop %ebp");
        emit("ret");
        createRuntimeHandlers();
        return null;
    }

    /*
     * a is R1's offset, b is R2's offset, then:
     * TR[Add R1, R2]    
     * mov a(%ebp), %eax
     * add b(%ebp), %eax 
     * mov %eax b(%ebp)
     *
     *  TR[Sub R1, R2]
     * mov b(%ebp), %eax
     * sub a(%ebp), %eax
     * mov %eax, b(%ebp)
     *
     * TR[Mul R1, R2]
     * mov b(%ebp), %eax
     * imul a(%ebp), %eax
     * mov %eax, b(%ebp)
     *
     * TR[Div R1, R2]       (Mod R1, R2)
     * mov $0, %edx
     * mov b(%ebp), %eax
     * mov a(%ebp), %ebx
     * cmp $0, %eax
     * je labelDBE
     * idiv %ebx
     * mov %eax,  b(%ebp)       (mov %edx,  b(%ebp))
     */
    public Object visit(LirArithmeticBinary lab) {
        int firstOffset = currentMethod.getVarOffset("R" + lab.getParam1());
        int secondOffset = currentMethod.getVarOffset("R" + lab.getParam2());
        String op = lab.getOp();
        if (op.equals("Add")) {
            emit("mov " + firstOffset + "(%ebp), %eax");
            emit("add " + secondOffset + "(%ebp), %eax");
            emit("movl %eax, " + secondOffset + "(%ebp)");
        } else if (op.equals("Sub")) {
            emit("mov " + secondOffset + "(%ebp), %eax");
            emit("sub " + firstOffset + "(%ebp), %eax");
            emit("movl %eax, " + secondOffset + "(%ebp)");
        } else if (op.equals("Mul")) {
            emit("mov " + secondOffset + "(%ebp), %eax");
            emit("imul " + firstOffset + "(%ebp), %eax");
            emit("movl %eax, " + secondOffset + "(%ebp)");
        } else { // division or mod
            emit("mov $0, %edx");
            emit("mov " + secondOffset + "(%ebp), %eax");
            emit("mov " + firstOffset + "(%ebp), %ebx");
            // division by zero check
            emit("cmp $0, %eax");
            emit("je " + Info.ErrorMessages.DivisionByZero.getFunctionLabel());
            // end of division by zero check
            emit("idiv %ebx");
            emit("movl " + (op.equals("Div") ? "%eax, " : "%edx, ") + secondOffset + "(%ebp)");
        }
        return null;
    }

    /*
     * a and b are like before
     * TR[ArrayLength R1,R2]
     * mov a(%ebp), %ebx
     * cmp $0, %ebx
     * je labelNPE
     * mov -4(%ebx), %ebx
     * mov %ebx, b(%ebp)
     */
    public Object visit(LirArrayLength lal) {
        int arrayOffset = currentMethod.getVarOffset("R" + lal.getArrayReg());
        int destOffset = currentMethod.getVarOffset("R" + lal.getDestReg());

        emit("mov " + arrayOffset + "(%ebp), %ebx");
        // check array is not null
        emit("cmp $0, %ebx");
        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
        // end of check
        emit("mov -4(%ebx), %ebx");
        emit("movl %ebx, " + destOffset + "(%ebp)");
        return null;

    }

    // leave IC comments in the code
    public Object visit(LirComment lc) {
        emit(lc.toString());
        return null;
    }

    /*
     * TR[Compare R1, R2]
     * mov b(%ebp), %eax
     * cmp a(%ebp), %eax
     *
     * TR[Compare c, R2]   where c is a constant
     * mov b(%ebp), %eax
     * cmp $c, %eax
     */
    public Object visit(LirCompare lc) {
        int secondOffset = currentMethod.getVarOffset("R" + lc.getOp2());
        emit("mov " + secondOffset + "(%ebp), %eax");
        if (lc.isLiteral()) {
            emit("cmp $" + lc.getOp1() + ", %eax");
        } else {
            int firstOffset = currentMethod.getVarOffset("R" + lc.getOp1());
            emit("cmp " + firstOffset + "(%ebp), %eax");
        }
        return null;
    }

    /*
     * TR[jump (or: jumpTrue, jumpFalse, jumpG, jumpGE, jumpL, jumpLE) label]
     * jmp (or: je, jne, jg, jge, jl, jle) label
     */
    public Object visit(LirJump lj) {
        String jumpType = "jmp";
        if (lj.getJumpType() != null) {
            switch (lj.getJumpType()) {
                case TRUE:
                    jumpType = "je";
                    break;
                case FALSE:
                    jumpType = "jne";
                    break;
                case G:
                    jumpType = "jg";
                    break;
                case GE:
                    jumpType = "jge";
                    break;
                case L:
                    jumpType = "jl";
                    break;
                case LE:
                    jumpType = "jle";
                    break;
            }
        }
        emit(jumpType + " " + lj.getJumpTo());
        return null;
    }

    /*for labels with dispatch vectors:
     * TR[_DV_A: [_A_sleep,_A_rise,_A_shine]]   (this is an example)
     * _DV_A:
     *      .long _A_sleep
     *      .long _A_rise
     *      .long _A_shine
     *
     * for labels with string literls:
     * TR[str1: "hello"]
     * str1: .string "hello"
     *
     * for other labels, other then _ic_main, just omit them.
    change current method if the label indicates we start a new function.
     * Finally, turn _ic_main to __ic_main. we need translatingMain so that __ic_main
     * will always appear right before the next label (in our translation, we create two
     * labels for main - _ic_main and a label that follows our naming convention -
     * _Main_main for instance. So they have to appear one after the other.
     */
    public Object visit(LirLabel ll) {
        if (ll.getLabels() != null) { // this label has the dispatch vector of some class
            emit(ll.getLabelName() + ":");
            for (int i = 0; i < ll.getLabels().size(); i++) {
                emit("\t.long " + ll.getLabels().get(i));
            }
        } else if (ll.getText() != null) {
            emit("\t.int " + (ll.getText().length() - 2)); // since we save the ""
            emit(ll.getLabelName() + ": .string " + ll.getText());
        } else {
            if (ll.isIsMethod()) {
                if (isFirstMethod) { // this is the first translated method
                    isFirstMethod = false;
                    emit("# text (code) section");
                    emit(".text");
                    emit("");
                    emit("# -------------------------------");
                    emit("# --- Begining of actual Code ---");
                    emit("# -------------------------------");
                } else { // function epilogue
                    emit("# epilogue");
                    emit(currentMethodLabelName + "_epilogue:");
                    emit("mov %ebp, %esp");
                    emit("pop %ebp");
                    emit("ret");
                }
                // now emit new label
                emit("\t.align 4");
                if (translatingMain) {
                    emit("__ic_main:");
                    translatingMain = false;
                }
                emit(ll.getLabelName() + ": ");
                // function prologue
                emit("# prologue");
                emit("push %ebp");
                emit("mov %esp, %ebp");
                currentMethod = layouts.get(ll.getLabelName());
                currentMethodLabelName = ll.getLabelName();
                emit("sub $" + currentMethod.getStackSize() + ", %esp");
            } else if (ll.getLabelName().equals("_ic_main")) { //  need to write __ic_main
                translatingMain = true;
            } else { // just a simple label
                emit(ll.toString());
            }
        }
        return null;
    }

    /*
     * Same translation like static call (so, see static call)
     * Allocate object is the only function that has a parameter that is
     * not from a register, so it has a special case.
     * In case of array allocation checks that size to allocate is legal.
     * Checks that reference arguments are not null.
     */
    public Object visit(LirLibCall llc) {
        if (llc.getLibcall().getNumArguments() == 2) { // push second argument
            int regOffset = currentMethod.getVarOffset("R" + llc.getOp2());
            emit("mov " + regOffset + "(%ebp), %eax");
            if (llc.getLibcall() == LibCall.STRCAT) {
                emit("cmp $0, %eax");
                emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
            }
            emit("push %eax");
        }


        if (llc.getLibcall().getNumArguments() >= 1) { // push first argument
            if (llc.getLibcall() == LibCall.ALLOCATE_OBJECT) {
                emit("push $" + llc.getOp1());
            } else {
                int regOffset = currentMethod.getVarOffset("R" + llc.getOp1());
                emit("mov " + regOffset + "(%ebp), %eax");
                // have to check whether legal size in case of array allocation
                if (llc.getLibcall() == LibCall.ALLOCATE_ARRAY) {
                    emit("cmp $0, %eax");
                    emit("jle " + Info.ErrorMessages.NegativeAllocation.getFunctionLabel());
                }
                if (llc.getLibcall() == LibCall.ATOS
                        || llc.getLibcall() == LibCall.STOA
                        || llc.getLibcall() == LibCall.STRCAT
                        || llc.getLibcall() == LibCall.STOI
                        || llc.getLibcall() == LibCall.PRINTLN
                        || llc.getLibcall() == LibCall.PRINT) {
                    // check array/string is not null
                    emit("cmp $0, %eax");
                    emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
                }
                emit("push %eax");
            }
        }

        emit("call " + llc.getLibcall().getFuncCall());
        if (llc.getDestRegister() != -1) {
            int destOffset = currentMethod.getVarOffset("R" + llc.getDestRegister());
            emit("movl %eax, " + destOffset + "(%ebp)");
        }
        emit("add $" + 4 * llc.getLibcall().getNumArguments() + ", %esp");
        return null;

    }

    /*TR[Or R1, R2]       (And R1, R2)
     * mov a(%ebp), %eax
     * or b(%ebp), %eax  (and b(%ebp), %eax)
     * mov %eax b(%ebp)
     */
    public Object visit(LirLogicalBinary llb) {
        int firstOffset = currentMethod.getVarOffset("R" + llb.getParam1());
        int secondOffset = currentMethod.getVarOffset("R" + llb.getParam2());
        emit("mov " + firstOffset + "(%ebp), %eax");
        emit((llb.getOp().equals("Or") ? "or " : "and ") + secondOffset + "(%ebp), %eax");
        // check whether really or and and
        emit("movl %eax, " + secondOffset + "(%ebp)");
        return null;
    }

    /*
     * R2's offset is b in the following translations.
     * TR[Move c, R2] (where c is constant)
     * mov $c, b(%ebp)
     *
     * TR[Move var, R2] (where var is a variable, whose offset is a)
     * mov a(%ebp), %eax
     * mov %eax, b(%ebp)
     *
     * TR[Move R2, var] (still, var's offset is a and R2's is b)
     * mov b(%ebp), %eax
     * mov %eax, a(%ebp)
     */
    public Object visit(LirMove lm) {
        int regOffset = currentMethod.getVarOffset("R" + lm.getRegNum());
        if (lm.getMemory() == null) {
            emit("movl $" + lm.getConstant() + ", " + regOffset + "(%ebp)");
        } else {
            int memoryOffset = currentMethod.getVarOffset(lm.getMemory());
            if (lm.isToReg()) {
                emit("mov " + memoryOffset + "(%ebp), %eax");
                emit("movl %eax, " + regOffset + "(%ebp)");
            } else {
                emit("mov " + regOffset + "(%ebp), %eax");
                emit("movl %eax, " + memoryOffset + "(%ebp)");
            }
        }
        return null;
    }

    /*
     * TR[MoveArray R1[R2], R3] (offsets are a, b, c)
     * mov a(%ebp), %eax
     * mov b(%ebp), %ecx
     * // next lines check for null pointer exception
     * cmp $0, %eax
     * je labelNPE
     * // next lines check for array index out of bounds
     * mov -4(%eax), %ebx
     * cmp %ecx, %ebx
     * jle labelABE
     * cmp $0, %ecx
     * jl labelABE
     * // and finally
     * mov c(%ebp), %ebx
     * mov %ebx, (%eax, %ecx, 4)

     * 
     * TR[R3, MoveArray R1[R2]] (same offsets)
     * // all same, except two last lines:
     * mov (%eax, %ecx, 4), %ebx
     * mov %ebx, c(%ebp)

     */
    public Object visit(LirMoveArray lma) {
        int arrayOffset = currentMethod.getVarOffset("R" + lma.getArrayReg());
        int indexOffset = currentMethod.getVarOffset("R" + lma.getArrayIndex());
        int destOffset = currentMethod.getVarOffset("R" + lma.getDestReg());

        emit("mov " + arrayOffset + "(%ebp), %eax");
        emit("mov " + indexOffset + "(%ebp), %ecx");
        // null pointer check
        emit("cmp $0, %eax");
        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
        // index out of bounds check
        emit("mov -4(%eax), %ebx");
        emit("cmp %ecx, %ebx");
        emit("jle " + Info.ErrorMessages.ArrayIndexOutOfBounds.getFunctionLabel());
        emit("cmp $0, %ecx");
        emit("jl " + Info.ErrorMessages.ArrayIndexOutOfBounds.getFunctionLabel());

        if (lma.isStore()) {
            emit("mov " + destOffset + "(%ebp), %ebx");
            emit("movl %ebx, (%eax, %ecx, 4)");
        } else {
            emit("mov (%eax, %ecx, 4), %ebx");
            emit("movl %ebx, " + destOffset + "(%ebp)");
        }
        return null;
    }

    /*
     * TR[MoveField _DV_A, R1.0] (R1's offset is b)
     * mov b(%ebp), %ebx
     * // check for null pointer
     * cmp $0, %ebx
     * je labelNPE
     * movl $_DV_A, (%ebp)
     *
     * TR[MoveField R1, R2.c] (offsets are a and b, c is a constant)
     * mov b(%ebp), %ebx
     * // check for null pointer
     * cmp $0, %ebx
     * je labelNPE
     * mov a(%ebp), %eax
     * mov %eax, (4*c)(%ebx)  (4*c is not really in brackets)
     *
     * TR[MoveField R2.c, R1]
     * mov b(%ebp), %ebx
     * // check for null pointer
     * cmp $0, %ebx
     * je labelNPE
     * mov (4*c)(%ebp), %eax
     * mov %eax, a(%ebp)  (4*c is not really in brackets)
     */
    public Object visit(LirMoveField lmf) {
        int objectOffset = currentMethod.getVarOffset("R" + lmf.getObjectReg());

        emit("mov " + objectOffset + "(%ebp), %ebx"); // move object into ebx
        // null pointer exception check
        emit("cmp $0, %ebx");
        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
        if (lmf.getDispatch() == null) {
            /*NOTE: The name destOffset here is confusing, it doesn't have to be the destination.
             * It is simply the involved register.
             */
            int destOffset = currentMethod.getVarOffset("R" + lmf.getDestReg());
            int fieldOffset = lmf.getFieldOffset();
            if (lmf.isStore()) { //store into the field
                emit("mov " + destOffset + "(%ebp), %eax");
                emit("movl %eax, " + 4 * fieldOffset + "(%ebx)");
            } else {
                emit("mov " + 4 * fieldOffset + "(%ebx), %eax"); //check legal
                emit("movl %eax, " + destOffset + "(%ebp)");
            }
        } else {
            emit("movl $" + lmf.getDispatch() + ", (%ebx)");
        }
        return null;

    }

    /*
     * TR[Return R1]
     * mov a(%ebp), %eax
     * jmp _A_foo_epilogue 
     *
     * TR[Return Rdummy]
     * jmp _A_foo_epilogue 
     */
    public Object visit(LirReturn lr) {
        if (lr.getParam() != -1) {
            int paramOffset = currentMethod.getVarOffset("R" + lr.getParam());
            emit("mov " + paramOffset + "(%ebp), %eax");
        }
        emit("jmp " + currentMethodLabelName + "_epilogue");
        return null;
    }

    /*
     * TR[StaticCall funcLabel(formal1=R1, ..., foramln=Rn), Rdest  (Rdest's offset is a)
     * generalCall (see generalCall, it pushes the registers in reversed order)
     * call funcLabel
     * mov %eax, a(%ebp)
     * add $(4*n), %esp  (of course, 4*n is not in brackets)
     */
    // verify that there's no need to store registers
    public Object visit(LirStaticCall lsc) {
        int targetOffset = currentMethod.getVarOffset("R" + lsc.getTargetRegister());
        pushVars(lsc);
        emit("call " + lsc.getMethodName());
        emit("movl %eax, " + targetOffset + "(%ebp)");
        emit("add $" + 4 * lsc.getRegisterNumbers().size() + ", %esp");
        return null;
    }

    /*
     * TR[Neg R1]   (Not R1)
     * mov a(%ebp), %eax
     * neg %eax         (not %eax)
     * mov %eax, a(%ebp)
     */
    public Object visit(LirUnaryOp luo) {
        int paramOffset = currentMethod.getVarOffset("R" + luo.getParam());
        emit("mov " + paramOffset + "(%ebp), %eax");
        emit((luo.isIsNeg() ? "neg" : "not") + " %eax");
        emit("movl %eax, " + paramOffset + "(%ebp)");
        return null;
    }

    /*
     * TR[VirtualCall R1.c(formal1=R1, ..., foramln=Rn), Rdest
     * (Rdest's offset is a, object's offset is b):
     * generalCall (see generalCall, it pushes the registers in reversed order)
     * mov b(%ebp), %eax
     * push %eax
     * mov 0(%eax), %eax
     * call *(4*c)(%eax)
     * mov %eax, a(%ebp)
     * add $(4*n), %esp         (again, both here and in 4*c, no brackets...)
     */
    public Object visit(LirVirtualCall lvc) {
        int targetOffset = currentMethod.getVarOffset("R" + lvc.getTargetRegister());
        int objectOffset = currentMethod.getVarOffset("R" + lvc.getObjectRegister());
        pushVars(lvc);
        emit("mov " + objectOffset + "(%ebp), %eax");
        // check for null pointer exception
        emit("cmp $0, %eax");
        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());

        emit("push %eax");
        emit("mov 0(%eax), %eax");
        emit("call *" + 4 * lvc.getMethodOffset() + "(%eax)");

        emit("movl %eax, " + targetOffset + "(%ebp)");
        emit("add $" + 4 * (lvc.getRegisterNumbers().size() + 1) + ", %esp"); // +1 for this
        return null;
    }

    /*
     * pushes the variables of a call into the stack in reverse order
     * formal1=R1, ..., formaln = Rn   (whose offsets are a1, a2, ... , an)
     * mov an(%ebp), %eax
     * push %eax
     * ...
     * mov a1(%ebp), %eax
     * push %eax
     */
    private void pushVars(LirAbstractCall lac) {
        List<Integer> regNumbers = lac.getRegisterNumbers();
        for (int i = regNumbers.size() - 1; i >= 0; i--) {
            int regOffset = currentMethod.getVarOffset("R" + regNumbers.get(i));
            emit("mov " + regOffset + "(%ebp), %eax");
            emit("push %eax");
        }
    }

    /*
     * Creates the handlers for runtime errors
     * TODO: add in the beginning of the program the error strings
     */
    private void createRuntimeHandlers() {
        for (Info.ErrorMessages errorMessage : Info.ErrorMessages.values()) {
            createSpecificHandler(errorMessage.getStringLabel(), errorMessage.getFunctionLabel());
        }
    }

    /*
     * creates the handler for a specific error.
     * error is one of NPE, ABE, ASE, DBE
     */
    private void createSpecificHandler(String errorStr, String label) {
        emit(label + ": "); // TODO: check whether name has no conflicts
        emit("push $" + errorStr);
        emit("call __println");
        emit("push $1");
        emit("call __exit");
    }

    private void createErrorStrings() { //TODO: add \n at the end?
        for (Info.ErrorMessages errorMessage : Info.ErrorMessages.values()) {
            new LirLabel(errorMessage.getStringLabel(), "\"" + errorMessage.getVal() + "\"").accept(this);
        }
    }
}
