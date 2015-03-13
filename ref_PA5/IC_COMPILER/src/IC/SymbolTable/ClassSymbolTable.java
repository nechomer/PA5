package IC.SymbolTable;

import IC.AST.ICClass;
import IC.AST.LibraryMethod;
import IC.AST.Method;
import IC.AST.StaticMethod;
import IC.AST.VirtualMethod;
import IC.SemanticalError;
import IC.Type.MethodType;
import IC.Type.Type;
import IC.Type.TypeTable;

/**
 * Assumptions:
 * The forum said that we can choose if we allow to override static stuff with
 * virtual stuff, and the other way around. We chose to allow static methods
 * even if the scope of the super classes has a method (static or virtual) or a
 * field with the same name. We also allow virtual methods with a name equivalent
 * to a static method in the super class
 *
 * We assume that no one is dumb enough to try and inherit from the Library. We
 * also *know* that the library does not inherit from any other class. We
 * also assume it's not syntactically possible, since the library is in another
 * file and may be included only after the declaration (which makes the
 * inheritance illegal).
 * 
 * @author Nimrod Rappoport & Barak Itkin
 */
public class ClassSymbolTable extends SymbolTable {

    protected ICClass cls;

    public ClassSymbolTable(String id, ICClass cls) {
        super(id);
        this.cls = cls;
    }

    public void defineMethod(Method m, String clsName) {
        String funcName = m.getName();//clsName + "." + m.getName();
        if (m instanceof StaticMethod)
            defineMethod((StaticMethod)m, funcName);
        else if(m instanceof VirtualMethod)
            defineMethod((VirtualMethod)m, funcName);
        else if(m instanceof LibraryMethod)
            defineMethod((LibraryMethod)m, funcName);
        else
            System.err.print("Code should not be reached - classsymtable defineMethod");
    }

    public void defineMethod(StaticMethod sm, String funcName) {
        if (entries.containsKey(funcName)) {
            throw new SemanticalError("Can't define a static method "
                    + funcName + " since the name is already taken!", sm.getLine());
        } else {
            Type[] paramTypes = new Type[sm.getFormals().size()];
            String[] paramNames = new String[sm.getFormals().size()];
            for (int i = 0; i < paramTypes.length; i++) {
                paramTypes[i] = TypeTable.getType(sm.getFormals().get(i).getType());
                paramNames[i] = sm.getFormals().get(i).getName();
            }
            Type methodType = new MethodType(funcName, TypeTable.getType(sm.getType()), paramTypes, paramNames);
            entries.put(funcName, new Symbol(funcName, methodType, Kind.STATIC_METHOD));
        }
    }

    public void defineMethod(LibraryMethod sm, String funcName) {
        if (entries.containsKey(funcName)) {
            throw new SemanticalError("Can't define a double library method "
                    + funcName + " since the name is already taken!", sm.getLine());
        } else {
            Type[] paramTypes = new Type[sm.getFormals().size()];
            String[] paramNames = new String[sm.getFormals().size()];
            for (int i = 0; i < paramTypes.length; i++) {
                paramTypes[i] = TypeTable.getType(sm.getFormals().get(i).getType());
                paramNames[i] = sm.getFormals().get(i).getName();
            }
            Type methodType = new MethodType(funcName, TypeTable.getType(sm.getType()), paramTypes, paramNames);
            entries.put(funcName, new Symbol(funcName, methodType, Kind.LIBRARY_METHOD));
        }
    }

    // Already checks for illegal overrides - nice!
    public void defineMethod(VirtualMethod sm, String funcName) {
        Type[] paramTypes = new Type[sm.getFormals().size()];
        String[] paramNames = new String[sm.getFormals().size()];

        for (int i = 0; i < paramTypes.length; i++) {
            paramTypes[i] = TypeTable.getType(sm.getFormals().get(i).getType());
            paramNames[i] = sm.getFormals().get(i).getName();
        }

        Type methodType = new MethodType(funcName, TypeTable.getType(sm.getType()), paramTypes, paramNames);

        if (entries.containsKey(funcName)) {
            throw new SemanticalError("Can't define a virtual method "
                    + funcName + " since the name is already taken!", sm.getLine());
        } else {
            Symbol match = this.virtualLookup(funcName);
            if (match != null) { // We found either a FIELD or a VIRTUAL_METHOD
                if (match.getKind() == Kind.FIELD) {
                    throw new SemanticalError("Can't have a function with "
                            + "the name " + funcName + " since there is a"
                            + " virtual field with the same name in "
                            + "a parent class", sm.getLine());
                } else if (!methodType.subtypeof(match.getType())) { // Found a virtual method
                    throw new SemanticalError("Illegal override of base method " + funcName, sm.getLine());
                }
            }
            entries.put(funcName, new Symbol(funcName, methodType, Kind.VIRTUAL_METHOD));
        }
    }

    // Only virtual symbols are supported in this IC version
    public void defineField(IC.AST.Field f) {
        String name = f.getName();
        Type fType = TypeTable.getType(f.getType());

        if (name.equals("vfunc")) {
            f.enclosingScope().print();
            f.enclosingScope().parentSymbolTable.print();;
        }
        // Go up, while the parent is still a class. Stop on legal override or a conflict
        if (virtualLookup(name) != null)
            throw new SemanticalError("The name of the field " + name
                + " conflicts with another virtual definition", f.getLine());

        entries.put(name, new Symbol(name, fType, Kind.FIELD));
    }


    public Symbol lookupMethod(String identifier, boolean sttc, int line) {
        Symbol match = sttc ? staticLookup(identifier) : virtualLookup(identifier);
        if (match != null && match.getKind().isMethod()) {
            return match;
        } else {
            throw new SemanticalError("No matching "
                    + (sttc ? "static" : "virtual") +  " method for the name "
                    + identifier, line);
        }
    }

    public Symbol lookupField(String identifier, boolean sttc, int line) {
        if (sttc)
            throw new SemanticalError("No match for " + identifier, line);

        Symbol match = virtualLookup(identifier);
        if (match != null && !match.getKind().isMethod()) {
            return match;
        } else {
            throw new SemanticalError("No matching field for the name "
                    + identifier, line);
        }
    }

    @Override
    public void printTableHeader() {
        System.out.println("Class Symbol Table: " + this.getId());
    }

    public ICClass getICClass() {
        return cls;
    }

}
