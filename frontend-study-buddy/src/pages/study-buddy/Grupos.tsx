import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Filter, Plus } from "lucide-react";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { Input } from "../../components/ui/Input";
import { gruposApi } from "../../api/studyBuddy";
import { formatDate } from "../../lib/format";
import {
  modalidadeEstudoLabel,
  periodoDisponibilidadeLabel,
  statusGrupoEstudoLabel,
} from "../../lib/studyBuddyFormat";
import type { GrupoEstudoResponse } from "../../study-buddy/types";

export default function Grupos() {
  const [grupos, setGrupos] = useState<GrupoEstudoResponse[]>([]);
  const [busca, setBusca] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    gruposApi
      .listar()
      .then(setGrupos)
      .finally(() => setCarregando(false));
  }, []);

  const gruposFiltrados = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    if (!termo) return grupos;
    return grupos.filter((grupo) =>
      [grupo.titulo, grupo.descricao, grupo.disciplina, grupo.publicadorNome]
        .join(" ")
        .toLowerCase()
        .includes(termo),
    );
  }, [busca, grupos]);

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 pb-20 space-y-6">
      <header className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <h1 className="text-3xl font-bold text-apto-text-main">Grupos de estudo</h1>
          <p className="text-apto-text-muted">
            Explore ofertas publicadas na instância Study Buddy do framework.
          </p>
        </div>
        <Link to="/grupos/novo">
          <Button className="flex items-center gap-2">
            <Plus size={16} />
            Publicar grupo
          </Button>
        </Link>
      </header>

      <div className="bg-white rounded-apto-section border border-apto-border p-4 flex items-center gap-3">
        <Filter size={16} className="text-apto-text-muted" />
        <Input
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
          placeholder="Buscar por título, disciplina ou publicador"
          className="border-0 shadow-none px-0"
        />
      </div>

      {carregando ? (
        <div className="text-apto-text-muted">Carregando grupos...</div>
      ) : gruposFiltrados.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Nenhum grupo encontrado.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {gruposFiltrados.map((grupo) => (
            <Link
              key={grupo.id}
              to={`/grupos/${grupo.id}`}
              className="bg-white rounded-apto-section border border-apto-border p-5 hover:shadow-md transition-shadow space-y-4"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h2 className="font-bold text-apto-text-main">{grupo.titulo}</h2>
                  <p className="text-sm text-apto-text-muted">
                    {grupo.disciplina} · {grupo.publicadorNome}
                  </p>
                </div>
                <Badge tone={grupo.status === "ATIVO" ? "success" : "warning"}>
                  {statusGrupoEstudoLabel[grupo.status]}
                </Badge>
              </div>

              <p className="text-sm text-apto-text-muted line-clamp-3">{grupo.descricao}</p>

              <div className="flex flex-wrap gap-2">
                <Badge tone="info">{modalidadeEstudoLabel[grupo.modalidade]}</Badge>
                <Badge tone="neutral">{periodoDisponibilidadeLabel[grupo.periodo]}</Badge>
                <Badge tone="primary">{grupo.quantidadeVagas} vagas</Badge>
              </div>

              <div className="text-xs text-apto-text-muted">
                Publicado em {formatDate(grupo.dataPublicacao)}
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
