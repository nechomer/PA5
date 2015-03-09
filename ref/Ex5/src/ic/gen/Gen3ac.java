package ic.gen;

import static ic.ir.TacValueRef.imm;
import static ic.ir.TacValueRef.lbl;
import static ic.ir.TacValueRef.loc;
import ic.ast.Visitor;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.PrimitiveType.DataType;
import ic.ast.decl.Program;
import ic.ast.decl.Type;
import ic.ast.expr.BinaryOp;
import ic.ast.expr.Length;
import ic.ast.expr.Literal;
import ic.ast.expr.NewArray;
import ic.ast.expr.NewInstance;
import ic.ast.expr.RefArrayElement;
import ic.ast.expr.RefField;
import ic.ast.expr.RefVariable;
import ic.ast.expr.StaticCall;
import ic.ast.expr.This;
import ic.ast.expr.UnaryOp;
import ic.ast.expr.VirtualCall;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StmtAssignment;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtBreak;
import ic.ast.stmt.StmtCall;
import ic.ast.stmt.StmtContinue;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtReturn;
import ic.ast.stmt.StmtWhile;
import ic.gen.ClassLayout.MethodInfo;
import ic.ir.TacInstruction;
import ic.ir.TacValueRef;
import ic.sem.ScopeNode;
import ic.sem.SemanticChecker;
import ic.sem.ScopeNode.ScopeType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class Gen3ac implements Visitor {

	/**
	 * Used to allocate indices to local variables and labels.
	 */
	static class LocalNamespace {
		private Map<String, Integer> symbol2addr;
		private int cnt;

		public LocalNamespace() {
			symbol2addr = new HashMap<>();
			cnt = 0;
		}

		public int lookup(String symbol) {
			Integer v = symbol2addr.get(symbol);
			if (v == null) {
				int fresh = fresh();
				symbol2addr.put(symbol, new Integer(fresh));
				return fresh;
			} else {
				return v.intValue();
			}
		}

		public int fresh() {
			return cnt++;
		}
	}

	private LocalNamespace locals = new LocalNamespace();
	private static Map<String, String> stringTable = new LinkedHashMap<>();
	static int lblCounter = 0;
	private Stack<Integer> loopStack = new Stack<Integer>();
	private static Map<String, ClassLayout> classLayouts = new LinkedHashMap<>();

	static {
		stringTable.put("nullPointer", "");
		stringTable.put("err1", "Runtime Error: Null pointer dereference!");
		stringTable.put("err2", "Runtime Error: Array index out of bounds!");
		stringTable.put("err3",
				"Runtime Error: Array allocation with negative array size!");
		stringTable.put("err4", "Runtime Error: Division by zero!");
	}

	protected void emit(String op, TacValueRef... args) {
		String indent = op.equals("") || op.startsWith(".") ? "" : "    ";
		System.out.println(indent + new TacInstruction(op, args));
	}

	@Override
	public Object visit(Program p) {
		emit("goto", lbl("main"));
		
		// Generate class layouts
		for (DeclClass c: p.getClasses()) {
	        ClassLayout cl = new ClassLayout(c, classLayouts.get(c.getSuperClassName()));
	        
	        // Add fields
	        for (DeclField f : c.getFields())
	            cl.addField(f.getName());
	        
	        // Add methods
	        for (DeclMethod m : c.getMethods()) {
	            if (m instanceof DeclVirtualMethod)
	                cl.addMethod(m.getName(), lbl("_" + c.getName() + "_" + m.getName()));
	        }

	        classLayouts.put(c.getName(), cl);		    
		}
		
		for (DeclClass c : p.getClasses()) {
			if (c.getName().equals("Library")) {
				continue;
			}
			c.accept(this);
		}

		// Data section
		emit(".data");
		
        // Dispatch vectors
        for (ClassLayout cl : classLayouts.values()) {
            emit("", cl.getDvLabel());
            for (MethodInfo mi : cl.getMethods()) 
                emit("(" + mi.label + ")");
        }
        
		// String Table
		for (Entry<String, String> e : stringTable.entrySet()) {
			emit("", lbl(e.getKey()));
			emit("" + e.getValue().length());
			emit("\"" + escapeString(e.getValue()) + "\"");
		}
		
		return null;
	}

	@Override
	public Object visit(DeclClass icClass) {
	    
		for (DeclMethod m : icClass.getMethods()) {
			if (m.getName().equals("main")) {
				emit("", lbl("main"));
				locals.lookup("args");
				m.accept(this);
				/* Prologue */
				emit("param", imm(0));
				emit("call", lbl("exit"));
				continue;
			}

			emit("", lbl("_" + icClass.getName() + "_" + m.getName()));
			Gen3ac g = new Gen3ac().allocFuncArgs(m.getFormals(), m instanceof DeclVirtualMethod);
			TacValueRef ret = (TacValueRef) m.accept(g);
			if (ret == null) {
				emit("ret");
			} else {
				emit("ret", ret);
			}
		}
		return null;
	}

	@Override
	public Object visit(DeclField field) {
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {
        for (Statement s : method.getStatements()) {
            if (s instanceof StmtReturn) {
                if (((StmtReturn) s).hasValue())
                    return ((StmtReturn) s).getValue().accept(this);
                else
                    return null;
            }
            s.accept(this);
        }
        return null;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		for (Statement s : method.getStatements()) {
			if (s instanceof StmtReturn) {
				if (((StmtReturn) s).hasValue())
					return ((StmtReturn) s).getValue().accept(this);
				else
					return null;
			}
			s.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		return null;
	}

	@Override
	public Object visit(PrimitiveType type) {
		return null;
	}

	@Override
	public Object visit(ClassType type) {
		return null;
	}

	@Override
	public Object visit(StmtAssignment assignment) {
		if (assignment.getVariable() instanceof RefArrayElement) {
			RefArrayElement refarr = (RefArrayElement) assignment.getVariable();
			TacValueRef arr = (TacValueRef) refarr.getArray().accept(this);
			TacValueRef ind = (TacValueRef) refarr.getIndex().accept(this);
			TacValueRef val = (TacValueRef) assignment.getAssignment().accept(
					this);
			TacValueRef size = loc(locals.fresh());
			emit("[]", arr, size);
			checkVal(ind, size, ">=", "err2");
			checkVal(ind, imm(0), "<", "err2");
			TacValueRef newloc = loc(locals.fresh());
			TacValueRef newind = loc(locals.fresh());
			emit("+", imm(1), ind, newind);
			emit("+", arr, newind, newloc);
			emit("[]=", newloc, val);
			return null;
		}
		
		if (assignment.getVariable() instanceof RefField) {
		    RefField location = (RefField)assignment.getVariable();
		    TacValueRef field_ptr = (TacValueRef) getRefFieldLvalue(location);
	        TacValueRef val = (TacValueRef) assignment.getAssignment().accept(this);
	        emit("[]=", field_ptr, val);
		    return null;
		}
		
		if (assignment.getVariable() instanceof RefVariable) {
		    RefVariable location = (RefVariable) assignment.getVariable();
	        Object locObj = location.scope.lookupId(location.getName());
	        if (locObj instanceof DeclField) {
	            TacValueRef field_ptr = getRefVarFieldLvalue((DeclField) locObj);
	            TacValueRef field_val = (TacValueRef) assignment.getAssignment().accept(this);
	            emit("[]=", field_ptr, field_val);
	            return null;
	        }
		}
		
		TacValueRef val = (TacValueRef) assignment.getAssignment().accept(this);
		TacValueRef var = (TacValueRef) assignment.getVariable().accept(this);
		emit("=", val, var);
		return null;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		return callStatement.getCall().accept(this);
	}

	@Override
	public Object visit(StmtReturn returnStatement) {
		if (returnStatement.hasValue()) {
			TacValueRef result = (TacValueRef) returnStatement.getValue()
					.accept(this);
			emit("ret", result);
			return result;
		}
		emit("ret");
		return null;
	}

	@Override
	public Object visit(StmtIf ifStatement) {
		TacValueRef ifalse = lbl("IFFalse" + lblCounter);
		TacValueRef iftrue = lbl("IFTrue" + lblCounter);
		lblCounter++;
		TacValueRef cond = (TacValueRef) ifStatement.getCondition()
				.accept(this);
		emit("if!", cond, ifalse);
		ifStatement.getOperation().accept(this);
		emit("goto", iftrue);
		emit("", ifalse);
		if (ifStatement.hasElse()) {
			ifStatement.getElseOperation().accept(this);
		}
		emit("goto", iftrue);
		emit("", iftrue);
		return null;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		TacValueRef before = lbl("W1" + lblCounter);
		TacValueRef after = lbl("W0" + lblCounter);
		loopStack.push(lblCounter);
		lblCounter++;
		emit("", before);
		TacValueRef cond = (TacValueRef) whileStatement.getCondition().accept(
				this);
		emit("if!", cond, after);

		whileStatement.getOperation().accept(this);
		emit("goto", before);
		emit("", after);
		loopStack.pop();
		return null;
	}

	@Override
	public Object visit(StmtBreak breakStatement) {
		emit("goto", lbl("W0" + loopStack.peek()));
		return null;
	}

	@Override
	public Object visit(StmtContinue continueStatement) {
		emit("goto", lbl("W1" + loopStack.peek()));
		return null;
	}

	@Override
	public Object visit(StmtBlock statementsBlock) {
		for (Statement s : statementsBlock.getStatements()) {
			s.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		TacValueRef var = loc(locals.lookup(localVariable.getName()));
		if (!localVariable.isInitialized()) {
			if (localVariable.getType().getArrayDimension() > 0) {
				emit("=", lbl("nullPointer"), var);
			}
			return null;
		}
		TacValueRef val = (TacValueRef) localVariable.getInitialValue().accept(
				this);
		emit("=", val, var);
		return null;
	}

	@Override
	public Object visit(RefVariable location) {
	    Object locObj = location.scope.lookupId(location.getName());
	    if (locObj instanceof DeclField) {
	        TacValueRef field_ptr = getRefVarFieldLvalue((DeclField) locObj);
	        TacValueRef field_val = loc(locals.fresh());
	        emit("[]", field_ptr, field_val);
	        return field_val;
	    }
	    
		return loc(locals.lookup(location.getName()));
	}

	@Override
	public Object visit(RefField location) {
	    TacValueRef field_ptr = (TacValueRef) getRefFieldLvalue(location);
	    
        TacValueRef field_val = loc(locals.fresh());
	    emit("[]", field_ptr, field_val);
		return field_val;
	}

	@Override
	public Object visit(RefArrayElement location) {
		TacValueRef arr = (TacValueRef) location.getArray().accept(this);
		checkVal(arr, lbl("nullPointer"), "==", "err1");
		TacValueRef ind = (TacValueRef) location.getIndex().accept(this);
		TacValueRef size = loc(locals.fresh());
		emit("[]", arr, size);
		checkVal(ind, size, ">=", "err2");
		checkVal(ind, imm(0), "<", "err2");
		TacValueRef newloc = loc(locals.fresh());
		TacValueRef newind = loc(locals.fresh());
		emit("+", imm(1), ind, newind);
		emit("+", arr, newind, newloc);
		TacValueRef val = loc(locals.fresh());
		emit("[]", newloc, val);
		return val;
	}

	@Override
	public Object visit(StaticCall call) {
		TacValueRef[] vars = new TacValueRef[call.getArguments().size()];
		TacValueRef res = loc(locals.fresh());
		for (int i = 0; i < call.getArguments().size(); i++) {
			vars[i] = (TacValueRef) call.getArguments().get(i).accept(this);
		}
		for (TacValueRef v : vars) {
			emit("param", v);
		}
		emit("call",
				lbl(!call.getClassName().equals("Library") 
				        ? "_" + call.getClassName() + "_" + call.getMethod()
				        : call.getMethod()), res);
		return res;
	}

	@Override
	public Object visit(VirtualCall call) {
	    TacValueRef obj_ptr;
	    DeclMethod m;
	    ClassLayout cl;
	    
	    // handle static calls
	    m = (DeclMethod) call.scope.lookupId(call.getMethod());
	    if (m instanceof DeclStaticMethod) {
            TacValueRef[] vars = new TacValueRef[call.getArguments().size()];
            TacValueRef res = loc(locals.fresh());
            for (int i = 0; i < call.getArguments().size(); i++) {
                vars[i] = (TacValueRef) call.getArguments().get(i).accept(this);
            }
            for (TacValueRef v : vars) {
                emit("param", v);
            }
            emit("call", lbl("_" + m.scope.getParent().getName() + "_" + call.getMethod()), res);
            return res;
	    }
	    
		if (call.hasExplicitObject()) {
		    obj_ptr = (TacValueRef) call.getObject().accept(this);
		    
		    // make sure that the object is not null
		    checkVal(obj_ptr, lbl("nullPointer"), "==", "err1");
		    
	        cl = classLayouts.get(call.getObject().nodeType.getDisplayName());
	        // find the scope of caller class
	        ScopeNode clScope = findClassScope(call.scope, call.getObject().nodeType.getDisplayName());
	        m = (DeclMethod)clScope.lookupId(call.getMethod());	        
		} else {
            obj_ptr = loc(locals.lookup("this"));
		    m = (DeclMethod) call.scope.lookupId(call.getMethod());
	        cl = classLayouts.get(m.scope.getParent().getName());
		}		

        TacValueRef dv_ptr = loc(locals.fresh());
        TacValueRef method_ptr = loc(locals.fresh());
        TacValueRef method_addr = loc(locals.fresh());
		
		emit("[]", obj_ptr, dv_ptr);
		emit("+", dv_ptr, imm(cl.getMethodOffset(m.getName())), method_ptr);
		emit("[]", method_ptr, method_addr);
		
        TacValueRef[] vars = new TacValueRef[call.getArguments().size()];
        TacValueRef res = loc(locals.fresh());
        for (int i = 0; i < call.getArguments().size(); i++) {
            vars[i] = (TacValueRef) call.getArguments().get(i).accept(this);
        }
        
        // "this" pointer must be the first argument to the call
        emit("param", obj_ptr);
        
        for (TacValueRef v : vars) {
            emit("param", v);
        }
        
        emit("call", method_addr, res);
        return res;
	}

	@Override
	public Object visit(This thisExpression) {
		return loc(locals.lookup("this"));
	}

	@Override
	public Object visit(NewInstance newClass) {
	    ClassLayout cl = classLayouts.get(newClass.getName());

	    TacValueRef class_ptr = loc(locals.fresh());
	    emit("param", imm(cl.getFieldCount() + 1));
	    emit("call", lbl("alloc"), class_ptr);
	    emit("[]=", class_ptr, cl.getDvLabel());
	    
		return class_ptr;
	}

	@Override
	public Object visit(NewArray newArray) {

		TacValueRef size = (TacValueRef) newArray.getSize().accept(this);
		checkVal(size, imm(0), "<", "err3");
		TacValueRef arr_pointer = loc(locals.fresh());
		TacValueRef new_size = loc(locals.fresh());
		emit("+", imm(1), size, new_size);
		emit("param", new_size);
		emit("call", lbl("alloc"), arr_pointer);
		emit("[]=", arr_pointer, size);
		return arr_pointer;
	}

	@Override
	public Object visit(Length length) {

		TacValueRef arr = (TacValueRef) length.getArray().accept(this);
		checkVal(arr, lbl("nullPointer"), "==", "err1");
		TacValueRef result = loc(locals.fresh());
		emit("[]", arr, result);
		return result;
	}

	@Override
	public Object visit(Literal literal) {

		if (literal.getType() == DataType.INT) {
			return imm(Integer.parseInt((String) literal.getValue()));
		} else if (literal.getType() == DataType.BOOLEAN) {
			if (((String) literal.getValue()).equals("true")) {
				return imm(1);
			} else {
				return imm(0);
			}
		} else if (literal.getType() == DataType.STRING) {
			int label = stringTable.size();
			stringTable.put(String.valueOf(label), (String) literal.getValue());
			return lbl(Integer.toString(label));
		} else {
			return lbl("nullPointer");
		}
	}

	@Override
	public Object visit(UnaryOp unaryOp) {

		TacValueRef operand = (TacValueRef) unaryOp.getOperand().accept(this);
		TacValueRef result = loc(locals.fresh());
		emit(unaryOp.getOperator().toString(), operand, result);
		return result;
	}

	@Override
	public Object visit(BinaryOp binaryOp) {

		switch (binaryOp.getOperator().toString()) {
		case "||": {
			TacValueRef first = (TacValueRef) binaryOp.getFirstOperand()
					.accept(this);
			TacValueRef result = loc(locals.fresh());
			TacValueRef flabel = lbl("CF" + lblCounter);
			TacValueRef tlabel = lbl("CT" + lblCounter);
			lblCounter++;
			emit("if!", first, flabel);
			emit("=", first, result);
			emit("goto", tlabel);
			emit("", flabel);
			TacValueRef second = (TacValueRef) binaryOp.getSecondOperand()
					.accept(this);
			emit("=", second, result);
			emit("goto", tlabel);
			emit("", tlabel);
			return result;
		}
		case "&&": {
			TacValueRef first = (TacValueRef) binaryOp.getFirstOperand()
					.accept(this);
			TacValueRef result = loc(locals.fresh());
			TacValueRef flabel = lbl("CF" + lblCounter);
			TacValueRef tlabel = lbl("CT" + lblCounter);
			lblCounter++;
			emit("if!", first, flabel);
			TacValueRef second = (TacValueRef) binaryOp.getSecondOperand()
					.accept(this);
			emit("=", second, result);
			emit("goto", tlabel);
			emit("", flabel);
			emit("=", first, result);
			emit("goto", tlabel);
			emit("", tlabel);
			return result;
		}
		case "+": {
			SemanticChecker sem = new SemanticChecker();
			Type t = (Type) binaryOp.getFirstOperand().accept(sem);
			TacValueRef first = (TacValueRef) binaryOp.getFirstOperand()
					.accept(this);
			TacValueRef second = (TacValueRef) binaryOp.getSecondOperand()
					.accept(this);
			if (t instanceof PrimitiveType) {
				if (((PrimitiveType) t).getDisplayName().equals("string")) {
					checkVal(first, lbl("nullPointer"), "==", "err1");
					checkVal(second, lbl("nullPointer"), "==", "err1");
					TacValueRef result = loc(locals.fresh());
					emit("param", first);
					emit("param", second);
					emit("call", lbl("stringCat"), result);
					return result;
				}
			}
			TacValueRef result = loc(locals.fresh());
			emit(binaryOp.getOperator().toString(), first, second, result);
			return result;

		}
		case "/":
		case "%": {
			TacValueRef second = (TacValueRef) binaryOp.getSecondOperand()
					.accept(this);
			checkVal(second, imm(0), "==", "err4");
			TacValueRef first = (TacValueRef) binaryOp.getFirstOperand()
					.accept(this);
			TacValueRef result = loc(locals.fresh());

			emit(binaryOp.getOperator().toString(), first, second, result);
			return result;
		}

		default: {
			TacValueRef first = (TacValueRef) binaryOp.getFirstOperand()
					.accept(this);

			TacValueRef second = (TacValueRef) binaryOp.getSecondOperand()
					.accept(this);

			TacValueRef result = loc(locals.fresh());

			emit(binaryOp.getOperator().toString(), first, second, result);
			return result;
		}
		}

	}

	private void checkVal(TacValueRef val, TacValueRef n, String cond,
			String err) {
		TacValueRef flabel = lbl("CF" + lblCounter);
		TacValueRef tlabel = lbl("CT" + lblCounter);
		TacValueRef result = loc(locals.fresh());
		lblCounter++;
		emit(cond, val, n, result);
		emit("if!", result, flabel);

		emit("param", lbl(err));
		emit("call", lbl("println"));
		emit("param", imm(1));
		emit("call", lbl("exit"));

		emit("goto", tlabel);
		emit("", flabel);
		emit("goto", tlabel);
		emit("", tlabel);
	}

	Gen3ac allocFuncArgs(List<Parameter> args, boolean isVirtual) {
	    if (isVirtual) 
	        locals.lookup("this");
	    
		for (Parameter a : args)
			locals.lookup(a.getName());
		return this;
	}
	
	private ScopeNode findClassScope(ScopeNode node, String className) {
	    if (node == null)
	        return null;
	    
	    while (node.getType() != ScopeType.Global)
	        node = node.getParent();

	    return node.getClass(className).scope;
	}
	
	// Get the l-value of an expression of type o.f
	private TacValueRef getRefFieldLvalue(RefField location) {
	    TacValueRef obj_ptr = (TacValueRef)location.getObject().accept(this);
	    
        // make sure that the object is not null
        checkVal(obj_ptr, lbl("nullPointer"), "==", "err1");
	    
        ClassType ct = (ClassType)location.getObject().nodeType;        
        ClassLayout cl = classLayouts.get(ct.getClassName());   
        TacValueRef field_ptr = loc(locals.fresh());
        emit("+", obj_ptr, imm(cl.getFieldOffset(location.getField())), field_ptr);
        return field_ptr;
	}
	
	private TacValueRef getRefVarFieldLvalue(DeclField f) {
        ClassLayout cl = classLayouts.get(f.scope.getName());   
        TacValueRef field_ptr = loc(locals.fresh());
        emit("+", loc(locals.lookup("this")), imm(cl.getFieldOffset(f.getName())), field_ptr);
        return field_ptr;
	}

	private static String escapeString(String s) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\n':
				b.append("\\n");
				break;
			case '\t':
				b.append("\\t");
				break;
			case '\\':
				b.append("\\\\");
				break;
			case '\"':
				b.append("\\\"");
				break;
			default:
				b.append(c);
			}
		}
		return b.toString();
	}

}
