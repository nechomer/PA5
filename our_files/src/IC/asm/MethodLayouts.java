package IC.asm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.AST.Formal;
import IC.SemanticChecks.FrameScope;

public class MethodLayouts {
	
    private Map<String, MethodLayout> methodLayouts;
    
    public void insertParameters(String methodName, List<Formal> fl) {
    	MethodLayout methodLayout = new MethodLayout();
    	methodLayout.insertParameters(fl);
    	methodLayouts.put(methodName, methodLayout);
    }
    
    public void insertVar(String methodName, String varName, String scopeName) {
		MethodLayout methodLayout = methodLayouts.get(methodName);
		methodLayout.insertVar(varName + ((scopeName == null) ? "" : "_" + scopeName));
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
