# 📝 Procesamiento de Lenguaje Natural - Validador de Oraciones

Validador de estructura gramatical para oraciones en español. Implementa un analizador sintáctico que valida la estructura: **Sujeto + Verbo + Complemento**.

**Materia:** Teoría de Autómatas y Computabilidad Avanzada  
**Unidad:** 2 - Procesamiento de Lenguaje Natural Básico  
**Grupo:** 2, 5, 8

---

## 🏗️ Arquitectura

Se utiliza el patrón **MVC + Componentes Especializados**:

- **Backend:** Java con Spring Boot
  - Lexer: Análisis léxico (tokenización)
  - Parser: Análisis sintáctico (validación gramatical)
  - AST: Árbol sintáctico abstracto con derivaciones
  
- **Frontend:** React con TypeScript
  - Interfaz visual para ingresar oraciones
  - Tabla de tokens y lexemas
  - Visualización del árbol de derivación
  - Pasos de derivación (izquierda)

**Por qué esta arquitectura:**
- Separación de responsabilidades clara
- Escalabilidad sin afectar componentes existentes
- Testabilidad: Cada módulo es independiente
- Mantenibilidad siguiendo principios SOLID

---

## 📐 Gramática

### Definición Formal

La gramática es **libre de contexto (CFG)** y está en forma **SLR(1)**:

```
S        → Oración

Oración  → Sujeto Verbo Complemento

Sujeto   → Determinante? Palabra+

Verbo    → Palabra+

Complemento → Palabra+ 
            | Determinante Palabra+ 
            | Preposición Determinante? Palabra+

Determinante → Palabra
Preposición  → Palabra

Palabra   → [cualquier secuencia alphanumerica]
```

### Características

- ✅ **Genérica:** Acepta cualquier oración sin palabras hardcodeadas
- ✅ **Sin ambigüedades:** Cada derivación es única
- ✅ **SLR(1):** Puede analizarse con tabla LR simple
- ✅ **Flexible:** El profesor puede probar con cualquier oración

---

## 🔤 Léxico

El análisis léxico produce dos tipos de tokens:

| Tipo | Descripción | Ejemplo |
|------|-------------|---------|
| **PALABRA** | Cualquier secuencia de caracteres no espacios | "el", "perro", "corre" |
| **EOF** | Fin de entrada | (automático) |

### Tabla de Tokens Generada

En el frontend se muestra una tabla interactiva con:
- **#:** Número de token
- **Tipo de Token:** Clasificación (PALABRA, etc.)
- **Lexema:** Valor exacto del token
- **Posición:** Índice en la entrada
- **Línea:** Número de línea

---

## 🌳 Derivación por la Izquierda

El parser realiza derivaciones **por la izquierda** (leftmost derivation).

### Ejemplo de Ejecución

**Entrada:** "el perro corre en el parque"

**Pasos de derivación:**
```
S
Oración
Sujeto Verbo Complemento
el perro Verbo Complemento
el perro corre Complemento
el perro corre en el parque
```

### Salida Visual

Se proporciona en dos formatos:

#### 1. **Formato ASCII** (Árbol de derivación)
```
└── Oración
    ├── Sujeto
    │   ├── el
    │   └── perro
    ├── Verbo
    │   └── corre
    └── Complemento
        ├── en
        ├── el
        └── parque
```

#### 2. **Formato Gráfico** (Visualización con colores)
- Nodos no terminales: **Azul** (`#3498db`)
- Nodos terminales (palabras): **Verde** (`#27ae60`)
- Conexiones visuales entre nodos

---

## 📊 Precedencia de Operadores

Como el análisis es **SOLO GRAMATICAL** (sin semántica), no se aplican operadores lógicos. La estructura es simplemente:

1. **Sujeto** (mínimo 1 palabra)
2. **Verbo** (mínimo 1 palabra)
3. **Complemento** (mínimo 1 palabra)

---

## 🚀 Instalación y Ejecución

### Requisitos Previos

- **Java 21+**
- **Maven 3.8+**
- **Node.js 18+**
- **npm 9+**

### Backend (Spring Boot)

```bash
# Navegar al directorio del backend
cd backend

# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

### Frontend (React)

```bash
# En otra terminal, navegar al frontend
cd frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev
```

El frontend estará disponible en: `http://localhost:3000`

### Acceso a la Aplicación

1. Abre navegador en `http://localhost:3000`
2. Ingresa una oración en el campo de texto
3. Haz clic en **"Analizar"**
4. Visualiza:
   - ✅ Tabla de tokens y lexemas
   - ✅ Pasos de derivación
   - ✅ Árbol en ASCII
   - ✅ Árbol gráfico

---

## 📁 Estructura del Proyecto

