import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Filter, MapPin, PawPrint, Sofa, Users } from "lucide-react";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { Select } from "../components/ui/Select";
import { Badge } from "../components/ui/Badge";
import { anunciosApi } from "../api/anuncios";
import { ApiError } from "../api/client";
import {
  enumOptions,
  formatBRL,
  tipoAnuncioLabel,
  tipoMoradiaLabel,
} from "../lib/format";
import type {
  BuscaAnuncioResponse,
  FiltroBuscaAnuncio,
  PaginaResponse,
  TipoAnuncio,
  TipoMoradia,
} from "../api/types";

export default function Buscar() {
  const [filtro, setFiltro] = useState<FiltroBuscaAnuncio>({
    page: 0,
    size: 12,
  });
  const [pagina, setPagina] = useState<PaginaResponse<BuscaAnuncioResponse> | null>(
    null,
  );
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [filtrosAbertos, setFiltrosAbertos] = useState(false);

  useEffect(() => {
    setCarregando(true);
    setErro(null);
    anunciosApi
      .buscar(filtro)
      .then(setPagina)
      .catch((e: unknown) =>
        setErro(e instanceof ApiError ? e.message : "Erro ao buscar."),
      )
      .finally(() => setCarregando(false));
  }, [filtro]);

  function set<K extends keyof FiltroBuscaAnuncio>(
    key: K,
    value: FiltroBuscaAnuncio[K],
  ) {
    setFiltro((prev) => ({ ...prev, [key]: value, page: 0 }));
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 pb-20">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-apto-text-main">
          Buscar moradias
        </h1>
        <Button
          variant="secondary"
          onClick={() => setFiltrosAbertos((v) => !v)}
          className="flex items-center gap-2"
        >
          <Filter size={16} />
          {filtrosAbertos ? "Fechar filtros" : "Filtros"}
        </Button>
      </div>

      {filtrosAbertos && (
        <div className="bg-white rounded-apto-section border border-apto-border p-5 mb-6 grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Input
            label="Bairro"
            value={filtro.bairro ?? ""}
            onChange={(e) => set("bairro", e.target.value || undefined)}
          />
          <Input
            label="Valor mín. (R$)"
            type="number"
            min={0}
            value={filtro.valorMin ?? ""}
            onChange={(e) =>
              set(
                "valorMin",
                e.target.value === "" ? undefined : Number(e.target.value),
              )
            }
          />
          <Input
            label="Valor máx. (R$)"
            type="number"
            min={0}
            value={filtro.valorMax ?? ""}
            onChange={(e) =>
              set(
                "valorMax",
                e.target.value === "" ? undefined : Number(e.target.value),
              )
            }
          />
          <Input
            label="Vagas"
            type="number"
            min={0}
            value={filtro.quantidadeVagas ?? ""}
            onChange={(e) =>
              set(
                "quantidadeVagas",
                e.target.value === "" ? undefined : Number(e.target.value),
              )
            }
          />
          <Select
            label="Tipo de moradia"
            value={filtro.tipoMoradia ?? ""}
            onChange={(e) =>
              set(
                "tipoMoradia",
                (e.target.value || undefined) as TipoMoradia | undefined,
              )
            }
            options={[
              { value: "", label: "Qualquer" },
              ...enumOptions(tipoMoradiaLabel),
            ]}
          />
          <Select
            label="Tipo de anúncio"
            value={filtro.tipoAnuncio ?? ""}
            onChange={(e) =>
              set(
                "tipoAnuncio",
                (e.target.value || undefined) as TipoAnuncio | undefined,
              )
            }
            options={[
              { value: "", label: "Qualquer" },
              ...enumOptions(tipoAnuncioLabel),
            ]}
          />
          <Select
            label="Mobiliado"
            value={
              filtro.mobiliado === undefined ? "" : String(filtro.mobiliado)
            }
            onChange={(e) =>
              set(
                "mobiliado",
                e.target.value === "" ? undefined : e.target.value === "true",
              )
            }
            options={[
              { value: "", label: "Qualquer" },
              { value: "true", label: "Sim" },
              { value: "false", label: "Não" },
            ]}
          />
          <Select
            label="Aceita animais"
            value={
              filtro.aceitaAnimais === undefined
                ? ""
                : String(filtro.aceitaAnimais)
            }
            onChange={(e) =>
              set(
                "aceitaAnimais",
                e.target.value === "" ? undefined : e.target.value === "true",
              )
            }
            options={[
              { value: "", label: "Qualquer" },
              { value: "true", label: "Sim" },
              { value: "false", label: "Não" },
            ]}
          />
        </div>
      )}

      {erro && (
        <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700 mb-4">
          {erro}
        </div>
      )}

      {carregando ? (
        <div className="text-apto-text-muted">Buscando...</div>
      ) : !pagina || pagina.conteudo.length === 0 ? (
        <div className="bg-white rounded-apto-section border border-apto-border p-10 text-center text-apto-text-muted">
          Nenhum anúncio encontrado com os filtros selecionados.
        </div>
      ) : (
        <>
          <p className="text-sm text-apto-text-muted mb-4">
            {pagina.totalElementos} resultado(s)
          </p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {pagina.conteudo.map((a) => (
              <AnuncioCard key={a.id} anuncio={a} />
            ))}
          </div>

          {pagina.totalPaginas > 1 && (
            <div className="flex items-center justify-center gap-3 mt-8">
              <Button
                variant="secondary"
                disabled={pagina.paginaAtual === 0}
                onClick={() =>
                  setFiltro((p) => ({
                    ...p,
                    page: Math.max(0, (p.page ?? 0) - 1),
                  }))
                }
              >
                Anterior
              </Button>
              <span className="text-sm text-apto-text-muted">
                Página {pagina.paginaAtual + 1} de {pagina.totalPaginas}
              </span>
              <Button
                variant="secondary"
                disabled={pagina.paginaAtual + 1 >= pagina.totalPaginas}
                onClick={() =>
                  setFiltro((p) => ({ ...p, page: (p.page ?? 0) + 1 }))
                }
              >
                Próxima
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

function AnuncioCard({ anuncio }: { anuncio: BuscaAnuncioResponse }) {
  return (
    <Link
      to={`/anuncios/${anuncio.id}`}
      className="bg-white rounded-apto-card border border-apto-border overflow-hidden hover:shadow-md transition-shadow flex flex-col"
    >
      <div className="aspect-[16/10] bg-gradient-to-br from-apto-primary-light to-apto-primary/10 flex items-center justify-center">
        <span className="text-apto-primary/50 font-bold text-sm tracking-widest">
          {tipoMoradiaLabel[anuncio.tipoMoradia].toUpperCase()}
        </span>
      </div>
      <div className="p-5 flex-1 flex flex-col">
        <div className="flex items-start justify-between gap-2 mb-2">
          <h3 className="font-bold text-apto-text-main line-clamp-2 leading-tight">
            {anuncio.titulo}
          </h3>
          <Badge
            tone={anuncio.tipoAnuncio === "IMOVEL_COMPLETO" ? "primary" : "info"}
          >
            {tipoAnuncioLabel[anuncio.tipoAnuncio]}
          </Badge>
        </div>
        <p className="flex items-center gap-1 text-xs text-apto-text-muted mb-3">
          <MapPin size={12} />
          {anuncio.bairro}
        </p>
        <p className="text-xl font-bold text-apto-text-main mb-3">
          {formatBRL(anuncio.valorMensal)}
          <span className="text-xs text-apto-text-muted font-normal">/mês</span>
        </p>
        <div className="flex items-center gap-3 text-xs text-apto-text-muted mt-auto">
          <span className="flex items-center gap-1">
            <Users size={12} /> {anuncio.quantidadeVagas} vagas
          </span>
          {anuncio.mobiliado && (
            <span className="flex items-center gap-1">
              <Sofa size={12} /> Mobiliado
            </span>
          )}
          {anuncio.aceitaAnimais && (
            <span className="flex items-center gap-1">
              <PawPrint size={12} /> Pets
            </span>
          )}
        </div>
        <p className="text-xs text-apto-text-muted mt-3 pt-3 border-t border-apto-border">
          Publicado por{" "}
          <span className="font-medium text-apto-text-main">
            {anuncio.nomeAnunciante}
          </span>
        </p>
      </div>
    </Link>
  );
}
