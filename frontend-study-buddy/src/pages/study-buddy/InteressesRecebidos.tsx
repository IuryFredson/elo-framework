import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { gruposApi, manifestacoesGrupoApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { formatDateTime, statusManifestacaoLabel } from "../../lib/format";
import type {
  GrupoEstudoResponse,
  ManifestacaoInteresseGrupoResponse,
} from "../../study-buddy/types";

export default function InteressesRecebidos() {
  const sessao = useSessaoObrigatoria();
  const { id } = useParams();
  const [grupos, setGrupos] = useState<GrupoEstudoResponse[]>([]);
  const [itens, setItens] = useState<ManifestacaoInteresseGrupoResponse[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(true);

  async function carregar() {
    setCarregando(true);
    setErro(null);
    try {
      const todos = await gruposApi.listar();
      const meusGrupos = todos.filter((grupo) => grupo.publicadorId === sessao.id);
      setGrupos(meusGrupos);

      const alvo = id ? meusGrupos.filter((grupo) => grupo.id === id) : meusGrupos;
      const listas = await Promise.all(
        alvo.map((grupo) => manifestacoesGrupoApi.porGrupo(grupo.id, sessao.id)),
      );
      setItens(listas.flat());
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao carregar interesses recebidos.");
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregar();
  }, [id, sessao.id]);

  const titulo = useMemo(() => {
    if (!id) return "Interesses recebidos";
    const grupo = grupos.find((item) => item.id === id);
    return grupo ? `Interesses de ${grupo.titulo}` : "Interesses recebidos";
  }, [grupos, id]);

  async function responder(acao: "aceitar" | "recusar", manifestacaoId: string) {
    try {
      if (acao === "aceitar") {
        await manifestacoesGrupoApi.aceitar(manifestacaoId, sessao.id);
      } else {
        await manifestacoesGrupoApi.recusar(manifestacaoId, sessao.id);
      }
      carregar();
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao responder interesse.");
    }
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 pb-20 space-y-6">
      <header>
        <h1 className="text-3xl font-bold text-apto-text-main">{titulo}</h1>
        <p className="text-apto-text-muted mt-2">
          Responda às manifestações recebidas nos grupos publicados por você.
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
          Nenhum interesse recebido até agora.
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
                  <p className="text-sm text-apto-text-muted">Interessado: {item.interessadoNome}</p>
                </div>
                <Badge tone={item.status === "ACEITA" ? "success" : item.status === "RECUSADA" ? "danger" : item.status === "CANCELADA" ? "neutral" : "warning"}>
                  {statusManifestacaoLabel[item.status]}
                </Badge>
              </div>
              <p className="text-sm text-apto-text-muted">{item.mensagem || "Sem mensagem adicional."}</p>
              <div className="flex items-center justify-between text-xs text-apto-text-muted flex-wrap gap-3">
                <span>Enviado em {formatDateTime(item.dataManifestacao)}</span>
                {item.status === "PENDENTE" && (
                  <div className="flex gap-2">
                    <Button size="sm" variant="secondary" onClick={() => responder("recusar", item.id)}>
                      Recusar
                    </Button>
                    <Button size="sm" onClick={() => responder("aceitar", item.id)}>
                      Aceitar
                    </Button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
