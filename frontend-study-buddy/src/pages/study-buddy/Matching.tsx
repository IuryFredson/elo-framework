import { useEffect, useState } from "react";
import { Compass } from "lucide-react";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { studyBuddyMatchingApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { origemCompatibilidadeLabel } from "../../lib/format";
import { formatarDisciplinas, modalidadeEstudoLabel, nivelConhecimentoLabel, objetivoEstudoLabel } from "../../lib/studyBuddyFormat";
import type { MatchEstudanteResponse } from "../../study-buddy/types";

export default function Matching() {
  const sessao = useSessaoObrigatoria();
  const [matches, setMatches] = useState<MatchEstudanteResponse[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [topN, setTopN] = useState(6);

  function carregar() {
    setCarregando(true);
    setErro(null);
    studyBuddyMatchingApi
      .buscar(sessao.id, topN)
      .then((resultado) => setMatches(resultado.candidatos))
      .catch((error: unknown) => {
        setErro(error instanceof ApiError ? error.message : "Erro ao calcular matching.");
      })
      .finally(() => setCarregando(false));
  }

  useEffect(() => {
    carregar();
  }, [sessao.id, topN]);

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 pb-20 space-y-6">
      <header className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-3xl font-bold text-apto-text-main">Matching</h1>
          <p className="text-apto-text-muted mt-2">
            Compatibilidade entre estudantes com base no perfil acadêmico da instância.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="secondary" size="sm" onClick={() => setTopN(3)}>
            Top 3
          </Button>
          <Button variant="secondary" size="sm" onClick={() => setTopN(6)}>
            Top 6
          </Button>
          <Button variant="secondary" size="sm" onClick={() => setTopN(10)}>
            Top 10
          </Button>
        </div>
      </header>

      {erro && (
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro}
        </div>
      )}

      {carregando ? (
        <div className="text-apto-text-muted">Calculando compatibilidade...</div>
      ) : matches.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Nenhum match encontrado.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {matches.map((match) => (
            <div key={match.id} className="bg-white rounded-apto-section border border-apto-border p-5 space-y-4">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h2 className="font-bold text-apto-text-main">{match.nome}</h2>
                  <p className="text-sm text-apto-text-muted">
                    {match.curso} · {match.instituicao}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-3xl font-bold text-apto-text-main">{match.percentualCompatibilidade}%</p>
                  <p className="text-xs text-apto-text-muted">compatibilidade</p>
                </div>
              </div>

              <div className="flex flex-wrap gap-2">
                <Badge tone={match.origem === "LLM" ? "primary" : "info"}>
                  {origemCompatibilidadeLabel[match.origem]}
                </Badge>
                <Badge tone="neutral">{objetivoEstudoLabel[match.objetivoEstudo]}</Badge>
                <Badge tone="neutral">{nivelConhecimentoLabel[match.nivelConhecimento]}</Badge>
                <Badge tone="neutral">{modalidadeEstudoLabel[match.modalidadePreferida]}</Badge>
              </div>

              <p className="text-sm text-apto-text-muted">{match.justificativa}</p>

              <div className="space-y-2">
                <p className="section-title">Critérios atendidos</p>
                <div className="flex flex-wrap gap-2">
                  {match.criteriosAtendidos.map((criterio) => (
                    <Badge key={criterio} tone="success">
                      {criterio}
                    </Badge>
                  ))}
                </div>
              </div>

              <div className="space-y-2">
                <p className="section-title">Disciplinas</p>
                <p className="text-sm text-apto-text-main flex items-center gap-2">
                  <Compass size={14} className="text-apto-primary" />
                  {formatarDisciplinas(match.disciplinasInteresse)}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
