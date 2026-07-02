import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Building2, GraduationCap, ArrowRight } from "lucide-react";
import { Avatar } from "../components/ui/Avatar";
import { Button } from "../components/ui/Button";
import { locadoresApi, universitariosApi } from "../api/usuarios";
import { useAuth } from "../auth/useAuth";
import { ApiError } from "../api/client";
import type {
  LocadorResponse,
  UsuarioUniversitarioResponse,
} from "../api/types";

type Tab = "UNIVERSITARIO" | "LOCADOR";

  export default function Login() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [tab, setTab] = useState<Tab>("UNIVERSITARIO");
    const [universitarios, setUniversitarios] = useState<
      UsuarioUniversitarioResponse[] | null
    >(null);
    const [locadores, setLocadores] = useState<LocadorResponse[] | null>(null);
    const [erro, setErro] = useState<string | null>(null);

    useEffect(() => {
      setErro(null);
      Promise.all([universitariosApi.listar(), locadoresApi.listar()])
        .then(([univs, locs]) => {
          setUniversitarios(univs);
          setLocadores(locs);
        })
        .catch((e: unknown) => {
          const msg =
            e instanceof ApiError
              ? `${e.status}: ${e.message}`
              : "Não foi possível carregar usuários do backend.";
          setErro(msg);
        });
    }, []);

  function entrarComo(
    tipo: Tab,
    user: UsuarioUniversitarioResponse | LocadorResponse,
  ) {
    login({ id: user.id, tipo, nome: user.nome });
    navigate("/", { replace: true });
  }

  const lista = tab === "UNIVERSITARIO" ? universitarios : locadores;

  return (
    <div className="max-w-2xl mx-auto px-4 py-12">
      <div className="text-center mb-8">
        <img src="/logo.svg" alt="Apto" className="h-16 w-auto mx-auto mb-4" />
        <h1 className="text-3xl font-bold text-apto-text-main">
          Entrar no Apto
        </h1>
        <p className="text-apto-text-muted mt-2">
          Selecione um usuário existente para simular login. O backend ainda não
          tem autenticação real.
        </p>
      </div>

      <div className="bg-white rounded-apto-section border border-apto-border p-1 flex gap-1 mb-6">
        <button
          type="button"
          onClick={() => setTab("UNIVERSITARIO")}
          className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-apto-ui font-medium text-sm transition-colors ${
            tab === "UNIVERSITARIO"
              ? "bg-apto-primary text-white"
              : "text-apto-text-muted hover:text-apto-text-main"
          }`}
        >
          <GraduationCap size={16} />
          Universitário
        </button>
        <button
          type="button"
          onClick={() => setTab("LOCADOR")}
          className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-apto-ui font-medium text-sm transition-colors ${
            tab === "LOCADOR"
              ? "bg-apto-primary text-white"
              : "text-apto-text-muted hover:text-apto-text-main"
          }`}
        >
          <Building2 size={16} />
          Locador
        </button>
      </div>

      {erro && (
        <div className="mb-4 p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      )}

      <div className="bg-white rounded-apto-section border border-apto-border divide-y divide-apto-border">
        {lista === null ? (
          <div className="p-6 text-center text-apto-text-muted">
            Carregando...
          </div>
        ) : lista.length === 0 ? (
          <div className="p-6 text-center text-apto-text-muted">
            Nenhum {tab === "UNIVERSITARIO" ? "universitário" : "locador"}{" "}
            cadastrado.
          </div>
        ) : (
          lista.map((u) => (
            <button
              key={u.id}
              type="button"
              onClick={() => entrarComo(tab, u)}
              className="w-full flex items-center gap-4 px-5 py-4 hover:bg-apto-bg/60 text-left transition-colors"
            >
              <Avatar nome={u.nome} size="md" />
              <div className="flex-1 min-w-0">
                <p className="font-bold text-apto-text-main truncate">
                  {u.nome}
                </p>
                <p className="text-sm text-apto-text-muted truncate">
                  {u.email}
                </p>
              </div>
              <ArrowRight
                size={18}
                className="text-apto-text-muted flex-shrink-0"
              />
            </button>
          ))
        )}
      </div>

      <div className="mt-8 text-center">
        <p className="text-sm text-apto-text-muted">
          Ainda não tem cadastro?{" "}
          <Link
            to="/cadastro"
            className="text-apto-primary font-bold hover:underline"
          >
            Criar usuário
          </Link>
        </p>
      </div>

      <div className="mt-4 text-center">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => {
            setUniversitarios(null);
            setLocadores(null);
            setErro(null);
            Promise.all([
              universitariosApi.listar(),
              locadoresApi.listar(),
            ]).then(([u, l]) => {
              setUniversitarios(u);
              setLocadores(l);
            });
          }}
        >
          Recarregar lista
        </Button>
      </div>
    </div>
  );
}
