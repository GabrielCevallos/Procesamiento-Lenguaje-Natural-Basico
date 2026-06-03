package com.pln.lexer;

/**
 * Enumeración de tipos de tokens para el análisis léxico.
 * Clasifica las palabras según su categoría gramatical.
 */
public enum TokenType {
    ARTICULO,     // Artículos: el, la, un, una, los, las, unos, unas
    PREPOSICION,  // Preposiciones: en, con, por, a, de, para, etc.
    SUSTANTIVO,   // Sustantivos (palabras genéricas)
    VERBO,        // Verbos (palabras genéricas)
    ADJETIVO,     // Adjetivos (palabras genéricas)
    PALABRA,      // Palabra genérica (clasificación por defecto)
    EOF,          // Fin del archivo
    WHITESPACE    // Espacio en blanco
}
