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

export const anunciosApi = {
  listar: () => api.get<AnuncioResponse[]>("/anuncios"),
  obter: (id: UUID) => api.get<AnuncioResponse>(`/anuncios/${id}`),
  criar: (body: CriarAnuncioRequest) =>
    api.post<AnuncioResponse>("/anuncios", body),
  atualizar: (id: UUID, anuncianteId: UUID, body: AtualizarAnuncioRequest) =>
    api.put<AnuncioResponse>(`/anuncios/${id}`, body, { anuncianteId }),
  alterarStatus: (id: UUID, status: StatusAnuncio) =>
    api.patch<AnuncioResponse>(`/anuncios/${id}/status`, status),
  excluir: (id: UUID) => api.delete(`/anuncios/${id}`),
  buscar: (filtro: FiltroBuscaAnuncio) =>
    api.get<PaginaResponse<BuscaAnuncioResponse>>("/anuncios/busca", {
      ...filtro,
    }),
};
