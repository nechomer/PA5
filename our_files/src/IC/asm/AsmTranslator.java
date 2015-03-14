package IC.asm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import IC.lir.DispatchTableBuilder;
import IC.lir.StringsBuilder;

public class AsmTranslator {
	
	private String lirFileName;
	private String lirStr;
	private StringBuilder sb; 
	private StringsBuilder ssb;
	private String dts;
	private Map<String,Boolean> lablesMap; // Contains labels, if method label than value = false
	
	public AsmTranslator(String lirFileName, String lirStr, StringsBuilder ssb, String dispatchTableString) {
		this.lirFileName = lirFileName;
		this.lirStr = lirStr;
		this.ssb = ssb;
		this.dts = dispatchTableString;
		this.lablesMap = new HashMap<String,Boolean>();
		this.sb = new StringBuilder();
	}
	
	public void makeFileProlog() {
		int index = lirFileName.lastIndexOf(File.separator);
		String result = ".title " + "\"" + lirFileName.substring(index+1);
		result = result.replaceAll(".lir", ".ic");
		result += "\"\n\n";
		result += "# global declarations\n";
		result += ".global __ic_main\n\n";
		result += "# data section \n.data\n";
		result += "\t.align 4\n\n";
		sb.append(result);
	}
	
	public void makeConstantStrings() {
		
		sb.append(this.ssb.exportStringLirTableForAsm());
	}
	
	public void makeDV() {
		
		BufferedReader bufReader = new BufferedReader(new StringReader(dts));
		String line = null;
		try {
			while ((line = bufReader.readLine()) != null) {
				
				if (line.startsWith("#")) continue;
				String[] words = line.split("[\\[\\,\\]]+");
				String result = words[0];
				for (int i = 1; i< words.length; i++) {
					result += " .long " + words[i] + "\n\t";
				}
				sb.append(result + "\n");
			}
		} catch (IOException e) {

		}
	}

	
	public void makeLableMap() {
		
		BufferedReader bufReader = new BufferedReader(new StringReader(lirStr));
		String line = null;
		try {
			while ((line = bufReader.readLine()) != null) {
				
				if (line.startsWith("_")) {
					lablesMap.put(line.substring(0, line.length()-2), false);
				}
				
				if (line.startsWith("jump")) {
					
					String[] jumpLine = line.split(" ");
					String jumpLabel = jumpLine[1].substring(0, jumpLine[1].length()-1);
					lablesMap.put(jumpLabel, true);
				}

			}
		} catch (IOException e) {

		}
		
	}
	
    private void emit(String str) {
        sb.append(str).append("\n");
    }
	
