package IC.Type;

/**
 * Boolean type
 * @author Nimrod Rappoport & Barak Itkin
 */
public class BoolType extends Type {

    public BoolType() {
        super("boolean");
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
