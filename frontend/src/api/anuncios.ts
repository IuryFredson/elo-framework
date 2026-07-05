import { api } from "./client";
import type {
  AnuncioResponse,
  AtualizarAnuncioRequest,
  BuscaAnuncioResponse,
  CriarAnuncioRequest,
  FiltroBuscaAnuncio,
  PaginaResponse,
  StatusAnuncio,
  UUID,
} from "./types";

const OFERTAS_PATH = "/ofertas";

export const anunciosApi = {
  listar: () => api.get<AnuncioResponse[]>(OFERTAS_PATH),
  obter: (id: UUID) => api.get<AnuncioResponse>(`${OFERTAS_PATH}/${id}`),
  criar: (body: CriarAnuncioRequest) =>
    api.post<AnuncioResponse>(OFERTAS_PATH, body),
  atualizar: (id: UUID, anuncianteId: UUID, body: AtualizarAnuncioRequest) =>
    api.put<AnuncioResponse>(`${OFERTAS_PATH}/${id}`, body, { publicadorId: anuncianteId }),
  alterarStatus: (id: UUID, status: StatusAnuncio) =>
    api.patch<AnuncioResponse>(`${OFERTAS_PATH}/${id}/status`, status),
  excluir: (id: UUID) => api.delete(`${OFERTAS_PATH}/${id}`),
  buscar: (filtro: FiltroBuscaAnuncio) =>
    api.get<PaginaResponse<BuscaAnuncioResponse>>(`${OFERTAS_PATH}/busca`, {
      ...filtro,
    }),
};
