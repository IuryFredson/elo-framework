import { api } from "./client";
import type {
  CriarManifestacaoInteresseRequest,
  ManifestacaoInteresseDetalheResponse,
  ManifestacaoInteresseResponse,
  UUID,
} from "./types";

const MANIFESTACOES_PATH = "/manifestacoes";

export const manifestacoesApi = {
  criar: (body: CriarManifestacaoInteresseRequest) =>
    api.post<ManifestacaoInteresseDetalheResponse>(
      MANIFESTACOES_PATH,
      body,
    ),

  obter: (id: UUID, solicitanteId: UUID) =>
    api.get<ManifestacaoInteresseDetalheResponse>(
      `${MANIFESTACOES_PATH}/${id}`,
      { solicitanteId },
    ),

  porAnuncio: (anuncioId: UUID, anuncianteId: UUID) =>
    api.get<ManifestacaoInteresseResponse[]>(
      `${MANIFESTACOES_PATH}/oferta/${anuncioId}`,
      { publicadorId: anuncianteId },
    ),

  porUsuario: (interessadoId: UUID) =>
    api.get<ManifestacaoInteresseResponse[]>(
      `${MANIFESTACOES_PATH}/interessado/${interessadoId}`,
    ),

  aceitar: (id: UUID, anuncianteId: UUID) =>
    api.patch<ManifestacaoInteresseDetalheResponse>(
      `${MANIFESTACOES_PATH}/${id}/aceitar`,
      undefined,
      { publicadorId: anuncianteId },
    ),

  recusar: (id: UUID, anuncianteId: UUID) =>
    api.patch<ManifestacaoInteresseDetalheResponse>(
      `${MANIFESTACOES_PATH}/${id}/recusar`,
      undefined,
      { publicadorId: anuncianteId },
    ),

  cancelar: (id: UUID, interessadoId: UUID) =>
    api.patch<ManifestacaoInteresseDetalheResponse>(
      `${MANIFESTACOES_PATH}/${id}/cancelar`,
      undefined,
      { interessadoId },
    ),
};
