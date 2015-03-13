
package IC.Type;

/**
 *
 * @author Barak Itkin
 */
public class VoidType extends Type {

    public VoidType() {
        super("void");
    }

    @Override
    public boolean subtypeof(Type t) {
        return t == this;
    }

    @Override
    public boolean isReferenceType() {
        return false; // so that null won't be of a matching subtype
    }

}
