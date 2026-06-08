import React from 'react';
import { TreeNode } from '../types/index';
import './TreeVisualization.css';

interface TreeVisualizationProps {
  treeData: TreeNode;
}

interface LayoutNode {
  label: string;
  isTerminal: boolean;
  x: number;
  y: number;
}

interface LayoutLink {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

const NODE_R = 30;
const VERTICAL_GAP = 80;
const MIN_NODE_SPACING = 70;

function getSubtreeWidth(node: TreeNode): number {
  if (!node.children || node.children.length === 0) {
    return MIN_NODE_SPACING;
  }
  return Math.max(
    node.children.reduce((sum, child) => sum + getSubtreeWidth(child), 0),
    MIN_NODE_SPACING
  );
}

function layoutTree(
  node: TreeNode,
  nodes: LayoutNode[],
  links: LayoutLink[],
  x: number,
  y: number
): void {
  nodes.push({
    label: node.symbol,
    isTerminal: node.terminal,
    x,
    y
  });

  if (node.children && node.children.length > 0) {
    const totalChildWidth = node.children.reduce(
      (sum, child) => sum + getSubtreeWidth(child), 0
    );
    let childX = x - totalChildWidth / 2;

    node.children.forEach((child) => {
      const childWidth = getSubtreeWidth(child);
      const childCenterX = childX + childWidth / 2;
      const childY = y + VERTICAL_GAP;

      links.push({
        x1: x, y1: y + NODE_R,
        x2: childCenterX, y2: childY - NODE_R
      });

      layoutTree(child, nodes, links, childCenterX, childY);
      childX += childWidth;
    });
  }
}

function getBounds(nodes: LayoutNode[]) {
  let minX = Infinity, maxX = -Infinity, maxY = -Infinity;
  for (const n of nodes) {
    minX = Math.min(minX, n.x - NODE_R - 10);
    maxX = Math.max(maxX, n.x + NODE_R + 10);
    maxY = Math.max(maxY, n.y + NODE_R + 10);
  }
  return { minX, maxX, maxY };
}

const TreeVisualization: React.FC<TreeVisualizationProps> = ({ treeData }) => {
  if (!treeData) {
    return <p className="no-data">No hay arbol para visualizar</p>;
  }

  const nodes: LayoutNode[] = [];
  const links: LayoutLink[] = [];
  const totalWidth = getSubtreeWidth(treeData);
  layoutTree(treeData, nodes, links, totalWidth / 2, 35);

  const { minX, maxX, maxY } = getBounds(nodes);
  const PAD = 30;
  const svgW = Math.max(300, maxX - minX + PAD * 2);
  const svgH = maxY + PAD * 2;

  return (
    <div className="tree-wrap">
      <svg width={svgW} height={svgH}>
        {links.map((link, i) => (
          <line
            key={`l${i}`}
            x1={link.x1 - minX + PAD}
            y1={link.y1 + PAD}
            x2={link.x2 - minX + PAD}
            y2={link.y2 + PAD}
            stroke="#95a5a6"
            strokeWidth="2"
          />
        ))}
        {nodes.map((node, i) => {
          const cx = node.x - minX + PAD;
          const cy = node.y + PAD;
          const fill = node.isTerminal ? '#27ae60' : '#3498db';
          const fontSize = node.isTerminal ? 11 : 12;
          return (
            <g key={`n${i}`}>
              <circle cx={cx} cy={cy} r={NODE_R} fill={fill} stroke="#2c3e50" strokeWidth="2" />
              <text
                x={cx}
                y={cy}
                textAnchor="middle"
                dominantBaseline="central"
                fill="white"
                fontSize={fontSize}
                fontWeight="bold"
              >
                {node.label}
              </text>
            </g>
          );
        })}
      </svg>
    </div>
  );
};

export default TreeVisualization;