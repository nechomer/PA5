package IC.asm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.AST.Formal;

public class MethodLayouts {
	
    public MethodLayouts() {
		methodLayouts = new HashMap<String, MethodLayout>();
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
    	return (methodLayout.offsets.get(regName));
    }
   
	private class MethodLayout {

	    private Map<String, Integer> offsets = new HashMap<String, Integer>();
	    
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
	    		offsets.put(param.getName(), lastParameterOffset);
	    		lastParameterOffset += 4;
	    	}
	    }

	    public int getVarStackSize() {
	    	return -lastVarOffset-4;
	    }
	    
	}

}
