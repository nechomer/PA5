package IC.SymbolTable;

import java.util.*;

/**
 * A base class for symbol tables
 * 
 * WARNING: THIS IMPLEMENTATION ASSUMES THAT A SYMBOL TABLE WILL NOT CHANGE IT'S
 * PARENT ONCE IT'S DEFINED!
 * @author Nimrod Rappoport & Barak Itkin
 */
public abstract class SymbolTable {

    /**
     * Assumptions:
     * For classes - always the class name
     * For methods - always ClassName.MethodName
     */
    protected String id;
    protected SymbolTable parentSymbolTable;
    /** map from String to Symbol **/
    protected Map<String, Symbol> entries;
    /** The list of child symbol tables (currently, necessary for printing) **/
    protected LinkedHashSet<SymbolTable> children;

    public SymbolTable(String id) {
        this.id = id;
        // To maintain the order, don't use a regular hash map - use this one
        this.entries = new LinkedHashMap<String, Symbol>();
        // To maintain the order, don't use a regular hash set - use this one
        this.children = new LinkedHashSet<SymbolTable>();
    }

    public SymbolTable getParentSymbolTable() {
        return this.parentSymbolTable;
    }

    public void setParentSymbolTable(SymbolTable st) {
        this.parentSymbolTable = st;
        if (st != null) {
            this.parentSymbolTable.children.add(this); // hash set - so won't add doubles!
        }
    }

    /**
     * A simple lookup method.
     * WARNING: Does not consider anything, just lookup and if not found look up!
     */
    public Symbol commnLookup(String identifier) {
        if (this.entries.containsKey(identifier)) {
            return this.entries.get(identifier);
        } else if (parentSymbolTable != null) {
            return parentSymbolTable.commnLookup(identifier);
        } else {
            return null;
        }
    }

    /**
     * A lookup method which only returns virtual identifiers
     */
    public Symbol virtualLookup(String identifier) {
        if (this.entries.containsKey(identifier) && entries.get(identifier).getKind().isVirtual()) {
            return this.entries.get(identifier);
        } else if (parentSymbolTable != null) {
            return parentSymbolTable.virtualLookup(identifier);
        } else {
            return null;
        }
    }

    /**
     * A lookup method which only returns static identifiers
     */
    public Symbol staticLookup(String identifier) {
        if (this.entries.containsKey(identifier) && entries.get(identifier).getKind().isStatic()) {
            return this.entries.get(identifier);
        } else if (this.parentSymbolTable != null) {
            return this.parentSymbolTable.staticLookup(identifier);
        } else {
            return null;
        }
    }

    /**
     * A lookup which does not search parent scopes
     */
    public Symbol depth0Lookup(String identifier) {
        return this.entries.get(identifier);
    }

    /**
     * Define directly in this scope
     */
    public void depth0Define(String identifier, Symbol m) {
        this.entries.put(identifier, m);
    }

    public String getId() {
        return id;
    }

    /** Required for pretty printing */
    public abstract void printTableHeader();

    public void print() {
        this.printTableHeader();
        for (Symbol symbol : this.entries.values()) {
            if (!symbol.getName().equals("$ret") && !symbol.getName().equals("this")) // don't print "extra" vars
            System.out.println("    " + symbol);
        }
        if (!this.children.isEmpty()) {
            System.out.print("Children tables:");
            for (SymbolTable child : children) {
                System.out.print(" " + child.getId());
            }
            System.out.println(); // finish children line
        }
        for (SymbolTable child : children) {
            System.out.println(); // breaks between children
            child.print();
        }

    }
}
