import { api } from "./client";
import type {
  AvaliacaoResponse,
  CriarAvaliacaoRequest,
  ResumoAvaliacoesAnuncianteResponse,
  UUID,
} from "./types";

export const avaliacoesApi = {
  criar: (body: CriarAvaliacaoRequest) =>
    api.post<AvaliacaoResponse>("/avaliacoes", body),

  porAnunciante: (perfilAnuncianteId: UUID) =>
    api.get<AvaliacaoResponse[]>(
      `/avaliacoes/anunciante/${perfilAnuncianteId}`,
    ),

  resumoAnunciante: (perfilAnuncianteId: UUID) =>
    api.get<ResumoAvaliacoesAnuncianteResponse>(
      `/avaliacoes/anunciante/${perfilAnuncianteId}/resumo`,
    ),

  porAvaliador: (avaliadorId: UUID) =>
    api.get<AvaliacaoResponse[]>(`/avaliacoes/avaliador/${avaliadorId}`),
};
