import { api } from "./client";
import type {
  AtualizarPerfilRequest,
  CriarLocadorRequest,
  CriarUsuarioUniversitarioRequest,
  LocadorResponse,
  PerfilResponse,
  UsuarioUniversitarioResponse,
  UUID,
} from "./types";

export const universitariosApi = {
  listar: () =>
    api.get<UsuarioUniversitarioResponse[]>("/usuarios/universitarios"),
  obter: (id: UUID) =>
    api.get<UsuarioUniversitarioResponse>(`/usuarios/universitarios/${id}`),
  criar: (body: CriarUsuarioUniversitarioRequest) =>
    api.post<UsuarioUniversitarioResponse>("/usuarios/universitarios", body),
};

export const locadoresApi = {
  listar: () => api.get<LocadorResponse[]>("/usuarios/locadores"),
  obter: (id: UUID) => api.get<LocadorResponse>(`/usuarios/locadores/${id}`),
  criar: (body: CriarLocadorRequest) =>
    api.post<LocadorResponse>("/usuarios/locadores", body),
};

export const perfilApi = {
  obter: (idUsuario: UUID) =>
    api.get<PerfilResponse>(`/usuarios/${idUsuario}/perfil`),
  atualizar: (idUsuario: UUID, body: AtualizarPerfilRequest) =>
    api.put<PerfilResponse>(`/usuarios/${idUsuario}/perfil`, body),
};
