package IC.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.AST.Formal;

public class MethodLayouts {
	
    public MethodLayouts() {
		methodLayouts = new HashMap<String, MethodLayout>();
		makeErrorChecksLayouts();
	}

	private Map<String, MethodLayout> methodLayouts;
    
    public void insertParameters(String methodName, List<Formal> paramteres) {
    	MethodLayout methodLayout = new MethodLayout();
    	methodLayout.insertParameters(paramteres);
    	methodLayouts.put(methodName, methodLayout);
    }
    
    public void insertVar(String methodName, String varName, String scopeName) {
		MethodLayout methodLayout = methodLayouts.get(methodName);
		methodLayout.insertVar(varName + ((scopeName == null) ? "" : "_" + scopeName));
    }
    
    public int getOffset(String methodName, String regName) {
    	MethodLayout methodLayout = methodLayouts.get(methodName);
    	return methodLayout.getOffset(regName);
    }
    
    public int getVarStackSize(String methodName) {
    	MethodLayout methodLayout = methodLayouts.get(methodName);
    	return methodLayout.getVarStackSize();
    }
    
    public List<String> getParamsReverseList(String methodName) {
    	MethodLayout methodLayout = methodLayouts.get(methodName);
    	return methodLayout.getParamsReverseList();
    }
    
    private void makeErrorChecksLayouts() {
    	String[] regArray = {"R0"};
    	String[] paramArrayCheckNullRef = {"a"};
    	String[] paramArrayCheckArrayAccess = {"a, i"};
    	String[] paramArrayCheckSize = {"n"};
    	String[] paramArrayCheckZero = {"b"};
    	insertErrorCheckLayout("__checkNullRef", regArray, paramArrayCheckNullRef);
    	insertErrorCheckLayout("__checkArrayAccess", regArray, paramArrayCheckArrayAccess);
    	insertErrorCheckLayout("__checkSize", regArray, paramArrayCheckSize);
    	insertErrorCheckLayout("__checkZero", regArray, paramArrayCheckZero);
    }
    
    private void insertErrorCheckLayout(String methodName, String[] regs, String[] parameters) {
    	MethodLayout methodLayout = new MethodLayout();
    	methodLayout.insertParameters(parameters);
    	for (String reg : regs) {
    		methodLayout.insertVar(reg);
    	}
    	methodLayouts.put(methodName, methodLayout);
    }
    
   
	private class MethodLayout {

	    private Map<String, Integer> offsets = new HashMap<String, Integer>();
	    
	    List<String> paramsReverseList = new ArrayList<String>();
	    
	    private int lastVarOffset = -4, lastParameterOffset = 8;
	    
	    public int getOffset(String regName) {
	    	return (offsets.get(regName));
	    }
	    
	    public void insertVar(String regName) {
	    	if (!offsets.containsKey(regName)) {
	    		offsets.put(regName, lastVarOffset);
	    		lastVarOffset -= 4;
	    	}
	    }
	    
	    public void insertParameters(List<Formal> parameters) {
	    	for (Formal param : parameters) {
	    		paramsReverseList.add(paramsReverseList.size()-1, param.getName());
	    		offsets.put(param.getName(), lastParameterOffset);
	    		lastParameterOffset += 4;
	    	}
	    }
	    
	    public void insertParameters(String[] parameters) {
	    	for (String param : parameters) {
	    		offsets.put(param, lastParameterOffset);
	    		lastParameterOffset += 4;
	    	}
	    }

	    public int getVarStackSize() {
	    	return -lastVarOffset-4;
	    }
	    
	    public List<String> getParamsReverseList() {
	    	return paramsReverseList;
	    }
	    
	}

}
