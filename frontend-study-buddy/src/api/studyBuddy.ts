import { api } from "./client";
import type { StatusDenuncia, UUID } from "./types";
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
  CriarDenunciaGrupoEstudoRequest,
  DenunciaGrupoEstudoResponse,
  ModerarDenunciaGrupoEstudoRequest,
  ModeracaoGrupoEstudoResponse,
  StudyBuddyMatchingResponse,
} from "../study-buddy/types";

const BASE_PATH = "/study-buddy";
const USUARIOS_PATH = `${BASE_PATH}/usuarios`;
const OFERTAS_PATH = `${BASE_PATH}/ofertas`;
const MANIFESTACOES_PATH = `/manifestacoes`;
const DENUNCIAS_PATH = `/denuncias`;
const MODERACOES_PATH = `/moderacoes/denuncias`;

export const estudantesApi = {
  listar: () => api.get<EstudanteResponse[]>(USUARIOS_PATH),
  obter: (id: UUID) => api.get<EstudanteResponse>(`${USUARIOS_PATH}/${id}`),
  criar: (body: CriarEstudanteRequest) =>
    api.post<EstudanteResponse>(USUARIOS_PATH, body),
  atualizar: (id: UUID, body: AtualizarEstudanteRequest) =>
    api.put<EstudanteResponse>(`${USUARIOS_PATH}/${id}`, body),
  alterarStatus: (id: UUID, body: AlterarStatusEstudanteRequest) =>
    api.patch<EstudanteResponse>(`${USUARIOS_PATH}/${id}/status`, body),
};

export const perfilAcademicoApi = {
  obter: (estudanteId: UUID) =>
    api.get<PerfilAcademicoResponse>(`${USUARIOS_PATH}/${estudanteId}/perfil`),
  atualizar: (estudanteId: UUID, body: AtualizarPerfilAcademicoRequest) =>
    api.put<PerfilAcademicoResponse>(
      `${USUARIOS_PATH}/${estudanteId}/perfil`,
      body,
    ),
};

export const gruposApi = {
  listar: () => api.get<GrupoEstudoResponse[]>(OFERTAS_PATH),
  obter: (id: UUID) => api.get<GrupoEstudoResponse>(`${OFERTAS_PATH}/${id}`),
  criar: (body: CriarGrupoEstudoRequest) =>
    api.post<GrupoEstudoResponse>(OFERTAS_PATH, body),
  atualizarStatus: (id: UUID, novoStatus: StatusGrupoEstudo) =>
    api.patch<GrupoEstudoResponse>(`${OFERTAS_PATH}/${id}/status`, novoStatus),
};

export const manifestacoesGrupoApi = {
  criar: (body: CriarManifestacaoInteresseGrupoRequest) =>
    api.post<ManifestacaoInteresseGrupoResponse>(MANIFESTACOES_PATH, body),
  aceitar: (id: UUID, publicadorId: UUID) =>
    api.patch<ManifestacaoInteresseGrupoResponse>(
      `${MANIFESTACOES_PATH}/${id}/aceitar`,
      undefined,
      { publicadorId },
    ),
  recusar: (id: UUID, publicadorId: UUID) =>
    api.patch<ManifestacaoInteresseGrupoResponse>(
      `${MANIFESTACOES_PATH}/${id}/recusar`,
      undefined,
      { publicadorId },
    ),
  cancelar: (id: UUID, interessadoId: UUID) =>
    api.patch<ManifestacaoInteresseGrupoResponse>(
      `${MANIFESTACOES_PATH}/${id}/cancelar`,
      undefined,
      { interessadoId },
    ),
  porGrupo: (grupoId: UUID, publicadorId: UUID) =>
    api.get<ManifestacaoInteresseGrupoResponse[]>(
      `${MANIFESTACOES_PATH}/oferta/${grupoId}`,
      { publicadorId },
    ),
  porEstudante: (estudanteId: UUID) =>
    api.get<ManifestacaoInteresseGrupoResponse[]>(
      `${MANIFESTACOES_PATH}/interessado/${estudanteId}`,
    ),
};

export const studyBuddyMatchingApi = {
  buscar: (estudanteId: UUID, topN = 10) =>
    api.get<StudyBuddyMatchingResponse>(`${BASE_PATH}/matching`, {
      solicitanteId: estudanteId,
      topN,
    }),
};

export const denunciasGrupoApi = {
  criar: (body: CriarDenunciaGrupoEstudoRequest) =>
    api.post<DenunciaGrupoEstudoResponse>(DENUNCIAS_PATH, body),
  listar: () => api.get<DenunciaGrupoEstudoResponse[]>(DENUNCIAS_PATH),
  obter: (id: UUID) => api.get<DenunciaGrupoEstudoResponse>(`${DENUNCIAS_PATH}/${id}`),
  porGrupo: (grupoId: UUID) =>
    api.get<DenunciaGrupoEstudoResponse[]>(`${DENUNCIAS_PATH}/oferta/${grupoId}`),
  porEstudante: (estudanteId: UUID) =>
    api.get<DenunciaGrupoEstudoResponse[]>(`${DENUNCIAS_PATH}/usuario/${estudanteId}`),
  porStatus: (status: StatusDenuncia) =>
    api.get<DenunciaGrupoEstudoResponse[]>(`${DENUNCIAS_PATH}/status/${status}`),
};

export const moderacaoGrupoApi = {
  moderar: (denunciaId: UUID, body: ModerarDenunciaGrupoEstudoRequest) =>
    api.patch<ModeracaoGrupoEstudoResponse>(`${MODERACOES_PATH}/${denunciaId}`, body),
};
