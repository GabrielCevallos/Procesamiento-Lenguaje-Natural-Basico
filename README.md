# Procesamiento de Lenguaje Natural - Analizador Sintactico

Analizador sintactico para oraciones simples en espanol basado en Gramaticas Libres de Contexto (GLC/CFG) para Chatbots.

**Materia:** Teoria de Automatas y Computabilidad Avanzada  
**Unidad:** 2 - Procesamiento de Lenguaje Natural Basico

## Gramatica Libre de Contexto

ORACION -> SUJETO VERBO COMPLEMENTO
SUJETO -> SN | PRONOMBRE
SN -> ARTICULO? SUSTANTIVO ADJETIVO?
COMPLEMENTO -> (PREPOSICION SN)+ | SN | ADVERBIO

## Arquitectura

**Backend:** Java + Spring Boot (puerto 8080)
- Lexer: Tokenizacion y clasificacion en 8 categorias (ARTICULO, PRONOMBRE, SUSTANTIVO, VERBO, ADJETIVO, ADVERBIO, PREPOSICION, CONJUNCION)
- Parser: Descenso recursivo predictivo con CFG formal
- AST: Arbol de derivacion con formas sentenciales
- AmbiguityDetector: Deteccion de ambiguedad de adjuncion de SP (PP attachment)

**Frontend:** React 18 + TypeScript + Vite (puerto 3000)
- Interfaz tipo chatbot
- Tabla de tokens por categoria gramatical
- Arbol de derivacion (ASCII y grafico)
- Identificacion de Sujeto, Verbo y Complemento
- Deteccion de ambiguedad sintactica

## Categorias Lexicas

| Categoria | Ejemplos |
|-----------|----------|
| ARTICULO | el, la, un, una, los, las |
| PRONOMBRE | yo, tu, el, ella, nosotros, ellos |
| SUSTANTIVO | perro, casa, libro, parque, museo |
| VERBO | corre, canta, lee, tiene, explica |
| ADJETIVO | negro, blanca, grande, pequeno |
| ADVERBIO | bien, mal, muy, siempre, aqui |
| PREPOSICION | en, con, por, a, de, para, al, del |
| CONJUNCION | y, e, ni, o, pero, aunque, porque |

## Verbos Soportados

150+ verbos regulares e irregulares con conjugacion completa (presente, preterito, imperfecto, futuro, condicional, subjuntivo, imperativo, gerundio, participio).

## Endpoint API

POST /api/parse/analyze
Body: { "sentence": "El perro corre en el parque" }

Respuesta: success, tokens, subject, verb, complement, derivationSteps, sententialForms, treeASCII, treeJSON, hasAmbiguity, ambiguities, error

## Oraciones de Prueba

**10 validas:**
1. El perro corre en el parque
2. La nina lee un libro
3. Nosotros visitamos el museo
4. El gato negro duerme en el sofa
5. Ella canta bien
6. Los ninos juegan en el jardin
7. Un hombre compra pan
8. El profesor explica la leccion
9. Mi amigo tiene un perro
10. La casa blanca esta en la colina

**5 invalidas:**
1. Corre parque (falta sujeto)
2. Azul rapidamente casa (falta verbo)
3. el (sujeto incompleto)
4. perro (falta verbo)
5. en el (sujeto incompleto)

## Instalacion y Ejecucion

**Backend:**
cd backend
mvn spring-boot:run

**Frontend:**
cd frontend
npm install
npm run dev

## Tecnologias

- Java 17+, Spring Boot 3.2+, Maven
- React 18, TypeScript, Vite, Axios
- Vocabulario en archivos JSON (no base de datos)
- Sin dependencias de NLP externas, OpenAI, GPT, LLMs, ML/DL