```
Procesamiento-Lenguaje-Natural-Basico/
│
├── backend/
│   ├── src/main/java/com/pln/
│   │   ├── lexer/
│   │   │   ├── TokenType.java       # Enumeración de tipos
│   │   │   ├── Token.java           # Representación de token
│   │   │   └── Lexer.java           # Análisis léxico
│   │   ├── parser/
│   │   │   ├── Parser.java          # Análisis sintáctico
│   │   │   └── ParseException.java  # Excepción de parsing
│   │   ├── ast/
│   │   │   ├── ASTNode.java         # Nodo del árbol
│   │   │   └── DerivationStep.java  # Paso de derivación
│   │   ├── controller/
│   │   │   └── ParseController.java # REST API
│   │   └── Application.java         # Main Spring Boot
│   ├── src/main/resources/
│   │   └── application.yml          # Configuración
│   └── pom.xml                      # Dependencias Maven
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── TokenTable.tsx       # Tabla de tokens
│   │   │   ├── TokenTable.css
│   │   │   ├── DerivationSteps.tsx  # Pasos y árbol ASCII
│   │   │   ├── DerivationSteps.css
│   │   │   ├── TreeVisualization.tsx # Árbol gráfico
│   │   │   └── TreeVisualization.css
│   │   ├── services/
│   │   │   └── api.ts               # Cliente HTTP
│   │   ├── types/
│   │   │   └── index.ts             # Tipos TypeScript
│   │   ├── App.tsx                  # Componente principal
│   │   ├── App.css                  # Estilos globales
│   │   └── main.tsx                 # Entry point
│   ├── index.html                   # HTML base
│   ├── package.json                 # Dependencias npm
│   ├── tsconfig.json                # Configuración TypeScript
│   └── vite.config.ts               # Configuración Vite
│
├── README.md                        # Este archivo
└── .gitignore                       # Git ignorar
```

---

## 🔍 Validación

El sistema valida la estructura gramatical de la oración. Ejemplos:

### ✅ Válidas

- "el perro corre"
- "mi hermano juega en el parque"
- "los gatos duermen"
- "una persona come manzanas"

### ❌ Inválidas

- "perro el corre" (orden incorrecto)
- "el corre" (falta complemento)
- "el perro" (falta verbo)

---

## 📝 Detalles Técnicos

### Lexer

- Divide la entrada en tokens por espacios en blanco
- Genera automaticamente un token EOF
- Rastrea posición y línea de cada token

### Parser

- Implementa descenso recursivo predictivo
- Realiza derivaciones por la izquierda
- Levanta excepciones con información de error

### AST

- Representa la estructura sintáctica completa
- Nodos terminales: palabras
- Nodos no terminales: símbolos gramaticales (S, Oración, Sujeto, etc.)
- Convertible a formato ASCII y JSON

### API REST

- **Endpoint:** `POST /api/parse/analyze`
- **Request:** `{ "sentence": "string" }`
- **Response:** 
  ```json
  {
    "success": boolean,
    "tokens": Array,
    "derivationSteps": Array,
    "treeASCII": string,
    "treeJSON": Object
  }
  ```

---

## 📚 Tecnologías Utilizadas

### Backend
- **Java 17**: Lenguaje de programación
- **Spring Boot 3.2**: Framework web
- **Maven**: Gestor de dependencias y build

### Frontend
- **React 18**: Biblioteca de UI
- **TypeScript**: Tipado estático
- **Vite**: Build tool rápido
- **Axios**: Cliente HTTP

---

## 👨‍💻 Implementación de Programación Limpia

- **Nombres significativos:** Variables y métodos con nombres claros
- **Funciones pequeñas:** Cada función hace una cosa bien
- **Comentarios:** Explicación de conceptos complejos
- **Manejo de errores:** Excepciones clara y descriptivas
- **Sin hardcoding:** Lógica flexible y genérica

---

## 🎯 Consideraciones Importantes

- ⚠️ **SOLO GRAMÁTICA:** El sistema valida estructura sintáctica, NO semántica
- ⚠️ **SIN PALABRAS RESERVADAS:** Cualquier palabra es válida
- ⚠️ **DERIVACIONES POR IZQUIERDA:** Orden específico de análisis
- ⚠️ **SIN OPERADORES:** (|, &, ~) no se incluyen porque no son necesarios para la estructura básica

---

## 📞 Soporte

Para dudas o problemas:
1. Verifica que ambos servidores están ejecutándose (puerto 8080 para backend, 3000 para frontend)
2. Revisa los logs en consola
3. Asegúrate de que los puertos no estén en uso

---

## 📄 Licencia

Proyecto académico - Teoría de Autómatas y Computabilidad Avanzada

---

**Última actualización:** Junio 2024
