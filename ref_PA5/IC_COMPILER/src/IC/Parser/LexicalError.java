package IC.Parser;

public class LexicalError extends Exception {

    private int line;
    private String message;

    public LexicalError(String message) {
        this.message = message;
    }

    public LexicalError(int line, String message) {
        this.line = line;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public int getLine() {
        return this.line;
    }
}
