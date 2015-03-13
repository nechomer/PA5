/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.lir;

/**
 *
 * @author Nimrod Rappoport
 */

/*
 * An enum for the different types of lir jump instruction
 */
public enum JumpTypes {
    TRUE("True"), FALSE("False"), G("G"), GE("GE"), L("L"), LE("LE");

    private String name;
    private JumpTypes(String name) {this.name = name;}

    public String getName() {return this.name;}

}
