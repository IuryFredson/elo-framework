import { api } from "./client";
import type {
  CriarDenunciaRequest,
  DenunciaResponse,
  StatusDenuncia,
  UUID,
} from "./types";

export const denunciasApi = {
  listar: () => api.get<DenunciaResponse[]>("/denuncias"),
  obter: (id: UUID) => api.get<DenunciaResponse>(`/denuncias/${id}`),
  criar: (body: CriarDenunciaRequest) =>
    api.post<DenunciaResponse>("/denuncias", body),
  porAnuncio: (anuncioId: UUID) =>
    api.get<DenunciaResponse[]>(`/denuncias/por-anuncio`, { anuncioId }),
  porUsuario: (usuarioId: UUID) =>
    api.get<DenunciaResponse[]>(`/denuncias/por-usuario`, { usuarioId }),
  porStatus: (status: StatusDenuncia) =>
    api.get<DenunciaResponse[]>(`/denuncias/por-status`, { status }),
};
