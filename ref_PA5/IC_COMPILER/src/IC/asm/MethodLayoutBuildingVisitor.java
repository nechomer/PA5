package IC.asm;

import IC.lir.LibCall;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Nimrod Rappoport
 * Barak -  read here!
 * The translation will be done in two passes - the first one will build the method layout
 * and the second one will do the actual translation.
 * Both tasks can be accomplished with a single pass on the code, but it will be
 * more difficult and less modular.
 * This class will build the method layouts, again using the
 * visitor design pattern.
 * I added an accept method in the relevant LirInstructions
 * Still haven't decided whether should have classes for assembly instructions
 * We should also decide where we want to implement run time checks - in assembly or in LIR.
 * I didn't go through all of the slides yet, but slides 28 and 29 of presentation 10 has many
 * translations (maybe even most of the translations).
 * IMPORTANT: We should have a way to know when we are done translating
 * one method and start translating the other, so we can switch the
 * current method layout.
 */
public class MethodLayoutBuildingVisitor implements LIRVisitor {

    /* currentMethod is the current method layout
     * layouts is a map that given a name of a method returns the
     * matching method layout. It is the output of this visitor.
     * Info is used in order to get the names of the formals of a function
     */
    private MethodLayout currentMethod;
    private Map<String, MethodLayout> layouts = new HashMap<String, MethodLayout>();
    private Info info;

    public MethodLayoutBuildingVisitor(Info info) {
        this.info = info;
    }

    /*
     * This is the method that the compiler class will call
     * The method will iterate over the program, and will update the currentMethod
     * and layouts accordingly
     */
    public Object build(List<LirInstruction> program) {

        for (LirInstruction instruction : program) {
            instruction.accept(this);
        }
        return layouts;
    }

    /*
     * The rest of the code is pretty straightforward.
     * We just add every register or variable we see to the current
     * method layout. They are all register, except in LirMove.
     * When we see a label that starts a function we change the current
     * method layout. We then also set offsets of all formal variables
     * of that function.
     */
    public Object visit(LirArithmeticBinary lab) {
        currentMethod.introduceVariable("R" + lab.getParam1());
        currentMethod.introduceVariable("R" + lab.getParam2());
        return null;
    }

    public Object visit(LirArrayLength lal) {
        currentMethod.introduceVariable("R" + lal.getArrayReg());
        currentMethod.introduceVariable("R" + lal.getDestReg());
        return null;
    }

    public Object visit(LirComment lc) {
        return null;
    }

    public Object visit(LirCompare lc) {
        if (!lc.isLiteral()) {
            currentMethod.introduceVariable("R" + lc.getOp1());
        }
        currentMethod.introduceVariable("R" + lc.getOp2());
        return null;
    }

    public Object visit(LirJump lj) {
        return null;
    }

    public Object visit(LirLabel ll) {
        if (ll.isIsMethod()) {
            currentMethod = new MethodLayout();
            this.layouts.put(ll.getLabelName(), currentMethod);
            String[] paramNames = info.getParamNames(ll.getLabelName());
            currentMethod.introduceFormals(paramNames);
        }
        return null;

    }

    public Object visit(LirLibCall llc) {
        if (llc.getLibcall() == LibCall.ALLOCATE_OBJECT) {
            currentMethod.introduceVariable("R" + llc.getDestRegister());
        } else {
            if (llc.getLibcall().getNumArguments() >= 1) {
                currentMethod.introduceVariable("R" + llc.getOp1());
            }
            if (llc.getLibcall().getNumArguments() == 2) {
                currentMethod.introduceVariable("R" + llc.getOp2());
            }
            if (llc.getDestRegister() != -1) {
                currentMethod.introduceVariable("R" + llc.getDestRegister());
            }
        }

        return null;

    }

    public Object visit(LirLogicalBinary llb) {
        currentMethod.introduceVariable("R" + llb.getParam1());
        currentMethod.introduceVariable("R" + llb.getParam2());
        return null;
    }

    public Object visit(LirMove lm) {
        currentMethod.introduceVariable("R" + lm.getRegNum());
        if (lm.getMemory() != null) {
            currentMethod.introduceVariable(lm.getMemory());
        }
        return null;
    }

    public Object visit(LirMoveArray lma) {
        currentMethod.introduceVariable("R" + lma.getArrayIndex());
        currentMethod.introduceVariable("R" + lma.getArrayReg());
        currentMethod.introduceVariable("R" + lma.getDestReg());
        return null;
    }

    public Object visit(LirMoveField lmf) {
        currentMethod.introduceVariable("R" + lmf.getObjectReg());
        if (lmf.getDispatch() == null) {
            currentMethod.introduceVariable("R" + lmf.getDestReg());
        }
        return null;
    }

    public Object visit(LirReturn lr) {
        if (lr.getParam() != -1) {
            currentMethod.introduceVariable("R" + lr.getParam());
        }
        return null;
    }

    public Object visit(LirStaticCall lsc) {
        currentMethod.introduceVariable("R" + lsc.getTargetRegister());
        List<Integer> registerNumbers = lsc.getRegisterNumbers();
        for (int i = 0; i < registerNumbers.size(); i++) {
            currentMethod.introduceVariable("R" + registerNumbers.get(i));
        }
        return null;
    }

    public Object visit(LirUnaryOp luo) {
        currentMethod.introduceVariable("R" + luo.getParam());
        return null;
    }

    public Object visit(LirVirtualCall lvc) {
        currentMethod.introduceVariable("R" + lvc.getObjectRegister());
        currentMethod.introduceVariable("R" + lvc.getTargetRegister());
        List<Integer> registerNumbers = lvc.getRegisterNumbers();
        for (int i = 0; i < registerNumbers.size(); i++) {
            currentMethod.introduceVariable("R" + registerNumbers.get(i));
        }
        return null;
    }
}