	public void translateLirToAsm() throws IOException {
		
		String asmFileName = lirFileName.replaceAll(".lir$", ".s");
		BufferedReader bufReader = new BufferedReader(new StringReader(lirStr));
		String line = null;
		String CurrMethod = null;
		BufferedWriter fw = new BufferedWriter(new FileWriter(asmFileName));
		
		makeFileProlog();
		makeConstantStrings();
		makeDV();
		
		// Aritmetic operands
        int firstOffset = currentMethod.getVarOffset("R" + lab.getParam1());
        int secondOffset = currentMethod.getVarOffset("R" + lab.getParam2());
        
        // ArrayLength 
        int arrayOffset = currentMethod.getVarOffset("R" + lal.getArrayReg());
        int destOffset = currentMethod.getVarOffset("R" + lal.getDestReg());
        
        // Compare
        int secondOffset = currentMethod.getVarOffset("R" + lc.getOp2());
        
        // move
        int regOffset = currentMethod.getVarOffset("R" + lm.getRegNum());
        
        //move array
        int arrayOffset = currentMethod.getVarOffset("R" + lma.getArrayReg());
        int indexOffset = currentMethod.getVarOffset("R" + lma.getArrayIndex());
        int destOffset = currentMethod.getVarOffset("R" + lma.getDestReg());
        
        //move field
        int objectOffset = currentMethod.getVarOffset("R" + lmf.getObjectReg());
        
        // static call
        int targetOffset = currentMethod.getVarOffset("R" + lsc.getTargetRegister());
        
		
		try {
			while ((line = bufReader.readLine()) != null) {

				if (line.startsWith("#")) continue;
				if (line.length() == 0) continue;
				
				if (line.startsWith("_")) {
					String label = line.substring(0, line.length()-2);
				}
				
				StringTokenizer tokenizer = new StringTokenizer(line); 	
				String lirOp = tokenizer.nextToken();
				if(lirOp.equals("Move")){
					
			        
			        if (lm.getMemory() == null) {
			            emit("movl $" + lm.getConstant() + ", " + regOffset + "(%ebp)");
			        } else {
			            int memoryOffset = currentMethod.getVarOffset(lm.getMemory());
			            if (lm.isToReg()) {
			                emit("mov " + memoryOffset + "(%ebp), %eax");
			                emit("movl %eax, " + regOffset + "(%ebp)");
			            } else {
			                emit("mov " + regOffset + "(%ebp), %eax");
			                emit("movl %eax, " + memoryOffset + "(%ebp)");
			            }
			        }
					
				}
				else if(lirOp.equals("MoveArray")){
					
			        emit("mov " + arrayOffset + "(%ebp), %eax");
			        emit("mov " + indexOffset + "(%ebp), %ecx");
			        // null pointer check
			        emit("cmp $0, %eax");
			        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
			        // index out of bounds check
			        emit("mov -4(%eax), %ebx");
			        emit("cmp %ecx, %ebx");
			        emit("jle " + Info.ErrorMessages.ArrayIndexOutOfBounds.getFunctionLabel());
			        emit("cmp $0, %ecx");
			        emit("jl " + Info.ErrorMessages.ArrayIndexOutOfBounds.getFunctionLabel());

			        if (lma.isStore()) {
			            emit("mov " + destOffset + "(%ebp), %ebx");
			            emit("movl %ebx, (%eax, %ecx, 4)");
			        } else {
			            emit("mov (%eax, %ecx, 4), %ebx");
			            emit("movl %ebx, " + destOffset + "(%ebp)");
			        }
					
				}
				else if(lirOp.equals("MoveField")){
					
			        emit("mov " + objectOffset + "(%ebp), %ebx"); // move object into ebx
			        // null pointer exception check
			        emit("cmp $0, %ebx");
			        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
			        if (lmf.getDispatch() == null) {
			            /*NOTE: The name destOffset here is confusing, it doesn't have to be the destination.
			             * It is simply the involved register.
			             */
			            int destOffset = currentMethod.getVarOffset("R" + lmf.getDestReg());
			            int fieldOffset = lmf.getFieldOffset();
			            if (lmf.isStore()) { //store into the field
			                emit("mov " + destOffset + "(%ebp), %eax");
			                emit("movl %eax, " + 4 * fieldOffset + "(%ebx)");
			            } else {
			                emit("mov " + 4 * fieldOffset + "(%ebx), %eax"); //check legal
			                emit("movl %eax, " + destOffset + "(%ebp)");
			            }
			        } else {
			            emit("movl $" + lmf.getDispatch() + ", (%ebx)");
			        }
					
				}
				else if(lirOp.equals("ArrayLength")){
					
			        emit("mov " + arrayOffset + "(%ebp), %ebx");
			        // check array is not null
			        emit("cmp $0, %ebx");
			        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());
			        // end of check
			        emit("mov -4(%ebx), %ebx");
			        emit("movl %ebx, " + destOffset + "(%ebp)");
					
				}
				// Arithmetic Instruction
				else if(lirOp.equals("Add")){
					
		            emit("mov " + firstOffset + "(%ebp), %eax");
		            emit("add " + secondOffset + "(%ebp), %eax");
		            emit("movl %eax, " + secondOffset + "(%ebp)");

				}
				else if(lirOp.equals("Sub")){
					
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("sub " + firstOffset + "(%ebp), %eax");
		            emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Mul")){
					
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("imul " + firstOffset + "(%ebp), %eax");
		            emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Div")){
					
		            emit("mov $0, %edx");
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("mov " + firstOffset + "(%ebp), %ebx");
		            // division by zero check
		            emit("cmp $0, %eax");
		            emit("je " + Info.ErrorMessages.DivisionByZero.getFunctionLabel());
		            // end of division by zero check
		            emit("idiv %ebx");
		            emit("movl " + (op.equals("Div") ? "%eax, " : "%edx, ") + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Mod")){
					
		            emit("mov $0, %edx");
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("mov " + firstOffset + "(%ebp), %ebx");
					
				}
				else if(lirOp.equals("Inc")){
					
				}
				else if(lirOp.equals("Dic")){
					
				}
				else if(lirOp.equals("Neg")){
					
			        int paramOffset = currentMethod.getVarOffset("R" + luo.getParam());
			        emit("mov " + paramOffset + "(%ebp), %eax");
			        emit((luo.isIsNeg() ? "neg" : "not") + " %eax");
			        emit("movl %eax, " + paramOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Not")){
					
			        int paramOffset = currentMethod.getVarOffset("R" + luo.getParam());
			        emit("mov " + paramOffset + "(%ebp), %eax");
			        emit((luo.isIsNeg() ? "neg" : "not") + " %eax");
			        emit("movl %eax, " + paramOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("And")){
					
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("and " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Or")){
					
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("or " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Xor")){
					
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("xor " secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Compare")){
					
			        emit("mov " + secondOffset + "(%ebp), %eax");
			        if (lc.isLiteral()) {
			            emit("cmp $" + lc.getOp1() + ", %eax");
			        } else {
			            int firstOffset = currentMethod.getVarOffset("R" + lc.getOp1());
			            emit("cmp " + firstOffset + "(%ebp), %eax");
			        }
					
				}
				else if(lirOp.equals("Jump")){
					
					emit("jmp" + " " + lj.getJumpTo());
					
				}
				else if(lirOp.equals("JumpTure")){
					
					emit("je" + " " + lj.getJumpTo());
					
				}
				else if(lirOp.equals("JumpFalse")){
					
					emit("jne" + " " + lj.getJumpTo());
					
				}
				else if(lirOp.equals("JumpG")){
					
					emit("jg" + " " + lj.getJumpTo());
					
				}
				else if(lirOp.equals("JumpGE")){
					
					emit("jge" + " " + lj.getJumpTo());
					
				}
				else if(lirOp.equals("JumpL")){
					
					emit("jl" + " " + lj.getJumpTo());
					
				}
				else if(lirOp.equals("JumpLE")){
					
					emit("jle" + " " + lj.getJumpTo());
					
				}
				else if(lirOp.equals("Library")){
					
				}
				else if(lirOp.equals("StaticCall")){
					
			        pushVars(lsc);
			        emit("call " + lsc.getMethodName());
			        emit("movl %eax, " + targetOffset + "(%ebp)");
			        emit("add $" + 4 * lsc.getRegisterNumbers().size() + ", %esp");
					
				}
				else if(lirOp.equals("VirtualCall")){
					
			        int targetOffset = currentMethod.getVarOffset("R" + lvc.getTargetRegister());
			        int objectOffset = currentMethod.getVarOffset("R" + lvc.getObjectRegister());
			        pushVars(lvc);
			        emit("mov " + objectOffset + "(%ebp), %eax");
			        // check for null pointer exception
			        emit("cmp $0, %eax");
			        emit("je " + Info.ErrorMessages.NullPointerReferece.getFunctionLabel());

			        emit("push %eax");
			        emit("mov 0(%eax), %eax");
			        emit("call *" + 4 * lvc.getMethodOffset() + "(%eax)");

			        emit("movl %eax, " + targetOffset + "(%ebp)");
			        emit("add $" + 4 * (lvc.getRegisterNumbers().size() + 1) + ", %esp"); // +1 for this
					
				}
				else if(lirOp.equals("Return")){
					
			        if (lr.getParam() != -1) {
			            int paramOffset = currentMethod.getVarOffset("R" + lr.getParam());
			            emit("mov " + paramOffset + "(%ebp), %eax");
			        }
			        emit("jmp " + currentMethodLabelName + "_epilogue");
					
				}
			}
		} catch (IOException e) {

		}

		fw.write("# text (code) section\n\t.text\n\n");
		fw.close();
	}
	
}
