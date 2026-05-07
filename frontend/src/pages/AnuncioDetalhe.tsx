import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  AlertTriangle,
  Award,
  Heart,
  MapPin,
  PawPrint,
  Sofa,
  Star,
  Users,
} from "lucide-react";
import { Button } from "../components/ui/Button";
import { Modal } from "../components/ui/Modal";
import { Textarea } from "../components/ui/Input";
import { Avatar } from "../components/ui/Avatar";
import { Badge } from "../components/ui/Badge";
import { AvaliarAnuncioModal } from "../components/AvaliarAnuncioModal";
import { DenunciarAnuncioModal } from "../components/DenunciarAnuncioModal";
import { anunciosApi } from "../api/anuncios";
import { moradiasApi } from "../api/moradias";
import { locadoresApi, universitariosApi } from "../api/usuarios";
import { manifestacoesApi } from "../api/manifestacoes";
import { reputacaoApi } from "../api/reputacao";
import { ApiError } from "../api/client";
import { useAuth } from "../auth/useAuth";
import { useToast } from "../components/ui/useToast";
import {
  formatBRL,
  formatDate,
  statusAnuncioLabel,
  tipoAnuncioLabel,
  tipoMoradiaLabel,
} from "../lib/format";
import type {
  AnuncioResponse,
  LocadorResponse,
  MoradiaResponse,
  ReputacaoLocadorResponse,
  UsuarioUniversitarioResponse,
} from "../api/types";

type Anunciante =
  | { tipo: "LOCADOR"; data: LocadorResponse }
  | { tipo: "UNIVERSITARIO"; data: UsuarioUniversitarioResponse }
  | { tipo: "DESCONHECIDO"; data: null };

