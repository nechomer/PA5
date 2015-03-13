/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.lir;

import IC.AST.Formal;
import IC.AST.LocalVariable;
import IC.AST.Method;
import IC.AST.VariableLocation;
import IC.SymbolTable.BlockSymbolTable;
import IC.SymbolTable.ClassSymbolTable;
import IC.SymbolTable.MethodSymbolTable;
import IC.SymbolTable.SymbolTable;
import java.util.HashMap;

/**
 * A class to make label/variable names consistant in the lir translation
 *
 * strings: strXXX, where XXX is the string number
 * variables: lXXX_Name, where XXX is the depth of the scope inside the function
 * class: _DV_CLASSNAME
 * function: _FUNC_CLASSNAME_FUNCNAME
 * 
 * @author Barak Itkin
 */
public class Naming {

    public static final class FunctionLocation {
        public final String className, functionName;

        public FunctionLocation(String className, String functionName) {
            this.className = className;
            this.functionName = functionName;
        }

    }

    private static HashMap<String,FunctionLocation> splitFuncLabel = new HashMap<String, FunctionLocation>();

    public static String generalEndLabel (int labelNumber) {
        return "_end_label" + labelNumber;
    }

    public static String ifEndLabel (int labelNumber) {
        return "_if_end_label" + labelNumber;
    }

    public static String ifFalseLabel (int labelNumber) {
        return "_if_false_label" + labelNumber;
    }

    public static String whileTestLabel (int labelNumber) {
        return "_while_test_label" + labelNumber;
    }

    public static String whileEndLabel (int labelNumber) {
        return "_while_end_label" + labelNumber;
    }

    public static String classDV (String className) {
        return "_DV_" + className;
    }

    public static String formalName (String formalName) {
        return "formal_" + formalName;
    }

    public static String varName (VariableLocation var) {
        return varName(var.getName(), var.enclosingScope());
    }

    public static String varName (LocalVariable var) {
        return varName(var.getName(), var.enclosingScope());
    }

    protected static String varName (String name, SymbolTable enclosingScope) {
        if (enclosingScope instanceof ClassSymbolTable)
            return ((ClassSymbolTable)enclosingScope).getId() + "_" + name;
        else if (enclosingScope instanceof BlockSymbolTable) {
            int level = 0;
            while (enclosingScope.getParentSymbolTable() instanceof BlockSymbolTable) {
                enclosingScope = enclosingScope.getParentSymbolTable();
                level++;
            }
            return "l" + level + "_" + name;
        } else if (enclosingScope instanceof MethodSymbolTable) {
            // TODO: May beed fixing for "this"
            Method m = ((MethodSymbolTable)enclosingScope).getMethod();
            for (Formal formal : m.getFormals()) {
                if (formal.getName().equals(name))
                    return formalName(name);
            }
            return "l0_" + name;
        } else {
            System.err.println("Code should not be reached! "
                    + "IC.lir.Naming.varName(String,SymbolTable)");
            return name;
        }
    }

    public static String functionLabelName (String className, String functionName) {
        String label = "_" + className + "_" + functionName
                // enable the following line if you want to gaurantee unique
                // label names, with the cost of ugliness
//                + "_" + count(className, "_") + "_" + count(functionName, "_")
                ;
        
        // WARNING! If updating this line, change getFunctionFromLabel() below!
        splitFuncLabel.put(label, new FunctionLocation(className, functionName));
        return label;
    }

    public static FunctionLocation getFunctionAndClassFromLabel (String labelName) {
        FunctionLocation funcLoc = splitFuncLabel.get(labelName);
        if (funcLoc == null)
            throw new AssertionError("Code should not be reached!\nNaming.getFunctionFromLabel");
        // WARNING! See functionLabelName() above!
        return funcLoc;
    }

    public static String stringLabel (int number) {
        return "str"+number;
    }

    public static int count (String src, String pattern) {
        return (src.length() - src.replaceAll(pattern, "").length()) / pattern.length();
    }

}
