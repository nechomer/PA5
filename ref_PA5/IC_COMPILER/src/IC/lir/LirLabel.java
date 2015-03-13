/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IC.lir;

import IC.asm.LIRVisitor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nimrod Rappoport
 */
public class LirLabel implements LirInstruction {

    private boolean isMethod = false;
    private String labelName;
    private List<String> labels;
    private String text;

    /*
     * labels is a list of labels that should be null unless this is a label to a dispatch table
     * text is the string literal in case this is a label of a string literal
     * name is the exact name of the label
     * isMethod is true <=> this label is the beginning of a function
     * Now some constructors
     */
    private LirLabel(String labelName, String text, boolean isDispatch) {
        this.labelName = labelName;
        if (isDispatch) {
            this.labels = new ArrayList<String>();
        } else {
            this.labels = null;
        }
        this.text = text;
    }

    public LirLabel(String labelName, boolean isDispatch) {
        this(labelName, null, isDispatch);
    }

    public LirLabel(String labelName, boolean isDispatch, boolean isMethod) {
        this(labelName, null, isDispatch);
        this.isMethod = isMethod;
    }

    public LirLabel(String labelName, String text) {
        this(labelName, text, false);
    }

    public LirLabel(String labelName, LirLabel dispatch) {
        this.labelName = labelName;
        this.labels = new ArrayList<String>();
        for (String label : dispatch.labels) {
            labels.add(label);
        }
    }

    public void addMethod(String name) {
        this.labels.add(name);
    }

    /*
     * This function returns the string for the label as it should be in the lir program
     */
    @Override
    public String toString() {
        String ret = "";
        ret = ret + this.labelName + ": ";
        if (labels != null) {
            ret += "[";
            for (int i = 0; i < labels.size() - 1; i++) {
                ret += labels.get(i) + ", ";
            }
            if (!labels.isEmpty()) {
                ret += labels.get(labels.size() - 1);
            }
            ret += "]";
        } else if (this.text != null) {
            ret +=  text;
        }
        return ret;
    }

    // called to replace a method when detecting override
    public void replaceMethod (String oldLabel, String newLabel) {
        int index =-1;
        for (String label : labels) {
            index++;
            if (label.equals(oldLabel))
                break;
        }
        labels.remove(index);
        labels.add(index, newLabel);
    }
//    public void replaceMethod(String newMethod) {
//        int index =-1;
//        String newMethodName = newMethod.substring
//                (newMethod.indexOf('_', 1)+1, newMethod.length());
//        for (String label : labels) {
//            String oldMethodName =  label.substring
//                (label.indexOf('_', 1)+1);
//            if (oldMethodName.equals(newMethodName)) {
//                index = labels.indexOf(label);
//            }
//        }
//        labels.remove(index);
//        labels.add(index, newMethod);
//    }

    // called only for labels for string literals
    public String getStringLiteral() {
        return this.text;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public String getLabelName() {
        return labelName;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getText() {
        return text;
    }

    public boolean isIsMethod() {
        return isMethod;
    }
    
}
