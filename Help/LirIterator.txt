package IC.asm;

import java.util.Iterator;

public class LirIterator implements Iterator<String> {

	private String lirString;
	private int indexString;
	private int lengthString;
	
	public LirIterator(String lirString) {
		this.lirString = lirString;
		this.indexString = 0;
		this.lengthString = lirString.length();
	}

	@Override
	public boolean hasNext() {
		if(this.indexString < this.lengthString)
			return true;
		
		return false;
	}

	@Override
	public String next() {
		int CurrIndex = this.lirString.indexOf("\n",this.indexString);
		String ret = this.lirString.substring(this.indexString, CurrIndex-1);
		this.indexString = CurrIndex+1;
		return ret;
	}
	
	
	

}
