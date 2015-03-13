package IC.Type;

/**
 *
 * @author Nimrod Rappoport
 */
public class MethodType extends Type {

    Type[] paramTypes;
    Type returnType;
    String[] paramNames; // Not really needed for type checking, but would make
                         // life very easy when translating to LIR

    public MethodType(String name, Type retType, Type[] paramTypes, String[] paramNames) {
        super(name);
        this.returnType = retType;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("{");
        for (Type argType : paramTypes) {
            buf.append(argType.toString()).append(" ");
        }
        buf.append("-> ");
        buf.append(returnType.toString());
        buf.append("}");
        return buf.toString();
    }

    /**
     * <pre>
     * A:
     *   T function(T1 a1, T2 a2, T3 a3, …, Tn an)
     *
     * B extends A (B<=A):
     *   S function(S1 b1, S2 b2, S3 b3, …, Sm bm)
     * </pre>
     * 
     * The name of the functions is the same (otherwise we have no problem in the first place)
     * The argument count is the same (n=m)
     * Ti<=Si for i=1,...,n  [No! I’m not confusing!]
     * S<=T or S=T=VOID
     */
    public boolean subtypeof(Type typeA) {
        if (!(typeA instanceof MethodType)) {
            return false;
        }

        MethodType A = (MethodType) typeA;
        Type[] Ti = A.paramTypes;
        Type[] Si = this.paramTypes;

        int n = Ti.length, m = Si.length;
        if (n != m) {
            return false;
        } else {
            for (int i = 0; i < Si.length; i++) {
                if (!Ti[i].subtypeof(Si[i])) {
                    return false;
                }
            }
        }

        //                         XOR
        if (this.returnType == null ^ A.returnType == null) {
            return false;
        } else if (this.returnType == null) {
            return true;
        } else if (!this.returnType.subtypeof(A.returnType)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isReferenceType() {
        return true;
    }

    public int getParamCount() {
        return paramTypes == null ? 0 : paramTypes.length;
    }

    public Type[] getParamTypes() {
        return paramTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    public String[] getParamNames() {
        return paramNames;
    }
}
