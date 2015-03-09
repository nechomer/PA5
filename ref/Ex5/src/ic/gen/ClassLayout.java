package ic.gen;

import static ic.ir.TacValueRef.lbl;
import ic.ast.decl.DeclClass;
import ic.ir.TacValueRef;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ClassLayout {
    private HashMap<String, MethodInfo> methodToInfo;
    private HashMap<String, Integer> fieldToOffset;
    private TacValueRef dvLabel;
    
    static class MethodInfo {
        public int offset;
        public TacValueRef label;
        
        public MethodInfo(int offset, TacValueRef label) {
            this.offset = offset;
            this.label = label;
        }
    }
    
    public ClassLayout(DeclClass icClass, ClassLayout parentCl) {
        methodToInfo = new LinkedHashMap<String, MethodInfo>();
        fieldToOffset =  new LinkedHashMap<String, Integer>();
        dvLabel = lbl("_" + icClass.getName() + "_@DV");
        
        // copy fields and methods from parent class
        if (parentCl != null) {
            for (Map.Entry<String, MethodInfo> en : parentCl.methodToInfo.entrySet()) 
                addMethod(en.getKey(), en.getValue().label);

            fieldToOffset.putAll(parentCl.fieldToOffset);
        }
    }
    
    public TacValueRef getDvLabel() {
        return dvLabel;
    }
    
    public int getFieldCount() {
        return fieldToOffset.size();
    }
    
    public void addMethod(String methodName, TacValueRef methodLabel) {
        MethodInfo mi = methodToInfo.get(methodName);
        // if method already exists, only override the label
        if (mi != null)
            mi.label = methodLabel;
        else
            mi = new MethodInfo(methodToInfo.size(), methodLabel);
        methodToInfo.put(methodName, mi);
    }
    
    public void addField(String name) {
        Integer offset = fieldToOffset.get(name);
        if (offset != null)
            return;
        fieldToOffset.put(name, fieldToOffset.size() + 1);
    }
    
    public TacValueRef getMethodLabel(String methodName) {
        MethodInfo mi = methodToInfo.get(methodName);
        if (mi == null)
            return null;
        return mi.label;
    }
    
    public int getMethodOffset(String methodName) {
        MethodInfo mi = methodToInfo.get(methodName);
        if (mi == null)
            return -1;
        return mi.offset;        
    }
    
    public int getFieldOffset(String fieldName) {
        Integer offset = fieldToOffset.get(fieldName);
        if (offset == null)
            return -1;
        return offset.intValue();    
    }
    
    public Collection<MethodInfo> getMethods() {
        return methodToInfo.values();
    }
    
    public Set<String> getFields() {
        return fieldToOffset.keySet();
    }   

}
