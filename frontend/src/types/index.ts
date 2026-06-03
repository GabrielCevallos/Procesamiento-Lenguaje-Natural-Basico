/**
 * Tipos TypeScript para la aplicación frontend.
 */

export interface Token {
  id: number;
  type: string;
  lexeme: string;
  part: string; // Sujeto, Verbo, o Complemento
}

export interface TreeNode {
  symbol: string;
  terminal: boolean;
  children: TreeNode[];
}

export interface AnalysisResponse {
  success: boolean;
  sentence: string;
  tokens?: Token[];
  derivationSteps?: string[];
  treeASCII?: string;
  treeJSON?: TreeNode;
  error?: string;
}
