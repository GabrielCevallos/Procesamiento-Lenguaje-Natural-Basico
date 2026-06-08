package com.pln.lexer;

import java.util.*;

public class Lexer {
    private final String input;
    private int position;
    private final List<Token> tokens;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.tokens = new ArrayList<>();
        LexicalLoader.load();
    }

    public List<Token> tokenize() {
        while (position < input.length()) {
            skipWhitespace();
            if (position >= input.length()) break;

            Token token = nextToken();
            if (token != null) {
                tokens.add(token);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private Token nextToken() {
        if (position >= input.length()) return null;

        StringBuilder word = new StringBuilder();

        while (position < input.length() && !Character.isWhitespace(input.charAt(position))) {
            word.append(input.charAt(position));
            position++;
        }

        String wordStr = word.toString();
        TokenType type = classifyToken(wordStr);
        return new Token(type, wordStr);
    }

    private TokenType classifyToken(String word) {
        String normalized = LexicalLoader.normalize(word);

        if (LexicalLoader.isArticulo(normalized)) return TokenType.ARTICULO;
        if (LexicalLoader.isPronombre(normalized)) return TokenType.PRONOMBRE;
        if (LexicalLoader.isPreposicion(normalized)) return TokenType.PREPOSICION;
        if (LexicalLoader.isConjuncion(normalized)) return TokenType.CONJUNCION;
        if (LexicalLoader.isAdverbio(normalized)) return TokenType.ADVERBIO;
        if (LexicalLoader.isSustantivo(normalized)) return TokenType.SUSTANTIVO;

        if (VerbConjugator.getAllVerbForms().contains(normalized)
                || VerbConjugator.getAllVerbForms().contains(word.toLowerCase())) {
            return TokenType.VERBO;
        }

        if (AdjectiveGenerator.getAllAdjectives().contains(normalized)
                || AdjectiveGenerator.getAllAdjectives().contains(word.toLowerCase())) {
            return TokenType.ADJETIVO;
        }

        return TokenType.SUSTANTIVO;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

}
