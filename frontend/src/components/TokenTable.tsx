/**
 * Componente: Tabla de Tokens y Lexemas
 * Muestra los tokens identificados durante el análisis léxico.
 */

import React from 'react';
import { Token } from '../types/index';
import './TokenTable.css';

interface TokenTableProps {
  tokens: Token[];
}

const getPartColor = (part: string): string => {
  switch (part) {
    case 'SUJETO': return '#e8f4f8';
    case 'VERBO': return '#fff3cd';
    case 'COMPLEMENTO': return '#e8f5e9';
    default: return '#f0f0f0';
  }
};

const getPartBgColor = (part: string): string => {
  switch (part) {
    case 'SUJETO': return '#3498db';
    case 'VERBO': return '#f39c12';
    case 'COMPLEMENTO': return '#27ae60';
    default: return '#95a5a6';
  }
};

const TokenTable: React.FC<TokenTableProps> = ({ tokens }) => {
  return (
    <div className="token-table-container">
      <h2>Tabla de Tokens y Lexemas</h2>
      {tokens.length === 0 ? (
        <p className="no-data">No hay tokens para mostrar</p>
      ) : (
        <table className="token-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Tipo de Token</th>
              <th>Lexema</th>
              <th>Elemento de la Oración</th>
            </tr>
          </thead>
          <tbody>
            {tokens.map((token) => (
              <tr key={token.id}>
                <td>{token.id}</td>
                <td className="token-type">{token.type}</td>
                <td className="lexeme">{token.lexeme}</td>
                <td>
                  <span
                    className="part-badge"
                    style={{
                      backgroundColor: getPartColor(token.part),
                      borderLeft: `4px solid ${getPartBgColor(token.part)}`
                    }}
                  >
                    {token.part}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default TokenTable;
