/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.asm;


public enum HandelLibCall {
	
    ALLOCATE_OBJECT("__allocateObject", 1),
    ALLOCATE_ARRAY("__allocateArray", 1),
    STRCAT("__stringCat", 2),
    PRINTLN("__println", 1),
    PRINT("__print", 1),
    PRINTI("__printi", 1),
    PRINTB("__printb", 1),
    READI("__readi", 0),
    READLN("__readln", 0),
    EOF("__eof", 0),
    STOI("__stoi", 2),
    ITOS("__itos", 1),
    STOA("__stoa", 1),
    ATOS("__atos", 1),
    RANDOM("__random", 1),
    TIME("__time", 0),
    EXIT("__exit", 1);
    
    private String funcCall;
    private int numArguments;
    
    private HandelLibCall(String funcCall, int numArguments) {
        this.funcCall = funcCall;
        this.numArguments = numArguments;
    }

    public String getFuncCall() {
        return funcCall;
    }

    public int getNumArguments() {
        return numArguments;
    }

}
