import { api } from "./client";
import type { UUID } from "./types";
import type {
  AlterarStatusEstudanteRequest,
  AtualizarEstudanteRequest,
  AtualizarPerfilAcademicoRequest,
  CriarEstudanteRequest,
  CriarGrupoEstudoRequest,
  CriarManifestacaoInteresseGrupoRequest,
  EstudanteResponse,
  GrupoEstudoResponse,
  ManifestacaoInteresseGrupoResponse,
  PerfilAcademicoResponse,
  StatusGrupoEstudo,
  StudyBuddyMatchingResponse,
} from "../study-buddy/types";

export const estudantesApi = {
  listar: () => api.get<EstudanteResponse[]>("/study-buddy/estudantes"),
  obter: (id: UUID) => api.get<EstudanteResponse>(`/study-buddy/estudantes/${id}`),
  criar: (body: CriarEstudanteRequest) =>
    api.post<EstudanteResponse>("/study-buddy/estudantes", body),
  atualizar: (id: UUID, body: AtualizarEstudanteRequest) =>
    api.put<EstudanteResponse>(`/study-buddy/estudantes/${id}`, body),
  alterarStatus: (id: UUID, body: AlterarStatusEstudanteRequest) =>
    api.patch<EstudanteResponse>(`/study-buddy/estudantes/${id}/status`, body),
};

export const perfilAcademicoApi = {
  obter: (estudanteId: UUID) =>
    api.get<PerfilAcademicoResponse>(`/study-buddy/estudantes/${estudanteId}/perfil`),
  atualizar: (estudanteId: UUID, body: AtualizarPerfilAcademicoRequest) =>
    api.put<PerfilAcademicoResponse>(
      `/study-buddy/estudantes/${estudanteId}/perfil`,
      body,
    ),
};

export const gruposApi = {
  listar: () => api.get<GrupoEstudoResponse[]>("/study-buddy/grupos"),
  obter: (id: UUID) => api.get<GrupoEstudoResponse>(`/study-buddy/grupos/${id}`),
  criar: (body: CriarGrupoEstudoRequest) =>
    api.post<GrupoEstudoResponse>("/study-buddy/grupos", body),
  atualizarStatus: (id: UUID, novoStatus: StatusGrupoEstudo) =>
    api.patch<GrupoEstudoResponse>(`/study-buddy/grupos/${id}/status`, novoStatus),
};

export const manifestacoesGrupoApi = {
  criar: (body: CriarManifestacaoInteresseGrupoRequest) =>
    api.post<ManifestacaoInteresseGrupoResponse>("/study-buddy/manifestacoes", body),
  aceitar: (id: UUID, publicadorId: UUID) =>
    api.post<ManifestacaoInteresseGrupoResponse>(
      `/study-buddy/manifestacoes/${id}/aceitar`,
      undefined,
      { publicadorId },
    ),
  recusar: (id: UUID, publicadorId: UUID) =>
    api.post<ManifestacaoInteresseGrupoResponse>(
      `/study-buddy/manifestacoes/${id}/recusar`,
      undefined,
      { publicadorId },
    ),
  cancelar: (id: UUID, interessadoId: UUID) =>
    api.post<ManifestacaoInteresseGrupoResponse>(
      `/study-buddy/manifestacoes/${id}/cancelar`,
      undefined,
      { interessadoId },
    ),
  porGrupo: (grupoId: UUID, publicadorId: UUID) =>
    api.get<ManifestacaoInteresseGrupoResponse[]>(
      `/study-buddy/grupos/${grupoId}/manifestacoes`,
      { publicadorId },
    ),
  porEstudante: (estudanteId: UUID) =>
    api.get<ManifestacaoInteresseGrupoResponse[]>(
      `/study-buddy/estudantes/${estudanteId}/manifestacoes`,
    ),
};

export const studyBuddyMatchingApi = {
  buscar: (estudanteId: UUID, topN = 10) =>
    api.get<StudyBuddyMatchingResponse>("/study-buddy/matching", {
      estudanteId,
      topN,
    }),
};
