import { createContext } from "react";
import type { TipoUsuario, UUID } from "../api/types";

export interface Sessao {
  id: UUID;
  tipo: TipoUsuario;
  nome: string;
}

export interface AuthContextValue {
  sessao: Sessao | null;
  carregando: boolean;
  login: (sessao: Sessao) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

export const AUTH_STORAGE_KEY = "apto.sessao";
