package com.pln.controller;

import com.pln.lexer.Lexer;
import com.pln.lexer.Token;
import com.pln.parser.Parser;
import com.pln.parser.ParseException;
import com.pln.ast.ASTNode;
import com.pln.ast.DerivationStep;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST para el análisis sintáctico.
 * Expone endpoints para procesar oraciones y obtener resultados.
 */
@RestController
@RequestMapping("/api/parse")
@CrossOrigin(origins = "*")
public class ParseController {

    /**
     * Procesa una oración y retorna el análisis completo.
     * 
     * @param request Objeto con la oración a analizar
     * @return Respuesta con tokens, AST, derivaciones, etc.
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyze(@RequestBody ParseRequest request) {
        String sentence = request.getSentence().trim();
        
        if (sentence.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "La oración no puede estar vacía"));
        }

        try {
            // Fase 1: Análisis léxico
            Lexer lexer = new Lexer(sentence);
            List<Token> tokens = lexer.tokenize();

            // Fase 2: Análisis sintáctico
            Parser parser = new Parser(tokens);
            ASTNode ast = parser.parse();

            // Construir respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sentence", sentence);
            response.put("tokens", formatTokensWithParts(tokens, ast));
            response.put("derivationSteps", formatDerivationSteps(parser.getDerivationSteps()));
            response.put("treeASCII", ast.toASCII());
            response.put("treeJSON", astToJSON(ast));

            return ResponseEntity.ok(response);

        } catch (ParseException e) {
            // En caso de error, retornamos los tokens de todas formas
            try {
                Lexer lexer = new Lexer(sentence);
                List<Token> tokens = lexer.tokenize();
                
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "tokens", formatTokens(tokens)
                    ));
            } catch (Exception ex) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Formatea los tokens en una lista de mapas para JSON.
     * Incluye parte como "Desconocido" (para respuesta de error).
     */
    private List<Map<String, Object>> formatTokens(List<Token> tokens) {
        List<Map<String, Object>> result = new ArrayList<>();
        int counter = 1;
        
        for (Token token : tokens) {
            if (token.getLexeme().isEmpty()) continue; // Ignorar EOF
            
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("id", counter++);
            tokenMap.put("type", formatTokenType(token.getType().toString()));
            tokenMap.put("lexeme", token.getLexeme());
            tokenMap.put("part", "Desconocido");
            
            result.add(tokenMap);
        }
        
        return result;
    }

    /**
     * Formatea los tokens con información de a qué parte pertenecen (Sujeto, Verbo, Complemento).
     * Usa una lista ordenada para soportar lexemas repetidos correctamente.
     */
    private List<Map<String, Object>> formatTokensWithParts(List<Token> tokens, ASTNode ast) {
        List<Map<String, Object>> result = new ArrayList<>();
        int counter = 1;
        
        // Extraer las partes en orden desde el AST
        List<String> partsInOrder = new ArrayList<>();
        collectPartsInOrder(ast, partsInOrder, null);
        
        int partIndex = 0;
        
        for (Token token : tokens) {
            if (token.getLexeme().isEmpty()) continue; // Ignorar EOF
            
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("id", counter++);
            tokenMap.put("type", formatTokenType(token.getType().toString()));
            tokenMap.put("lexeme", token.getLexeme());
            
            // Asignar parte según el orden del AST
            String part = partIndex < partsInOrder.size() ? partsInOrder.get(partIndex) : "Desconocido";
            tokenMap.put("part", part);
            partIndex++;
            
            result.add(tokenMap);
        }
        
        return result;
    }

    /**
     * Recolecta las partes gramaticales en orden desde las hojas del AST.
     */
    private void collectPartsInOrder(ASTNode node, List<String> parts, String currentPart) {
        if (node.isTerminal()) {
            parts.add(currentPart != null ? currentPart : "Desconocido");
            return;
        }
        
        String nextPart = currentPart;
        if (node.getSymbol().equals("Sujeto") || 
            node.getSymbol().equals("Verbo") || 
            node.getSymbol().equals("Complemento")) {
            nextPart = node.getSymbol();
        }
        
        for (ASTNode child : node.getChildren()) {
            collectPartsInOrder(child, parts, nextPart);
        }
    }

    /**
     * Formatea el nombre del tipo de token para mostrar en español.
     */
    private String formatTokenType(String type) {
        return switch (type) {
            case "ARTICULO" -> "Artículo";
            case "PREPOSICION" -> "Preposición";
            case "SUSTANTIVO" -> "Sustantivo";
            case "VERBO" -> "Verbo";
            case "ADJETIVO" -> "Adjetivo";
            case "PALABRA" -> "Palabra";
            default -> type;
        };
    }

    /**
     * Formatea los pasos de derivación.
     */
    private List<String> formatDerivationSteps(List<DerivationStep> steps) {
        List<String> result = new ArrayList<>();
        
        for (DerivationStep step : steps) {
            result.add(step.getSententialForm());
        }
        
        return result;
    }

    /**
     * Convierte el AST a JSON para visualización en el frontend.
     */
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

    /**
     * Clase interna para el request.
     */
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
