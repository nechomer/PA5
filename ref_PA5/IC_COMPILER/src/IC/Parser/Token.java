package IC.Parser;

import java_cup.runtime.Symbol;

public class Token extends Symbol {

    private int line;
    private String strValue;

    @SuppressWarnings("LeakingThisInConstructor")
    public Token(int id, int line) {
        super(id, null);
        this.line = line;
        this.value = this;
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public Token(int id, String strValue, int line) {
        super(id, null);
        this.line = line;
        this.value = this;
        this.strValue = strValue;
    }

    public int getType() {
        return super.sym;
    }

    public String getValue() {
        return this.strValue;
    }

    public int getLine() {
        return this.line;
    }

    @Override
    public String toString ()
    {
        return this.getLine () + ": " + this.getType() + "(" + this.getValue() + ")";
    }

}
