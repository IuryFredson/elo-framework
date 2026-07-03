import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Pause, Play, Trash2 } from "lucide-react";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { anunciosApi } from "../api/anuncios";
import { useAuth } from "../auth/useAuth";
import { useToast } from "../components/ui/useToast";
import { ApiError } from "../api/client";
import { formatBRL, formatDate, statusAnuncioLabel } from "../lib/format";
import type { AnuncioResponse, StatusAnuncio } from "../api/types";

export default function MeusAnuncios() {
  const { sessao } = useAuth();
  const { show } = useToast();
  const [anuncios, setAnuncios] = useState<AnuncioResponse[]>([]);
  const [carregando, setCarregando] = useState(true);

  const recarregar = useCallback(() => {
    if (!sessao) return;
    setCarregando(true);
    anunciosApi
      .listar()
      .then((todos) => setAnuncios(todos.filter((a) => a.anuncianteId === sessao.id)))
      .finally(() => setCarregando(false));
  }, [sessao]);

  useEffect(() => {
    recarregar();
  }, [recarregar]);

  if (!sessao) return null;

  async function alterarStatus(a: AnuncioResponse, novo: StatusAnuncio) {
    try {
      await anunciosApi.alterarStatus(a.id, novo);
      show(`Anúncio ${statusAnuncioLabel[novo].toLowerCase()}`);
      recarregar();
    } catch (e: unknown) {
      show(
        e instanceof ApiError ? e.message : "Erro ao alterar status",
        "error",
      );
    }
  }

  async function excluir(a: AnuncioResponse) {
    if (!confirm(`Excluir o anúncio "${a.titulo}"?`)) return;
    try {
      await anunciosApi.excluir(a.id);
      show("Anúncio excluído");
      recarregar();
    } catch (e: unknown) {
      show(e instanceof ApiError ? e.message : "Erro ao excluir", "error");
    }
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 pb-20">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-apto-text-main">Meus anúncios</h1>
        <Link to="/anuncios/novo">
          <Button>Novo anúncio</Button>
        </Link>
      </div>

      {carregando ? (
        <div className="text-apto-text-muted">Carregando...</div>
      ) : anuncios.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Você ainda não publicou nenhum anúncio.
        </div>
      ) : (
        <div className="space-y-4">
          {anuncios.map((a) => (
            <div
              key={a.id}
              className="bg-white rounded-apto-section border border-apto-border p-5 flex flex-col sm:flex-row sm:items-center gap-4"
            >
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <Badge tone={statusTone(a.status)}>
                    {statusAnuncioLabel[a.status]}
                  </Badge>
                  <span className="text-xs text-apto-text-muted">
                    Publicado em {formatDate(a.dataPublicacao)}
                  </span>
                </div>
                <Link
                  to={`/anuncios/${a.id}`}
                  className="font-bold text-apto-text-main hover:text-apto-primary block truncate"
                >
                  {a.titulo}
                </Link>
                <p className="text-sm text-apto-text-muted">
                  {formatBRL(a.valorMensal)}/mês
                </p>
              </div>

              <div className="flex flex-wrap gap-2">
                <Link to={`/anuncios/${a.id}/interesses`}>
                  <Button variant="secondary" size="sm">
                    Ver interesses
                  </Button>
                </Link>
                {a.status === "ATIVO" ? (
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => alterarStatus(a, "PAUSADO")}
                    className="flex items-center gap-1"
                  >
                    <Pause size={14} />
                    Pausar
                  </Button>
                ) : a.status === "PAUSADO" ? (
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => alterarStatus(a, "ATIVO")}
                    className="flex items-center gap-1"
                  >
                    <Play size={14} />
                    Ativar
                  </Button>
                ) : null}
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => excluir(a)}
                  className="flex items-center gap-1 text-red-600 hover:bg-red-50"
                >
                  <Trash2 size={14} />
                  Excluir
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function statusTone(s: StatusAnuncio) {
  if (s === "ATIVO") return "success" as const;
  if (s === "PAUSADO") return "warning" as const;
  return "neutral" as const;
}
