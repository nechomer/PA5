package IC.SymbolTable;

/**
 * A statement block symbol table
 * @author Nimrod Rappoport
 */
public class BlockSymbolTable extends MethodSymbolTable {

    String in;
    public BlockSymbolTable(MethodSymbolTable parent) {
        super("statement block in " + parent.id, parent.sttc, parent.m);
    }

    @Override
    public void printTableHeader() {
        System.out.println("Statement Block Symbol Table ( located in " + parentSymbolTable.id + " )");
    }

}
