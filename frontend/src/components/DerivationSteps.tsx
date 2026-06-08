import React from 'react';
import './DerivationSteps.css';

interface DerivationStepsProps {
  steps: string[];
}

const DerivationSteps: React.FC<DerivationStepsProps> = ({ steps }) => {
  return (
    <div className="derivation-panel">
      <h2>Pasos de Derivacion</h2>
      {steps.length === 0 ? (
        <p className="no-data">No hay pasos de derivacion</p>
      ) : (
        <div className="steps-list">
          {steps.map((step, index) => (
            <div key={index} className="step-item">
              <span className="step-number">{index + 1}</span>
              <span className="step-text">{step}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DerivationSteps;