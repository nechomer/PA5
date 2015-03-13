/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.Parser;

import IC.AST.*;
import IC.AST.Field;
import IC.AST.Method;
import java.util.*;

/**
 *
 * @author omer
 */
public class ClassBody {
    private List<Field> fields;
    private List<Method> methods;

    public ClassBody() {
        fields=new ArrayList<Field>();
        methods=new ArrayList<Method>();
    }

    public void add(List<Field> fields2) {
        for (Field field : fields2)
            fields.add(field);
    }

    public void add(Method m) {
        methods.add(m);
    }

    public List<Method> getMethods() {
        return this.methods;
    }

    public List<Field> getFields() {
        return this.fields;
    }

}
