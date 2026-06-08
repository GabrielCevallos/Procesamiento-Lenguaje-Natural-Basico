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
      setError('Por favor, ingresa una oracion');
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
        setAnalysisResult(result);
        setError(result.error || 'Error desconocido durante el analisis');
      }
    } catch (err: any) {
      const errorData = err.response?.data;

      if (errorData) {
        setAnalysisResult(errorData);
        setError(errorData.error || 'Error en el analisis');
      } else {
        setError(err.message || 'Error de conexion con el servidor');
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
        <h1>Analizador Sintactico de Oraciones en Espanol</h1>
        <p className="subtitle">Gramatica: Sujeto + Verbo + Complemento (GLC/CFG)</p>
      </header>

      <section className="grammar-section">
        <h3>Gramatica Libre de Contexto</h3>
        <div className="grammar-rules">
          <code>{'ORACION \u2192 SUJETO VERBO COMPLEMENTO'}</code>
          <code>{'SUJETO \u2192 SN | PRONOMBRE'}</code>
          <code>{'SN \u2192 ARTICULO? SUSTANTIVO ADJETIVO?'}</code>
          <code>{'COMPLEMENTO \u2192 PREPOSICION SN | SN | ADVERBIO'}</code>
        </div>
      </section>

      <main className="app-main">
        <form onSubmit={handleAnalyze} className="input-form">
          <div className="input-group">
            <label htmlFor="sentence">Ingresa una oracion:</label>
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
              {loading ? 'Analizando...' : 'Analizar'}
            </button>
            <button type="button" onClick={handleClear} disabled={loading} className="btn-clear">
              Limpiar
            </button>
          </div>
        </form>

        {error && (
          <div className="error-message">
            <span>Error:</span> {error}
          </div>
        )}

        {analysisResult && (
          <div className="results-container">
            {analysisResult.success && (
              <div className="success-message">
                Oracion valida
              </div>
            )}

            {!analysisResult.success && (
              <div className="invalid-message">
                Oracion invalida
              </div>
            )}

            {analysisResult.subject && analysisResult.subject.length > 0 && (
              <div className="grammatical-parts">
                <div className="part-box subject-box">
                  <span className="part-label">Sujeto:</span>
                  <span className="part-value">{analysisResult.subject.join(' ')}</span>
                </div>
                <div className="part-box verb-box">
                  <span className="part-label">Verbo:</span>
                  <span className="part-value">{analysisResult.verb?.join(' ')}</span>
                </div>
                <div className="part-box complement-box">
                  <span className="part-label">Complemento:</span>
                  <span className="part-value">{analysisResult.complement?.join(' ')}</span>
                </div>
              </div>
            )}

            {analysisResult.hasAmbiguity && analysisResult.ambiguities && (
              <div className="ambiguity-section">
                <h3>Ambiguedad Sintactica Detectada</h3>
                {analysisResult.ambiguities.map((amb, idx) => (
                  <p key={idx} className="ambiguity-item">{amb}</p>
                ))}
              </div>
            )}

            {analysisResult.tokens && analysisResult.tokens.length > 0 && (
              <TokenTable tokens={analysisResult.tokens} />
            )}

            {analysisResult.derivationSteps && (
              <>
                <DerivationSteps steps={analysisResult.derivationSteps!} />
                <TreeVisualization treeData={analysisResult.treeJSON!} />
              </>
            )}
          </div>
        )}
      </main>

      <footer className="app-footer">
        <p>Procesamiento de Lenguaje Natural | Teoria de Automatas y Computabilidad | GLC/CFG</p>
      </footer>
    </div>
  );
}

export default App;
