import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { manifestacoesApi } from "../api/manifestacoes";
import { useAuth } from "../auth/useAuth";
import { useToast } from "../components/ui/useToast";
import { ApiError } from "../api/client";
import { formatDateTime, statusManifestacaoLabel } from "../lib/format";
import type {
  ManifestacaoInteresseResponse,
  StatusManifestacaoInteresse,
} from "../api/types";

export default function Interesses() {
  const { sessao } = useAuth();
  const { show } = useToast();
  const [itens, setItens] = useState<ManifestacaoInteresseResponse[]>([]);
  const [carregando, setCarregando] = useState(true);

  const recarregar = useCallback(() => {
    if (!sessao) return;
    setCarregando(true);
    manifestacoesApi
      .porUsuario(sessao.id)
      .then(setItens)
      .finally(() => setCarregando(false));
  }, [sessao]);

  useEffect(() => {
    recarregar();
  }, [recarregar]);

  async function cancelar(item: ManifestacaoInteresseResponse) {
    if (!sessao) return;
    if (!confirm(`Cancelar interesse no anúncio "${item.anuncioTitulo}"?`))
      return;
    try {
      await manifestacoesApi.cancelar(item.id, sessao.id);
      show("Interesse cancelado");
      recarregar();
    } catch (e: unknown) {
      show(e instanceof ApiError ? e.message : "Erro ao cancelar", "error");
    }
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 pb-20">
      <h1 className="text-2xl font-bold text-apto-text-main mb-6">
        Meus interesses enviados
      </h1>

      {carregando ? (
        <div className="text-apto-text-muted">Carregando...</div>
      ) : itens.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Você ainda não manifestou interesse em nenhum anúncio.
        </div>
      ) : (
        <div className="space-y-3">
          {itens.map((m) => (
            <div
              key={m.id}
              className="bg-white rounded-apto-section border border-apto-border p-5 flex flex-col sm:flex-row sm:items-center gap-4"
            >
              <div className="flex-1 min-w-0">
                <Badge tone={statusTone(m.status)}>
                  {statusManifestacaoLabel[m.status]}
                </Badge>
                <Link
                  to={`/anuncios/${m.anuncioId}`}
                  className="block font-bold text-apto-text-main hover:text-apto-primary mt-1 truncate"
                >
                  {m.anuncioTitulo}
                </Link>
                <p className="text-xs text-apto-text-muted mt-1">
                  Enviado em {formatDateTime(m.dataManifestacao)}
                  {m.dataResposta &&
                    ` · Respondido em ${formatDateTime(m.dataResposta)}`}
                </p>
                {m.mensagem && (
                  <p className="text-sm text-apto-text-muted italic mt-2 border-l-2 border-apto-border pl-3">
                    "{m.mensagem}"
                  </p>
                )}
              </div>
              {m.status === "PENDENTE" && (
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => cancelar(m)}
                >
                  Cancelar
                </Button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function statusTone(s: StatusManifestacaoInteresse) {
  if (s === "ACEITA") return "success" as const;
  if (s === "RECUSADA" || s === "CANCELADA") return "neutral" as const;
  return "warning" as const;
}
