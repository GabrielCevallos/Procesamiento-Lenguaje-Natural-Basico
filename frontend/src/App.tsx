/**
 * Componente Principal: App
 * Interfaz de usuario principal para el análisis de oraciones.
 */

import React, { useState } from 'react';
import parseService from './services/api';
import TokenTable from './components/TokenTable';
import TreeVisualization from './components/TreeVisualization';
import DerivationSteps from './components/DerivationSteps';
import { AnalysisResponse } from './types/index';
import './App.css';

function App() {
  const [sentence, setSentence] = useState('');
  const [analysisResult, setAnalysisResult] = useState<AnalysisResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleAnalyze = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!sentence.trim()) {
      setError('Por favor, ingresa una oración');
      return;
    }

    setLoading(true);
    setError(null);
    setAnalysisResult(null);

    try {
      const result = await parseService.analyzeSentence(sentence);
      
      if (result.success) {
        setAnalysisResult(result);
        setError(null);
      } else {
        // En caso de error, igualmente mostramos los tokens si están disponibles
        setAnalysisResult(result);
        setError(result.error || 'Error desconocido durante el análisis');
      }
    } catch (err: any) {
      // Si hay error HTTP 400, los datos del error están en la respuesta
      const errorData = err.response?.data;
      
      if (errorData) {
        setAnalysisResult(errorData);
        setError(errorData.error || 'Error en el análisis');
      } else {
        setError(err.message || 'Error de conexión con el servidor');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setSentence('');
    setAnalysisResult(null);
    setError(null);
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>📝 Validador de Oraciones - Análisis Sintáctico</h1>
        <p className="subtitle">Gramática: Sujeto + Verbo + Complemento</p>
      </header>

      <main className="app-main">
        <form onSubmit={handleAnalyze} className="input-form">
          <div className="input-group">
            <label htmlFor="sentence">Ingresa una oración:</label>
            <input
              id="sentence"
              type="text"
              value={sentence}
              onChange={(e) => setSentence(e.target.value)}
              placeholder="Ej: el perro corre en el parque"
              disabled={loading}
              className={error ? 'input-error' : ''}
            />
          </div>

          <div className="button-group">
            <button type="submit" disabled={loading} className="btn-analyze">
              {loading ? '⏳ Analizando...' : '🔍 Analizar'}
            </button>
            <button type="button" onClick={handleClear} disabled={loading} className="btn-clear">
              🔄 Limpiar
            </button>
          </div>
        </form>

        {error && (
          <div className="error-message">
            <span>❌ Error:</span> {error}
          </div>
        )}

        {analysisResult && (
          <div className="results-container">
            {analysisResult.success && (
              <div className="success-message">
                ✅ ¡Oración válida!
              </div>
            )}

            {analysisResult.tokens && analysisResult.tokens.length > 0 && (
              <TokenTable tokens={analysisResult.tokens} />
            )}

            {analysisResult.success && analysisResult.derivationSteps && (
              <>
                <DerivationSteps 
                  steps={analysisResult.derivationSteps!}
                  treeASCII={analysisResult.treeASCII || ''}
                />

                <TreeVisualization treeData={analysisResult.treeJSON!} />
              </>
            )}
          </div>
        )}
      </main>

      <footer className="app-footer">
        <p>© 2024 - Procesamiento de Lenguaje Natural | Teoría de Autómatas y Computabilidad</p>
      </footer>
    </div>
  );
}

export default App;
