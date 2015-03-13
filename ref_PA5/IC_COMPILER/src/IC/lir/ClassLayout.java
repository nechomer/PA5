/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IC.lir;

import IC.AST.Field;
import IC.AST.Method;
import IC.SymbolTable.ClassSymbolTable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nimrod Rappoport
 */
public class ClassLayout {

    /*
     * A class for a class layout.
     * name is the name of the corresponding class
     * dispatch is a lir label that describes the dispatch vector
     * lastMethod remember the index in the dispatch vector of the last inserted method
     * lastField likewise, but last index of fields
     * methodToOffset and fieldToOffset are maps from methods and field to their offsets
     */
    private String name;
    private int lastMethod = 0, lastField = 1; 
    private Map<Method, Integer> methodToOffset = new HashMap<Method, Integer>();
    private LirLabel dispatch;
    private Map<Field, Integer> fieldToOffset = new HashMap<Field, Integer>();

    // a constructer for classes that do not inherit
    public ClassLayout(String name) {
        this.name = name;
        this.dispatch = new LirLabel(Naming.classDV(name), true);
    }

    /* a constructor for classes that inherit. Takes their parent's method
     * to offset and field to offset, as well as the dispatch vector.
     */
    public ClassLayout(ClassLayout cl, String name) {
        this.name = name;
        for (Method method : cl.methodToOffset.keySet()) {
            methodToOffset.put(method, cl.getMethodOffset(method));
            lastMethod++;
        }
        for (Field field : cl.fieldToOffset.keySet()) {
            fieldToOffset.put(field, cl.getFieldOffset(field));
            lastField++;
        }

        this.dispatch = new LirLabel(Naming.classDV(name), cl.dispatch);
    }

    // returns the method offset of a certain method
    public Integer getMethodOffset(Method method) {
        return methodToOffset.get(method);
    }

    // return the field offset of a certain field
    public Integer getFieldOffset(Field field) {
        return fieldToOffset.get(field);
    }

    // returns the offset of a method with the given name
    public Integer getMethodOffset(String methodName) {
        for (Method method : methodToOffset.keySet()) {
            if (method.getName().equals(methodName)) {
                return methodToOffset.get(method);
            }
        }
        return null; //unreachable
    }

    // like last function but for fields
    public Integer getFieldOffset(String fieldName) {
        for (Field field : fieldToOffset.keySet()) {
            if (field.getName().equals(fieldName)) {
                return fieldToOffset.get(field);
            }
        }
        return null; //unreachable
    }

    /* adds a method. does this by looking for an existing method with the name
     * if exists, replaces it and changes the dispatch vector
     * else, adds it at the end of the dispatch vector.
     */
    public void addMethod(Method method) {
        boolean override = false;
        int offset;
        Method curMethod=null;
        for (Method existMethod : methodToOffset.keySet()) {
            if (existMethod.getName().equals(method.getName())) {
                curMethod=existMethod;
                override = true;
                String oldClass = ((ClassSymbolTable)existMethod.enclosingScope()).getICClass().getName();
                dispatch.replaceMethod(Naming.functionLabelName(oldClass, method.getName()), Naming.functionLabelName(name, method.getName()));
//                dispatch.replaceMethod(Naming.functionLabelName(this.name, method.getName()));
            }
        }
          if(override) {
                offset = methodToOffset.get(curMethod);
                methodToOffset.remove(curMethod);
                methodToOffset.put(method, offset);
        }

        if (!override) {
            methodToOffset.put(method, lastMethod++);
            dispatch.addMethod(Naming.functionLabelName(this.name, method.getName()));
        }
    }

    // adds field. no inheritance problems.
    public void addField(Field field) {
        fieldToOffset.put(field, lastField++);
    }

    // returns the number of bytes the object needs
    public int size() {
        return 4 * lastField;
    }

    // returns the dispatch vector
    LirInstruction getLabel() {
        return this.dispatch;
    }
}
