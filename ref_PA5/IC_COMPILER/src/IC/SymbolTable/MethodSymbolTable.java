package IC.SymbolTable;

import IC.AST.Method;
import IC.SemanticalError;

/**
 * A symbol table for methods
 * @author Nimrod Rappoport & Barak Itkin
 */
public class MethodSymbolTable extends SymbolTable {

    protected boolean sttc;
    protected Method m;

    public MethodSymbolTable(String id, boolean sttc, Method m) {
        super(id);
        this.sttc = sttc;
        this.m = m;
    }

    public void define(Symbol identifier, int line) {
        if (entries.containsKey(identifier.getName()))
            throw new SemanticalError("Redefinition of " + identifier, line);
        else
            this.entries.put(identifier.getName(), identifier);
    }

    public Symbol lookup(String identifier, int line) {
        Symbol match = sttc ? staticLookup(identifier) : virtualLookup(identifier);
        if (match == null)
            throw new SemanticalError("No such " + (sttc ? "static" : "virtual") + " identifier " + identifier, line);
        else
            return match;
    }

    @Override
    public void printTableHeader() {
        System.out.println("Method Symbol Table: " + this.getId());
    }

    public boolean isStaticScope() {
        return sttc;
    }

    public Method getMethod () {
        return m;
    }


}
