package IC.Parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java_cup.runtime.Symbol;

/**
 * Report errors properly, with something more readable than just a token's int
 * identifier from the symbol file.
 * @author Barak Itkin
 */
public class ErrorReporting {

    public static void syntax_error(Symbol symbol) {
        Token t = (Token) symbol;
        String tokenName = "";
        String tokenVal = (t.getValue() == null) ? "" : ("(" + t.getValue() + ")");

        /* Now, we use java's reflection mechanisem to try and locate the
         * name of the token's type (i.e. it's identifier from sym). We pass
         * over each field inside sym and try to see if it's equal to our
         * value.
         *
         * Just an enhancement to provide better error reporting (since we
         * only have the sym number of the token, but that does not allow us
         * to figure out which token this was).
         */
        for (Field field : sym.class.getDeclaredFields()) {
            try {
                if (field.getType() == int.class
                    && Modifier.isStatic(field.getModifiers())
                    && field.getInt(null) == t.sym) {
                    tokenName = field.getName();
                    break;
                }
            } catch (Exception ex) { }
        }

        System.err.println("Unexpected token at line " + t.getLine() + ": " + tokenName + tokenVal);

    }
}
