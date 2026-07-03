import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "../../auth/useAuth";
import type { TipoUsuario } from "../../api/types";

interface ProtectedRouteProps {
  children: ReactNode;
  permitirTipos?: TipoUsuario[];
}

export function ProtectedRoute({
  children,
  permitirTipos,
}: ProtectedRouteProps) {
  const { sessao, carregando } = useAuth();

  if (carregando) {
    return (
      <div className="flex items-center justify-center min-h-[60vh] text-apto-text-muted">
        Carregando...
      </div>
    );
  }

  if (!sessao) {
    return <Navigate to="/login" replace />;
  }

  if (permitirTipos && !permitirTipos.includes(sessao.tipo)) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
