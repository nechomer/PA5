package IC.SymbolTable;

/**
 * Represent the kind of each symbol
 * @author Barak Itkin
 */
public enum Kind {
    VAR            (true,  true,  false, "Local variable"),
    PARAMETER      (true,  true,  false, "Parameter"),
    FIELD          (false, true,  false, "Field"),
    VIRTUAL_METHOD (false, true,  true,  "Virtual method"),
    CLASS          (true,  true,  false, "Class"),
    STATIC_METHOD  (true,  false, true,  "Static method"),
    LIBRARY_METHOD (true,  false, true,  STATIC_METHOD.humanName);

    /**
     * Should this identifier be found in a static lookup?
     */
    boolean inStatic;
    /**
     * Should this identifier be found in a virtual lookup?
     */
    boolean inVirtual;
    /**
     * Is this a method?
     */
    boolean method;
    /**
     * Pretty printing description
     */
    String humanName;

    Kind(boolean inStatic, boolean inVirtual, boolean method, String humanName) {
        this.inStatic = inStatic;
        this.inVirtual = inVirtual;
        this.method = method;
        this.humanName = humanName;
    }

    public boolean isStatic() {
        return inStatic;
    }

    public boolean isVirtual() {
        return inVirtual;
    }

    public boolean isMethod() {
        return method;
    }

    @Override
    public String toString() {
        return humanName;
    }
}
