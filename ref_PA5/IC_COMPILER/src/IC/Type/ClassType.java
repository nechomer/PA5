package IC.Type;

import IC.AST.ICClass;

/**
 * Class types
 * @author Nimrod Rappoport & Barak Itkin
 */
public class ClassType extends Type {

//    ICClass classAST;
    ClassType superClassType;

    public ClassType(String name, ICClass classAST, ClassType superClassType) {
        super(name);
//        this.classAST = classAST;
        this.superClassType = superClassType;
    }

    @Override
    public boolean subtypeof(Type t) {
        if (this == t) {
            return true;
        }
        if (isRootType()) {
            return false;
        }
        return this.superClassType.subtypeof(t);
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }

    private boolean isRootType() {
        return this.superClassType == null;
    }

    public String getClassName() {
//        return this.classAST.getName();
        return this.name;
    }
}
