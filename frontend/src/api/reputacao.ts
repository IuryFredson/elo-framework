import { api } from "./client";
import type {
  PerfilAnuncianteResponse,
  ReputacaoAnuncianteResponse,
  UUID,
} from "./types";

export const reputacaoApi = {
  doAnunciante: (perfilAnuncianteId: UUID) =>
    api.get<ReputacaoAnuncianteResponse>(
      `/reputacao/anunciante/${perfilAnuncianteId}`,
    ),
};

export const perfilAnuncianteApi = {
  porUsuario: (usuarioId: UUID) =>
    api.get<PerfilAnuncianteResponse>(
      `/perfis-anunciante/usuario/${usuarioId}`,
    ),

  habilitar: (universitarioId: UUID) =>
    api.post<PerfilAnuncianteResponse>(
      `/perfis-anunciante/universitarios/${universitarioId}`,
      {},
    ),

  desabilitar: (universitarioId: UUID) =>
    api.delete<PerfilAnuncianteResponse>(
      `/perfis-anunciante/universitarios/${universitarioId}`,
    ),
};