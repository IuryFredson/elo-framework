import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Flag, ShieldAlert, Users } from "lucide-react";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { Textarea } from "../../components/ui/Input";
import { ReportarGrupoModal } from "../../components/study-buddy/ReportarGrupoModal";
import { gruposApi, manifestacoesGrupoApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { formatDate, statusManifestacaoLabel } from "../../lib/format";
import {
  modalidadeEstudoLabel,
  periodoDisponibilidadeLabel,
  statusGrupoEstudoLabel,
} from "../../lib/studyBuddyFormat";
import type { GrupoEstudoResponse, ManifestacaoInteresseGrupoResponse } from "../../study-buddy/types";

export default function GrupoDetalhe() {
  const { id } = useParams();
  const sessao = useSessaoObrigatoria();
  const [grupo, setGrupo] = useState<GrupoEstudoResponse | null>(null);
  const [manifestacoes, setManifestacoes] = useState<ManifestacaoInteresseGrupoResponse[]>([]);
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const [reportarAberto, setReportarAberto] = useState(false);

  useEffect(() => {
    if (!id) return;
    gruposApi
      .obter(id)
      .then((dadosGrupo) => {
        setGrupo(dadosGrupo);
        if (dadosGrupo.publicadorId === sessao.id) {
          return manifestacoesGrupoApi.porGrupo(dadosGrupo.id, sessao.id);
        }
        return [];
      })
      .then(setManifestacoes)
      .catch((error: unknown) => {
        setErro(error instanceof ApiError ? error.message : "Erro ao carregar grupo.");
      });
  }, [id, sessao.id]);

  const minhaManifestacao = useMemo(
    () => manifestacoes.find((item) => item.interessadoId === sessao.id),
    [manifestacoes, sessao.id],
  );

  if (erro) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12">
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      </div>
    );
  }

  if (!grupo) {
    return <div className="max-w-4xl mx-auto px-4 py-12 text-apto-text-muted">Carregando grupo...</div>;
  }

  const souPublicador = grupo.publicadorId === sessao.id;

  async function manifestarInteresse() {
    if (!grupo) return;
    setErro(null);
    setEnviando(true);
    try {
      await manifestacoesGrupoApi.criar({
        grupoId: grupo.id,
        interessadoId: sessao.id,
        mensagem,
      });
      const lista = await manifestacoesGrupoApi.porEstudante(sessao.id);
      setManifestacoes(lista.filter((item) => item.grupoId === grupo.id));
      setMensagem("");
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao manifestar interesse.");
    } finally {
      setEnviando(false);
    }
  }

  async function atualizarStatus(novoStatus: GrupoEstudoResponse["status"]) {
    if (!grupo) return;
    try {
      const atualizado = await gruposApi.atualizarStatus(grupo.id, novoStatus);
      setGrupo(atualizado);
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao atualizar status do grupo.");
    }
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 pb-20 space-y-6">
      <div className="bg-white rounded-apto-section border border-apto-border p-6 space-y-5">
        <div className="flex items-start justify-between gap-4 flex-wrap">
          <div>
            <h1 className="text-3xl font-bold text-apto-text-main">{grupo.titulo}</h1>
            <p className="text-apto-text-muted mt-1">
              {grupo.disciplina} · publicado por {grupo.publicadorNome}
            </p>
          </div>
          <Badge tone={grupo.status === "ATIVO" ? "success" : "warning"}>
            {statusGrupoEstudoLabel[grupo.status]}
          </Badge>
        </div>

        <p className="text-apto-text-main leading-relaxed">{grupo.descricao}</p>

        <div className="flex flex-wrap gap-2">
          <Badge tone="info">{modalidadeEstudoLabel[grupo.modalidade]}</Badge>
          <Badge tone="neutral">{periodoDisponibilidadeLabel[grupo.periodo]}</Badge>
          <Badge tone="primary">{grupo.quantidadeVagas} vagas</Badge>
        </div>

        <div className="flex items-center justify-between text-sm text-apto-text-muted flex-wrap gap-2">
          <span>Publicado em {formatDate(grupo.dataPublicacao)}</span>
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="sm" onClick={() => setReportarAberto(true)}>
              <Flag size={14} />
            </Button>
            {souPublicador && (
              <Link to={`/grupos/${grupo.id}/interesses`}>
                <Button variant="secondary" size="sm" className="flex items-center gap-2">
                  <Users size={14} />
                  Ver interesses
                </Button>
              </Link>
            )}
          </div>
        </div>
      </div>

      {souPublicador ? (
        <section className="bg-white rounded-apto-section border border-apto-border p-6 space-y-4">
          <div className="flex items-center gap-2 text-apto-primary">
            <ShieldAlert size={16} />
            <h2 className="font-semibold text-apto-text-main">Gestão do grupo</h2>
          </div>
          <p className="text-sm text-apto-text-muted">
            Esta área acompanha o padrão do Apto: o publicador controla o status da oferta
            e acompanha as manifestações recebidas.
          </p>
          <div className="flex flex-wrap gap-2">
            <Button variant="secondary" size="sm" onClick={() => atualizarStatus("ATIVO")}>
              Ativar
            </Button>
            <Button variant="secondary" size="sm" onClick={() => atualizarStatus("PAUSADO")}>
              Pausar
            </Button>
            <Button variant="secondary" size="sm" onClick={() => atualizarStatus("ENCERRADO")}>
              Encerrar
            </Button>
          </div>
          <p className="text-sm text-apto-text-muted">
            Interesses carregados nesta tela: {manifestacoes.length}.
          </p>
        </section>
      ) : (
        <section className="bg-white rounded-apto-section border border-apto-border p-6 space-y-4">
          <h2 className="text-xl font-bold text-apto-text-main">Manifestar interesse</h2>
          <p className="text-sm text-apto-text-muted">
            A manifestação de interesse é fixa no framework e aqui representa o interesse em participar deste grupo.
          </p>
          {minhaManifestacao ? (
            <div className="rounded-apto-ui border border-apto-border bg-apto-bg p-4 space-y-2">
              <div className="flex items-center justify-between gap-3">
                <p className="font-medium text-apto-text-main">Interesse já registrado</p>
                <Badge tone={minhaManifestacao.status === "ACEITA" ? "success" : minhaManifestacao.status === "RECUSADA" ? "danger" : "warning"}>
                  {statusManifestacaoLabel[minhaManifestacao.status]}
                </Badge>
              </div>
              <p className="text-sm text-apto-text-muted">{minhaManifestacao.mensagem || "Sem mensagem adicional."}</p>
            </div>
          ) : (
            <>
              <Textarea
                label="Mensagem opcional"
                value={mensagem}
                onChange={(e) => setMensagem(e.target.value)}
                placeholder="Conte seu objetivo, disponibilidade ou nível na disciplina."
                maxLength={500}
                hint={`${mensagem.length}/500`}
              />
              <Button onClick={manifestarInteresse} disabled={enviando || grupo.status !== "ATIVO"}>
                {enviando ? "Enviando..." : "Manifestar interesse"}
              </Button>
              {grupo.status !== "ATIVO" && (
                <p className="text-sm text-amber-700">Este grupo não está ativo para novas manifestações.</p>
              )}
            </>
          )}
        </section>
      )}

      <ReportarGrupoModal
        open={reportarAberto}
        onClose={() => setReportarAberto(false)}
        grupoTitulo={grupo.titulo}
      />
    </div>
  );
}
