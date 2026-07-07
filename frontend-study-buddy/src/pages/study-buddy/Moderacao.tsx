import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ShieldAlert } from "lucide-react";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { Modal } from "../../components/ui/Modal";
import { Select } from "../../components/ui/Select";
import { Textarea } from "../../components/ui/Input";
import { denunciasGrupoApi, moderacaoGrupoApi } from "../../api/studyBuddy";
import { ApiError } from "../../api/client";
import { useToast } from "../../components/ui/useToast";
import { enumOptions, formatDateTime, statusDenunciaLabel } from "../../lib/format";
import {
  acaoModeracaoGrupoLabel,
  criterioDenunciaStudyBuddyLabel,
  statusGrupoEstudoLabel,
} from "../../lib/studyBuddyFormat";
import type { StatusDenuncia } from "../../api/types";
import type {
  AcaoModeracaoGrupoEstudo,
  DenunciaGrupoEstudoResponse,
} from "../../study-buddy/types";

const STATUS_TABS: StatusDenuncia[] = [
  "PENDENTE",
  "EM_ANALISE",
  "PROCEDENTE",
  "IMPROCEDENTE",
  "ARQUIVADA",
];

export default function Moderacao() {
  const { show } = useToast();
  const [tab, setTab] = useState<StatusDenuncia>("PENDENTE");
  const [itens, setItens] = useState<DenunciaGrupoEstudoResponse[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [selecionada, setSelecionada] = useState<DenunciaGrupoEstudoResponse | null>(null);

  const recarregar = useCallback(() => {
    setCarregando(true);
    denunciasGrupoApi
      .porStatus(tab)
      .then(setItens)
      .catch((error: unknown) =>
        show(error instanceof ApiError ? error.message : "Erro ao carregar denúncias.", "error"),
      )
      .finally(() => setCarregando(false));
  }, [tab, show]);

  useEffect(() => {
    recarregar();
  }, [recarregar]);

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 pb-20">
      <header className="flex items-center gap-3 mb-6">
        <ShieldAlert className="text-apto-primary" size={28} />
        <div>
          <h1 className="text-2xl font-bold text-apto-text-main">Moderação de grupos</h1>
          <p className="text-sm text-apto-text-muted">
            Reveja denúncias de grupos de estudo e aplique ações de moderação sobre a oferta.
          </p>
        </div>
      </header>

      <div className="bg-white rounded-apto-section border border-apto-border p-1 inline-flex gap-1 mb-5 flex-wrap">
        {STATUS_TABS.map((status) => (
          <button
            key={status}
            type="button"
            onClick={() => setTab(status)}
            className={`px-4 py-2 rounded-apto-ui text-sm font-medium transition-colors ${
              tab === status
                ? "bg-apto-primary text-white"
                : "text-apto-text-muted hover:text-apto-text-main"
            }`}
          >
            {statusDenunciaLabel[status]}
          </button>
        ))}
      </div>

      {carregando ? (
        <div className="text-apto-text-muted">Carregando denúncias...</div>
      ) : itens.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Nenhuma denúncia em "{statusDenunciaLabel[tab]}".
        </div>
      ) : (
        <div className="space-y-3">
          {itens.map((denuncia) => (
            <div
              key={denuncia.id}
              className="bg-white rounded-apto-section border border-apto-border p-5"
            >
              <div className="flex items-start justify-between gap-3 mb-2">
                <div className="flex-1 min-w-0">
                  <div className="flex flex-wrap items-center gap-2 mb-1">
                    <Badge tone={tone(denuncia.status)}>
                      {statusDenunciaLabel[denuncia.status]}
                    </Badge>
                    <Badge tone="info">
                      {criterioDenunciaStudyBuddyLabel[denuncia.criterio]}
                    </Badge>
                    <span className="text-xs text-apto-text-muted">
                      {formatDateTime(denuncia.criadoEm)}
                    </span>
                  </div>
                  <h3 className="font-bold text-apto-text-main">{denuncia.titulo}</h3>
                  <p className="text-sm text-apto-text-muted mt-1 leading-relaxed">
                    {denuncia.corpo}
                  </p>
                  <Link
                    to={`/grupos/${denuncia.grupoId}`}
                    className="text-xs text-apto-primary hover:underline mt-2 inline-block"
                  >
                    Ver grupo →
                  </Link>
                </div>
                <Button size="sm" onClick={() => setSelecionada(denuncia)}>
                  Moderar
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}

      <ModerarModal
        denuncia={selecionada}
        onClose={() => setSelecionada(null)}
        onSucesso={() => {
          setSelecionada(null);
          recarregar();
        }}
      />
    </div>
  );
}

function tone(status: StatusDenuncia) {
  if (status === "PROCEDENTE") return "danger" as const;
  if (status === "IMPROCEDENTE" || status === "ARQUIVADA") return "neutral" as const;
  if (status === "EM_ANALISE") return "info" as const;
  return "warning" as const;
}

function ModerarModal({
  denuncia,
  onClose,
  onSucesso,
}: {
  denuncia: DenunciaGrupoEstudoResponse | null;
  onClose: () => void;
  onSucesso: () => void;
}) {
  const { show } = useToast();
  const [novoStatus, setNovoStatus] = useState<StatusDenuncia>("EM_ANALISE");
  const [acaoGrupoEstudo, setAcaoGrupoEstudo] = useState<AcaoModeracaoGrupoEstudo>("NENHUMA");
  const [justificativa, setJustificativa] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    if (denuncia) {
      setNovoStatus(denuncia.status === "PENDENTE" ? "EM_ANALISE" : denuncia.status);
      setAcaoGrupoEstudo("NENHUMA");
      setJustificativa("");
      setErro(null);
    }
  }, [denuncia]);

  if (!denuncia) return null;

  const denunciaId = denuncia.id;

  async function moderar() {
    setEnviando(true);
    setErro(null);
    try {
      const resposta = await moderacaoGrupoApi.moderar(denunciaId, {
        novoStatus,
        acaoGrupoEstudo,
        justificativa,
      });
      show(
        `Denúncia moderada. Grupo: ${statusGrupoEstudoLabel[resposta.statusGrupoAtual]}.`,
      );
      onSucesso();
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao moderar denúncia.");
    } finally {
      setEnviando(false);
    }
  }

  return (
    <Modal
      open={!!denuncia}
      onClose={onClose}
      title={`Moderar: ${denuncia.titulo}`}
      size="lg"
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
          <Button onClick={moderar} disabled={enviando}>
            {enviando ? "Aplicando..." : "Aplicar moderação"}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        <div className="rounded-apto-ui bg-apto-bg p-4 border border-apto-border">
          <p className="text-xs text-apto-text-muted mb-1">Conteúdo da denúncia</p>
          <p className="text-sm text-apto-text-main leading-relaxed">{denuncia.corpo}</p>
        </div>

        <div className="grid sm:grid-cols-2 gap-4">
          <Select
            label="Novo status"
            value={novoStatus}
            onChange={(event) => setNovoStatus(event.target.value as StatusDenuncia)}
            options={enumOptions(statusDenunciaLabel)}
          />
          <Select
            label="Ação no grupo"
            value={acaoGrupoEstudo}
            onChange={(event) =>
              setAcaoGrupoEstudo(event.target.value as AcaoModeracaoGrupoEstudo)
            }
            options={enumOptions(acaoModeracaoGrupoLabel)}
          />
        </div>

        <Textarea
          label="Justificativa"
          maxLength={1000}
          value={justificativa}
          onChange={(event) => setJustificativa(event.target.value)}
          placeholder="Explique o raciocínio da decisão..."
          hint={`${justificativa.length}/1000`}
        />

        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}
      </div>
    </Modal>
  );
}
