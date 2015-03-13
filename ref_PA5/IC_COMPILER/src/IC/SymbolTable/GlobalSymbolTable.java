package IC.SymbolTable;

import IC.SemanticalError;

/**
 * The global symbol table
 * @author Nimrod Rappoport
 */
public class GlobalSymbolTable extends SymbolTable {

    //TODO: check that this still works at the end
    public GlobalSymbolTable(String id) {
        super(id);
    }

    public void define(Symbol identifier, int line) {
        if (entries.containsKey(identifier.getName()))
            throw new SemanticalError("Redefinition of " + identifier, line);
        else
            this.entries.put(identifier.getName(), identifier);
    }

    public Symbol lookup(String identifier, int line) {
        return staticLookup(identifier);    // in IC, all classes are static
    }

    @Override
    public void printTableHeader() {
        System.out.println("Global Symbol Table: " + this.getId());
    }

}
