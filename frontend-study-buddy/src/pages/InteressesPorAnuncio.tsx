import { useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Mail, Phone } from "lucide-react";
import { Avatar } from "../components/ui/Avatar";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { manifestacoesApi } from "../api/manifestacoes";
import { anunciosApi } from "../api/anuncios";
import { useAuth } from "../auth/useAuth";
import { useToast } from "../components/ui/useToast";
import { ApiError } from "../api/client";
import { formatDateTime, statusManifestacaoLabel } from "../lib/format";
import type {
  AnuncioResponse,
  ContatoLiberadoResponse,
  ManifestacaoInteresseDetalheResponse,
  ManifestacaoInteresseResponse,
  StatusManifestacaoInteresse,
} from "../api/types";

export default function InteressesPorAnuncio() {
  const { id: anuncioId } = useParams<{ id: string }>();
  const { sessao } = useAuth();
  const { show } = useToast();

  const [anuncio, setAnuncio] = useState<AnuncioResponse | null>(null);
  const [itens, setItens] = useState<ManifestacaoInteresseResponse[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [detalhes, setDetalhes] = useState<
    Record<string, ManifestacaoInteresseDetalheResponse>
  >({});

  const recarregar = useCallback(() => {
    if (!sessao || !anuncioId) return;
    setCarregando(true);
    Promise.all([
      anunciosApi.obter(anuncioId),
      manifestacoesApi.porAnuncio(anuncioId, sessao.id),
    ])
      .then(([a, lista]) => {
        setAnuncio(a);
        setItens(lista);
      })
      .catch((e: unknown) =>
        show(
          e instanceof ApiError ? e.message : "Erro ao carregar interesses",
          "error",
        ),
      )
      .finally(() => setCarregando(false));
  }, [anuncioId, sessao, show]);

  useEffect(() => {
    recarregar();
  }, [recarregar]);

  async function aceitar(m: ManifestacaoInteresseResponse) {
    if (!sessao) return;
    try {
      const detalhe = await manifestacoesApi.aceitar(m.id, sessao.id);
      show("Interesse aceito — contato liberado");
      setDetalhes((prev) => ({ ...prev, [m.id]: detalhe }));
      recarregar();
    } catch (e: unknown) {
      show(e instanceof ApiError ? e.message : "Erro ao aceitar", "error");
    }
  }

  async function recusar(m: ManifestacaoInteresseResponse) {
    if (!sessao) return;
    try {
      await manifestacoesApi.recusar(m.id, sessao.id);
      show("Interesse recusado");
      recarregar();
    } catch (e: unknown) {
      show(e instanceof ApiError ? e.message : "Erro ao recusar", "error");
    }
  }

  async function verContato(m: ManifestacaoInteresseResponse) {
    if (!sessao) return;
    if (detalhes[m.id]) return;
    try {
      const d = await manifestacoesApi.obter(m.id, sessao.id);
      setDetalhes((prev) => ({ ...prev, [m.id]: d }));
    } catch (e: unknown) {
      show(e instanceof ApiError ? e.message : "Erro ao carregar", "error");
    }
  }

  if (!sessao) return null;

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 pb-20">
      <Link
        to="/meus-anuncios"
        className="text-sm text-apto-text-muted hover:text-apto-primary"
      >
        ← Meus anúncios
      </Link>
      <h1 className="text-2xl font-bold text-apto-text-main mt-2 mb-6">
        Interesses recebidos
        {anuncio && (
          <span className="block text-base font-normal text-apto-text-muted">
            {anuncio.titulo}
          </span>
        )}
      </h1>

      {carregando ? (
        <div className="text-apto-text-muted">Carregando...</div>
      ) : itens.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Nenhum interesse manifestado ainda.
        </div>
      ) : (
        <div className="space-y-3">
          {itens.map((m) => {
            const detalhe = detalhes[m.id];
            const contato = detalhe?.contatoInteressado;
            return (
              <div
                key={m.id}
                className="bg-white rounded-apto-section border border-apto-border p-5"
              >
                <div className="flex items-center gap-3 mb-3">
                  <Avatar nome={m.interessadoNome} size="md" />
                  <div className="flex-1 min-w-0">
                    <p className="font-bold text-apto-text-main truncate">
                      {m.interessadoNome}
                    </p>
                    <p className="text-xs text-apto-text-muted">
                      {formatDateTime(m.dataManifestacao)}
                    </p>
                  </div>
                  <Badge tone={statusTone(m.status)}>
                    {statusManifestacaoLabel[m.status]}
                  </Badge>
                </div>

                {m.mensagem && (
                  <p className="text-sm text-apto-text-muted italic border-l-2 border-apto-border pl-3 mb-3">
                    "{m.mensagem}"
                  </p>
                )}

                {m.status === "PENDENTE" && (
                  <div className="flex gap-2">
                    <Button size="sm" onClick={() => aceitar(m)}>
                      Aceitar
                    </Button>
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={() => recusar(m)}
                    >
                      Recusar
                    </Button>
                  </div>
                )}

                {m.status === "ACEITA" && (
                  <div className="mt-2">
                    {contato ? (
                      <ContatoBox contato={contato} />
                    ) : (
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => verContato(m)}
                      >
                        Ver contato liberado
                      </Button>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

function ContatoBox({ contato }: { contato: ContatoLiberadoResponse }) {
  return (
    <div className="rounded-apto-ui bg-emerald-50 border border-emerald-200 p-3 space-y-1.5">
      <p className="text-[10px] uppercase font-bold text-emerald-700 tracking-wider">
        Contato liberado
      </p>
      <p className="font-bold text-apto-text-main">{contato.nome}</p>
      <p className="flex items-center gap-2 text-sm text-apto-text-main">
        <Mail size={14} className="text-emerald-700" />
        {contato.email}
      </p>
      <p className="flex items-center gap-2 text-sm text-apto-text-main">
        <Phone size={14} className="text-emerald-700" />
        {contato.telefone}
      </p>
      {contato.emailInstitucional && (
        <p className="flex items-center gap-2 text-sm text-apto-text-muted">
          <Mail size={14} />
          {contato.emailInstitucional}
        </p>
      )}
    </div>
  );
}

function statusTone(s: StatusManifestacaoInteresse) {
  if (s === "ACEITA") return "success" as const;
  if (s === "RECUSADA" || s === "CANCELADA") return "neutral" as const;
  return "warning" as const;
}
