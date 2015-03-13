package IC.Type;

/**
 * Array types
 * @author Nimrod Rappoport & Barak Itkin
 */
public class ArrayType extends Type {

    /** The type of the array */
    Type elemType;

    public ArrayType(Type elemType) {
        super(elemType.name + " array");
        this.elemType = elemType;
    }

    @Override
    public boolean subtypeof(Type t) {
        return t == this;
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }

    @Override
    public String toString() {
        return elemType.toString() + "[]";
    }

    public Type getElemType() {
        return elemType;
    }
}
