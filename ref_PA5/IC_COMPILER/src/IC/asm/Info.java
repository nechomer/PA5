/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IC.asm;

import IC.SymbolTable.ClassSymbolTable;
import IC.Type.MethodType;
import IC.Type.TypeTable;
import IC.lir.Naming;
import IC.lir.Naming.FunctionLocation;
import java.util.HashMap;

/**
 *
 * @author Barak Itkin
 */
public class Info {

    public enum ErrorMessages {
        NullPointerReferece("NPE", "Runtime Error: Null pointer dereference!"),
        ArrayIndexOutOfBounds("ABE", "Runtime Error: Array index out of bounds!"),
        NegativeAllocation("ASE", "Runtime Error: Array allocation with negative array size!"),
        DivisionByZero("DBE", "Runtime Error: Division by zero!");
        public String label, val;

        private ErrorMessages(String label, String val) {
            this.label = label;
            this.val = val;
        }

        public String getFunctionLabel() {
            return "label"+label;
        }

        public String getStringLabel() {
            return "str"+label;
        }

        public String getVal() {
            return val;
        }

    }

    public final HashMap<String, ClassSymbolTable> classSyms;

    public Info(HashMap<String, ClassSymbolTable> classSyms) {
        this.classSyms = classSyms;
    }

    /**
     * Given a label of a method (the LIR label that is), determine whether it's
     * static or not
     * @param functionLabelName the label of the function
     * @return true iff the function is static
     */
    public boolean isStaticMethod(String functionLabelName) {
        FunctionLocation fl = Naming.getFunctionAndClassFromLabel(functionLabelName);
        switch (classSyms.get(fl.className).depth0Lookup(fl.functionName).getKind()) {
            case STATIC_METHOD:
                return true;
            case VIRTUAL_METHOD:
                return false;
            default:
                throw new AssertionError("Code should not be reached! Info.isStaticMethod");
        }
    }

    /**
     * Given a label of a method (the LIR label that is), return the labels of
     * it's parameters. If the function is virtual, also include the this
     * parameter as the first parameter.
     * @param functionLabelName the label of the function
     * @return An array containing the labels of the function parameters, by
     *         their order!
     */
    public String[] getParamNames(String functionLabelName) {
        FunctionLocation fl = Naming.getFunctionAndClassFromLabel(functionLabelName);
        MethodType mt = TypeTable.getMethodType(fl.className, fl.functionName, -1);
        String[] paramNames = mt.getParamNames();
        String[] paramLabels;
        int offset;
        int i;
        if (isStaticMethod(functionLabelName)) {
            offset = 0;
            paramLabels = new String[paramNames.length];
        } else {
            offset = 1;
            paramLabels = new String[paramNames.length + 1];
            paramLabels[0] = "this";
        }
        for (i = offset; i < paramLabels.length; i++) {
            paramLabels[i] = Naming.formalName(paramNames[i - offset]);
        }
        return paramLabels;
    }
}
