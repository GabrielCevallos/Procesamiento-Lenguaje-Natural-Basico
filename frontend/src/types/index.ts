export interface Token {
  id: number;
  type: string;
  lexeme: string;
  part: string;
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
  subject?: string[];
  verb?: string[];
  complement?: string[];
  derivationSteps?: string[];
  treeJSON?: TreeNode;
  hasAmbiguity?: boolean;
  ambiguities?: string[];
  error?: string;
}
