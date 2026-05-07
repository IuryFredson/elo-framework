import { Link, useLocation, useNavigate } from "react-router-dom";
import { Compass, Home as HomeIcon, LogOut, Search, User } from "lucide-react";
import { useState } from "react";
import { cn } from "../../lib/utils";
import { Button } from "../ui/Button";
import { Avatar } from "../ui/Avatar";
import { useAuth } from "../../auth/useAuth";

export function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { sessao, logout } = useAuth();
  const [menuAberto, setMenuAberto] = useState(false);

  const itensUniversitario = [
    { label: "Início", href: "/", icon: HomeIcon },
    { label: "Buscar", href: "/buscar", icon: Search },
    { label: "Matchmaking", href: "/matchmaking", icon: Compass },
    { label: "Interesses", href: "/interesses", icon: User },
  ];

  const itensLocador = [
    { label: "Início", href: "/", icon: HomeIcon },
    { label: "Meus Anúncios", href: "/meus-anuncios", icon: HomeIcon },
    {
      label: "Interesses recebidos",
      href: "/interesses-recebidos",
      icon: User,
    },
  ];

  const navItems = sessao
    ? sessao.tipo === "UNIVERSITARIO"
      ? itensUniversitario
      : itensLocador
    : [];

  const ctaHref =
    sessao?.tipo === "LOCADOR" ? "/anuncios/novo" : "/anuncios/novo";
  const ctaLabel = sessao?.tipo === "LOCADOR" ? "Publicar vaga" : "Publicar";

  return (
    <nav className="fixed top-0 w-full h-16 bg-white border-b border-apto-border px-6 md:px-8 flex items-center justify-between z-50">
      <Link to="/" className="flex items-center gap-2">
        <img src="/logo.svg" alt="Apto" className="h-7 w-auto" />
        <span className="text-2xl font-extrabold tracking-tighter text-apto-primary">
          APTO
        </span>
      </Link>

      <div className="flex items-center gap-6">
        {sessao && (
          <div className="hidden md:flex items-center gap-6">
            {navItems.map((item) => {
              const isActive = location.pathname === item.href;
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
              className="hidden sm:flex px-5 bg-apto-primary rounded-[10px]"
              onClick={() => navigate(ctaHref)}
            >
              {ctaLabel}
            </Button>

            <div className="relative">
              <button
                type="button"
                onClick={() => setMenuAberto((v) => !v)}
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
                      <p className="text-xs text-apto-text-muted">
                        {sessao.tipo === "UNIVERSITARIO"
                          ? "Universitário"
                          : "Locador"}
                      </p>
                    </div>
                    <Link
                      to="/profile"
                      className="block px-4 py-2.5 text-sm hover:bg-apto-bg"
                      onClick={() => setMenuAberto(false)}
                    >
                      Meu perfil
                    </Link>
                    <Link
                      to="/moderacao"
                      className="block px-4 py-2.5 text-sm hover:bg-apto-bg"
                      onClick={() => setMenuAberto(false)}
                    >
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
