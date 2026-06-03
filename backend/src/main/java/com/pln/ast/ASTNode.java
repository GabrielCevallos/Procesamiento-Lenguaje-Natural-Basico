package com.pln.ast;

import java.util.*;

/**
 * Nodo del Árbol Sintáctico Abstracto (AST).
 * Representa un símbolo (terminal o no terminal) en la derivación.
 */
public class ASTNode {
    private final String symbol;
    private final boolean isTerminal;
    private final List<ASTNode> children;
    private final int derivationOrder;

    public ASTNode(String symbol, boolean isTerminal, int derivationOrder) {
        this.symbol = symbol;
        this.isTerminal = isTerminal;
        this.derivationOrder = derivationOrder;
        this.children = new ArrayList<>();
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    public int getDerivationOrder() {
        return derivationOrder;
    }

    /**
     * Convierte el árbol a representación en texto plano (ASCII).
     * 
     * @return String con el árbol formateado
     */
    public String toASCII() {
        StringBuilder sb = new StringBuilder();
        toASCII(this, "", true, sb);
        return sb.toString();
    }

    private void toASCII(ASTNode node, String prefix, boolean isLast, StringBuilder sb) {
        sb.append(prefix);
        sb.append(isLast ? "└── " : "├── ");
        sb.append(node.symbol);
        if (node.isTerminal) {
            sb.append(" (terminal)");
        }
        sb.append("\n");

        List<ASTNode> children = node.children;
        for (int i = 0; i < children.size(); i++) {
            boolean childIsLast = i == children.size() - 1;
            String childPrefix = prefix + (isLast ? "    " : "│   ");
            toASCII(children.get(i), childPrefix, childIsLast, sb);
        }
    }

    @Override
    public String toString() {
        return symbol;
    }
}
