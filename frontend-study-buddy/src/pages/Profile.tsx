import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  BookOpen,
  Calendar,
  Mail,
  Moon,
  Phone,
  Settings,
  Sparkles,
  Store,
  Users2,
  Volume2,
} from "lucide-react";
import { Avatar } from "../components/ui/Avatar";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { perfilApi } from "../api/usuarios";
import { perfilAnuncianteApi } from "../api/reputacao";
import { useAuth } from "../auth/useAuth";
import { ApiError } from "../api/client";
import {
  formatDate,
  frequenciaVisitasLabel,
  generoLabel,
  horarioSonoLabel,
  nivelBarulhoLabel,
  nivelOrganizacaoLabel,
  preferenciaGeneroConvivenciaLabel,
  rotinaEstudosLabel,
} from "../lib/format";
import type { PerfilAnuncianteResponse, PerfilResponse } from "../api/types";

export default function Profile() {
  const { sessao, atualizarPerfilAnunciante } = useAuth();
  const [perfil, setPerfil] = useState<PerfilResponse | null>(null);
  const [perfilAnunciante, setPerfilAnunciante] =
    useState<PerfilAnuncianteResponse | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    if (!sessao || sessao.tipo !== "UNIVERSITARIO") {
      setCarregando(false);
      return;
    }
    setCarregando(true);

    Promise.all([
      perfilApi.obter(sessao.id).catch((e: unknown) => {
        if (e instanceof ApiError && e.status === 404) return null;
        throw e;
      }),
      // verifica se já tem PerfilAnunciante — 404 significa que não tem ainda
      perfilAnuncianteApi.porUsuario(sessao.id).catch(() => null),
    ])
      .then(([p, pa]) => {
        setPerfil(p);
        setPerfilAnunciante(pa);
      })
      .catch((e: unknown) =>
        setErro(e instanceof ApiError ? e.message : "Erro ao carregar perfil."),
      )
      .finally(() => setCarregando(false));
  }, [sessao]);

  if (!sessao) return null;

  if (sessao.tipo === "LOCADOR") {
    return (
      <div className="max-w-2xl mx-auto px-4 py-12">
        <div className="bg-white rounded-apto-section border border-apto-border p-8 text-center space-y-4">
          <Avatar nome={sessao.nome} size="xl" className="mx-auto" />
          <h1 className="text-2xl font-bold text-apto-text-main">
            {sessao.nome}
          </h1>
          <Badge tone="primary">Locador</Badge>
          <p className="text-apto-text-muted">
            Locadores publicam anúncios e recebem manifestações de interesse. O
            perfil de convivência é exclusivo de universitários.
          </p>
          <Link to="/meus-anuncios">
            <Button>Ver meus anúncios</Button>
          </Link>
        </div>
      </div>
    );
  }

  if (carregando) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-apto-text-muted">
        Carregando perfil...
      </div>
    );
  }

  if (erro) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12">
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      </div>
    );
  }

  if (!perfil) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12">
        <div className="bg-white rounded-apto-section border border-apto-border p-8 text-center space-y-4">
          <h1 className="text-2xl font-bold text-apto-text-main">
            Complete seu perfil
          </h1>
          <p className="text-apto-text-muted">
            Para usar o matchmaking você precisa preencher seu perfil de
            convivência.
          </p>
          <Link to="/profile/convivencia">
            <Button>Preencher perfil</Button>
          </Link>
        </div>
      </div>
    );
  }

  const habits = [
    {
      label: "Ciclo de Sono",
      value: perfil.horarioSono ? horarioSonoLabel[perfil.horarioSono] : "—",
      icon: Moon,
    },
    {
      label: "Tolerância a Barulho",
      value: perfil.nivelBarulhoAceitavel
        ? nivelBarulhoLabel[perfil.nivelBarulhoAceitavel]
        : "—",
      icon: Volume2,
    },
    {
      label: "Visitas Externas",
      value: perfil.frequenciaVisitas
        ? frequenciaVisitasLabel[perfil.frequenciaVisitas]
        : "—",
      icon: Users2,
    },
    {
      label: "Organização",
      value: perfil.nivelOrganizacao
        ? nivelOrganizacaoLabel[perfil.nivelOrganizacao]
        : "—",
      icon: Sparkles,
    },
    {
      label: "Rotina de Estudos",
      value: perfil.rotinaEstudos
        ? rotinaEstudosLabel[perfil.rotinaEstudos]
        : "—",
      icon: BookOpen,
    },
    {
      label: "Convivência preferida",
      value: perfil.preferenciaGeneroConvivencia
        ? preferenciaGeneroConvivenciaLabel[perfil.preferenciaGeneroConvivencia]
        : "—",
      icon: Users2,
    },
  ];

  return (
    <div className="max-w-4xl mx-auto px-4 py-12 space-y-8 pb-20">
      <div className="flex flex-col md:flex-row gap-8 items-start">
        <div className="w-full md:w-1/3 space-y-6">
          <div className="bg-white p-6 rounded-apto-section border border-apto-border shadow-sm text-center">
            <Avatar nome={perfil.nome} size="xl" className="mx-auto mb-4" />
            <h2 className="text-xl font-bold text-apto-text-main">
              {perfil.nome}
            </h2>
            <p className="text-sm text-apto-text-muted">{perfil.curso}</p>

            <Link to="/profile/convivencia">
              <Button
                variant="secondary"
                className="w-full mt-6 flex items-center justify-center gap-2 font-bold py-2.5"
              >
                <Settings size={16} />
                Editar perfil
              </Button>
            </Link>
          </div>

          <div className="bg-white p-6 rounded-apto-section border border-apto-border shadow-sm space-y-3">
            <p className="section-title">Contato</p>
            <div className="flex items-center gap-2 text-sm text-apto-text-main">
              <Mail size={14} className="text-apto-text-muted" />
              <span className="truncate">{perfil.email}</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-apto-text-main">
              <Mail size={14} className="text-apto-text-muted" />
              <span className="truncate">{perfil.emailInstitucional}</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-apto-text-main">
              <Phone size={14} className="text-apto-text-muted" />
              <span>{perfil.telefone}</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-apto-text-main">
              <Calendar size={14} className="text-apto-text-muted" />
              <span>
                {formatDate(perfil.dataNascimento)} ·{" "}
                {generoLabel[perfil.genero]}
              </span>
            </div>
          </div>

          {/*  parte do anunciante */}
          <SecaoAnunciante
            sessaoId={sessao.id}
            perfilAnunciante={perfilAnunciante}
            onAtualizar={(pa) => {
              setPerfilAnunciante(pa);
              // propaga para a sessão para que a Navbar atualize imediatamente
              atualizarPerfilAnunciante(pa?.id ?? null);
            }}
          />
        </div>

        <div className="flex-1 space-y-6">
          <section className="bg-white p-8 rounded-apto-section border border-apto-border shadow-sm space-y-6">
            <div>
              <h3 className="text-2xl font-bold text-apto-text-main">
                Hábitos de convivência
              </h3>
              <p className="text-apto-text-muted leading-relaxed mt-2">
                Estas preferências são a base do nosso{" "}
                <strong>matchmaking</strong>. Seja honesto para garantir uma
                boa experiência!
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {habits.map((habit) => {
                const Icon = habit.icon;
                return (
                  <div
                    key={habit.label}
                    className="p-4 rounded-xl border border-apto-border bg-apto-bg/50 space-y-1"
                  >
                    <div className="flex items-center gap-2 text-apto-primary mb-1">
                      <Icon size={16} />
                      <span className="text-[10px] font-bold uppercase tracking-wider text-apto-text-muted">
                        {habit.label}
                      </span>
                    </div>
                    <p className="font-bold text-apto-text-main">
                      {habit.value}
                    </p>
                  </div>
                );
              })}
            </div>

            <div className="grid grid-cols-3 gap-3 pt-2">
              <FlagPill label="Álcool" valor={perfil.consomeAlcool} />
              <FlagPill label="Fumante" valor={perfil.fumante} />
              <FlagPill label="Animais" valor={perfil.aceitaAnimais} />
            </div>

            {perfil.descricaoLivre && (
              <div className="pt-6 border-t border-apto-border space-y-3">
                <h4 className="font-bold text-apto-text-main uppercase text-[12px] tracking-wider">
                  Bio / Sobre mim
                </h4>
                <p className="text-apto-text-muted leading-relaxed italic border-l-4 border-apto-border pl-4">
                  "{perfil.descricaoLivre}"
                </p>
              </div>
            )}
          </section>
        </div>
      </div>
    </div>
  );
}

