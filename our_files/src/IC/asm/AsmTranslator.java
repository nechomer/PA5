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
	
	public void translateLirToAsm() throws IOException {
		
		String asmFileName = lirFileName.replaceAll(".lir$", ".s");
		BufferedReader bufReader = new BufferedReader(new StringReader(lirStr));
		String line = null;
		
		BufferedWriter fw = new BufferedWriter(new FileWriter(asmFileName));
		makeFileProlog();
		makeConstantStrings();
		makeDV();
		
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
					
				}
				else if(lirOp.equals("MoveArray")){
					
				}
				else if(lirOp.equals("MoveField")){
					
				}
				else if(lirOp.equals("ArrayLength")){
					
				}
				else if(lirOp.equals("Add")){
					
				}
				else if(lirOp.equals("Sub")){
					
				}
				else if(lirOp.equals("Mul")){
					
				}
				else if(lirOp.equals("Div")){
					
				}
				else if(lirOp.equals("Mod")){
					
				}
				else if(lirOp.equals("Inc")){
					
				}
				else if(lirOp.equals("Dic")){
					
				}
				else if(lirOp.equals("Neg")){
					
				}
				else if(lirOp.equals("Not")){
					
				}
				else if(lirOp.equals("And")){
					
				}
				else if(lirOp.equals("Or")){
					
				}
				else if(lirOp.equals("Xor")){
					
				}
				else if(lirOp.equals("Compare")){
					
				}
				else if(lirOp.equals("Jump")){
					
				}
				else if(lirOp.equals("JumpTure")){
					
				}
				else if(lirOp.equals("JumpFalse")){
					
				}
				else if(lirOp.equals("JumpG")){
					
				}
				else if(lirOp.equals("JumpGE")){
					
				}
				else if(lirOp.equals("JumpL")){
					
				}
				else if(lirOp.equals("JumpLE")){
					
				}
				else if(lirOp.equals("Library")){
					
				}
				else if(lirOp.equals("StaticCall")){
					
				}
				else if(lirOp.equals("VirtualCall")){
					
				}
				else if(lirOp.equals("Return")){
					
				}
			}
		} catch (IOException e) {

		}

		fw.write("# text (code) section\n\t.text\n\n");
		fw.close();
	}
	
}
