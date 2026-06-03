/**
 * Componente: Visualización del Árbol Sintáctico
 * Renderiza el árbol en formato gráfico con nodos coloreados.
 */

import React from 'react';
import { TreeNode } from '../types/index';
import './TreeVisualization.css';

interface TreeVisualizationProps {
  treeData: TreeNode;
}

const TreeVisualization: React.FC<TreeVisualizationProps> = ({ treeData }) => {
  if (!treeData) {
    return <p className="no-data">No hay árbol para visualizar</p>;
  }

  return (
    <div className="tree-visualization">
      <h2>🎨 Árbol de Derivación (Visualización Gráfica)</h2>
      <div className="tree-container">
        <TreeNodeComponent node={treeData} />
      </div>
    </div>
  );
};

interface TreeNodeComponentProps {
  node: TreeNode;
}

const TreeNodeComponent: React.FC<TreeNodeComponentProps> = ({ node }) => {
  const isTerminal = node.terminal;
  const nodeColor = isTerminal ? '#27ae60' : '#3498db';
  const bgColor = isTerminal ? '#d5f4e6' : '#ebf5fb';

  return (
    <div className="tree-node-wrapper">
      <div 
        className="tree-node" 
        style={{ 
          borderColor: nodeColor, 
          backgroundColor: bgColor 
        }}
      >
        <span className="node-symbol" style={{ color: nodeColor }}>
          {node.symbol}
        </span>
        {isTerminal && <span className="terminal-badge">(T)</span>}
      </div>
      
      {node.children && node.children.length > 0 && (
        <div className="tree-children">
          {node.children.map((child, index) => (
            <div key={index} className="child-wrapper">
              <TreeNodeComponent node={child} />
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TreeVisualization;
