import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Compass, Sparkles } from "lucide-react";
import { Avatar } from "../components/ui/Avatar";
import { Badge } from "../components/ui/Badge";
import { Button } from "../components/ui/Button";
import { matchmakingApi } from "../api/matchmaking";
import { useAuth } from "../auth/useAuth";
import { ApiError } from "../api/client";
import { generoLabel } from "../lib/format";
import type { MatchColegaResponse } from "../api/types";

export default function Matchmaking() {
  const { sessao } = useAuth();
  const [candidatos, setCandidatos] = useState<MatchColegaResponse[] | null>(
    null,
  );
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [perfilIncompleto, setPerfilIncompleto] = useState(false);

  useEffect(() => {
    if (!sessao || sessao.tipo !== "UNIVERSITARIO") {
      setCarregando(false);
      return;
    }
    setCarregando(true);
    setErro(null);
    matchmakingApi
      .buscarColegas(sessao.id, 10)
      .then((r) => setCandidatos(r.candidatos))
      .catch((e: unknown) => {
        if (e instanceof ApiError) {
          if (
            (e.status === 400 || e.status === 422) &&
            (typeof e.message === "string" &&
              e.message.toLowerCase().includes("perfil"))
          ) {
            setPerfilIncompleto(true);
          } else {
            setErro(e.message);
          }
        } else {
          setErro("Erro ao buscar matches.");
        }
      })
      .finally(() => setCarregando(false));
  }, [sessao]);

  if (!sessao || sessao.tipo !== "UNIVERSITARIO") {
    return (
      <div className="max-w-2xl mx-auto px-4 py-12 text-apto-text-muted">
        Matchmaking é exclusivo de universitários.
      </div>
    );
  }

  if (perfilIncompleto) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-12">
        <div className="bg-white rounded-apto-section border border-apto-border p-8 text-center space-y-4">
          <Sparkles className="text-apto-primary mx-auto" size={32} />
          <h1 className="text-2xl font-bold text-apto-text-main">
            Complete seu perfil de convivência
          </h1>
          <p className="text-apto-text-muted">
            Sem o perfil preenchido, não conseguimos calcular quão compatível
            você é com outros universitários.
          </p>
          <Link to="/profile/convivencia">
            <Button>Preencher perfil</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 pb-20">
      <header className="flex items-center gap-3 mb-2">
        <Compass className="text-apto-primary" size={28} />
        <h1 className="text-2xl font-bold text-apto-text-main">Matchmaking</h1>
      </header>
      <p className="text-apto-text-muted mb-8 max-w-2xl">
        Estes são os universitários mais compatíveis com seu perfil de
        convivência. A compatibilidade é calculada por IA quando possível, com
        fallback determinístico baseado nos seus hábitos.
      </p>

      {carregando ? (
        <div className="text-apto-text-muted">Calculando matches...</div>
      ) : erro ? (
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      ) : !candidatos || candidatos.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Nenhum candidato encontrado no momento.
        </div>
      ) : (
        <div className="space-y-4">
          {candidatos.map((c) => (
            <CandidatoCard key={c.id} candidato={c} />
          ))}
        </div>
      )}
    </div>
  );
}

function CandidatoCard({ candidato }: { candidato: MatchColegaResponse }) {
  const cor =
    candidato.percentualCompatibilidade >= 80
      ? "text-emerald-600"
      : candidato.percentualCompatibilidade >= 60
        ? "text-amber-600"
        : "text-apto-text-muted";

  return (
    <div className="bg-white rounded-apto-section border border-apto-border p-5">
      <div className="flex items-start gap-4">
        <Avatar nome={candidato.nome} size="lg" />
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap mb-1">
            <h3 className="font-bold text-apto-text-main truncate">
              {candidato.nome}
            </h3>
            <Badge tone={candidato.origem === "LLM" ? "primary" : "info"}>
              {candidato.origem === "LLM"
                ? "IA"
                : "Cálculo direto"}
            </Badge>
          </div>
          <p className="text-sm text-apto-text-muted">
            {candidato.curso} · {generoLabel[candidato.genero]}
          </p>
        </div>
        <div className="text-right">
          <p className={`text-3xl font-bold ${cor}`}>
            {candidato.percentualCompatibilidade}%
          </p>
          <p className="text-[10px] uppercase font-bold text-apto-text-muted tracking-wider">
            Compatibilidade
          </p>
        </div>
      </div>

      {candidato.justificativa && (
        <div className="mt-4 pt-4 border-t border-apto-border">
          <p className="section-title mb-2">Por que vocês combinam</p>
          <p className="text-sm text-apto-text-main leading-relaxed">
            {candidato.justificativa}
          </p>
        </div>
      )}
    </div>
  );
}
