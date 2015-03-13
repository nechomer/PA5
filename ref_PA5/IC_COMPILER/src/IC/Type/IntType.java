package IC.Type;

/**
 * Integer type
 * @author Nimrod Rappoport & Barak Itkin
 */
public class IntType extends Type {

    public IntType() {
        super("int");
    }

    @Override
    public boolean subtypeof(Type t) {
        return t == this;
    }

    @Override
    public boolean isReferenceType() {
        return false;
    }
}
