import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  BookOpen,
  Compass,
  Home as HomeIcon,
  LogOut,
  ShieldAlert,
  User,
  Users,
} from "lucide-react";
import { useState } from "react";
import { cn } from "../../lib/utils";
import { Button } from "../ui/Button";
import { Avatar } from "../ui/Avatar";
import { useAuth } from "../../auth/useAuth";

const NAV_ITEMS = [
  { label: "Início", href: "/", icon: HomeIcon },
  { label: "Grupos", href: "/grupos", icon: BookOpen },
  { label: "Matchmaking", href: "/matchmaking", icon: Compass },
  { label: "Meus interesses", href: "/interesses", icon: User },
  { label: "Recebidos", href: "/interesses-recebidos", icon: Users },
];

export function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { sessao, logout } = useAuth();
  const [menuAberto, setMenuAberto] = useState(false);

  return (
    <nav className="fixed top-0 w-full h-16 bg-white border-b border-apto-border px-6 md:px-8 flex items-center justify-between z-50">
      <Link to="/" className="flex items-center gap-3">
        <div className="h-9 w-9 rounded-2xl bg-apto-primary-light text-apto-primary flex items-center justify-center">
          <Users size={18} />
        </div>
        <div>
          <p className="text-lg font-extrabold tracking-tight text-apto-primary">
            Study Buddy
          </p>
          <p className="text-[11px] text-apto-text-muted leading-none">
            Instância do Elo Framework
          </p>
        </div>
      </Link>

      <div className="flex items-center gap-6">
        {sessao && (
          <div className="hidden md:flex items-center gap-6">
            {NAV_ITEMS.map((item) => {
              const isActive =
                location.pathname === item.href ||
                (item.href !== "/" && location.pathname.startsWith(item.href));
              return (
                <Link
                  key={item.href}
                  to={item.href}
                  className={cn(
                    "text-sm font-medium transition-colors",
                    isActive
                      ? "text-apto-primary"
                      : "text-apto-text-muted hover:text-apto-primary",
                  )}
                >
                  {item.label}
                </Link>
              );
            })}
          </div>
        )}

        {sessao ? (
          <>
            <Button
              size="md"
              className="hidden sm:flex px-5 rounded-[10px]"
              onClick={() => navigate("/grupos/novo")}
            >
              Publicar grupo
            </Button>

            <div className="relative">
              <button
                type="button"
                onClick={() => setMenuAberto((valor) => !valor)}
                className="flex items-center gap-2 hover:opacity-90"
                aria-label="Menu do usuário"
              >
                <Avatar nome={sessao.nome} size="sm" />
                <span className="hidden sm:block text-sm font-medium text-apto-text-main max-w-[140px] truncate">
                  {sessao.nome}
                </span>
              </button>

              {menuAberto && (
                <>
                  <button
                    type="button"
                    className="fixed inset-0 z-40"
                    onClick={() => setMenuAberto(false)}
                    aria-hidden
                  />
                  <div className="absolute right-0 top-full mt-2 w-56 bg-white border border-apto-border rounded-apto-section shadow-lg overflow-hidden z-50">
                    <div className="px-4 py-3 border-b border-apto-border">
                      <p className="text-sm font-bold text-apto-text-main truncate">
                        {sessao.nome}
                      </p>
                      <p className="text-xs text-apto-text-muted">Estudante</p>
                    </div>
                    <Link
                      to="/perfil"
                      className="block px-4 py-2.5 text-sm hover:bg-apto-bg"
                      onClick={() => setMenuAberto(false)}
                    >
                      Meu perfil
                    </Link>
                    <Link
                      to="/moderacao"
                      className="flex items-center gap-2 px-4 py-2.5 text-sm hover:bg-apto-bg"
                      onClick={() => setMenuAberto(false)}
                    >
                      <ShieldAlert size={14} />
                      Moderação
                    </Link>
                    <button
                      type="button"
                      onClick={() => {
                        setMenuAberto(false);
                        logout();
                        navigate("/login");
                      }}
                      className="w-full flex items-center gap-2 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 border-t border-apto-border"
                    >
                      <LogOut size={14} />
                      Sair
                    </button>
                  </div>
                </>
              )}
            </div>
          </>
        ) : (
          <Button
            size="md"
            className="px-5 rounded-[10px]"
            onClick={() => navigate("/login")}
          >
            Entrar
          </Button>
        )}
      </div>
    </nav>
  );
}
