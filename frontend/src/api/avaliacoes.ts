import { api } from "./client";
import type {
  AvaliacaoResponse,
  CriarAvaliacaoRequest,
  ResumoAvaliacoesLocadorResponse,
  UUID,
} from "./types";

export const avaliacoesApi = {
  criar: (body: CriarAvaliacaoRequest) =>
    api.post<AvaliacaoResponse>("/avaliacoes", body),
  porLocador: (locadorId: UUID) =>
    api.get<AvaliacaoResponse[]>(`/avaliacoes/locador/${locadorId}`),
  resumoLocador: (locadorId: UUID) =>
    api.get<ResumoAvaliacoesLocadorResponse>(
      `/avaliacoes/locador/${locadorId}/resumo`,
    ),
  porAvaliador: (avaliadorId: UUID) =>
    api.get<AvaliacaoResponse[]>(`/avaliacoes/avaliador/${avaliadorId}`),
};
