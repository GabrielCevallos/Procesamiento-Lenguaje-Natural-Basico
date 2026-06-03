package com.pln.lexer;

/**
 * Representa un token identificado durante el análisis léxico.
 * Cada token contiene su tipo, lexema (valor) y posición en la entrada.
 */
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final int position;
    private final int line;

    public Token(TokenType type, String lexeme, int position, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.position = position;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s', pos=%d, line=%d)", type, lexeme, position, line);
    }
}
