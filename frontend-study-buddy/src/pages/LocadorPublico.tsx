import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Award } from "lucide-react";
import { Avatar } from "../components/ui/Avatar";
import { Badge } from "../components/ui/Badge";
import { StarRating } from "../components/ui/StarRating";
import { avaliacoesApi } from "../api/avaliacoes";
import { perfilAnuncianteApi, reputacaoApi } from "../api/reputacao";
import { locadoresApi } from "../api/usuarios";
import { ApiError } from "../api/client";
import { formatDateTime } from "../lib/format";
import type {
  AvaliacaoResponse,
  LocadorResponse,
  ReputacaoAnuncianteResponse,
  ResumoAvaliacoesAnuncianteResponse,
} from "../api/types";

export default function LocadorPublico() {
  const { id } = useParams<{ id: string }>();

  const [locador, setLocador] = useState<LocadorResponse | null>(null);
  const [resumo, setResumo] =
    useState<ResumoAvaliacoesAnuncianteResponse | null>(null);
  const [reputacao, setReputacao] =
    useState<ReputacaoAnuncianteResponse | null>(null);
  const [avaliacoes, setAvaliacoes] = useState<AvaliacaoResponse[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    setCarregando(true);

    // 1. busca dados do locador e resolve o PerfilAnunciante em paralelo
    Promise.all([locadoresApi.obter(id), perfilAnuncianteApi.porUsuario(id)])
      .then(async ([l, perfil]) => {
        setLocador(l);

        // 2. com o perfilAnuncianteId em mãos, busca avaliações e reputação
        const perfilId = perfil.id;
        const [r, rep, avs] = await Promise.all([
          avaliacoesApi.resumoAnunciante(perfilId).catch(() => null),
          reputacaoApi.doAnunciante(perfilId).catch(() => null),
          avaliacoesApi.porAnunciante(perfilId).catch(() => []),
        ]);

        setResumo(r);
        setReputacao(rep);
        setAvaliacoes(avs);
      })
      .catch((e: unknown) =>
        setErro(e instanceof ApiError ? e.message : "Erro ao carregar."),
      )
      .finally(() => setCarregando(false));
  }, [id]);

  if (carregando) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-apto-text-muted">
        Carregando...
      </div>
    );
  }

  if (erro || !locador) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12">
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro ?? "Locador não encontrado."}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 pb-20 space-y-6">
      <div className="bg-white rounded-apto-section border border-apto-border p-6 flex flex-col sm:flex-row gap-6 items-start">
        <Avatar nome={locador.nome} size="xl" />
        <div className="flex-1 min-w-0">
          <Badge tone="primary">Locador</Badge>
          <h1 className="text-2xl font-bold text-apto-text-main mt-2">
            {locador.nome}
          </h1>
          <p className="text-apto-text-muted">{locador.nomeExibicaoOuRazao}</p>
          <p className="text-sm text-apto-text-muted mt-1">{locador.email}</p>
        </div>
        {reputacao && (
          <div className="flex items-center gap-3 px-4 py-3 bg-apto-bg rounded-apto-ui border border-apto-border">
            <Award className="text-amber-500" size={24} />
            <div>
              <p className="text-[10px] uppercase font-bold text-apto-text-muted">
                Reputação
              </p>
              <p className="text-2xl font-bold text-apto-text-main leading-none">
                {reputacao.reputacaoScore.toFixed(1)}
              </p>
              <p className="text-xs text-apto-text-muted">
                {reputacao.totalAvaliacoes} avaliações
              </p>
            </div>
          </div>
        )}
      </div>

      {resumo && resumo.totalAvaliacoes > 0 && (
        <div className="bg-white rounded-apto-section border border-apto-border p-6">
          <h2 className="font-bold text-apto-text-main mb-4">
            Médias por dimensão
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
            <DimensaoMedia label="Geral" valor={resumo.notaMediaGeral} />
            <DimensaoMedia
              label="Comunicação"
              valor={resumo.notaMediaComunicacao}
            />
            <DimensaoMedia
              label="Fidelidade do anúncio"
              valor={resumo.notaMediaFidelidadeAnuncio}
            />
            <DimensaoMedia
              label="Estado da moradia"
              valor={resumo.notaMediaEstadoMoradia}
            />
            <DimensaoMedia
              label="Custo-benefício"
              valor={resumo.notaMediaCustoBeneficio}
            />
          </div>
        </div>
      )}

      <div className="bg-white rounded-apto-section border border-apto-border p-6">
        <h2 className="font-bold text-apto-text-main mb-4">
          Avaliações ({avaliacoes.length})
        </h2>
        {avaliacoes.length === 0 ? (
          <p className="text-apto-text-muted text-sm">
            Nenhuma avaliação ainda.
          </p>
        ) : (
          <div className="space-y-5 divide-y divide-apto-border">
            {avaliacoes.map((a) => (
              <article key={a.id} className="pt-5 first:pt-0">
                <div className="flex items-center justify-between gap-3 mb-2">
                  <div className="flex items-center gap-3">
                    <Avatar nome={a.avaliadorNome} size="sm" />
                    <div>
                      <p className="font-bold text-sm text-apto-text-main">
                        {a.avaliadorNome}
                      </p>
                      <p className="text-xs text-apto-text-muted">
                        {formatDateTime(a.dataCriacao)}
                      </p>
                    </div>
                  </div>
                  <StarRating value={a.notaGeral} readOnly size={14} />
                </div>
                {a.comentario && (
                  <p className="text-sm text-apto-text-main leading-relaxed">
                    "{a.comentario}"
                  </p>
                )}
                <p className="text-xs text-apto-text-muted mt-2">
                  Sobre o anúncio{" "}
                  <span className="font-medium">{a.anuncioTitulo}</span>
                </p>
              </article>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function DimensaoMedia({
  label,
  valor,
}: {
  label: string;
  valor: number | null;
}) {
  return (
    <div className="rounded-apto-ui border border-apto-border p-3 bg-apto-bg/30 text-center">
      <p className="text-[10px] uppercase font-bold text-apto-text-muted tracking-wider">
        {label}
      </p>
      <p className="text-2xl font-bold text-apto-text-main">
        {valor != null ? valor.toFixed(1) : "—"}
      </p>
    </div>
  );
}
