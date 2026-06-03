package com.pln.parser;

import com.pln.lexer.Token;
import com.pln.lexer.TokenType;
import com.pln.ast.*;

import java.util.*;

/**
 * Parser: Analizador sintáctico.
 * Valida que la entrada cumpla con la gramática:
 *
 * S        → Oración
 * Oración  → Sujeto Verbo Complemento
 * Sujeto   → Determinante? Palabra+
 * Verbo    → Palabra+
 * Complemento → Palabra+ | Determinante Palabra+ | Preposición Determinante? Palabra+
 *
 * Realiza derivaciones por la izquierda y construye el AST.
 */
public class Parser {
    private List<Token> tokens;
    private int current;
    private List<DerivationStep> derivationSteps;
    private int stepCounter;
    private ASTNode root;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.derivationSteps = new ArrayList<>();
        this.stepCounter = 0;
    }

    /**
     * Realiza el análisis sintáctico de los tokens.
     * 
     * @return Raíz del AST si es válido
     * @throws ParseException si la estructura no es válida
     */
    public ASTNode parse() throws ParseException {
        addDerivationStep("S");
        root = parseOracion();
        
        if (!isAtEnd()) {
            throw new ParseException("La oración tiene estructura inválida");
        }
        
        return root;
    }

    /**
     * Analiza una oración: Sujeto Verbo Complemento
     */
    private ASTNode parseOracion() throws ParseException {
        addDerivationStep("Oración");
        
        ASTNode node = new ASTNode("Oración", false, stepCounter);
        
        ASTNode sujeto = parseSujeto();
        node.addChild(sujeto);
        addDerivationStep("Sujeto Verbo Complemento");
        
        ASTNode verbo = parseVerbo();
        node.addChild(verbo);
        addDerivationStep("Verbo Complemento");
        
        ASTNode complemento = parseComplemento();
        node.addChild(complemento);
        addDerivationStep("Complemento");
        
        return node;
    }

    /**
     * Analiza el sujeto: Determinante? Palabra+
     * Consume tokens hasta encontrar un VERBO o PREPOSICION.
     */
    private ASTNode parseSujeto() throws ParseException {
        ASTNode node = new ASTNode("Sujeto", false, stepCounter);
        
        while (current < tokens.size() && tokens.get(current).getType() != TokenType.EOF) {
            TokenType type = tokens.get(current).getType();
            
            // El verbo pertenece a la siguiente parte
            if (type == TokenType.VERBO) {
                break;
            }
            // Una preposición inicia el complemento
            if (type == TokenType.PREPOSICION) {
                break;
            }
            
            Token word = tokens.get(current);
            ASTNode wordNode = new ASTNode(word.getLexeme(), true, stepCounter);
            node.addChild(wordNode);
            current++;
            
            // Dejar al menos 2 tokens para verbo + complemento
            if (remainingTokens() < 2) {
                break;
            }
        }
        
        if (node.getChildren().isEmpty()) {
            throw new ParseException("Falta el sujeto de la oración");
        }
        
        return node;
    }

    /**
     * Analiza el verbo: Palabra+ (mínimo una palabra)
     * Consume tokens VERBO, o una PALABRA como fallback.
     */
    private ASTNode parseVerbo() throws ParseException {
        ASTNode node = new ASTNode("Verbo", false, stepCounter);
        
        if (current >= tokens.size() || tokens.get(current).getType() == TokenType.EOF) {
            throw new ParseException("Falta un verbo en la oración");
        }
        
        // Consumir uno o más tokens VERBO consecutivos
        if (tokens.get(current).getType() == TokenType.VERBO) {
            while (current < tokens.size() && tokens.get(current).getType() == TokenType.VERBO) {
                Token word = tokens.get(current);
                ASTNode wordNode = new ASTNode(word.getLexeme(), true, stepCounter);
                node.addChild(wordNode);
                current++;
            }
        } else {
            // Fallback: tomar al menos una PALABRA como verbo desconocido
            Token word = tokens.get(current);
            ASTNode wordNode = new ASTNode(word.getLexeme(), true, stepCounter);
            node.addChild(wordNode);
            current++;
        }
        
        return node;
    }

    /**
     * Analiza el complemento: Palabra+ | Determinante Palabra+ | Preposición Determinante? Palabra+
     * Consume todos los tokens restantes.
     */
    private ASTNode parseComplemento() throws ParseException {
        ASTNode node = new ASTNode("Complemento", false, stepCounter);
        
        if (current >= tokens.size() || tokens.get(current).getType() == TokenType.EOF) {
            throw new ParseException("Falta el complemento de la oración");
        }
        
        while (current < tokens.size() && tokens.get(current).getType() != TokenType.EOF) {
            Token word = tokens.get(current);
            ASTNode wordNode = new ASTNode(word.getLexeme(), true, stepCounter);
            node.addChild(wordNode);
            current++;
        }
        
        return node;
    }

    /**
     * Cuenta cuántos tokens quedan sin procesar (sin incluir EOF).
     */
    private int remainingTokens() {
        int count = 0;
        int temp = current;
        while (temp < tokens.size() && tokens.get(temp).getType() != TokenType.EOF) {
            count++;
            temp++;
        }
        return count;
    }

    /**
     * Verifica si estamos al final de los tokens.
     */
    private boolean isAtEnd() {
        return current >= tokens.size() || 
               (current == tokens.size() - 1 && tokens.get(current).getType() == TokenType.EOF);
    }

    /**
     * Añade un paso de derivación por la izquierda.
     */
    private void addDerivationStep(String rule) {
        stepCounter++;
        derivationSteps.add(new DerivationStep(stepCounter, rule, ""));
    }

    /**
     * Obtiene los pasos de derivación realizados.
     */
    public List<DerivationStep> getDerivationSteps() {
        return derivationSteps;
    }

    /**
     * Obtiene el AST generado.
     */
    public ASTNode getAST() {
        return root;
    }
}
