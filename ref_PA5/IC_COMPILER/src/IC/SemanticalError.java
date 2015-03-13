package IC;

/**
 *
 * @author Barak Itkin
 */
public class SemanticalError extends Error {

    public int line;

    public SemanticalError(String message, int line) {
        super(message);
        this.line = line;
    }

}
