/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IC.lir;

import IC.asm.LIRVisitor;

/**
 *
 * @author Nimrod Rappoport
 */

/*
 * A class for a lir comment
 */
public class LirComment implements LirInstruction{
    private String comment;

    public LirComment(String comment) {
        this.comment = comment;
    }

    /**
     * Create a string representing the lir/assembly comment
     * @return the above string
     */
    @Override
    public String toString() {
        return "#  " + this.comment;
    }

    public Object accept(LIRVisitor visitor) {
		return visitor.visit(this);
	}

    public String getComment() {
        return comment;
    }



}
