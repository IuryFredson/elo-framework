import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { BookOpen, Compass, Flag, Sparkles } from "lucide-react";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { gruposApi, manifestacoesGrupoApi, perfilAcademicoApi, studyBuddyMatchingApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { formatDate } from "../../lib/format";
import { formatarDisciplinas, modalidadeEstudoLabel, statusGrupoEstudoLabel } from "../../lib/studyBuddyFormat";
import type {
  GrupoEstudoResponse,
  ManifestacaoInteresseGrupoResponse,
  MatchEstudanteResponse,
} from "../../study-buddy/types";

export default function Home() {
  const sessao = useSessaoObrigatoria();
  const [perfilOk, setPerfilOk] = useState<boolean | null>(null);
  const [grupos, setGrupos] = useState<GrupoEstudoResponse[]>([]);
  const [matches, setMatches] = useState<MatchEstudanteResponse[]>([]);
  const [interesses, setInteresses] = useState<ManifestacaoInteresseGrupoResponse[]>([]);

  useEffect(() => {
    gruposApi.listar().then((lista) => setGrupos(lista.slice(0, 6))).catch(() => {});
    manifestacoesGrupoApi
      .porEstudante(sessao.id)
      .then((lista) => setInteresses(lista.slice(0, 4)))
      .catch(() => {});

    perfilAcademicoApi
      .obter(sessao.id)
      .then(() => {
        setPerfilOk(true);
        return studyBuddyMatchingApi.buscar(sessao.id, 3);
      })
      .then((resultado) => setMatches(resultado.candidatos))
      .catch((error: unknown) => {
        if (error instanceof ApiError && error.status === 404) {
          setPerfilOk(false);
          return;
        }
        setPerfilOk(true);
      });
  }, [sessao.id]);

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 pb-20 space-y-8">
      <header className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h1 className="text-3xl font-bold text-apto-text-main">
            Olá, {sessao.nome.split(" ")[0]}
          </h1>
          <p className="text-apto-text-muted">
            Organize grupos, manifeste interesse e encontre colegas compatíveis.
          </p>
        </div>
        <div className="flex gap-2">
          <Link to="/grupos">
            <Button variant="secondary" className="flex items-center gap-2">
              <BookOpen size={16} />
              Explorar grupos
            </Button>
          </Link>
          <Link to="/matchmaking">
            <Button className="flex items-center gap-2">
              <Compass size={16} />
              Ver matches
            </Button>
          </Link>
        </div>
      </header>

      {perfilOk === false && (
        <div className="bg-amber-50 border border-amber-200 rounded-apto-section p-5 flex items-start gap-3">
          <Sparkles className="text-amber-600 flex-shrink-0" size={20} />
          <div className="flex-1">
            <p className="font-bold text-apto-text-main">Complete seu perfil acadêmico</p>
            <p className="text-sm text-apto-text-muted">
              O matching precisa dele para calcular compatibilidade entre estudantes.
            </p>
          </div>
          <Link to="/perfil/academico">
            <Button size="sm">Preencher</Button>
          </Link>
        </div>
      )}

      {matches.length > 0 && (
        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-apto-text-main">Top matches</h2>
            <Link to="/matchmaking" className="text-sm font-medium text-apto-primary hover:underline">
              Ver todos
            </Link>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {matches.map((match) => (
              <div
                key={match.id}
                className="bg-white rounded-apto-section border border-apto-border p-4 space-y-3"
              >
                <div className="flex items-center justify-between gap-3">
                  <div>
                    <p className="font-bold text-apto-text-main">{match.nome}</p>
                    <p className="text-xs text-apto-text-muted">
                      {match.curso} · {match.instituicao}
                    </p>
                  </div>
                  <p className="text-2xl font-bold text-apto-text-main">
                    {match.percentualCompatibilidade}%
                  </p>
                </div>
                <div className="flex items-center justify-between">
                  <Badge tone={match.origem === "LLM" ? "primary" : "info"}>
                    {match.origem === "LLM" ? "IA" : "Determinístico"}
                  </Badge>
                  <span className="text-xs text-apto-text-muted">
                    {match.criteriosAtendidos.length} critérios
                  </span>
                </div>
                <p className="text-sm text-apto-text-muted line-clamp-3">{match.justificativa}</p>
              </div>
            ))}
          </div>
        </section>
      )}

      <section className="grid grid-cols-1 lg:grid-cols-[1.4fr_1fr] gap-6">
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-apto-text-main">Grupos recentes</h2>
            <Link to="/grupos" className="text-sm font-medium text-apto-primary hover:underline">
              Ver todos
            </Link>
          </div>
          <div className="space-y-4">
            {grupos.length === 0 ? (
              <div className="bg-white rounded-apto-section border border-apto-border p-8 text-center text-apto-text-muted">
                Nenhum grupo disponível.
              </div>
            ) : (
              grupos.map((grupo) => (
                <Link
                  key={grupo.id}
                  to={`/grupos/${grupo.id}`}
                  className="block bg-white rounded-apto-section border border-apto-border p-5 hover:shadow-md transition-shadow"
                >
                  <div className="flex items-start justify-between gap-3 mb-3">
                    <div>
                      <p className="font-bold text-apto-text-main">{grupo.titulo}</p>
                      <p className="text-sm text-apto-text-muted">
                        {grupo.disciplina} · {grupo.publicadorNome}
                      </p>
                    </div>
                    <Badge tone={grupo.status === "ATIVO" ? "success" : "warning"}>
                      {statusGrupoEstudoLabel[grupo.status]}
                    </Badge>
                  </div>
                  <p className="text-sm text-apto-text-muted line-clamp-2 mb-3">{grupo.descricao}</p>
                  <div className="flex items-center justify-between text-xs text-apto-text-muted">
                    <span>{modalidadeEstudoLabel[grupo.modalidade]}</span>
                    <span>{formatDate(grupo.dataPublicacao)}</span>
                  </div>
                </Link>
              ))
            )}
          </div>
        </div>

        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-apto-text-main">Atividade recente</h2>
            <Link to="/interesses" className="text-sm font-medium text-apto-primary hover:underline">
              Ver interesses
            </Link>
          </div>
          <div className="bg-white rounded-apto-section border border-apto-border p-5 space-y-4">
            {interesses.length === 0 ? (
              <p className="text-sm text-apto-text-muted">
                Você ainda não manifestou interesse em grupos.
              </p>
            ) : (
              interesses.map((item) => (
                <div key={item.id} className="border-b last:border-b-0 border-apto-border pb-3 last:pb-0">
                  <div className="flex items-center justify-between gap-3 mb-1">
                    <p className="font-medium text-apto-text-main line-clamp-1">{item.grupoTitulo}</p>
                    <Badge tone={item.status === "ACEITA" ? "success" : item.status === "RECUSADA" ? "danger" : "warning"}>
                      {item.status}
                    </Badge>
                  </div>
                  <p className="text-xs text-apto-text-muted">{item.publicadorNome}</p>
                </div>
              ))
            )}
          </div>

          <div className="bg-white rounded-apto-section border border-apto-border p-5 space-y-3">
            <div className="flex items-center gap-2 text-apto-primary">
              <Flag size={16} />
              <p className="font-semibold text-apto-text-main">Denúncia e moderação</p>
            </div>
            <p className="text-sm text-apto-text-muted">
              O frontend já reserva espaço para sinalização de grupos e fila de análise,
              mas a lógica operacional será ligada em etapa posterior.
            </p>
            <p className="text-sm text-apto-text-main">
              Disciplinas em destaque: {matches[0] ? formatarDisciplinas(matches[0].disciplinasInteresse.slice(0, 3)) : "aguardando perfil"}.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
