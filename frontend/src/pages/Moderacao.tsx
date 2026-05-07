import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ShieldAlert } from "lucide-react";
import { Badge } from "../components/ui/Badge";
import { Button } from "../components/ui/Button";
import { Modal } from "../components/ui/Modal";
import { Select } from "../components/ui/Select";
import { Textarea } from "../components/ui/Input";
import { denunciasApi } from "../api/denuncias";
import { moderacaoApi } from "../api/moderacao";
import { ApiError } from "../api/client";
import { useToast } from "../components/ui/useToast";
import {
  acaoModeracaoLabel,
  enumOptions,
  formatDateTime,
  statusDenunciaLabel,
} from "../lib/format";
import type {
  AcaoModeracaoAnuncio,
  DenunciaResponse,
  StatusDenuncia,
} from "../api/types";

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
  const [itens, setItens] = useState<DenunciaResponse[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [selecionada, setSelecionada] = useState<DenunciaResponse | null>(null);

  const recarregar = useCallback(() => {
    setCarregando(true);
    denunciasApi
      .porStatus(tab)
      .then(setItens)
      .catch((e: unknown) =>
        show(e instanceof ApiError ? e.message : "Erro", "error"),
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
          <h1 className="text-2xl font-bold text-apto-text-main">
            Moderação de denúncias
          </h1>
          <p className="text-sm text-apto-text-muted">
            Reveja, classifique e aplique ações sobre anúncios denunciados.
          </p>
        </div>
      </header>

      <div className="bg-white rounded-apto-section border border-apto-border p-1 inline-flex gap-1 mb-5 flex-wrap">
        {STATUS_TABS.map((s) => (
          <button
            key={s}
            type="button"
            onClick={() => setTab(s)}
            className={`px-4 py-2 rounded-apto-ui text-sm font-medium transition-colors ${
              tab === s
                ? "bg-apto-primary text-white"
                : "text-apto-text-muted hover:text-apto-text-main"
            }`}
          >
            {statusDenunciaLabel[s]}
          </button>
        ))}
      </div>

      {carregando ? (
        <div className="text-apto-text-muted">Carregando...</div>
      ) : itens.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Nenhuma denúncia em "{statusDenunciaLabel[tab]}".
        </div>
      ) : (
        <div className="space-y-3">
          {itens.map((d) => (
            <div
              key={d.id}
              className="bg-white rounded-apto-section border border-apto-border p-5"
            >
              <div className="flex items-start justify-between gap-3 mb-2">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <Badge tone={tone(d.statusDenuncia)}>
                      {statusDenunciaLabel[d.statusDenuncia]}
                    </Badge>
                    <span className="text-xs text-apto-text-muted">
                      {formatDateTime(d.criadoEm)}
                    </span>
                  </div>
                  <h3 className="font-bold text-apto-text-main">{d.titulo}</h3>
                  <p className="text-sm text-apto-text-muted mt-1 leading-relaxed">
                    {d.corpo}
                  </p>
                  <Link
                    to={`/anuncios/${d.anuncioId}`}
                    className="text-xs text-apto-primary hover:underline mt-2 inline-block"
                  >
                    Ver anúncio →
                  </Link>
                </div>
                <Button size="sm" onClick={() => setSelecionada(d)}>
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

function tone(s: StatusDenuncia) {
  if (s === "PROCEDENTE") return "danger" as const;
  if (s === "IMPROCEDENTE" || s === "ARQUIVADA") return "neutral" as const;
  if (s === "EM_ANALISE") return "info" as const;
  return "warning" as const;
}

function ModerarModal({
  denuncia,
  onClose,
  onSucesso,
}: {
  denuncia: DenunciaResponse | null;
  onClose: () => void;
  onSucesso: () => void;
}) {
  const { show } = useToast();
  const [novoStatus, setNovoStatus] = useState<StatusDenuncia>("EM_ANALISE");
  const [acaoAnuncio, setAcaoAnuncio] =
    useState<AcaoModeracaoAnuncio>("NENHUMA");
  const [justificativa, setJustificativa] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    if (denuncia) {
      setNovoStatus(
        denuncia.statusDenuncia === "PENDENTE"
          ? "EM_ANALISE"
          : denuncia.statusDenuncia,
      );
      setAcaoAnuncio("NENHUMA");
      setJustificativa("");
      setErro(null);
    }
  }, [denuncia]);

  if (!denuncia) return null;

  async function moderar() {
    if (!denuncia) return;
    setEnviando(true);
    setErro(null);
    try {
      await moderacaoApi.moderar(denuncia.id, {
        novoStatus,
        acaoAnuncio,
        justificativa,
      });
      show("Denúncia moderada");
      onSucesso();
    } catch (e: unknown) {
      setErro(e instanceof ApiError ? e.message : "Erro ao moderar.");
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
          <p className="text-sm text-apto-text-main leading-relaxed">
            {denuncia.corpo}
          </p>
        </div>

        <div className="grid sm:grid-cols-2 gap-4">
          <Select
            label="Novo status"
            value={novoStatus}
            onChange={(e) => setNovoStatus(e.target.value as StatusDenuncia)}
            options={enumOptions(statusDenunciaLabel)}
          />
          <Select
            label="Ação no anúncio"
            value={acaoAnuncio}
            onChange={(e) =>
              setAcaoAnuncio(e.target.value as AcaoModeracaoAnuncio)
            }
            options={enumOptions(acaoModeracaoLabel)}
          />
        </div>

        <Textarea
          label="Justificativa"
          maxLength={1000}
          value={justificativa}
          onChange={(e) => setJustificativa(e.target.value)}
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
