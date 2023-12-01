package Compilador.misc;

public class ErrorMessage {
    public int line;
    public String message;

    public ErrorMessage(int line, String message) {
        this.line = line;
        this.message = message;
    }
}
