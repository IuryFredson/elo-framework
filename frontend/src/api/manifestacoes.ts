import { api } from "./client";
import type {
  CriarManifestacaoInteresseRequest,
  ManifestacaoInteresseDetalheResponse,
  ManifestacaoInteresseResponse,
  UUID,
} from "./types";

export const manifestacoesApi = {
  criar: (body: CriarManifestacaoInteresseRequest) =>
    api.post<ManifestacaoInteresseDetalheResponse>(
      "/manifestacoes-interesse",
      body,
    ),

  obter: (id: UUID, solicitanteId: UUID) =>
    api.get<ManifestacaoInteresseDetalheResponse>(
      `/manifestacoes-interesse/${id}`,
      { solicitanteId },
    ),

  porAnuncio: (anuncioId: UUID, anuncianteId: UUID) =>
    api.get<ManifestacaoInteresseResponse[]>(
      `/manifestacoes-interesse/anuncio/${anuncioId}`,
      { anuncianteId },
    ),

  porUsuario: (interessadoId: UUID) =>
    api.get<ManifestacaoInteresseResponse[]>(
      `/manifestacoes-interesse/usuario/${interessadoId}`,
    ),

  aceitar: (id: UUID, anuncianteId: UUID) =>
    api.patch<ManifestacaoInteresseDetalheResponse>(
      `/manifestacoes-interesse/${id}/aceitar`,
      undefined,
      { anuncianteId },
    ),

  recusar: (id: UUID, anuncianteId: UUID) =>
    api.patch<ManifestacaoInteresseDetalheResponse>(
      `/manifestacoes-interesse/${id}/recusar`,
      undefined,
      { anuncianteId },
    ),

  cancelar: (id: UUID, interessadoId: UUID) =>
    api.patch<ManifestacaoInteresseDetalheResponse>(
      `/manifestacoes-interesse/${id}/cancelar`,
      undefined,
      { interessadoId },
    ),
};
