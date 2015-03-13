package IC.SymbolTable;

import IC.Type.Type;

/**
 * Symbols for the symbol table
 * @author Nimrod Rappoport & Barak Itkin
 */
public class Symbol {

    private String name;
    /**
     * The type of the symbol (for example, int)
     */
    private Type type;
    /**
     * The kind of the symbol (for example, field)
     */
    private Kind kind;

    public Symbol(String name, Type type, Kind kind) {
        this.name = name;
        this.type = type;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        if (kind == Kind.CLASS) // Don't do "class: A A"
            return getKind() + ": "  + getName();
        else if (kind.isMethod())
            return getKind() + ": " + getName() + " " + getType();
        else
            return getKind() + ": " + getType() + " " + getName();
    }


}
