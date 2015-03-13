package IC.Type;

/**
 * String type
 * @author Nimrod Rappoport & Barak Itkin
 */
public class StringType extends Type {

    public StringType() {
        super("string");
    }

    @Override
    public boolean subtypeof(Type t) {
        return t == this;
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }
}
