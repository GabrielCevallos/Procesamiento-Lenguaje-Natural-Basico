package com.pln.lexer;

import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdjectiveGenerator {
    private static final Set<String> ALL_ADJECTIVES = new HashSet<>();
    private static Map<String, Map<String, Object>> RULES;

    public static Set<String> getAllAdjectives() {
        return ALL_ADJECTIVES;
    }

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(
                AdjectiveGenerator.class.getResourceAsStream("/adjective-rules.json"),
                new TypeReference<Map<String, Object>>() {}
            );

            RULES = (Map<String, Map<String, Object>>) data.get("rules");

            List<Map<String, String>> adjectives = mapper.readValue(
                AdjectiveGenerator.class.getResourceAsStream("/adjectives.json"),
                new TypeReference<List<Map<String, String>>>() {}
            );

            for (Map<String, String> entry : adjectives) {
                String adj = entry.get("adjective");
                String type = entry.get("type");
                generate(adj, type);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load adjective data", e);
        }
    }

    private static void generate(String adj, String type) {
        Map<String, Object> rule = RULES.get(type);
        if (rule == null) return;

        Integer removeLast = (Integer) rule.get("removeLast");
        String stem = (removeLast != null) ? adj.substring(0, adj.length() - removeLast) : adj;

        String femSuffix = (String) rule.get("feminineSuffix");
        String mascPluralSuffix = (String) rule.get("masculinePluralSuffix");
        String femPluralSuffix = (String) rule.get("femininePluralSuffix");
        String pluralSuffix = (String) rule.get("pluralSuffix");

        if (femSuffix != null) {
            String fem = stem + femSuffix;
            if (mascPluralSuffix != null) {
                addForms(adj, fem, adj + mascPluralSuffix, fem + (femPluralSuffix != null ? femPluralSuffix : mascPluralSuffix));
            } else {
                addForms(adj, fem);
            }
        } else if (pluralSuffix != null) {
            if (pluralSuffix.isEmpty()) {
                addForms(adj);
            } else if ("ces".equals(pluralSuffix)) {
                addForms(adj, stem + pluralSuffix);
            } else {
                addForms(adj, adj + pluralSuffix);
            }
        }
    }

    private static void addForms(String... forms) {
        for (String f : forms) {
            if (f != null && !f.isEmpty()) {
                ALL_ADJECTIVES.add(f);
            }
        }
    }
}
