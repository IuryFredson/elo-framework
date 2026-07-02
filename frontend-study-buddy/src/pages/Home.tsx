import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Compass, Plus, Search, Sparkles } from "lucide-react";
import { Avatar } from "../components/ui/Avatar";
import { Badge } from "../components/ui/Badge";
import { Button } from "../components/ui/Button";
import { anunciosApi } from "../api/anuncios";
import { matchmakingApi } from "../api/matchmaking";
import { manifestacoesApi } from "../api/manifestacoes";
import { useAuth } from "../auth/useAuth";
import { ApiError } from "../api/client";
import {
  formatBRL,
  generoLabel,
  statusAnuncioLabel,
  statusManifestacaoLabel,
  tipoMoradiaLabel,
} from "../lib/format";
import type {
  AnuncioResponse,
  BuscaAnuncioResponse,
  ManifestacaoInteresseResponse,
  MatchColegaResponse,
} from "../api/types";

export default function Home() {
  const { sessao } = useAuth();
  if (!sessao) return null;

  return sessao.tipo === "UNIVERSITARIO" ? (
    <HomeUniversitario />
  ) : (
    <HomeLocador />
  );
}

function HomeUniversitario() {
  const { sessao } = useAuth();
  const [anuncios, setAnuncios] = useState<BuscaAnuncioResponse[]>([]);
  const [matches, setMatches] = useState<MatchColegaResponse[]>([]);
  const [perfilOk, setPerfilOk] = useState<boolean | null>(null);

  useEffect(() => {
    if (!sessao) return;
    anunciosApi
      .buscar({ size: 6 })
      .then((p) => setAnuncios(p.conteudo))
      .catch(() => {});
    matchmakingApi
      .buscarColegas(sessao.id, 3)
      .then((r) => {
        setMatches(r.candidatos);
        setPerfilOk(true);
      })
      .catch((e: unknown) => {
        if (e instanceof ApiError && e.status === 400) {
          setPerfilOk(false);
        } else {
          setPerfilOk(true);
        }
      });
  }, [sessao]);

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 pb-20 space-y-8">
      <header className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-apto-text-main">
            Olá, {sessao?.nome.split(" ")[0]}
          </h1>
          <p className="text-apto-text-muted">
            Encontre uma moradia ou um colega compatível.
          </p>
        </div>
        <div className="flex gap-2">
          <Link to="/buscar">
            <Button variant="secondary" className="flex items-center gap-2">
              <Search size={16} />
              Buscar
            </Button>
          </Link>
          <Link to="/matchmaking">
            <Button className="flex items-center gap-2">
              <Compass size={16} />
              Matchmaking
            </Button>
          </Link>
        </div>
      </header>

      {perfilOk === false && (
        <div className="bg-amber-50 border border-amber-200 rounded-apto-section p-5 flex items-start gap-3">
          <Sparkles className="text-amber-600 flex-shrink-0" size={20} />
          <div className="flex-1">
            <p className="font-bold text-apto-text-main">
              Complete seu perfil de convivência
            </p>
            <p className="text-sm text-apto-text-muted">
              Sem ele, o matchmaking não consegue calcular candidatos
              compatíveis.
            </p>
          </div>
          <Link to="/profile/convivencia">
            <Button size="sm">Preencher</Button>
          </Link>
        </div>
      )}

      {matches.length > 0 && (
        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-apto-text-main">
              Top matches
            </h2>
            <Link
              to="/matchmaking"
              className="text-sm font-medium text-apto-primary hover:underline"
            >
              Ver todos
            </Link>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {matches.map((m) => (
              <div
                key={m.id}
                className="bg-white rounded-apto-section border border-apto-border p-4 space-y-3"
              >
                <div className="flex items-center gap-3">
                  <Avatar nome={m.nome} size="md" />
                  <div className="flex-1 min-w-0">
                    <p className="font-bold text-apto-text-main truncate">
                      {m.nome}
                    </p>
                    <p className="text-xs text-apto-text-muted">
                      {m.curso} · {generoLabel[m.genero]}
                    </p>
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <Badge tone={m.origem === "LLM" ? "primary" : "info"}>
                    {m.origem === "LLM" ? "IA" : "Cálculo direto"}
                  </Badge>
                  <p className="text-2xl font-bold text-apto-text-main">
                    {m.percentualCompatibilidade}%
                  </p>
                </div>
              </div>
            ))}
          </div>
        </section>
      )}

      <section>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-apto-text-main">
            Anúncios recentes
          </h2>
          <Link
            to="/buscar"
            className="text-sm font-medium text-apto-primary hover:underline"
          >
            Ver todos
          </Link>
        </div>
        {anuncios.length === 0 ? (
          <div className="bg-white rounded-apto-section border border-apto-border p-8 text-center text-apto-text-muted">
            Nenhum anúncio disponível.
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {anuncios.map((a) => (
              <Link
                key={a.id}
                to={`/anuncios/${a.id}`}
                className="bg-white rounded-apto-card border border-apto-border overflow-hidden hover:shadow-md transition-shadow"
              >
                <div className="aspect-video bg-gradient-to-br from-apto-primary-light to-apto-primary/10 flex items-center justify-center text-apto-primary/40 font-bold tracking-widest text-sm">
                  {tipoMoradiaLabel[a.tipoMoradia].toUpperCase()}
                </div>
                <div className="p-4">
                  <p className="font-bold text-apto-text-main line-clamp-1">
                    {a.titulo}
                  </p>
                  <p className="text-xs text-apto-text-muted">{a.bairro}</p>
                  <p className="font-bold text-apto-text-main mt-2">
                    {formatBRL(a.valorMensal)}/mês
                  </p>
                </div>
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

function HomeLocador() {
  const { sessao } = useAuth();
  const [anuncios, setAnuncios] = useState<AnuncioResponse[]>([]);
  const [interessesPendentes, setInteressesPendentes] = useState<
    ManifestacaoInteresseResponse[]
  >([]);

  useEffect(() => {
    if (!sessao) return;
    anunciosApi.listar().then(async (todos) => {
      const meus = todos.filter((a) => a.anuncianteId === sessao.id);
      setAnuncios(meus);
      const pendentes: ManifestacaoInteresseResponse[] = [];
      for (const a of meus) {
        try {
          const lista = await manifestacoesApi.porAnuncio(a.id, sessao.id);
          pendentes.push(...lista.filter((m) => m.status === "PENDENTE"));
        } catch {
          // ignora — falha de uma manifestacao não deve quebrar a home
        }
      }
      setInteressesPendentes(pendentes);
    });
  }, [sessao]);

  const ativos = anuncios.filter((a) => a.status === "ATIVO").length;
  const pausados = anuncios.filter((a) => a.status === "PAUSADO").length;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 pb-20 space-y-8">
      <header className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-3xl font-bold text-apto-text-main">
            Olá, {sessao?.nome.split(" ")[0]}
          </h1>
          <p className="text-apto-text-muted">Painel do anunciante</p>
        </div>
        <Link to="/anuncios/novo">
          <Button className="flex items-center gap-2">
            <Plus size={16} />
            Publicar anúncio
          </Button>
        </Link>
      </header>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <Stat label="Anúncios ativos" valor={ativos} tone="success" />
        <Stat label="Pausados" valor={pausados} tone="warning" />
        <Stat
          label="Interesses pendentes"
          valor={interessesPendentes.length}
          tone="primary"
        />
      </div>

      <section>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-apto-text-main">
            Interesses pendentes
          </h2>
          <Link
            to="/interesses-recebidos"
            className="text-sm font-medium text-apto-primary hover:underline"
          >
            Ver todos
          </Link>
        </div>
        {interessesPendentes.length === 0 ? (
          <div className="bg-white rounded-apto-section border border-apto-border p-8 text-center text-apto-text-muted">
            Nenhum interesse pendente no momento.
          </div>
        ) : (
          <div className="space-y-3">
            {interessesPendentes.slice(0, 5).map((m) => (
              <Link
                key={m.id}
                to={`/anuncios/${m.anuncioId}/interesses`}
                className="bg-white rounded-apto-section border border-apto-border p-4 flex items-center gap-3 hover:border-apto-primary"
              >
                <Avatar nome={m.interessadoNome} size="sm" />
                <div className="flex-1 min-w-0">
                  <p className="font-bold text-apto-text-main truncate">
                    {m.interessadoNome}
                  </p>
                  <p className="text-xs text-apto-text-muted truncate">
                    Em "{m.anuncioTitulo}"
                  </p>
                </div>
                <Badge tone="warning">
                  {statusManifestacaoLabel[m.status]}
                </Badge>
              </Link>
            ))}
          </div>
        )}
      </section>

      <section>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-apto-text-main">
            Meus anúncios
          </h2>
          <Link
            to="/meus-anuncios"
            className="text-sm font-medium text-apto-primary hover:underline"
          >
            Gerenciar
          </Link>
        </div>
        {anuncios.length === 0 ? (
          <div className="bg-white rounded-apto-section border border-apto-border p-8 text-center text-apto-text-muted">
            Você ainda não publicou nenhum anúncio.
          </div>
        ) : (
          <div className="space-y-3">
            {anuncios.slice(0, 5).map((a) => (
              <Link
                key={a.id}
                to={`/anuncios/${a.id}`}
                className="bg-white rounded-apto-section border border-apto-border p-4 flex items-center justify-between gap-3 hover:border-apto-primary"
              >
                <div className="min-w-0">
                  <p className="font-bold text-apto-text-main truncate">
                    {a.titulo}
                  </p>
                  <p className="text-xs text-apto-text-muted">
                    {formatBRL(a.valorMensal)}/mês
                  </p>
                </div>
                <Badge
                  tone={
                    a.status === "ATIVO"
                      ? "success"
                      : a.status === "PAUSADO"
                        ? "warning"
                        : "neutral"
                  }
                >
                  {statusAnuncioLabel[a.status]}
                </Badge>
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

function Stat({
  label,
  valor,
  tone,
}: {
  label: string;
  valor: number;
  tone: "success" | "warning" | "primary";
}) {
  const colors = {
    success: "text-emerald-600",
    warning: "text-amber-600",
    primary: "text-apto-primary",
  };
  return (
    <div className="bg-white rounded-apto-section border border-apto-border p-5">
      <p className="text-[11px] uppercase font-bold text-apto-text-muted tracking-wider">
        {label}
      </p>
      <p className={`text-3xl font-bold mt-1 ${colors[tone]}`}>{valor}</p>
    </div>
  );
}
