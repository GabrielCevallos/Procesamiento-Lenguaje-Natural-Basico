package com.pln.parser;

/**
 * Excepción levantada cuando el análisis sintáctico falla.
 * Indica que la entrada no cumple con la gramática definida.
 */
public class ParseException extends Exception {
    private final int lineNumber;
    private final int position;

    public ParseException(String message, int lineNumber, int position) {
        super(message);
        this.lineNumber = lineNumber;
        this.position = position;
    }

    public ParseException(String message) {
        super(message);
        this.lineNumber = -1;
        this.position = -1;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getPosition() {
        return position;
    }
}
