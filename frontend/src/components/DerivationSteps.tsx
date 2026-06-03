/**
 * Componente: Pasos de Derivación
 * Muestra los pasos de derivación por la izquierda y el árbol en ASCII.
 */

import React from 'react';
import './DerivationSteps.css';

interface DerivationStepsProps {
  steps: string[];
  treeASCII: string;
}

const DerivationSteps: React.FC<DerivationStepsProps> = ({ steps, treeASCII }) => {
  return (
    <div className="derivation-container">
      <div className="derivation-steps">
        <h2>📐 Pasos de Derivación por la Izquierda</h2>
        {steps.length === 0 ? (
          <p className="no-data">No hay pasos de derivación</p>
        ) : (
          <div className="steps-list">
            {steps.map((step, index) => (
              <div key={index} className="step">
                <span className="step-number">{index}</span>
                <span className="step-content">{step}</span>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="tree-ascii">
        <h2>🌳 Árbol de Derivación (ASCII)</h2>
        {treeASCII ? (
          <pre className="ascii-tree">{treeASCII}</pre>
        ) : (
          <p className="no-data">No hay árbol para mostrar</p>
        )}
      </div>
    </div>
  );
};

export default DerivationSteps;
