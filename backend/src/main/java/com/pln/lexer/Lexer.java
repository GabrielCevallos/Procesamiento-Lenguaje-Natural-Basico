package com.pln.lexer;

import java.util.*;

/**
 * Lexer: Realizador del análisis léxico.
 * Divide la entrada en tokens (palabras) y los clasifica por categoría gramatical.
 */
public class Lexer {
    private final String input;
    private int position;
    private int line;
    private final List<Token> tokens;
    
    // Diccionarios para clasificación (palabras comunes universales)
    private static final Set<String> ARTICULOS = new HashSet<>(Arrays.asList(
        "el", "la", "los", "las", "un", "una", "unos", "unas", "lo"
    ));
    
    private static final Set<String> PREPOSICIONES = new HashSet<>(Arrays.asList(
        "en", "con", "por", "a", "de", "para", "sin", "bajo", "sobre", "entre",
        "hacia", "desde", "hasta", "mediante", "durante", "ante", "tras"
    ));
    
    // Formas comunes de verbos (sin tilde para simplificar)
    private static final Set<String> VERBOS_COMUNES = new HashSet<>(Arrays.asList(
        "corre", "come", "duerme", "juega", "salta", "canta", "baila", "habla",
        "camina", "vive", "muere", "nace", "crece", "aprende", "enseña", "trabaja",
        "descansa", "viaja", "vuelve", "llega", "sale", "entra", "compra", "vende",
        "abre", "cierra", "ve", "oye", "toca", "huele", "sabe", "piensa", "siente",
        "es", "está", "son", "están", "fue", "fueron", "será", "serán", "ha", "han",
        "haya", "hayas", "hayan", "tengo", "tiene", "tienen", "tenemos"
    ));

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.tokens = new ArrayList<>();
    }

    /**
     * Realiza el análisis léxico completo.
     * 
     * @return Lista de tokens identificados y clasificados
     */
    public List<Token> tokenize() {
        while (position < input.length()) {
            skipWhitespace();
            
            if (position >= input.length()) {
                break;
            }

            Token token = nextToken();
            if (token != null) {
                tokens.add(token);
            }
        }

        tokens.add(new Token(TokenType.EOF, "", position, line));
        return tokens;
    }

    /**
     * Obtiene el siguiente token de la entrada.
     * 
     * @return Token encontrado o null si no hay más caracteres
     */
    private Token nextToken() {
        if (position >= input.length()) {
            return null;
        }

        int startPos = position;
        StringBuilder word = new StringBuilder();

        // Leer caracteres hasta encontrar espacio o fin de entrada
        while (position < input.length() && !Character.isWhitespace(input.charAt(position))) {
            word.append(input.charAt(position));
            position++;
        }

        String wordStr = word.toString().toLowerCase();
        TokenType type = classifyToken(wordStr);

        return new Token(type, word.toString(), startPos, line);
    }

    /**
     * Clasifica una palabra en su categoría gramatical.
     * 
     * @param word Palabra a clasificar (en minúsculas)
     * @return Tipo de token correspondiente
     */
    private TokenType classifyToken(String word) {
        if (ARTICULOS.contains(word)) {
            return TokenType.ARTICULO;
        }
        if (PREPOSICIONES.contains(word)) {
            return TokenType.PREPOSICION;
        }
        if (VERBOS_COMUNES.contains(word)) {
            return TokenType.VERBO;
        }
        
        // Heurística: si termina en 'o' o 'a' podría ser sustantivo/adjetivo
        // pero para evitar hardcoding, lo dejamos como PALABRA genérica
        return TokenType.PALABRA;
    }

    /**
     * Salta espacios en blanco y actualiza el número de línea.
     */
    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            if (input.charAt(position) == '\n') {
                line++;
            }
            position++;
        }
    }

    /**
     * Obtiene todos los tokens generados.
     * 
     * @return Lista de tokens
     */
    public List<Token> getTokens() {
        return tokens;
    }
}

