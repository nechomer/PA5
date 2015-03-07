package IC.asm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AsmTranslator {
	
	private String lirFileName; 
	BufferedWriter fw;
	BufferedReader fr;
	
	public AsmTranslator(String lirFileName) {
		this.lirFileName = lirFileName;
	}
	
	public void writeFileProlog() throws IOException {
		int index = lirFileName.lastIndexOf(File.separator);
		String result = ".title " + "\"" + lirFileName.substring(index+1);
		result = result.replaceAll(".lir", ".ic");
		result += "\"\n\n";
		result += "# global declarations\n";
		result += ".global __ic_main\n\n";
		result += "# data section \n.data\n";
		result += "\t.align 4\n\n";
		fw.write(result);
	}
	
	public void writeConstantStrings() throws IOException {
		String line;
		while (!(line = fr.readLine()).equals("# End of constant strings section")) {
			String[] words = line.split(":", 2);
			String result = "\t.int " + (words[1].length() - 2) + "\n";
			result += words[0] + ": .string" + words[1] + "\n";
			fw.write(result);
		}
	}
	
	public void writeDV() throws IOException {
		String line;
		while (!(line = fr.readLine()).equals("# End of dispatch table section")) {
			if (line.startsWith("#")) continue;
			String[] words = line.split("[\\[\\,\\]]+");
			String result = words[0];
			for (int i = 1; i< words.length; i++) {
				result += " .long " + words[i] + "\n\t";
			}
			fw.write(result + "\n");
		}
	}

	public void translateLirToAsm() throws IOException {
		String asmFileName = lirFileName.replaceAll(".lir$", ".s");
		fw = new BufferedWriter(new FileWriter(asmFileName));
		fr = new BufferedReader(new FileReader(lirFileName));
		writeFileProlog();
		writeConstantStrings();
		writeDV();
		fw.write("# text (code) section\n\t.text\n\n");
		fr.close();
		fw.close();
	}
	
}