export default function AnuncioDetalhe() {
  const { id } = useParams<{ id: string }>();
  const { sessao } = useAuth();

  const [anuncio, setAnuncio] = useState<AnuncioResponse | null>(null);
  const [moradia, setMoradia] = useState<MoradiaResponse | null>(null);
  const [anunciante, setAnunciante] = useState<Anunciante | null>(null);
  const [reputacao, setReputacao] = useState<ReputacaoLocadorResponse | null>(
    null,
  );
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  const [interesseAberto, setInteresseAberto] = useState(false);
  const [avaliarAberto, setAvaliarAberto] = useState(false);
  const [denunciarAberto, setDenunciarAberto] = useState(false);

  useEffect(() => {
    if (!id) return;
    setCarregando(true);
    setErro(null);
    anunciosApi
      .obter(id)
      .then(async (a) => {
        setAnuncio(a);
        const moradiaPromise = moradiasApi.obter(a.moradiaId);
        const anunciantePromise = locadoresApi
          .obter(a.anuncianteId)
          .then(
            (l) => ({ tipo: "LOCADOR", data: l }) as Anunciante,
          )
          .catch(() =>
            universitariosApi
              .obter(a.anuncianteId)
              .then(
                (u) => ({ tipo: "UNIVERSITARIO", data: u }) as Anunciante,
              )
              .catch(
                () =>
                  ({
                    tipo: "DESCONHECIDO",
                    data: null,
                  }) as Anunciante,
              ),
          );

        const [m, an] = await Promise.all([moradiaPromise, anunciantePromise]);
        setMoradia(m);
        setAnunciante(an);

        if (an.tipo === "LOCADOR") {
          reputacaoApi
            .doLocador(an.data.id)
            .then(setReputacao)
            .catch(() => setReputacao(null));
        }
      })
      .catch((e: unknown) => {
        setErro(
          e instanceof ApiError ? e.message : "Erro ao carregar anúncio.",
        );
      })
      .finally(() => setCarregando(false));
  }, [id]);

  if (carregando) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-12 text-apto-text-muted">
        Carregando...
      </div>
    );
  }

  if (erro || !anuncio || !moradia) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-12">
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro ?? "Anúncio não encontrado."}
        </div>
      </div>
    );
  }

  const ehDono = sessao?.id === anuncio.anuncianteId;
  const podeManifestar =
    sessao?.tipo === "UNIVERSITARIO" && !ehDono && anuncio.status === "ATIVO";
  const podeAvaliar =
    sessao?.tipo === "UNIVERSITARIO" &&
    !ehDono &&
    anunciante?.tipo === "LOCADOR";

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 pb-20 grid grid-cols-1 lg:grid-cols-[1fr,360px] gap-8">
      <div className="space-y-6">
        <div className="aspect-[16/9] bg-gradient-to-br from-apto-primary-light to-apto-primary/10 rounded-apto-card flex items-center justify-center">
          <span className="text-apto-primary/40 font-bold tracking-widest">
            {tipoMoradiaLabel[moradia.tipoMoradia].toUpperCase()}
          </span>
        </div>

        <div className="bg-white rounded-apto-section border border-apto-border p-6 space-y-4">
          <div className="flex items-start justify-between gap-3">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <Badge tone="primary">
                  {tipoAnuncioLabel[anuncio.tipoAnuncio]}
                </Badge>
                <Badge
                  tone={
                    anuncio.status === "ATIVO"
                      ? "success"
                      : anuncio.status === "PAUSADO"
                        ? "warning"
                        : "neutral"
                  }
                >
                  {statusAnuncioLabel[anuncio.status]}
                </Badge>
              </div>
              <h1 className="text-2xl font-bold text-apto-text-main">
                {anuncio.titulo}
              </h1>
              <p className="flex items-center gap-1 text-sm text-apto-text-muted mt-1">
                <MapPin size={14} />
                {moradia.bairro} · {moradia.enderecoResumo}
              </p>
            </div>
            <p className="text-2xl font-bold text-apto-text-main flex-shrink-0">
              {formatBRL(anuncio.valorMensal)}
              <span className="text-sm text-apto-text-muted font-normal">
                /mês
              </span>
            </p>
          </div>

          <p className="text-apto-text-main leading-relaxed whitespace-pre-line">
            {anuncio.descricao}
          </p>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-4 border-t border-apto-border">
            <Atributo
              icon={Users}
              label="Vagas"
              value={String(moradia.quantidadeVagas)}
            />
            <Atributo
              icon={Sofa}
              label="Mobiliado"
              value={moradia.mobiliado ? "Sim" : "Não"}
            />
            <Atributo
              icon={PawPrint}
              label="Pets"
              value={moradia.aceitaAnimais ? "Sim" : "Não"}
            />
            <Atributo
              icon={MapPin}
              label="Tipo"
              value={tipoMoradiaLabel[moradia.tipoMoradia]}
            />
          </div>

          {moradia.regrasMoradia && (
            <div className="pt-4 border-t border-apto-border">
              <p className="section-title mb-2">Regras da moradia</p>
              <p className="text-sm text-apto-text-muted leading-relaxed whitespace-pre-line">
                {moradia.regrasMoradia}
              </p>
            </div>
          )}

          <p className="text-xs text-apto-text-muted pt-3 border-t border-apto-border">
            Publicado em {formatDate(anuncio.dataPublicacao)}
          </p>
        </div>
      </div>

      <aside className="space-y-4">
        {anunciante && anunciante.tipo !== "DESCONHECIDO" && (
          <div className="bg-white rounded-apto-section border border-apto-border p-5 space-y-4">
            <p className="section-title">Anunciante</p>
            <div className="flex items-center gap-3">
              <Avatar nome={anunciante.data.nome} size="md" />
              <div className="flex-1 min-w-0">
                <p className="font-bold text-apto-text-main truncate">
                  {anunciante.data.nome}
                </p>
                <p className="text-xs text-apto-text-muted">
                  {anunciante.tipo === "LOCADOR" ? "Locador" : "Universitário"}
                </p>
              </div>
            </div>

            {anunciante.tipo === "LOCADOR" && reputacao && (
              <div className="flex items-center gap-2 px-3 py-2 bg-apto-bg rounded-apto-ui border border-apto-border">
                <Award className="text-amber-500" size={16} />
                <div>
                  <p className="text-[10px] uppercase font-bold text-apto-text-muted">
                    Reputação
                  </p>
                  <p className="font-bold text-apto-text-main text-sm">
                    {reputacao.reputacaoScore.toFixed(1)} / 5.0
                  </p>
                </div>
                <span className="text-xs text-apto-text-muted ml-auto">
                  {reputacao.totalAvaliacoes} avaliações
                </span>
              </div>
            )}

            {anunciante.tipo === "LOCADOR" && (
              <Link to={`/locadores/${anunciante.data.id}`}>
                <Button variant="secondary" size="sm" className="w-full">
                  Ver perfil público
                </Button>
              </Link>
            )}
          </div>
        )}

        {podeManifestar && (
          <div className="bg-white rounded-apto-section border border-apto-border p-5 space-y-3">
            <Button
              className="w-full flex items-center justify-center gap-2"
              onClick={() => setInteresseAberto(true)}
            >
              <Heart size={16} />
              Tenho interesse
            </Button>
            <p className="text-xs text-apto-text-muted text-center">
              O anunciante recebe sua mensagem. Quando aceitar, vocês trocam
              contato.
            </p>
          </div>
        )}

        {podeAvaliar && (
          <div className="bg-white rounded-apto-section border border-apto-border p-5">
            <Button
              variant="secondary"
              className="w-full flex items-center justify-center gap-2"
              onClick={() => setAvaliarAberto(true)}
            >
              <Star size={16} />
              Avaliar locador
            </Button>
          </div>
        )}

        {ehDono && (
          <div className="bg-white rounded-apto-section border border-apto-border p-5 space-y-2">
            <Link to={`/anuncios/${anuncio.id}/interesses`}>
              <Button variant="secondary" size="sm" className="w-full">
                Ver manifestações de interesse
              </Button>
            </Link>
          </div>
        )}

        {sessao && !ehDono && (
          <div className="bg-white rounded-apto-section border border-apto-border p-4">
            <button
              type="button"
              className="text-xs text-apto-text-muted hover:text-red-600 flex items-center gap-1"
              onClick={() => setDenunciarAberto(true)}
            >
              <AlertTriangle size={12} />
              Denunciar este anúncio
            </button>
          </div>
        )}
      </aside>

      <ManifestarInteresseModal
        open={interesseAberto}
        onClose={() => setInteresseAberto(false)}
        anuncioId={anuncio.id}
      />

      <AvaliarAnuncioModal
        open={avaliarAberto}
        onClose={() => setAvaliarAberto(false)}
        anuncioId={anuncio.id}
        onSucesso={() => {
          if (anunciante?.tipo === "LOCADOR") {
            reputacaoApi
              .doLocador(anunciante.data.id)
              .then(setReputacao)
              .catch(() => {});
          }
        }}
      />

      <DenunciarAnuncioModal
        open={denunciarAberto}
        onClose={() => setDenunciarAberto(false)}
        anuncioId={anuncio.id}
      />
    </div>
  );
}