// parte do anunciante
function SecaoAnunciante({
  sessaoId,
  perfilAnunciante,
  onAtualizar,
}: {
  sessaoId: string;
  perfilAnunciante: PerfilAnuncianteResponse | null;
  onAtualizar: (pa: PerfilAnuncianteResponse | null) => void;
}) {
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  async function habilitar() {
    setCarregando(true);
    setErro(null);
    try {
      const pa = await perfilAnuncianteApi.habilitar(sessaoId);
      onAtualizar(pa);
    } catch (e: unknown) {
      setErro(
        e instanceof ApiError ? e.message : "Erro ao habilitar perfil de anunciante.",
      );
    } finally {
      setCarregando(false);
    }
  }

  async function desabilitar() {
    setCarregando(true);
    setErro(null);
    try {
      const pa = await perfilAnuncianteApi.desabilitar(sessaoId);
      onAtualizar(pa);
    } catch (e: unknown) {
      setErro(
        e instanceof ApiError ? e.message : "Erro ao desabilitar perfil de anunciante.",
      );
    } finally {
      setCarregando(false);
    }
  }

  const estaAtivo = perfilAnunciante?.ativo === true;

  return (
    <div className="bg-white p-6 rounded-apto-section border border-apto-border shadow-sm space-y-4">
      <div className="flex items-center gap-2">
        <Store size={16} className="text-apto-primary" />
        <p className="section-title">Anunciante</p>
      </div>

      {estaAtivo ? (
        <>
          <div className="flex items-center gap-2">
            <Badge tone="success">Ativo</Badge>
            <p className="text-sm text-apto-text-muted">
              Você pode publicar anúncios de moradia.
            </p>
          </div>
          <Link to="/meus-anuncios">
            <Button variant="secondary" size="sm" className="w-full">
              Ver meus anúncios
            </Button>
          </Link>
          <button
            type="button"
            onClick={desabilitar}
            disabled={carregando}
            className="w-full text-xs text-apto-text-muted hover:text-red-600 transition-colors"
          >
            {carregando ? "Aguarde..." : "Desativar papel de anunciante"}
          </button>
        </>
      ) : (
        <>
          <p className="text-sm text-apto-text-muted leading-relaxed">
            Quer sublocar um quarto ou anunciar sua moradia? Ative o papel de
            anunciante para publicar anúncios e receber manifestações de
            interesse.
          </p>
          <Button
            className="w-full"
            onClick={habilitar}
            disabled={carregando}
          >
            <Store size={14} className="mr-2" />
            {carregando ? "Ativando..." : "Quero anunciar"}
          </Button>
        </>
      )}

      {erro && (
        <p className="text-xs text-red-600">{erro}</p>
      )}
    </div>
  );
}

function FlagPill({ label, valor }: { label: string; valor?: boolean | null }) {
  const tone =
    valor === true ? "success" : valor === false ? "neutral" : "neutral";
  const texto = valor === true ? "Sim" : valor === false ? "Não" : "—";
  return (
    <div className="p-3 rounded-xl border border-apto-border bg-apto-bg/50 text-center space-y-1">
      <p className="text-[10px] font-bold uppercase tracking-wider text-apto-text-muted">
        {label}
      </p>
      <Badge tone={tone}>{texto}</Badge>
    </div>
  );
}