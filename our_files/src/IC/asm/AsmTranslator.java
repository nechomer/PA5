package IC.asm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import IC.Parser.sym;
import IC.lir.DispatchTableBuilder;
import IC.lir.StringsBuilder;

public class AsmTranslator {
	
	private String lirFileName;
	private String lirStr;
	private StringBuilder sb; 
	private String dts;
	private MethodLayouts ml;
	private Map<String,Boolean> lablesMap; // Contains labels, if method label than value = false
	private Map<String, String> regToDVPtr;
	
	public AsmTranslator(String lirFileName, String lirStr, String dispatchTableString, MethodLayouts ml) {
		this.lirFileName = lirFileName;
		this.lirStr = lirStr;
		this.dts = dispatchTableString;
		this.ml = ml;
		this.lablesMap = new HashMap<String,Boolean>();
		this.regToDVPtr = new HashMap<String, String>();
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
			sb.append(".text\n");
		} catch (IOException e) {

		}
	}

	
	public void makeLableMap() {
		
		BufferedReader bufReader = new BufferedReader(new StringReader(lirStr));
		String line = null;
		try {
			while ((line = bufReader.readLine()) != null) {
				
				if (line.startsWith("_")) {
					lablesMap.put(line.substring(0, line.length()-1), false);
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
	

	
	public void translateLirToAsm() throws IOException {
		
		BufferedReader bufReader = new BufferedReader(new StringReader(lirStr));
		String line = null;
		String CurrMethod = null;
		
		makeFileProlog();
		makeConstantStrings();
		makeDV();
        makeLableMap();
		
        String firstToken = null;
        String secondToken = null;
        
        int firstOffset = 0;
        int secondOffset = 0;
        
        int objectOffset = 0;
        int arrayOffset = 0;
        int indexOffset = 0;
        int destOffset = 0;
        
			while ((line = bufReader.readLine()) != null) {
				
				if (line.length() == 0) continue;
				
				if (line.startsWith("#")) {
					emit(line);
					if(line.equals("# End Of Method Block"))
						makeEpilogueForFunc(CurrMethod);
					continue;
				}
				
				if (line.startsWith("_")) {
					String label = line.substring(0, line.length()-1);
					System.out.println(label);
					if(!lablesMap.get(label)) {  // Its a method
						CurrMethod = label;
						emit(".align 4");
						emit(CurrMethod + ":");
						makePrologue(CurrMethod);
					} else{

					}
					continue;
				}
				
				StringTokenizer tokenizer = new StringTokenizer(line); 	
				String lirOp = tokenizer.nextToken();
				if(lirOp.equals("Move")){
					
					firstToken = formatStr(tokenizer.nextToken());
					secondToken = tokenizer.nextToken();
					updateRegs(firstToken, secondToken);
					secondOffset = ml.getOffset(CurrMethod, secondToken);
					
					if (!isMem(firstToken) && ml.getOffset(CurrMethod, firstToken) == 0) {
						
						emit("movl $" + firstToken + ", " + secondOffset + "(%ebp)");
						
					} else {
						
						firstOffset = ml.getOffset(CurrMethod, firstToken);
						emit("mov " + firstOffset + "(%ebp), %eax");
						emit("movl %eax, " + secondOffset + "(%ebp)");

					}
					
				}
				else if(lirOp.equals("MoveArray")){
					
					firstToken = formatStr(tokenizer.nextToken());
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
					
					firstToken = formatStr(tokenizer.nextToken());
					secondToken = tokenizer.nextToken();
					updateRegs(firstToken, secondToken);
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
					
					arrayOffset = ml.getOffset(CurrMethod, formatStr(tokenizer.nextToken()));
					destOffset = ml.getOffset(CurrMethod, tokenizer.nextToken());
			        emit("mov " + arrayOffset + "(%ebp), %ebx");
			        emit("mov -4(%ebx), %ebx");
			        emit("movl %ebx, " + destOffset + "(%ebp)");
					
				}
				// Arithmetic Instruction
				else if(lirOp.equals("Add")){
					
					firstToken = formatStr(tokenizer.nextToken());
					if(!isDigit(firstToken)){
						firstOffset = ml.getOffset(CurrMethod, firstToken);
						emit("mov " + firstOffset + "(%ebp), %eax");
						
					}else{
						emit("mov $" + firstToken + ", %eax");
					}
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
		            
		            emit("add " + secondOffset + "(%ebp), %eax");
		            emit("movl %eax, " + secondOffset + "(%ebp)");

				}
				else if(lirOp.equals("Sub")){
				
					firstToken = formatStr(tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
					emit("mov " + secondOffset + "(%ebp), %eax");
					
					
					if(!isDigit(firstToken)){
						firstOffset = ml.getOffset(CurrMethod, firstToken);
						emit("sub " + firstOffset + "(%ebp), %eax");
						
					}else{
						emit("sub $" + firstToken + ", %eax");
					}
					
					
		            emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Mul")){
					
					firstToken = formatStr(tokenizer.nextToken());
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
					emit("mov " + secondOffset + "(%ebp), %eax");
					
					
					if(!isDigit(firstToken)){
						firstOffset = ml.getOffset(CurrMethod, firstToken);
						emit("imul " + firstOffset + "(%ebp), %eax");
						
					}else{
						emit("imul $" + firstToken + ", %eax");
					}
					
		            emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Div")){
					
					firstOffset = ml.getOffset(CurrMethod, formatStr(tokenizer.nextToken()));
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
		            emit("mov $0, %edx");
		            emit("mov " + secondOffset + "(%ebp), %eax");
		            emit("mov " + firstOffset + "(%ebp), %ebx");
					
				}
				else if(lirOp.equals("Mod")){
					
					firstOffset = ml.getOffset(CurrMethod, formatStr(tokenizer.nextToken()));
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
					
					firstOffset = ml.getOffset(CurrMethod, formatStr(tokenizer.nextToken()));
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("and " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				else if(lirOp.equals("Or")){
					
					firstOffset = ml.getOffset(CurrMethod, formatStr(tokenizer.nextToken()));
					secondOffset = ml.getOffset(CurrMethod,tokenizer.nextToken());
			        emit("mov " + firstOffset + "(%ebp), %eax");
			        emit("or " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}
				
				else if(lirOp.equals("Xor")){
					
					firstToken = tokenizer.nextToken();
					secondOffset = ml.getOffset(CurrMethod,formatStr(tokenizer.nextToken()));
			        emit("mov " + "$"+ firstToken + ", %eax");
			        emit("xor " + secondOffset + "(%ebp), %eax");
			        emit("movl %eax, " + secondOffset + "(%ebp)");
					
				}

				else if(lirOp.equals("Compare")){
					
					firstToken = formatStr(tokenizer.nextToken());
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
					
					firstToken = tokenizer.nextToken();
					String funcName = getFuncFromCall(firstToken);
					secondToken = tokenizer.nextToken();
					String[] regs = getRegsFromLibCall(getParamsFromCall(firstToken));
					int regOffset = 0;
					
					for(String reg:regs){
						if(reg.startsWith("R")){
				            regOffset = ml.getOffset(CurrMethod, reg);
				            emit("mov " + regOffset + "(%ebp), %eax");
				            emit("push %eax");
						} else{
							emit("push $" + reg);
						}
							
					}
					
					emit("call " + funcName);
					
					if(!secondToken.equals("Rdummy")) {
						
						firstOffset = ml.getOffset(CurrMethod, secondToken);
						emit("movl %eax, " + firstOffset + "(%ebp)");
						
					}
					
					if(regs.length != 0)
						emit("add $" + 4 * regs.length + ", %esp");
							
				}
				else if(lirOp.equals("StaticCall")){
					
					firstToken = formatStr(tokenizer.nextToken());
					secondToken = tokenizer.nextToken();
					Map<String,String> paramMap = makeParamsToRegs(getParamsFromCall(firstToken));
					String funcName = getFuncFromCall(firstToken);
					
					if(paramMap != null)
						pushVars(CurrMethod, funcName, paramMap);
					
					emit("call " + funcName);
					if(!secondToken.equals("Rdummy")) {
						
						firstOffset = ml.getOffset(CurrMethod, secondToken);
						emit("movl %eax, " + firstOffset + "(%ebp)");
						
					}
					
					if(paramMap != null)
						emit("add $" + 4 * paramMap.size() + ", %esp");
				}
				else if(lirOp.equals("VirtualCall")){
					
					firstToken = tokenizer.nextToken();
					secondToken = tokenizer.nextToken();
					
					Map<String,String> paramMap = makeParamsToRegs(getParamsFromCall(firstToken));
					String className = regToDVPtr.get(removeDot(firstToken)); 
					String funcName = DispatchTableBuilder.getFuncName(className, getVirtualMethodOffset(firstToken));
					String[] regs = new String[2];
					
					if(paramMap != null)
						pushVars(CurrMethod, funcName, paramMap);
					
					getFieldRegs(getFuncFromCall(firstToken),regs);
					
					objectOffset = ml.getOffset(CurrMethod, regs[0]);
					emit("mov " + objectOffset + "(%ebp), %eax");
			        emit("push %eax");
			        emit("mov 0(%eax), %eax");

			        emit("call *" + 4 * Integer.parseInt(regs[1]) + "(%eax)");
			        
					if(!secondToken.equals("Rdummy")) {
						
						firstOffset = ml.getOffset(CurrMethod, secondToken);
						emit("movl %eax, " + firstOffset + "(%ebp)");
						
					}
					
					if(paramMap != null)
						emit("add $" + 4 * (paramMap.size() + 1) + ", %esp");
					
				}
				else if(lirOp.equals("Return")){

					firstToken = tokenizer.nextToken();

					if(!firstToken.equals("Rdummy")) {
						firstOffset = ml.getOffset(CurrMethod, firstToken);
						emit("mov " + firstOffset + "(%ebp), %eax");

					}
					emit("jmp " + CurrMethod + "_epilogue");

				}
			}
			
			String asmFileName = lirFileName.replaceAll(".ic$", ".s");
			
			FileWriter fw = null;
			try {
				fw = new FileWriter(asmFileName);
				fw.write(sb.toString());
			} catch (IOException e) {
			}
			finally {
				if(fw!=null)
					fw.close();
			}

	}

    private void pushVars(String fromFunction, String toFunction, Map<String,String> paramsToRegs) {
       
    	List<String> params = ml.getParamsReverseList(toFunction);
        int regOffset = 0;
        for (String param:params ) {
            regOffset = ml.getOffset(fromFunction, paramsToRegs.get(param));
            emit("mov " + regOffset + "(%ebp), %eax");
            emit("push %eax");
        }
    }
    
    private static Map<String,String> makeParamsToRegs(String str){
    	 	
		StringTokenizer tokenizer = new StringTokenizer(str,",");
		if(!tokenizer.hasMoreTokens())
			return null;
		
		Map<String,String> ret = new HashMap<String,String>();
		String paramToRegStr = null;
		String[] paramToReg;
		
		while(tokenizer.hasMoreTokens()) {
			paramToRegStr = formatStr(tokenizer.nextToken());
			paramToReg = paramToRegStr.split("=");
			ret.put(paramToReg[0], paramToReg[1]);
		}
		
		return ret;
    	
    }
    
    private static String[] getRegsFromLibCall(String str){
	 	return str.split(",");
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
    	regs[0] = token.substring(0, endReg1);
    	regs[1] = token.substring(endReg1+1, endReg2);
    	
    }
    
    private void getFieldRegs(String token, String[] regs){
    	
    	String[] tmp = token.split("\\.");
    	regs[0] = tmp[0];
    	regs[1] = tmp[1];
    	
    }
    private static String formatStr(String str) {
    	
    	if(str.endsWith(","))
    		return str.substring(0, str.length()-1);
    	else
    		return str;
    }
    private static String getParamsFromCall(String str){
    	
    	if((str.indexOf("(") +1) != (str.indexOf(")")) )
    		return str.substring(str.indexOf("(") + 1, str.indexOf(")"));
    	else
    		return "";
    }
    private static String getFuncFromCall(String str){
    	return str.substring(0, str.indexOf("("));
    }
    private void makeEpilogueForFunc(String currMethod) {
    	
        emit("# Epilogue");
        emit(currMethod + "_epilogue:");
        emit("mov (%ebp), %esp");
        emit("pop (%ebp)");
        emit("ret");
    }
    private void makePrologue(String currMethod) {
    	
        emit("# Prologue");
        emit("push (%ebp)");
        emit("mov %esp, (%ebp)");
        if ( ml.getVarStackSize(currMethod) > 0) {
        	emit("sub $" + ml.getVarStackSize(currMethod) + ", %esp");
        }
    	
    }
    private static String removeDot(String str) {
    	int dotIndex = -1;
    	String ret = str;
    	if((dotIndex = str.indexOf(".")) != -1) {
    		ret = str.substring(0,dotIndex-1);
    	}
    	return ret;
    }
    private void updateRegs(String left, String right) {
    	if(regToDVPtr.containsKey(removeDot(left))) {
    		regToDVPtr.put(removeDot(right), regToDVPtr.get(removeDot(left)));
    	} else {
    		regToDVPtr.put(removeDot(right), removeDot(left));
    	}
    }
    private static int getVirtualMethodOffset(String str) {
    	int dotIndex = -1;
    	int braceIndex = -1;
    	String ret = str;
    	if(((dotIndex = str.indexOf(".")) != -1) && ((braceIndex = str.indexOf("(")) != -1)) {
    		ret = str.substring(dotIndex+1, braceIndex);
    	}
    	return Integer.parseInt(ret);
    }
	
}
