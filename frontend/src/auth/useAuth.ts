import { useContext } from "react";
import { AuthContext, type Sessao } from "./authStore";

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth precisa de AuthProvider");
  return ctx;
}

export function useSessaoObrigatoria(): Sessao {
  const { sessao } = useAuth();
  if (!sessao) {
    throw new Error("Sessão obrigatória — acessou rota protegida sem login");
  }
  return sessao;
}
