import { useCallback, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import {
  AUTH_STORAGE_KEY,
  AuthContext,
  type Sessao,
} from "./authStore";

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

  const value = useMemo(
    () => ({ sessao, carregando, login, logout }),
    [sessao, carregando, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
