/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.lir;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nimrod Rappoport
 */
public abstract class LirAbstractCall implements LirInstruction{
    /*
     * Abstract class for a lir static or virtual call instruction
     * methodName is the name of the method, target register is where the return value
     * should be saved, and the two lists are of formals and from which register they
     * take their value
     */

    private int targetRegister;
    private List<String> formalNames = new ArrayList<String>();
    private List<Integer> registerNumbers = new ArrayList<Integer>();

    public LirAbstractCall(int targetRegister) {
        this.targetRegister = targetRegister;
    }

    public void addFormal(String formalName, int registerNumber) {
        this.formalNames.add(formalName);
        this.registerNumbers.add(registerNumber);
    }

    @Override
    public String toString() {
        String ret = "(";
        if (!formalNames.isEmpty()) {
            for (int i=0; i<formalNames.size() - 1; i++) {
                ret+=formalNames.get(i) + "=R" + registerNumbers.get(i) + ", ";
            }
            ret+=formalNames.get(formalNames.size()-1) +
                    "=R" + registerNumbers.get(formalNames.size()-1);
        }
        ret+=") ,R"+targetRegister;
        return ret;
    }

    public List<String> getFormalNames() {
        return formalNames;
    }

    public List<Integer> getRegisterNumbers() {
        return registerNumbers;
    }

    public int getTargetRegister() {
        return targetRegister;
    }

}
