package com.pln.lexer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class LexicalLoader {
    private static final Map<String, Set<String>> dictionaries = new HashMap<>();
    private static boolean loaded = false;

    private static final List<String> ARRAY_FILES = Arrays.asList(
        "articulos.json", "pronombres.json", "sustantivos.json",
        "adverbios.json", "preposiciones.json", "conjunciones.json"
    );

    public static void load() {
        if (loaded) return;
        ObjectMapper mapper = new ObjectMapper();
        try {
            for (String file : ARRAY_FILES) {
                String key = file.replace(".json", "");
                dictionaries.put(key, loadJsonArray(mapper, file));
            }
            loaded = true;
        } catch (Exception e) {
            throw new RuntimeException("Error loading lexical resources: " + e.getMessage(), e);
        }
    }

    private static Set<String> loadJsonArray(ObjectMapper mapper, String filename) throws Exception {
        InputStream is = LexicalLoader.class.getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new RuntimeException("Resource not found: " + filename);
        }
        List<String> list = mapper.readValue(is, new TypeReference<List<String>>() {});
        return list.stream()
                .map(w -> normalize(w.toLowerCase()))
                .collect(Collectors.toSet());
    }

    public static String normalize(String word) {
        return word.toLowerCase()
                .replace('á', 'a')
                .replace('é', 'e')
                .replace('í', 'i')
                .replace('ó', 'o')
                .replace('ú', 'u')
                .replace('ü', 'u')
                .replace('ñ', 'n');
    }

    public static boolean isArticulo(String word) { return contains("articulos", word); }
    public static boolean isPronombre(String word) { return contains("pronombres", word); }
    public static boolean isSustantivo(String word) { return contains("sustantivos", word); }
    public static boolean isAdverbio(String word) { return contains("adverbios", word); }
    public static boolean isPreposicion(String word) { return contains("preposiciones", word); }
    public static boolean isConjuncion(String word) { return contains("conjunciones", word); }

    private static boolean contains(String category, String word) {
        load();
        Set<String> dict = dictionaries.get(category);
        return dict != null && dict.contains(normalize(word));
    }
}
