package com.pln.controller;

import com.pln.lexer.Lexer;
import com.pln.lexer.Token;
import com.pln.parser.Parser;
import com.pln.parser.AmbiguityDetector;
import com.pln.ast.ASTNode;
import com.pln.ast.DerivationStep;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/parse")
@CrossOrigin(origins = "*")
public class ParseController {

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyze(@RequestBody ParseRequest request) {
        String sentence = request.getSentence().trim();

        if (sentence.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "La oraci\u00f3n no puede estar vac\u00eda"));
        }

        try {
            Lexer lexer = new Lexer(sentence);
            List<Token> tokens = lexer.tokenize();

            Parser parser = new Parser(tokens);
            ASTNode ast = parser.parse();

            boolean success = !parser.hasError();

            AmbiguityDetector ambiguityDetector = new AmbiguityDetector(tokens);
            List<String> ambiguities = ambiguityDetector.detect();

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("sentence", sentence);
            response.put("tokens", formatTokensWithParts(tokens, ast));
            response.put("subject", extractPart(tokens, ast, "SUJETO"));
            response.put("verb", extractPart(tokens, ast, "VERBO"));
            response.put("complement", extractPart(tokens, ast, "COMPLEMENTO"));
            response.put("derivationSteps", formatDerivationSteps(parser.getDerivationSteps()));
            response.put("treeJSON", astToJSON(ast));
            response.put("hasAmbiguity", !ambiguities.isEmpty());
            response.put("ambiguities", ambiguities);

            if (!success) {
                response.put("error", parser.getParseError());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage(), "success", false));
        }
    }

    private List<Map<String, Object>> formatTokensWithParts(List<Token> tokens, ASTNode ast) {
        List<Map<String, Object>> result = new ArrayList<>();
        int counter = 1;

        List<String> partsInOrder = new ArrayList<>();
        collectPartsInOrder(ast, partsInOrder, null);

        int partIndex = 0;

        for (Token token : tokens) {
            if (token.getLexeme().isEmpty()) continue;

            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("id", counter++);
            tokenMap.put("type", formatTokenType(token.getType().toString()));
            tokenMap.put("lexeme", token.getLexeme());

            String part = partIndex < partsInOrder.size() ? partsInOrder.get(partIndex) : "Desconocido";
            tokenMap.put("part", part);
            partIndex++;

            result.add(tokenMap);
        }

        return result;
    }

    private void collectPartsInOrder(ASTNode node, List<String> parts, String currentPart) {
        if (node.isTerminal()) {
            parts.add(currentPart != null ? currentPart : "Desconocido");
            return;
        }

        String nextPart = currentPart;
        if ("SUJETO".equals(node.getSymbol()) || "VERBO".equals(node.getSymbol()) || "COMPLEMENTO".equals(node.getSymbol())) {
            nextPart = node.getSymbol();
        }

        for (ASTNode child : node.getChildren()) {
            collectPartsInOrder(child, parts, nextPart);
        }
    }

    private List<String> extractPart(List<Token> tokens, ASTNode ast, String targetPart) {
        List<String> lexemes = new ArrayList<>();
        List<String> partsInOrder = new ArrayList<>();
        collectPartsInOrder(ast, partsInOrder, null);

        int partIndex = 0;
        for (Token token : tokens) {
            if (token.getLexeme().isEmpty()) continue;
            if (partIndex < partsInOrder.size() && targetPart.equals(partsInOrder.get(partIndex))) {
                lexemes.add(token.getLexeme());
            }
            partIndex++;
        }
        return lexemes;
    }

    private String formatTokenType(String type) {
        return switch (type) {
            case "ARTICULO" -> "Art\u00edculo";
            case "PRONOMBRE" -> "Pronombre";
            case "SUSTANTIVO" -> "Sustantivo";
            case "VERBO" -> "Verbo";
            case "ADJETIVO" -> "Adjetivo";
            case "ADVERBIO" -> "Adverbio";
            case "PREPOSICION" -> "Preposici\u00f3n";
            case "CONJUNCION" -> "Conjunci\u00f3n";
            default -> type;
        };
    }

    private List<String> formatDerivationSteps(List<DerivationStep> steps) {
        List<String> result = new ArrayList<>();
        for (DerivationStep step : steps) {
            result.add(step.getRule());
        }
        return result;
    }

    private Map<String, Object> astToJSON(ASTNode node) {
        Map<String, Object> json = new HashMap<>();
        json.put("symbol", node.getSymbol());
        json.put("terminal", node.isTerminal());

        List<Map<String, Object>> children = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            children.add(astToJSON(child));
        }
        json.put("children", children);

        return json;
    }

    public static class ParseRequest {
        private String sentence;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }
    }
}
