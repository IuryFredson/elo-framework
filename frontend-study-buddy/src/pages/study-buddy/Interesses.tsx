import { useEffect, useState } from "react";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { manifestacoesGrupoApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { formatDateTime, statusManifestacaoLabel } from "../../lib/format";
import type { ManifestacaoInteresseGrupoResponse } from "../../study-buddy/types";

export default function Interesses() {
  const sessao = useSessaoObrigatoria();
  const [itens, setItens] = useState<ManifestacaoInteresseGrupoResponse[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(true);

  function carregar() {
    setCarregando(true);
    manifestacoesGrupoApi
      .porEstudante(sessao.id)
      .then(setItens)
      .catch((error: unknown) => {
        setErro(error instanceof ApiError ? error.message : "Erro ao carregar interesses.");
      })
      .finally(() => setCarregando(false));
  }

  useEffect(() => {
    carregar();
  }, [sessao.id]);

  async function cancelar(id: string) {
    try {
      await manifestacoesGrupoApi.cancelar(id, sessao.id);
      carregar();
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao cancelar interesse.");
    }
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 pb-20 space-y-6">
      <header>
        <h1 className="text-3xl font-bold text-apto-text-main">Meus interesses</h1>
        <p className="text-apto-text-muted mt-2">
          Acompanhe as manifestações enviadas para grupos de estudo.
        </p>
      </header>

      {erro && (
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      )}

      {carregando ? (
        <div className="text-apto-text-muted">Carregando...</div>
      ) : itens.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Você ainda não manifestou interesse em grupos.
        </div>
      ) : (
        <div className="space-y-4">
          {itens.map((item) => (
            <div
              key={item.id}
              className="bg-white rounded-apto-section border border-apto-border p-5 space-y-3"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h2 className="font-bold text-apto-text-main">{item.grupoTitulo}</h2>
                  <p className="text-sm text-apto-text-muted">Publicador: {item.publicadorNome}</p>
                </div>
                <Badge tone={item.status === "ACEITA" ? "success" : item.status === "RECUSADA" ? "danger" : item.status === "CANCELADA" ? "neutral" : "warning"}>
                  {statusManifestacaoLabel[item.status]}
                </Badge>
              </div>
              <p className="text-sm text-apto-text-muted">{item.mensagem || "Sem mensagem adicional."}</p>
              <div className="flex items-center justify-between text-xs text-apto-text-muted flex-wrap gap-2">
                <span>Enviado em {formatDateTime(item.dataManifestacao)}</span>
                {item.status === "PENDENTE" && (
                  <Button variant="secondary" size="sm" onClick={() => cancelar(item.id)}>
                    Cancelar manifestação
                  </Button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
