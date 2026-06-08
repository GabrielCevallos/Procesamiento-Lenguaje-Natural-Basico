package com.pln.parser;

import com.pln.lexer.Token;
import com.pln.lexer.TokenType;
import com.pln.ast.*;
import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int current;
    private final List<DerivationStep> derivationSteps;
    private int stepCounter;
    private ASTNode root;
    private String parseError;
    private final List<String> ruleStack;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.derivationSteps = new ArrayList<>();
        this.stepCounter = 0;
        this.parseError = null;
        this.ruleStack = new ArrayList<>();
    }

    public ASTNode parse() {
        ruleStack.add("ORACION");
        addStep("ORACION", "Simbolo inicial");
        root = parseOracion();
        if (!isAtEnd() && !hasError()) {
            parseError = "Error: elementos inesperados despues del complemento: '" + peek().getLexeme() + "'";
        }
        return root;
    }

    public boolean hasError() { return parseError != null; }
    public String getParseError() { return parseError; }
    public List<DerivationStep> getDerivationSteps() { return derivationSteps; }
    public List<String> getSententialForms() { return ruleStack; }
    private ASTNode parseOracion() {
        ASTNode node = new ASTNode("ORACION", false);

        replaceLast("ORACION", "SUJETO VERBO COMPLEMENTO");
        addStep("SUJETO VERBO COMPLEMENTO", "ORACION -> SUJETO VERBO COMPLEMENTO");

        ASTNode sujeto = parseSujeto();
        if (sujeto.getChildren().isEmpty()) {
            parseError = "Error: No se encontro un sujeto valido. Se esperaba un sintagma nominal (articulo + sustantivo) o un pronombre.";
            node.addChild(sujeto);
            return node;
        }
        node.addChild(sujeto);

        ASTNode verbo = parseVerbo();
        if (verbo.getChildren().isEmpty()) {
            parseError = "Error: No se encontro un verbo. Toda oracion debe contener al menos un verbo conjugado.";
            node.addChild(verbo);
            return node;
        }
        node.addChild(verbo);

        ASTNode complemento = parseComplemento();
        if (!complemento.getChildren().isEmpty()) {
            node.addChild(complemento);
        }

        return node;
    }

    private ASTNode parseSujeto() {
        ASTNode node = new ASTNode("SUJETO", false);

        int saved = current;
        ASTNode sn = parseSN();
        if (sn != null && !sn.getChildren().isEmpty()) {
            node.addChild(sn);
            replaceLast("SUJETO", "SN");
            addStep(buildCurrentForm(), "SUJETO -> SN");
            return node;
        }
        current = saved;

        if (match(TokenType.PRONOMBRE)) {
            ASTNode pronVal = new ASTNode(previous().getLexeme(), true);
            ASTNode pronNode = new ASTNode("PRONOMBRE", false);
            pronNode.addChild(pronVal);
            node.addChild(pronNode);
            replaceLast("SUJETO", previous().getLexeme());
            addStep(buildCurrentForm(), "SUJETO -> PRONOMBRE");
            return node;
        }

        return node;
    }

    private ASTNode parseSN() {
        int saved = current;
        ASTNode node = new ASTNode("SN", false);

        if (match(TokenType.ARTICULO)) {
            ASTNode artVal = new ASTNode(previous().getLexeme(), true);
            ASTNode artNode = new ASTNode("ARTICULO", false);
            artNode.addChild(artVal);
            node.addChild(artNode);
        }

        boolean hasCore = false;

        if (match(TokenType.SUSTANTIVO)) {
            ASTNode sustVal = new ASTNode(previous().getLexeme(), true);
            ASTNode sustNode = new ASTNode("SUSTANTIVO", false);
            sustNode.addChild(sustVal);
            node.addChild(sustNode);
            hasCore = true;
            if (match(TokenType.SUSTANTIVO)) {
                ASTNode sustVal2 = new ASTNode(previous().getLexeme(), true);
                ASTNode sustNode2 = new ASTNode("SUSTANTIVO", false);
                sustNode2.addChild(sustVal2);
                node.addChild(sustNode2);
            }
        } else if (node.getChildren().isEmpty() && match(TokenType.PRONOMBRE)) {
            ASTNode pronVal = new ASTNode(previous().getLexeme(), true);
            ASTNode pronNode = new ASTNode("PRONOMBRE", false);
            pronNode.addChild(pronVal);
            node.addChild(pronNode);
            hasCore = true;
        }

        if (!hasCore && node.getChildren().isEmpty()) {
            current = saved;
            return null;
        } else if (!hasCore) {
            current = saved;
            return null;
        }

        if (match(TokenType.ADJETIVO)) {
            ASTNode adjVal = new ASTNode(previous().getLexeme(), true);
            ASTNode adjNode = new ASTNode("ADJETIVO", false);
            adjNode.addChild(adjVal);
            node.addChild(adjNode);
        }

        replaceLast("SN", buildSNForm(node));
        addStep(buildCurrentForm(), "SN -> ARTICULO? SUSTANTIVO ADJETIVO?");

        return node;
    }

    private ASTNode parseVerbo() {
        ASTNode node = new ASTNode("VERBO", false);
        if (match(TokenType.VERBO)) {
            ASTNode verbVal = new ASTNode(previous().getLexeme(), true);
            node.addChild(verbVal);
            replaceLast("VERBO", previous().getLexeme());
            addStep(buildCurrentForm(), "VERBO -> " + previous().getLexeme());
            while (match(TokenType.VERBO)) {
                ASTNode v = new ASTNode(previous().getLexeme(), true);
                node.addChild(v);
                addStep(buildCurrentForm(), "VERBO -> " + previous().getLexeme());
            }
        }
        return node;
    }

    private ASTNode parseComplemento() {
        ASTNode node = new ASTNode("COMPLEMENTO", false);
        if (isAtEnd()) return node;

        boolean hasComplement = false;

        while (!isAtEnd()) {
            int saved = current;
            if (match(TokenType.PREPOSICION)) {
                ASTNode prepVal = new ASTNode(previous().getLexeme(), true);
                ASTNode prepNode = new ASTNode("PREPOSICION", false);
                prepNode.addChild(prepVal);

                ASTNode sn = parseSN();
                if (sn != null && !sn.getChildren().isEmpty()) {
                    node.addChild(prepNode);
                    node.addChild(sn);
                    hasComplement = true;
                    continue;
                }
                current = saved;
                break;
            }

            if (!hasComplement) {
                ASTNode sn = parseSN();
                if (sn != null && !sn.getChildren().isEmpty()) {
                    node.addChild(sn);
                    hasComplement = true;
                    continue;
                }
            }

            if (!hasComplement && match(TokenType.ADVERBIO)) {
                ASTNode advVal = new ASTNode(previous().getLexeme(), true);
                ASTNode advNode = new ASTNode("ADVERBIO", false);
                advNode.addChild(advVal);
                node.addChild(advNode);
                hasComplement = true;
                continue;
            }

            if (!hasComplement && match(TokenType.ADJETIVO)) {
                ASTNode adjVal = new ASTNode(previous().getLexeme(), true);
                ASTNode adjNode = new ASTNode("ADJETIVO", false);
                adjNode.addChild(adjVal);
                node.addChild(adjNode);
                hasComplement = true;
                continue;
            }

            break;
        }

        if (hasComplement) {
            replaceLast("COMPLEMENTO", buildCompForm(node));
            addStep(buildCurrentForm(), "COMPLEMENTO -> COMPLEMENTO");
        }

        return node;
    }

    private String buildSNForm(ASTNode sn) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode child : sn.getChildren()) {
            for (ASTNode leaf : child.getChildren()) {
                sb.append(leaf.getSymbol()).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String buildCompForm(ASTNode comp) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode child : comp.getChildren()) {
            if (child.isTerminal()) {
                sb.append(child.getSymbol()).append(" ");
            } else {
                for (ASTNode leaf : child.getChildren()) {
                    sb.append(leaf.getSymbol()).append(" ");
                }
            }
        }
        return sb.toString().trim();
    }

    private String buildCurrentForm() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruleStack.size(); i++) {
            sb.append(ruleStack.get(i));
            if (i < ruleStack.size() - 1) sb.append(" ");
        }
        String form = sb.toString().trim();
        return form.isEmpty() ? "ORACION" : form;
    }

    private void replaceLast(String from, String to) {
        for (int i = ruleStack.size() - 1; i >= 0; i--) {
            if (ruleStack.get(i).equals(from)) {
                ruleStack.set(i, to);
                return;
            }
        }
    }

    private boolean match(TokenType type) {
        if (check(type)) { advance(); return true; }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        if (current >= tokens.size()) return true;
        return tokens.get(current).getType() == TokenType.EOF;
    }

    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private void addStep(String form, String rule) {
        stepCounter++;
        derivationSteps.add(new DerivationStep(stepCounter, form, rule));
    }
}
