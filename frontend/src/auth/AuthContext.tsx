import { useCallback, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { AUTH_STORAGE_KEY, AuthContext, type Sessao } from "./authStore";
import type { UUID } from "../api/types";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [sessao, setSessao] = useState<Sessao | null>(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    try {
      const raw = localStorage.getItem(AUTH_STORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw) as Sessao;
        if (parsed.id && parsed.tipo && parsed.nome) {
          setSessao(parsed);
        }
      }
    } catch {
      localStorage.removeItem(AUTH_STORAGE_KEY);
    }
    setCarregando(false);
  }, []);

  const login = useCallback((s: Sessao) => {
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(s));
    setSessao(s);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    setSessao(null);
  }, []);

  const atualizarPerfilAnunciante = useCallback(
    (perfilAnuncianteId: UUID | null) => {
      setSessao((prev) => {
        if (!prev) return prev;
        const atualizada = { ...prev, perfilAnuncianteId };
        localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(atualizada));
        return atualizada;
      });
    },
    [],
  );

  const value = useMemo(
    () => ({ sessao, carregando, login, logout, atualizarPerfilAnunciante }),
    [sessao, carregando, login, logout, atualizarPerfilAnunciante],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}