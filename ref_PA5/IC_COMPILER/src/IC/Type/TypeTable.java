package IC.Type;

import IC.AST.ICClass;
import java.util.*;
import IC.SemanticalError;

/**
 *
 * @author Nimrod Rappoport
 */
public class TypeTable {

    private static final Map<Type, ArrayType> uniqueArrayTypes = new LinkedHashMap<Type, ArrayType>();
    private static final Map<String, ClassType> uniqueClassTypes = new LinkedHashMap<String, ClassType>();
    private static final Map<String, MethodType> uniqueMethodTypes = new LinkedHashMap<String, MethodType>();
    
    public static final Type boolType = new BoolType();
    public static final Type intType = new IntType();
    public static final Type nullType = new NullType();
    public static final Type stringType = new StringType();
    public static final Type voidType = new VoidType();

    private static final List<Type> types = new LinkedList<Type>(Arrays.asList(boolType, intType, nullType, stringType, voidType));

    // Returns unique array type object
    public static ArrayType getArrayType(Type elemType) {
        if (uniqueArrayTypes.containsKey(elemType)) {
            // array type object already created – return it
            return uniqueArrayTypes.get(elemType);
        } else {
            // object doesn’t exist – create and return it
            ArrayType arrt = new ArrayType(elemType);
            uniqueArrayTypes.put(elemType, arrt);
            types.add(arrt);
            return arrt;
        }
    }

    /**
     * Create a new unique type for a given class name. If the class is already
     * defined, or if it's based on a class that wasn't defined yet, throw an
     * error.
     * @param name The name of the class to be defined
     * @param superClassName The name of it's base class
     * @param classAST The AST node representing the class
     * @return The type representing this class
     * @throws SematicalError Warn about a redefinition if the class was already
     *                        defined
     * @throws SematicalError Warn about wrong predecense if a class was defined
     *                        before it's super class
     */
    // Returns unique class type object
    // TODO: function arguments may change
    // TODO: fix superClass
    public static ClassType defineClassType(String name, String superClassName, ICClass classAST) {
        ClassType superClassType = uniqueClassTypes.get(superClassName);

        if (uniqueClassTypes.containsKey(name)) {
            throw new SemanticalError("Redefinition of class " + name, classAST.getLine());
        } else {
            // object doesn’t exist – create and return it
            ClassType clst = new ClassType(name, classAST, superClassType);
            uniqueClassTypes.put(name, clst);
            types.add(clst);
            return clst;
        }
    }

    /**
     * Get the unique type for a given class name. If the class is not already
     * defined, throw an error.
     * @param name The name of the class to get it's type
     * @return The type representing this class
     * @throws SematicalError If trying to reference an undefined class
     */
    public static ClassType getClassType(String name, int line) {
        if (uniqueClassTypes.containsKey(name)) {
            // class type object already created – return it
            return uniqueClassTypes.get(name);
        } else {
            throw new SemanticalError("Trying to reference the undefined class " + name, line);
        }
    }

    // Returns unique class type object
    // IT IS CRUCIAL THAT WE DEFINE A UNIQUE TYPE FOR METHODS WITH THE SAME
    // SIGNATURE, BECAUSE IT WILL HELP THE LIR TRANSLATION!
    public static MethodType defineMethodType(String className, IC.AST.Method methodAST) {
        Type retType = TypeTable.getType(methodAST.getType());
        Type[] paramTypes = new Type[methodAST.getFormals().size()];
        String[] paramNames = new String[methodAST.getFormals().size()];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypes[i] = TypeTable.getType(methodAST.getFormals().get(i).getType());
            paramNames[i] = methodAST.getFormals().get(i).getName();
        }

        String funcName = methodAST.getName();
        String name = className + "." + funcName;
        if (uniqueMethodTypes.containsKey(name)) {
            // method type object already defined!
            throw new SemanticalError("Redefinition of method " + funcName
                    + " in " + className, methodAST.getLine());
        } else {
            // object doesn’t exist – create and return it
            MethodType mtdt = new MethodType(name, retType, paramTypes, paramNames);
            uniqueMethodTypes.put(name, mtdt);
            types.add(mtdt);
            return mtdt;
        }
    }

    /**
     * Get the unique type for a given method name.
     * WARNING: does not check for class existance, does not check super classes
     * for that function!!!
     * @param name The name of the class to get it's type
     * @return The type representing this class
     * @throws SematicalError If trying to reference an undefined class
     */
    public static MethodType getMethodType(String className, String funcName, int line) {
        String typeName = className + "." + funcName;
        if (uniqueMethodTypes.containsKey(typeName)) {
            // class type object already created – return it
            return uniqueMethodTypes.get(typeName);
        } else {
            throw new SemanticalError("Function " + funcName + " does not exist in " + className, line);
        }
    }

    public static Type getType(IC.AST.Type type) {
        Type baseType;
        if (type == null)
            return null;

        String baseName = type.getName();
        if (baseName.equals("int"))
            baseType = TypeTable.intType;
        else if (baseName.equals("boolean"))
            baseType = TypeTable.boolType;
        else if (baseName.equals("null"))
            baseType = TypeTable.nullType;
        else if (baseName.equals("string"))
            baseType = TypeTable.stringType;
        else if (baseName.equals("void"))
            baseType = TypeTable.voidType;
        else if (uniqueClassTypes.containsKey(baseName))
            baseType = uniqueClassTypes.get(baseName);
        else
            throw new SemanticalError("Unknown type " + type.getName(), type.getLine());

        for (int i= 0; i < type.getDimension(); i++) {
            baseType = TypeTable.getArrayType(baseType);
        }
        
        return baseType;
    }

    public static String makeTypeListString(Type[] types) {
        if (types.length == 0)
            return "()";
        
        StringBuffer sb = new StringBuffer("(");
        for (int i = 0; i < types.length - 1; i++) {
            sb.append(types[i]).append(", ");
        }
        sb.append(types[types.length - 1]).append(")");

        return sb.toString();
    }

    public static void print() {
        System.out.println("Type table:");
        int i = 1;
        for (Type type : types) {
            System.out.print("    " + i++ + ": ");
            if (type instanceof ArrayType)
                System.out.print("Array type: ");
            else if (type instanceof ClassType)
                System.out.print("Class: ");
            else if (type instanceof MethodType)
                System.out.print("Method type (" + ((MethodType)type).name + "): ");
            else
                System.out.print("Primitive type: ");
            System.out.print(type);

             if (type instanceof ClassType && ((ClassType)type).superClassType != null)
                System.out.print(", Superclass ID: " + (1+types.indexOf(((ClassType)type).superClassType)));

            System.out.println();
        }
    }
}
