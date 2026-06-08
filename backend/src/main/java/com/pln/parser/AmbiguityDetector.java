package com.pln.parser;

import com.pln.lexer.*;
import java.util.*;

public class AmbiguityDetector {
    private final List<Token> tokens;
    private final List<String> ambiguities;

    public AmbiguityDetector(List<Token> tokens) {
        this.tokens = tokens;
        this.ambiguities = new ArrayList<>();
    }

    public List<String> detect() {
        detectPPAttachmentAmbiguity();
        return ambiguities;
    }

    public boolean hasAmbiguity() {
        return !ambiguities.isEmpty();
    }

    public List<String> getAmbiguities() {
        return ambiguities;
    }

    private void detectPPAttachmentAmbiguity() {
        List<Token> filtered = new ArrayList<>();
        for (Token t : tokens) {
            if (t.getType() != TokenType.EOF && t.getType() != TokenType.WHITESPACE) {
                filtered.add(t);
            }
        }

        for (int i = 0; i < filtered.size(); i++) {
            if (filtered.get(i).getType() == TokenType.VERBO) {
                for (int j = i + 1; j < filtered.size(); j++) {
                    if (filtered.get(j).getType() == TokenType.PREPOSICION) {
                        int nounCount = 0;
                        for (int k = i + 1; k < j; k++) {
                            if (filtered.get(k).getType() == TokenType.SUSTANTIVO
                                    || filtered.get(k).getType() == TokenType.PRONOMBRE) {
                                nounCount++;
                            }
                        }
                        if (nounCount >= 1) {
                            String prep = filtered.get(j).getLexeme();
                            StringBuilder sp = new StringBuilder();
                            for (int k = j + 1; k < filtered.size() && k < j + 4; k++) {
                                sp.append(filtered.get(k).getLexeme()).append(" ");
                            }
                            String verbLex = filtered.get(i).getLexeme();
                            String nounLex = "";
                            for (int k = i + 1; k < j; k++) {
                                if (filtered.get(k).getType() == TokenType.SUSTANTIVO
                                        || filtered.get(k).getType() == TokenType.PRONOMBRE) {
                                    nounLex = filtered.get(k).getLexeme();
                                    break;
                                }
                            }
                            ambiguities.add(
                                "Posible ambig\u00fcedad sint\u00e1ctica detectada: El sintagma preposicional \"" +
                                prep + " " + sp.toString().trim() + "\" podr\u00eda modificar al verbo \"" +
                                verbLex + "\" (circunstancial) o al sintagma nominal \"" +
                                nounLex + "\" (adyacente). Este fen\u00f3meno se conoce como ambig\u00fcedad de adjunci\u00f3n del sintagma preposicional (PP attachment ambiguity)."
                            );
                            break;
                        }
                    }
                }
            }
        }
    }


}
