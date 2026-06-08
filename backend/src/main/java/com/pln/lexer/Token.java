package com.pln.lexer;

/**
 * Representa un token identificado durante el análisis léxico.
 * Cada token contiene su tipo, lexema (valor) y posición en la entrada.
 */
public class Token {
    private final TokenType type;
    private final String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s')", type, lexeme);
    }
}