function Atributo({
  icon: Icon,
  label,
  value,
}: {
  icon: typeof Users;
  label: string;
  value: string;
}) {
  return (
    <div className="flex items-center gap-2 text-sm">
      <Icon size={14} className="text-apto-primary" />
      <div>
        <p className="text-[10px] uppercase font-bold text-apto-text-muted">
          {label}
        </p>
        <p className="font-medium text-apto-text-main">{value}</p>
      </div>
    </div>
  );
}

function ManifestarInteresseModal({
  open,
  onClose,
  anuncioId,
}: {
  open: boolean;
  onClose: () => void;
  anuncioId: string;
}) {
  const { sessao } = useAuth();
  const { show } = useToast();
  const [mensagem, setMensagem] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  async function enviar() {
    if (!sessao) return;
    setEnviando(true);
    setErro(null);
    try {
      await manifestacoesApi.criar({
        anuncioId,
        interessadoId: sessao.id,
        mensagem,
      });
      show("Interesse enviado");
      setMensagem("");
      onClose();
    } catch (e: unknown) {
      setErro(e instanceof ApiError ? e.message : "Erro ao enviar interesse.");
    } finally {
      setEnviando(false);
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Manifestar interesse"
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
          <Button onClick={enviar} disabled={enviando}>
            {enviando ? "Enviando..." : "Enviar interesse"}
          </Button>
        </>
      }
    >
      <Textarea
        label="Mensagem para o anunciante"
        value={mensagem}
        onChange={(e) => setMensagem(e.target.value)}
        maxLength={500}
        placeholder="Conte um pouco sobre você e por que tem interesse..."
        hint={`${mensagem.length}/500`}
      />
      {erro && (
        <div className="mt-3 p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      )}
    </Modal>
  );
}
