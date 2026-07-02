import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, Users } from "lucide-react";
import { Avatar } from "../../components/ui/Avatar";
import { Button } from "../../components/ui/Button";
import { estudantesApi } from "../../api/studyBuddy";
import { useAuth } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import type { EstudanteResponse } from "../../study-buddy/types";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [estudantes, setEstudantes] = useState<EstudanteResponse[] | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    estudantesApi
      .listar()
      .then(setEstudantes)
      .catch((error: unknown) => {
        const message =
          error instanceof ApiError
            ? `${error.status}: ${error.message}`
            : "Não foi possível carregar estudantes do backend.";
        setErro(message);
      });
  }, []);

  function entrarComo(estudante: EstudanteResponse) {
    login({ id: estudante.id, tipo: "ESTUDANTE", nome: estudante.nome });
    navigate("/", { replace: true });
  }

  return (
    <div className="max-w-2xl mx-auto px-4 py-12">
      <div className="text-center mb-8">
        <div className="h-16 w-16 rounded-[20px] bg-apto-primary-light text-apto-primary flex items-center justify-center mx-auto mb-4">
          <Users size={28} />
        </div>
        <h1 className="text-3xl font-bold text-apto-text-main">Entrar no Study Buddy</h1>
        <p className="text-apto-text-muted mt-2">
          Selecione um estudante existente para simular login. Esta instância ainda
          não usa autenticação real.
        </p>
      </div>

      {erro && (
        <div className="mb-4 p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      )}

      <div className="bg-white rounded-apto-section border border-apto-border divide-y divide-apto-border">
        {estudantes === null ? (
          <div className="p-6 text-center text-apto-text-muted">Carregando...</div>
        ) : estudantes.length === 0 ? (
          <div className="p-6 text-center text-apto-text-muted">
            Nenhum estudante cadastrado.
          </div>
        ) : (
          estudantes.map((estudante) => (
            <button
              key={estudante.id}
              type="button"
              onClick={() => entrarComo(estudante)}
              className="w-full flex items-center gap-4 px-5 py-4 hover:bg-apto-bg/60 text-left transition-colors"
            >
              <Avatar nome={estudante.nome} size="md" />
              <div className="flex-1 min-w-0">
                <p className="font-bold text-apto-text-main truncate">{estudante.nome}</p>
                <p className="text-sm text-apto-text-muted truncate">
                  {estudante.email} · {estudante.instituicao}
                </p>
              </div>
              <ArrowRight size={18} className="text-apto-text-muted flex-shrink-0" />
            </button>
          ))
        )}
      </div>

      <div className="mt-8 text-center">
        <p className="text-sm text-apto-text-muted">
          Ainda não tem cadastro?{" "}
          <Link to="/cadastro" className="text-apto-primary font-bold hover:underline">
            Criar estudante
          </Link>
        </p>
      </div>

      <div className="mt-4 text-center">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => {
            setErro(null);
            setEstudantes(null);
            estudantesApi.listar().then(setEstudantes).catch(() => {
              setErro("Falha ao recarregar estudantes.");
            });
          }}
        >
          Recarregar lista
        </Button>
      </div>
    </div>
  );
}
