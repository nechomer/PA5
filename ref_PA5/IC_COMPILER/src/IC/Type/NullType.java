package IC.Type;

/**
 * Null type
 * @author Nimrod Rappoport
 */
public class NullType extends Type {

    public NullType() {
        super("null");
    }

    @Override
    public boolean subtypeof(Type t) {
        return t.isReferenceType();
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }
}
