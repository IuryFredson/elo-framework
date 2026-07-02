import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Badge } from "../components/ui/Badge";
import { anunciosApi } from "../api/anuncios";
import { useAuth } from "../auth/useAuth";
import { formatBRL, statusAnuncioLabel } from "../lib/format";
import type { AnuncioResponse } from "../api/types";

export default function InteressesRecebidos() {
  const { sessao } = useAuth();
  const [anuncios, setAnuncios] = useState<AnuncioResponse[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    if (!sessao) return;
    anunciosApi
      .listar()
      .then((todos) =>
        setAnuncios(todos.filter((a) => a.anuncianteId === sessao.id)),
      )
      .finally(() => setCarregando(false));
  }, [sessao]);

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 pb-20">
      <h1 className="text-2xl font-bold text-apto-text-main mb-6">
        Interesses recebidos
      </h1>
      <p className="text-apto-text-muted text-sm mb-4">
        Selecione um dos seus anúncios para ver as manifestações de interesse.
      </p>

      {carregando ? (
        <div className="text-apto-text-muted">Carregando...</div>
      ) : anuncios.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Você ainda não publicou nenhum anúncio.{" "}
          <Link to="/anuncios/novo" className="text-apto-primary font-bold">
            Criar agora
          </Link>
          .
        </div>
      ) : (
        <div className="space-y-3">
          {anuncios.map((a) => (
            <Link
              key={a.id}
              to={`/anuncios/${a.id}/interesses`}
              className="block bg-white rounded-apto-section border border-apto-border p-5 hover:border-apto-primary transition-colors"
            >
              <div className="flex items-center gap-2 mb-1">
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
              </div>
              <p className="font-bold text-apto-text-main">{a.titulo}</p>
              <p className="text-sm text-apto-text-muted">
                {formatBRL(a.valorMensal)}/mês
              </p>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
