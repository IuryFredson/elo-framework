import { api } from "./client";
import type {
  CriarMoradiaRequest,
  MoradiaResponse,
  UUID,
} from "./types";

export const moradiasApi = {
  listar: () => api.get<MoradiaResponse[]>("/moradias"),
  obter: (id: UUID) => api.get<MoradiaResponse>(`/moradias/${id}`),
  criar: (body: CriarMoradiaRequest) =>
    api.post<MoradiaResponse>("/moradias", body),
  atualizar: (id: UUID, body: CriarMoradiaRequest) =>
    api.put<MoradiaResponse>(`/moradias/${id}`, body),
  excluir: (id: UUID) => api.delete(`/moradias/${id}`),
};
