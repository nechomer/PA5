package IC.Type;

/**
 * Base class for types
 * @author Nimrod Rappoport & Barak Itkin
 */
public abstract class Type {

    /* The name of the type */
    protected String name;

    public Type(String name) {
        this.name = name;
    }

    /** A is-a relation */
    public abstract boolean subtypeof(Type t);

    /** Is this type passed by reference? */
    public abstract boolean isReferenceType();

    @Override
    public String toString() {
        return name;
    }
}
