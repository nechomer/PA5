package IC.asm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
	private String dts;
	private MethodLayouts ml;
	private Map<String,Boolean> lablesMap; // Contains labels, if method label than value = false
	
	public AsmTranslator(String lirFileName, String lirStr, String dispatchTableString, MethodLayouts ml) {
		this.lirFileName = lirFileName;
		this.lirStr = lirStr;
		this.dts = dispatchTableString;
		this.ml = ml;
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
		
		sb.append(StringsBuilder.exportStringLirTableForAsm());
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
    
    private static boolean isDigit(String str) {
    	
    	return Character.isDigit(str.charAt(0));
    }
    
    private static boolean isLoadArray(String str) {
    	
    	return (str.indexOf("[") != -1);
    }
    
    private static boolean isLoadField(String str) {
    	
    	return (str.indexOf(".") != -1);
    }
    
    private static boolean isDv(String str) {
    	
    	return (str.indexOf("_") != -1);
    }
    
    private static boolean isMem(String str) {
    	
    	return (str.indexOf("this") != -1);
    }
    
    private void getArrayRegs(String token, String[] regs){
    	
    	int endReg1 = token.indexOf("[");
    	int endReg2 = token.indexOf("]");
    	regs[0] = token.substring(0, endReg1-1);
    	regs[1] = token.substring(endReg1+1, endReg2-1);
    	
    }
    
    private void getFieldRegs(String token, String[] regs){
    	
    	String[] tmp = token.split(".");
    	regs[0] = tmp[0];
    	regs[1] = tmp[1];
    	
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
		
        String firstToken = null;
        String secondToken = null;
        
        int firstOffset = 0;
        int secondOffset = 0;
        
        int objectOffset = 0;
        int arrayOffset = 0;
        int indexOffset = 0;
        int destOffset = 0;
        
        
        
		
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
					
					firstToken = tokenizer.nextToken();
					secondOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					
					if (!isMem(firstToken)) {
						
						emit("movl $" + firstToken + ", " + secondOffset + "(%ebp)");
						
					} else {
						
						firstOffset = ml.getOffset(CurrMethod, firstToken);
						emit("mov " + firstOffset + "(%ebp), %eax");
						emit("movl %eax, " + secondOffset + "(%ebp)");

					}
					
				}
				else if(lirOp.equals("MoveArray")){
					
					firstToken = tokenizer.nextToken();
					secondToken = tokenizer.nextToken();
					String[] regs= new String[2];
					boolean isLoad = isLoadArray(firstToken);
					
					if(isLoad)
						getArrayRegs(firstToken, regs);
					else 
						getArrayRegs(secondToken, regs);
					
					arrayOffset = ml.getOffset(CurrMethod, regs[0]);
					indexOffset = ml.getOffset(CurrMethod, regs[1]);
					destOffset = ml.getOffset(CurrMethod, isLoad? secondToken : firstToken);
					
			        emit("mov " + arrayOffset + "(%ebp), %eax");
			        emit("mov " + indexOffset + "(%ebp), %ecx");

			        if (isLoad) {
			            emit("mov (%eax, %ecx, 4), %ebx");
			            emit("movl %ebx, " + destOffset + "(%ebp)");
			        } else {
			            emit("mov " + destOffset + "(%ebp), %ebx");
			            emit("movl %ebx, (%eax, %ecx, 4)");
			        }
					
				}
				else if(lirOp.equals("MoveField")){
					
					firstToken = tokenizer.nextToken();
					secondToken = tokenizer.nextToken();
					String[] regs= new String[2];
					boolean isLoad = isLoadField(firstToken);
					if(isLoad)
						getFieldRegs(firstToken, regs);
					else 
						getFieldRegs(secondToken, regs);
					
					objectOffset = ml.getOffset(CurrMethod, regs[0]);
					
			        emit("mov " + objectOffset + "(%ebp), %ebx"); // move object into ebx

			        if ( !isDv(firstToken)) {
			            /*NOTE: The name destOffset here is confusing, it doesn't have to be the destination.
			             * It is simply the involved register.
			             */
			            destOffset = ml.getOffset(CurrMethod, isLoad? secondToken : firstToken);
			            int fieldOffset = ml.getOffset(CurrMethod, regs[1]);
			            
			            if (isLoad) { //load into the field
			                emit("mov " + 4 * fieldOffset + "(%ebx), %eax"); //check legal
			                emit("movl %eax, " + destOffset + "(%ebp)");
			            } else {
			                emit("mov " + destOffset + "(%ebp), %eax");
			                emit("movl %eax, " + 4 * fieldOffset + "(%ebx)");
			            }
			        } else {
			            emit("movl $" + firstToken + ", (%ebx)");
			        }
					
				}
				else if(lirOp.equals("ArrayLength")){
					
					arrayOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					destOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
			        emit("mov " + arrayOffset + "(%ebp), %ebx");
			        emit("mov -4(%ebx), %ebx");
			        emit("movl %ebx, " + destOffset + "(%ebp)");
					
				}
				// Arithmetic Instruction
				else if(lirOp.equals("Add")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
		            emit("mov " + firstOffset + "(%ebp), %eax");
		            emit("add " + secondOffset + "(%ebp), %eax");
		            emit("movl %eax, " + secondOffset + "(%ebp)");

				}
				else if(lirOp.equals("Sub")){
				
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("sub " + firstOffset + "(%ebp), %eax");
		            emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Mul")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("imul " + firstOffset + "(%ebp), %eax");
		            emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Div")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
		            emit("mov $0, %edx");
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("mov " + firstOffset + "(%ebp), %ebx");
					
				}
				else if(lirOp.equals("Mod")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
		            emit("mov $0, %edx");
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("mov " + firstOffset + "(%ebp), %ebx");
					
				}
				else if(lirOp.equals("Neg")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("neg " + "%eax");
			        emit("movl %eax, " + firstOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Not")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("not " + "%eax");
			        emit("movl %eax, " + firstOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("And")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("and " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Or")){
					
					firstOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("or " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				
				else if(lirOp.equals("Xor")){
					
					firstToken = tokenizer.nextToken();
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
			        emit("mov " + "$"+ firstToken + ", %eax");
			        emit("xor " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}

				else if(lirOp.equals("Compare")){
					
					firstToken = tokenizer.nextToken();
		        	secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
			        emit("mov " + secondOffset + "(%ebp), %eax");
			        if (isDigit(firstToken)) {
			            emit("cmp $" + firstToken + ", %eax");
			        } else {
			        	firstOffset = ml.getOffset(CurrMethod,firstToken);
			            emit("cmp " + firstOffset + "(%ebp), %eax");
			        }
					
				}
				else if(lirOp.equals("Jump")){
					
					firstToken = tokenizer.nextToken();
					emit("jmp" + " " + firstToken);
					
				}
				else if(lirOp.equals("JumpTure")){
					
					firstToken = tokenizer.nextToken();
					emit("je" + " " + firstToken);
					
				}
				else if(lirOp.equals("JumpFalse")){
					
					firstToken = tokenizer.nextToken();
					emit("jne" + " " + firstToken);
					
				}
				else if(lirOp.equals("JumpG")){
					
					firstToken = tokenizer.nextToken();
					emit("jg" + " " + firstToken);
					
				}
				else if(lirOp.equals("JumpGE")){
					
					firstToken = tokenizer.nextToken();
					emit("jge" + " " + firstToken);
					
				}
				else if(lirOp.equals("JumpL")){
					
					firstToken = tokenizer.nextToken();
					emit("jl" + " " + firstToken);
					
				}
				else if(lirOp.equals("JumpLE")){
					
					firstToken = tokenizer.nextToken();
					emit("jle" + " " + firstToken);
					
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
	
    private void pushVars(String currFunction, String[] params) {
        List<Integer> regNumbers = lac.getRegisterNumbers();
        for (int i = regNumbers.size() - 1; i >= 0; i--) {
            int regOffset = currentMethod.getVarOffset("R" + regNumbers.get(i));
            emit("mov " + regOffset + "(%ebp), %eax");
            emit("push %eax");
        }
    }
	
}
