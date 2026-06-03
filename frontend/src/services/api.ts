/**
 * Servicio de API para comunicación con el backend.
 */

import axios from 'axios';
import { AnalysisResponse } from '../types/index';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const parseService = {
  /**
   * Analiza una oración.
   * @param sentence La oración a analizar
   * @returns Respuesta con análisis completo
   */
  analyzeSentence: async (sentence: string): Promise<AnalysisResponse> => {
    const response = await api.post('/parse/analyze', { sentence });
    return response.data;
  },
};

export default parseService;
